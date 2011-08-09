package org.sikuli.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoManager;

import net.miginfocom.swing.MigLayout;

public class SlideDeckEditor extends JPanel {

   JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

   LeftPanel leftPanel = new LeftPanel();
   RightPanel rightPanel = new RightPanel();
   
   protected SlideEditView editView = new SlideEditView();
   private SlideDeckListView listView = new SlideDeckListView();

   JPanel blankView;
   
   
   class LeftPanel extends JScrollPane{
      
      LeftPanel(){
         setMinimumSize(new Dimension(200,10));
      }      
   }
   
   class RightPanel extends JPanel{
      
      JScrollPane scrollPane = new JScrollPane();
      RightPanel(){
         setLayout(new MigLayout("insets 0"));
         add(scrollPane, "dock center");
         setBackground(Color.red);
      }
      
      void setComponent(JComponent comp){
         //removeAll();
         //add(scrollPane, "dock center");
         scrollPane.setViewportView(comp);
         validate();
      }
      
   }

   protected SlideDeckEditor(){
      setLayout(new BorderLayout());      
      splitPane.setLeftComponent(leftPanel);
      splitPane.setRightComponent(rightPanel);
      add(splitPane, BorderLayout.CENTER);
      
      setSlideDeckListView(getListView());
      setSlideEditView(editView);
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
         getListView().ensureIndexIsVisible(index);
      }else{
         editView.setSlide(null);
         getListView().setSelectedIndex(-1);         
      }
   }

   SlideDeckEditor(Deck document){
      this();
      setSlideDeck(document);      
   }
   
   int getSelectedIndex(){
      return getListView().getSelectedIndex();
   }
   
   public Slide getSelected(){
      return (Slide) getListView().getSelectedValue();
   }
   
   class SlideListener implements ChangeListener{
      @Override
      public void stateChanged(ChangeEvent arg0) {
         getListView().repaint();
      }
   }
   private SlideListener slideListener = new SlideListener();
   
   protected class SlideDeckListener implements ChangeListener, ListDataListener{
      
      
      @Override
      public void contentsChanged(ListDataEvent arg0) {
      }

      @Override
      public void intervalAdded(final ListDataEvent e) {
         Deck slideDeck = (Deck) e.getSource();            
         for (int i = e.getIndex0(); i <= e.getIndex1(); ++i){               
            Slide addedSlide = (Slide) slideDeck.getElementAt(i);
            addListenersToSlide(addedSlide);
         }               
         
         EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
               getListView().setSelectedIndex(e.getIndex0()); // this allows the editview to be selected
               getListView().addSelectionInterval(e.getIndex0(), e.getIndex1());   // this allows the added elements to be highlighted
               getListView().ensureIndexIsVisible(e.getIndex1()); // allows the added elements to be visible
            }
         });
         
      }

      @Override
      public void intervalRemoved(ListDataEvent e) {
         //int index = getSelectedIndex();
         //System.out.println(String.format("list element removed notified from %d to %d", e.getIndex0(), e.getIndex1()));

         Deck slideDeck = (Deck) e.getSource();            
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
                  getListView().setSelectedIndex(indexToSelect);
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
   
   private void addListenersToSlideDeck(Deck slideDeck){
      if (slideDeck == null) return;
      slideDeck.addUndoableEditListener(undoManager);
      slideDeck.addListDataListener(slideDeckListener);
      System.out.println("add undo");
   }
   
   private void removeListenersFromSlideDeck(Deck slideDeck){
      if (slideDeck == null) return;
      slideDeck.removeUndoableEditListener(undoManager);
      slideDeck.removeListDataListener(slideDeckListener);
   }
   
   
   private Deck<? extends Slide> slideDeck;
   public void setSlideDeck(Deck<? extends Slide> slideDeck) {
      
      if (this.slideDeck != null){
         for (Slide slide : this.slideDeck.getElements()){
            removeListenersFromSlide(slide);
         }      
         removeListenersFromSlideDeck(this.slideDeck);
      }
      
      this.slideDeck = slideDeck;

      addListenersToSlideDeck(slideDeck);
      for (Slide slide : slideDeck.getElements()){
         addListenersToSlide(slide);
      }

      getListView().setModel(slideDeck);
      getListView().setSelectedIndex(0);//setSelectedSlideIndex(0);      
      
      
   }

   public Deck getSlideDeck() {
      return slideDeck;
   }
   

   public void refresh() {   
//      if (getSlideDeck()==null)
//         return;
      
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
      int index = getListView().getSelectedIndex();
      if (index < 0)
         return null;
      else
         return (Slide) getSlideDeck().getElementAt(index);
   }

   public void setSlideEditView(SlideEditView view){
      editView = view;
      editView.setSlide(getSelectedSlide());
      rightPanel.setComponent(editView);      
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
               int[] indices = getListView().getSelectedIndices();
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
               getListView().setSelectedIndex(lastIndex);
            }

         };


      }    
   };


   private EditActionFactory editActionFactory = new EditActionFactory();   


   public void setSlideDeckListView(SlideDeckListView view) {

      setVisible(true);

      setListView(view);      
      
      //getListView().setDragEnabled(true);
      if (getSlideDeck()!=null)
         getListView().setModel(getSlideDeck());
      getListView().setMinimumSize(new Dimension(120,150));
      getListView().addListSelectionListener(new ListSelectionListener(){

         @Override
         public void valueChanged(ListSelectionEvent e) {            
            //System.out.println(String.format("index0:%d, index1:%d, adjusting:%s",e.getFirstIndex(),e.getLastIndex(),e.getValueIsAdjusting()));
            boolean isOnlyOneItemSelected = getListView().getSelectedIndices().length == 1;
            //System.out.println("only one item selected " + listView.isSelectedIndex(0));               
            //System.out.println("is index0 selected " + listView.isSelectedIndex(e.getFirstIndex()));               
            
            if (isOnlyOneItemSelected){               
               int index = getListView().getSelectedIndex();
               editView.setSlide((Slide)getSlideDeck().getElementAt(index));
               System.out.println(String.format("selecting:%d",index));
//               setSelectedSlideIndex(index);
            }
            
         }

      });
      getListView().addKeyListener(new KeyAdapter(){

         @Override
         public void keyPressed(KeyEvent e){
            if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE ){               
               getEditActionFactory().deleteSelectedSlideAction().actionPerformed(new ActionEvent(this,0,null));
            }
            
//            if (e.getKeyCode() == KeyEvent.VK_2){
//               System.out.println("key pressed");
//               validate();
//               System.out.println(listView.getBounds());
//               System.out.println(listView.getVisibleRect());
//               System.out.println(listView.getCellBounds(0,3));
//               //listView.ensureIndexIsVisible(0);
//               System.out.println(listView.getFirstVisibleIndex());
//               System.out.println(listView.getLastVisibleIndex());
//               System.out.println(listView.getVisibleRowCount());
//               System.out.println(listView.getFixedCellHeight());
//               System.out.println(listView.getModel().getSize());
//               
//               Step step = (Step) listView.getModel().getElementAt(0);
//               System.out.println("step:"+ step.getSprites().size());
//               
//
//               listView.repaint();
//               listView.paintImmediately(0,0,150,467);
//               listView.setSelectedIndex(2);
//            }
         }

      });      
      
      
      leftPanel.setViewportView(getListView());
      //leftPanel.set
      leftPanel.repaint();
      //splitPane.setLeftComponent(listView);      
      refresh();
      getListView().setSelectedIndex(0);
   }

   public EditActionFactory getEditActionFactory() {
      return editActionFactory;
   }

   public void setListView(SlideDeckListView listView) {
      this.listView = listView;
   }

   public SlideDeckListView getListView() {
      return listView;
   }




}


