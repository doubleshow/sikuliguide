package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

public interface Sprite {
   public void setX(int x);
   public int getX();
   public void setY(int y);
   public int getY();   
   public void setWidth(int width);
   public int getWidth();
   public void setHeight(int width);
   public int getHeight();
   public void setOpacity(float opacity);
   public float getOpacity();   
   public boolean isWithShadow();
   public void setWithShadow(boolean hasShadow);
   
   public String getName();
   public void setName(String name);
   
   public Color getBackground();
   public Color getForeground();
   
   public void setBackground(Color color);
   public void setForeground(Color color);

   
   public void addPropertyChangeListener(PropertyChangeListener listener);
   public void removePropertyChangeListener(PropertyChangeListener listener);
   
   static public final String PROPERTY_HEIGHT = "height";
   static public final String PROPERTY_WIDTH = "width";
   static public final String PROPERTY_X = "x";
   static public final String PROPERTY_Y = "y";   
   static public final String PROPERTY_NAME = "name";   
   static public final String PROPERTY_OPACITY = "opacity";
   static public final String PROPERTY_FOREGROUND = "foreground";
   static public final String PROPERTY_BACKGROUND = "background";
}


@Root
class DefaultSprite implements Sprite {
   
   public DefaultSprite(){      
   }
   
   public DefaultSprite(int x, int y, int width, int height){
      setX(x);
      setY(y);
      setWidth(width);
      setHeight(height);
   }
       
   @Override
   public Object clone() throws CloneNotSupportedException{
      DefaultSprite o = (DefaultSprite) super.clone();
      o.x = x;
      o.y = y;
      o.width = width;
      o.height = height;
      o.backgroundColor = backgroundColor;
      o.foregroundColor = foregroundColor;
      o.withShadow = withShadow;
      o.opacity = opacity;
      return o;
   }
   
   protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
   
   public void addPropertyChangeListener( PropertyChangeListener listener ){
      this.pcs.addPropertyChangeListener( listener );
   }

   public void removePropertyChangeListener( PropertyChangeListener listener ){
      this.pcs.removePropertyChangeListener( listener );
   }

   @Attribute
   private int x = 0;
   @Attribute 
   private int y = 0;
   @Attribute
   private int width = 0;
   @Attribute
   private int height = 0;
   @Attribute
   private String name = "";
   
   public int padding=5;

   @Attribute
   private boolean withShadow;
   
   @Attribute 
   private float opacity = 1f;
   
   @Attribute
   private String backgroundColor = "FFFFFFFF";
   
   @Attribute
   private String foregroundColor = "FF000000";
   
   boolean selected = false;
   
   public void setLocation(Point location) {
      setLocation(x,y);
   }
   public void setLocation(int x, int y) {
      setX(x);
      setY(y);
   }

   public Point getLocation() {
      return new Point(x,y);
   }
   public void setSize(Dimension size) {
      setSize(size.width,size.height);
   }
   
   public void setSize(int width, int height) {
      setWidth(width);
      setHeight(height);
   }

   public Dimension getSize() {
      return new Dimension(width,height);
   }
   
   
   String color2String(Color color){
      return Integer.toHexString(color.getRGB());
   }
   
   Color string2Color(String colorString){
      int rgb = Long.decode("0x"+colorString).intValue();
      return new Color(rgb);
   }
   
   @Override
   public void setBackground(Color color) {
      this.pcs.firePropertyChange(PROPERTY_BACKGROUND, this.backgroundColor,  this.backgroundColor = color2String(color));
   }
   
   @Override
   public Color getBackground() {
      return string2Color(backgroundColor);
   }
   
   @Override
   public void setForeground(Color color) {
      this.pcs.firePropertyChange(PROPERTY_FOREGROUND, this.foregroundColor,  this.foregroundColor = color2String(color));
   }
   
   @Override
   public Color getForeground() {
      return string2Color(foregroundColor);
   }
   
   @Override
   public String getName(){
      return name;
   }
   
   @Override
   public void setName(String name){
      this.pcs.firePropertyChange(PROPERTY_NAME, this.name, this.name = name);
   }
      
   @Override
   public void setWithShadow(boolean hasShadow) {
      this.withShadow = hasShadow;
   }
   
   @Override
   public boolean isWithShadow() {
      return withShadow;
   }
   
   @Override
   public void setOpacity(float opacity) {
      this.pcs.firePropertyChange(PROPERTY_HEIGHT, this.opacity, this.opacity = opacity);
   }
   
   @Override
   public float getOpacity() {
      return opacity;
   }

   public Rectangle getBounds() {
      return new Rectangle(getLocation(), getSize());
   }

   public void setBounds(Rectangle bounds) {
      setLocation(bounds.getLocation());
      setSize(bounds.getSize());
   }

   @Override
   public int getHeight() {
      return height;
   }

   @Override
   public int getWidth() {
      return width;
   }

   @Override
   public int getX() {
      return x;
   }

   @Override
   public int getY() {
      return y;
   }

   
   @Override
   public void setHeight(int height) {
      int old = this.height;
      this.height = height;
      this.pcs.firePropertyChange(PROPERTY_HEIGHT, old, height);
   }

   @Override
   public void setWidth(int width) {
      int old = this.width;
      this.width = width;
      this.pcs.firePropertyChange(PROPERTY_WIDTH, old, width);
   }

   @Override
   public void setX(int x) {
      int old = this.x;
      this.x = x;
      this.pcs.firePropertyChange(PROPERTY_X, old, x);      
   }

   @Override
   public void setY(int y) {
      this.pcs.firePropertyChange(PROPERTY_Y, this.y, this.y = y);      
   }
   
}
