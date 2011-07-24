package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

interface ContextImage extends Sprite {   
   BufferedImage getBufferedImage();  
}

class ContextImageView extends SpriteView {
   
   ContextImageView(ContextImage contextImage){
      super(contextImage);
      setLayout(null);
      
      JLabel label = new JLabel();
      BufferedImage b = contextImage.getBufferedImage();      
      ImageIcon icon = new ImageIcon(b);
      label.setIcon(icon);
      label.setSize(new Dimension(b.getWidth(), b.getHeight()));
      label.setLocation(0,0);
      setName("ContextImage");
      add(label);
      //setSize(label.getSize());
   }
   
}

class DefaultContextImage extends DefaultSprite implements ContextImage, Serializable {
   
   transient BufferedImage image = null;   
   SerializableBufferedImage serializableImage;
   
   DefaultContextImage(File file) throws IOException{      
      image = ImageIO.read(file);
      serializableImage = new SerializableBufferedImage(image);
   }
   
   public int getWidth(){
      return getBufferedImage().getWidth();
   }
   
   public int getHeight(){
      return getBufferedImage().getHeight();
   }
   
   public BufferedImage getBufferedImage(){
      if (image == null && serializableImage != null)
         image = serializableImage.getBufferedImage(); 
      return image;
   }

}


class SerializableBufferedImage implements Serializable {
   
   private byte[] byteImage = null;
 
   public SerializableBufferedImage(BufferedImage bufferedImage) {
      this.byteImage = toByteArray(bufferedImage);
   }
 
   public BufferedImage getBufferedImage() {
      return fromByteArray(byteImage);
   }
 
   private BufferedImage fromByteArray(byte[] imagebytes) {
      try {
         if (imagebytes != null && (imagebytes.length > 0)) {
            BufferedImage im = ImageIO.read(new ByteArrayInputStream(imagebytes));
            return im;
         }
         return null;
      } catch (IOException e) {
         throw new IllegalArgumentException(e.toString());
      }
   }
 
   private byte[] toByteArray(BufferedImage bufferedImage) {
      if (bufferedImage != null) {
         BufferedImage image = bufferedImage;
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try {
            ImageIO.write(image, "png", baos);
         } catch (IOException e) {
            throw new IllegalStateException(e.toString());
         }
         byte[] b = baos.toByteArray();
         return b;
      }
      return new byte[0];
   }
}