package maze;

/**
 * A GUI class that acts as a wrapper around the MVC structure.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class Gui{
   private Model model;
   private MazeView mazeView;
   private GuiView guiView;
   
   /**
    * Sets up the MVC structure.
    */
   public Gui(){
      model = new Model();
      mazeView = new MazeView();
      guiView = new GuiView(mazeView);
      
      model.addObserver(guiView);
      model.addObserver(mazeView);
      
      Controller controller = new Controller();
      controller.setModel(model);
      
      mazeView.addController(controller);
      guiView.addController(controller);
   }
}