from pyspark import SparkContext, RDD
import time

def pageRankOptimized(nodesRdd: RDD, d: float=0.85, maxIter: int=20, epsilon: float=1e-5, k: int=10) -> RDD:
    start = time.time()

    n = nodesRdd.count()
    if n==0:
        print("Nema cvorova - dati graf je prazan.")
        return nodesRdd
    
    numPartitions = nodesRdd.getNumPartitions()
    numPartitions = max(numPartitions, 8)
        
    graphKV = nodesRdd.map(lambda node: (node.nodeId, node.outNeighbors)).partitionBy(numPartitions=numPartitions).cache() # (nodeId, [neighbors])
    ranks = graphKV.mapValues(lambda _: 1.0/n). partitionBy(numPartitions).cache() # (nodeId, rank)

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
        sumIncoming = contributions.reduceByKey(lambda a, b: a+b, numPartitions=numPartitions)

        baseScore = (1.0-d)/n
        danglingShare = d * (danglingMass /n)

        newRanks = (
            graphKV.leftOuterJoin(sumIncoming)
            .mapValues(lambda x: baseScore + danglingShare + d *(x[1] if x[1] is not None else 0.0))
            .partitionBy(numPartitions)
            .cache()
        )
        
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
    
