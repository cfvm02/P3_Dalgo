import java.io.*;
import java.util.*;

public class ProblemaP3 {
    // Node class representing each cell
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

    // Graph class using adjacency list
    static class Graph {
        Map<Integer, List<Integer>> adjacencyList;

        public Graph(int n) {
            adjacencyList = new HashMap<>();
            for (int i = 0; i < n; i++) {
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
    }

    public static void main(String[] args) throws IOException {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))
        ) {
            // Process each test case
            int testCases = Integer.parseInt(reader.readLine().trim());
            for (int t = 0; t < testCases; t++) {
                handleSingleTestCase(reader, writer);
            }
        }
    }

    private static void handleSingleTestCase(BufferedReader reader, BufferedWriter writer) throws IOException {
        String[] params = reader.readLine().trim().split(" ");
        int n = Integer.parseInt(params[0]); // Number of nodes
        int d = Integer.parseInt(params[1]); // Maximum distance
        int k = Math.max(2, (int) Math.sqrt(n)); // Dynamic k for Baker's technique

        List<Node> nodes = readNodes(reader, n);
        Graph graph = buildGraph(nodes, d);

        int[] layer = assignLayers(graph, n, k);
        int[] group = assignGroups(graph, n, layer, k);

        writeResults(writer, nodes, group);
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
        int n = nodes.size();
        Graph graph = new Graph(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (canConnect(nodes.get(i), nodes.get(j), d)) {
                    graph.addEdge(i, j);
                }
            }
        }
        return graph;
    }

    private static boolean canConnect(Node a, Node b, int d) {
        double distance = Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
        return distance <= d && !Collections.disjoint(a.peptides, b.peptides);
    }

    private static int[] assignLayers(Graph graph, int n, int k) {
        int[] layer = new int[n];
        Arrays.fill(layer, -1);

        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        layer[0] = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int neighbor : graph.getNeighbors(node)) {
                if (layer[neighbor] == -1) {
                    layer[neighbor] = (layer[node] + 1) % k;
                    queue.add(neighbor);
                }
            }
        }
        return layer;
    }

    private static int[] assignGroups(Graph graph, int n, int[] layer, int k) {
        int[] group = new int[n];
        Arrays.fill(group, -1);
        int groupId = 1;

        for (int l = 0; l < k; l++) {
            List<Integer> layerNodes = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (layer[i] == l) {
                    layerNodes.add(i);
                }
            }
            for (int node : layerNodes) {
                if (group[node] == -1) {
                    assignGroup(graph, group, node, groupId++);
                }
            }
        }
        return group;
    }

    private static void assignGroup(Graph graph, int[] group, int startNode, int groupId) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startNode);
        group[startNode] = groupId;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int neighbor : graph.getNeighbors(node)) {
                if (group[neighbor] == -1) {
                    group[neighbor] = groupId;
                    queue.add(neighbor);
                }
            }
        }
    }

    private static void writeResults(BufferedWriter writer, List<Node> nodes, int[] group) throws IOException {
        for (int i = 0; i < nodes.size(); i++) {
            writer.write(nodes.get(i).id + " " + group[i]);
            writer.newLine();
        }
        writer.flush();
    }
}
