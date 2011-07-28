package org.sikuli.guide;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.sikuli.ui.Bundleable;
import org.sikuli.ui.BundleableDocument;
import org.sikuli.ui.DefaultSlide;
import org.sikuli.ui.DefaultSlideDeck;
import org.sikuli.ui.Deck;
import org.sikuli.ui.XMLBundleableAdapter;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

import com.google.common.base.Preconditions;

@Root
public class Story extends DefaultSlideDeck<Step> 
implements PropertyChangeListener, BundleableDocument {

   //@ElementList
   //ArrayList<Step> _stepList = new ArrayList<Step>();

   @ElementList   
   public List<Step> getSteps(){
      System.out.println("called by someone");
      return getElements();
   }

   @ElementList
   public void setSteps(List<Step> steps){
      // only works when story is empty ... =(
      System.out.println("called once, input has " + steps.size() + " steps");
      for (Step step : steps){
         addStep(step);
      }
   }

   //   EventListenerList listenerList = new EventListenerList();
   //   public void addListDataListener(ListDataListener l){
   //      listenerList.add(ListDataListener.class, l);
   //   }
   //   
   //   public void removeListDataListener(ListDataListener l){
   //      listenerList.remove(ListDataListener.class, l);
   //   }
   //   
   //   protected void fireListDataContentsChanged(int index0, int index1) {
   //      ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1);
   //      Object[] listeners = listenerList.getListenerList();
   //      for (int i = listeners.length-2; i>=0; i-=2) {
   //          if (listeners[i]==ListDataListener.class) {
   //              ((ListDataListener)listeners[i+1]).contentsChanged(e);
   //          }
   //      }
   //  }
   //
   //   protected void fireListDataIntervalAdded(int index0, int index1) {
   //      ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
   //      Object[] listeners = listenerList.getListenerList();
   //      for (int i = listeners.length-2; i>=0; i-=2) {
   //          if (listeners[i]==ListDataListener.class) {
   //              ((ListDataListener)listeners[i+1]).intervalAdded(e);
   //          }
   //      }
   //  }
   //   
   //   protected void fireListDataIntervalRemoved(int index0, int index1) {
   //      ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
   //      Object[] listeners = listenerList.getListenerList();
   //      for (int i = listeners.length-2; i>=0; i-=2) {
   //          if (listeners[i]==ListDataListener.class) {
   //              ((ListDataListener)listeners[i+1]).intervalRemoved(e);
   //          }
   //      }
   //  }




   //   BundlePersisterSupport bundleSaveLoadSupport = new BundlePersisterSupport();


   public void addStep(Step step) {
      insertElementAt(step, getSize());
   }

   public void removeStep(Step step){
      removeElement(step);
   }


   @Override
   public void propertyChange(PropertyChangeEvent e) {

   }

   @Override
   public List<Bundleable> getBundleables() {
      List<Bundleable> list = new ArrayList<Bundleable>();
      for (Step step : getSteps()){
         list.addAll((List<Bundleable>) step.getContextImages());
      }
      return list;
   }


   Bundleable bundlealeAdapter = new XMLBundleableAdapter(this);   
   @Override
   public void writeToBundle(File bundlePath) throws IOException {
      bundlealeAdapter.writeToBundle(bundlePath);
   }

   @Override
   public void readFromBundle(File bundlePath) throws IOException {
      System.out.println("loading from " + bundlePath.getAbsolutePath());
      bundlealeAdapter.readFromBundle(bundlePath);      
      System.out.println("now has " + getSize() + " steps");
   }

   //   private void initFrom(Story story){
   //      Preconditions.checkState(getSize()==0);
   //      for (Step step : story.getSteps()){
   //         addStep(step);
   //      }
   //   }

   @Attribute
   public int id;
}

