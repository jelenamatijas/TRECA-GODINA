from pyspark.sql import SparkSession
from pyspark.sql.functions import sum, avg, count
from pyspark.sql.types import (
    StructType,
    StructField,
    StringType,
    FloatType,
    IntegerType,
    DateType,
)
import time
import csv


class Purchase:

    def __init__(self, x1, x2) -> None:
        self.x1 = x1
        self.x2 = x2


def process_with_rdd(spark):
    # Read CSV file as RDD
    header = spark.sparkContext.textFile("purchase_data.csv").first()
    rdd = (
        spark.sparkContext.textFile("purchase_data.csv")
        .filter(lambda x: x != header)
        .map(lambda line: next(csv.reader([line])))
    )

    # Start timing
    start_time = time.time()

    # Converting relevant fields to appropriate types
    parsed_rdd = rdd.map(
        lambda x: (
            x[0],  # order_id (string)
            x[1],  # customer_id (string)
            x[2],  # product_category (string)
            float(x[3]),  # price (float)
            int(x[4]),  # quantity (int)
            x[5],  # location (string)
            x[6],  # purchase_date (string)
        )
    )

    # 1. Calculate total sales by category
    category_sales = parsed_rdd.map(lambda x: (x[2], x[3] * x[4])).reduceByKey(
        lambda x, y: x + y
    )

    # 2. Find average order value by location
    location_avg = (
        parsed_rdd.map(lambda x: (x[5], (x[3] * x[4], 1)))
        .reduceByKey(lambda x, y: (x[0] + y[0], x[1] + y[1]))
        .mapValues(lambda x: x[0] / x[1])
    )

    # 3. Count orders by customer
    customer_orders = parsed_rdd.map(lambda x: (x[1], 1)).reduceByKey(
        lambda x, y: x + y
    )

    # 4. Get top 5 products by revenue
    category_sales.sortBy(lambda x: x[1], ascending=False).take(5)

    # 5. Calculate daily sales
    daily_sales = parsed_rdd.map(lambda x: (x[6], x[3] * x[4])).reduceByKey(
        lambda x, y: x + y
    )

    # Force evaluation
    category_sales.collect()
    location_avg.collect()
    customer_orders.collect()
    daily_sales.collect()

    end_time = time.time()
    return end_time - start_time


def process_with_dataframe(spark):

    schema = StructType(
        [
            StructField("order_id", StringType(), False),  # False means not nullable
            StructField("customer_id", StringType(), False),
            StructField("product_category", StringType(), False),
            StructField("price", FloatType(), False),
            StructField("quantity", IntegerType(), False),
            StructField("location", StringType(), False),
            StructField("purchase_date", DateType(), False),
        ]
    )

    df = spark.read.csv(
        "purchase_data.csv", header=True, schema=schema, dateFormat="yyyy-MM-dd"
    )

    # df = spark.read.csv("purchase_data.csv", header=True, inferSchema=True)

    start_time = time.time()

    # 1. Calculate total sales by category
    category_sales = df.groupBy("product_category").agg(
        sum(df.price * df.quantity).alias("total_sales")
    )

    # 2. Find average order value by location
    location_avg = df.groupBy("location").agg(
        avg(df.price * df.quantity).alias("avg_order_value")
    )

    # 3. Count orders by customer
    customer_orders = df.groupBy("customer_id").agg(
        count("order_id").alias("order_count")
    )

    # 4. Get top 5 products by revenue
    top_products = (
        df.withColumn("revenue", df.price * df.quantity)
        .groupBy("product_category")
        .agg(sum("revenue").alias("total_revenue"))
        .orderBy("total_revenue", ascending=False)
        .limit(5)
    )

    # 5. Calculate daily sales
    daily_sales = df.groupBy("purchase_date").agg(
        sum(df.price * df.quantity).alias("daily_sales")
    )

    # Force evaluation
    category_sales.collect()
    location_avg.collect()
    customer_orders.collect()
    top_products.collect()
    daily_sales.collect()

    end_time = time.time()
    return end_time - start_time


def print_sample_results(spark):
    """Print sample results from both approaches for verification"""
    print("\nSample Results Comparison:")
    print("-" * 50)

    # RDD Results
    header = spark.sparkContext.textFile("purchase_data.csv").first()
    rdd = (
        spark.sparkContext.textFile("purchase_data.csv")
        .filter(lambda line: line != header)
        .map(lambda line: next(csv.reader([line])))
    )

    parsed_rdd = rdd.map(
        lambda x: (x[0], x[1], x[2], float(x[3]), int(x[4]), x[5], x[6])
    )

    category_sales_rdd = (
        parsed_rdd.map(lambda x: (x[2], x[3] * x[4]))
        .reduceByKey(lambda x, y: x + y)
        .sortBy(lambda x: x[1], ascending=False)
        .take(3)
    )

    # DataFrame Results
    df = spark.read.csv("purchase_data.csv", header=True, inferSchema=True)
    category_sales_df = (
        df.groupBy("product_category")
        .agg(sum(df.price * df.quantity).alias("total_sales"))
        .orderBy("total_sales", ascending=False)
        .limit(3)
    )

    print("Top 3 Categories by Sales:")
    print("RDD Result:")
    for category, sales in category_sales_rdd:
        print(f"{category}: ${sales:,.2f}")

    print("\nDataFrame Result:")
    for row in category_sales_df.collect():
        print(f"{row['product_category']}: ${row['total_sales']:,.2f}")


def main():
    # Initialize Spark session
    spark = SparkSession.builder.appName(  # type: ignore
        "Structured vs Unstructured Processing"
    ).getOrCreate()

    rdd_times = []
    df_times = []

    print("Running performance comparison...")

    # RDD processing
    rdd_time = process_with_rdd(spark)
    rdd_times.append(rdd_time)
    print(f"Option 1 Processing Time: {rdd_time:.2f} seconds")

    # DataFrame processing
    df_time = process_with_dataframe(spark)
    df_times.append(df_time)
    print(f"Option 2 Processing Time: {df_time:.2f} seconds")

    print("\nFinal Results:")

    print_sample_results(spark)

    spark.stop()


if __name__ == "__main__":
    main()
