package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.sikuli.ui.BundleableDocument;
import org.sikuli.ui.BundleableDocumentOwner;
import org.sikuli.ui.Bundler;
import org.sikuli.ui.SlideDeckEditor;

// Save/Load contents to a bundle folder including xml and images
public class StoryEditor extends SlideDeckEditor 
   implements BundleableDocumentOwner {

   
   public static class EditorAction extends AbstractAction {
      
      public EditorAction(String s) {
         super(s);
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         //System.out.println("editor action triggered");
      }
      
      // return the one that currently has focus
      public StoryEditor getFocusedComponent(){
         Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
         if (comp instanceof StoryEditor)
            return (StoryEditor) comp;
         return null;
      }
      
      // return the one as the action source, or if not possible, the one that most recently has focus
      public StoryEditor getComponent(ActionEvent e){
         if (e.getSource() instanceof StoryEditor){
            return (StoryEditor) e.getSource();
         }else {      
            return StoryEditor.getFocusedComponent();
         }
      }
   }
   
   private static class ActionSupport{      
      StoryEditor _mostRecentlyFocusedInstance = null;
      void register(final StoryEditor editor){
         editor.addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent arg0) {
               _mostRecentlyFocusedInstance = editor;               
            }

            @Override
            public void focusLost(FocusEvent arg0) {
               //_mostRecentlyFocusedInstance = null;               
            }            
         });         
      }
   }
   private static ActionSupport _actionSupport = new ActionSupport();
   private static StoryEditor getFocusedComponent(){
      return _actionSupport._mostRecentlyFocusedInstance;
   }
   
   class LastFocusOwnerTracker implements FocusListener{
      Component _lastFocusOwner;

      @Override
      public void focusGained(FocusEvent e) {
         _lastFocusOwner = e.getComponent();
      }

      @Override
      public void focusLost(FocusEvent e) {
      }
      
      Component getLastFocusOwner(){
         return _lastFocusOwner;
      }
   }   
   LastFocusOwnerTracker _lastFocusOwnerTracker = new LastFocusOwnerTracker();
   
   public StoryEditor() {
      super();
      
      ComponentActionSupport.register(this);
      
      initActionInputMap();
      setFocusable(true);
      
      setStory(new Story());
          

      StoryListView listView = new StoryListView();
      setSlideDeckListView(listView);
      listView.setTransferHandler(new SpriteTransferHandler());
      listView.setDragEnabled(false);

      StepEditView editView = new StepEditView();      
      editView.setPreferredSize(new Dimension(800,600));
      editView.setMinimumSize(new Dimension(800,600));
      setSlideEditView(editView);
      
      // these allow StoryEditor to remember which view mostly recently had focus
      // so that an action invoked by another component can be re-directed to the
      // right component
      listView.addFocusListener(_lastFocusOwnerTracker);
      editView.addFocusListener(_lastFocusOwnerTracker);
      
      editView.requestFocus();
      
      setTransferHandler(new SpriteTransferHandler());
   }
   
   // Redirect the focus to editView
   @Override
   public void requestFocus(){
      super.requestFocus();
//      System.out.println("requesting focus for editview");
      //getListView().setFocusable(false);
      getEditView().requestFocus();
      //getListView().setFocusable(true);
   }
   
   void play() {
      Step step = (Step) getSelected();
      StoryPlayer player;
      try {
         player = new DefaultStoryPlayer();
         List<Step> steps = new ArrayList<Step>();
         steps.add(step);
         player.play(steps);
      } catch (AWTException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   private void addActions(ActionMap map, Action[] actions) {
      int n = actions.length;
      for (int i = 0; i < n; i++) {
         Action a = actions[i];
         map.put(a.getValue(Action.NAME), a);
      }
   }

   Bundler bps = new Bundler();
   private void initActionInputMap(){
      
      addActions(getActionMap(), StoryEditorKit.getActions());
      addActions(getActionMap(), StepEditKit.getActions());
      //ActionMap map = getActionMap();
      InputMap imap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

      //map.put(StoryEditorKit.playCurrentStepAction.getValue(Action.NAME), StoryEditorKit.playCurrentStepAction);
      imap.put(KeyStroke.getKeyStroke("meta R"), StoryEditorKit.playCurrentStepAction);           
      imap.put(KeyStroke.getKeyStroke("meta N"), StoryEditorKit.insertNewStepAction);
      imap.put(KeyStroke.getKeyStroke("meta O"), bps.getLoadAction().getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("meta S"), bps.getSaveAction().getValue(Action.NAME));
      
            
      //map.put(bps.getLoadAction().getValue(Action.NAME), bps.getLoadAction());
      //map.put(bps.getSaveAction().getValue(Action.NAME), bps.getSaveAction());
   }

   private SelectionTool<Step> selectionTool = new SelectionTool<Step>();
   public class SelectionTool<T>{
            
      public T getSelected(){
         return (T) getListView().getSelectedValue();         
      }

      public int getSelectedIndex() {
         return getListView().getSelectedIndex();

      }
      
   }

   @Override
   public BundleableDocument getBundleableDocument() {
      return getStory();
   }

   @Override
   public void setBundleableDocument(BundleableDocument newDocument) {
      setStory((Story)newDocument);
   }

   void setStory(Story story){
      setSlideDeck(story);
   }
   
   public Story getStory() {
      return (Story) getSlideDeck();
   }

   public SelectionTool<Step> getSelectionTool() {
      return selectionTool;
   }


   public StepEditView getEditView(){
      return (StepEditView) editView;
   }
   
   protected void insertImage(File file){      
      try {
         getEditView().editTool.importContextImage(file);
      } catch (IOException e) {
      }   
   }

   public Component getLastFocusOwner() {
      return _lastFocusOwnerTracker.getLastFocusOwner();
   }


}
