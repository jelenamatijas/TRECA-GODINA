# data_generator.py
import random
from datetime import datetime, timedelta


def generate_sample_data(num_records):
    # Product categories and their price ranges
    categories = {
        "Electronics": (100, 2000),
        "Clothing": (20, 200),
        "Books": (10, 50),
        "Home & Kitchen": (30, 500),
        "Sports": (25, 300),
    }

    locations = [
        "New York",
        "Los Angeles",
        "Chicago",
        "Houston",
        "Phoenix",
        "Philadelphia",
        "San Antonio",
        "San Diego",
        "Dallas",
        "San Jose",
    ]

    records = []
    start_date = datetime(2022, 1, 1)

    for i in range(num_records):
        category = random.choice(list(categories.keys()))
        price_range = categories[category]

        record = {
            "order_id": f"ORD-{i+1:06d}",
            "customer_id": f"CUST-{random.randint(1, 1000):04d}",
            "product_category": category,
            "price": round(random.uniform(price_range[0], price_range[1]), 2),
            "quantity": random.randint(1, 5),
            "location": random.choice(locations),
            "purchase_date": (
                start_date + timedelta(days=random.randint(0, 365))
            ).strftime("%Y-%m-%d"),
        }
        records.append(record)

    with open("purchase_data.csv", "w") as f:
        f.write(
            "order_id,customer_id,product_category,price,quantity,location,purchase_date\n"
        )
        for record in records:
            f.write(
                f"{record['order_id']},{record['customer_id']},{record['product_category']},"
                f"{record['price']},{record['quantity']},{record['location']},{record['purchase_date']}\n"
            )


if __name__ == "__main__":
    generate_sample_data(1_000_000)
