package org.sikuli.ui;

import java.util.List;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;

public interface SlideDeck {
   List<Slide> getSlides();   
   int size();
   
   void remove(int index);
   void add(Slide slide);
   
   public void addUndoableEditListener(UndoableEditListener listener);
   public void removeUndoableEditListener(UndoableEditListener listener);

}