package org.sikuli.ui;

import java.util.List;

import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditListener;

public interface SlideDeck {
   List<Slide> getSlides();
   int size();
   
   void remove(int index);
   void remove(Slide slide);
   void add(Slide slide);
   void add(int index, Slide slide);
   
   void addChangeListener(ChangeListener l);
   void removeChangeListener(ChangeListener l);
   public void addUndoableEditListener(UndoableEditListener listener);
   public void removeUndoableEditListener(UndoableEditListener listener);

}