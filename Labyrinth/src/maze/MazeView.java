package maze;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/**
 * A JPanel that is the maze game.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class MazeView extends JPanel implements Observer{
   private Maze maze;
   private Player p1;
   private Player p2;
   private Room endRoom;
   private List<Room> hintPath;
   private String overlayText;
   private boolean multiplayer;
   private Powerup powerups;
   private Dimension dimensionSize = new Dimension(0,0);
   
   // images
   private Image roomImage;
   private Image wallImage;
   private Image connImage;
   private Image hintImage;
   private Image endImage;
   private Image playerImage;
   private Image powerupImage;
   private Map<String, Image> powerupEffectImages = new HashMap<String, Image>();
   
   // size of graphics
   private static final int ROOMSIZE = 20;
   private static final int WALLSIZE = 6;
   private static final int PLAYERSIZE = 18;
   private static final int ENDSIZE = 20;
   private static final int POWERUPSIZE = 18;
   private static final int POWERUPEFFECTSIZE = 10;
   
   // game status
   private boolean win = false;
   private boolean gameOver = false;
   
   // p1 movement
   private int p1AnimateRow;
   private int p1AnimateCol;
   private int p1OldRow;
   private int p1OldCol;
   private int animateLoopCountPlayer1;
   private Timer animateTimerPlayer1;
   private String p1Key;
   
   // p2 movement
   private int p2AnimateRow;
   private int p2AnimateCol;
   private int p2OldRow;
   private int p2OldCol;
   private int animateLoopCountPlayer2;
   private Timer animateTimerPlayer2;
   private String p2Key;
   
   // controller
   private ActionListener controller;
   
   /**
    * Initilises the maze view, setting the initial theme and cross-theme images.
    * The key bindings are also set.
    */
   public MazeView(){
      ImageIcon img;
      img = new ImageIcon(this.getClass().getResource("../images/fastEffect.png"));
      powerupEffectImages.put("fast", img.getImage());
      img = new ImageIcon(this.getClass().getResource("../images/slowEffect.png"));
      powerupEffectImages.put("slow", img.getImage());
      img = new ImageIcon(this.getClass().getResource("../images/oppositeEffect.png"));
      powerupEffectImages.put("opposite", img.getImage());
      
      setTheme("Castle");
      
      InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
      ActionMap am = this.getActionMap();
      im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "Hint");
      am.put("Hint", new KeyBindingAction("Hint"));
      
      im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "Reset");
      am.put("Reset", new KeyBindingAction("Reset"));
      
      im.put(KeyStroke.getKeyStroke("pressed UP"), "Up2");
      im.put(KeyStroke.getKeyStroke("pressed DOWN"), "Down2");
      im.put(KeyStroke.getKeyStroke("pressed LEFT"), "Left2");
      im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "Right2");
      
      im.put(KeyStroke.getKeyStroke("released UP"), "Clear2");
      im.put(KeyStroke.getKeyStroke("released DOWN"), "Clear2");
      im.put(KeyStroke.getKeyStroke("released LEFT"), "Clear2");
      im.put(KeyStroke.getKeyStroke("released RIGHT"), "Clear2");
      
      im.put(KeyStroke.getKeyStroke("pressed W"), "Up1");
      im.put(KeyStroke.getKeyStroke("pressed S"), "Down1");
      im.put(KeyStroke.getKeyStroke("pressed A"), "Left1");
      im.put(KeyStroke.getKeyStroke("pressed D"), "Right1");
      
      im.put(KeyStroke.getKeyStroke("released W"), "Clear1");
      im.put(KeyStroke.getKeyStroke("released S"), "Clear1");
      im.put(KeyStroke.getKeyStroke("released A"), "Clear1");
      im.put(KeyStroke.getKeyStroke("released D"), "Clear1");

      am.put("Up1", new KeyBindingDirection("Up1"));
      am.put("Down1", new KeyBindingDirection("Down1"));
      am.put("Left1", new KeyBindingDirection("Left1"));
      am.put("Right1", new KeyBindingDirection("Right1"));
      
      am.put("Up2", new KeyBindingDirection("Up2"));
      am.put("Down2", new KeyBindingDirection("Down2"));
      am.put("Left2", new KeyBindingDirection("Left2"));
      am.put("Right2", new KeyBindingDirection("Right2"));
      
      am.put("Clear1", new KeyBindingClear("Clear1"));
      am.put("Clear2", new KeyBindingClear("Clear2"));
   }
   
   /* (non-Javadoc)
    * @see javax.swing.JComponent#getPreferredSize()
    */
   @Override
   public Dimension getPreferredSize(){
      return dimensionSize;
   }
   /* (non-Javadoc)
    * @see javax.swing.JComponent#getMinimumSize()
    */
   @Override
   public Dimension getMinimumSize(){
      return dimensionSize;
   }
   /* (non-Javadoc)
    * @see javax.swing.JComponent#getMaximumSize()
    */
   @Override
   public Dimension getMaximumSize(){
      return dimensionSize;
   }
   
   /**
    * Clears the player's direction.
    */
   private class KeyBindingClear extends AbstractAction{
      private String key;
      public KeyBindingClear(String key){
         this.key = key;
      }
      /* (non-Javadoc)
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
       */
      @Override
      public void actionPerformed(ActionEvent event){
         int playerNum = Integer.parseInt(key.substring(key.length()-1));
         if(multiplayer && playerNum == 2){
            p2Key = null;
         }
         else{
            p1Key = null;
         }
      }
   }
   /**
    * Sends player movement to the controller and sets the current player direction.
    * The player direction is used to continue moving the player if the key is held down.
    */
   private class KeyBindingDirection extends AbstractAction{
      private String key;
      public KeyBindingDirection(String key){
         this.key = key;
      }
      /* (non-Javadoc)
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
       */
      @Override
      public void actionPerformed(ActionEvent event){
         int playerNum = Integer.parseInt(key.substring(key.length()-1));
         if(multiplayer && playerNum==2){
            if(p2Key != null){
               return;
            }
         }
         else{
            if(p1Key != null){
               return;
            }
         }
         event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, key);
         controller.actionPerformed(event);
         if(multiplayer){
            if(playerNum == 1){
               p1Key = key;
            }
            else{
               p2Key = key;
            }
         }
         else{
            p1Key = key;
         }
      }
   }
   /**
    * Simply sends the pressed key to the controller.
    */
   private class KeyBindingAction extends AbstractAction{
      private String key;
      public KeyBindingAction(String key){
         this.key = key;
      }
      /* (non-Javadoc)
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
       */
      @Override
      public void actionPerformed(ActionEvent event){
         event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, key);
         controller.actionPerformed(event);
      }
   }
   
   /**
    * Sets the controller.
    * @param controller The controller.
    */
   public void addController(ActionListener controller){
      this.controller = controller;
   }

   /* (non-Javadoc)
    * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
    */
   @Override
   public void update(Observable obs, Object obj) {
      State state = (State) obj;
      if("scoreTimer".equals(state.getLastChange())){
         return;
      }
      hintPath = state.getHintPath();
      p1 = state.getPlayer1();
      p2 = state.getPlayer2();
      maze = state.getMaze();
      win = state.isWin();
      endRoom = state.getEndRoom();
      multiplayer = state.isMultiplayer();
      powerups = state.getPowerups();
      
      updateDimensions(maze.getSize());
      
      if("move1".equals(state.getLastChange())){
         animatePlayer1();
      }
      else if("move2".equals((state.getLastChange()))){
         animatePlayer2();
      }
      else if("new".equals(state.getLastChange())){
         p1AnimateRow = p1.getRow()*(ROOMSIZE+WALLSIZE)+WALLSIZE+1;
         p1AnimateCol = p1.getCol()*(ROOMSIZE+WALLSIZE)+WALLSIZE+1;
         if(multiplayer){
            p2AnimateRow = p2.getRow()*(ROOMSIZE+WALLSIZE)+WALLSIZE+1;
            p2AnimateCol = (maze.getSize() - p2.getCol()-1)*(ROOMSIZE+WALLSIZE)+WALLSIZE+1;
         }
         gameOver = false;
         repaint();
      }
      else if("reset".equals(state.getLastChange())){
         p1AnimateRow = p1.getRow()*(ROOMSIZE+WALLSIZE)+WALLSIZE+1;
         p1AnimateCol = p1.getCol()*(ROOMSIZE+WALLSIZE)+WALLSIZE+1;
         repaint();
      }
      else if("gameOver".equals(state.getLastChange())){
         gameOver = true;
         overlayText = "Game Over";
         repaint();
      }
      else if(state.getLastChange().startsWith("powerup")){
         repaint();
      }
      else if(state.getLastChange().equals("win")){
         overlayText = "You Win!";
         repaint();
      }
      else if(state.getLastChange().equals("winP1")){
         overlayText = "Player 1 Wins!";
         repaint();
      }
      else if(state.getLastChange().equals("winP2")){
         overlayText = "Player 2 Wins!";
         repaint();
      }
      else if(state.getLastChange().startsWith("hint")){
         repaint();
      }
   }
   
   /**
    * Sets the theme of the maze game.
    * @param theme A string containing the name of the theme.
    */
   public void setTheme(String theme){
      ImageIcon img = new ImageIcon(this.getClass().getResource("../images/"+theme+"/room.png"));
      roomImage = img.getImage();
      
      img = new ImageIcon(this.getClass().getResource("../images/"+theme+"/wall.png"));
      wallImage = img.getImage();
      
      img = new ImageIcon(this.getClass().getResource("../images/"+theme+"/conn.png"));
      connImage = img.getImage();
      
      
      img = new ImageIcon(this.getClass().getResource("../images/"+theme+"/hint.png"));
      hintImage = img.getImage();
      
      img = new ImageIcon(this.getClass().getResource("../images/"+theme+"/end.png"));
      endImage = img.getImage();
      
      img = new ImageIcon(this.getClass().getResource("../images/"+theme+"/player.png"));
      playerImage = img.getImage();
      
      img = new ImageIcon(this.getClass().getResource("../images/"+theme+"/powerup.png"));
      powerupImage = img.getImage();
   }
   
   /**
    * Updates the GUI dimensions of this component.
    * @param mazeSize the size (width/height) of the maze that needs to be displayed.
    */
   public void updateDimensions(int mazeSize){
      if(multiplayer){
         dimensionSize = new Dimension(2*(mazeSize*(ROOMSIZE+WALLSIZE) + WALLSIZE),
                              mazeSize*(ROOMSIZE+WALLSIZE) + WALLSIZE);
      }
      else{
         dimensionSize = new Dimension(mazeSize*(ROOMSIZE+WALLSIZE) + WALLSIZE,
                              mazeSize*(ROOMSIZE+WALLSIZE) + WALLSIZE);
      }
   }
   
   /**
    * Animates player 1 movement.
    */
   private void animatePlayer1(){
      p1OldRow = p1.getPrevRow();
      p1OldCol = p1.getPrevCol();
      animateLoopCountPlayer1 = 1;
      /*
       * slow:    10 * 0.04 = 0.4 seconds
       * normal:  10 * 0.02 = 0.2 seconds
       * fast:    10 * 0.01 = 0.1 seconds
       */
      int animationLength = 20;
      if("fast".equals(p1.getCurrentPowerup())){
         animationLength = 10;
      }
      else if("slow".equals(p1.getCurrentPowerup())){
         animationLength = 40;
      }
      animateTimerPlayer1 = new Timer(animationLength, new ActionListener(){
         public void actionPerformed(ActionEvent event){
            p1AnimateRow = (int) ((p1.getRow()*0.1*animateLoopCountPlayer1
                                    +p1OldRow*0.1*(10-animateLoopCountPlayer1))
                                    *(ROOMSIZE+WALLSIZE)+WALLSIZE+1);
            p1AnimateCol = (int) ((p1.getCol()*0.1*animateLoopCountPlayer1
                                    +p1OldCol*0.1*(10-animateLoopCountPlayer1))
                                    *(ROOMSIZE+WALLSIZE)+WALLSIZE+1);
            
            repaint();
            if(animateLoopCountPlayer1 == 10){
               animateTimerPlayer1.stop();
               controller.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "FinishAnimate1"));
               if(p1Key != null){
                  controller.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, p1Key));
               }
            }
            animateLoopCountPlayer1++;
         }
      });
      animateTimerPlayer1.start();
   }
   
   /**
    * Animates player 2 movement.
    */
   private void animatePlayer2(){
      p2OldRow = p2.getPrevRow();
      p2OldCol = p2.getPrevCol();
      animateLoopCountPlayer2 = 1;
      int animationLength = 20;
      if("fast".equals(p2.getCurrentPowerup())){
         animationLength = 10;
      }
      else if("slow".equals(p2.getCurrentPowerup())){
         animationLength = 40;
      }
      animateTimerPlayer2 = new Timer(animationLength, new ActionListener(){
         public void actionPerformed(ActionEvent event){
            p2AnimateRow = (int) ((p2.getRow()*0.1*animateLoopCountPlayer2
                                    +p2OldRow*0.1*(10-animateLoopCountPlayer2))
                                    *(ROOMSIZE+WALLSIZE)+WALLSIZE+1);
            p2AnimateCol = (int) (((maze.getSize()-p2.getCol()-1)*0.1*animateLoopCountPlayer2
                                    +(maze.getSize()-p2OldCol-1)*0.1*(10-animateLoopCountPlayer2))
                                    *(ROOMSIZE+WALLSIZE)+WALLSIZE+1);
            repaint();
            if(animateLoopCountPlayer2 == 10){
               animateTimerPlayer2.stop();
               controller.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "FinishAnimate2"));
               if(p2Key != null){
                  controller.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, p2Key));
               }
            }
            animateLoopCountPlayer2++;
         }
      });
      animateTimerPlayer2.start();
   }
   
   
   /* (non-Javadoc)
    * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
    */
   @Override
   public void paintComponent(Graphics g){
      super.paintComponent(g);
      
      g.setColor(Color.red);
      int mazeSize = maze.getSize();
      int row;
      int col;
      int paintCol = 0;
      int paintColWithWall = 0;
      int paintRow = 0;
      int paintRowWithWall = 0;
      for(row=0; row<mazeSize; row++){
         for(col=0; col<mazeSize; col++){
            paintCol = col*(ROOMSIZE+WALLSIZE);
            paintColWithWall = col*(ROOMSIZE+WALLSIZE)+WALLSIZE;
            paintRow = row*(ROOMSIZE+WALLSIZE);
            paintRowWithWall = row*(ROOMSIZE+WALLSIZE)+WALLSIZE;
            
            // NW wall
            g.drawImage(wallImage, paintCol, paintRow, WALLSIZE, WALLSIZE, null);
            
            // N wall or connector
            if(row==0 || !maze.isConnected(new Room(row, col), new Room(row-1, col))){
               g.drawImage(wallImage, paintColWithWall, paintRow, ROOMSIZE, WALLSIZE, null);
            }
            else{
               g.drawImage(connImage, paintColWithWall, paintRow, ROOMSIZE, WALLSIZE, null);
            }
            // W wall or connector
            if(col==0 || !maze.isConnected(new Room(row, col), new Room(row, col-1))){
               g.drawImage(wallImage, paintCol, paintRowWithWall, WALLSIZE, ROOMSIZE, null);
            }
            else{
               g.drawImage(connImage, paintCol, paintRowWithWall, WALLSIZE, ROOMSIZE, null);
            }
            
            // room
            if(hintPath.contains(new Room(row, col))){
               g.drawImage(hintImage, paintColWithWall, paintRowWithWall, ROOMSIZE, ROOMSIZE, null);
            }
            else{
               g.drawImage(roomImage, paintColWithWall, paintRowWithWall, ROOMSIZE, ROOMSIZE, null);
            }
         }
         
         // right border
         paintCol = col*(ROOMSIZE+WALLSIZE);
         // NW wall
         g.drawImage(wallImage, paintCol, paintRow, WALLSIZE, WALLSIZE, null);
         // W wall
         g.drawImage(wallImage, paintCol, paintRowWithWall, WALLSIZE, ROOMSIZE, null);
      }
      
      // bottom border
      paintRow = row*(ROOMSIZE+WALLSIZE);
      for(col=0; col<mazeSize;col++){
         paintCol = col*(ROOMSIZE+WALLSIZE);
         paintColWithWall = col*(ROOMSIZE+WALLSIZE)+WALLSIZE;
         // NW wall
         g.drawImage(wallImage, paintCol, paintRow, WALLSIZE, WALLSIZE, null);
         // N wall
         g.drawImage(wallImage, paintColWithWall, paintRow, ROOMSIZE, WALLSIZE, null);
      }
      paintCol = col*(ROOMSIZE+WALLSIZE);
      // NW wall
      g.drawImage(wallImage, paintCol, paintRow, WALLSIZE, WALLSIZE, null);
      
      // end
      g.drawImage(endImage, (ROOMSIZE+WALLSIZE)*endRoom.getCol()+WALLSIZE,
            (ROOMSIZE+WALLSIZE)*endRoom.getRow()+WALLSIZE, ENDSIZE, ENDSIZE, null);
      
      // player
      g.drawImage(playerImage, p1AnimateCol, p1AnimateRow, PLAYERSIZE, PLAYERSIZE, null);
      
      // power ups on map
      for(Room r : powerups.getPowerupRooms()){
         g.drawImage(powerupImage, r.getCol()*(ROOMSIZE+WALLSIZE)+WALLSIZE+(ROOMSIZE-POWERUPSIZE)/2,
               r.getRow()*(ROOMSIZE+WALLSIZE)+WALLSIZE+(ROOMSIZE-POWERUPSIZE)/2, POWERUPSIZE, POWERUPSIZE, null);
      }
      
      // current player powerup effects
      if(p1.getCurrentPowerup() != null){
         String powerupName = p1.getCurrentPowerup();
         g.drawImage(powerupEffectImages.get(powerupName), p1AnimateCol+(ROOMSIZE-POWERUPEFFECTSIZE)/2 + 1,
               p1AnimateRow-POWERUPEFFECTSIZE + 5, POWERUPEFFECTSIZE, POWERUPEFFECTSIZE, null);
      }
      
      // =============================================
   if(multiplayer){
      int offsetX = maze.getSize()*(ROOMSIZE+WALLSIZE) + WALLSIZE;
      for(row=0; row<mazeSize; row++){
         for(col=0; col<mazeSize; col++){
            int inverseCol = mazeSize-col-1;
            
            paintCol = offsetX+col*(ROOMSIZE+WALLSIZE);
            paintColWithWall = offsetX+col*(ROOMSIZE+WALLSIZE)+WALLSIZE;
            paintRow = row*(ROOMSIZE+WALLSIZE);
            paintRowWithWall = row*(ROOMSIZE+WALLSIZE)+WALLSIZE;
            
            // NW wall
            g.drawImage(wallImage, paintCol, paintRow, WALLSIZE, WALLSIZE, null);
            
            // N wall or connector
            if(row==0 || !maze.isConnected(new Room(row, inverseCol), new Room(row-1, inverseCol))){
               g.drawImage(wallImage, paintColWithWall, paintRow, ROOMSIZE, WALLSIZE, null);
            }
            else{
               g.drawImage(connImage, paintColWithWall, paintRow, ROOMSIZE, WALLSIZE, null);
            }
            
            // W wall or connector
            if(col==0 || !maze.isConnected(new Room(row, inverseCol), new Room(row, inverseCol+1))){
               g.drawImage(wallImage, paintCol, paintRowWithWall, WALLSIZE, ROOMSIZE, null);
            }
            else{
               g.drawImage(connImage, paintCol, paintRowWithWall, WALLSIZE, ROOMSIZE, null);
            }
            
            //room
            g.drawImage(roomImage, paintColWithWall, paintRowWithWall, ROOMSIZE, ROOMSIZE, null);
         }
         // right border
         paintCol = offsetX+col*(ROOMSIZE+WALLSIZE);
         // NW wall
         g.drawImage(wallImage, paintCol, paintRow, WALLSIZE, WALLSIZE, null);
         // N wall
         g.drawImage(wallImage, paintCol, paintRowWithWall, WALLSIZE, ROOMSIZE, null);
      }
      
      // bottom border
      paintRow = row*(ROOMSIZE+WALLSIZE);
      for(col=0; col<mazeSize;col++){
         paintCol = offsetX+col*(ROOMSIZE+WALLSIZE);
         paintColWithWall =offsetX+col*(ROOMSIZE+WALLSIZE)+WALLSIZE;
         // NW wall
         g.drawImage(wallImage, paintCol, paintRow, WALLSIZE, WALLSIZE, null);
         // N wall
         g.drawImage(wallImage, paintColWithWall, paintRow, ROOMSIZE, WALLSIZE, null);
      }
      paintCol = offsetX+col*(ROOMSIZE+WALLSIZE);
      // NW wall
      g.drawImage(wallImage, paintCol, paintRow, WALLSIZE, WALLSIZE, null);
      
      
      // end
      g.drawImage(endImage, offsetX+(ROOMSIZE+WALLSIZE)*(endRoom.getCol()-endRoom.getCol())+WALLSIZE,
            (ROOMSIZE+WALLSIZE)*endRoom.getRow()+WALLSIZE, ENDSIZE, ENDSIZE, null);

      // player
      g.drawImage(playerImage, offsetX+p2AnimateCol, p2AnimateRow, PLAYERSIZE, PLAYERSIZE, null);
      
      // power ups on map
      for(Room r : powerups.getPowerupRooms()){
         g.drawImage(powerupImage, offsetX+(mazeSize-r.getCol()-1)*(ROOMSIZE+WALLSIZE)+WALLSIZE+(ROOMSIZE-POWERUPSIZE)/2,
               r.getRow()*(ROOMSIZE+WALLSIZE)+WALLSIZE+(ROOMSIZE-POWERUPSIZE)/2, POWERUPSIZE, POWERUPSIZE, null);
      }
      
      // current player powerup effects
      if(p2.getCurrentPowerup() != null){
         String powerupName = p2.getCurrentPowerup();
         g.drawImage(powerupEffectImages.get(powerupName), offsetX+p2AnimateCol+(ROOMSIZE-POWERUPEFFECTSIZE)/2 + 1,
               p2AnimateRow-POWERUPEFFECTSIZE + 5, POWERUPEFFECTSIZE, POWERUPEFFECTSIZE, null);
      }
   }
      
   // ========================================================
      if(gameOver || win){
         Graphics2D g2 = (Graphics2D) g;
         // smooth fonts
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         Font textFont = new Font("Arial", Font.BOLD, 24);
         g2.setFont(textFont);
         // center text
         FontMetrics textMetrics = g2.getFontMetrics(textFont);
         int centeredX = (this.getWidth()/2) - (textMetrics.stringWidth(overlayText)/2);  
         int centeredY = (this.getHeight()/2) + WALLSIZE;  
         g2.setColor(new Color(0, 0, 0, 175));
         g2.fillRect(0, 0, this.getWidth(), this.getHeight());
         g2.setColor(new Color(255, 153, 0));
         g2.drawString(overlayText, centeredX, centeredY);
      }
   }
}