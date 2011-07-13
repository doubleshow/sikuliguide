package org.sikuli.guide;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class StepEditView extends StepView {
 
   SpriteView selectedSpriteView;
   
   SelectionTool selectionTool = new SelectionTool();
   KeyTool keyTool = new KeyTool();
   ComponentDragMover cm = new ComponentDragMover();
   
   public StepEditView(Step step) {
      super(step);
      setFocusable(true);
      
      for (Component comp : getComponents()){
         if (comp instanceof SpriteView){
            SpriteView spriteView = (SpriteView) comp;
            
            spriteView.addMouseListener(selectionTool);
            spriteView.addKeyListener(keyTool);
            
            cm.registerComponent(spriteView);
            cm.addMoveListener(new DraggedMoveListener(){

               @Override
               public void componentMoved(Component source, Point origin, Point destination) {

                  SpriteView view = (SpriteView) source;
//                  undoableEditSupport.postEdit(new UndoableMove(view.getModel(), origin, destination));

                  Point newLocation = view.getLocation();
                  view.getSprite().setX(newLocation.x);
                  view.getSprite().setY(newLocation.y);                  
               };

            });

            
         } else{
            
            comp.addMouseListener(selectionTool);
            comp.addKeyListener(keyTool);
         }         
      }   
      

      
      
      _controlBox = new ControlBox();
      _controlBoxView = new ControlBoxView(_controlBox);
      
      add(_controlBoxView,0);
//      addKeyListener(keyTool);
   }
   
   ControlBox _controlBox;
   ControlBoxView _controlBoxView;

   Sprite getSelectedSprite(){
      if (selectedSpriteView != null)
         return selectedSpriteView.getSprite();
      else
         return null;
   }
   
   private void clearSelection() {
      selectedSpriteView = null;            
      _controlBoxView.setVisible(false);
   }

   class KeyTool implements KeyListener {

      @Override
      public void keyPressed(KeyEvent e) {
         System.out.println("pressed");
         if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
            
            if (selectedSpriteView != null){               
               _step.removeSprite(selectedSpriteView.getSprite());               
               remove(selectedSpriteView);
               
               _controlBoxView.setVisible(false);               
               repaint();
            }
         }else if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
            
            clearSelection();
            
         }
      }

      @Override
      public void keyReleased(KeyEvent arg0) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void keyTyped(KeyEvent arg0) {
         // TODO Auto-generated method stub
         
      }
      
   }
   
   class SelectionTool implements MouseListener {

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
         if (e.getSource() instanceof ContextImageView){
            
            clearSelection();
            
         } else if (e.getSource() instanceof SpriteView){
            
            // do select

            selectedSpriteView = (SpriteView) e.getSource();
            selectedSpriteView.requestFocus();
            
            _controlBoxView.setVisible(true);
            _controlBox.setTarget(getSelectedSprite());            
         }
      }

      @Override
      public void mouseReleased(MouseEvent arg0) {
      }
   }
   
   

}