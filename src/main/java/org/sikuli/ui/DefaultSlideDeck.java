package org.sikuli.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEditSupport;

public class DefaultSlideDeck implements SlideDeck {

   List<Slide> slides = new ArrayList<Slide>();
   
   @Override
   public List<Slide> getSlides() {
      return slides;
   }

   @Override
   public int size() {
      return slides.size();
   }

   @Override
   public void remove(final int index) {
      
      final Slide slideToDelete = slides.get(index);               
      slides.remove(index);
      
      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
         public void undo() throws CannotUndoException {
            super.undo();
            slides.add(index, slideToDelete);
         }
      });
      
      fireStateChanged();
   }
   
   
   @Override
   public void remove(final Slide slide) {
      final int index = slides.indexOf(slide);
      slides.remove(index);
      
      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
         public void undo() throws CannotUndoException {
            super.undo();
            slides.add(index, slide);
         }
      });
      
      fireStateChanged();
   }


   @Override
   public void add(int index, final Slide slide){
      slides.add(index, slide);   
      
      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
         public void undo() throws CannotUndoException {
            super.undo();
            slides.remove(slide);
         }
      });      
      
      fireStateChanged();
   }
   
   @Override
   public void add(final Slide slide) {
      slides.add(slide);   
      
      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
         public void undo() throws CannotUndoException {
            super.undo();
            slides.remove(slide);
         }
      });
      
      fireStateChanged();
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
   
   
   
   
}