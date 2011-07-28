package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

@Root
public class DefaultSprite implements StyledSprite, Serializable {
   
   private static final int MIN_HEIGHT = 20;
   private static final int MIN_WIDTH = 20;
   
   public DefaultSprite(){      
   }
   
   public DefaultSprite(int x, int y, int width, int height){
      setX(x);
      setY(y);
      setWidth(width);
      setHeight(height);
   }
   
   public String toXML(){
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);
      Writer writer = new StringWriter();
      try {
         serializer.write(this, writer);
         return writer.toString(); 
      } catch (Exception e) {
         return "";
      }
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
   
   private void readObject(ObjectInputStream ois) 
   throws IOException,ClassNotFoundException {
      ois.defaultReadObject();
      System.out.println("readObject");
      pcs = new PropertyChangeSupport(this);
   }
   
   transient protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
   
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
   private int width = MIN_WIDTH;
   @Attribute
   private int height = MIN_HEIGHT;
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
      height = Math.max(MIN_HEIGHT,height);
      int old = this.height;
      this.height = height;
      this.pcs.firePropertyChange(PROPERTY_HEIGHT, old, height);
   }

   @Override
   public void setWidth(int width) {
      width = Math.max(MIN_WIDTH,width);
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