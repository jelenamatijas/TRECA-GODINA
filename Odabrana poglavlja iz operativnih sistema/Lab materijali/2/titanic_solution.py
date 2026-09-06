from dataclasses import dataclass
from typing import Literal

from pyspark import RDD, SparkContext

Survived = Literal[0, 1]
Pclass = Literal[1, 2, 3]
Sex = Literal["male", "female"]
Embarked = Literal["C", "Q", "S"]


@dataclass
class Passenger:
    passenger_id: int
    survived: Survived
    pclass: Pclass
    name: str
    sex: Sex
    age: float | None
    sibsp: int
    parch: int
    ticket: str
    fare: float | None
    cabin: str
    embarked: Embarked | None


def split_csv_line(line: str) -> list[str]:
    parts: list[str] = []
    current: list[str] = []
    in_quotes = False

    i = 0
    while i < len(line):
        ch = line[i]

        if ch == '"':
            if in_quotes and i + 1 < len(line) and line[i + 1] == '"':
                current.append('"')
                i += 1
            else:
                in_quotes = not in_quotes
        elif ch == "," and not in_quotes:
            parts.append("".join(current))
            current = []
        else:
            current.append(ch)
        i += 1

    parts.append("".join(current))
    return parts


def parse_float(value: str) -> float | None:
    value = value.strip()
    if value == "":
        return None
    try:
        return float(value)
    except ValueError:
        return None


def parse_line(line: str) -> Passenger:
    parts = split_csv_line(line)

    if len(parts) != 12:
        raise ValueError(
            f"Unexpected number of columns: {len(parts)} in line: {line!r}"
        )

    passenger_id = int(parts[0])

    survived_val = int(parts[1])
    survived: Survived = survived_val  # type: ignore[assignment]

    pclass_val = int(parts[2])
    pclass: Pclass = pclass_val  # type: ignore[assignment]

    name = parts[3]

    sex_raw = parts[4].strip().lower()
    sex: Sex = sex_raw  # type: ignore[assignment]

    age = parse_float(parts[5])

    sibsp = int(parts[6]) if parts[6].strip() != "" else 0
    parch = int(parts[7]) if parts[7].strip() != "" else 0

    ticket = parts[8].strip()
    fare = parse_float(parts[9])
    cabin = parts[10].strip()

    embarked_raw = parts[11].strip().upper()
    embarked: Embarked | None
    if embarked_raw in ("C", "Q", "S"):
        embarked = embarked_raw  # type: ignore[assignment]
    else:
        embarked = None

    return Passenger(
        passenger_id=passenger_id,
        survived=survived,
        pclass=pclass,
        name=name,
        sex=sex,
        age=age,
        sibsp=sibsp,
        parch=parch,
        ticket=ticket,
        fare=fare,
        cabin=cabin,
        embarked=embarked,
    )


def load_passengers(sc: SparkContext, path: str) -> RDD[Passenger]:

    lines = sc.textFile(path)
    header = lines.first()

    return lines.filter(lambda line: line != header).map(parse_line)


def survival_rate_by_class_and_sex(
    passengers: RDD[Passenger],
) -> RDD[tuple[tuple[Pclass, Sex], float]]:
    """
    Compute survival rate for each (Pclass, Sex) pair using only RDD API.

    Returns:
        RDD where each element is ((pclass, sex), survival_rate).
    """
    # (Pclass, Sex) -> (sum_survived, count)
    class_sex_stats: RDD[tuple[tuple[Pclass, Sex], tuple[int, int]]] = passengers.map(
        lambda p: ((p.pclass, p.sex), (p.survived, 1))
    ).reduceByKey(lambda a, b: (a[0] + b[0], a[1] + b[1]))

    rates: RDD[tuple[tuple[Pclass, Sex], float]] = class_sex_stats.map(
        lambda kv: (kv[0], kv[1][0] / float(kv[1][1]))
    )

    return rates


def average_fare_by_embarked_and_class(
    passengers: RDD[Passenger],
) -> RDD[tuple[tuple[Embarked, Pclass], float]]:
    """
    Compute average fare for each (Embarked, Pclass) combination using RDD API.

    Only considers passengers with non-null fare and non-null embarked.
    """
    # ((Embarked, Pclass) -> (sum_fare, count))
    stats: RDD[tuple[tuple[Embarked, Pclass], tuple[float, int]]] = (
        passengers.filter(lambda p: p.fare is not None and p.embarked is not None)
        .map(
            lambda p: (
                (p.embarked, p.pclass),  # type: ignore[arg-type]
                (p.fare if p.fare is not None else 0.0, 1),
            )
        )
        .reduceByKey(lambda a, b: (a[0] + b[0], a[1] + b[1]))
    )

    averages: RDD[tuple[tuple[Embarked, Pclass], float]] = stats.map(
        lambda kv: (kv[0], kv[1][0] / float(kv[1][1]))
    )

    return averages


def _extract_family(name: str) -> str | None:
    """
    Extract family name from full passenger name.

    Titanic names are of the form "Last, Title. First ...".
    Returns the part before the first comma, lowercased and stripped.
    """
    if not name:
        return None
    return name.split(",", 1)[0].strip().lower()


def top_families_by_survival(
    passengers: RDD[Passenger],
    min_family_size: int = 3,
    top_n: int = 5,
) -> list[tuple[str, int, float]]:
    """
    Compute the top families by survival rate.

    Family definition:
        - substring before the first comma in Passenger.name.

    Steps:
        - Group by family name.
        - For each family compute:
            * family_size
            * survived_count
            * survival_rate = survived_count / family_size
        - Ignore families with size < min_family_size.
        - Sort by:
            1) survival_rate descending
            2) family_size descending
            3) family name ascending
        - Return top_n entries as a Python list.
    """
    # (family_name -> (sum_survived, count))
    family_stats: RDD[tuple[str, tuple[int, int]]] = (
        passengers.map(lambda p: (_extract_family(p.name), (p.survived, 1)))
        .filter(lambda kv: kv[0] is not None)
        .map(lambda kv: (kv[0] or "", kv[1]))
        .reduceByKey(lambda a, b: (a[0] + b[0], a[1] + b[1]))
    )

    family_rates: RDD[tuple[str, int, float]] = family_stats.filter(
        lambda kv: kv[1][1] >= min_family_size
    ).map(
        lambda kv: (
            kv[0],  # family name
            kv[1][1],  # size
            kv[1][0] / float(kv[1][1]),  # survival_rate
        )
    )

    sorted_families: list[tuple[str, int, float]] = family_rates.sortBy(
        lambda x: (-x[2], -x[1], x[0])
    ).take(top_n)

    return sorted_families


def main() -> None:
    sc = SparkContext(appName="TitanicRDD")

    passengers = load_passengers(sc, "./data/titanic.csv")

    print("\n=== Survival rate by (Pclass, Sex) ===")
    class_sex = survival_rate_by_class_and_sex(passengers)
    for (pclass, sex), rate in class_sex.collect():
        print(f"Pclass={pclass}, Sex={sex}, Survival rate={rate:.3f}")

    print("\n=== Average fare by (Embarked, Pclass) ===")
    embarked_class = average_fare_by_embarked_and_class(passengers)
    for (emb, pclass), avg_fare in embarked_class.collect():
        print(f"Embarked={emb}, Pclass={pclass}, Avg fare={avg_fare:.2f}")

    print("\n=== Top families by survival (min size=3, top 5) ===")
    families = top_families_by_survival(passengers, min_family_size=3, top_n=5)
    for family, size, rate in families:
        print(f"Family={family}, Size={size}, Survival rate={rate:.3f}")

    sc.stop()


if __name__ == "__main__":
    main()

