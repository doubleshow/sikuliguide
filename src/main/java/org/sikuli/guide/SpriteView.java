package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

class SpriteView extends JComponent implements PropertyChangeListener {
      
   protected Sprite _sprite;
   
   public SpriteView(Sprite sprite){
      _sprite = sprite;
      _sprite.addPropertyChangeListener(this);      
//      init();
      updateBounds();
      updateStyle();
   }
   
   // Initialize the view
   protected void init(){      
   }
   
   Point origin = new Point(0,0);
   
   protected void updateStyle(){
      setForeground(_sprite.getForeground());
      setBackground(_sprite.getBackground());
   }
   
   // Update the view based on the current attributes of the associated model
   protected void updateBounds(){
      //setForeground(model.getForeground());
      
      //origin = getModel().getStep().getView().getOrigin();
     // Point modelLocation = model.getLocation();      
      //modelLocation.translate(origin.x,origin.y);
      
      setLocation(_sprite.getX(), _sprite.getY());
      setSize(_sprite.getWidth(), _sprite.getHeight());
//      if (_sprite.isHasShadow() && shadowRenderer == null){
//         shadowRenderer = new ShadowRenderer(this, 10);
//      }
   }
   
   public void updateModelLocation(Point newLocation){
      _sprite.setX(newLocation.x - origin.x);
      _sprite.setY(newLocation.y - origin.y);
   }
      
//   Rectangle actualBounds = new Rectangle();
//   public void setActualSize(int width, int height){
//      setActualSize(new Dimension(width, height));
//   }
//   
//   public void setActualSize(Dimension actualSize){
//      
//      actualBounds.setSize(actualSize);
//      
//      Dimension paintSize = (Dimension) actualSize.clone();
//
//      if (_sprite.isHasShadow()){
//         paintSize.width += (2*shadowSize);
//         paintSize.height += (2*shadowSize);
//      }
//      super.setSize(paintSize);
//   }  
//   
//   public void setActualLocation(int x, int y){
//      setActualLocation(new Point(x,y));
//   }
//   
//   public void setActualLocation(Point p){
//      
//      int paintX = p.x;
//      int paintY = p.y;
//      
//      actualBounds.setLocation(p);
//      
//      if (_sprite.isHasShadow()){
//         paintX -= (shadowSize-shadowOffset);
//         paintY -= (shadowSize-shadowOffset);
//      }
//      
//      super.setLocation(paintX, paintY);
//   }
//   
//   public int getActualWidth(){
//      return getActualBounds().width;
//   }
//   
//   public int getActualHeight(){
//      return getActualBounds().height;
//   }
//
//   public Rectangle getActualBounds() {
//      return actualBounds;
//   }
//
//   public Dimension getActualSize() {
//      return actualBounds.getSize();
//   }
//   
//   public Point getActualLocation() {
//      return actualBounds.getLocation();
//   }
//
//   
//   ShadowRenderer shadowRenderer;
//   int shadowSize = 10;
//   int shadowOffset = 2;
//   
//   class ShadowRenderer {
//
//      SpriteView source;
//      public ShadowRenderer(SpriteView source, int shadowSize){
//         this.source = source;
//         sourceActualSize = source.getActualSize();
//         this.shadowSize = shadowSize;
//      }
//
//      float shadowOpacity = 0.8f;
//      int shadowSize = 10;
//      Color shadowColor = Color.black;
//      BufferedImage createShadowMask(BufferedImage image){ 
//         BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB); 
//
//         Graphics2D g2d = mask.createGraphics(); 
//         g2d.drawImage(image, 0, 0, null); 
//         // Ar = As*Ad - Cr = Cs*Ad -> extract 'Ad' 
//         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, shadowOpacity)); 
//         g2d.setColor(shadowColor); 
//         g2d.fillRect(0, 0, image.getWidth(), image.getHeight()); 
//         g2d.dispose(); 
//         return mask; 
//      } 
//
//      ConvolveOp getBlurOp(int size) {
//         float[] data = new float[size * size];
//         float value = 1 / (float) (size * size);
//         for (int i = 0; i < data.length; i++) {
//            data[i] = value;
//         }
//         return new ConvolveOp(new Kernel(size, size, data));
//      }
//
//      BufferedImage shadowImage = null;
//      Dimension sourceActualSize = null;
//      public BufferedImage createShadowImage(){    
//
//         BufferedImage image = new BufferedImage(source.getActualWidth() + shadowSize * 2,
//               source.getActualHeight() + shadowSize * 2, BufferedImage.TYPE_INT_ARGB);
//         Graphics2D g2 = image.createGraphics();
//         g2.translate(shadowSize,shadowSize);
//         source.setDoubleBuffered(false);
//         //source.paintPlain(g2);
//         
////         Container parent = new CellRendererPane();
////         
////         SwingUtilities.paintComponent(g2,source,parent,0,0,image.getWidth(),image.getHeight());
//
//         shadowImage = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
//         getBlurOp(shadowSize).filter(createShadowMask(image), shadowImage);
//         g2.dispose();
//         
//         //Debug.info("[Shadow] shadowImage created: " + shadowImage);
//
//         return shadowImage;
//      }
//
//      public void paintComponent(Graphics g){      
//         Graphics2D g2d = (Graphics2D)g;
//
//         // create shadow image if the size of the source component has changed since last rendering attempt
//         if (shadowImage == null || source.getActualHeight() != sourceActualSize.height || 
//               source.getActualWidth() != sourceActualSize.width){
//            createShadowImage();
//            sourceActualSize = source.getActualSize();
//         }
//         //Debug.info("[Shadow] painting shadow" + shadowImage);
//         g2d.drawImage(shadowImage, 0, 0, null, null);
//      }
//   }
//   
//   public void paintPlain(Graphics g){
//      super.paint(g);
//   }

   public void paint(Graphics g){
      
      // render the component in an offscreen buffer with shadow
      BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = image.createGraphics();
      
//      if (shadowRenderer != null){
//         shadowRenderer.paintComponent(g2);
//         g2.translate((shadowSize-shadowOffset),(shadowSize-shadowOffset));
//      }
      
      super.paint(g2);
      
      Graphics2D g2d = (Graphics2D) g;      
      ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,_sprite.getOpacity()));
      g2d.drawImage(image,0,0,null,null);


//      if (_model.isSelected()){
//          Rectangle r = getBounds();
//          g2d.setColor(getForeground());
//          g2d.drawRect(0,0,r.width-1,r.height-1);
//      }
      
      // Debug draw
      
//      Rectangle r = getBounds();
//      g2d.setColor(Color.red);
//      g2d.drawRect(0,0,r.width-1,r.height-1);
      
   }

   public Sprite getModel() {
      return _sprite;
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      
//      if (getParent() != null){
//         Rectangle r = new Rectangle(getBounds());
//         getParent().repaint(r.x,r.y,r.width,r.height);
////         getTopLevelAncestor().repaint();
//      }

      
      if (evt.getPropertyName().equals(Sprite.PROPERTY_X)){         
         updateBounds();
      } else if (evt.getPropertyName().equals(Sprite.PROPERTY_Y)){      
         updateBounds();
      } else if (evt.getPropertyName().equals(Sprite.PROPERTY_WIDTH)){
         updateBounds();
      } else if (evt.getPropertyName().equals(Sprite.PROPERTY_HEIGHT)){
         updateBounds();
      } else if (evt.getPropertyName().equals(Sprite.PROPERTY_BACKGROUND) ||
            evt.getPropertyName().equals(Sprite.PROPERTY_FOREGROUND)){
         updateStyle();   
      } else if (evt.getPropertyName().equals(Sprite.PROPERTY_OPACITY)){

      } else {  
         return;
      }

//      if (getParent() != null){
//         Rectangle r = new Rectangle(getBounds());
//         //getParent().repaint(r.x,r.y,r.width,r.height);
//         //getParent().repaint();
//         // TODO: fix this
////         if (getTopLevelAncestor() != null)
////            getTopLevelAncestor().repaint();
//         repaint();
//      }

   }
}


class ViewFactory {
   
   public static ContextImageView createView(AbstractContextImage abstractContextImage){
      return new ContextImageView(abstractContextImage);
   }
   
   public static SpriteView createView(Sprite sprite){
      if (sprite instanceof FlagText)
         return new FlagTextView((FlagText)sprite);
      else if (sprite instanceof Text)
         return new TextView((Text)sprite);
      else if (sprite instanceof Circle)
         return new CircleView((Circle)sprite);

      return new SpriteView(sprite);
      //      else if (sprite instanceof SklAnchorModel)
//         return new SklAnchorView(sprite);
//      else if (sprite instanceof SklImageModel)
//         return new SklImageView(sprite);
//      else if (sprite instanceof SklControlBox)
//         return new SklControlBoxView(sprite);
//      else         
//         return new SpriteView(sprite);
   }      
}