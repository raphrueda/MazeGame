package maze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The powerups available on the maze.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class Powerup{
   private Map<Room, String> powerups = new HashMap<Room, String>();
   private boolean enabled = true;
   
   /**
    * Randomly generates powerups for the maze.
    * @param mazeSize The size (width/height) of the maze.
    */
   public Powerup(int mazeSize){
      Random generator = new Random();
      int numPowerups = 0;
      
      // determine the number of powerups on the map
      int rand = generator.nextInt(99);
      if(mazeSize < 6){
         numPowerups = 1;
      }
      else if(mazeSize < 11){
         numPowerups = 2;
      }
      else if(mazeSize < 16){
         if(rand < 33){
            numPowerups = 3;
         }
         else if(rand < 66){
            numPowerups = 4;
         }
         else{
            numPowerups = 5;
         }
      }
      else if(mazeSize < 26){
         if(rand < 33){
            numPowerups = 8;
         }
         else if(rand < 66){
            numPowerups = 9;
         }
         else{
            numPowerups = 10;
         }
      }
      else{
         if(rand < 33){
            numPowerups = 15;
         }
         else if(rand < 66){
            numPowerups = 16;
         }
         else{
            numPowerups = 17;
         }
      }
      
      // determine the powerup types
      for(int i=0; i<numPowerups; i++){
         rand = generator.nextInt(100);
            
         String name = null;
         if(rand < 50){
            name = "fast";
         }
         else if(rand < 75){
            name = "slow";
         }
         else if(rand < 100){
            name = "opposite";
         }
         
         Room r;
         do{
            r = new Room(generator.nextInt(mazeSize), generator.nextInt(mazeSize));
         }while(powerups.containsKey(r) ||
               ((r.getRow()==0||r.getRow()==mazeSize-1) && r.getRow()==r.getCol()));
         powerups.put(r, name);
      }
   }
   
   /**
    * Returns the powerup at a room and removes it from the available powerups on the maze.
    * @param r The room.
    * @return The powerup at the room or null if there are none.
    */
   public String getPowerupAtRoom(Room r){
      String result = null;
      if(enabled && powerups.get(r) != null){
         result = powerups.remove(r);
      }
      return result;
   }
   
   /**
    * Returns a list of rooms with powerups on the maze.
    * @return A list of rooms with powerups on the maze.
    */
   public List<Room> getPowerupRooms(){
      return (enabled)? new ArrayList<Room>(powerups.keySet()):new ArrayList<Room>();
   }
   
   /**
    * Disables all powerups so that no further powerups can be taken.
    */
   public void disable(){
      enabled = false;
   }
}
