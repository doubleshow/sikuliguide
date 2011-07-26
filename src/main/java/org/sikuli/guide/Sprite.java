package org.sikuli.guide;

import java.awt.Color;
import java.beans.PropertyChangeListener;



interface StyledSprite extends Sprite {
   public void setOpacity(float opacity);
   public float getOpacity();   
   public boolean isWithShadow();
   public void setWithShadow(boolean hasShadow);      
   public Color getBackground();
   public Color getForeground();   
   public void setBackground(Color color);
   public void setForeground(Color color);
   
   static public final String PROPERTY_OPACITY = "opacity";
   static public final String PROPERTY_FOREGROUND = "foreground";
   static public final String PROPERTY_BACKGROUND = "background";
}

public interface Sprite {
   public void setX(int x);
   public int getX();
   public void setY(int y);
   public int getY();   
   public void setWidth(int width);
   public int getWidth();
   public void setHeight(int width);
   public int getHeight();
   
   public String getName();
   public void setName(String name);
   
   
   public void addPropertyChangeListener(PropertyChangeListener listener);
   public void removePropertyChangeListener(PropertyChangeListener listener);
   
   static public final String PROPERTY_HEIGHT = "height";
   static public final String PROPERTY_WIDTH = "width";
   static public final String PROPERTY_X = "x";
   static public final String PROPERTY_Y = "y";   
   static public final String PROPERTY_NAME = "name";   
}
