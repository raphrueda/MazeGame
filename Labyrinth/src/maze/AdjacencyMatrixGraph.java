package maze;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An adjacency matrix representation of a symmetric graph.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 * @param <E> The type of the graph nodes.
 */
public class AdjacencyMatrixGraph<E> implements Graph<E> {
   private boolean[][] g;
   private Map<E, Integer> nodeToIndex;
   private Map<Integer, E> indexToNode;

   /**
    * Initializes the graph.
    * @param size
    */
   public AdjacencyMatrixGraph(int size) {
      nodeToIndex = new HashMap<E, Integer>(size);
      indexToNode = new HashMap<Integer, E>(size);
      g = new boolean[size*size][size*size];
   }

   /*
    * (non-Javadoc)
    * @see Graph#addNode(java.lang.Object)
    */
   @Override
   public boolean addNode(E node) {
      if (nodeToIndex.containsKey(node)) {
         return false;
      } else {
         nodeToIndex.put(node, nodeToIndex.size());
         indexToNode.put(indexToNode.size(), node);
         return true;
      }
   }

   /*
    * (non-Javadoc)
    * @see Graph#addEdge(java.lang.Object, java.lang.Object)
    */
   @Override
   public boolean addEdge(E from, E to) {
      Integer fromIndex;
      Integer toIndex;
      if((fromIndex=nodeToIndex.get(from))!=null && (toIndex = nodeToIndex.get(to))!=null
            && g[fromIndex][toIndex]!=true){
         g[fromIndex][toIndex] = true;
         g[toIndex][fromIndex] = true;
         return true;
      }
      else{
         return false;
      }
   }

   /*
    * (non-Javadoc)
    * @see Graph#hasEdge(java.lang.Object, java.lang.Object)
    */
   @Override
   public boolean hasEdge(E from, E to) {
      Integer fromIndex;
      Integer toIndex;
      if((fromIndex=nodeToIndex.get(from))!=null && (toIndex = nodeToIndex.get(to))!=null){
         return g[fromIndex][toIndex];
      }
      else{
         return false;
      }
   }

   /*
    * (non-Javadoc)
    * @see Graph#getNodes()
    */
   @Override
   public List<E> getNodes() {
      return new ArrayList<E>(nodeToIndex.keySet());
   }

   /*
    * (non-Javadoc)
    * @see Graph#getNeighbours(java.lang.Object)
    */
   @Override
   public List<E> getNeighbours(E node) {
      ArrayList<E> neighbours = new ArrayList<E>();
      int row = nodeToIndex.get(node);
      for (int col = 0; col < nodeToIndex.size(); col++) {
         if (g[row][col]) {
            neighbours.add(indexToNode.get(col));
         }
      }
      return neighbours;
   }
}
