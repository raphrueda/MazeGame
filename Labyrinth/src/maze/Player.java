package maze;

/**
 * A player for the maze game.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class Player {
   private int row = 0;
   private int col = 0;
   private int prevRow = 0;
   private int prevCol = 0;
   private boolean animating = false;
   private String currentPowerup = null;

   /**
    * Returns the room the player is in.
    * @return The room the player is in.
    */
   public Room getRoom(){
      return new Room(row, col);
   }
   
   /**
    * Returns the row the player is in.
    * @return The row the player is in.
    */
   public int getRow() {
      return row;
   }
   /**
    * Returns the column the player is in.
    * @return The column the player is in.
    */
   public int getCol() {
      return col;
   }
   
   /**
    * Returns the row the player was most recently in.
    * @return The row the player was most recently in.
    */
   public int getPrevRow(){
      return prevRow;
   }
   /**
    * Returns the column the player was most recently in.
    * @return The column the player was most recently in.
    */
   public int getPrevCol(){
      return prevCol;
   }
   
   /**
    * Changes the players row relative to the current position.
    * @param n The amount to change the row by.
    */
   public void moveRow(int n) {
      prevRow = row;
      prevCol = col;
      row += n;
   }
   /**
    * Changes the players column relative to the current position.
    * @param n The amount to change the column by.
    */
   public void moveCol(int n) {
      prevRow = row;
      prevCol = col;
      col += n;
   }
   
   /**
    * Resets the player back to the starting room (0,0).
    */
   public void reset(){
      row = 0;
      col = 0;
      prevRow = 0;
      prevCol = 0;
   }

   /**
    * Returns true if the player is in the process of animating movement.
    * @return True if the player is in the process of animating movement.
    */
   public boolean isAnimating() {
      return animating;
   }
   /**
    * Sets the animating status.
    * @param animating The new animating status.
    */
   public void setAnimating(boolean animating) {
      this.animating = animating;
   }

   /**
    * Returns the player's current powerup.
    * @return The player's current powerup.
    */
   public String getCurrentPowerup() {
      return currentPowerup;
   }
   /**
    * Sets the player's current powerup.
    * @param currentPowerup The new current powerup.
    */
   public void setCurrentPowerup(String currentPowerup) {
      this.currentPowerup = currentPowerup;
   }
}
