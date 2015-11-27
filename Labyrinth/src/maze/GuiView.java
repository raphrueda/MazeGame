package maze;

import java.util.Observable;
import java.util.Observer;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

/**
 * The GUI used by the game, that contains all GUI elements.
 * @author Adrian, Bec, Jeffrey, Raph
 *
 */
public class GuiView implements Observer{
   private JFrame frame = new JFrame("Labyrinth");
   private MazeView mazeView;
   private GridBagConstraints gbc;
   
   // hover effects
   private MouseAdapter menuHoverEffect = new MouseAdapter(){
      /* (non-Javadoc)
       * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseEntered(MouseEvent evt) {
         JComponent b = (JComponent) evt.getSource();
         b.setFont(BOLDMENUFONT);
      }
      /* (non-Javadoc)
       * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseExited(MouseEvent evt) {
         JComponent b = (JComponent) evt.getSource();
         b.setFont(PLAINMENUFONT);
      }
   };
   private MouseAdapter normalHoverEffect = new MouseAdapter(){
      /* (non-Javadoc)
       * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseEntered(MouseEvent evt) {
         JComponent b = (JComponent) evt.getSource();
         b.setFont(BOLDFONT);
      }
      /* (non-Javadoc)
       * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseExited(MouseEvent evt) {
         JComponent b = (JComponent) evt.getSource();
         b.setFont(PLAINFONT);
      }
   };
   
   // fonts
   private static final Font PLAINMENUFONT = new Font("Arial", Font.PLAIN, 18);
   private static final Font BOLDMENUFONT = new Font("Arial", Font.BOLD, 18);
   private static final Font PLAINFONT = new Font("Arial", Font.PLAIN, 14);
   private static final Font BOLDFONT = new Font("Arial", Font.BOLD, 14);
   
   // home buttons
   private JButton singleplayerButton;
   private JButton multiplayerButton;
   private JButton themeButton = new JButton("Choose Theme");
   private JButton helpButton;
   
   // difficulty panel buttons
   private JButton[] difficultyButtons;
   private JButton backButton;
   
   // game panel buttons
   private JButton homeReturnButton = new JButton("Home");
   private JButton newMazeButton = new JButton("New Maze");
   private JButton resetButton = new JButton("Reset");
   private JButton hintButton = new JButton("Get a Hint");
   private JLabel timerLabel = new JLabel();
   private JScrollPane sp;

   // panels 
   private CardLayout cl = new CardLayout();
   private JPanel mainContainer = new CardContainer();
   private JPanel homePanel = new BackgroundPanel("../images/background.png");
   private JPanel diffPanel = new BackgroundPanel("../images/background.png");
   private JPanel gamePanel = new TiledBackgroundPanel("../images/tiledBackground.png");
   private JPanel themesPanel = new TiledBackgroundPanel("../images/tiledBackground.png");
   private JPanel controlPanel = new JPanel();
   
   /**
    * JPanel that returns it's preferred size as the preferred size of the current inner panel.
    */
   private class CardContainer extends JPanel{
      /* (non-Javadoc)
       * @see javax.swing.JComponent#getPreferredSize()
       */
      @Override
      public Dimension getPreferredSize(){
         Component currentComponent = null;
         for(Component c : this.getComponents()){
            if(c.isVisible()){
               currentComponent = c;
               break;
            }
         }
         if(currentComponent != null){
            Dimension pref = currentComponent.getPreferredSize();
            return pref;
         }
         return new Dimension(300, 300);
      }
   }
   
   /**
    * JPanel with a tiled background image.
    */
   private class TiledBackgroundPanel extends JPanel{
      private Image img;
      public TiledBackgroundPanel(String loc){
         img = new ImageIcon(this.getClass().getResource(loc)).getImage();
      }
      /* (non-Javadoc)
       * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
       */
      @Override
      public void paintComponent(Graphics g) {
         int width = this.getWidth();
         int height = this.getHeight();
         int imageW = img.getWidth(this);
         int imageH = img.getHeight(this);
         for (int x = 0; x < width; x += imageW) {
             for (int y = 0; y < height; y += imageH) {
                 g.drawImage(img, x, y, this);
             }
         }
     }
   }
   
   /**
    * JPanel with a background image.
    */
   private class BackgroundPanel extends JPanel{
      private Image img;
      public BackgroundPanel(String loc){
         img = new ImageIcon(this.getClass().getResource(loc)).getImage();
      }
      /* (non-Javadoc)
       * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
       */
      @Override
      public void paintComponent(Graphics g){
         super.paintComponent(g);
         g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
      }
      /* (non-Javadoc)
       * @see javax.swing.JComponent#getPreferredSize()
       */
      @Override
      public Dimension getPreferredSize(){
         return new Dimension(1000, 600);
      }
   }
   
   /**
    * Creates and shows the GUI.
    * @param mazeView The component that displays the maze.
    */
   public GuiView(final MazeView mazeView){
      this.mazeView = mazeView;
      mainContainer.setLayout(cl); //set card layout 
      
      createHomePanel();
      
      createThemePanel();
      
      createDifficultyPanel();
      
      createGamePanel();
   
      mainContainer.add("home", homePanel);
      mainContainer.add("diff", diffPanel);
      mainContainer.add("themes", themesPanel);
      mainContainer.add("game", gamePanel);
      
      cl.show(mainContainer, "home");
      
      frame.add(mainContainer);
      packAndCenter();
      frame.setVisible(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
   
   /**
    * Creates the GUI for the home/title screen panel.
    */
   private void createHomePanel(){
         homePanel.setName("home");
         homePanel.setLayout(new GridBagLayout());
         
         // singleplayer button
         singleplayerButton = new JButton("Classic");
         singleplayerButton.setActionCommand("Singleplayer");
         singleplayerButton.setFocusPainted(false);
         singleplayerButton.setContentAreaFilled(false);
         singleplayerButton.setBorderPainted(false);
         singleplayerButton.setOpaque(false);
         singleplayerButton.setForeground(Color.WHITE);
         singleplayerButton.setFont(PLAINMENUFONT);
         singleplayerButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
               cl.show(mainContainer, "diff");
               packAndCenter();
            }
         });
         singleplayerButton.addMouseListener(menuHoverEffect);
         gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.insets = new Insets(250, 0, 0, 0); // margin at top
         gbc.anchor = GridBagConstraints.PAGE_END;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         homePanel.add(singleplayerButton, gbc);
         
         // multiplayer button
         multiplayerButton = new JButton("Versus");
         multiplayerButton.setActionCommand("Multiplayer");
         multiplayerButton.setFocusPainted(false);
         multiplayerButton.setContentAreaFilled(false);
         multiplayerButton.setBorderPainted(false);
         multiplayerButton.setOpaque(false);
         multiplayerButton.setForeground(Color.WHITE);
         multiplayerButton.setFont(PLAINMENUFONT);
         multiplayerButton.addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent event){
                cl.show(mainContainer, "diff");
               packAndCenter();
             }
          });
         multiplayerButton.addMouseListener(menuHoverEffect);
         gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = 1;
         gbc.insets = new Insets(10, 0, 0, 0);
         gbc.anchor = GridBagConstraints.PAGE_END;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         homePanel.add(multiplayerButton, gbc);
         
         // change theme button
         themeButton.setFocusPainted(false);
         themeButton.setContentAreaFilled(false);
         themeButton.setBorderPainted(false);
         themeButton.setOpaque(false);
         themeButton.setForeground(Color.WHITE);
         themeButton.setFont(PLAINMENUFONT);
         themeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
               cl.show(mainContainer, "themes");
               packAndCenter();
            }
         });
         themeButton.addMouseListener(menuHoverEffect);
         gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = 2;
         gbc.insets = new Insets(10, 0, 0, 0);
         gbc.fill = GridBagConstraints.HORIZONTAL;
         homePanel.add(themeButton, gbc);
         
         // help panel
         final BackgroundPanel helpPanel = new BackgroundPanel("../images/help.png");
         // help button
         helpButton = new JButton("Help");
         helpButton.setFocusPainted(false);
         helpButton.setContentAreaFilled(false);
         helpButton.setBorderPainted(false);
         helpButton.setOpaque(false);
         helpButton.setForeground(Color.WHITE);
         helpButton.setFont(PLAINMENUFONT);
         helpButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
               JOptionPane.showMessageDialog(frame, helpPanel, "Help", JOptionPane.PLAIN_MESSAGE);
            }
         });
         helpButton.addMouseListener(menuHoverEffect);
         gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = 3;
         gbc.insets = new Insets(10, 0, 0, 0);
         gbc.anchor = GridBagConstraints.PAGE_START;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         homePanel.add(helpButton, gbc);
      }

   /**
    * Creates the GUI for the difficulty selection panel.
    */
   private void createDifficultyPanel(){
      diffPanel.setName("Difficulty");
      diffPanel.setLayout(new GridBagLayout());
      
      difficultyButtons = new JButton[5];
      JPanel diffButtonsPanel = new JPanel(new FlowLayout());
      diffButtonsPanel.setOpaque(false);
      for(int i=0; i<difficultyButtons.length; i++){
         String text = null;
         int mazeSize = 0;
         switch(i){
         case 0:
            text = "Beginner";
            mazeSize = 5;
            break;
         case 1:
            text = "Easy";
            mazeSize = 10;
            break;
         case 2:
            text = "Advanced";
            mazeSize = 15;
            break;
         case 3:
            text = "Pro";
            mazeSize = 25;
            break;
         case 4:
            text = "Insane";
            mazeSize = 40;
            break;
         }
         difficultyButtons[i] = new JButton(text);
         difficultyButtons[i].setActionCommand("Difficulty" + Integer.toString(mazeSize));
         difficultyButtons[i].setFocusPainted(false);
         difficultyButtons[i].setContentAreaFilled(false);
         difficultyButtons[i].setBorderPainted(false);
         difficultyButtons[i].setOpaque(false);
         difficultyButtons[i].setForeground(Color.WHITE);
         difficultyButtons[i].setPreferredSize(new Dimension(140, 30));
         difficultyButtons[i].setFont(PLAINMENUFONT);
         difficultyButtons[i].addMouseListener(menuHoverEffect);
         
         diffButtonsPanel.add(difficultyButtons[i]);
      }
      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.insets = new Insets(250, 0, 0, 0); // margin at top
      gbc.anchor = GridBagConstraints.PAGE_END;
      diffPanel.add(diffButtonsPanel, gbc);
      
      // back button
      backButton = new JButton("Return to Home");
      backButton.setFocusPainted(false);
      backButton.setContentAreaFilled(false);
      backButton.setBorderPainted(false);
      backButton.setOpaque(false);
      backButton.setForeground(Color.WHITE);
      backButton.setFont(PLAINMENUFONT);
      backButton.setBounds(0, 0, 10, 10);
      backButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent event){
            cl.show(mainContainer, "home");
            packAndCenter();
         }
      });
      backButton.addMouseListener(menuHoverEffect);
      
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.insets = new Insets(30, 0, 0, 0);
      gbc.anchor = GridBagConstraints.PAGE_END;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      diffPanel.add(backButton, gbc);
   }
   
   /**
    * Creates the GUI for the theme selection panel.
    */
   private void createThemePanel(){
      themesPanel.setName("Themes");
      themesPanel.setLayout(new GridLayout(0,4));
      
      ButtonGroup themeGroup = new ButtonGroup();
      LinkedList<String> dirs = new LinkedList<String>();
      File dir = new File(this.getClass().getResource("../images/").getPath());
      for(File f : dir.listFiles()){
         if(f.isDirectory()){
            dirs.push(f.getName());
         }
      }
      Collections.sort(dirs);
      for(final String file : dirs){
         JRadioButton themeRadioButton = new JRadioButton(file,
               new ImageIcon(this.getClass().getResource("../images/"+file+"/preview.png")));
         themeRadioButton.setFont(PLAINFONT);
         themeRadioButton.setOpaque(false);
         themeRadioButton.setBackground(Color.GRAY);
         themeRadioButton.setForeground(Color.WHITE);
         themeGroup.add(themeRadioButton);
         themesPanel.add(themeRadioButton);
         themeRadioButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
               mazeView.setTheme(file);
               cl.show(mainContainer, "home");
               packAndCenter();
            }
         });
         themeRadioButton.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent evt) {
               JComponent b = (JComponent) evt.getSource();
               b.setOpaque(true);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
               JComponent b = (JComponent) evt.getSource();
               b.setOpaque(false);
            }
         });
      }
   }

   /**
    * Creates the GUI for the actual game panel.
    */
   private void createGamePanel(){
      gamePanel.setName("Game");
      gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.PAGE_AXIS));
      
      // control panel
      controlPanel.setOpaque(false);
      controlPanel.setLayout(new FlowLayout());
      homeReturnButton.setFocusPainted(false);
      homeReturnButton.setContentAreaFilled(false);
      homeReturnButton.setBorderPainted(false);
      homeReturnButton.setOpaque(false);
      homeReturnButton.setForeground(Color.WHITE);
      homeReturnButton.setPreferredSize(new Dimension(80, 30));
      homeReturnButton.setFont(PLAINFONT);
      homeReturnButton.addMouseListener(normalHoverEffect);
      homeReturnButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event) 
         {
            cl.show(mainContainer, "home");
            packAndCenter();
         }
      });
      controlPanel.add(homeReturnButton);
      
      resetButton.setFocusPainted(false);
      resetButton.setContentAreaFilled(false);
      resetButton.setBorderPainted(false);
      resetButton.setOpaque(false);
      resetButton.setForeground(Color.WHITE);
      resetButton.setPreferredSize(new Dimension(80, 30));
      resetButton.setFont(PLAINFONT);
      resetButton.addMouseListener(normalHoverEffect);
      controlPanel.add(resetButton);
      
      newMazeButton.setFocusPainted(false);
      newMazeButton.setContentAreaFilled(false);
      newMazeButton.setBorderPainted(false);
      newMazeButton.setOpaque(false);
      newMazeButton.setForeground(Color.WHITE);
      newMazeButton.setPreferredSize(new Dimension(105, 30));
      newMazeButton.setFont(PLAINFONT);
      newMazeButton.addMouseListener(normalHoverEffect);
      newMazeButton.setActionCommand("Start");
      controlPanel.add(newMazeButton);
      
      hintButton.setFocusPainted(false);
      hintButton.setContentAreaFilled(false);
      hintButton.setBorderPainted(false);
      hintButton.setOpaque(false);
      hintButton.setForeground(Color.WHITE);
      hintButton.setPreferredSize(new Dimension(105, 30));
      hintButton.setFont(PLAINFONT);
      hintButton.addMouseListener(normalHoverEffect);
      hintButton.setActionCommand("Hint");
      controlPanel.add(hintButton);
      
      // timer/Score
      timerLabel.setForeground(Color.WHITE);
      timerLabel.setPreferredSize(new Dimension(120, 20));
      timerLabel.setFont(PLAINFONT);
      controlPanel.add(timerLabel);
      
      gbc = new GridBagConstraints();
      gbc.gridy = 0;
      controlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
      gamePanel.add(controlPanel);
      
      // game
      gbc = new GridBagConstraints();
      gbc.gridy = 1;
      sp = new JScrollPane(mazeView);
      sp.setAlignmentX(Component.CENTER_ALIGNMENT);
      sp.setBorder(BorderFactory.createEmptyBorder());
      sp.getVerticalScrollBar().setPreferredSize(new Dimension(17,0));
      sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0,17));
      gamePanel.add(sp);
   }
   
   /**
    * Sets the controller to listen to the relevant buttons.
    * @param controller The controller.
    */
   public void addController(ActionListener controller){
      singleplayerButton.addActionListener(controller);
      multiplayerButton.addActionListener(controller);
      hintButton.addActionListener(controller);
      resetButton.addActionListener(controller);
      newMazeButton.addActionListener(controller);
      for(int i=0; i<difficultyButtons.length; i++){
         difficultyButtons[i].addActionListener(controller);
      }
   }
   
   /**
    * Packs the frame and aligns it to the top of the screen and centers is horizontally.
    */
   private void packAndCenter(){
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      sp.setMaximumSize(screenSize);
      Dimension mazeViewPref = mazeView.getPreferredSize();
      
      Dimension spDimension = new Dimension(screenSize.width, screenSize.height);
      spDimension.width -= 25;
      spDimension.height -= 100;
      boolean hasVertScroll = true;
      boolean hasHoriScroll = true;
      
      if(spDimension.width > mazeViewPref.width){
         spDimension.width = mazeViewPref.width;
         hasHoriScroll = false;
      }
      if(spDimension.height > mazeViewPref.height){
         spDimension.height = mazeViewPref.height;
         hasVertScroll = false;
      }
      
      if(hasVertScroll){
         spDimension.width += 19;
      }
      if(hasHoriScroll){
         spDimension.height += 19;
      }
      
      sp.setPreferredSize(spDimension);
      sp.setMaximumSize(spDimension);
      
      frame.pack();
      frame.pack(); // need to repeat this for some reason.
      Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
      Point newLocation = new Point(middle.x - (frame.getWidth() / 2), 0);
      frame.setLocation(newLocation);
   }
   
   /* (non-Javadoc)
    * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
    */
   @Override
   public void update(Observable obs, Object obj) {
      State state = (State) obj;
      if("new".equals(state.getLastChange())){
         mazeView.updateDimensions(state.getMaze().getSize());
         timerLabel.setText("Time Left: " + state.getScore());
         resetButton.setEnabled(true);
         resetButton.setForeground(Color.WHITE);
         resetButton.addMouseListener(normalHoverEffect);
         if(state.isMultiplayer()){
            resetButton.setVisible(false);
            hintButton.setVisible(false);
            timerLabel.setVisible(false);
         }
         else{
            resetButton.setVisible(true);
            hintButton.setVisible(true);
            timerLabel.setVisible(true);
         }
         cl.show(mainContainer, "game");
         packAndCenter();
      }
      else if("win".equals(state.getLastChange())){
         resetButton.setEnabled(false);
         resetButton.setForeground(Color.GRAY);
         for(MouseListener l : resetButton.getMouseListeners()){
            resetButton.removeMouseListener(l);
         }
         resetButton.setFont(PLAINFONT);
      }
      else if("gameOver".equals(state.getLastChange())){
         resetButton.setEnabled(false);
         resetButton.setForeground(Color.GRAY);
         for(MouseListener l : resetButton.getMouseListeners()){
            resetButton.removeMouseListener(l);
         }
         resetButton.setFont(PLAINFONT);
         timerLabel.setText("Game Over");
      }
      else if("reset".equals(state.getLastChange())){
         timerLabel.setText("Time Left: " + state.getScore());
      }
      else if("scoreTimer".equals(state.getLastChange())){
         timerLabel.setText("Time Left: " + state.getScore());
      }
      else if("hintOn".equals(state.getLastChange())){
         if(!"Game Over".equals(timerLabel.getText())){
            timerLabel.setText("Time Left: " + state.getScore());
         }
      }
   }
}
