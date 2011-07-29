package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.event.ActionEvent;

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

class StoryEditorKit {

   static class PlayAction extends AbstractAction{

      final static String CURRENT = "current";

      PlayAction(String s){
         super(s);
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         System.out.println("PLAY");
         if (e.getSource() instanceof StoryEditor){
            StoryEditor editor = (StoryEditor) e.getSource();
            

            if (e.getActionCommand().equals(CURRENT)){

               editor.play();

            }     
         }
      }

   }

   static Action playCurrentStepAction = new PlayAction("Play"){
      {
         putValue(Action.NAME, "playCurrentStepAction");
         putValue(Action.ACTION_COMMAND_KEY, CURRENT);
      }
   };

}


// Save/Load contents to a bundle folder including xml and images
public class StoryEditor extends SlideDeckEditor implements BundleableDocumentOwner {


   StoryEditor() {
      super();
      initActionInputMap();
      setFocusable(true);
      
      setStory(new Story());
          

      StoryListView listView = new StoryListView();
      setSlideDeckListView(listView);
      listView.setTransferHandler(new SpriteTransferHandler());

      StepEditView editView = new StepEditView();      
      setSlideEditView(editView);

      editView.requestFocus();
   }
   
   
   void play() {
      Step step = (Step) getSelected();
      StoryPlayer player;
      try {
         player = new StoryPlayer();
         player.play(step);
      } catch (AWTException e) {
         // TODO Auto-generated catch block
         //e.printStackTrace();
      }
   }

   Bundler bps = new Bundler();
   private void initActionInputMap() {
      ActionMap map = getActionMap();
      InputMap imap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

      map.put(StoryEditorKit.playCurrentStepAction.getValue(Action.NAME), StoryEditorKit.playCurrentStepAction);
      imap.put(KeyStroke.getKeyStroke("meta R"), StoryEditorKit.playCurrentStepAction.getValue(Action.NAME));
            
      map.put(bps.getLoadAction().getValue(Action.NAME), bps.getLoadAction());
      imap.put(KeyStroke.getKeyStroke("meta O"), bps.getLoadAction().getValue(Action.NAME));
      map.put(bps.getSaveAction().getValue(Action.NAME), bps.getSaveAction());
      imap.put(KeyStroke.getKeyStroke("meta S"), bps.getSaveAction().getValue(Action.NAME));
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
   
   Story getStory() {
      return (Story) getSlideDeck();
   }



}
