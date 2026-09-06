import time
from pyspark import RDD, SparkContext

def print_rdd_execution_plan(rdd: RDD) -> None:
    raw = rdd.toDebugString()
    if isinstance(raw, (bytes, bytearray)):
        text = raw.decode("utf-8", errors="replace")
    else:
        text = str(raw)

    lines = text.strip().splitlines()

    for line in lines:
        pretty = (
            line
            .replace("|  ", "│  ")
            .replace("+-", "└─")
        )
        print(pretty)

sc = SparkContext(appName="WordCountRDD")

file_path = "./data/bible.txt"

start_time = time.time()

lines: RDD[str] = sc.textFile(file_path)


words: RDD[str] = lines.flatMap(
    lambda line: line.lower().replace("[^a-zA-Z ]", "").split()
)

paired_words: RDD[tuple[str, int]] = words.map(lambda word: (word, 1))

grouped_words = paired_words.groupByKey()


word_counts = grouped_words.mapValues(lambda x: sum(x))  # type: ignore

print_rdd_execution_plan(word_counts)
# Zašto se ova varijanta funkcije brže izvršava?
word_counts = (
    lines.flatMap(lambda line: line.lower().replace("[^a-zA-Z ]", "").split())
    .map(lambda word: (word, 1))
    .reduceByKey(lambda a, b: a + b)  # type: ignore
)

print_rdd_execution_plan(word_counts)

# result = word_counts.collect()

end_time = time.time()

sc.stop()

print((end_time - start_time) * 1000)
