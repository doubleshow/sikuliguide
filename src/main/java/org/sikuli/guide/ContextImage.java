package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

interface AbstractContextImage {   
   BufferedImage getBufferedImage();  
   int getWidth();
   int getHeight();
}

class ContextImageView extends JLabel {
   
   ContextImageView(AbstractContextImage contextImage){
      BufferedImage b = contextImage.getBufferedImage();      
      ImageIcon icon = new ImageIcon(b);
      setIcon(icon);
      setSize(new Dimension(b.getWidth(), b.getHeight()));
      setName("ContextImage");
   }
   
   
}



class ContextImage implements AbstractContextImage, Serializable {
   
   transient BufferedImage image = null;   
   SerializableBufferedImage serializableImage;
   
   ContextImage(File file) throws IOException{      
      image = ImageIO.read(file);
      serializableImage = new SerializableBufferedImage(image);
   }
   
   public int getWidth(){
      return image.getWidth();
   }
   
   public int getHeight(){
      return image.getHeight();
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