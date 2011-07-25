package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.ListModel;
import javax.swing.event.ChangeListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

import org.sikuli.ui.DefaultSlide;
import org.sikuli.ui.Slide;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Step extends DefaultSlide implements PropertyChangeListener {
   
   public Step(){
      super();
   }
   
   @ElementList
   DefaultListModel _spriteList = new DefaultListModel();
   
   int canvasWidth;
   int canvasHeight;   
   
   ContextImage _contextImage;
   public ContextImage getContextImage(){
      return _contextImage;
   }   
   
   public void setContextImage(ContextImage contextImage){
      _contextImage = contextImage;
      _spriteList.removeElement(_contextImage);
      _spriteList.addElement(_contextImage);
   }
   
   public List<Sprite> getSprites(){
      List<Sprite> aList = new ArrayList<Sprite>();
      for (int i = 0; i < _spriteList.getSize(); ++i){
         aList.add((Sprite) _spriteList.getElementAt(i));
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
      
      undoableEditSupport.postEdit(new AbstractUndoableEdit(){
         public void undo() throws CannotUndoException {
            super.undo();
            addSprite(index, spriteRemoved);
         }
      });      

   }



   @Override
   public void propertyChange(PropertyChangeEvent e) {
      
   }
   
   private List<Relationship> relationships = new ArrayList<Relationship>();
   public void addRelationship(Relationship rel) {      
      // do not allow self-referencing relationship
      if (rel.getParent() == rel.getDependent())
         return;      
      relationships.add(rel);
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
   
   public void removeRelationship(Relationship rel) {
      rel.setParent(null); // TODO: this is a hack to remove property listeners to parent
      rel.setDependent(null);
      relationships.remove(rel);     
   }
   
   public void removeRelationships(Sprite s) {
      List<Relationship> toRemove = getRelationships(s);
      for (Relationship r : toRemove){
         removeRelationship(r);
      }
   }

}
