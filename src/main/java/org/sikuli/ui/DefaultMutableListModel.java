package org.sikuli.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

class DefaultMutableListModel<T> extends AbstractListModel 
   implements MutableListModel<T>{
   
   List<T> elements = new ArrayList<T>();

   @Override
   public T getElementAt(int index) {
      return elements.get(index);
   }

   @Override
   public int getSize() {
      return elements.size();
   }

   @Override
   public boolean removeElement(T elem) {
      int index = elements.indexOf(elem);
      boolean ret = elements.remove(elem);
      fireIntervalRemoved(this,index,index);
      return ret;
   }
   
   @Override
   public void removeElementAt(int index) {
      elements.remove(index);
      fireIntervalRemoved(this,index,index);
   }

   @Override
   public void insertElementAt(T elem, int index) {
      elements.add(index,elem);
      fireIntervalAdded(this,index,index);
   }

   @Override
   public void insertElementsAt(List<T> elems, int index) {
      elements.addAll(index,elems);
      fireIntervalAdded(this, index, index + elems.size() - 1);      
   }

   public int indexOf(T elem) {
      return elements.indexOf(elem);
   }

   public void clear(){
      int n = getSize();
      elements.clear();
      fireIntervalRemoved(this,0,n-1);
   }
   
}