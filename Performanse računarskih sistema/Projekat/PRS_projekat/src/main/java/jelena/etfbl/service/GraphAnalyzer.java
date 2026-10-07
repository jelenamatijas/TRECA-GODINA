package jelena.etfbl.service;

import jelena.etfbl.database.Table;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

public class GraphAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(GraphAnalyzer.class);
    private Map<String, Table> tables;

    public GraphAnalyzer(Map<String, Table> tables){
        this.tables = tables;
    }

    public List<List<String>> findSameLevels() {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, List<String>> adjList = new HashMap<>();

        for (String table : tables.keySet()) {
            inDegree.put(table, 0);
            adjList.put(table, new ArrayList<>());
        }

        for (Table t : tables.values()) {
            String trenutnaTabela = t.getTableName();
            for (String zavisnost : t.getDependencies()) {
                if (tables.containsKey(zavisnost) && !zavisnost.equals(trenutnaTabela)) {
                    adjList.get(zavisnost).add(trenutnaTabela);
                    inDegree.put(trenutnaTabela, inDegree.get(trenutnaTabela) + 1);
                }
            }
        }

        List<List<String>> levels = new ArrayList<>();
        Queue<String> red = new LinkedList<>();

        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                red.offer(entry.getKey());
            }
        }

        int obradjeno = 0;

        while (!red.isEmpty()) {
            int velicinaNivoa = red.size();
            List<String> trenutniNivo = new ArrayList<>();

            for (int i = 0; i < velicinaNivoa; i++) {
                String tabela = red.poll();
                trenutniNivo.add(tabela);
                obradjeno++;

                for (String komsija : adjList.get(tabela)) {
                    inDegree.put(komsija, inDegree.get(komsija) - 1);
                    if (inDegree.get(komsija) == 0) {
                        red.offer(komsija);
                    }
                }
            }
            levels.add(trenutniNivo);
        }

        if (obradjeno != tables.size()) {
            log.warn("Detektovan ciklus u bazi.");
            List<String> preostale = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
                if (entry.getValue() > 0) {
                    preostale.add(entry.getKey());
                }
            }
            if (!preostale.isEmpty()) {
                levels.add(preostale);
            }
        }

        return levels;
    }

    public String renderGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TABLE DEPENDENCY GRAPH ===\n\n");
        List<List<String>> levels = findSameLevels();
        for (int i = 0; i < levels.size(); i++) {
            sb.append(String.format("Level %d (parallel): %s\n", i, levels.get(i)));
        }
        return sb.toString();
    }
}