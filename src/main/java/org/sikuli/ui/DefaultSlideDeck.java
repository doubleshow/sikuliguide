package org.sikuli.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEditSupport;

public class DefaultSlideDeck extends DefaultMutableListModel<Slide> 
   implements SlideDeck {

   @Override
   public List<Slide> getSlides() {
      
      ArrayList<Slide> slides = new ArrayList<Slide>();
      for (int i=0; i< getSize(); ++i){
         slides.add(getElementAt(i));
      }
      return slides;
   }

//   @Override
//   public int size() {
//      return slides.size();
//   }
//
//   
//   
//   @Override
//   public void remove(final int index) {
//      final Slide slideToDelete = slides.get(index);     
//      slides.remove(index);
//      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
//         public void undo() throws CannotUndoException {
//            super.undo();
//            slides.add(index, slideToDelete);
//         }
//      });
//
//      fireStateChanged();
//      fireIntervalRemoved(this, index,index);
//   }
//
//
//   @Override
//   public void remove(final Slide slide) {
//      final int index = slides.indexOf(slide);
//      remove(index);
//   }
//
//   @Override
//   public void add(int index, final Slide slide){
//      slides.add(index, slide);   
//
//      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
//         public void undo() throws CannotUndoException {
//            super.undo();
//            slides.remove(slide);
//         }
//      });      
//
//      fireStateChanged();
//      fireIntervalAdded(this,index,index);
//   }
//
//   @Override
//   public void add(final Slide slide) {
//      add(size(), slide);      
//   }


   protected void fireStateChanged(){
      ChangeEvent evt = new ChangeEvent(this);
      Object[] listeners = listenerList.getListenerList();
      for (int i=0; i<listeners.length; i+=2) {
         if (listeners[i]==ChangeListener.class) {
            ((ChangeListener)listeners[i+1]).stateChanged(evt);
         }
      }
   }

   transient private EventListenerList listenerList = new EventListenerList();   
   @Override
   public void addChangeListener(ChangeListener l) {
      listenerList.add(ChangeListener.class, l);
   }

   @Override
   public void removeChangeListener(ChangeListener l) {
      listenerList.remove(ChangeListener.class, l);
   }

   transient UndoableEditSupport undoableEditSupport = new UndoableEditSupport(this);  
   @Override
   public void addUndoableEditListener(
         UndoableEditListener undoableEditListener) {
      undoableEditSupport.addUndoableEditListener(undoableEditListener);
   }

   @Override
   public void removeUndoableEditListener(
         UndoableEditListener undoableEditListener) {
      undoableEditSupport.removeUndoableEditListener(undoableEditListener);
   }


//   // ListModel Implementation
//
//   @Override
//   public Slide getElementAt(int index) {
//      return slides.get(index);
//   }
//
//   @Override
//   public int getSize() {
//      return size();
//   }
//
//   @Override
//   public void addListDataListener(ListDataListener l) {
//      listenerList.add(ListDataListener.class, l);      
//   }
//
//   @Override
//   public void removeListDataListener(ListDataListener l) {
//      listenerList.remove(ListDataListener.class, l);         
//   }
//
//
//   protected void fireListDataContentsChanged(int index0, int index1) {
//      ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1);
//      Object[] listeners = listenerList.getListenerList();
//      for (int i = listeners.length-2; i>=0; i-=2) {
//         if (listeners[i]==ListDataListener.class) {
//            ((ListDataListener)listeners[i+1]).contentsChanged(e);
//         }
//      }
//   }
//
//   protected void fireListDataIntervalAdded(int index0, int index1) {
//      ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
//      Object[] listeners = listenerList.getListenerList();
//      for (int i = listeners.length-2; i>=0; i-=2) {
//         if (listeners[i]==ListDataListener.class) {
//            ((ListDataListener)listeners[i+1]).intervalAdded(e);
//         }
//      }
//   }
//
//   protected void fireListDataIntervalRemoved(int index0, int index1) {
//      ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
//      Object[] listeners = listenerList.getListenerList();
//      for (int i = listeners.length-2; i>=0; i-=2) {
//         if (listeners[i]==ListDataListener.class) {
//            ((ListDataListener)listeners[i+1]).intervalRemoved(e);
//         }
//      }
//   }

   @Override
   public boolean removeElement(final Slide slideToDelete) {
      final int index = indexOf(slideToDelete);     
      boolean ret = super.removeElement(slideToDelete);

      if (ret){

         undoableEditSupport.postEdit(new AbstractUndoableEdit(){
            public void undo() throws CannotUndoException {
               super.undo();
               DefaultSlideDeck.super.insertElementAt(slideToDelete, index);
            }
         });
         fireStateChanged();
      }

      return ret;
   }

   @Override
   public void insertElementAt(final Slide slide, int index) {
      super.insertElementAt(slide, index);
      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
         public void undo() throws CannotUndoException {
            super.undo();
            DefaultSlideDeck.super.removeElement(slide);
         }
      });      
      System.out.println("inserted. List has " + getSize() + " elements.");
      fireStateChanged();
   }

   @Override
   public void removeElementAt(final int index) {
      final Slide slideDeleted = (Slide) getElementAt(index);
      super.removeElementAt(index);
      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
         public void undo() throws CannotUndoException {
            super.undo();
            DefaultSlideDeck.super.insertElementAt(slideDeleted, index);
         }
      });      
      fireStateChanged();
   }



}