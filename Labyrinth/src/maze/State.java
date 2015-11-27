package maze;

import java.util.List;

/**
 * The state of the maze game.
 * This is simply a data container that is passed from the model to the views.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class State {
   private Player player1;
   private Player player2;
   private Maze maze;
   private List<Room> hintPath;
   private String lastChange;
   private boolean win;
   private Room endRoom;
   private boolean multiplayer;
   private Powerup powerups;
   private int score;
   
   /**
    * Returns player 1.
    * @return Player 1.
    */
   public Player getPlayer1() {
      return player1;
   }
   /**
    * Sets player 1.
    * @param p1 Player 1.
    */
   public void setPlayer1(Player p1) {
      this.player1 = p1;
   }
   
   /**
    * Returns player 2.
    * @return Player 2.
    */
   public Player getPlayer2() {
      return player2;
   }
   /**
    * Sets player 2.
    * @param p2 Player 2.
    */
   public void setPlayer2(Player player2) {
      this.player2 = player2;
   }
   
   /**
    * Returns the maze.
    * @return The maze.
    */
   public Maze getMaze() {
      return maze;
   }
   /**
    * Sets the maze.
    * @param maze The maze.
    */
   public void setMaze(Maze maze) {
      this.maze = maze;
   }
   
   /**
    * Returns a list of five rooms that lead to the end room.
    * @return A list of five rooms that lead to the end room.
    */
   public List<Room> getHintPath() {
      return hintPath;
   }
   /**
    * Sets the hint path.
    * @param hintPath The new hint path.
    */
   public void setHintPath(List<Room> hintPath) {
      this.hintPath = hintPath;
   }
   
   /**
    * Returns the last change to the game state.
    * @return The last change to the game state.
    */
   public String getLastChange() {
      return lastChange;
   }
   /**
    * Sets the last change to the game state.
    * @param lastChange The last change.
    */
   public void setLastChange(String lastChange) {
      this.lastChange = lastChange;
   }
   
   /**
    * Returns if the game has been won.
    * @return True if the game has been won.
    */
   public boolean isWin() {
      return win;
   }
   /**
    * Sets the game win status.
    * @param win The new game win status.
    */
   public void setWin(boolean win) {
      this.win = win;
   }
   
   /**
    * Returns the game end room.
    * @return The game end room.
    */
   public Room getEndRoom() {
      return endRoom;
   }
   /**
    * Sets the game end room.
    * @param endRoom The new end room.
    */
   public void setEndRoom(Room endRoom) {
      this.endRoom = endRoom;
   }
   
   /**
    * Returns the current score/timer.
    * @return The current score/timer.
    */
   public int getScore() {
      return score;
   }
   /**
    * Sets the current score/timer.
    * @param score The new score/timer.
    */
   public void setScore(int score) {
      this.score = score;
   }
   
   /**
    * Returns if the multiplayer mode is enabled.
    * @return True if multiplayer mode is enabled.
    */
   public boolean isMultiplayer() {
      return multiplayer;
   }
   /**
    * Sets the mode to either singleplayer or multiplayer.
    * @param multiplayer True if multiplayer, false if singleplayer.
    */
   public void setMultiplayer(boolean multiplayer) {
      this.multiplayer = multiplayer;
   }
   
   /**
    * Returns the powerups for the game.
    * @return The powerups for the game.
    */
   public Powerup getPowerups() {
      return powerups;
   }
   /**
    * Sets the powerups for the game.
    * @param powerups The new powerups.
    */
   public void setPowerups(Powerup powerups) {
      this.powerups = powerups;
   }
}
