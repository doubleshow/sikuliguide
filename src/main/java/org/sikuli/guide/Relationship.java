package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

interface Relationship {   
   Sprite getParent();
   Sprite getDependent();
   void setParent(Sprite parent);
   void setDependent(Sprite dependent);   
   void update();
}

class DefaultRelationship implements Relationship, PropertyChangeListener {
   
   DefaultRelationship(Sprite parent, Sprite dependent){
      setParent(parent);
      setDependent(dependent);
   }
   
   private Sprite parent;
   private Sprite dependent;
   
   @Override
   public void setParent(Sprite parent) {
      this.parent = parent;
   }
   @Override
   public Sprite getParent() {
      if (this.parent != null){
         this.parent.removePropertyChangeListener(this);
      }

      if (parent != null)      
         parent.addPropertyChangeListener(this);      
      return parent;
   }
   @Override
   public void setDependent(Sprite dependent) {
      this.dependent = dependent;
   }
   @Override
   public Sprite getDependent() {
      return dependent;
   }
   @Override
   public void update(){      
   }
   
   @Override
   public void propertyChange(PropertyChangeEvent e) {
      update();
   }
   
}

class SideRelationship extends DefaultRelationship{
   Side side;
   public SideRelationship(Sprite p, Sprite d, Side side) {
      super(p,d);
      this.side = side;
      update();
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
   public void update(){
      super.update();
            
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
