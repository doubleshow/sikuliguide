package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

interface Target extends StyledSprite {
   public BufferedImage getBufferedImage();
   public boolean isFound();
   public void setFound(boolean visible);
}

class DefaultTarget extends DefaultSprite implements Target{

   boolean visible = false;
   transient BufferedImage image;
   public DefaultTarget(BufferedImage image) {
      super(0,0,image.getWidth(), image.getHeight());
      this.image = image;
   }
   
   public DefaultTarget(){     
   }

   @Override
   public boolean isFound() {
      return false;
   }

   @Override
   public void setFound(boolean visible) {
      this.visible = visible;
   }

   @Override
   public BufferedImage getBufferedImage() {
      return image;
   }

}

class ContextTarget extends DefaultTarget {
   
   
   private ContextImage contextImage;
   private BufferedImage image = null;
   
   ContextTarget(ContextImage contextImage){
      this.contextImage = contextImage;
   }
      
   ContextTarget(){      
   }
   
   // create a context target with the same spatial properties of the source target
   ContextTarget(ContextImage contextImage, Target source){
      setX(source.getX());
      setY(source.getY());
      setWidth(source.getWidth());
      setHeight(source.getHeight());
      this.contextImage = contextImage;
   }   

   
   BufferedImage crop(BufferedImage src, Rectangle rect){
      BufferedImage dest = new BufferedImage((int)rect.getWidth(), (int)rect.getHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics g = dest.getGraphics();
      g.drawImage(src, 0, 0, (int)rect.getWidth(), (int)rect.getHeight(), (int)rect.getX(), (int)rect.getY(),  (int)rect.getX() +  (int)rect.getWidth(),  (int)rect.getY() + (int)rect.getHeight(), null);
      g.dispose();
      return dest;
   }
   
   @Override
   public BufferedImage getBufferedImage(){
      if (image == null){
         
         // compute the area within the context image occupied by the target
         Rectangle area = getBounds(); // target's bounds relative to Canvas           
         area.x -= contextImage.getX();   // translate to context image's coordinate system
         area.y -= contextImage.getY();
         
         image = crop(contextImage.getBufferedImage(), area);
      }
      return image;
   }
   
   public void setContextImage(DefaultContextImage contextImage) {
      this.contextImage = contextImage;
   }


   public ContextImage getContextImage() {
      return contextImage;
   }
}


class TargetView extends SpriteView {

   Target _target;
   public TargetView(Target targetSprite) {
      super(targetSprite);
      _target = targetSprite;
      setOpaque(false);
      //setLayout(new MigLayout());
      setLayout(new BorderLayout());
//      setMinimumSize(new Dimension(25,25));
//      updateBounds();
      URL imageURL = getClass().getResource("images/anchor.png");
      if (imageURL != null) {
          ImageIcon icon = new ImageIcon(imageURL);
          JLabel label = new JLabel(icon);
          //add(label);
          add(label,BorderLayout.CENTER);
      }
      //validate();
   }
   
   @Override
   protected void updateBounds(){
      super.updateBounds();
      validate();
   }   
   
   @Override
   public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;
         if (true){
            Rectangle r = getBounds();
            g2d.setColor(getForeground());
            g2d.drawRect(0,0,r.width-1,r.height-1);
            g2d.setColor(Color.white);
            g2d.drawRect(1,1,r.width-3,r.height-3);
            g2d.setColor(getForeground());
            g2d.drawRect(2,2,r.width-5,r.height-5);
            g2d.setColor(Color.white);
            g2d.drawRect(3,3,r.width-7,r.height-7);
         }else{
            Rectangle r = getBounds();
            g2d.setColor(Color.red);            
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawRect(1,1,r.width-3,r.height-3);
         }
   }
   
}