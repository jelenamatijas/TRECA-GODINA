from dataSchema import dataSchema
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, unix_timestamp, hour, dayofweek, when, count, avg, expr, round as spark_round, stddev, concat, lit, date_trunc
from meteostatSchema import weatherSchema, getWeather
spark = SparkSession.builder.appName("CityBikeAnalysis").getOrCreate()

filePath = "./CityBike/data/202410-citibike-tripdata/"
filePathWrite = "./CityBike/data/cleaned-tripdata"

data = spark.read.format("csv").option("header", "true").schema(dataSchema).load(filePath)

initialCount = data.count()
print(f"Ukupan broj ucitanih redova iz svih fajlova: {initialCount}")
data.show(5, truncate=False)

dataWithDuration = data.withColumn("duration_sec", unix_timestamp(col("ended_at")) - unix_timestamp(col("started_at")))

cleanedData = dataWithDuration.filter(
    col("ride_id").isNotNull() &
    col("started_at").isNotNull() &
    col("ended_at").isNotNull() &
    (col("ended_at")>col("started_at")) &
    (col("duration_sec")>=60) &
    (col("duration_sec")<=86400)
)

cleanedCount = cleanedData.count()
removedCount = initialCount - cleanedCount
print(f"Broj redova nakon ciscenja: {cleanedCount}")
print(f"Broj uklonjenih redova: {removedCount}")

cleanedData.limit(100).coalesce(1).write.mode("overwrite").option("header", "true").csv(filePathWrite)

# -----------------------------------------------------------------

addedData = cleanedData.withColumn("duration_sec", unix_timestamp(col("ended_at")) - unix_timestamp(col("started_at"))).withColumn("duration_min", col("duration_sec")/60)
addedData = addedData.withColumn("start_hour", hour(col("started_at"))).withColumn("day_of_week", dayofweek(col("started_at"))).withColumn("is_weekend", when(col("day_of_week").isin(1,7), True).otherwise(False))
addedData = addedData.withColumn("is_round_trip", when((col("start_station_id") == col("end_station_id")) & col("start_station_id").isNotNull(), True).otherwise(False))
addedData = addedData.withColumn(
    "ride_purpose",
    when(
        (col("is_weekend") ==False)&
        (col("is_round_trip") == False) &
        (col("start_hour").isin(7,8,9,16,17,18)),
        "Commute"
    ).when(
        (col("is_weekend") == False) &
        (col("start_hour").isin(10,11,12,13,14,15)),
        "Midday Utility"
    ).when(
        (col("is_weekend") == True) | (col("is_round_trip") == True),
        "Recreation" 
    ).when(
        (col("start_hour").isin(22,23,0,1,2,3,4,5)),
        "Night Ride"
    ).otherwise("Other")
)

print("--- Klasifikacije svrhe voznje ---")
addedData.select("ride_id", "start_hour", "is_weekend", "is_round_trip", "ride_purpose").show(10, truncate=False)

# ----------------------------------------------------------------------------

addedData = addedData.withColumn(
    "day_type", when(col("is_weekend")==True, "Weekend").otherwise("Weekday")
)

groupedData = addedData.groupBy("day_type", "member_casual", "start_hour").agg(
    count("ride_id").alias("total_rides"),
    avg("duration_min").alias("average_duration_min"),
    expr("percentile_approx(duration_min, 0.5)").alias("median_duration_min")
).orderBy("day_type", "member_casual", "start_hour")

print("--- Prikaz rezultata po satima, tipu dana i korisniku ---")
groupedData.show(20, truncate=False)

filePathWrite = "./CityBike/data/hourly-ride-analysis-tripdata"
groupedData.coalesce(1).write.mode("overwrite").option("header", "true").csv(filePathWrite)

print(f"Rezultati su sacuvani u {filePathWrite}")

# -----------------------------------------------------------------------------------

stationProfiles = addedData.filter(
    col("start_station_name").isNotNull()
    ).groupBy("start_station_name").agg(
        count("ride_id").alias("total_rides"),
        spark_round(
            count(when(col("member_casual") == "member", 1))/count("ride_id"),
            4
        ).alias("member_share"),
        spark_round(
            count(when(col("is_round_trip") == True, 1))/count("ride_id"),
            4
        ).alias("round_trip_share"),
        spark_round(
            expr("percentile_approx(duration_min, 0.5)"),
            2
        ).alias("median_duration_min"),
        spark_round(
            count(when(col("start_hour").isin(6,7,8,9,10,11), 1))/count("ride_id"),
            4
        ).alias("morning_share"),
        spark_round(
            count(when(col("start_hour").isin(12,13,14,15,16,17), 1))/count("ride_id"),
            4
        ).alias("afternoon_share"),
        spark_round(
            count(when(col("start_hour").isin(18,19,20,21,22), 1))/count("ride_id"),
            4
        ).alias("evening_share"),
        spark_round(
            count(when(col("start_hour").isin(23,0,1,2,3,4,5), 1))/count("ride_id"),
            4
        ).alias("night_share")
    )

stableStations = stationProfiles.filter(
    col("total_rides") >= 500
)

topN = stableStations.orderBy(col("total_rides").desc()).limit(20)

print("--- TOP 20 najaktivnijih rekrativnih stanica sa minimalno 500 voznji ---")
topN.show(20, truncate=False)

filePathWrite = "./CityBike/data/topN"
topN.coalesce(1).write.mode("overwrite").option("header", "true").csv(filePathWrite)
print(f"Rezultati za topN su sacuvani u {filePathWrite}")

topRoundTrip = stableStations.orderBy(col("round_trip_share").desc()).limit(20)

print("--- TOP 20 rekrativnih stanica po udjelu round-trip voznji ---")
topRoundTrip.show(20, truncate=False)

filePathWrite = "./CityBike/data/topN-round-trip"
topRoundTrip.coalesce(1).write.mode("overwrite").option("header", "true").csv(filePathWrite)
print(f"Rezultati za topN su sacuvani u {filePathWrite}")

# ---------------------------------------------------------------------

relationAnalysis = addedData.filter(col("start_station_name").isNotNull() & col("end_station_name").isNotNull()).groupBy("start_station_name", "end_station_name").agg(
    count("ride_id").alias("total_rides"),
    spark_round(expr("percentile_approx(duration_min, 0.5)"), 2).alias("typical_duration_min"),
    spark_round(stddev("duration_min"), 2).alias("duration_std_dev")
)

stableRelations = relationAnalysis.filter(col("total_rides") >= 100).withColumn(
    "relation",
    concat(col("start_station_name"), lit(" -> "), col("end_station_name"))
).select("relation", "total_rides", "typical_duration_min", "duration_std_dev").orderBy(col("total_rides").desc()).limit(20)

print("--- TOP 20 najcescih relacija sa 100 voznji ---")
stableRelations.show(20, truncate=False)

filePathWrite = "./CityBike/data/top-relations-analysis"
stableRelations.coalesce(1).write.mode("overwrite").option("header", "true").csv(filePathWrite)
print(f"Rezultati za analizu relacije su sacuvani u {filePathWrite}")

# ----------------------------------------------------------------------

totalRidesCount = addedData.count()

globalRoundTrip = addedData.groupBy("is_round_trip").agg(
    count("ride_id").alias("total_rides"),
    spark_round((count("ride_id")/totalRidesCount)*100, 2).alias("share_percent"),
    spark_round(expr("percentile_approx(duration_min, 0.5)"), 2).alias("typical_duration_min")
)
print("--- Globalna analiza round-trip voznji ---")
globalRoundTrip.show()

segmented = addedData.groupBy("member_casual", "rideable_type").agg(
    count("ride_id").alias("total_rides"),
    count(when(col("is_round_trip") == True, 1)).alias("round_trip_count"),
    spark_round(
            (count(when(col("is_round_trip") == True, 1)) / count("ride_id")) * 100, 2
        ).alias("round_trip_share_percent"),
    spark_round(
        expr("percentile_approx(if(is_round_trip = true, duration_min, null), 0.5)"),2
    ).alias("typical_duration_round_trip_min"),
    spark_round(
        expr("percentile_approx(if(is_round_trip = false, duration_min, null), 0.5)"),2
    ).alias("typical_duration_oneway_min")
).orderBy("member_casual", "rideable_type")

print("--- Poredjenje round-trip ponasanja po segmentima ---")
segmented.show(truncate=False)

filePathWrite = "./CityBike/data/round-trip-global"
globalRoundTrip.coalesce(1).write.mode("overwrite").option("header", "true").csv(filePathWrite)
print(f"Globalna analiza je sacuvana na: {filePathWrite}")

filePathWrite = "./CityBike/data/round-trip-segmented"
segmented.coalesce(1).write.mode("overwrite").option("header", "true").csv(filePathWrite)
print(f"Segmentna analiza je sacuvana na: {filePathWrite}")

# -------------------------------------------------------------------------

addedData = addedData.withColumn("ride_join_bucket", date_trunc("hour", col("started_at")))
finalWeather = getWeather()
mergedData = addedData.join(
    finalWeather,
    addedData.ride_join_bucket == finalWeather.weather_join_bucket,
    "left"
)

rainImpactAnalysis = mergedData.filter(col("rain_indicator").isNotNull()).groupBy("day_type", "rain_indicator").agg(
    count("ride_id").alias("total_rides"),
    spark_round(expr("percentile_approx(duration_min, 0.5)"), 2).alias("typical_duration_min")
    ).orderBy("day_type", "rain_indicator")

print("--- Analiza uticaja padavina na potraznju i trajanje na osnovu tipa dana ---")
rainImpactAnalysis.show(truncate=False)
filePathWrite = "./CityBike/data/rain-impact-analysis"
rainImpactAnalysis.coalesce(1).write.mode("overwrite").option("header", "true").csv(filePathWrite)
print(f"Rezultati analize padavina sacuvani su na: {filePathWrite}")

tempImpactAnalysis = mergedData.filter(col("temp_band").isNotNull()).groupBy("temp_band", "member_casual").agg(
    count("ride_id").alias("total_rides"),
    spark_round(expr("percentile_approx(duration_min, 0.5)"), 2).alias("typical_duration_min")
    ).orderBy("temp_band", "member_casual")

print("--- Analiza uticaja temperature na potraznju i trajanje na osnovu tipa korisnika ---")
tempImpactAnalysis.show(truncate=False)
filePathWrite = "./CityBike/data/temp-impact-analysis"
tempImpactAnalysis.coalesce(1).write.mode("overwrite").option("header", "true").csv(filePathWrite)
print(f"Rezultati analize temperature sacuvani su na: {filePathWrite}")

# ----------------------------------------------------
