package org.sikuli.guide;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.sikuli.ui.Slide;

class StepEditView extends StepView {
 
   SpriteView selectedSpriteView;
   
   SelectionTool selectionTool = new SelectionTool();
   KeyTool keyTool = new KeyTool();
   ComponentDragMover cm = new ComponentDragMover();
   TextPropertyEditor _textPropertyEditor = new TextPropertyEditor();
   
   public StepEditView() {
      
      _controlBox = new ControlBox();
      _controlBoxView = new ControlBoxView(_controlBox);
      
      _textPropertyEditor.setVisible(false);
      _controlBoxView.setVisible(false);
      
      add(_controlBoxView,0);
      add(_textPropertyEditor,0);
   }
   
   private void addListenersToSprites(){
      for (Component comp : contentLayer.getComponents()){
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
   }
   
   @Override
   public void setSlide(Slide slide){
      super.setSlide(slide);
      addListenersToSprites();
   }
   
   @Override
   public void setStep(Step step) {
      super.setStep(step);
      addListenersToSprites();
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

   Sprite getSelectedSprite(){
      if (selectedSpriteView != null)
         return selectedSpriteView.getSprite();
      else
         return null;
   }
   
   private void clearSelection() {
      selectedSpriteView = null;            
      _controlBoxView.setVisible(false);
      _textPropertyEditor.setVisible(false);
      _textPropertyEditor.saveText();
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
            
            _textPropertyEditor.saveText();
            _textPropertyEditor.setVisible(false);
            
            if (e.getClickCount() == 2 && getSelectedSprite() instanceof Text){
               _textPropertyEditor.setVisible(true);
               Point loc = selectedSpriteView.getLocation();
               _textPropertyEditor.setLocation(loc.x, loc.y-30);
               _textPropertyEditor.setTextSprite((Text)getSelectedSprite());
               _textPropertyEditor.requestFocus();
               
               
            }
         }
      }

      @Override
      public void mouseReleased(MouseEvent arg0) {
      }
   }
   
   

}