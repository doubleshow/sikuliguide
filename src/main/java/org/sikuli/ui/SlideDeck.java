package org.sikuli.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

interface SlideDeck {
   List<Slide> getSlides();   
   int size();
   
   void remove(int index);
   void add(Slide slide);

   
   public void addUndoableEditListener(UndoableEditListener listener);
   public void removeUndoableEditListener(UndoableEditListener listener);

}

class DefaultSlideDeck implements SlideDeck {

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
   }
   
   UndoableEditSupport undoableEditSupport = new UndoableEditSupport(this);
  
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