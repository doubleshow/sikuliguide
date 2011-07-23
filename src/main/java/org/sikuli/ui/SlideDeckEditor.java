package org.sikuli.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import org.sikuli.guide.Step;

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

//   protected void removeSelectedSlide() {
//      int index = listView.getSelectedIndex();
//      getSlideDeck().getSlides().remove(index);
//      listView.setSelectedIndex(Math.max(index-1,0));
//      refresh();
//   }

   protected void setSelectedSlideIndex(int index) {
      if (index >= 0 && index < getSlideDeck().getSize()){
         editView.setSlide((Slide)getSlideDeck().getElementAt(index));
//         listView.setSelectedIndex(index);
         listView.ensureIndexIsVisible(index);
      }else{
         editView.setSlide(null);
         listView.setSelectedIndex(-1);         
      }
   }

   SlideDeckEditor(SlideDeck document){
      this();
      setSlideDeck(document);      
   }
   
   int getSelectedIndex(){
      return listView.getSelectedIndex();
   }
   
   
   class SlideListener implements ChangeListener{
      @Override
      public void stateChanged(ChangeEvent arg0) {
         listView.repaint();
      }
   }
   private SlideListener slideListener = new SlideListener();
   
   protected class SlideDeckListener implements ChangeListener, ListDataListener{
      
      
      @Override
      public void contentsChanged(ListDataEvent arg0) {
      }

      @Override
      public void intervalAdded(final ListDataEvent e) {
         SlideDeck slideDeck = (SlideDeck) e.getSource();            
         for (int i = e.getIndex0(); i <= e.getIndex1(); ++i){               
            Slide addedSlide = (Slide) slideDeck.getElementAt(i);
            addListenersToSlide(addedSlide);
         }               
         
         EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
               listView.setSelectedIndex(e.getIndex0()); // this allows the editview to be selected
               listView.addSelectionInterval(e.getIndex0(), e.getIndex1());   // this allows the added elements to be highlighted
            }
         });
         
      }

      @Override
      public void intervalRemoved(ListDataEvent e) {
         //int index = getSelectedIndex();
         //System.out.println(String.format("list element removed notified from %d to %d", e.getIndex0(), e.getIndex1()));

         SlideDeck slideDeck = (SlideDeck) e.getSource();            
         for (int i = e.getIndex0(); i <= e.getIndex1(); ++i){               
//            Slide slide = (Slide) slideDeck.getElementAt(i);
//            removeListenersFromSlide(slide);
         }               

         
         Slide editingSlide = editView.getSlide();
         
         if (getSlideDeck().getSize()==0){
            //System.out.println("list is empty");              
            setSelectedSlideIndex(-1);
         
         }else {
            final int indexToSelect = Math.min(e.getIndex0(), getSlideDeck().getSize()-1);
            //System.out.println("automatically select " + indexToSelect);
            
            
            // here listView may or may not have responded to 
            // intervalRemoved callback. so as a workaround, we make
            // the selection later to make sure the listView has
            // updated itself.
            EventQueue.invokeLater(new Runnable(){
               @Override
               public void run(){
                  listView.setSelectedIndex(indexToSelect);
               }
            });
            
         }
      }

      @Override
      public void stateChanged(ChangeEvent e) {
         // TODO Auto-generated method stub
         
      }         
      
      
   }
   private SlideDeckListener slideDeckListener = new SlideDeckListener();
   
   private void addListenersToSlide(Slide slide){
      slide.addChangeListener(slideListener); 
      slide.addUndoableEditListener(undoManager);
      //System.out.println("add undo");
   }
   
   private void removeListenersFromSlide(Slide slide){
      if (slide == null) return;
      slide.removeChangeListener(slideListener);
      slide.removeUndoableEditListener(undoManager);
   }
   
   private void addListenersToSlideDeck(SlideDeck slideDeck){
      if (slideDeck == null) return;
      slideDeck.addUndoableEditListener(undoManager);
      slideDeck.addListDataListener(slideDeckListener);
      System.out.println("add undo");
   }
   
   private void removeListenersFromSlideDeck(SlideDeck slideDeck){
      if (slideDeck == null) return;
      slideDeck.removeUndoableEditListener(undoManager);
      slideDeck.removeListDataListener(slideDeckListener);
   }
   
   
   private SlideDeck slideDeck;
   public void setSlideDeck(SlideDeck slideDeck) {
      
      removeListenersFromSlideDeck(this.slideDeck);
      addListenersToSlideDeck(slideDeck);
      
      this.slideDeck = slideDeck;
      listView.setModel(slideDeck);
      listView.setSelectedIndex(0);//setSelectedSlideIndex(0);      
      
      //listView.setPreferredSize(new Dimension(100,400));
//      listView.setSlideDeck(slideDeck);
      
      // TODO: Dry this (cf AddNewSlideAction)
      for (Slide slide : slideDeck.getSlides()){
         addListenersToSlide(slide);
//         slide.addChangeListener(slideListener);
//         slide.addUndoableEditListener(undoManager);         
      }
      
   }

   public SlideDeck getSlideDeck() {
      return slideDeck;
   }
   

   public void refresh() {   
      if (getSlideDeck()==null)
         return;
      
//      for (Slide slide : slideDeck.getSlides()){
//         slide.removeChangeListener(slideListener);
//         slide.addChangeListener(slideListener);
//         slide.removeUndoableEditListener(undoManager);
//         slide.addUndoableEditListener(undoManager);         
//      }
//      
//      int index = listView.getSelectedIndex();
//      listView.setListData(getSlideDeck().getSlides().toArray());
//      listView.setSelectedIndex(index);

//      if (getSlideDeck().getSlides().size() == 0){
//         editView.setSlide(null);
//      }      
   }
   
   
   private Slide getSelectedSlide(){
      int index = listView.getSelectedIndex();
      if (index < 0)
         return null;
      else
         return (Slide) getSlideDeck().getElementAt(index);
   }

   public void setSlideEditView(SlideEditView view){
      editView = view;
      editView.setSlide(getSelectedSlide());
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
               int[] indices = listView.getSelectedIndices();
               for (int i = indices.length - 1; i >= 0; i--){                  
                  getSlideDeck().removeElementAt(indices[i]);
                  
               }
            }

         };
      }       

      public Action addNewSlideToEndAction(final Slide newSlide){

         return new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
               int lastIndex = getSlideDeck().getSize();
               getSlideDeck().insertElementAt(newSlide,lastIndex);
               listView.setSelectedIndex(lastIndex);
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
      //listView.setSlideDeck(getSlideDeck());
      if (getSlideDeck()!=null)
         listView.setModel(getSlideDeck());
      listView.setMinimumSize(new Dimension(120,150));
      listView.addListSelectionListener(new ListSelectionListener(){

         @Override
         public void valueChanged(ListSelectionEvent e) {            
            //System.out.println(String.format("index0:%d, index1:%d, adjusting:%s",e.getFirstIndex(),e.getLastIndex(),e.getValueIsAdjusting()));
            boolean isOnlyOneItemSelected = listView.getSelectedIndices().length == 1;
            //System.out.println("only one item selected " + listView.isSelectedIndex(0));               
            //System.out.println("is index0 selected " + listView.isSelectedIndex(e.getFirstIndex()));               
            
            if (isOnlyOneItemSelected){               
               int index = listView.getSelectedIndex();
               editView.setSlide((Slide)getSlideDeck().getElementAt(index));
//               System.out.println(String.format("selecting:%d",index));
//               setSelectedSlideIndex(index);
            }
            
         }

      });
      listView.addKeyListener(new KeyAdapter(){

         @Override
         public void keyPressed(KeyEvent e){
            if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE ){               
               getEditActionFactory().deleteSelectedSlideAction().actionPerformed(new ActionEvent(this,0,null));
            }
            
            if (e.getKeyCode() == KeyEvent.VK_2){
               System.out.println("key pressed");
               validate();
               System.out.println(listView.getBounds());
               System.out.println(listView.getVisibleRect());
               System.out.println(listView.getCellBounds(0,3));
               //listView.ensureIndexIsVisible(0);
               System.out.println(listView.getFirstVisibleIndex());
               System.out.println(listView.getLastVisibleIndex());
               System.out.println(listView.getVisibleRowCount());
               System.out.println(listView.getFixedCellHeight());
               System.out.println(listView.getModel().getSize());
               
               Step step = (Step) listView.getModel().getElementAt(0);
               System.out.println("step:"+ step.getSprites().size());
               

               listView.repaint();
               listView.paintImmediately(0,0,150,467);
               listView.setSelectedIndex(2);
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


