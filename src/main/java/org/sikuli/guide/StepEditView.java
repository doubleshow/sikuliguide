package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.sikuli.ui.Slide;


class StepEditKit {

   static Action deleteSelectedSpritesAction =       
      new AbstractAction("Delete"){

      {
         putValue(Action.NAME, "DeleteSelectedSpritesAction");
      }

      @Override
      public void actionPerformed(ActionEvent e) {

         //            System.out.println("DeleteSelectedSpritesAction performed");
         StepEditView editView = (StepEditView) e.getSource();
         Step step = editView.getStep();            
         List<SpriteView> spriteViews = editView.selectionTool.getSelectedSpriteViews();

         //            System.out.println("DeleteSelectedSpritesAction: " + spriteViews.size() + " selected");
         for (SpriteView spriteView : spriteViews){               
            step.removeSprite(spriteView.getSprite());
         }
         editView.selectionTool.clearSelection();
      }
   };

   static private void moveHelper(ActionEvent e, Direction direction){

      StepEditView editView = (StepEditView) e.getSource();
      List<SpriteView> spriteViews = editView.selectionTool.getSelectedSpriteViews();

      for (SpriteView spriteView : spriteViews){
         if (direction == Direction.UP){
            int y = spriteView.getSprite().getY();
            spriteView.getSprite().setY(y-10);
         }else if (direction == Direction.DOWN){
            int y = spriteView.getSprite().getY();
            spriteView.getSprite().setY(y+10);
         }else if (direction == Direction.LEFT){
            int x = spriteView.getSprite().getX();
            spriteView.getSprite().setX(x-10);
         }else if (direction == Direction.RIGHT){
            int x = spriteView.getSprite().getX();
            spriteView.getSprite().setX(x+10);
         }
      }

   }

   static enum Direction {
      UP,
      DOWN,
      LEFT,
      RIGHT
   }

   static Action moveSelectedSpritesUpAction =       
      new AbstractAction("Move Up"){

      {
         putValue(Action.NAME, "moveSelectedSpritesUpAction");
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         moveHelper(e, Direction.UP);
      }
   };   

   static Action moveSelectedSpritesDownAction =       
      new AbstractAction("Move Down"){

      {
         putValue(Action.NAME, "moveSelectedSpritesDownAction");
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         moveHelper(e, Direction.DOWN);
      }
   };   

   static Action moveSelectedSpritesRightAction =       
      new AbstractAction("Move Right"){

      {
         putValue(Action.NAME, "moveSelectedSpritesRightAction");
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         moveHelper(e, Direction.RIGHT);
      }
   };   
   
   static Action moveSelectedSpritesLeftAction =       
      new AbstractAction("Move Left"){

      {
         putValue(Action.NAME, "moveSelectedSpritesLeftAction");
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         moveHelper(e, Direction.LEFT);
      }
   };   
}

class StepEditView extends StepView {

   //SpriteView selectedSpriteView;

   JPanel controlLayer = new JPanel();
   JPanel editLayer = new JPanel();


   SelectionTool selectionTool = new SelectionTool();
   MoveTool moveTool = new MoveTool();
   EditTool editTool = new EditTool();   

   public StepEditView() {

      controlLayer.setOpaque(false);
      controlLayer.setLayout(null);
      controlLayer.setSize(640,480);

      editLayer.setOpaque(false);
      editLayer.setLayout(null);
      editLayer.setSize(640,480);

      add(controlLayer,0);
      add(editLayer,0);      

      ActionMap map = getActionMap();
      map.put(StepEditKit.deleteSelectedSpritesAction.getValue(Action.NAME), StepEditKit.deleteSelectedSpritesAction);
      
      map.put(StepEditKit.moveSelectedSpritesUpAction.getValue(Action.NAME), StepEditKit.moveSelectedSpritesUpAction);
      map.put(StepEditKit.moveSelectedSpritesDownAction.getValue(Action.NAME), StepEditKit.moveSelectedSpritesDownAction);
      map.put(StepEditKit.moveSelectedSpritesLeftAction.getValue(Action.NAME), StepEditKit.moveSelectedSpritesLeftAction);
      map.put(StepEditKit.moveSelectedSpritesRightAction.getValue(Action.NAME), StepEditKit.moveSelectedSpritesRightAction);

      InputMap imap = getInputMap();
      imap.put(KeyStroke.getKeyStroke("DELETE"), StepEditKit.deleteSelectedSpritesAction.getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("BACK_SPACE"), StepEditKit.deleteSelectedSpritesAction.getValue(Action.NAME));

      imap.put(KeyStroke.getKeyStroke("UP"), StepEditKit.moveSelectedSpritesUpAction.getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("DOWN"), StepEditKit.moveSelectedSpritesDownAction.getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("LEFT"), StepEditKit.moveSelectedSpritesLeftAction.getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("RIGHT"), StepEditKit.moveSelectedSpritesRightAction.getValue(Action.NAME));

      setFocusable(true);

      addMouseListener(new MouseAdapter(){         
         public void mousePressed(MouseEvent e){
            requestFocus();
         }         
      });

      //          TransferHandler.getCutAction().getValue(Action.NAME));
      //      imap.put(KeyStroke.getKeyStroke("meta C"),
      //          TransferHandler.getCopyAction().getValue(Action.NAME));
      //      imap.put(KeyStroke.getKeyStroke("meta V"),
      //          TransferHandler.getPasteAction().getValue(Action.NAME));
   }

   private void addListenerToSprite(SpriteView spriteView){
      spriteView.addMouseListener(selectionTool);
      //
      spriteView.addMouseListener(moveTool);           
      spriteView.addMouseMotionListener(moveTool);
      spriteView.addKeyListener(moveTool);
      //      
      //      spriteView.addMouseListener(editTool);
      //spriteView.addKeyListener(editTool);
   }

   private void addListenersToSprites(){
      for (Component comp : contentLayer.getComponents()){
         if (comp instanceof SpriteView){
            SpriteView spriteView = (SpriteView) comp;
            addListenerToSprite(spriteView);
         }
      }
   }

   @Override
   public void setSlide(Slide slide){
      super.setSlide(slide);
      addListenersToSprites();
      selectionTool.setListModel(_step._spriteList);
      selectionTool.clearSelection();
   }

   @Override
   public void setStep(Step step) {
      super.setStep(step);
      addListenersToSprites();
      selectionTool.setListModel(_step._spriteList);
      selectionTool.clearSelection();
   }

   @Override
   public void refresh(){
      super.refresh();
      addListenersToSprites();
   }


   public StepEditView(Step step) {
      this();
      setStep(step);
   }

   ControlBox _controlBox;
   ControlBoxView _controlBoxView;

   //   Sprite getSelectedSprite(){
   //      if (selectedSpriteView != null)
   //         return selectedSpriteView.getSprite();
   //      else
   //         return null;
   //   }
   //
   //   private void clearSelection() {
   //      selectedSpriteView = null;            
   //      _controlBoxView.setVisible(false);
   //      editTool.endEdit();
   //   }

   //   void selectSpriteView(SpriteView spriteView) {
   //      selectedSpriteView = spriteView;
   //      selectedSpriteView.requestFocus();
   //
   ////      _controlBoxView.setVisible(true);
   ////      _controlBox.setTarget(getSelectedSprite());
   //      editTool.endEdit();
   //   }



   class EditTool implements KeyListener, MouseListener {

      private TextPropertyEditor _textPropertyEditor = new TextPropertyEditor();

      EditTool(){
         _textPropertyEditor.setVisible(false);
         editLayer.add(_textPropertyEditor,0);
      }

      void beginEdit(SpriteView spriteView) {         
         if (spriteView.getSprite() instanceof Text){
            _textPropertyEditor.setVisible(true);
            Point loc = spriteView.getLocation();
            _textPropertyEditor.setLocation(loc.x, loc.y-30);
            _textPropertyEditor.setTextSprite((Text) spriteView.getSprite());
            _textPropertyEditor.requestFocus();
         }
      }

      void abortEdit(){

      }

      void endEdit(){
         _textPropertyEditor.setVisible(false);
         _textPropertyEditor.saveText();
      }

      @Override
      public void mousePressed(MouseEvent e){
         //System.out.println("clicked :" + e);

         // double-click on a sprite to begin editing it
         if (e.getSource() instanceof SpriteView){
            if (e.getClickCount() == 2){

               beginEdit((SpriteView) e.getSource());
            }         
         }
      }


      @Override
      public void keyPressed(KeyEvent e) {
         System.out.println("EditTool: keyPressed");
         if (e.getKeyCode() == KeyEvent.VK_DELETE || 
               e.getKeyCode() == KeyEvent.VK_BACK_SPACE){

            StepEditKit.deleteSelectedSpritesAction.actionPerformed(new ActionEvent(StepEditView.this,0,""));

            //            if (selectedSpriteView != null){               
            //               _step.removeSprite(selectedSpriteView.getSprite());               
            //               remove(selectedSpriteView);
            //
            //               selectionTool.clearSelection();
            //               repaint();
            //            }
         }else if (e.getKeyCode() == KeyEvent.VK_ESCAPE){

            selectionTool.clearSelection();

         }
      }

      @Override
      public void keyReleased(KeyEvent arg0) {
      }

      @Override
      public void keyTyped(KeyEvent arg0) {
      }

      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

      @Override
      public void mouseReleased(MouseEvent e) {
      }

   }

   class MoveTool extends MouseAdapter implements KeyListener {
      Proxy proxy = new Proxy();


      MoveTool(){         
         controlLayer.add(proxy);
      }

      ComponentDragMover cm;
      class Proxy extends JPanel {

         Proxy() {
            setOpaque(true);
            setBackground(new Color(0.8f,0,0,0.5f));
            cm = new ComponentDragMover();
            cm.registerComponent(this);
            cm.addMoveListener(new DraggedMoveListener(){

               @Override
               public void componentMoved(Component source, Point origin,
                     Point destination) {
                  proxy.setVisible(false);                  
                  int dx = destination.x - origin.x;
                  int dy = destination.y - origin.y;
                  target.setX(target.getX()+dx);
                  target.setY(target.getY()+dy);
               }

            });
         }

         Sprite target;
         void setMoveTarget(Sprite sprite){
            target = sprite;
         }
      }

      @Override
      public void mouseDragged(MouseEvent e){
         //System.out.println("dragged: " + e.getSource());
         proxy.setVisible(true);         
         e.setSource(proxy);
         cm.mouseDragged(e);
      }

      @Override
      public void mouseMoved(MouseEvent e){
         //System.out.println("moved");
         e.setSource(proxy);
         cm.mouseMoved(e);
      }

      @Override
      public void mouseReleased(MouseEvent e){
         //System.out.println("released: " + e.getSource());
         if (e.getSource() == proxy){
            return;
            // Hack: ignore the second call to Proxy
            // so the client won't get moved callback twice
            // not sure why this method would be invoked
            // for Proxy, probably because e.setSource() call
         }
         e.setSource(proxy);         
         cm.mouseReleased(e);
      }

      @Override
      public void mousePressed(final MouseEvent e) {   

         // single mouse press to trigger move action
         if (e.getClickCount() == 1 && e.getSource() instanceof SpriteView){

            SpriteView spriteView = (SpriteView) e.getSource();
            proxy.setBounds(spriteView.getBounds());         
            proxy.setMoveTarget(spriteView.getSprite());

            // transfer mouse control to proxy
            e.setSource(proxy);
            cm.mousePressed(e);
         }
      }

      @Override
      public void keyPressed(KeyEvent e) {
         SpriteView spriteView = (SpriteView) e.getSource();
         Sprite sprite = spriteView.getSprite();

         // these allow users to move sprites by arrow keys
         if (e.getKeyCode() == KeyEvent.VK_UP){         
            sprite.setY(sprite.getY() - 5);
         }else if (e.getKeyCode() == KeyEvent.VK_DOWN){         
            sprite.setY(sprite.getY() + 5);
         }else if (e.getKeyCode() == KeyEvent.VK_LEFT){         
            sprite.setX(sprite.getX() - 5);
         }else if (e.getKeyCode() == KeyEvent.VK_RIGHT){         
            sprite.setX(sprite.getX() + 5);
         }

      }

      @Override
      public void keyReleased(KeyEvent e) {
      }

      @Override
      public void keyTyped(KeyEvent e) {
      }

   }

   class SelectionTool implements MouseListener {
      JList selectionList = new JList();
      ControlBox controlBox = new ControlBox();
      ControlBoxView controlBoxView = new ControlBoxView(controlBox);

      SelectionTool(){
         //selectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         selectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         controlLayer.add(controlBoxView);
      }

      void setListModel(ListModel listModel){
         selectionList.setModel(listModel);
      }

      void clearSelection(){
         selectionList.clearSelection();
         controlBoxView.setVisible(false);
      }

      List<SpriteView> getSelectedSpriteViews(){
         int[] indices = selectionList.getSelectedIndices();
         List<SpriteView> ret = new ArrayList<SpriteView>();
         for (int i = 0; i < indices.length; ++i){
            ret.add(getSpriteView(indices[i]));
         }         
         return ret;
      }

      @Override
      public void mouseClicked(MouseEvent arg0) {
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
      }

      @Override
      public void mouseExited(MouseEvent arg0) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
         // this allows the editor to gain focus when
         // any sprite is clicked on (must be a better way
         // to achieve this).
         requestFocus();

         if (e.getSource() instanceof ContextImageView){
            clearSelection();
            editTool.endEdit();

         } else if (e.getSource() instanceof SpriteView){


            if (e.getClickCount() == 1 && e.getSource() instanceof SpriteView){

               // do select

               SpriteView spriteView = (SpriteView) e.getSource();

               selectionTool.select(spriteView);

               //
               //               Sprite sprite = spriteView.getSprite();
               //
               //               int index = _step.indexOf(selectedSpriteView.getSprite());
               //               selectionList.setSelectedIndex(index);


            }

         }
      }

      private void select(SpriteView spriteView) {
         int index = _step.indexOf(spriteView.getSprite());
         selectionList.setSelectedIndex(index);    
         controlBoxView.setVisible(true);
         controlBox.setTarget(spriteView.getSprite());
         //  spriteView.requestFocus();
      }


      SpriteView getSpriteView(int index){
         Sprite sprite = _step.getSprites().get(index);
         for (Component comp : contentLayer.getComponents()){
            if (((SpriteView) comp).getSprite().equals(sprite)){
               return (SpriteView) comp;
            }            
         }
         return null;
      }

      SpriteView getSelectedSpriteView() {
         int index = selectionList.getSelectedIndex();
         return getSpriteView(index);
      }


      @Override
      public void mouseReleased(MouseEvent arg0) {
      }
   }


   ActionFactory actionFactory = new ActionFactory();
   ActionFactory getActionFactory(){
      return actionFactory;
   }
   class ActionFactory {

      Action addNewSpriteAction(final Sprite sprite){

         return new AbstractAction("Add"){

            @Override
            public void actionPerformed(ActionEvent e) {
               //               _step.addSprite(sprite);      
               //               SpriteView spriteView = ViewFactory.createView(sprite);
               //               contentLayer.add(spriteView,0);
               //               selectSpriteView(spriteView);            
            }

         };

      }
   }




   public void spritePasted(Sprite sprite) {
      _step.addSprite(sprite);      
      SpriteView spriteView = ViewFactory.createView(sprite);
      contentLayer.add(spriteView,0);
      addListenerToSprite(spriteView);
      selectionTool.select(spriteView);    
   }

   public Sprite getSelectedSprite() {
      return selectionTool.getSelectedSpriteView().getSprite();
   }




}