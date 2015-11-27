package maze;
import java.util.List;

/**
 * 
 * @author Adrian, Bec, Jeffrey, Raph
 *
 * @param <E> The type of the graph nodes.
 */
public interface Graph<E> {

	/**
	 * Adds node to the graph.
	 * @param node The node to be added.
	 * @return True if node was not already in graph.
	 */
	public boolean addNode(E node);

	/**
	 * Adds edge to the graph.
	 * @param from The starting node.
	 * @param to The ending node.
	 * @return True if both nodes are in the graph and there wasn't already an edge between them.
	 */
	public boolean addEdge(E from, E to);

	/**
	 * Checks for edge in graph.
	 * @param from The starting node.
	 * @param to The ending node.
	 * @return True if edge exists.
	 */
	public boolean hasEdge(E from, E to);
	
	/**
	 * Gets all nodes in the graph.
	 * @return All nodes in the graph in no defined order
	 */
	public List<E> getNodes();
	
	/**
	 * Gets all neighbours of the given node
	 * @param node The given node
	 * @return THe neighbours in no defined order.
	 */
 	public List<E> getNeighbours(E node);
}
