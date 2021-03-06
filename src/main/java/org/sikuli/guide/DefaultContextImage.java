package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import org.sikuli.ui.Bundleable;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Validate;

abstract class ContextImage extends DefaultSprite implements Bundleable{   
   abstract BufferedImage getBufferedImage();
   @Attribute
   private int originalWidth;
   @Attribute
   private int originalHeight;

   public void setOriginalWidth(int originalWidth) {
      this.originalWidth = originalWidth;
   }
   public int getOriginalWidth() {
      return originalWidth;
   }
   public void setOriginalHeight(int originalHeight) {
      this.originalHeight = originalHeight;
   }
   public int getOriginalHeight() {
      return originalHeight;
   }      


   @Attribute
   String imageId = "";

   public String getImageId(){
      return imageId;
   }
   public void setImageId(String imageId){
      this.imageId = imageId;
   }
}

class ContextImageView extends SpriteView {

   ContextImageView(ContextImage contextImage){
      super(contextImage);
      setLayout(null);

      //      JLabel label = new JLabel();
      //      BufferedImage b = contextImage.getBufferedImage();
      //      if (b == null){
      //         return;
      //      }
      //      
      //      ImageIcon icon = new ImageIcon(b);
      //      label.setIcon(icon);
      //      label.setSize(new Dimension(b.getWidth(), b.getHeight()));
      //      label.setLocation(0,0);
      //      setName("ContextImage");
      //      add(label);
      //setSize(label.getSize());
   }

   ContextImage getContextImage(){
      return (ContextImage) _sprite;
   }

   @Override
   public void paintComponent(Graphics g){
      Graphics2D g2d = (Graphics2D) g;      
      ContextImage c = getContextImage();
      g2d.drawImage(c.getBufferedImage(), 0, 0, c.getWidth(), c.getHeight(), null);      

   }   
}

@Root
class DefaultContextImage extends ContextImage 
implements Serializable, Bundleable {

   float getAspectRatio(){
      return 1f * getBufferedImage().getHeight()/getBufferedImage().getWidth();
   }

   transient BufferedImage image = null;   
   SerializableBufferedImage serializableImage;

   @Element(required = false)
   String transferFilePath;

   String getImageFilename(){
      return "image-" + getImageId() + ".png";
   }

   @Commit
   void commit(){
      if (transferFilePath != null){
         try {
            image = ImageIO.read(new File(transferFilePath));
            transferFilePath = null;
         } catch (IOException e) {
         }
      }
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
      System.out.println(this);
      System.out.println(file.getAbsolutePath());
      image = ImageIO.read(file);
      serializableImage = new SerializableBufferedImage(image);
   }

   DefaultContextImage(File file) throws IOException{      
      image = ImageIO.read(file);
      serializableImage = new SerializableBufferedImage(image);
      imageId = UUID.randomUUID().toString();
      setWidth(image.getWidth());
      setHeight(image.getHeight());
      setOriginalWidth(image.getWidth());
      setOriginalHeight(image.getHeight());
   }

   DefaultContextImage(){      
   }

   public BufferedImage getBufferedImage(){
      if (image == null && serializableImage != null)
         image = serializableImage.getBufferedImage(); 
      return image;
   }

   public void exportTemporaryImageFileForTransfer() {
      File temp;
      try {
         temp = File.createTempFile("org.sikuli.guide.ContextImage.transfer.","png");
         temp.deleteOnExit();         
         ImageIO.write(getBufferedImage(),"png",temp);
         transferFilePath = temp.getAbsolutePath();
      } catch (IOException e) {
      }
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