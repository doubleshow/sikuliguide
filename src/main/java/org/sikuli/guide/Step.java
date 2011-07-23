package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
   ArrayList<Sprite> _spriteList = new ArrayList<Sprite>();
   
   AbstractContextImage _contextImage;
   public AbstractContextImage getContextImage(){
      return _contextImage;
   }
   public void setContextImage(AbstractContextImage contextImage){
      _contextImage = contextImage;
   }
   
   public List<Sprite> getSprites(){
      return _spriteList;
   }
   
   public void addSprite(Sprite sprite) {
      _spriteList.add(sprite);      
      sprite.addPropertyChangeListener(this);   
      fireStateChanged();
      //fireDataContentsChanged();   
   }
   
   public void addSprite(int index, Sprite sprite){
      _spriteList.add(index,sprite);      
      sprite.addPropertyChangeListener(this);   
      fireStateChanged();      
   }
   
   public void removeSprite(Sprite sprite){
      _spriteList.remove(sprite);      
      sprite.removePropertyChangeListener(this);            
      fireStateChanged();
      //fireDataContentsChanged();
   }
   
   public void removeSprite(final int index){
      final Sprite spriteRemoved = _spriteList.remove(index);      
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
      relationships.add(rel);
   }

   public List<Relationship> getRelationships() {
      return relationships;
   }
   
   public void removeRelationship(Relationship rel) {
      relationships.remove(rel);      
   }
   
   public void removeRelationship(Sprite s) {
      List<Relationship> newList = new ArrayList<Relationship>();
      for (Relationship rel : relationships){         
         if (rel.getParent() != s && rel.getDependent() != s){
            newList.add(rel);
         }
      }
      relationships = newList;
   }

}
