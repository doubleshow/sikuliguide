package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

interface Relationship {   
   Sprite getParent();
   Sprite getDependent();
   void setParent(Sprite parent);
   void setDependent(Sprite dependent);   
   void update(Sprite source);
}

@Root
class DefaultRelationship implements Relationship, PropertyChangeListener {
   
   DefaultRelationship(Sprite parent, Sprite dependent){
      setParent(parent);
      setDependent(dependent);
   }
   
   DefaultRelationship(){   
   }
   
   private Sprite parent;
   
   private Sprite dependent;
   
   @Element
   @Override
   public void setParent(Sprite parent) {
      if (this.parent != null){
         this.parent.removePropertyChangeListener(this);
      }
      if (parent != null)      
         parent.addPropertyChangeListener(this);      
      this.parent = parent;
   }
   
   @Element
   @Override
   public Sprite getParent() {
      return parent;
   }
   
   @Element   
   @Override
   public void setDependent(Sprite dependent) {
      if (getDependent() != null)
         getDependent().removePropertyChangeListener(this);
      if (dependent != null)
         dependent.addPropertyChangeListener(this);
      this.dependent = dependent;
   }
   
   @Element
   @Override
   public Sprite getDependent() {      
      return dependent;
   }
   @Override
   public void update(Sprite source){      
   }
   
   @Override
   public void propertyChange(PropertyChangeEvent e) {
      update((Sprite)e.getSource());
   }
   
}

@Root
class OffsetRelationship extends DefaultRelationship{
   
   OffsetRelationship() {
   }

   
   @Attribute
   int offsetX;
   @Attribute
   int offsetY;
   
   OffsetRelationship(Sprite parent, Sprite dependent){
      super(parent, dependent);
      offsetX = dependent.getX() - parent.getX();
      offsetY = dependent.getY() - parent.getY();  
   }
   
   @Override
   public void update(Sprite source){
      if (source == getParent()){
         getDependent().setX(getParent().getX() + offsetX);
         getDependent().setY(getParent().getY() + offsetY);
      }else{
         offsetX = getDependent().getX() - getParent().getX();
         offsetY = getDependent().getY() - getParent().getY();      
      }
   }      
   
}

@Root
class SideRelationship extends DefaultRelationship{
   
   @Attribute
   Side side;
   
   public SideRelationship(Sprite p, Sprite d, Side side) {
      super(p,d);
      this.side = side;
      update(p);
   }
   
   SideRelationship() {
   }

   public enum Side{
      ABOVE,
      BELOW,
      LEFT,
      RIGHT,
      IN,
      SURROUND
   };
   
   @Override
   public void update(Sprite source){
      super.update(source);
            
      Rectangle r = new Rectangle(getParent().getX(), getParent().getY(), getParent().getWidth(), getParent().getHeight());
      Dimension d = new Dimension(getDependent().getWidth(), getDependent().getHeight());
            
      int x=0;
      int y=0;
      if (side == Side.ABOVE){
         x = r.x + r.width/2 - d.width/2;
         y = r.y - d.height;
      } else if (side == Side.BELOW){
         x = r.x + r.width/2 - d.width/2;
         y = r.y + r.height;
      } else if (side == Side.LEFT){
         x = r.x - d.width;
         y = r.y + r.height/2 - d.height/2;   
      } else if (side == Side.RIGHT){
         x = r.x + r.width;
         y = r.y + r.height/2 - d.height/2;                  
      } else if (side == Side.IN){
         x = r.x + r.width/2 - d.width/2;
         y = r.y + r.height/2 - d.height/2;                  
      } else if (side == Side.SURROUND){
         x = r.x;
         y = r.y;         
         getDependent().setWidth(r.width);
         getDependent().setHeight(r.height);
      } 
      
      getDependent().setX(x);
      getDependent().setY(y);           
   }      

}
