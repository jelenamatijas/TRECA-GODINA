from pyspark import SparkContext, RDD
import time

def pageRank(nodesRdd: RDD, d: float=0.85, maxIter: int=20, epsilon: float=1e-5, k: int=10) -> RDD:
    start = time.time()

    n = nodesRdd.count()
    if n==0:
        print("Nema cvorova - dati graf je prazan.")
        return nodesRdd
    
    graphKV = nodesRdd.map(lambda node: (node.nodeId, node.outNeighbors)).cache() # (nodeId, [neighbors])
    ranks = graphKV.mapValues(lambda _: 1.0/n) # (nodeId, rank)
    graph = graphKV

    for i in range(1, maxIter+1):
        startIter = time.time()
        joined = graphKV.join(ranks) # (nodeId, ([neighbors], rank))
        danglingMass = joined.filter(lambda x: len(x[1][0])==0).map(lambda x: x[1][1]).sum()

        def computeContributios(nodeData):
            nodeId, (neighbors, rank) = nodeData
            numNeighbors = len(neighbors)

            if numNeighbors>0:
                for neighbor in neighbors:
                    yield(neighbor, rank/numNeighbors)
            else:
                yield(nodeId, 0.0)
        
        contributions = joined.flatMap(computeContributios)
        sumIncoming = contributions.reduceByKey(lambda a, b: a+b)

        baseScore = (1.0-d)/n
        newRanks = graph.leftOuterJoin(sumIncoming)\
                    .mapValues(lambda x: baseScore + d*((x[1] if x[1] is not None else 0.0) + (danglingMass/n)))\
                    .cache()
        
        difference = ranks.join(newRanks).map(lambda x: abs(x[1][0] - x[1][1])).sum()
        
        ranks.unpersist()
        ranks = newRanks

        endIter = time.time()
        iterDuration = endIter - startIter
        print(f"Iteracija {i}/{maxIter} - Razlika: {difference:.4f} | Vrijeme izvrsavanja: {iterDuration}")

        if difference<epsilon:
            print(f"Konvergencija postignuta u iteraciji {i}.")
            break

    end = time.time()
    print(f"Ukupno vrijeme izvrsavanja algoritma: {end - start}")

    topK = ranks.takeOrdered(k, key=lambda x: -x[1]) # type: ignore

    print(f"\n--- Top {k} cvorova po rangu ---")
    for rankIndex, (nodeId, rank) in enumerate(topK, 1):
        print(f"{rankIndex}. Node ID: {nodeId:<5} | Rank: {rank:.4f}")

    return ranks
    
def validate(graph: RDD, ranks: RDD, tolerance: float = 1e-4) -> bool:
    passed = True

    expectedCount = graph.count()
    actualCount = ranks.count()
    
    if expectedCount == actualCount:
        print(f"[OK] Broj rangova odgovara broju cvorova u grafu.")
    else:
        print(f"[FAIL] Broj rangova ne odgovara broju cvorova u grafu.")
        passed = False

    negativeRanks = ranks.filter(lambda x: x[1]<0).count()

    if negativeRanks == 0:
        print(f"[OK] Svi rangovi su nenegativni.")
    else:
        print(f"[FAIL] Pronadjeni su negativni rangovi.")
        passed = False

    totalSum = ranks.map(lambda x: x[1]).sum()

    if abs(totalSum-1.0)<=tolerance:
        print(f"[OK] Suma svih rangova je stabilna i iznosi {totalSum}.")
    else:
        print(f"[FAIL] Suma rangova nije stabilna i iznosi {totalSum}")
        passed = False

    danglingIds = graph.filter(lambda node: len(node.outNeighbors) == 0).map(lambda node: node.nodeId)
    danglingCount = danglingIds.count()

    if danglingCount>0:
        danglingRanks = ranks.join(danglingIds.map(lambda x: (x, None)))

        minRank = danglingRanks.map(lambda x: x[1][0]).min() if danglingCount>0 else 0.0

        if minRank > 0.0:
            print(f"[OK] Graf sadrzi {danglingCount} dangling cvorova. Svi su ispravno tretirani.")
        else:
            print(f"[FAIL] Dangling cvorovi nisu pravilno tretirani.")
            passed = False

    else:
        print("Ovaj graf nema dangling cvorova za testiranje.")

    return passed