package org.sikuli.guide;

import static com.google.common.collect.Iterables.any;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sikuli.ui.FileChooser;
import org.sikuli.ui.Slide;
import org.sikuli.ui.SlideEditView;

import com.google.common.base.Predicate;


class StepEditKit {
   
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

   static class ImportContentImageAction extends AbstractAction {
      
      final static String FILE = "file";
      
      ImportContentImageAction(String s){
         super(s);
      }
      
      
      private void scaleToFitCanvasSize(ContextImage contextImage, Dimension canvasSize){
         float scalex = 1f * contextImage.getOriginalWidth() / canvasSize.width;
         float scaley = 1f * contextImage.getOriginalHeight() / canvasSize.height;

         boolean isContextImageLargerThanCanvas = scalex > 1 || scaley > 1; 
            
         if (isContextImageLargerThanCanvas){
            
            float largerScale = Math.max(scalex, scaley);
            
            int width  = (int) (contextImage.getOriginalWidth() / largerScale);
            int height = (int) (contextImage.getOriginalHeight() / largerScale);
            
            contextImage.setWidth(width);
            contextImage.setHeight(height);
         }
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         StepEditView editView = (StepEditView) e.getSource();
         try {

            Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, editView);
            File imageFile = new FileChooser(frame).loadImage();
            if (imageFile == null)  // if the user cancel the file chooser
               return;  // do nothing, return
            
            ContextImage contextImage = new DefaultContextImage(imageFile);            
            
            
            Dimension canvasSize = editView.getCanvasSize();
            scaleToFitCanvasSize(contextImage, canvasSize);
            placeInCenterOfCanvas(contextImage, canvasSize);
            
            
            Step step = editView.getStep();
            step.addSprite(contextImage);
            
            // After an image is imported
            // only that newly inserted image is selected
            editView.selectionTool.clearSelection();
            editView.selectionTool.select(contextImage);
            
            
         } catch (IOException ignored) {
         }
         
      }


      private void placeInCenterOfCanvas(ContextImage contextImage,
            Dimension canvasSize) {
         contextImage.setX(canvasSize.width/2 - contextImage.getWidth()/2);
         contextImage.setY(canvasSize.height/2 - contextImage.getHeight()/2);         
      }
      
   }
   
   static Action importContextImageFromFileAction = new ImportContentImageAction("Import Context Image"){
      { 
         putValue(Action.NAME, "importContextImageFromFileAction");
         putValue(Action.ACTION_COMMAND_KEY, FILE);         
      }
   };

   
   static class InsertSpriteAction extends AbstractAction {

      final static String TARGET = "target";
      final static String TEXT = "text";
      final static String CIRCLE = "circle";
      final static String FLAG = "flag";

      InsertSpriteAction(String s){
         super(s);
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         StepEditView editView = (StepEditView) e.getSource();

         Point location = MouseInfo.getPointerInfo().getLocation();
         Point origin = editView.getLocationOnScreen();
         int x = location.x - origin.x;
         int y = location.y - origin.y;

         // skip if the insertion point is not within the 
         // visible area of the edit view
         if (!editView.getVisibleRect().contains(x,y)){
            return;         
         }
         
         // convert to location relative to the canvas
         x -= editView.canvas.getLocation().x;
         y -= editView.canvas.getLocation().y;

         Sprite sprite = null;
         if (e.getActionCommand().equals(TARGET)){       
            sprite = new DefaultTarget();
         }else if (e.getActionCommand().equals(TEXT)){
            sprite = new DefaultText("Text");
         }else if (e.getActionCommand().equals(FLAG)){
            sprite = new FlagText("Flag");
         }else if (e.getActionCommand().equals(CIRCLE)){
            sprite = new Circle(){
               {
                  setForeground(Color.red);
                  setWidth(100);
                  setHeight(100);
               }
            };
         }else{
            return;
         }

         sprite.setX(x - sprite.getWidth()/2);
         sprite.setY(y - sprite.getHeight()/2);

         editView.getStep().addSprite(sprite);
      }

   }

   static Action insertTargetAction = new InsertSpriteAction("Insert Target"){
      { 
         putValue(Action.NAME, "insertTargetAction");
         putValue(Action.ACTION_COMMAND_KEY, TARGET);         
      }
   };
   static Action insertTextAction = new InsertSpriteAction("Insert Target"){
      { 
         putValue(Action.NAME, "insertTextAction");
         putValue(Action.ACTION_COMMAND_KEY, TEXT);         
      }
   };
   static Action insertCircleAction = new InsertSpriteAction("Insert Circle"){
      { 
         putValue(Action.NAME, "insertCircleAction");
         putValue(Action.ACTION_COMMAND_KEY, CIRCLE);         
      }
   };
   static Action insertFlagTextAction = new InsertSpriteAction("Insert Flag"){
      { 
         putValue(Action.NAME, "insertFlagAction");
         putValue(Action.ACTION_COMMAND_KEY, FLAG);         
      }
   };


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

class StepEditView extends SlideEditView {

   Canvas canvas = new Canvas();
   JPanel overlayLayer = new JPanel();
   JPanel contextLayer = new JPanel();
   JPanel controlLayer = new JPanel();
   JPanel editLayer = new JPanel();


   SelectionTool selectionTool = new SelectionTool();
   CopyCutPasteTool copyCutPasteTool = new CopyCutPasteTool();
   MoveTool moveTool = new MoveTool();
   EditTool editTool = new EditTool();  
   
   Step _step;   

   private void initActionInputMaps(){
      ActionMap map = getActionMap();
      InputMap imap = getInputMap();

      map.put(StepEditKit.deleteSelectedSpritesAction.getValue(Action.NAME), StepEditKit.deleteSelectedSpritesAction);
      imap.put(KeyStroke.getKeyStroke("DELETE"), StepEditKit.deleteSelectedSpritesAction.getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("BACK_SPACE"), StepEditKit.deleteSelectedSpritesAction.getValue(Action.NAME));

      map.put(StepEditKit.moveSelectedSpritesUpAction.getValue(Action.NAME), StepEditKit.moveSelectedSpritesUpAction);
      map.put(StepEditKit.moveSelectedSpritesDownAction.getValue(Action.NAME), StepEditKit.moveSelectedSpritesDownAction);
      map.put(StepEditKit.moveSelectedSpritesLeftAction.getValue(Action.NAME), StepEditKit.moveSelectedSpritesLeftAction);
      map.put(StepEditKit.moveSelectedSpritesRightAction.getValue(Action.NAME), StepEditKit.moveSelectedSpritesRightAction);
      imap.put(KeyStroke.getKeyStroke("UP"), StepEditKit.moveSelectedSpritesUpAction.getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("DOWN"), StepEditKit.moveSelectedSpritesDownAction.getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("LEFT"), StepEditKit.moveSelectedSpritesLeftAction.getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("RIGHT"), StepEditKit.moveSelectedSpritesRightAction.getValue(Action.NAME));

      map.put(StepEditKit.importContextImageFromFileAction.getValue(Action.NAME), StepEditKit.importContextImageFromFileAction);
      imap.put(KeyStroke.getKeyStroke("meta I"), StepEditKit.importContextImageFromFileAction.getValue(Action.NAME));

      map.put(StepEditKit.toogleOverlayVisibilityAction.getValue(Action.NAME), StepEditKit.toogleOverlayVisibilityAction);
      imap.put(KeyStroke.getKeyStroke("ctrl 1"), StepEditKit.toogleOverlayVisibilityAction.getValue(Action.NAME));
      map.put(StepEditKit.toogleContextVisibilityAction.getValue(Action.NAME), StepEditKit.toogleContextVisibilityAction);
      imap.put(KeyStroke.getKeyStroke("ctrl 2"), StepEditKit.toogleContextVisibilityAction.getValue(Action.NAME));

      
      map.put(StepEditKit.linkSelectedSpritesAction.getValue(Action.NAME), StepEditKit.linkSelectedSpritesAction);
      imap.put(KeyStroke.getKeyStroke("meta L"), StepEditKit.linkSelectedSpritesAction.getValue(Action.NAME));
      map.put(StepEditKit.unlinkSelectedSpritesAction.getValue(Action.NAME), StepEditKit.unlinkSelectedSpritesAction);
      imap.put(KeyStroke.getKeyStroke("meta K"), StepEditKit.unlinkSelectedSpritesAction.getValue(Action.NAME));

      map.put(StepEditKit.selectAllAction.getValue(Action.NAME), StepEditKit.selectAllAction);
      map.put(StepEditKit.selectNoneAction.getValue(Action.NAME), StepEditKit.selectNoneAction);
      imap.put(KeyStroke.getKeyStroke("meta A"), StepEditKit.selectAllAction.getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("ESCAPE"), StepEditKit.selectNoneAction.getValue(Action.NAME));


      map.put(StepEditKit.insertTargetAction.getValue(Action.NAME), StepEditKit.insertTargetAction);
      imap.put(KeyStroke.getKeyStroke("meta 1"), StepEditKit.insertTargetAction.getValue(Action.NAME));
      map.put(StepEditKit.insertTextAction.getValue(Action.NAME), StepEditKit.insertTextAction);
      imap.put(KeyStroke.getKeyStroke("meta 2"), StepEditKit.insertTextAction.getValue(Action.NAME));
      map.put(StepEditKit.insertCircleAction.getValue(Action.NAME), StepEditKit.insertCircleAction);
      imap.put(KeyStroke.getKeyStroke("meta 3"), StepEditKit.insertCircleAction.getValue(Action.NAME));
      map.put(StepEditKit.insertFlagTextAction.getValue(Action.NAME), StepEditKit.insertFlagTextAction);
      imap.put(KeyStroke.getKeyStroke("meta 4"), StepEditKit.insertFlagTextAction.getValue(Action.NAME));


      map.put(TransferHandler.getCutAction().getValue(Action.NAME),
            TransferHandler.getCutAction());
      map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
            TransferHandler.getCopyAction());
      map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
            TransferHandler.getPasteAction());
      imap.put(KeyStroke.getKeyStroke("meta X"),
            TransferHandler.getCutAction().getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("meta C"),
            TransferHandler.getCopyAction().getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("meta V"),
            TransferHandler.getPasteAction().getValue(Action.NAME));


   }


   public Dimension getCanvasSize() {
      return canvas.getSize();
   }


   class Canvas extends JPanel{
      Canvas(){
         setOpaque(true);
         setBackground(Color.white);
         setFocusedStyle(false);
      }
      
      void setBorderColor(Color color){
         setBorder(BorderFactory.createLineBorder(color));
      }
      
      void setFocusedStyle(boolean isFocused){
         if (isFocused){
            setBorderColor(new Color(0.3f,0.3f,0.3f));
         }else{
            setBorderColor(new Color(0.8f,0.8f,0.8f));
         }
      }
   }

   public StepEditView() {

      initActionInputMaps();
      initComponents();

      setLayout(null);      
      //setBackground(null);  
      //setPreferredSize(new Dimension(800,600));
      setMinimumSize(new Dimension(720,520));

      setFocusable(true);

      addMouseListener(new MouseAdapter(){         
         public void mousePressed(MouseEvent e){
            requestFocus();
            // this allows the focus to clear when users click
            // on empty area (not occupied by any sprite)
            selectionTool.clearSelection();
         }         
      });

   }
   
   
   

   void initComponents() {
      
      setOpaque(true);
      
      addFocusListener(new FocusAdapter(){

         @Override
         public void focusGained(FocusEvent e) {
            canvas.setFocusedStyle(true);
            StepEditView.this.repaint();
         }

         @Override
         public void focusLost(FocusEvent e) {
            canvas.setFocusedStyle(false);
            StepEditView.this.repaint();
         }
         
      });
      
      addComponentListener(new ComponentAdapter(){
         @Override
         public void componentResized(ComponentEvent arg0) {
            Dimension size = getSize();            
            // center the canvas in the container
            canvas.setLocation(size.width/2-canvas.getSize().width/2,size.height/2-canvas.getSize().height/2);
            overlayLayer.setSize(size);
            contextLayer.setSize(size);
            controlLayer.setSize(size);
            editLayer.setSize(size);
            
            relationshipGroupView.setSize(editLayer.getSize());
            
            for (SpriteView spriteView : getSpriteViews()){
               Point o = canvas.getLocation();
               spriteView.setOffset(o);
            }
            for (Component comp : controlLayer.getComponents()){
               Point o = canvas.getLocation();
               ((ControlBoxView) comp).setOffset(o);
               ((ControlBoxView) comp).updateTarget();
            }
            for (Component comp : editLayer.getComponents()){
               Point o = canvas.getLocation();
               if (comp instanceof RelationshipGroupView){
                  ((RelationshipGroupView) comp).updateSelectedSprites();
               }
            }            
            
         }
      });
      
      canvas.setSize(700,500);
      
      overlayLayer.setOpaque(false);
      overlayLayer.setLayout(null);

      contextLayer.setOpaque(false);
      contextLayer.setLayout(null);
      
      controlLayer.setOpaque(false);
      controlLayer.setLayout(null);

      editLayer.setOpaque(false);
      editLayer.setLayout(null);

      add(canvas,0);
      add(contextLayer,0);
      add(overlayLayer,0);
      add(controlLayer,0);
      add(editLayer,0);      
      
      relationshipGroupView.setSize(editLayer.getSize());
      editLayer.add(relationshipGroupView);   
      
      
      setTransferHandler(new SpriteTransferHandler());
   }

   private void addListenerToSprite(SpriteView spriteView){
      spriteView.addMouseListener(selectionTool);

      spriteView.addMouseListener(moveTool);           
      spriteView.addMouseMotionListener(moveTool);

      spriteView.addMouseListener(editTool);
   }

   private void addListenersToSprites(){
      // TODO: improve this
      for (Component comp : overlayLayer.getComponents()){
         if (comp instanceof SpriteView){
            SpriteView spriteView = (SpriteView) comp;
            addListenerToSprite(spriteView);
         }
      }
      for (Component comp : contextLayer.getComponents()){
         if (comp instanceof SpriteView){
            SpriteView spriteView = (SpriteView) comp;
            addListenerToSprite(spriteView);
         }
      }
   }

   @Override
   public void setSlide(Slide slide){
      super.setSlide(slide);
      _step = (Step) slide;
      refresh();
      
      if (slide != null){
         addListenersToSprites();
         selectionTool.setListModel(_step._spriteList);
         selectionTool.clearSelection();
         validate();
      }
   }

   public Step getStep(){
      return _step;
   }
   
   SpriteView addView(Sprite sprite) {
      
      SpriteView spriteView = ViewFactory.createView(sprite);
      spriteView.setOffset(canvas.getLocation());     
      spriteView.updateBounds();
      
      if (sprite instanceof ContextImage){
         contextLayer.add(spriteView,0);
      }else{
         overlayLayer.add(spriteView,0);
      }
      addListenerToSprite(spriteView);
      spriteToSpriteView.put(sprite, spriteView);
      return spriteView;
   }

   
   void updateStep(){
      contextLayer.removeAll();
      overlayLayer.removeAll();
      spriteToSpriteView.clear();

      if (_step == null)
         return;
      
      for (Sprite sprite : _step.getSprites()){       
         
         addView(sprite);
//         SpriteView view = ViewFactory.createView(sprite);
//         
//         view.setOffset(canvas.getLocation());
//         view.updateBounds();
//         
//         if (sprite instanceof ContextImage){
//            contextLayer.add(view,0);
//         }else{
//            overlayLayer.add(view,0);
//         }
//                  
//         addListenerToSprite(view);
//         spriteToSpriteView.put(sprite, view);
      }   
      
      for (Relationship relationship : _step.getRelationships()){
         relationship.update(null);
      }
      
      validate();
   }
   
   public void setStep(Step step) {
      _step = step;
      setSlide(step);
      updateStep();

//      addListenersToSprites();
      selectionTool.setListModel(_step._spriteList);
      selectionTool.clearSelection();
      validate();
   }

   @Override
   public void refresh(){
      super.refresh();
      updateStep();
   }


   public StepEditView(Step step) {
      this();
      setStep(step);
   }

   ControlBox _controlBox;
   ControlBoxView _controlBoxView;

   class EditTool extends MouseAdapter {

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
   }

   class MoveTool extends MouseAdapter {
      Proxy proxy = new Proxy();

      MoveTool(){         
         editLayer.add(proxy);
      }

      void moveSelectedSpritesByOffset(int dx, int dy){

         for (Sprite sprite : selectionTool.getSelectedSprites()){

            final Sprite fSprite = sprite;

            List<Relationship> relationshipsThatInvolveThisSprite = getStep().getRelationships(sprite);
            boolean willSpriteBeMovedByTargetViaRelationship = any(relationshipsThatInvolveThisSprite, new Predicate<Relationship>(){

               @Override
               public boolean apply(Relationship relationship) {
                  boolean isSpriteLinkedToATarget = relationship.getDependent() == fSprite && relationship.getParent() instanceof Target;                  
                  boolean isTargetAlsoSelected = selectionTool.isSelected(relationship.getParent());
                  return isSpriteLinkedToATarget && isTargetAlsoSelected;
               }

            });

            if (!willSpriteBeMovedByTargetViaRelationship){               
               sprite.setX(sprite.getX()+dx);
               sprite.setY(sprite.getY()+dy);
            }            
         }
      }

      ComponentDragMover cm;
      class Proxy extends JPanel {

         Proxy() {
            setOpaque(true);
            setLayout(null);
            setBackground(new Color(0,0,0,0.1f));
            cm = new ComponentDragMover();
            cm.registerComponent(this);
            cm.addMoveListener(new DraggedMoveListener(){

               @Override
               public void componentMoved(Component source, Point origin,
                     Point destination) {
                  proxy.setVisible(false);                  
                  int dx = destination.x - origin.x;
                  int dy = destination.y - origin.y;
                  moveSelectedSpritesByOffset(dx,dy);
               }

            });
         }

//         Sprite target;
//         void setMoveTarget(Sprite sprite){
//            target = sprite;
//            
//            
//            
//         }

         void setMoveTargets(List<Sprite> sprites){

            Rectangle bounds = null;
            for (Sprite s : sprites){
               Rectangle b = getSpriteView(s).getBounds();// Rectangle(s.getX(), s.getY(), s.getWidth(), s.getHeight());
               
               if (bounds == null)
                  bounds = b;
               else
                  bounds.add(b);
               
               // also each sprite's dependents
               List<Sprite> dependents = getStep().getDependentsOf(s);
               for (Sprite d : dependents){
                  bounds.add(getSpriteView(d).getBounds());
               }

            }            

            if (bounds != null)
               setBounds(bounds);

            Point o = bounds.getLocation();
            Point loc = canvas.getLocation();
            
            // add views
            removeAll();
            for (Sprite s : sprites){
               SpriteView v = ViewFactory.createView(s);
               
               Point o1;
               o1 = new Point(-o.x,-o.y);
               o1.x += loc.x;
               o1.y += loc.y;
               
               v.setOffset(o1);
               v.updateBounds();
               v.setOpacity(0.5f);
               add(v);
               
//               // also each sprite's dependents
               List<Sprite> dependents = getStep().getDependentsOf(s);
               for (Sprite d : dependents){
                  v = ViewFactory.createView(d);
                  
                  o1 = new Point(-o.x,-o.y);
                  o1.x += loc.x;
                  o1.y += loc.y;

                  v.setOffset(o1);
                  v.updateBounds();
                  v.setOpacity(0.5f);
                  add(v);
               }

            }     
            
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

            //SpriteView spriteView = (SpriteView) e.getSource();
            //proxy.setBounds(spriteView.getBounds());         
            //proxy.setMoveTarget(spriteView.getSprite());
            proxy.setMoveTargets(selectionTool.getSelectedSprites());

            // transfer mouse control to proxy
            e.setSource(proxy);
            cm.mousePressed(e);
         }
      }



   }

   SpriteView getSpriteView(int index){
      Sprite sprite = _step.getSprites().get(index);
      return getSpriteView(sprite);
   }

   List<SpriteView> getSpriteViews(){
      Component[] comps = overlayLayer.getComponents();
      List<SpriteView> ret = new ArrayList<SpriteView>();
      for (int i = 0; i < comps.length; ++i){
         ret.add((SpriteView)comps[i]);
      }
      comps = contextLayer.getComponents();
      for (int i = 0; i < comps.length; ++i){
         ret.add((SpriteView)comps[i]);
      }         
      return ret;
   }
   
   Map<Sprite, SpriteView> spriteToSpriteView = new HashMap<Sprite, SpriteView>();
   SpriteView getSpriteView(Sprite sprite){
      return spriteToSpriteView.get(sprite);
   }

   
   class RelationshipGroupView extends JPanel implements  
   ChangeListener, ListSelectionListener {
      
      RelationshipGroupView(){
         setOpaque(false);
         setLayout(null);
         selectionTool.addListSelectionListener(this);
      }
      
      class RelationshipGroup extends JPanel implements PropertyChangeListener{
         
         List<Sprite> sprites = new ArrayList<Sprite>();      
         RelationshipGroup(Sprite sprite){
            setOpaque(true);            
            setBackground(new Color(0,1,0,0.1f));
            setBorder(BorderFactory.createLineBorder(Color.white));
            sprites = collectRelatedSprites(sprite); 
            for (Sprite s : sprites){
               s.addPropertyChangeListener(this);
            }
            updateBounds();
         }
                  
         // allows updates when sprites are moved
         @Override
         public void propertyChange(PropertyChangeEvent arg0) {
            updateBounds();
         }

         void updateBounds(){
            Rectangle bounds = null;
            
            // do not should highlight if the group only has an item
            if (sprites.size() == 1){
               setVisible(false);
               return;
            }else{
               setVisible(true);
            }            
            
            for (Sprite sprite : sprites){               
               // needs to be notified when a sprite is deleted
               if (getSpriteView(sprite)==null)
                  continue;
               
               Rectangle b = getSpriteView(sprite).getBounds();
               if (bounds == null){
                  bounds = b;
               }else{
                  bounds.add(b);
               }
            }
            
            bounds.grow(3,3);
            System.out.println("setting bounds to " + bounds);

            setBounds(bounds);
            RelationshipGroupView.this.repaint();
         }
      }
      
      List<Sprite> collectRelatedSprites(Sprite root){
         List<Sprite> accumulator = new ArrayList<Sprite>();         
         accumulator.add(root);
         collectRelatedSpritesHelper(root, accumulator);
         return accumulator;
      }

      void collectRelatedSpritesHelper(Sprite root, List<Sprite> accumulator){
         for (Relationship rel : getStep().getRelationships()){
            Sprite nextNode = null;
            if (root == rel.getParent()){
               nextNode = rel.getDependent();
            }else if (root == rel.getDependent()){
               nextNode = rel.getParent();
            }

            if (nextNode != null && !accumulator.contains(nextNode)){
               accumulator.add(nextNode);
               collectRelatedSpritesHelper(nextNode, accumulator);
            }
         }  
      }

  
      
      List<RelationshipGroup> groups = new ArrayList<RelationshipGroup>(); 
      boolean isAlreadyInAGroup(Sprite sprite){
         for (RelationshipGroup group : groups){
            if (group.sprites.contains(sprite))
               return true;
         }
         return false;
      }     
      
      void updateSelectedSprites(){
         clear();
         if (getStep()!=null)
            getStep().addChangeListener(this);

         List<Sprite> sprites = selectionTool.getSelectedSprites();
         for (Sprite sprite : sprites){            
            if (!isAlreadyInAGroup(sprite)){               
               RelationshipGroup g = new RelationshipGroup(sprite);
               groups.add(g);
               add(g);  
            }
         }
         
      }
      
      void clear(){
         groups.clear();
         removeAll();
         if (getStep()!=null)
            getStep().removeChangeListener(this);
      }
      
      // allows update when sprites are linked/unlinked
      @Override
      public void stateChanged(ChangeEvent e) {
         if (e.getSource() == getStep()){
            updateSelectedSprites();
         }         
      }
      
      
      // allows update when sprites are selected/unselected
      @Override
      public void valueChanged(ListSelectionEvent e) {
         System.out.println("slection changed");
         updateSelectedSprites();
      }
      
      
      
   }
   
   RelationshipGroupView relationshipGroupView = new RelationshipGroupView();
   class SelectionTool extends MouseAdapter {
      JList selectionList = new JList();
      public void addListSelectionListener(ListSelectionListener listener) {
         selectionList.addListSelectionListener(listener);
      }

      Map<SpriteView, ControlBoxView> controlBoxes = new HashMap<SpriteView, ControlBoxView>();

      SelectionTool(){
         selectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         //selectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      }

      public void selectAll() {
         for (Sprite sprite : _step.getSprites()){
            select(sprite);
         }
      }

      void setListModel(ListModel listModel){
         selectionList.setModel(listModel);
      }

      void clearSelection(){
         selectionList.clearSelection();
         controlLayer.removeAll();
         editTool.endEdit();
         relationshipGroupView.clear();
         repaint();
      }

      List<SpriteView> getSelectedSpriteViews(){
         int[] indices = selectionList.getSelectedIndices();
         List<SpriteView> ret = new ArrayList<SpriteView>();
         for (int i = 0; i < indices.length; ++i){
            ret.add(getSpriteView(indices[i]));
         }         
         return ret;
      }

      List<Sprite> getSelectedSprites(){
         int[] indices = selectionList.getSelectedIndices();
         List<Sprite> ret = new ArrayList<Sprite>();
         for (int i = 0; i < indices.length; ++i){
            ret.add(getStep().getSprites().get(indices[i]));
         }         
         return ret;
      }

      @Override
      public void mousePressed(MouseEvent e) {
         // this allows the editor to gain focus when
         // any sprite is clicked on (must be a better way
         // to achieve this).
         requestFocus();

         if (e.getSource() instanceof SpriteView){
            SpriteView spriteView = (SpriteView) e.getSource();

            if (e.getClickCount() == 1){

               if (!e.isMetaDown() &&  // click on a non-selected element without ctrl/meta down would deselect all previous selections
                     !isSelected(spriteView.getSprite())){ // but click on a selected element would not deselect others
                  clearSelection();
               }

               if (isSelected(spriteView.getSprite())){
                  // try to select the sprite underneath

                  // TODO: enable selection of sprits underneath other
                  // transparent sprites

                  //                  Point p0 = e.getLocationOnScreen();
                  //                  Point p1 = e.getPoint();
                  //                 
                  //                  // find the view below
                  //                  //List<SpriteView> spriteViews = 
                  //                     
                  //                  for (SpriteView otherSpriteView : getSpriteViews()){                  
                  //                     if (otherSpriteView == spriteView) 
                  //                        continue;
                  //                     if (otherSpriteView.isOpaque())
                  //                        continue;
                  //                     if (otherSpriteView instanceof ContextImage)
                  //                        continue;
                  //
                  //                     
                  //                     Point o = spriteView.getLocation();
                  //                     Point p = e.getPoint();
                  //                     Point q = otherSpriteView.getLocation();
                  //                     
                  //                     Point r = new Point();
                  //                     r.x = o.x + p.x - q.x;
                  //                     r.y = o.y + p.y - q.y;
                  //                    
                  //                     if (otherSpriteView.contains(r)){
                  //                        clearSelection();
                  //                        select(otherSpriteView);
                  //                     }
                  //
                  //                  }

               }else{
                  select(spriteView);
               }
            }

         }
      }

      private boolean isSelected(Sprite sprite){
         int index = _step.indexOf(sprite);
         return selectionList.isSelectedIndex(index);
      }

      void select(Sprite sprite){
         int index = _step.indexOf(sprite);
         selectionList.addSelectionInterval(index,index);
         
         ControlBoxView box = new ControlBoxView();
         controlLayer.add(box);
         
         box.setOffset(getSpriteView(sprite).getOffset());         
         box.setTarget(sprite);         
         box.setVisible(true);
         box.repaint();
      }

      private void select(SpriteView spriteView) {
         select(spriteView.getSprite());
      }


      SpriteView getSelectedSpriteView() {
         int index = selectionList.getSelectedIndex();
         return getSpriteView(index);
      }


   }

   Story getStory(){
      if (getStoryEditor()!=null){
         return getStoryEditor().getStory();
      }
      return null;      
   }
   
   StoryEditor getStoryEditor(){
      Object obj = SwingUtilities.getAncestorOfClass(StoryEditor.class, this);
      if (obj != null)
         return (StoryEditor) obj;
      else
         return null;
   }
   
   
   class CopyCutPasteTool{
      

      List<ContextImage> cachedContextImages = new ArrayList<ContextImage>();
            
      private boolean loadBufferedImageFromExistingImages(DefaultContextImage contextImage){
         Story story = getStory();
         
         ContextImage existingImageWithSameImageId = null;
         
         existingImageWithSameImageId = story.getContextImage(contextImage.getImageId());

         if (existingImageWithSameImageId == null){            
            
            for (ContextImage cutContextImage : cachedContextImages){
               boolean isCutContextImageSameAsPasteContextImage = cutContextImage.getImageId().equals(contextImage.getImageId());  
               if (isCutContextImageSameAsPasteContextImage){
                  existingImageWithSameImageId = cutContextImage;
                  break;
               }                  
            }                 
         }

         if (existingImageWithSameImageId != null){
            contextImage.image = existingImageWithSameImageId.getBufferedImage();
            return true;
         }else
            return false;
      }
    
      
      void pasteSprites(List<Sprite> listSpriteToPaste) {
         selectionTool.clearSelection();
         for (Sprite spriteToPaste : listSpriteToPaste){

            if (spriteToPaste instanceof DefaultContextImage){
               DefaultContextImage contextImageToPaste = (DefaultContextImage) spriteToPaste;

               // before adding to the step, try to load the buffered image from an
               // existing context image with the same image id
               if (!loadBufferedImageFromExistingImages(contextImageToPaste))
                  continue;
            }
            
            getStep().addSprite(spriteToPaste);                  
            SpriteView spriteView = addView(spriteToPaste);
            selectionTool.select(spriteView);
         }
      }

      
      void copySprites(List<Sprite> sprites) {      
         cachedContextImages.clear();
         for (Sprite sprite : sprites){
            if (sprite instanceof ContextImage){
               cachedContextImages.add((ContextImage) sprite);
            }      
         }
      }


      void cutSprites(List<Sprite> sprites) {      
         cachedContextImages.clear();
         for (Sprite sprite : sprites){
            _step.removeSprite(sprite);
            selectionTool.clearSelection();
            if (sprite instanceof ContextImage){
               cachedContextImages.add((ContextImage) sprite);
            }      
         }
      }
   }

   public Sprite getSelectedSprite() {
      return selectionTool.getSelectedSpriteView().getSprite();
   }


}