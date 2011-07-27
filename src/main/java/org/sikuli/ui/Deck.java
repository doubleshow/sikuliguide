package org.sikuli.ui;

import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditListener;

interface MutableListModel<T> extends ListModel {
   public boolean removeElement(T elem);
   public void removeElementAt(int index);
   public void insertElementAt(T elem, int index);
   public void insertElementsAt(List<T> elem, int index);
}

public interface Deck<T extends Slide> extends MutableListModel<T> {
   List<T> getElements();
   
   void addChangeListener(ChangeListener l);
   void removeChangeListener(ChangeListener l);
   public void addUndoableEditListener(UndoableEditListener listener);
   public void removeUndoableEditListener(UndoableEditListener listener);

}

interface SlideDeck extends Deck<Slide>{
}