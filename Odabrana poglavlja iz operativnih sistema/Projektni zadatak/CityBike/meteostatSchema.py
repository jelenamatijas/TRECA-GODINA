from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StructField,IntegerType, StringType, DoubleType
from pyspark.sql.functions import col, concat, when, from_utc_timestamp, date_trunc, coalesce, lit, lpad, to_timestamp

spark = SparkSession.builder.appName("CityBike Weather app").getOrCreate()

weatherSchema = StructType([
    StructField("year", IntegerType(), True),
    StructField("month", IntegerType(), True),
    StructField("day", IntegerType(), True),
    StructField("hour", IntegerType(), True),
    StructField("temp", DoubleType(), True),
    StructField("temp_source", StringType(), True),
    StructField("rhum", DoubleType(), True),
    StructField("rhum_source", StringType(), True),
    StructField("prcp", DoubleType(), True),
    StructField("prcp_source", StringType(), True),
    StructField("wdir", DoubleType(), True),
    StructField("wdir_source", StringType(), True),
    StructField("wspd", DoubleType(), True),
    StructField("wspd_source", StringType(), True),
    StructField("wpgt", DoubleType(), True),
    StructField("wpgt_source", StringType(), True),
    StructField("pres", DoubleType(), True),
    StructField("pres_source", StringType(), True),
    StructField("cldc", DoubleType(), True),
    StructField("cldc_source", StringType(), True),
    StructField("coco", DoubleType(), True),
    StructField("coco_source", StringType(), True)
])

def getWeather():
    filePath = "./CityBike/data/weather/KJRB0.csv"
    weatherData = spark.read.format("csv").option("header", "true").schema(weatherSchema).load(filePath)

    weatherDataPrepared = weatherData.withColumn(
        "time_string",
        concat(
            col("year"), lit("-"),
            lpad(col("month"), 2, "0"), lit("-"),
            lpad(col("day"), 2, "0"), lit(" "),
            lpad(col("hour"), 2, "0"), lit(":00:00"),
        )
    ).withColumn(
        "time", to_timestamp(col("time_string"), "yyyy-MM-dd HH:mm:ss")
    )

    weatherDataPrepared = weatherDataPrepared.withColumn(
        "time_local", 
        from_utc_timestamp(col("time"), "America/New_York")
    ).withColumn(
        "weather_join_bucket", 
        date_trunc("hour", col("time_local"))
    ).filter(
        (col("time_local") >= "2024-10-01 00:00:00") &
        (col("time_local") <= "2024-10-31 23:59:59")
    )

    finalWeatherData = weatherDataPrepared.withColumn(
        "rain_indicator",
        when(coalesce(col("prcp"), lit(0.0)) > 0.0, "rainy").otherwise("non-rainy")
        ).withColumn(
        "temp_band",
            when(col("temp") < 5, "B1") 
            .when((col("temp") >= 5) & (col("temp") < 10), "B2") 
            .when((col("temp") >= 10) & (col("temp") < 15), "B3") 
            .when((col("temp") >= 15) & (col("temp") < 20), "B4")
            .when(col("temp") >= 20, "B5")
            .otherwise(lit(None))
        ).withColumn(
            "wind_band",
            when(coalesce(col("wspd"), lit(0.0)) < 10.0, "weak wind").
            when((col("wspd") >= 10.0) & (col("wspd") < 20.0), "intermediate wind").
            otherwise("strong wind")
        ).select(
            "weather_join_bucket",
            "temp",
            "prcp",
            "wspd",
            "rain_indicator",
            "temp_band",
            "wind_band"
        )

    print("--- Meteostat dataset ---")
    finalWeatherData.show(15, truncate=False)

    filePath = "./CityBike/data/prepared-weather-data"
    finalWeatherData.coalesce(1).write.mode("overwrite").option("header", "true").csv(filePath)
    return finalWeatherData

# 1. INDIKATOR PADAVINA (rain_indicator):
#    - "rainy": Kada su ukupne padavine u proteklom satu (prcp) veće od 0.0 mm.
#    - "non-rainy": Kada padavina nije bilo (prcp == 0.0 mm ili NULL).
#    Definicija sluzi za poredjenje odziva korisnika u kisnim u odnosu na suve uslove.
# 2. TEMPERATURNI BANDOVI (temp_band):
#    Kategorizacija vanjske temperature (°C) u 5 definisanih opsega:
#    - B1: Vrlo hladno  (temp < 5°C)
#    - B2: Hladno       (5°C <= temp < 10°C)
#    - B3: Umjereno     (10°C <= temp < 15°C)
#    - B4: Toplo        (15°C <= temp < 20°C)
#    - B5: Vrlo toplo   (temp >= 20°C)
#    Definicija omogucava analizu uticaja ambijentalne temperature na duzinu i broj voznji.
# 3. BANDOVI VJETRA (wind_band):
#    Klasifikacija brzine vjetra (wspd u km/h) u tri kategorije:
#    - "weak wind":         brzina vjetra < 10 km/h
#    - "intermediate wind": 10 km/h <= brzina vjetra < 20 km/h
#    - "strong wind":       brzina vjetra >= 20 km/h
#    Definicija sluzi kao dodatna kontrolna varijabla u analizi otezanih uslova vožnje.