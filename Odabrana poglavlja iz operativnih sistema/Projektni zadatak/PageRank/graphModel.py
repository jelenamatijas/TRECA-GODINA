import json
from typing import List, Optional
class Node:
    def __init__(self, nodeId: int, outNeighbors: Optional[List[int]]=None): # ne moze list[int]=None ???
        self.nodeId = nodeId
        self.outNeighbors = outNeighbors if outNeighbors is not None else []
        self.outDegree = len(self.outNeighbors)

    def toDict(self) -> dict:
        return {
            "nodeId": self.nodeId,
            "outNeighbors": self.outNeighbors
        }

    @classmethod
    def fromDict(cls, data: dict):
        return cls(nodeId = data["nodeId"], outNeighbors = data["outNeighbors"])
    
    def __repr__(self) -> str:
        return f"Node(ID={self.nodeId}, outDegree={self.outDegree}, neighbors={self.outNeighbors})"
        