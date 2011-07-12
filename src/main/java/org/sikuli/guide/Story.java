package org.sikuli.guide;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Story implements PropertyChangeListener {
   
   @ElementList
   ArrayList<Step> _stepList = new ArrayList<Step>();
   
   
   public ArrayList<Step> getSteps(){
      return _stepList;
   }
   
   EventListenerList listenerList = new EventListenerList();
   public void addListDataListener(ListDataListener l){
      listenerList.add(ListDataListener.class, l);
   }
   
   public void removeListDataListener(ListDataListener l){
      listenerList.remove(ListDataListener.class, l);
   }
   
   protected void fireListDataContentsChanged(int index0, int index1) {
      ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1);
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length-2; i>=0; i-=2) {
          if (listeners[i]==ListDataListener.class) {
              ((ListDataListener)listeners[i+1]).contentsChanged(e);
          }
      }
  }

   protected void fireListDataIntervalAdded(int index0, int index1) {
      ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length-2; i>=0; i-=2) {
          if (listeners[i]==ListDataListener.class) {
              ((ListDataListener)listeners[i+1]).intervalAdded(e);
          }
      }
  }
   
   protected void fireListDataIntervalRemoved(int index0, int index1) {
      ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length-2; i>=0; i-=2) {
          if (listeners[i]==ListDataListener.class) {
              ((ListDataListener)listeners[i+1]).intervalRemoved(e);
          }
      }
  }

   
   public void addStep(Step step) {
      _stepList.add(step);      
      
      fireListDataIntervalAdded(_stepList.size()-1,_stepList.size()-1);
      //sprite.addPropertyChangeListener(this);      
      //fireDataContentsChanged();
   }
   
   public void removeStep(Step step){
      int index = _stepList.indexOf(step);

      fireListDataIntervalRemoved(index, index);
      
      _stepList.remove(step);      
      
      //sprite.removePropertyChangeListener(this);            
      //fireDataContentsChanged();
   }


   @Override
   public void propertyChange(PropertyChangeEvent e) {
      
   }

}
