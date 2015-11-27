package maze;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import javax.swing.Timer;

/**
 * The model of the maze.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class Model extends Observable{
   private State state = new State();
   
   // show the hint for 3 seconds
   private Timer hintTimer = new Timer(3000, new ActionListener(){
      public void actionPerformed(ActionEvent event){
         state.getHintPath().clear();
         hintTimer.stop();
         state.setLastChange("hintOff");
         modelChanged();
      }
   });
   
   // update the score/timer every second
   private Timer scoreTimer = new Timer(1000, new ActionListener(){
      public void actionPerformed(ActionEvent event) {
         state.setScore(state.getScore()-1);
         if(state.getScore() == 0){
            scoreTimer.stop();
            state.getPlayer1().setCurrentPowerup(null);
            state.getPlayer2().setCurrentPowerup(null);
            state.getPowerups().disable();
            state.setLastChange("gameOver");
         }
         else{
            state.setLastChange("scoreTimer");
         }
         modelChanged();
      }
   });
   
   // powerups last for 5 seconds
   private Timer powerupTimerPlayer1 = new Timer(5000, new ActionListener(){
      public void actionPerformed(ActionEvent event){
         state.getPlayer1().setCurrentPowerup(null);
         powerupTimerPlayer1.stop();
         state.setLastChange("powerupOff");
         modelChanged();
      }
   });
   
   private Timer powerupTimerPlayer2 = new Timer(5000, new ActionListener(){
      public void actionPerformed(ActionEvent event){
         state.getPlayer2().setCurrentPowerup(null);
         powerupTimerPlayer2.stop();
         state.setLastChange("powerupOff");
         modelChanged();
      }
   });
   
   private int mazeSize;
   private int initialScore;
   
   /**
    * Moves the player around the maze.
    * @param direction The direction to move the player, suffixed with the player number.
    */
   public void movePlayer(String direction){
      // figure out which player is moving
      Player player = state.getPlayer1();
      String playerNumber = "1";
      if(state.isMultiplayer() && direction.endsWith("2")){
         player = state.getPlayer2();
         playerNumber = direction.substring(direction.length()-1);
         if(direction.startsWith("Left")){
            direction = direction.replace("Left", "Right");
         }
         else if(direction.startsWith("Right")){
            direction = direction.replace("Right", "Left");
         }
      }
      direction = direction.substring(0, direction.length()-1);
      
      // player can't move if they are currently animating a previous move
      if(player.isAnimating()){
         return;
      }
      
      Maze maze = state.getMaze();
      Room playerCurRoom = player.getRoom();
      
      // check for opposite powerup
      int opposite = ("opposite".equals(player.getCurrentPowerup()))? -1:1;
      
      if(direction.equals("Up")){
         if(maze.isConnected(playerCurRoom, new Room(player.getRow()-1*opposite, player.getCol()))){
            player.moveRow(-1*opposite);
         }
         else{
            return;
         }
      }
      else if(direction.equals("Left")){
         if(maze.isConnected(playerCurRoom, new Room(player.getRow(), player.getCol()-1*opposite))){
            player.moveCol(-1*opposite);
         }
         else{
            return;
         }
      }
      else if(direction.equals("Down")){
         if(maze.isConnected(playerCurRoom, new Room(player.getRow()+1*opposite, player.getCol()))){
            player.moveRow(1*opposite);
         }
         else{
            return;
         }
      }
      else if(direction.equals("Right")){
         if(maze.isConnected(playerCurRoom, new Room(player.getRow(), player.getCol()+1*opposite))){
            player.moveCol(1*opposite);
         }
         else{
            return;
         }
      }
      player.setAnimating(true);
      state.setLastChange("move"+playerNumber);
      modelChanged();
   }
   
   /**
    * Update state as necessary upon completion of animation.
    * @param playerNumber The player number that has finished animating.
    */
   public void completeMovePlayer(int playerNumber){
      List<Room> hintPath = state.getHintPath();
      Player player = state.getPlayer1();
      if(playerNumber == 2){
         player = state.getPlayer2();
      }
      Room playerCurRoom = player.getRoom();
      player.setAnimating(false);
      
      // clear the hint path once they've reached the end of the hint
      if(!hintPath.isEmpty() && playerCurRoom.equals(hintPath.get(hintPath.size()-1))){
         hintPath.clear();
         state.setLastChange("hintOff");
      }
      
      // set player's powerup if they get one
      String powerup = state.getPowerups().getPowerupAtRoom(playerCurRoom);
      if(powerup != null){
         state.setLastChange("powerupOn");
         player.setCurrentPowerup(powerup);
         if(playerNumber == 1){
            powerupTimerPlayer1.restart();
         }
         else{
            powerupTimerPlayer2.restart();
         }
      }
      
      // if someone has won
      if(playerCurRoom.equals(state.getEndRoom()) && state.getScore()!=0 && !state.isWin()){
         state.setWin(true);
         // single player
         if(!state.isMultiplayer()){
            state.setLastChange("win");
         }
         // multiplayer
         else{
            if(playerNumber == 1){
               state.setLastChange("winP1");
            }
            else{
               state.setLastChange("winP2");
            }
         }
         scoreTimer.stop();
         state.getPowerups().disable();
         state.getPlayer1().setCurrentPowerup(null);
         state.getPlayer2().setCurrentPowerup(null);
      }
      
      if(state.getLastChange() != null){
         modelChanged();
      }
   }
   
   /**
    * Shows a hint.
    */
   public void displayHint(){
      // no hints in multiplayer
      if(state.isMultiplayer()){
         return;
      }
      if(!state.isWin()){
         state.setScore((int) Math.round(state.getScore()*0.8));
      }
      state.setHintPath(state.getMaze().getHint(state.getPlayer1().getRoom()));
      state.setLastChange("hintOn");
      hintTimer.restart();
      modelChanged();
   }
   
   /**
    * Sets the mode as either singleplayer or multiplayer.
    * @param isMultiplayer True if multiplayer.
    */
   public void setMultiplayer(boolean isMultiplayer){
      state.setMultiplayer(isMultiplayer);
   }
   
   /**
    * Creates a new maze game.
    */
   public void newMaze(){
      state.setMaze(new Maze(mazeSize));
      state.setPlayer1(new Player());
      state.setPlayer2(new Player());
      state.setPowerups(new Powerup(mazeSize));
      
      state.setHintPath(new LinkedList<Room>());
      state.setWin(false);
      state.setEndRoom(new Room(mazeSize-1, mazeSize-1));
      
      if(mazeSize < 6){
         initialScore = 20;
      }
      else if(mazeSize < 11){
         initialScore = 40;
      }
      else if(mazeSize < 16){
         initialScore = 70;
      }
      else if(mazeSize < 26){
         initialScore = 120;
      }
      else{
         initialScore = 300;
      }
      state.setScore(initialScore);
      if(state.isMultiplayer()){
         scoreTimer.stop();
      }
      else{
         scoreTimer.restart();
      }
      
      state.setLastChange("new");
      modelChanged();
   }
   
   /**
    * Resets the player to the start of the maze.
    */
   public void reset(){
      state.setScore(initialScore);
      state.getPlayer1().reset();
      state.getHintPath().clear();
      state.setLastChange("reset");
      modelChanged();
   }
   
   /**
    * Sets a new maze size and generates a new maze game.
    * @param mazeSize The width/height of the new maze.
    */
   public void setMazeSize(int mazeSize){
      this.mazeSize = mazeSize;
      newMaze();
   }
   
   /**
    * Notify observers of change.
    */
   private void modelChanged(){
      setChanged();
      notifyObservers(state);
      state.setLastChange(null);
   }
}