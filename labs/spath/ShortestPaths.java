//
// SHORTESTPATHS.JAVA
// Compute shortest paths in a weighted, directed graph.
//

package spath;

import java.util.LinkedList;
import java.util.HashMap;

// heap-related structures from Lab 3
import heaps.Decreaser;
import heaps.MinHeap;

// directed graph structure
import spath.graphs.DirectedGraph;
import spath.graphs.Edge;
import spath.graphs.Vertex;

// vertex/dist pair for priority queue
import spath.VertexAndDist;

import timing.Ticker;


public class ShortestPaths {

    // "infinity" value for path lengths
    private final static Integer inf = Integer.MAX_VALUE;
    
    // a directed graph, and a weighting function on its edges
    private final DirectedGraph g;
    private HashMap<Edge, Integer> weights;	
    
    // starting vertex for shortest path computation
    private Vertex startVertex;
    
    // map from vertices to their handles into the priority queue
    private HashMap<Vertex, Decreaser<VertexAndDist>> handles;
    
    // map from vertices to their parent edges in the shortest-path tree
    private HashMap<Vertex, Edge> parentEdges;
    
    //
    // constructor
    //
    public ShortestPaths(DirectedGraph g, HashMap<Edge,Integer> weights, 
			 Vertex startVertex) {
    	this.g           = g;
    	this.weights     = weights;

    	this.startVertex = startVertex;	
	
    	this.handles     = new HashMap<Vertex, Decreaser<VertexAndDist>>();
    	this.parentEdges = new HashMap<Vertex, Edge>();
    }

    
    //
    // run() 
    //
    // Given a weighted digraph stored in g/weights, compute a
    // shortest-path tree of parent edges back to a given starting
    // vertex.
    //
    public void run() {
    	Ticker ticker = new Ticker(); // heap requires a ticker
	
    	MinHeap<VertexAndDist> pq = 
    			new MinHeap<VertexAndDist>(g.getNumVertices(), ticker);
	
    	//
    	// Put all vertices into the heap, infinitely far from start.
    	// Record handle to each inserted vertex, and initialize
    	// parent edge of each to null (since we have as yet found 
    	// no path to it.)
    	//
    	for (Vertex v : g.vertices()) {
    		Decreaser<VertexAndDist> d = pq.insert(new VertexAndDist(v, inf));
    		handles.put(v, d);
    		parentEdges.put(v, null);
    	}
	
    	//
    	// Relax the starting vertex's distance to 0.
    	//   - get the handle to the vertex from the heap
    	//   - extract the vertex + distance object from the handle
    	//   - create a *new* vertex + distance object with a reduced 
    	//      distance
    	//   - update the heap through the vertex's handle
    	//
    	Decreaser<VertexAndDist> startHandle = handles.get(startVertex);
    	VertexAndDist vd = startHandle.getValue();
    	startHandle.decrease(new VertexAndDist(vd.vertex,0));
	
    	//
    	// OK, now it's up to you!
    	// Implement the main loop of Dijkstra's shortest-path algorithm,
    	// recording the parent edges of each vertex in parentEdges.
    	// FIXME
    	//
    	//	if the heap is not empty
		while(!pq.isEmpty()){
			//	extract the min from the heap
			VertexAndDist min = pq.extractMin();
			//	get the original shortest distance
			int original = min.getDistance() ; 
			//	iterate all the outgoing edges
			for (Edge e : min.getVertex().edgesFrom()) {
				//	get the weight of the edge
				int weight = this.weights.get(e);
				//	use handlesMap to get the vertex that the edge is pointing to
				Decreaser<VertexAndDist> bDist = handles.get(e.to);
				//	if the vertex.value.distance is greater than the original + weight
				//	update it by decrease method
				//	put it to the parentEdgesMap for tracking the shortest Path
				if ((original + weight) < bDist.getValue().getDistance()){
					bDist.decrease(bDist.getValue().newDistance(original+weight));
					parentEdges.put(e.to, e);
				}
			}
		}
    }
   
    //
    // returnPath()
    //
    // Given an ending vertex v, compute a linked list containing every
    // edge on a shortest path from the starting vertex (stored) to v.
    // The edges should be ordered starting from the start vertex.
    //
    public LinkedList<Edge> returnPath(Vertex endVertex) {
    	LinkedList<Edge> path = new LinkedList<Edge>();
	
    	//
    	// FIXME: implement this using the parent edges computed in run()
    	//
    	//	duplicate the endVertex since we're going to keep updating the endpoint
    	Vertex copy = endVertex;
    	//	get the distance from our handlesMap, 
    	//	Get the value, in this case Decreaser<VertexAndDist>, of given key, in this case, vertex
    	//	if the value of the distance of the Decreaser is greater than 0
    	// 	means we're not done, we need to keep finding the previous vertex
		while((handles.get(copy).getValue().getDistance()) > 0){
			//	using the key, in this case copy, to get the edge from parentEdges
			path.addFirst(parentEdges.get(copy));
			//	update the copy to the edge's parent(from)
			copy = parentEdges.get(copy).from;
		}
    	return path;
    }
    
    ////////////////////////////////////////////////////////////////
    
    //
    // returnLength()
    // Compute the total weight of a putative shortest path
    // from the start vertex to the specified end vertex.
    // No user-serviceable parts inside.
    //
    public int returnLength(Vertex endVertex) {
    	LinkedList<Edge> path = returnPath(endVertex);
    	int pathLength = 0;
    	for(Edge e : path) {
    		pathLength += weights.get(e);
    	}
    	return pathLength;
    }
}
