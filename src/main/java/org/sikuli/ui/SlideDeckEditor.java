package org.sikuli.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import quicktime.std.comp.Component;


public class SlideDeckEditor extends JPanel {

   JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

   SlideEditView editView = new SlideEditView();
   SlideDeckListView listView = new SlideDeckListView();

   JPanel blankView;

   protected SlideDeckEditor(){
      setLayout(new BorderLayout());      
      setSlideDeckListView(listView);

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
      
      // TODO: Dry this (cf AddNewSlideAction)
      for (Slide slide : slideDeck.getSlides()){
         slide.addChangeListener(slideListener);
         slide.addUndoableEditListener(undoManager);         
      }
      
      setSelectedSlideIndex(0);      
      slideDeck.addUndoableEditListener(undoManager);
   }

   public SlideDeck getSlideDeck() {
      return slideDeck;
   }
   
   class SlideListener implements ChangeListener{

      @Override
      public void stateChanged(ChangeEvent e) {               
         listView.repaint();               
      }
   }
   private SlideListener slideListener = new SlideListener();

   public void refresh() {   
      if (getSlideDeck()==null)
         return;

      
      for (Slide slide : slideDeck.getSlides()){
         slide.removeChangeListener(slideListener);
         slide.addChangeListener(slideListener);
         slide.removeUndoableEditListener(undoManager);
         slide.addUndoableEditListener(undoManager);         
      }
      
      int index = listView.getSelectedIndex();
      listView.setListData(getSlideDeck().getSlides().toArray());
      listView.setSelectedIndex(index);

      if (getSlideDeck().getSlides().size() == 0){
         editView.setSlide(null);
      }      
   }

   public void setSlideEditView(SlideEditView view){
      editView = view;
      splitPane.setRightComponent(editView);      
      refresh();
   }

   UndoManager undoManager = new UndoManager();
   public Action undoAction(){
      return new AbstractAction(){

         @Override
         public void actionPerformed(ActionEvent e) {
            UndoManagerHelper.getUndoAction(undoManager).actionPerformed(e);
            refresh();            
         }

      };
   }

   public class EditActionFactory {

      public Action deleteSelectedSlideAction(){

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

      public Action addNewSlideToEndAction(final Slide newSlide){

         return new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
               getSlideDeck().add(newSlide);
               
               newSlide.addChangeListener(new ChangeListener(){
                  @Override
                  public void stateChanged(ChangeEvent arg0) {
                     System.out.println("called");
                     refresh();
                  }
               });               
               
               refresh();

               listView.setSelectedIndex(getSlideDeck().size()-1);
            }

         };


      }    
   };


   private EditActionFactory editActionFactory = new EditActionFactory();   


   public void setSlideDeckListView(SlideDeckListView view) {

      setVisible(true);

      listView = view;      
      
      listView.setDragEnabled(true);
      //listView.setTransferHandler(new ArrayListTransferHandler());
      
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


