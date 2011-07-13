package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
class ContextImage implements AbstractContextImage {
   
   BufferedImage image;
   ContextImage(File file) throws IOException{      
      image = ImageIO.read(file);
   }
   
   public int getWidth(){
      return image.getWidth();
   }
   
   public int getHeight(){
      return image.getHeight();
   }
   
   public BufferedImage getBufferedImage(){
      return image;
   }   
}