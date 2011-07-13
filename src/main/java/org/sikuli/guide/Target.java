package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

interface Target extends Sprite {
   public BufferedImage getImage();
   public boolean isFound();
   public void setFound(boolean visible);
}

class DefaultTarget extends DefaultSprite implements Target {
   public BufferedImage getImage(){
      return null;
   }
   
   boolean visible = false;
   public boolean isFound(){
      return false;
   }
   
   public void setFound(boolean visible){
      this.visible = visible;
   }
}


class TargetView extends SpriteView {

   Target _target;
   public TargetView(Target targetSprite) {
      super(targetSprite);
      _target = targetSprite;
      setOpaque(false);
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