package org.sikuli.guide;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.sikuli.ui.DefaultSlide;
import org.sikuli.ui.DefaultSlideDeck;
import org.sikuli.ui.Deck;
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
      return getElements();
   }
   
   @ElementList
   public void setSteps(List<Step> steps){
      clear();
      for (Step step : steps){
         insertElementAt(step, getSize());
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
      list.add(this);      
      for (Step step : getSteps()){
         list.add((Bundleable) step.getContextImage());
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
      bundlealeAdapter.readFromBundle(bundlePath);
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

interface BundleableDocumentEditor {
   BundleableDocument getBundleableDocument();   
}

interface BundleableDocument extends Bundleable {
   List<Bundleable> getBundleables();
}

interface Bundleable{
   public void writeToBundle(File bundlePath) throws IOException;
   public void readFromBundle(File bundlePath) throws IOException;
}

class XMLBundleableAdapter implements Bundleable{
   private Strategy strategy = new CycleStrategy("id","ref");
   private Serializer serializer = new Persister(strategy);    
   
   Object object;
   XMLBundleableAdapter(Object object){
      this.object = object;
   }

   @Override
   public void writeToBundle(File bundlePath) throws IOException {
      try {
         serializer.write(object, new File(bundlePath, "data.xml"));
      } catch (Exception e) {
         throw new IOException("can not write to bundle path: " + bundlePath.getAbsolutePath());
      }         
   }

   @Override
   public void readFromBundle(File bundlePath) throws IOException {
      try{
         serializer.read(object, new File(bundlePath, "data.xml"));
      } catch (Exception e) {
         throw new IOException("can not read from bundle path: " + bundlePath.getAbsolutePath() + " because " + e.getMessage());
      }         
   }
}

class BundlePersisterSupport {
   
//   Document source;
//   BundlePersisterSupport(Document source){
//      this.source = source;
//   }
//   
   final static String SAVE = "save";
   final static String LOAD = "load";
   class BundleAction extends AbstractAction {

      @Override
      public void actionPerformed(ActionEvent e) {
         
         if (!(e.getSource() instanceof BundleableDocumentEditor)){
            return;
         }
         
         BundleableDocumentEditor editor = (BundleableDocumentEditor) e.getSource();
         BundleableDocument document = editor.getBundleableDocument();

         try {
         
            if (e.getActionCommand().equals(SAVE)){
               save(document);
            }else if (e.getActionCommand().equals(LOAD)){
               load(document);
            }
         
         
         } catch (Exception e1) {
            e1.printStackTrace();
         }

         
      }
      
      void save(BundleableDocument source) throws Exception {
         prepareBundlePath();
         clearBundle();
         source.writeToBundle(bundlePath);
         for (Bundleable b : source.getBundleables()){
            b.writeToBundle(getBundlePath());            
         }
      }
      
      void load(BundleableDocument source) throws Exception {
         prepareBundlePath();
         source.readFromBundle(bundlePath);
         for (Bundleable b : source.getBundleables()){
            b.readFromBundle(getBundlePath());            
         }
      }
   }
   
   
   
   Action saveAction = new BundleAction() {
      {
         putValue(Action.ACTION_COMMAND_KEY, SAVE);
      }
   };
   
   Action loadAction = new BundleAction() {
      {
         putValue(Action.ACTION_COMMAND_KEY, LOAD);
      }
   };
   
   File bundlePath;   
   File getBundlePath(){
      return bundlePath;
   }
   void setBundlePath(File bundlePath){
      this.bundlePath = bundlePath;
   }
   
   void clearBundle(){
      // TODO: delete only those image files no longer
      // referred to by the story
      for (File file : getBundlePath().listFiles(new FileFilter(){

         @Override
         public boolean accept(File f) {
            return f.getAbsolutePath().endsWith("png");
         }
         
      })){
         
         file.delete();
         
      }      
   }
   
   void prepareBundlePath(){
      if (getBundlePath() == null){
         // TODO: popup a dialog tochoose a bundlepath
         setBundlePath(new File("bundle"));
      }
      
      
      if (!getBundlePath().exists()){
         getBundlePath().mkdir();
      }
   }
   
//   String getXMLFilename(){
//      return "test.xml";
//   }
//   
//   File getXMLFile(){
//      return new File(getBundlePath(), getXMLFilename());
//   }

//   private Strategy strategy = new CycleStrategy("id","ref");
//   private Serializer serializer = new Persister(strategy);         
//
//   void saveStory(Story story) throws Exception{
//      prepareBundlePath();         
//      serializer.write(story, getXMLFile());         
//      for (Step step : story.getSteps()){            
//         ((DefaultContextImage)step.getContextImage()).writeToBundle(getBundlePath());
//      }
//
//   }
//   
//   Story loadStory() throws Exception{
//      Story story = serializer.read(Story.class, getXMLFile());
//      for (Step step : story.getSteps()){            
//         ((DefaultContextImage)step.getContextImage()).readFromBundle(getBundlePath());
//      }         
//      return story;
//   }
//   
//   <T> void save(T object) throws Exception {
//      serializer.write(object, getXMLFile());
//   }
//   
//   <T> T load(Class<? extends T> klass) throws Exception{
//      return serializer.read(klass, getXMLFile());
//   }
//   
//   void saveStepImages(Story story) throws IOException{         
//      for (Step step : story.getSteps()){            
//         ((DefaultContextImage)step.getContextImage()).writeToBundle(getBundlePath());
//      }         
//   }
}
