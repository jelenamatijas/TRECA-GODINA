from pyspark import SparkContext, RDD
import time

def pageRankPersonalized(nodesRdd: RDD, seedSet: set, d: float=0.85, maxIter: int=20, epsilon: float=1e-5, k: int=10) -> RDD:
    start = time.time()
    sc = nodesRdd.context
    broadcast = sc.broadcast(seedSet)
    lenBroadcast = len(seedSet)

    if lenBroadcast == 0:
        print("Skup seed cvorova S je prazan.")
        return nodesRdd

    n = nodesRdd.count()
    if n==0:
        print("Nema cvorova - dati graf je prazan.")
        return nodesRdd
    
    numPartitions = nodesRdd.getNumPartitions()
    numPartitions = max(numPartitions, 8)

    graphKV = nodesRdd.map(lambda node: (node.nodeId, node.outNeighbors)).partitionBy(numPartitions=numPartitions).cache() # (nodeId, [neighbors])
    ranks = graphKV.mapValues(lambda _: 1.0/n).partitionBy(numPartitions=numPartitions).cache() # (nodeId, rank)

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
        sumIncoming = contributions.reduceByKey(lambda a, b: a+b, numPartitions = numPartitions)

        localSeedSet = broadcast.value

        def calculatePPR(x):
            nodeId, (neighbors, incoming) = x
            increment = incoming if incoming is not None else 0.0
            if nodeId in localSeedSet:
                teleport = (1.0-d)/lenBroadcast
                dangingShare = d * (danglingMass/lenBroadcast)
                return (nodeId, teleport + d * increment + dangingShare)
            else:
                return (nodeId, d * increment)
            
        newRanks = (graphKV.leftOuterJoin(sumIncoming)
                    .map(calculatePPR)
                    .partitionBy(numPartitions)
                    .cache())
        
        difference = ranks.join(newRanks).map(lambda x: abs(x[1][0] - x[1][1])).sum()
        
        ranks.unpersist()
        ranks = newRanks

        endIter = time.time()
        iterDuration = endIter - startIter
        print(f"Iteracija {i}/{maxIter} - Razlika: {difference:.4f} | Vrijeme izvrsavanja: {iterDuration}")

        if difference<epsilon:
            break

    end = time.time()
    print(f"Ukupno vrijeme izvrsavanja algoritma: {end - start}")
 
    topK = ranks.takeOrdered(k, key=lambda x: -x[1]) # type: ignore
 
    print(f"\n--- Top {k} cvorova po rangu (personalizovani PageRank) ---")
    for rankIndex, (nodeId, rank) in enumerate(topK, 1):
        print(f"{rankIndex}. Node ID: {nodeId:<5} | Rank: {rank:.4f}")

    return ranks
    
def calculateOverlap(topKS, topKP) -> float:
    setS = set([nodeId for nodeId, _ in topKS])
    setP = set([nodeId for nodeId, _ in topKP])

    intersection = setS.intersection(setP)
    k = len(setS)

    if k == 0:
        return 0.0
    return (len(intersection) / k)*100.0