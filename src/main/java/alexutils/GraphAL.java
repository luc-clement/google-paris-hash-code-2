package alexutils;

import java.util.LinkedList;
import java.util.Stack;

// graph modeled with adjacency lists
public class GraphAL {
	
	private class EdgeNode {
		
		public int destination;
		public int length;
		public EdgeNode next;
		
		public EdgeNode(int dest, int l){
			destination = dest;
			length = l;
			next = null;
		}
	}
	
	private EdgeNode[] edges; // index <=> vertex
	private boolean directed;
	private boolean weighted;
	private int nVertices;
	
	public GraphAL(int nVertices, boolean directed, boolean weighted){ // empty graph constructor (no edges)
		edges = new EdgeNode[nVertices];
		this.nVertices = nVertices;
		this.directed = directed;
		this.weighted = weighted;
	}
	
	public int getEdge(int i, int j){ // -1 if not found, else length of the edge
		if(i >= nVertices || j >= nVertices)
			return -1;
		EdgeNode edge = edges[i];
		while(edge != null){
			if(edge.destination == j)
				return edge.length;
			edge = edge.next;
		}
		return -1;
	}
	
	public void insertEdge(int i, int j){
		insertEdge(i,j,1);
	}
	
	public void insertEdge(int i, int j, int length){ // if directed == false, insert j,i too
		if(!weighted)
			length = 1;
		if(!directed && i != j)
			_insertEdge(j,i,length);
		_insertEdge(i,j,length);
	}
	
	private void _insertEdge(int i, int j, int length){ // weighted & directed
		EdgeNode edge = new EdgeNode(j,length);
		EdgeNode parent = edges[i];
		if(parent == null){
			edges[i] = edge;
			return;
		}
		while(parent.next != null)
			parent = parent.next;
		parent.next = edge;
	}
	
	public void deleteEdge(int i, int j){
		if(!directed && i != j)
			_deleteEdge(j,i);
		_deleteEdge(i,j);
	}
	
	private void _deleteEdge(int i, int j){
		if(i >= nVertices || j >= nVertices)
			return;
		EdgeNode parent = edges[i];
		if(parent == null)
			return;
		if(parent.destination == j){
			edges[i] = parent.next;
			return;
		}
		EdgeNode edge = parent.next;
		while(edge != null){
			if(edge.destination == j){
				parent.next = edge.next;
				return;
			}
			parent = edge;
			edge = edge.next;
		}
	}
	
	private int findClosestUnvisitedNode(int[] dist, boolean[] visited){
		int minDist = Integer.MAX_VALUE;
		int index = -1;
		for(int i = 0; i < nVertices; ++i){
			if(!visited[i] && dist[i] < minDist){
				index = i;
				minDist = dist[i];
			}
		}
		return index;
	}
	
	private LinkedList<Integer> buildPath(int[] previous, int start, int end){
		LinkedList<Integer> path = new LinkedList<Integer>();
		int currNode = end;
		while(previous[currNode] != -1){
			path.push(currNode);
			currNode = previous[currNode];
		}
		return path;
	}
	
	public LinkedList<Integer> dijkstra(int start, int end){
		if(start >= nVertices || start < 0 || end >= nVertices || end < 0)
			return new LinkedList<Integer>();
		int[] distance = new int[nVertices];
		int[] previous = new int[nVertices]; // previous node in optimal path
		boolean[] visited = new boolean[nVertices];
		for(int i = 0; i < nVertices; ++i){
			distance[i] = Integer.MAX_VALUE;
			previous[i] = -1;
			visited[i] = false;
		}
		distance[start] = 0;
		int closest;
		int nVisited = 0;
		EdgeNode neighbor;
		while(nVisited < nVertices){
			closest = findClosestUnvisitedNode(distance, visited);
			if(closest == -1) // ie all unvisited nodes are unreachable
				return new LinkedList<Integer>();
			visited[closest] = true;
			++nVisited;
			if(closest == end){
				return buildPath(previous, start, end);
			}
			neighbor = edges[closest];
			while(neighbor != null){
				if(distance[closest] + neighbor.length < distance[neighbor.destination]){
					distance[neighbor.destination] = distance[closest] + neighbor.length;
					previous[neighbor.destination] = closest;
				}
				neighbor = neighbor.next;
			}
			
		}
		return new LinkedList<Integer>();
	}
	
	public boolean dfs(int start, int end){ // return true if end is reachable from start
		Stack<Integer> nodesToVisit = new Stack<Integer>();
		boolean[] visitedNodes = new boolean[nVertices];
		for(int i = 0; i < nVertices; ++i)
			visitedNodes[i] = false;
		if(start < nVertices)
			nodesToVisit.push(start);
		else
			return false;
		int node;
		EdgeNode neighbor;
		while(!nodesToVisit.empty()){
			node = nodesToVisit.pop();
			if(node == end)
				return true;
			visitedNodes[node] = true;
			neighbor = edges[node];
			while(neighbor != null){
				if(!visitedNodes[neighbor.destination])
					nodesToVisit.push(neighbor.destination);
				neighbor = neighbor.next;
			}
		}
		return false;
	}
	
	public boolean bfs(int start, int end){
		LinkedList<Integer> nodesToVisit = new LinkedList<Integer>();
		boolean[] visitedNodes = new boolean[nVertices];
		for(int i = 0; i < nVertices; ++i)
			visitedNodes[i] = false;
		if(start < nVertices)
			nodesToVisit.add(start);
		else
			return false;
		int node;
		EdgeNode neighbor;
		while(nodesToVisit.size() != 0){
			node = nodesToVisit.pop();
			if(node == end)
				return true;
			visitedNodes[node] = true;
			neighbor = edges[node];
			while(neighbor != null){
				if(!visitedNodes[neighbor.destination])
					nodesToVisit.add(neighbor.destination);
				neighbor = neighbor.next;
			}
		}
		return false;
	}
	
	public String toString(){
		String res = "";
		EdgeNode edge;
		for(int i = 0; i < nVertices; ++i){
			res += i + " : [ ";
			edge = edges[i];
			while(edge != null){
				//if(!directed && edge.destination >= i || directed){
					res += edge.destination;
					if(weighted)
						res += " (" + edge.length + ")";
					res += " ";
				//}
				edge = edge.next;
			}
			res += "]\n";
		}
		return res;
	}
	
	public static GraphAL createRandomGraph(int size, boolean directed, double p){ // unweigthed
		return createRandomGraph(size, directed, p, 1);
	}
	
	public static GraphAL createRandomGraph(int size, boolean directed, double p, int maxWeight){ // minWeight = 1
		GraphAL graph;
		if(maxWeight == 1)
			graph = new GraphAL(size, directed, false);
		else
			graph = new GraphAL(size, directed, true);
		for(int i = 0; i < size; ++i){
			for(int j = directed ? 0 : i; j < size; ++j){
				if(Math.random() < p){
					if(maxWeight == 1)
						graph.insertEdge(i, j);
					else
						graph.insertEdge(i, j, 1 + (int) Math.floor((double) maxWeight * Math.random()));
				}
			}
		}
		return graph;
	}
}
