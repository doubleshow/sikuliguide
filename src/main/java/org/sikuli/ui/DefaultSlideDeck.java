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

public class DefaultSlideDeck<T extends Slide> extends DefaultMutableListModel<T> 
    implements Deck<T> {

   @Override
   public List<T> getElements() {
      
      List<T> slides = new ArrayList<T>();
      for (int i=0; i< getSize(); ++i){
         slides.add(getElementAt(i));
      }
      return slides;
   }

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

   @Override
   public boolean removeElement(final T slideToDelete) {
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
   public void insertElementAt(final T slide, int index) {
      super.insertElementAt(slide, index);
      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
         public void undo() throws CannotUndoException {
            super.undo();
            DefaultSlideDeck.super.removeElement(slide);
         }
      });      
      fireStateChanged();
   }

   @Override
   public void removeElementAt(final int index) {
      final T slideDeleted = (T) getElementAt(index);
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