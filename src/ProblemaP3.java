import java.io.*;
import java.util.*;

public class ProblemaP3 {
    static class Node {
        int id, x, y;
        Set<String> peptides;

        public Node(int id, int x, int y, Set<String> peptides) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.peptides = peptides;
        }
    }

    static class Graph {
        Map<Integer, List<Integer>> adjacencyList;

        public Graph(int n) {
            adjacencyList = new HashMap<>();
            for (int i = 1; i <= n; i++) {
                adjacencyList.put(i, new ArrayList<>());
            }
        }

        public void addEdge(int u, int v) {
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }

        public List<Integer> getNeighbors(int u) {
            return adjacencyList.get(u);
        }

        public Set<int[]> getEdges() {
            Set<int[]> edges = new HashSet<>();
            for (int u : adjacencyList.keySet()) {
                for (int v : adjacencyList.get(u)) {
                    if (u < v) { // Ensure each edge is added only once
                        edges.add(new int[]{u, v});
                    }
                }
            }
            return edges;
        }
    }

    public static void main(String[] args) throws IOException {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))
        ) {
            int testCases = Integer.parseInt(reader.readLine().trim());
            for (int t = 0; t < testCases; t++) {
                handleSingleTestCase(reader, writer);
            }
        }
    }

    private static void handleSingleTestCase(BufferedReader reader, BufferedWriter writer) throws IOException {
        String[] params = reader.readLine().trim().split(" ");
        int n = Integer.parseInt(params[0]);
        int d = Integer.parseInt(params[1]);

        List<Node> nodes = readNodes(reader, n);
        Graph graph = buildGraph(nodes, d);

        int[] groupAssignment = findMinimumCliqueCover(graph, n);

        // Print results in the required format
        for (int i = 1; i <= n; i++) {
            writer.write(i + " " + groupAssignment[i]);
            writer.newLine();
        }
        writer.flush();
    }

    private static List<Node> readNodes(BufferedReader reader, int n) throws IOException {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String[] data = reader.readLine().trim().split(" ");
            int id = Integer.parseInt(data[0]);
            int x = Integer.parseInt(data[1]);
            int y = Integer.parseInt(data[2]);
            Set<String> peptides = new HashSet<>(Arrays.asList(data).subList(3, data.length));
            nodes.add(new Node(id, x, y, peptides));
        }
        return nodes;
    }

    private static Graph buildGraph(List<Node> nodes, int d) {
        Graph graph = new Graph(nodes.size());

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                if (canConnect(nodes.get(i), nodes.get(j), d)) {
                    graph.addEdge(nodes.get(i).id, nodes.get(j).id);
                }
            }
        }
        return graph;
    }

    private static boolean canConnect(Node a, Node b, int d) {
        double distance = Math.hypot(a.x - b.x, a.y - b.y);
        boolean hasSharedPeptides = !Collections.disjoint(a.peptides, b.peptides);
        return distance <= d && hasSharedPeptides;
    }

    private static int[] findMinimumCliqueCover(Graph graph, int n) {
        int[] groupAssignment = new int[n + 1];
        Arrays.fill(groupAssignment, -1);
        int currentGroup = 1;

        for (int node = 1; node <= n; node++) {
            if (groupAssignment[node] == -1) { // If the node is not yet assigned
                Set<Integer> clique = findClique(graph, node, groupAssignment);
                for (int member : clique) {
                    groupAssignment[member] = currentGroup; // Assign the current group to all members of the clique
                }
                currentGroup++;
            }
        }
        return groupAssignment;
    }

    private static Set<Integer> findClique(Graph graph, int startNode, int[] groupAssignment) {
        Set<Integer> clique = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startNode);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            if (clique.contains(node)) continue;
            clique.add(node);

            for (int neighbor : graph.getNeighbors(node)) {
                if (groupAssignment[neighbor] == -1 && allInClique(graph, neighbor, clique)) {
                    queue.add(neighbor);
                }
            }
        }
        return clique;
    }

    private static boolean allInClique(Graph graph, int node, Set<Integer> clique) {
        for (int member : clique) {
            if (!graph.getNeighbors(member).contains(node)) {
                return false;
            }
        }
        return true;
    }
}
