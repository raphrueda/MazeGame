package maze;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A square maze represented in graph form.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class Maze {
   private Graph<Room> mazeGraph;
   private int size;
   private Room[][] rooms;
   private Map<Room, Room> solution;
   
   /**
    * Creates a square maze.
    * @param size The width/height of the square maze.
    */
   public Maze(int size){
      this.size = size;
      // graph of adjacent rooms
      Graph<Room> adjacentRoomsGraph = new AdjacencyMatrixGraph<Room>(size);
      // the actual maze graph
      mazeGraph = new AdjacencyMatrixGraph<Room>(size);
      rooms = new Room[size][size];
      solution = new HashMap<Room, Room>();
      
      // populate graphs with rooms
      for(int row=0; row<size; row++){
         for(int col=0; col<size; col++){
            rooms[row][col] = new Room(row, col);
            adjacentRoomsGraph.addNode(rooms[row][col]);
            mazeGraph.addNode(rooms[row][col]);
         }
      }
      
      // populate graph of adjacent rooms
      for(int row=0; row<size; row++){
         for(int col=0; col<size; col++){
            int rowMore = row+1;
            int colMore = col+1;
            if(rowMore<size){
               adjacentRoomsGraph.addEdge(rooms[row][col], rooms[rowMore][col]);
            }
            if(colMore<size){
               adjacentRoomsGraph.addEdge(rooms[row][col], rooms[row][colMore]);
            }
         }
      }
      
      generateRB(adjacentRoomsGraph);
   }
   
   
   /**
    * Generates a maze (in graph form) using the recursive backtracker algorithm.
    * @param adjacentRoomsGraph A graph where adjacent rooms are connected.
    */
   private void generateRB(Graph<Room> adjacentRoomsGraph){
      LinkedList<Room> unvisited = new LinkedList<Room>(mazeGraph.getNodes());
      LinkedList<Room> visited = new LinkedList<Room>();
      Room current = rooms[size-1][size-1];
      unvisited.remove(rooms[size-1][size-1]);
      visited.add(current);
      LinkedList<Room> stack = new LinkedList<Room>();
      while(!unvisited.isEmpty()){
         List<Room> neighbours = adjacentRoomsGraph.getNeighbours(current);
         neighbours.removeAll(visited);
         Collections.shuffle(neighbours);
         if(!neighbours.isEmpty()){
            Room chosen = neighbours.get(0);
            mazeGraph.addEdge(current, chosen);
            solution.put(chosen, current);
            stack.push(current);
            current = chosen;
            visited.add(current);
            unvisited.remove(current);
         }
         else if(!stack.isEmpty()){
            current = stack.pop();
         }
         else{
            current = unvisited.pop();
            visited.add(current);
         }
      }
   }
   
   /**
    * Returns the size (width/height) of the square maze.
    * @return The size (width/height) of the square maze.
    */
   public int getSize(){
      return size;
   }
   
   /**
    * Returns true if two rooms are connected.
    * @param a The first room.
    * @param b The second room.
    * @return true if the two rooms are connected.
    */
   public boolean isConnected(Room a, Room b){
      return (mazeGraph.hasEdge(a, b));
   }
   
   /**
    * Returns a list of rooms that lead to the end room.
    * @param current The current room.
    * @return A list of five rooms that lead from the current room to the end room.
    */
   public List<Room> getHint(Room current){
      List<Room> result = new LinkedList<Room>();
      for(int i=0; i<5; i++){
         current = solution.get(current);
         result.add(current);
      }
      return result;
   }
}