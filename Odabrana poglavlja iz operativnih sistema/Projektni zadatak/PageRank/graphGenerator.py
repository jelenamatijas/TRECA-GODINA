import random
from pyspark import SparkContext, RDD
from graphModel import Node
import json

def generateGraph(sc: SparkContext, numNodes: int, numEdges: int, seed: int) -> RDD[Node]:
    rdds = sc.parallelize(range(numEdges*2), numSlices=4) # 4 particije jer imam 2 "radnika"

    def generateRandomEdge(index):
        random.seed(seed + index)
        source = random.randint(0, numNodes-1)
        destination = random.randint(0, numNodes-1)
        return (source, destination)
    
    edgesRDD = (
        rdds
        .map(generateRandomEdge)
        .filter(lambda edge: edge[0] != edge[1])
        .distinct()
        .zipWithIndex()
        .filter(lambda pair: pair[1] < numEdges)
        .map(lambda pair: pair[0])
    )

    groupedEdges = edgesRDD.groupByKey().mapValues(list)
    allNodesIDs = sc.parallelize(range(numNodes)).map(lambda nodeId: (nodeId, []))
    
    finalList = (
        allNodesIDs
        .union(groupedEdges)
        .reduceByKey(lambda a, b: a if len(a) > len(b) else b)
    )

    nodesRDD = finalList.map(lambda node: Node(nodeId=node[0], outNeighbors=node[1]))

    return nodesRDD

def loadGraph(sc: SparkContext, filePath: str) -> RDD[Node]:
    lines = sc.textFile(filePath)

    def parseLine(line):
        parts = line.replace(",", " ").split()
        if len(parts) >=2:
            try:
                source = int(parts[0])
                destination = int(parts[1])
                if source != destination:
                    return [(source, destination)]
            except ValueError:
                pass
        return []
    
    edgesRDD = (
        lines.flatMap(parseLine)
        .distinct()
    )

    groupedEdges = edgesRDD.groupByKey().mapValues(list)

    allIDs = edgesRDD.flatMap(lambda edge: [edge[0], edge[1]]).distinct()
    allNodes = allIDs.map(lambda nodeId: (nodeId, []))

    finalNodesRDD = (
        allNodes.union(groupedEdges)
        .reduceByKey(lambda a, b: a if len(a) > len(b) else b)
        .map(lambda node: Node(nodeId=node[0], outNeighbors=node[1]))
    )

    return finalNodesRDD

def serialize(nodesRDD: RDD[Node], path: str):
    (
        nodesRDD
        .map(lambda node: json.dumps(node.toDict()))
        .saveAsTextFile(path)
    )

def deserialize(sc: SparkContext, path: str) -> RDD[Node]:
    lines = sc.textFile(path)

    nodesRDD = (
        lines.map(lambda line: json.loads(line))
        .map(lambda dict: Node.fromDict(dict))
    )
    return nodesRDD
