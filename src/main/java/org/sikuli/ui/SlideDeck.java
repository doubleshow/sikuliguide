package org.sikuli.ui;

import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditListener;

interface MutableListModel<T> extends ListModel {
   public boolean removeElement(T elem);
   public void removeElementAt(int index);
   public void insertElementAt(T elem, int index);
}

public interface SlideDeck extends MutableListModel<Slide> {
   List<Slide> getSlides();
//   int size();  
//   void remove(int index);
//   void remove(Slide slide);
//   void add(Slide slide);
//   void add(int index, Slide slide);
   
   void addChangeListener(ChangeListener l);
   void removeChangeListener(ChangeListener l);
   public void addUndoableEditListener(UndoableEditListener listener);
   public void removeUndoableEditListener(UndoableEditListener listener);

}