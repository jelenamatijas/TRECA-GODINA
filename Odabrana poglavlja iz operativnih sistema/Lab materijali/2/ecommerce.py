import random
import time
from datetime import datetime, timedelta
from typing import Iterable

import numpy as np
from pyspark import SparkConf, SparkContext, RDD

def measure_time(func):
    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = func(*args, **kwargs)
        end_time = time.time()
        print(f"{func.__name__} execution time: {end_time - start_time:.4f} seconds")
        return result

    return wrapper



class TransactionDataGenerator:
    """
    Generates synthetic e-commerce transaction data.

    Each transaction has:
      - customer_id: e.g. "C000123"
      - timestamp: ISO string "YYYY-MM-DD HH:MM"
      - category: e.g. "Electronics"
      - amount: float (order amount in EUR)
    """

    def __init__(self):
        self.categories = [
            "Electronics",
            "Clothing",
            "Home",
            "Sports",
            "Books",
            "Groceries",
        ]

        # Different customer "segments" with different average spend
        self.segment_configs = [
            {"name": "bargain", "mean": 15.0, "sigma": 0.4},
            {"name": "regular", "mean": 40.0, "sigma": 0.5},
            {"name": "premium", "mean": 120.0, "sigma": 0.6},
        ]

    def _random_customer_id(self, idx: int) -> str:
        return f"C{idx:06d}"

    def _sampling_segment(self):
        # Slightly more regular customers than other segments
        segments = [0, 1, 2]
        probs = [0.3, 0.5, 0.2]
        return random.choices(segments, probs)[0]

    def generate_dataset(
        self,
        num_customers: int = 50_000,
        num_days: int = 365,
        avg_transactions_per_customer_per_day: float = 0.2,
    ) -> list[tuple[str, str, str, float]]:
        """
        Generate synthetic transaction dataset.

        Returns list of:
          (customer_id, timestamp_str, category, amount)
        """
        start_date = datetime(2023, 1, 1)
        data: list[tuple[str, str, str, float]] = []

        for customer_idx in range(num_customers):
            customer_id = self._random_customer_id(customer_idx)
            segment_idx = self._sampling_segment()
            seg = self.segment_configs[segment_idx]

            # Each customer has a Poisson-like number of transactions per day
            for day in range(num_days):
                current_date = start_date + timedelta(days=day)

                # Draw number of transactions for this day
                # (can be 0 most of the time)
                # Using a simple approximate Poisson via binomial
                max_possible = 10
                p = min(
                    1.0,
                    avg_transactions_per_customer_per_day / max_possible,
                )
                num_tx_today = sum(
                    1 for _ in range(max_possible) if random.random() < p
                )

                for _ in range(num_tx_today):
                    # Random time of day
                    hour = random.randint(0, 23)
                    minute = random.randint(0, 59)
                    ts = current_date.replace(hour=hour, minute=minute)

                    category = random.choice(self.categories)

                    # Amount: lognormal around segment mean
                    base = np.random.lognormal(
                        mean=np.log(seg["mean"]), sigma=seg["sigma"]
                    )
                    # Add some random noise and floor at 1.0
                    amount = max(1.0, float(base + random.uniform(-5, 5)))

                    data.append(
                        (
                            customer_id,
                            ts.strftime("%Y-%m-%d %H:%M"),
                            category,
                            round(amount, 2),
                        )
                    )

        random.shuffle(data)
        return data


@measure_time
def total_spent_per_customer_groupby(
    rdd: RDD[tuple[str, tuple[str, str, float]]],
) -> list[tuple[str, float]]:
    """
    Task 1 (with groupByKey):

    Compute total amount spent per customer.

    Returns:
      List of (customer_id, total_amount) sorted by total_amount descending,
      limited to first 20 customers.

    NOTE for students:
      - This implementation uses groupByKey + mapValues with a Python aggregation.
      - Rewrite this to use reduceByKey to reduce shuffling and memory usage.
    """
    # Extract (customer_id, amount)
    customer_amounts: RDD[tuple[str, float]] = rdd.map(
        lambda x: (x[0], x[1][2])  # (customer_id, amount)
    )

    # Inefficient variant: groupByKey → Python-side sum
    grouped: RDD[tuple[str, Iterable[float]]] = customer_amounts.groupByKey()

    summed: RDD[tuple[str, float]] = grouped.mapValues(
        lambda amounts: sum(list(amounts))
    )

    # Sort descending by total amount and take top 20
    top20 = (
        summed.sortBy(lambda kv: kv[1], ascending=False)
        .take(20)
    )

    return top20


@measure_time
def daily_category_stats_groupby(
    rdd: RDD[tuple[str, tuple[str, str, float]]],
) -> list[tuple[tuple[str, str], dict]]:
    """
    Task 2 (with groupByKey):

    Compute daily statistics per category:
      - total revenue
      - number of orders
      - average order value

    Keyed by (date_str, category).

    Returns:
      List of ((date_str, category), stats_dict) where stats_dict is:
        {
          "total": float,
          "count": int,
          "avg": float,
        }

      The list is not sorted; you may sort in your experiments if needed.

    NOTE for students:
      - This implementation uses groupByKey + mapValues with a Python aggregation.
      - Rewrite this using reduceByKey to perform aggregation more efficiently.
    """
    # Extract date part from timestamp
    # (customer_id, (ts, category, amount)) -> ((date, category), amount)
    keyed_by_day_category: RDD[tuple[tuple[str, str], float]] = rdd.map(
        lambda x: ((x[1][0][:10], x[1][1]), x[1][2])
    )

    # Inefficient variant: groupByKey → Python aggregation
    grouped: RDD[tuple[tuple[str, str], Iterable[float]]] = keyed_by_day_category.groupByKey()

    daily_stats: RDD[tuple[tuple[str, str], dict]] = grouped.mapValues(
        lambda amounts_iter: _build_daily_stats(amounts_iter)
    )

    return daily_stats.collect()


def _build_daily_stats(amounts_iter: Iterable[float]) -> dict:
    amounts = list(amounts_iter)
    if not amounts:
        return {"total": 0.0, "count": 0, "avg": 0.0}
    total = float(sum(amounts))
    count = len(amounts)
    avg = total / count
    return {"total": total, "count": count, "avg": avg}


@measure_time
def top_customers_per_category_groupby(
    rdd: RDD[tuple[str, tuple[str, str, float]]],
    top_n: int = 5,
) -> list[tuple[str, list[tuple[str, float]]]]:
    """
    Task 3 (EXERCISE – no solution provided):

    For each category, find the TOP-N customers by total spending in that category.

    Input RDD schema:
      RDD[(customer_id, (timestamp_str, category, amount))]

    Desired output:
      List of (category, top_customers) where:
        - category: str
        - top_customers: list[(customer_id, total_amount_in_this_category)]
          sorted by total_amount_in_this_category descending,
          limited to at most `top_n` customers per category.

    Constraints / hints:
      - You should first implement a variant using groupByKey.
      - For example, you can:
          1. Map to a composite key ((category, customer_id), amount)
             and then groupByKey on that composite key.
          2. Sum per ((category, customer_id)) pair.
          3. Re-key by category and group customers per category.
          4. Sort and take top-N per category.
      - After that, optimize your implementation by replacing groupByKey
        with reduceByKey where appropriate.

    IMPORTANT:
      - This function is intentionally left unimplemented. Students are
        expected to implement it as part of the exercise.
    """
    raise NotImplementedError(
        "Students should implement this function using groupByKey first, "
        "then optimize it with reduceByKey."
    )


if __name__ == "__main__":
    conf = SparkConf().setAppName("EcommerceGroupByPractice").setMaster("local[*]")
    sc = SparkContext(conf=conf)

    generator = TransactionDataGenerator()

    # Adjust these parameters to scale the dataset up/down
    num_customers = 20_000
    num_days = 180
    avg_tx_per_customer_per_day = 0.3

    print("Generating synthetic transaction data...")
    transactions = generator.generate_dataset(
        num_customers=num_customers,
        num_days=num_days,
        avg_transactions_per_customer_per_day=avg_tx_per_customer_per_day,
    )
    print(f"Generated {len(transactions)} transactions.")

    base_rdd: RDD[tuple[str, tuple[str, str, float]]] = sc.parallelize(transactions).map(
        lambda t: (t[0], (t[1], t[2], t[3]))
    )

    print("Number of records:", base_rdd.count())

    print("\n=== Task 1: Total spent per customer (groupByKey variant) ===")
    top_customers = total_spent_per_customer_groupby(base_rdd)
    print("Sample result (first 5):", top_customers[:5])

    print("\n=== Task 2: Daily per-category stats (groupByKey variant) ===")
    daily_stats = daily_category_stats_groupby(base_rdd)
    print("Sample result (first 5):", daily_stats[:5])

    print(
        "\n=== Task 3: Top-N customers per category (EXERCISE – will raise NotImplementedError) ==="
    )
    try:
        top_per_category = top_customers_per_category_groupby(base_rdd, top_n=5)
        print("Sample result (first 3):", top_per_category[:3])
    except NotImplementedError as e:
        print("Task 3 not implemented:", e)

    sc.stop()
