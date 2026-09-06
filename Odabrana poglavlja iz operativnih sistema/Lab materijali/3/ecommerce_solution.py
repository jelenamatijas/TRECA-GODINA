# ecommerce_partition_practice_solutions.py

import random
import time
from datetime import datetime, timedelta
from typing import Iterable

import numpy as np
from pyspark import SparkConf, SparkContext, RDD


# ---------------------------------------------------------------------------
# Helper: measure execution time of tasks
# ---------------------------------------------------------------------------
def measure_time(func):
    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = func(*args, **kwargs)
        end_time = time.time()
        print(f"{func.__name__} execution time: {end_time - start_time:.4f} seconds")
        return result

    return wrapper


# ---------------------------------------------------------------------------
# Synthetic transaction data generator (same as in practice file)
# ---------------------------------------------------------------------------
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

            for day in range(num_days):
                current_date = start_date + timedelta(days=day)

                max_possible = 10
                p = min(
                    1.0,
                    avg_transactions_per_customer_per_day / max_possible,
                )
                num_tx_today = sum(
                    1 for _ in range(max_possible) if random.random() < p
                )

                for _ in range(num_tx_today):
                    hour = random.randint(0, 23)
                    minute = random.randint(0, 59)
                    ts = current_date.replace(hour=hour, minute=minute)

                    category = random.choice(self.categories)

                    base = np.random.lognormal(
                        mean=np.log(seg["mean"]), sigma=seg["sigma"]
                    )
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


# ---------------------------------------------------------------------------
# Helper used in task 2
# ---------------------------------------------------------------------------
def _build_daily_stats_from_tuple(agg: tuple[float, int]) -> dict:
    total, count = agg
    if count == 0:
        return {"total": 0.0, "count": 0, "avg": 0.0}
    avg = total / count
    return {"total": total, "count": count, "avg": avg}


# ---------------------------------------------------------------------------
# Task 1: optimized with reduceByKey
# ---------------------------------------------------------------------------
@measure_time
def total_spent_per_customer_groupby(
    rdd: RDD[tuple[str, tuple[str, str, float]]],
) -> list[tuple[str, float]]:
    """
    Task 1 (optimized with reduceByKey):

    Compute total amount spent per customer.

    Returns:
      List of (customer_id, total_amount) sorted by total_amount descending,
      limited to first 20 customers.
    """
    # (customer_id, (ts, category, amount)) -> (customer_id, amount)
    customer_amounts: RDD[tuple[str, float]] = rdd.map(
        lambda x: (x[0], x[1][2])
    )

    # Optimized: reduceByKey to aggregate sums per customer_id
    summed: RDD[tuple[str, float]] = customer_amounts.reduceByKey(
        lambda a, b: a + b
    )

    top20 = (
        summed.sortBy(lambda kv: kv[1], ascending=False)
        .take(20)
    )

    return top20


# ---------------------------------------------------------------------------
# Task 2: optimized with reduceByKey
# ---------------------------------------------------------------------------
@measure_time
def daily_category_stats_groupby(
    rdd: RDD[tuple[str, tuple[str, str, float]]],
) -> list[tuple[tuple[str, str], dict]]:
    """
    Task 2 (optimized with reduceByKey):

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
    """
    # (customer_id, (ts, category, amount)) -> ((date, category), amount)
    keyed_by_day_category: RDD[tuple[tuple[str, str], float]] = rdd.map(
        lambda x: ((x[1][0][:10], x[1][1]), x[1][2])
    )

    # Map to ((date, category), (total, count)) and reduceByKey
    totals_and_counts: RDD[tuple[tuple[str, str], tuple[float, int]]] = (
        keyed_by_day_category
        .map(lambda kv: (kv[0], (kv[1], 1)))
        .reduceByKey(lambda a, b: (a[0] + b[0], a[1] + b[1]))
    )

    daily_stats: RDD[tuple[tuple[str, str], dict]] = totals_and_counts.mapValues(
        _build_daily_stats_from_tuple
    )

    return daily_stats.collect()


# ---------------------------------------------------------------------------
# Task 3: implemented with reduceByKey
# ---------------------------------------------------------------------------
@measure_time
def top_customers_per_category_groupby(
    rdd: RDD[tuple[str, tuple[str, str, float]]],
    top_n: int = 5,
) -> list[tuple[str, list[tuple[str, float]]]]:
    """
    Task 3: For each category, find the TOP-N customers by total spending.

    Input RDD schema:
      RDD[(customer_id, (timestamp_str, category, amount))]

    Output:
      List of (category, top_customers) where:
        - category: str
        - top_customers: list[(customer_id, total_amount_in_this_category)]
          sorted by total_amount_in_this_category descending,
          limited to at most `top_n` customers per category.
    """

    # Step 1: per (category, customer) total spent
    # (customer_id, (ts, category, amount)) -> ((category, customer_id), amount)
    category_customer_amounts: RDD[tuple[tuple[str, str], float]] = rdd.map(
        lambda x: ((x[1][1], x[0]), x[1][2])
    )

    # ((category, customer), amount) -> aggregated total per (category, customer)
    category_customer_totals: RDD[tuple[tuple[str, str], float]] = (
        category_customer_amounts.reduceByKey(lambda a, b: a + b)
    )

    # Step 2: re-key by category, value is list[(customer, total)] so we can
    # use reduceByKey to maintain top-N in each value.
    per_category_lists: RDD[tuple[str, list[tuple[str, float]]]] = (
        category_customer_totals.map(
            lambda kv: (kv[0][0], [(kv[0][1], kv[1])])
        )
    )

    def merge_customer_lists(
        lst1: list[tuple[str, float]],
        lst2: list[tuple[str, float]],
    ) -> list[tuple[str, float]]:
        combined = lst1 + lst2
        combined.sort(key=lambda x: x[1], reverse=True)
        return combined[:top_n]

    top_per_category_rdd: RDD[tuple[str, list[tuple[str, float]]]] = (
        per_category_lists.reduceByKey(merge_customer_lists)
    )

    return top_per_category_rdd.collect()


# ---------------------------------------------------------------------------
# Main driver for testing the solutions
# ---------------------------------------------------------------------------
if __name__ == "__main__":
    conf = SparkConf().setAppName("EcommerceGroupBySolutions").setMaster("local[*]")
    sc = SparkContext(conf=conf)

    generator = TransactionDataGenerator()

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

    # Base RDD: (customer_id, (timestamp, category, amount))
    base_rdd: RDD[tuple[str, tuple[str, str, float]]] = sc.parallelize(transactions).map(
        lambda t: (t[0], (t[1], t[2], t[3]))
    )

    print("Number of records:", base_rdd.count())

    print("\n=== Task 1: Total spent per customer (reduceByKey-optimized) ===")
    top_customers = total_spent_per_customer_groupby(base_rdd)
    print("Sample result (first 5):", top_customers[:5])

    print("\n=== Task 2: Daily per-category stats (reduceByKey-optimized) ===")
    daily_stats = daily_category_stats_groupby(base_rdd)
    print("Sample result (first 5):", daily_stats[:5])

    print("\n=== Task 3: Top-N customers per category (reduceByKey implementation) ===")
    top_per_category = top_customers_per_category_groupby(base_rdd, top_n=5)
    print("Sample result (first 3):", top_per_category[:3])

    sc.stop()
