package org.sikuli.guide;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

interface PointingText extends Text {
   
   public void setDirection(int direction);
   public int getDirection();
   
   static public final String PROPERTY_DIRECTION = "direction";
   
   public final static int DIRECTION_EAST = 1;
   public final static int DIRECTION_WEST = 2;
   public final static int DIRECTION_SOUTH = 3;
   public final static int DIRECTION_NORTH = 4;
}

@Root
class DefaultPointingText 
   extends DefaultText implements PointingText{
   
   public DefaultPointingText(String text, int direction) {
      super(text);
      setDirection(direction);
   }
   
   public DefaultPointingText(String text) {
      super(text);
      setDirection(PointingText.DIRECTION_EAST);
    }
   
   public DefaultPointingText() {
      super();
      setDirection(PointingText.DIRECTION_EAST);      
    }

   @Element
   private int direction;
   
   @Override   
   public void setDirection(int direction) {
      this.pcs.firePropertyChange(PROPERTY_DIRECTION, this.direction, this.direction = direction);
   }

   @Override
   public int getDirection(){
      return direction;
   }
   
}