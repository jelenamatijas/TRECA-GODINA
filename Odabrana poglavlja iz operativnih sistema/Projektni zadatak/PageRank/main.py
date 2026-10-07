from pyspark import SparkContext, RDD
from graphGenerator import generateGraph, serialize, deserialize, loadGraph
from pageRank import pageRank, validate
from optimized import pageRankOptimized
from personalized import pageRankPersonalized, calculateOverlap
import time
import argparse
import os
import json

def main():
    parser = argparse.ArgumentParser(description="PageRank & Personalized PageRank u PySpark RDD")
    inputGroup = parser.add_argument_group("Ulazni podaci")
    inputGroup.add_argument("--input_file", type=str, default=None, help="Putanja do edge-list tekstualnog fajla grafa.")
    inputGroup.add_argument("-N", type=int, default=10, help="Broj cvorova za generisanje grafa (default: 10)")
    inputGroup.add_argument("-E", type=int, default=20, help="Broj grana za generisanje u grafu (default: 20)")
    inputGroup.add_argument("--seed", type=int, default=42, help="Seed za generator slucajnih vrijednosti (default: 42)")

    outputGroup = parser.add_argument_group("Izlazni direktorijumi ili fajlovi")
    outputGroup.add_argument("--serialize_out", type=str, default=None, help="Opciona putanja za serijalizaciju grafa u JSON")
    outputGroup.add_argument("--ranks_out", type=str, default="./data/finalRanks", help="Putanja za cuvanje finalnih rangova (default: ./data/finalRanks)")

    algorithmGroup = parser.add_argument_group("PageRank parametri")
    algorithmGroup.add_argument("-d", type=float, default=0.85, help="Damping faktor (default: 0.85).")
    algorithmGroup.add_argument("--maxIter", type=int, default=20, help="Maksimalan broj iteracija algoritma (default: 20).")
    algorithmGroup.add_argument("--epsilon", type=float, default=1e-5, help="Kriterijum stabilizacije/konvergencije (default: 1e-5).")
    algorithmGroup.add_argument("-K", type=int, default=5, help="Broj top-K čvorova za prikaz na konzoli (default: 5).")
    algorithmGroup.add_argument("--optimized", action="store_true", help="Pokreni optimizovanu verziju algoritma sa partitionBy.")

    personalizedGroup = parser.add_argument_group("Personalizovani PageRank opcije")
    personalizedGroup.add_argument("--personalized", action="store_true", help="Pokreni personalizovani PageRank.")
    personalizedGroup.add_argument("--seed_nodes", type=str, default=None, help="Lista ID-eva seed cvorova odvojenih zarezom (npr. 1,2,3)")
    personalizedGroup.add_argument("--seed_file", type=str, default=None, help="Putanja do fajla sa ID-evima seed cvorova (jedan po redu)")
    personalizedGroup.add_argument("--run_experiment", action="store_true", help="Pokreni eksperiment na 10 nasumicnih grafova i sacuvaj JSON.")
    personalizedGroup.add_argument("--json_out", type=str, default="./data/experiment_results.json", help="Putanja za cuvanje JSON rezultata eksperimenta.")

    args = parser.parse_args()

    sc = SparkContext(appName="PageRank")
    sc.setLogLevel("WARN")

    setS = set()
    if args.seed_nodes:
        setS = set(int(x.strip()) for x in args.seed_nodes.split(","))
    elif args.seed_file and os.path.exists(args.seed_file):
        with open(args.seed_file, "r") as f:
            setS = set(int(line.strip()) for line in f if line.strip())
    else:
        setS = {1, 2, 3}

    if args.run_experiment:
        print(f"\n--- POKRETANJE EKSPERIMENTA NAD 10 GRAFOVA ---")
        experiment_results = []

        for idx in range(1, 11):
            currentSeed = args.seed + idx
            print(f"\nIteracija eksperimenta {idx}/10 (Seed: {currentSeed})")
            
            expGraph = generateGraph(sc, numNodes=args.N, numEdges=args.E, seed=currentSeed)
            
            ranksS = pageRank(nodesRdd=expGraph, d=args.d, maxIter=args.maxIter, epsilon=args.epsilon, k=args.K)
            topKS = ranksS.takeOrdered(args.K, key=lambda x: -x[1])
            topKSids = [node_id for node_id, _ in topKS]

            ranksP = pageRankPersonalized(nodesRdd=expGraph, seedSet=setS, d=args.d, maxIter=args.maxIter, epsilon=args.epsilon, k=args.K)
            topKP = ranksP.takeOrdered(args.K, key=lambda x: -x[1])
            topKPids = [node_id for node_id, _ in topKP]

            overlap = calculateOverlap(topKS, topKP)

            experiment_results.append({
                "run_id": idx,
                "seed": currentSeed,
                "num_nodes": args.N,
                "num_edges": args.E,
                "seed_set_S": list(setS),
                "topK_standard": topKSids,
                "topK_personalized": topKPids,
                "overlap_percentage": overlap
            })
            
            expGraph.unpersist()
            ranksS.unpersist()
            ranksP.unpersist()

        dir_name = os.path.dirname(args.json_out)
        if dir_name:
            os.makedirs(dir_name, exist_ok=True)

        with open(args.json_out, "w") as jf:
            json.dump(experiment_results, jf, indent=4)
        
        print(f"\nEksperiment uspješno završen i sačuvan u {args.json_out}!")
        sc.stop()
        return

    graph: RDD

    if args.input_file:
        print(f"\n--- 1. Ucitavanje grafa iz fajla: {args.input_file} ---")
        graph = loadGraph(sc, args.input_file)
    else:
        print(f"\n--- 1. Generisanje grafa (N={args.N}, E={args.E}, seed={args.seed}) ---")
        graph = generateGraph(sc, numNodes=args.N, numEdges=args.E, seed=args.seed)

    print(f"Ucitano/Generisano cvorova za analizu (uzorci):")
    for node in graph.take(5):
        print(node)

    if args.serialize_out:
        print(f"\n--- 2. Serijalizacija grafa ---")
        serialize(graph, args.serialize_out)
        print(f"Graf je uspesno serijalizovan u: {args.serialize_out}")

    print("\n--- 3. Pokretanje PageRank algoritma ---")
    finalRanks: RDD 
    
    if args.personalized:
        print(f"\n--- Pokretanje i poređenje Standardnog i Personalizovanog PageRank-a (S = {setS}) ---")
        standardRanks = pageRank(nodesRdd=graph, d=args.d, maxIter=args.maxIter, epsilon=args.epsilon, k=args.K)
        topKS = standardRanks.takeOrdered(args.K, key=lambda x: -x[1])

        finalRanks = pageRankPersonalized(nodesRdd=graph, seedSet=setS, d=args.d, maxIter=args.maxIter, epsilon=args.epsilon, k=args.K)
        topKP = finalRanks.takeOrdered(args.K, key=lambda x: -x[1])

        overlap = calculateOverlap(topKS, topKP)
        print(f"\n=== POREĐENJE REZULTATA ===")
        print(f"Top-{args.K} Standardni PageRank:    {[nid for nid, _ in topKS]}")
        print(f"Top-{args.K} Personalizovani PageRank: {[nid for nid, _ in topKP]}")
        print(f"Procenat preklapanja (Overlap): {overlap:.2f}%")

    elif args.optimized:
        print("\n--- Pokretanje optimizovanog PageRank algoritma ---")
        finalRanks = pageRankOptimized(nodesRdd=graph, d=args.d, maxIter=args.maxIter, epsilon=args.epsilon, k=args.K)
    else:
        print("\n--- Pokretanje standardnog PageRank algoritma ---")
        finalRanks = pageRank(nodesRdd=graph, d=args.d, maxIter=args.maxIter, epsilon=args.epsilon, k=args.K)

    print("\n--- Validacija rezultata ---")
    validate(graph=graph, ranks=finalRanks, tolerance=1e-4)

    print(f"\n--- 4. Čuvanje finalnih rangova ---")
    try:
        finalRanks.saveAsTextFile(args.ranks_out)
        print(f"Finalni rangovi su uspešno sačuvani u: {args.ranks_out}")
    except Exception as e:
        print(f"Greška ili direktorijum već postoji na putanji {args.ranks_out}. Detalji: {e}")

    sc.stop()

if __name__ == '__main__':
    main()