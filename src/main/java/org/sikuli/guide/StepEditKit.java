package org.sikuli.guide;

import java.awt.Color;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import org.sikuli.ui.FileChooser;

public class StepEditKit {
   
   static public final String insertTextAction = "insertTextAction";
   static public final String insertTargetAction = "insertTargetAction";
   static public final String insertCircleAction = "insertCircleAction";
   static public final String insertFlagTextAction = "insertFlagTextAction";
   

   static class ToogleLayerVisibilityAction extends AbstractAction {
      final static String CONTEXT = "context";
      final static String OVERLAY = "overlay";

      ToogleLayerVisibilityAction(String s){
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         StepEditView editView = (StepEditView) e.getSource();

         if (e.getActionCommand().equals(OVERLAY)){
            editView.overlayLayer.setVisible(!editView.overlayLayer.isVisible());
         }else if (e.getActionCommand().equals(CONTEXT)){
            editView.contextLayer.setVisible(!editView.contextLayer.isVisible());
         }
      }

   }

   static Action toogleOverlayVisibilityAction  =       
      new ToogleLayerVisibilityAction("Overlay"){
      {
         putValue(Action.NAME, "toogleOverlayVisibilityAction");
         putValue(Action.ACTION_COMMAND_KEY, OVERLAY);
      }
   };

   static Action toogleContextVisibilityAction  =       
      new ToogleLayerVisibilityAction("Context"){
      {
         putValue(Action.NAME, "toogleContextVisibilityAction");
         putValue(Action.ACTION_COMMAND_KEY, CONTEXT);
      }
   };

   static class LinkAction extends AbstractAction {

      final static String LINK = "link";
      final static String UNLINK = "unlink";


      LinkAction(String s){
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         StepEditView editView = (StepEditView) e.getSource();
         Step step = editView.getStep();
         List<Sprite> sprites = editView.selectionTool.getSelectedSprites();

         if (e.getActionCommand().equals(LINK)){

            // find Target
            int count=0;
            Target target = null;
            for (Sprite sprite : sprites){
               if (sprite instanceof Target){
                  target = (Target) sprite;
                  count++; 
               }
            }

            // must be exactly one target
            if (count != 1)
               return;

            // link everyone else to this target
            for (Sprite sprite : sprites){
               if (sprite == target) 
                  continue;   // don't link target to itself

               if (sprite instanceof ContextImage)
                  continue;   // don't link a context image to the target

               // remove from all its existing relationships
               step.removeRelationships(sprite);

               System.out.println("linking:" + count);
               Relationship r = new OffsetRelationship(target, sprite);
               step.addRelationship(r);
            }

         }else if (e.getActionCommand().equals(UNLINK)){

            for (Sprite sprite : sprites){
               step.removeRelationships(sprite);
            }
         }
      }

   }

   static Action linkSelectedSpritesAction  =       
      new LinkAction("Link"){
      {
         putValue(Action.NAME, "linkSelectedSpritesAction");
         putValue(Action.ACTION_COMMAND_KEY, LINK);
      }
   };

   static Action unlinkSelectedSpritesAction  =       
      new LinkAction("UlLink"){
      {
         putValue(Action.NAME, "unlinkSelectedSpritesAction");
         putValue(Action.ACTION_COMMAND_KEY, UNLINK);                  

      }
   };


   static class SelectionAction extends AbstractAction {

      final static String ALL = "all";
      final static String NONE = "none";

      SelectionAction(String s){
         super(s);
      }

      public void actionPerformed(ActionEvent e) {
         StepEditView editView = (StepEditView) e.getSource();

         if (e.getActionCommand().equals(ALL)){
            editView.selectionTool.selectAll();
         }else if (e.getActionCommand().equals(NONE)){
            editView.selectionTool.clearSelection();
         }
      }

   }

   static Action selectAllAction  =       
      new SelectionAction("Select All"){
      {
         putValue(Action.NAME, "selectAllAction");
         putValue(Action.ACTION_COMMAND_KEY, ALL);                  
      }
   };

   static Action selectNoneAction  =       
      new SelectionAction("Select None"){
      {
         putValue(Action.NAME, "selectNoneAction");
         putValue(Action.ACTION_COMMAND_KEY, NONE);                           
      }
   };


   static Action deleteSelectedSpritesAction =       
      new AbstractAction("Delete"){

      {
         putValue(Action.NAME, "DeleteSelectedSpritesAction");
      }

      @Override
      public void actionPerformed(ActionEvent e) {

         StepEditView editView = (StepEditView) e.getSource();
         Step step = editView.getStep();            
         List<SpriteView> spriteViews = editView.selectionTool.getSelectedSpriteViews();

         for (SpriteView spriteView : spriteViews){               
            step.removeSprite(spriteView.getSprite());
         }
         editView.updateStep();
         editView.selectionTool.clearSelection();
      }
   };

   static class ImportContextImageAction extends AbstractAction {

      final static String FILE = "file";

      ImportContextImageAction(String s){
         super(s);
      }




      @Override
      public void actionPerformed(ActionEvent e) {
         StepEditView editView = (StepEditView) e.getSource();
         try {

            Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, editView);
            File imageFile = new FileChooser(frame).loadImage();
            if (imageFile == null)  // if the user cancel the file chooser
               return;  // do nothing, return

            editView.editTool.importContextImage(imageFile);

         } catch (IOException ignored) {
         }

      }



   }

   static Action importContextImageFromFileAction = new ImportContextImageAction("Import Context Image"){
      { 
         putValue(Action.NAME, "importContextImageFromFileAction");
         putValue(Action.ACTION_COMMAND_KEY, FILE);         
      }
   };


   static class InsertSpriteAction extends ComponentAction<StepEditView> {

      final static String TARGET = "target";
      final static String TEXT = "text";
      final static String CIRCLE = "circle";
      final static String FLAG = "flag";
      
      String type;
      InsertSpriteAction(String s, String type){
         super(s, StepEditView.class);
         this.type = type;
      }

      @Override
      protected void execute(StepEditView editView) {
         //StepEditView editView = getComponent(e);         
         System.out.println("Insert action executed for type "  + type);
         Point location = MouseInfo.getPointerInfo().getLocation();
         Point origin = editView.getLocationOnScreen();
         int x = location.x - origin.x;
         int y = location.y - origin.y;

         // skip if the insertion point is not within the 
         // visible area of the edit view
         if (!editView.getVisibleRect().contains(x,y)){
            // if so, insert in the center of the view
            x = editView.getWidth()/2;
            y = editView.getHeight()/2;
         }

         // convert to location relative to the canvas
         x -= editView.canvas.getLocation().x;
         y -= editView.canvas.getLocation().y;
         
         Sprite sprite = null;
         if (type.equals(TARGET)){       
            sprite = new DefaultTarget();
         }else if (type.equals(TEXT)){
            sprite = new DefaultText("Text");
         }else if (type.equals(FLAG)){
            sprite = new FlagText("Flag");
         }else if (type.equals(CIRCLE)){
            Circle circle  = new Circle();
            circle.setForeground(Color.red);
            circle.setWidth(100);
            circle.setHeight(100);
            sprite = circle;
         }else{
            return;
         }

         sprite.setX(x - sprite.getWidth()/2);
         sprite.setY(y - sprite.getHeight()/2);

         editView.getStep().addSprite(sprite);
      }

   }
   
   static Action[] getActions(){
      return new Action[]{
            getInsertTargetAction(),
            getInsertTextAction(),
            getInsertCircleAction(),
            getInsertFlagTextAction()
      };
   }

   public static Action getInsertTargetAction() { 
      return new InsertSpriteAction(insertTargetAction, InsertSpriteAction.TARGET);
   }

   public static Action getInsertTextAction() { 
      return new InsertSpriteAction(insertTextAction,InsertSpriteAction.TEXT);
   }
   
   public static Action getInsertCircleAction() {
      return new InsertSpriteAction(insertCircleAction, InsertSpriteAction.CIRCLE);
   }

   public static Action getInsertFlagTextAction() {
      return new InsertSpriteAction(insertFlagTextAction, InsertSpriteAction.FLAG);
   }


   static class MoveSelectedSpriteAction extends AbstractAction {

      final static String DOWN = "down";
      final static String UP = "up";
      final static String LEFT = "left";
      final static String RIGHT = "right";

      MoveSelectedSpriteAction(String s){
         super(s);
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         StepEditView editView = (StepEditView) e.getSource();
         if (e.getActionCommand().equals(UP)){
            editView.moveTool.moveSelectedSpritesByOffset(0,-10);
         }else if (e.getActionCommand().equals(DOWN)){
            editView.moveTool.moveSelectedSpritesByOffset(0,10);
         }else if (e.getActionCommand().equals(LEFT)){
            editView.moveTool.moveSelectedSpritesByOffset(-10,0);
         }else if (e.getActionCommand().equals(RIGHT)){
            editView.moveTool.moveSelectedSpritesByOffset(10,0);
         }         
      }
   }

   static Action moveSelectedSpritesUpAction =       
      new MoveSelectedSpriteAction("Move Up"){
      {
         putValue(Action.NAME, "moveSelectedSpritesUpAction");
         putValue(Action.ACTION_COMMAND_KEY, UP);         
      }
   };   

   static Action moveSelectedSpritesDownAction =       
      new MoveSelectedSpriteAction("Move Down"){
      {
         putValue(Action.NAME, "moveSelectedSpritesDownAction");
         putValue(Action.ACTION_COMMAND_KEY, DOWN);
      }
   };

   static Action moveSelectedSpritesRightAction =       
      new MoveSelectedSpriteAction("Move Right"){
      {
         putValue(Action.NAME, "moveSelectedSpritesRightAction");
         putValue(Action.ACTION_COMMAND_KEY, RIGHT);
      }
   };   

   static Action moveSelectedSpritesLeftAction =       
      new MoveSelectedSpriteAction("Move Left"){
      {
         putValue(Action.NAME, "moveSelectedSpritesLeftAction");
         putValue(Action.ACTION_COMMAND_KEY, LEFT);         
      }
   };   
}