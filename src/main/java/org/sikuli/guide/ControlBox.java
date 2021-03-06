/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

public class ControlBox extends DefaultSprite {

   private Sprite target;

   ControlBox(Sprite target){
      this.setTarget(target);      
   }
   
   ControlBox(){      
   }

//   public SklView createView(){   
//      return new SklControlBoxView(this);
//   }
   
   class TargetPropertyChangeListener implements PropertyChangeListener{
      @Override
      public void propertyChange(PropertyChangeEvent e) {         
         ControlBox.this.pcs.firePropertyChange(PROPERTY_TARGET, 
               null,  
               ControlBox.this.target);
      }
   }
   TargetPropertyChangeListener targetPropertyChangeListener = new TargetPropertyChangeListener();
   
   public void setTarget(Sprite target) {

      // unfollow the existing target
      if (this.target != null)
         this.target.removePropertyChangeListener(targetPropertyChangeListener);
      
      // follow the new target
      if (target != null){
         target.addPropertyChangeListener(targetPropertyChangeListener);
      }
      
      this.pcs.firePropertyChange(PROPERTY_TARGET, this.target, this.target = target);
   }

   public Sprite getTarget() {
      return target;
   }

   static public final String PROPERTY_TARGET = "target";

}

class ControlBoxView extends SpriteView {

   class ResizeMovement extends MouseAdapter {

      int xo=0,yo=0;

      @Override
      public void mouseDragged(MouseEvent e) {
         //Debug.info("dragged to: " + e.getPoint());

         Point p = e.getPoint();
         p.x -= 5;
         p.y -= 5;
         update((ControlPoint) e.getSource(), p);
      }

      @Override
      public void mousePressed(MouseEvent e) {
         xo = e.getX();
         yo = e.getY();
         //setAutoMoveEnabled(false);         
      }

      @Override
      public void mouseReleased(MouseEvent e) {
         //Debug.info("released at: " + e.getPoint());
         //setAutoMoveEnabled(true); 

         // finished resizing
         //editor.currentStepContentChanged();
      }

      void update(ControlPoint cp, Point p){
         repaint();
         int dx=0;
         int dy=0;
         int dw=0;
         int dh=0;
         if (cp  == tr){
            dw = p.x - xo;
            dy = p.y - yo;
            dh = -dy;            
         } else if (cp == tl){
            dx = p.x - xo;
            dy = p.y - yo;
            dh = -dy;
            dw = -dx;
         } else if (cp == bl){
            dx = p.x - xo;
            dh = p.y - yo;
            dw = -dx;
         } else if (cp == br){
            dx = 0;
            dy = 0;
            dw = p.x - xo;
            dh = p.y - yo;
         }

         Rectangle r0 = getBounds();
         Rectangle rect = getBounds();
         rect.x += dx;
         rect.y += dy;
         rect.height += dh;
         rect.width += dw;
         
         Sprite targetSprite = ((ControlBox) _controlBox).getTarget();
         

         
         rect.grow(-10,-10);
         if (isAspectRatioPreserving && targetSprite instanceof DefaultContextImage){            
            
            float aspectRatio = ((DefaultContextImage) targetSprite).getAspectRatio();

            int height = (int) (rect.width * aspectRatio);
            int ddh = height - rect.height;
            if (cp  == tr || cp == tl){
               rect.y = rect.y + rect.height - height;
               rect.height = height;
            } else if (cp == bl || cp == br){
               rect.height = height;
            }
            
         }
         rect.grow(10,10);
         setBounds(rect);


         updateControlPoints();

         Rectangle bounds = new Rectangle(rect);
         bounds.grow(-10,-10);
         
         bounds.x -= getOffset().x;
         bounds.y -= getOffset().y;

         targetSprite.setX(bounds.x);
         targetSprite.setY(bounds.y);
         targetSprite.setWidth(bounds.width);
         targetSprite.setHeight(bounds.height);


      }


   }
   
   boolean isAspectRatioPreserving = true;


   class ControlPoint extends JComponent{

      ControlPoint(){
         setSize(10,10);
         addMouseMotionListener(new ResizeMovement());
         addMouseListener(new ResizeMovement());
         setForeground(new Color(1f,1f,1f,0.5f));
      }

      @Override
      public void paintComponent(Graphics g){
         super.paintComponent(g);

         Graphics2D g2d = (Graphics2D) g;
         g2d.fillRect(0,0,getWidth(),getHeight());
         g2d.setColor(Color.black);
         g2d.drawRect(0,0,getWidth()-1,getHeight()-1);
      }

   }

   class ConnectControlPoint extends JComponent{

      ConnectControlPoint(){
         setSize(10,10);
         addMouseMotionListener(new ResizeMovement());
         addMouseListener(new ResizeMovement());
         setForeground(new Color(0.8f,0f,0f,0.9f));
      }

      @Override
      public void paintComponent(Graphics g){
         super.paintComponent(g);        
         Graphics2D g2d = (Graphics2D) g;
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
               RenderingHints.VALUE_ANTIALIAS_ON);                
         Ellipse2D.Double ellipse =
            new Ellipse2D.Double(0,0,getWidth(),getHeight());
         g2d.fill(ellipse);
      }
   }

   ControlPoint tl,bl,tr,br;
   ConnectControlPoint ctl,cbl,ctr,cbr;
   ControlBox _controlBox;
   public ControlBoxView()  {      
      super(new ControlBox());
      
      _controlBox = (ControlBox) getSprite();

      setLayout(null);
      setOpaque(false);
      setName("ControlBox");
      
      tl = new ControlPoint();       
      add(tl);

      bl = new ControlPoint();
      add(bl);

      br = new ControlPoint();
      add(br);

      tr = new ControlPoint();
      add(tr);
   }

   void updateTarget(){

      Rectangle r = getBounds();
      Sprite target = ((ControlBox) getModel()).getTarget();
      
      if (target == null)
         return;
      
      Rectangle rnew = new Rectangle(target.getX(), target.getY(), target.getWidth(), target.getHeight());
      rnew.x += getOffset().x;
      rnew.y += getOffset().y;
      
      rnew.grow(10,10);
      setBounds(rnew);
      updateControlPoints();

      r.add(getBounds());
      
      if (getTopLevelAncestor() != null){
         // getParent().repaint(r.x,r.y,r.width,r.height);
         getTopLevelAncestor().repaint();
      }

   }

   void updateControlPoints(){

      int w = getWidth();
      int h = getHeight();

      tl.setLocation(0,0);
      bl.setLocation(0,h-10);     
      br.setLocation(w-10,h-10);     
      tr.setLocation(w-10,0);     

      //      ctl.setLocation(w/2-5,0);

      //      cbl.setLocation(w/2-5,h-10);     
      //      cbr.setLocation(0,h/2-5);     
      //      ctr.setLocation(w-10,h/2-5);


   }

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals(ControlBox.PROPERTY_TARGET)){ 
         updateTarget();
      }
      super.propertyChange(evt);      
   }

   @Override
   public void paintComponent(Graphics g){
      super.paintComponent(g);      

      Graphics2D g2d = (Graphics2D) g;      
      g2d.setColor(new Color(1f,1f,1f,0.5f));
      g2d.drawRect(5,5,getWidth()-11,getHeight()-11);
   }

   public void setTarget(Sprite sprite) {
      _controlBox.setTarget(sprite);      
   }

}