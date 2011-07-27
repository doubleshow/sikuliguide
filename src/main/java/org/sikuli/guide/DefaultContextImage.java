package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

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

@Root
class DefaultContextImage extends DefaultSprite 
   implements ContextImage, Serializable, Bundleable {
   
   transient BufferedImage image = null;   
   SerializableBufferedImage serializableImage;

   @Attribute
   private String imageId = "";

//   @Attribute
//   public String getImageId(){
//      System.out.println(""+getX() +","+ getY());
//      if (imageId == null){
//         //imageId = UUID.randomUUID().toString();
//      }
//      return imageId;
//   }
//   @Attribute
//   public void setImageId(String imageId){
//      this.imageId = imageId;
//   }
   
   String getImageFilename(){
      //return "image-" + getImageId() + ".png";
      return "image-" + imageId + ".png";
   }
   
   
//   @Override
//   // TODO: implement the correct behavior
   public boolean isDirty() {
      return true;
   }
   
   @Override
   public void writeToBundle(File bundlePath) throws IOException{
      File file = new File(bundlePath, getImageFilename());
      
      if (isDirty() && !file.exists())     
         ImageIO.write(getBufferedImage(), "png", file);
   }
   
   @Override
   public void readFromBundle(File bundlePath) throws IOException{
      File file = new File(bundlePath, getImageFilename());
      System.out.println(file.getAbsolutePath());
      image = ImageIO.read(file);
   }
   
   DefaultContextImage(File file) throws IOException{      
      image = ImageIO.read(file);
      serializableImage = new SerializableBufferedImage(image);
   }
   
   DefaultContextImage(){      
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