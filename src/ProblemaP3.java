import java.io.*;
import java.util.*;

public class ProblemaP3 {
    static class Nodo {
        int id, x, y;
        Set<String> peptidos;

        public Nodo(int id, int x, int y, Set<String> peptidos) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.peptidos = peptidos;
        }
    }

    static class Grafo {
        Map<Integer, List<Integer>> listaAdyacencia;

        public Grafo(int n) {
            listaAdyacencia = new HashMap<>();
            for (int i = 1; i <= n; i++) {
                listaAdyacencia.put(i, new ArrayList<>());
            }
        }

        public void agregarArista(int u, int v) {
            listaAdyacencia.get(u).add(v);
            listaAdyacencia.get(v).add(u);
        }

        public List<Integer> obtenerVecinos(int u) {
            return listaAdyacencia.get(u);
        }

        public Set<int[]> obtenerAristas() {
            Set<int[]> aristas = new HashSet<>();
            for (int u : listaAdyacencia.keySet()) {
                for (int v : listaAdyacencia.get(u)) {
                    if (u < v) { // Asegurar que cada arista se agrega solo una vez
                        aristas.add(new int[]{u, v});
                    }
                }
            }
            return aristas;
        }
    }

    public static void main(String[] args) throws IOException {
        try (
            BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter escritor = new BufferedWriter(new OutputStreamWriter(System.out))
        ) {
            int casosPrueba = Integer.parseInt(lector.readLine().trim());
            for (int t = 0; t < casosPrueba; t++) {
                manejarCasoPrueba(lector, escritor);
            }
        }
    }

    private static void manejarCasoPrueba(BufferedReader lector, BufferedWriter escritor) throws IOException {
        String[] parametros = lector.readLine().trim().split(" ");
        int n = Integer.parseInt(parametros[0]);
        int d = Integer.parseInt(parametros[1]);

        List<Nodo> nodos = leerNodos(lector, n);
        Grafo grafo = construirGrafo(nodos, d);

        int[] asignacionGrupos = encontrarCubiertaCliqueMinima(grafo, n);

        // Imprimir resultados en el formato requerido
        for (int i = 1; i <= n; i++) {
            escritor.write(i + " " + asignacionGrupos[i]);
            escritor.newLine();
        }
        escritor.flush();
    }

    private static List<Nodo> leerNodos(BufferedReader lector, int n) throws IOException {
        List<Nodo> nodos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String[] datos = lector.readLine().trim().split(" ");
            int id = Integer.parseInt(datos[0]);
            int x = Integer.parseInt(datos[1]);
            int y = Integer.parseInt(datos[2]);
            Set<String> peptidos = new HashSet<>(Arrays.asList(datos).subList(3, datos.length));
            nodos.add(new Nodo(id, x, y, peptidos));
        }
        return nodos;
    }

    private static Grafo construirGrafo(List<Nodo> nodos, int d) {
        Grafo grafo = new Grafo(nodos.size());

        for (int i = 0; i < nodos.size(); i++) {
            for (int j = i + 1; j < nodos.size(); j++) {
                if (puedenConectar(nodos.get(i), nodos.get(j), d)) {
                    grafo.agregarArista(nodos.get(i).id, nodos.get(j).id);
                }
            }
        }
        return grafo;
    }

    private static boolean puedenConectar(Nodo a, Nodo b, int d) {
        // Posible BUG: Asegurar que 'd' no sea negativo
        double distancia = Math.hypot(a.x - b.x, a.y - b.y);
        boolean tienePeptidosComunes = !Collections.disjoint(a.peptidos, b.peptidos);
        return distancia <= d && tienePeptidosComunes;
    }

    private static int[] encontrarCubiertaCliqueMinima(Grafo grafo, int n) {
        int[] asignacionGrupos = new int[n + 1];
        Arrays.fill(asignacionGrupos, -1); // Inicializar todos los nodos como no asignados
        int grupoActual = 1;

        for (int nodo = 1; nodo <= n; nodo++) {
            if (asignacionGrupos[nodo] == -1) { // Si el nodo no está asignado aún
                Set<Integer> clique = encontrarClique(grafo, nodo, asignacionGrupos);
                for (int miembro : clique) {
                    asignacionGrupos[miembro] = grupoActual; // Asignar el grupo actual a todos los miembros de la clique
                }
                grupoActual++;
            }
        }
        return asignacionGrupos;
    }

    private static Set<Integer> encontrarClique(Grafo grafo, int nodoInicio, int[] asignacionGrupos) {
        Set<Integer> clique = new HashSet<>();
        Queue<Integer> cola = new LinkedList<>();
        cola.add(nodoInicio);

        while (!cola.isEmpty()) {
            int nodo = cola.poll();
            if (clique.contains(nodo)) continue; // Evitar ciclos
            clique.add(nodo);

            for (int vecino : grafo.obtenerVecinos(nodo)) {
                if (asignacionGrupos[vecino] == -1 && todosEnClique(grafo, vecino, clique)) {
                    cola.add(vecino);
                }
            }
        }
        return clique;
    }

    private static boolean todosEnClique(Grafo grafo, int nodo, Set<Integer> clique) {
        // Verificar si 'nodo' puede unirse a la clique
        for (int miembro : clique) {
            if (!grafo.obtenerVecinos(miembro).contains(nodo)) {
                return false;
            }
        }
        return true;
    }
}
