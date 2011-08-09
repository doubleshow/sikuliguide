package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.ListModel;
import javax.swing.event.ChangeListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

import org.sikuli.ui.Bundleable;
import org.sikuli.ui.DefaultSlide;
import org.sikuli.ui.Slide;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

@Root
@Order(elements = {"sprites", "relationships"})
public class Step extends DefaultSlide implements PropertyChangeListener {
   
   public Step(){
      super();
   }
   
   Step createCopy() throws Exception{
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);
      
      
      for (Sprite sprite : getSprites()){
         if (sprite instanceof DefaultContextImage){
            ((DefaultContextImage) sprite).exportTemporaryImageFileForTransfer();
         }
      }
      
      StringWriter writer = new StringWriter();
      serializer.write(this, writer);
      
      StringReader reader = new StringReader(writer.toString());
      return serializer.read(Step.class, reader);      
   }
   
   void removeAllSprites(){
      //_spriteList = new DefaultListModel();
      for (int i = _spriteList.getSize()-1; i >= 0; --i){         
         removeSprite(i);         
      }      
   }   
   
   DefaultListModel _spriteList = new DefaultListModel();
   
   int canvasWidth;
   int canvasHeight;   
   
//   @Element
//   ContextImage _contextImage;
//   public ContextImage getContextImage(){
//      return _contextImage;
//   }   
   
//   public void setContextImage(ContextImage contextImage){
//      _contextImage = contextImage;
//      _spriteList.removeElement(_contextImage);
//      _spriteList.addElement(_contextImage);
//   }
   
   public List<Target> getTargets(){
      List<Target> aList = new ArrayList<Target>();
      for (int i = 0; i < _spriteList.getSize(); ++i){
         if (_spriteList.getElementAt(i) instanceof Target){         
            aList.add((Target) _spriteList.getElementAt(i));
         }
      }
      return aList;
   }
   

   
   public void addSprite(Sprite sprite) {
      _spriteList.addElement(sprite);      
      sprite.addPropertyChangeListener(this);   
      fireStateChanged();
      //fireDataContentsChanged();   
   }
   
   
   public int indexOf(Sprite sprite){
      return _spriteList.indexOf(sprite);
   }
   
   public void addSprite(int index, Sprite sprite){
      _spriteList.add(index,sprite);      
      sprite.addPropertyChangeListener(this);   
      fireStateChanged();      
   }
   
   public void removeSprite(Sprite sprite){
      _spriteList.removeElement(sprite);    
      removeRelationships(sprite);
      sprite.removePropertyChangeListener(this);            
      fireStateChanged();
      //fireDataContentsChanged();
   }
   
   public void removeSprite(final int index){
      final Sprite spriteRemoved = (Sprite) _spriteList.getElementAt(index);
      _spriteList.removeElement(index);      
      removeRelationships(spriteRemoved);
      spriteRemoved.removePropertyChangeListener(this);            
      fireStateChanged();
      //fireDataContentsChanged();
      
//      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
//         public void undo() throws CannotUndoException {
//            super.undo();
//            addSprite(index, spriteRemoved);
//         }
//      });      

   }



   @Override
   public void propertyChange(PropertyChangeEvent e) {
      
   }
   
   @ElementList
   private List<Relationship> relationships = new ArrayList<Relationship>();
   public void addRelationship(Relationship rel) {      
      // do not allow self-referencing relationship
      if (rel.getParent() == rel.getDependent())
         return;      
      relationships.add(rel);
      fireStateChanged();
   }

   public List<Relationship> getRelationships() {
      return relationships;
   }
   
   public List<Relationship> getRelationships(Sprite sprite) {
      List<Relationship> newList = new ArrayList<Relationship>();
      for (Relationship rel : relationships){         
         if (rel.getParent() == sprite || rel.getDependent() == sprite){
            newList.add(rel);
         }
      }
      return newList;
   }
   
   public List<Sprite> getDependentsOf(Sprite sprite){
      List<Sprite> dependents = new ArrayList<Sprite>();
      for (Relationship r : relationships){
         if (r.getParent() == sprite){
            dependents.add(r.getDependent());
         }        
      }
      return dependents;
   }
   
   public void removeRelationship(Relationship rel) {
      rel.setParent(null); // TODO: this is a hack to remove property listeners to parent
      rel.setDependent(null);
      relationships.remove(rel);   
      fireStateChanged();
   }
   
   public void removeRelationships(Sprite s) {
      List<Relationship> toRemove = getRelationships(s);
      for (Relationship r : toRemove){
         removeRelationship(r);
      }
   }
   
   
   
   
   @ElementList
   void setSprites(List<Sprite> sprites){
      removeAllSprites();
      for (Sprite sprite : sprites){
         addSprite(sprite);
      }
   }
   @ElementList
   public List<Sprite> getSprites(){
      List<Sprite> aList = new ArrayList<Sprite>();
      for (int i = 0; i < _spriteList.getSize(); ++i){
         aList.add((Sprite) _spriteList.getElementAt(i));
      }
      return aList;
   }

   public List<? super ContextImage> getContextImages() {
      List<ContextImage> aList = new ArrayList<ContextImage>();
      for (int i = 0; i < _spriteList.getSize(); ++i){
         Sprite sprite = (Sprite) _spriteList.getElementAt(i);
         if (sprite instanceof ContextImage)
            aList.add((ContextImage)sprite);
      }
      return aList;
   }

   public <T> List<T> getSpritesOfClass(Class<? extends T> type) {
      List<T> list = new ArrayList<T>();
      for (Sprite sprite : getSprites()){
         if (type.isInstance(sprite)){
            list.add((T) sprite);
         }
      }
      return list;
   }

}
