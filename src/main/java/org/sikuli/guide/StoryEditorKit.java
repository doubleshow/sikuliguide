package org.sikuli.guide;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.TransferHandler;

public class StoryEditorKit {


   static public final String insertNewStepAction = "insertNewStepAction";
   static public final String playCurrentStepAction = "playCurrentStepAction";
   static public final String cutAction = (String) TransferHandler.getCutAction().getValue(Action.NAME);
   static public final String copyAction = (String) TransferHandler.getCopyAction().getValue(Action.NAME);
   static public final String pasteAction = (String) TransferHandler.getPasteAction().getValue(Action.NAME);

   //   static public final String insertTextAction = "insertTextAction";
   //   static public final String insertTargetAction = "insertTargetAction";
   //   static public final String insertCircleAction = "insertCircleAction";
   //   static public final String insertFlagTextAction = "insertFlagTextAction";

   static public Action getCutAction(){
      return new ComponentAction<StoryEditor>(cutAction, StoryEditor.class){
         @Override
         public void actionPerformed(ActionEvent ae) {            
            StoryEditor editor = getComponent(ae);
            ae.setSource(editor);            
            TransferHandler.getCutAction().actionPerformed(ae);
         }         
      };
   }

   static public Action getCopyAction(){
      return new ComponentAction<StoryEditor>(copyAction, StoryEditor.class){
         @Override
         public void actionPerformed(ActionEvent ae) {            
            StoryEditor editor = getComponent(ae);
            ae.setSource(editor);            
            TransferHandler.getCopyAction().actionPerformed(ae);
         }         
      };
   }

   static public Action getPasteAction(){
      return new ComponentAction<StoryEditor>(pasteAction, StoryEditor.class){
         @Override
         public void actionPerformed(ActionEvent ae) {            
            StoryEditor editor = getComponent(ae);
            ae.setSource(editor);            
            TransferHandler.getPasteAction().actionPerformed(ae);
         }         
      };
   }
   
   static public Action[] getActions(){      
      return new Action[]{         
            getInsertNewStepAction(),
            getCutAction(),
            getCopyAction(),
            getPasteAction()
      };
   }


   static class InsertAction extends ComponentAction<StoryEditor>{

      final static String NEW = "new";

      InsertAction(String s){
         super(s, StoryEditor.class);
      }

      @Override
      protected void execute(StoryEditor editor) {
         int index = editor.getSelectionTool().getSelectedIndex();
         Step newStep = new Step();
         editor.getStory().insertElementAt(newStep, index+1);        
      }

      //      @Override
      //      public boolean isEnabled(){
      //         System.out.println("isEnabled checked!!");
      //         StoryEditor editor = getFocusedComponent();
      //         if (editor == null)
      //            return false;
      //         else            
      //            return editor.getStory() != null && editor.getStory().getSize() < 3;
      //      }

   }

   //   public static Action insertNewStepAction = new EditAction("Insert New"){
   //      {Ïc
   //         putValue(Action.NAME, "insertNewStepAction");
   //         putValue(Action.ACTION_COMMAND_KEY, NEW);
   //      }
   //
   //   };

   static class PlayAction extends StoryEditorKit.InsertAction{

      final static String CURRENT = "current";

      PlayAction(String s){
         super(s);
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         System.out.println("PLAY");
         StoryEditor editor = getComponent(e);
         if (e.getActionCommand().equals(CURRENT)){
            editor.play();
         }
      }

   }

   public static Action getInsertNewStepAction() {
      return new InsertAction(insertNewStepAction);
   }

   //   static Action playCurrentStepAction = new PlayAction("Play"){
   //      {
   //         putValue(Action.NAME, "playCurrentStepAction");
   //         putValue(Action.ACTION_COMMAND_KEY, CURRENT);
   //      }
   //   };

}