# temperature_data_generator.py
import random
import time
from datetime import datetime, timedelta

import numpy as np
from pyspark import SparkConf, SparkContext


class TemperatureDataGenerator:
    def __init__(self):
        self.cities_metadata = {
            "New York": {
                "base_temp": 12,
                "seasonal_variation": 15,
                "daily_variation": 5,
            },
            "Los Angeles": {
                "base_temp": 19,
                "seasonal_variation": 8,
                "daily_variation": 3,
            },
            "Chicago": {
                "base_temp": 10,
                "seasonal_variation": 18,
                "daily_variation": 6,
            },
            "Houston": {
                "base_temp": 20,
                "seasonal_variation": 12,
                "daily_variation": 4,
            },
            "Phoenix": {
                "base_temp": 23,
                "seasonal_variation": 15,
                "daily_variation": 7,
            },
            "Philadelphia": {
                "base_temp": 13,
                "seasonal_variation": 16,
                "daily_variation": 5,
            },
            "San Antonio": {
                "base_temp": 20,
                "seasonal_variation": 13,
                "daily_variation": 5,
            },
            "San Diego": {
                "base_temp": 18,
                "seasonal_variation": 6,
                "daily_variation": 2,
            },
            "Dallas": {"base_temp": 19, "seasonal_variation": 14, "daily_variation": 6},
            "San Jose": {
                "base_temp": 17,
                "seasonal_variation": 7,
                "daily_variation": 3,
            },
        }

    def generate_city_temperature(self, city, date):
        """Generate realistic temperature for a given city and date"""
        metadata = self.cities_metadata[city]

        day_of_year = date.timetuple().tm_yday

        base_temp = metadata["base_temp"]

        seasonal_effect = metadata["seasonal_variation"] * np.sin(
            2 * np.pi * (day_of_year - 80) / 365
        )  # Shifted to peak in summer

        daily_variation = random.uniform(-1, 1) * metadata["daily_variation"]

        temperature = base_temp + seasonal_effect + daily_variation

        temperature += random.gauss(0, 0.5)

        return round(temperature, 2)

    def generate_readings_for_city(self, city, start_date, num_days, readings_per_day):
        """Generate multiple readings for a city over a period"""
        readings = []

        for day in range(num_days):
            current_date = start_date + timedelta(days=day)

            for _ in range(readings_per_day):
                hour = random.randint(0, 23)
                minute = random.randint(0, 59)
                timestamp = current_date.replace(hour=hour, minute=minute)

                temperature = self.generate_city_temperature(city, current_date)

                hour_effect = -2 * np.cos(2 * np.pi * hour / 24)  # Cooler at night
                temperature += hour_effect

                readings.append(
                    (city, timestamp.strftime("%Y-%m-%d %H:%M"), temperature)
                )

        return readings

    def generate_dataset(self, num_days=365, readings_per_day=24):
        """Generate complete dataset for all cities"""
        start_date = datetime(2023, 1, 1)
        all_readings = []

        for city in self.cities_metadata.keys():
            city_readings = self.generate_readings_for_city(
                city, start_date, num_days, readings_per_day
            )
            all_readings.extend(city_readings)

        random.shuffle(all_readings)
        return all_readings


conf = SparkConf().setAppName("TemperaturePartitioning").setMaster("local[*]")
sc = SparkContext(conf=conf)


class CityPartitioner:
    def __init__(self, cities):
        self.cities = {city: idx for idx, city in enumerate(cities)}

    def __call__(self, key):
        return self.cities.get(key, 0)


def measure_time(func):
    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = func(*args, **kwargs)
        end_time = time.time()
        print(f"Execution time: {end_time - start_time:.4f} seconds")
        return result

    return wrapper

data_generator = TemperatureDataGenerator()

temperature_data = data_generator.generate_dataset(
    num_days=365 * 5,
    readings_per_day=96
)
cities = list(data_generator.cities_metadata.keys())

print(f"Generated {len(temperature_data)} temperature readings")

# (city, (timestamp, temp))
default_keyed_rdd = sc.parallelize(temperature_data).map(
    lambda x: (x[0], (x[1], x[2]))
)

# Second RDD: per-city configuration / metadata
# (city, (elevation, population))
city_configs = [
    (
        city,
        (
            random.randint(0, 500),                  # elevation in meters
            random.randint(100_000, 10_000_000),     # population
        ),
    )
    for city in cities
]
city_config_rdd_default = sc.parallelize(city_configs)

custom_partitioner = CityPartitioner(cities)

readings_partitioned = (
    default_keyed_rdd
    .partitionBy(numPartitions=len(cities), partitionFunc=custom_partitioner)
    .cache()
)
city_config_partitioned = (
    city_config_rdd_default
    .partitionBy(numPartitions=len(cities), partitionFunc=custom_partitioner)
    .cache()
)

default_keyed_rdd.cache()
city_config_rdd_default.cache()


def expensive_city_simulation(temps: list[float]) -> float:
    """
    Artificially expensive computation per city: several numeric passes.
    """
    arr = np.array(temps, dtype=float)
    for _ in range(40):  # increase to make it heavier
        arr = np.tanh(arr * 1.01) * 30 + np.sin(arr)
    return float(arr.mean())


@measure_time
def city_level_pipeline_default(readings_rdd, config_rdd):
    """
    Default partitioning:
      - join will cause a full shuffle of both RDDs
      - then we do heavy per-city work
    """
    # readings_rdd: (city, (timestamp, temp))
    # config_rdd  : (city, (elevation, population))

    # Join by city
    joined = readings_rdd.mapValues(lambda v: v[1]).join(config_rdd)
    # joined: (city, (temp, (elevation, population)))

    # Group all temps per city and run heavy simulation
    city_stats = (
        joined
        .mapValues(lambda v: v[0])  # keep only temperatures
        .groupByKey()
        .mapValues(lambda temps: expensive_city_simulation(list(temps)))
    )

    result = city_stats.collect()
    return result


@measure_time
def city_level_pipeline_custom(readings_rdd, config_rdd):
    """
    Custom partitioning:
      - readings_rdd and config_rdd are already partitioned by the same
        CityPartitioner (same number of partitions).
      - join can reuse partitioning and avoid an extra full shuffle.
    """
    joined = readings_rdd.mapValues(lambda v: v[1]).join(config_rdd)
    city_stats = (
        joined
        .mapValues(lambda v: v[0])  # keep only temperatures
        .groupByKey()
        .mapValues(lambda temps: expensive_city_simulation(list(temps)))
    )

    result = city_stats.collect()
    return result


print("\n=== City-level heavy pipeline with DEFAULT partitioning ===")
result_default = city_level_pipeline_default(
    default_keyed_rdd,
    city_config_rdd_default,
)
print("Sample:", result_default[:3])

print("\n=== City-level heavy pipeline with CUSTOM city partitioning ===")
result_custom = city_level_pipeline_custom(
    readings_partitioned,
    city_config_partitioned,
)
print("Sample:", result_custom[:3])

print("\nPartition distribution for DEFAULT readings:")
default_partition_sizes = default_keyed_rdd.glom().map(len).collect()
for i, size in enumerate(default_partition_sizes):
    print(f"Partition {i}: {size} records")

print("\nPartition distribution for CUSTOM readings:")
custom_partition_sizes = readings_partitioned.glom().map(len).collect()
for i, size in enumerate(custom_partition_sizes):
    print(f"Partition {i}: {size} records")

sc.stop()