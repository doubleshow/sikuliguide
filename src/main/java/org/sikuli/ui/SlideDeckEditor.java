package org.sikuli.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import quicktime.std.comp.Component;


interface Slide{  
   String getName();
   //String toString();
}



class SlideEditView extends JPanel {
   
   SlideEditView(){      
   }

   private Slide slide;
   public Slide getSlide() {
      return slide;
   }
   
   public void setSlide(Slide slide){
      this.slide = slide;
      repaint();
   }
   
}

class SlideDeckListView extends JList {
   
}

public class SlideDeckEditor extends JPanel {
   
   JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
   
   SlideEditView editView = new SlideEditView();
   SlideDeckListView listView = new SlideDeckListView();

   JPanel blankView;
   
   
   
   SlideDeckEditor(){
      setLayout(new BorderLayout());
      
      
      
      setSlideDeckListView(listView);
//      listView.setMinimumSize(new Dimension(150,0));
//      listView.addListSelectionListener(new ListSelectionListener(){
//
//         @Override
//         public void valueChanged(ListSelectionEvent e) {
//            int index = listView.getSelectedIndex();
//            setSelectedSlideIndex(index);
//         }
//
//      });
//      listView.addKeyListener(new KeyAdapter(){
//         
//         @Override
//         public void keyPressed(KeyEvent e){
//            if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE ){               
//               removeSelecteSlide();
//            }
//         }
//         
//      });

      
      splitPane.setLeftComponent(listView);
      splitPane.setRightComponent(editView);
      add(splitPane, BorderLayout.CENTER);
   }
   
   protected void removeSelectedSlide() {
      int index = listView.getSelectedIndex();
      getSlideDeck().getSlides().remove(index);
      listView.setSelectedIndex(Math.max(index-1,0));
      refresh();
   }

   protected void setSelectedSlideIndex(int index) {
      if (index >= 0 && index < getSlideDeck().getSlides().size()){
         editView.setSlide(getSlideDeck().getSlides().get(index));
         listView.setSelectedIndex(index);
         listView.ensureIndexIsVisible(index);
      }      
   }

   SlideDeckEditor(SlideDeck document){
      this();
      setSlideDeck(document);      
   }

   

   private SlideDeck slideDeck;
   
   
   public void setSlideDeck(SlideDeck slideDeck) {
      this.slideDeck = slideDeck;
      listView.setListData(slideDeck.getSlides().toArray());
      setSelectedSlideIndex(0);      
      slideDeck.addUndoableEditListener(manager);      
   }

   public SlideDeck getSlideDeck() {
      return slideDeck;
   }

   public void refresh() {   
      if (getSlideDeck()==null)
         return;
      
      int index = listView.getSelectedIndex();
      listView.setListData(getSlideDeck().getSlides().toArray());
      listView.setSelectedIndex(index);
      
      if (getSlideDeck().getSlides().size() == 0){
         editView.setSlide(null);
      }      
   }

   public void addNewSlide(Slide newSlide) {
      getSlideDeck().getSlides().add(newSlide);
      refresh();
      setSelectedSlideIndex(getSlideDeck().getSlides().size()-1);
   }

   public void setSlideEditView(SlideEditView view){
      editView = view;
      splitPane.setRightComponent(editView);      
      refresh();
   }
   
   UndoManager manager = new UndoManager();
   Action undoAction(){
      return new AbstractAction(){

         @Override
         public void actionPerformed(ActionEvent e) {
            UndoManagerHelper.getUndoAction(manager).actionPerformed(e);
            refresh();            
         }

      };
   }
   
   
//   void performUndoableAction(UndoableEditAction undoableAction){
//      undoableAction.actionPerformed(null);
//      //getSlid.postEdit(undoableAction);
//   }
   
   class EditActionFactory {
      
      Action deleteSelectedSlideAction(){
         
         return new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
               final int index = listView.getSelectedIndex();
               getSlideDeck().remove(index);
               refresh();

               listView.setSelectedIndex(Math.max(index-1,0));
            }

         };
      }       
      
      Action addNewSlideToEndAction(final Slide newSlide){
         
         return new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
               getSlideDeck().add(newSlide);
               refresh();
               
               listView.setSelectedIndex(getSlideDeck().size()-1);
            }

         };


      }    
   };
      
   
   private EditActionFactory editActionFactory = new EditActionFactory();   

      
   public void setSlideDeckListView(SlideDeckListView view) {
      //listView.setVisible(false);
      //splitPane.add(listView);
      
      listView = view;      
      listView.setMinimumSize(new Dimension(120,150));
      listView.addListSelectionListener(new ListSelectionListener(){

         @Override
         public void valueChanged(ListSelectionEvent e) {
            int index = listView.getSelectedIndex();
            setSelectedSlideIndex(index);
         }

      });
      listView.addKeyListener(new KeyAdapter(){
         
         @Override
         public void keyPressed(KeyEvent e){
            if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE ){               
               getEditActionFactory().deleteSelectedSlideAction().actionPerformed(new ActionEvent(this,0,null));
            }
         }
         
      });      
      splitPane.setLeftComponent(listView);      
      refresh();
      listView.setSelectedIndex(0);
   }

   public EditActionFactory getEditActionFactory() {
      return editActionFactory;
   }
   
   
   
   
}

class UndoManagerHelper {

   public static Action getUndoAction(UndoManager manager, String label) {
     return new UndoAction(manager, label);
   }

   public static Action getUndoAction(UndoManager manager) {
     return new UndoAction(manager, "Undo");
   }

   public static Action getRedoAction(UndoManager manager, String label) {
     return new RedoAction(manager, label);
   }

   public static Action getRedoAction(UndoManager manager) {
     return new RedoAction(manager, "Redo");
   }

   private abstract static class UndoRedoAction extends AbstractAction {
     UndoManager undoManager = new UndoManager();

     String errorMessage = "Cannot undo";

     String errorTitle = "Undo Problem";

     protected UndoRedoAction(UndoManager manager, String name) {
       super(name);
       undoManager = manager;
     }

     public void setErrorMessage(String newValue) {
       errorMessage = newValue;
     }

     public void setErrorTitle(String newValue) {
       errorTitle = newValue;
     }

     protected void showMessage(Object source) {
//       if (source instanceof Component) {
//         JOptionPane.showMessageDialog((Component) source, errorMessage,
//             errorTitle, JOptionPane.WARNING_MESSAGE);
//       } else {
//         System.err.println(errorMessage);
//       }
     }
   }

   public static class UndoAction extends UndoRedoAction {
     public UndoAction(UndoManager manager, String name) {
       super(manager, name);
       setErrorMessage("Cannot undo");
       setErrorTitle("Undo Problem");
     }

     public void actionPerformed(ActionEvent actionEvent) {
       try {
         undoManager.undo();
       } catch (CannotUndoException cannotUndoException) {
         showMessage(actionEvent.getSource());
       }
     }
   }

   public static class RedoAction extends UndoRedoAction {
     String errorMessage = "Cannot redo";

     String errorTitle = "Redo Problem";

     public RedoAction(UndoManager manager, String name) {
       super(manager, name);
       setErrorMessage("Cannot redo");
       setErrorTitle("Redo Problem");
     }

     public void actionPerformed(ActionEvent actionEvent) {
       try {
         undoManager.redo();
       } catch (CannotRedoException cannotRedoException) {
         showMessage(actionEvent.getSource());
       }
     }
   }
 }

