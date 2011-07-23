package org.sikuli.ui;

import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditListener;

public interface Slide {  
   String getName();
   void setName(String name);
   void addChangeListener(ChangeListener l);
   void removeChangeListener(ChangeListener l);
   public void addUndoableEditListener(UndoableEditListener listener);
   public void removeUndoableEditListener(UndoableEditListener listener);   
}