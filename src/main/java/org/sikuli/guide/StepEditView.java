package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
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

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

import org.sikuli.ui.Slide;

import com.google.common.base.Predicate;


class StepEditKit {

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
         final int x = location.x - origin.x;
         final int y = location.y - origin.y;

         // skip if the insertion point is not within the 
         // visible area of the edit view
         if (!editView.getVisibleRect().contains(x,y)){
            return;         
         }

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

class StepEditView extends StepView {

   //SpriteView selectedSpriteView;

   JPanel controlLayer = new JPanel();
   JPanel editLayer = new JPanel();


   SelectionTool selectionTool = new SelectionTool();
   MoveTool moveTool = new MoveTool();
   EditTool editTool = new EditTool();  

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


   class Canvas extends JPanel{
      Canvas(){
         setOpaque(true);
         setBackground(Color.white);
         setBorder(BorderFactory.createLineBorder(new Color(0.4f,0.4f,0.4f)));
      }
   }

   public StepEditView() {

      initActionInputMaps();

      initComponents();

      setPreferredSize(new Dimension(800,600));

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

   @Override
   void initComponents() {
      super.initComponents();
      
      remove(contentLayer);
      remove(contextLayer);

      Canvas canvas = new Canvas();
      canvas.setSize(640,480);
      canvas.setLocation(40,40);
      
      contentLayer.setOpaque(false);
      contentLayer.setLayout(null);
      contentLayer.setSize(800,600);

      contextLayer.setOpaque(false);
      contextLayer.setLayout(null);
      contextLayer.setSize(800,600);
      
      controlLayer.setOpaque(false);
      controlLayer.setLayout(null);
      controlLayer.setSize(800,600);

      editLayer.setOpaque(false);
      editLayer.setLayout(null);
      editLayer.setSize(800,600);

      add(canvas,0);
      add(contextLayer,0);
      add(contentLayer,0);
      add(controlLayer,0);
      add(editLayer,0);      
      
      relationshipGroupView.setSize(editLayer.getSize());
      editLayer.add(relationshipGroupView);
   }

   private void addListenerToSprite(SpriteView spriteView){
      spriteView.addMouseListener(selectionTool);

      spriteView.addMouseListener(moveTool);           
      spriteView.addMouseMotionListener(moveTool);

      spriteView.addMouseListener(editTool);
   }

   private void addListenersToSprites(){
      // TODO: improve this
      for (Component comp : contentLayer.getComponents()){
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
               Rectangle b = new Rectangle(s.getX(), s.getY(), s.getWidth(), s.getHeight());               
               if (bounds == null)
                  bounds = b;
               else
                  bounds.add(b);
               
               // also each sprite's dependents
               List<Sprite> dependents = getStep().getDependentsOf(s);
               for (Sprite d : dependents){
                  bounds.add(((DefaultSprite) d).getBounds());
               }

            }            

            if (bounds != null)
               setBounds(bounds);

            Point o = bounds.getLocation();

            // add views
            removeAll();
            for (Sprite s : sprites){
               SpriteView v = ViewFactory.createView(s);
               v.setOffset(new Point(-o.x,-o.y));
               v.updateBounds();
               v.setOpacity(0.5f);
               add(v);
               
//               // also each sprite's dependents
               List<Sprite> dependents = getStep().getDependentsOf(s);
               for (Sprite d : dependents){
                  v = ViewFactory.createView(d);
                  v.setOffset(new Point(-o.x,-o.y));
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


   List<SpriteView> getSpriteViews(){
      Component[] comps = contentLayer.getComponents();
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
               Rectangle b = ((DefaultSprite) sprite).getBounds();
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
   class SelectionTool implements MouseListener {
      JList selectionList = new JList();
      public void addListSelectionListener(ListSelectionListener listener) {
         selectionList.addListSelectionListener(listener);
      }

      ControlBox controlBox = new ControlBox();
      ControlBoxView controlBoxView = new ControlBoxView(controlBox);

      Map<SpriteView, ControlBoxView> controlBoxes = new HashMap<SpriteView, ControlBoxView>();

//      ListSelectionModel getSelectionModel(){
//         return selectionList.addListSelectionListener(listener);
//      }
      
      SelectionTool(){
         selectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         //selectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         //selectionList.getSelectionModel().addListSelectionListener(relationshipGroupView);
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
      public void mouseClicked(MouseEvent arg0) {
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
      }

      @Override
      public void mouseExited(MouseEvent arg0) {
      }      

      @Override
      public void mouseReleased(MouseEvent arg0) {
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

      private void select(Sprite sprite){
         int index = _step.indexOf(sprite);
         selectionList.addSelectionInterval(index,index);
         ControlBox box = new ControlBox();
         ControlBoxView view = new ControlBoxView(box);
         box.setTarget(sprite);
         controlLayer.add(view);
         
         //relationshipGroupView.clear();
         //relationshipGroupView.add(sprite);
         //relationshipGroupView.setVisible(true);

         view.setVisible(true);
         view.repaint();
      }

      private void select(SpriteView spriteView) {
         select(spriteView.getSprite());
      }


      SpriteView getSpriteView(int index){
         Sprite sprite = _step.getSprites().get(index);
         for (SpriteView view : getSpriteViews()){
            if (view.getSprite().equals(sprite)){
               return view;
            }            
         }
         return null;
      }

      SpriteView getSelectedSpriteView() {
         int index = selectionList.getSelectedIndex();
         return getSpriteView(index);
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

   public void spritesPasted(List<Sprite> sprites) {
      selectionTool.clearSelection();
      for (Sprite sprite : sprites){
         _step.addSprite(sprite);      
         SpriteView spriteView = ViewFactory.createView(sprite);
         contentLayer.add(spriteView,0);
         addListenerToSprite(spriteView);
         selectionTool.select(spriteView);
      }
   }

   public void spriteCut(Sprite sprite) {
      _step.removeSprite(sprite);
      selectionTool.clearSelection();      
   }


   public Sprite getSelectedSprite() {
      return selectionTool.getSelectedSpriteView().getSprite();
   }





}