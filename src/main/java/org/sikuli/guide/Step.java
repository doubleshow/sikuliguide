package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Step implements PropertyChangeListener {
   
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
      //fireDataContentsChanged();   
   }
   
   public void removeSprite(Sprite sprite){
      _spriteList.remove(sprite);      
      sprite.removePropertyChangeListener(this);            
      //fireDataContentsChanged();
   }


   @Override
   public void propertyChange(PropertyChangeEvent e) {
      
   }

}
