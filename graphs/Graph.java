package graphs;

import java.util.*;

/**
 * Implements a graph. We use two maps: one map for adjacency properties
 * (adjancencyMap) and one map (dataMap) to keep track of the data associated
 * with a vertex.
 * @param <E>
 */
public class Graph<E> {
	/* You must use the following maps in your implementation */
	private String vertexName;
	private HashMap<String, HashMap<String, Integer>> adjacencyMap;
	private HashMap<String, E> dataMap;

	public Graph() {
		adjacencyMap = new HashMap<>();
		dataMap = new HashMap<>();
	}

	public void addVertex(String vertexName, E data) {
		if (adjacencyMap.containsKey(vertexName)) {
			throw new IllegalArgumentException(
					"Vertex already exists in the graph.");
		}

		this.vertexName = vertexName;
		adjacencyMap.put(vertexName, new HashMap<>());
		dataMap.put(vertexName, data);

	}

	public void addDirectedEdge(String startVertexName, String endVertexName,
			int cost) {
		if (!adjacencyMap.containsKey(startVertexName)
				|| !adjacencyMap.containsKey(endVertexName)) {
			throw new IllegalArgumentException(
					"One or both vertices are not part of the graph.");
		}

		adjacencyMap.get(startVertexName).put(endVertexName, cost);
	}

	@Override
	public String toString() {
		StringBuffer vertices = new StringBuffer();
		StringBuffer edges = new StringBuffer();

		TreeSet<String> sortedVertices = new TreeSet<>(this.dataMap.keySet());

		vertices.append("Vertices: ").append(sortedVertices)
				.append("\n");
		edges.append("Edges:\n");

		for (String vertex : sortedVertices) {
			if (this.adjacencyMap.containsKey(vertex)) {
				edges.append("Vertex(").append(vertex)
						.append(")--->").append(this.adjacencyMap.get(vertex))
						.append("\n");
			}
		}

		return vertices.toString()
				+ edges.toString().trim();
	}


	public Map<String, Integer> getAdjacentVertices(String vertexName) {
		if (!adjacencyMap.containsKey(vertexName)) {
			throw new IllegalArgumentException(
					"Vertex is not part of the graph.");
		}

		return new HashMap<>(
				adjacencyMap.getOrDefault(vertexName, new HashMap<>()));
	}

	public int getCost(String startVertexName, String endVertexName) {
		if (!adjacencyMap.containsKey(startVertexName)
				|| !adjacencyMap.containsKey(endVertexName)) {
			throw new IllegalArgumentException(
					"vertices are not part of the graph.");
		}

		return adjacencyMap.get(startVertexName).getOrDefault(endVertexName,
				-1);
	}

	public Set<String> getVertices() {
		return new TreeSet<>(adjacencyMap.keySet());
	}

	public E getData(String vertex) {
		if (!adjacencyMap.containsKey(vertex)) {
			throw new IllegalArgumentException(
					"Vertex is not part of the graph.");
		}

		return dataMap.get(vertex);
	}

	public void doDepthFirstSearch(String startVertexName,
			CallBack<E> callback) {

		if (!this.dataMap.containsKey(startVertexName))
			throw new IllegalArgumentException();
		Stack<String> stack = new Stack<String>();
		HashSet<String> visited = new HashSet<String>();

		stack.push(startVertexName);
		while (!stack.isEmpty()) {
			String vertex = stack.pop();
			if (!visited.contains(vertex)) {
				callback.processVertex(vertex, this.dataMap.get(vertex));
				for (String s : this.adjacencyMap.get(vertex).keySet()) {
					stack.push(s);
				}
				visited.add(vertex);
			}
		}
	}

	public void doBreadthFirstSearch(String startVertexName,
			CallBack<E> callback) {
		if (!adjacencyMap.containsKey(startVertexName)) {
			throw new IllegalArgumentException(
					"Start vertex is not part of the graph.");
		}

		Set<String> visited = new HashSet<>();
		Queue<String> queue = new LinkedList<>();
		queue.add(startVertexName);

		while (!queue.isEmpty()) {
			String vertex = queue.poll();
			if (!visited.contains(vertex)) {
				visited.add(vertex);
				callback.processVertex(vertex, dataMap.get(vertex));
				for (String s : adjacencyMap
						.getOrDefault(vertex, new HashMap<>()).keySet()) {
					queue.add(s);
				}
			}
		}
	}

	public int doDijkstras(String startVertexName, String endVertexName,
			ArrayList<String> shortestPath) {
		if (!adjacencyMap.containsKey(startVertexName)
				|| !adjacencyMap.containsKey(endVertexName)) {
			throw new IllegalArgumentException(
					"One or both vertices are not part of the graph.");
		}

		PriorityQueue<Node> pq = new PriorityQueue<>(
				Comparator.comparingInt(node -> node.cost));
		HashMap<String, Integer> dist = new HashMap<>();
		HashMap<String, String> prev = new HashMap<>();

		for (String vertex : adjacencyMap.keySet()) {
			if (vertex.equals(startVertexName)) {
				dist.put(vertex, 0);
			} else {
				dist.put(vertex, Integer.MAX_VALUE);
			}
			prev.put(vertex, null);
			pq.add(new Node(vertex, dist.get(vertex)));
		}

		while (!pq.isEmpty()) {
			Node minNode = pq.poll();
			String u = minNode.vertex;
			int uDist = dist.get(u);
			if (uDist == Integer.MAX_VALUE)
				break;

			for (String v : adjacencyMap.get(u).keySet()) {
				int alt = uDist + adjacencyMap.get(u).get(v);
				if (alt < dist.get(v)) {
					dist.put(v, alt);
					prev.put(v, u);
					pq.add(new Node(v, alt));
				}
			}
		}

		int shortestCost = dist.get(endVertexName);
		if (shortestCost == Integer.MAX_VALUE) {
			shortestPath.add("None");
			return -1;
		}

		String vertex = endVertexName;
		while (vertex != null) {
			shortestPath.add(0, vertex);
			vertex = prev.get(vertex);
		}

		return shortestCost;
	}

	private class Node {
		String vertex;
		int cost;

		Node(String vertex, int cost) {
			this.vertex = vertex;
			this.cost = cost;
		}
	}

}