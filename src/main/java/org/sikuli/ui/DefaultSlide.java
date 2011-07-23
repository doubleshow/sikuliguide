package org.sikuli.ui;

import java.io.Serializable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEditSupport;

public class DefaultSlide implements Slide, Serializable{

   public DefaultSlide(String name){
      this.name = name;
   }
   
   protected DefaultSlide(){
      
   }
   
   private String name = "";
   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public void setName(String name){
      final String oldName = this.name;
      this.name = name;
      fireStateChanged();
      
      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
         public void undo() throws CannotUndoException {
            super.undo();
            DefaultSlide.this.name = oldName;
         }
      });      
   }
   
   @Override
   public String toString(){
      return "Slide: " + getName();
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
   
   
   transient protected UndoableEditSupport undoableEditSupport = new UndoableEditSupport(this);   
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
