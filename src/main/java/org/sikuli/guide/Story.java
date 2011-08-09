package org.sikuli.guide;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
implements PropertyChangeListener, ChangeListener, BundleableDocument {

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
      //System.out.println("called once, input has " + steps.size() + " steps");
      for (Step step : steps){
         addStep(step);
      }
      
   }

   public void addStep(Step step) {
      // TODO: move up to superclass
      step.addChangeListener(this);
      insertElementAt(step, getSize());
   }

   public void removeStep(Step step){
      step.removeChangeListener(this);
      removeElement(step);
   }

   @Override
   public void stateChanged(ChangeEvent e) {
      fireStateChanged();
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
   
   // return the context image with the given imageId or null if such contextimage doesnot exist
   public ContextImage getContextImage(String imageId){
      for (Step step : getSteps()){         
         for (ContextImage contextImage : step.getSpritesOfClass(ContextImage.class)){
            if (contextImage.getImageId().equals(imageId)){
               return contextImage;
            }
         }         
      }    
      return null;
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

