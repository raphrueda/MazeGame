package maze;

/**
 * A room in the maze.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class Room {
   private int row;
   private int col;
   
   /**
    * Creates a room at the specified location.
    * @param row The row of the room.
    * @param col The column of the room.
    */
   public Room(int row, int col){
      this.row = row;
      this.col = col;
   }
   
   /**
    * Returns the room's column.
    * @return The room's column.
    */
   public int getCol(){
      return col;
   }
   /**
    * Returns the room's row.
    * @return The room's row.
    */
   public int getRow(){
      return row;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString(){
      return "Room["+row+","+col+"]";
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object otherObject){
      Room r = (Room) otherObject;
      if(this == r) return true;
      if(r == null) return false;
      if(getClass() != r.getClass()) return false;
      
      if(row==r.row && col==r.col){
         return true;
      }
      return false;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode(){
      return 13*new Integer(row).hashCode() + 17*new Integer(col).hashCode();
   }
}
