package maze;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The maze controller.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class Controller implements ActionListener{
   private Model model;

   /**
    * Sets the model the controller will use.
    * @param model The model the controller will use.
    */
   public void setModel(Model model){
      this.model = model;
   }
   
   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   @Override
   public void actionPerformed(ActionEvent event) {
      String command = event.getActionCommand();
      if(command.startsWith("Up") || command.startsWith("Down")
            || command.startsWith("Left") || command.startsWith("Right")){
         model.movePlayer(command);
      }
      else if(command.startsWith("FinishAnimate")){
         model.completeMovePlayer(Integer.parseInt(command.substring(command.length()-1)));
      }
      else if(command.equals("Hint")){
         model.displayHint();
      }
      else if(command.equals("Singleplayer")){
         model.setMultiplayer(false);
      }
      else if(command.equals("Multiplayer")){
         model.setMultiplayer(true);
      }
      else if(command.equals("Start")){
         model.newMaze();
      }
      else if(command.equals("Reset")){
         model.reset();
      }
      else if(command.startsWith("Difficulty")){
         int mazeSize = Integer.parseInt(command.substring(command.indexOf('y')+1));
         model.setMazeSize(mazeSize);
      }
   }
}