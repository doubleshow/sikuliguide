package org.sikuli.guide;

import java.util.ArrayList;
import java.util.List;

public class SpriteViewGroup {      
   List<SpriteView> views = new ArrayList<SpriteView>();
   float opacity;
   public void setOpacity(float opacity){
      for (SpriteView view : views){
         view.setOpacity(opacity);
         view.repaint();
      }
      this.opacity = opacity;
   }
   
   public float getOpacity(){
      return opacity;
   }

   public void setVisible(boolean b) {
      for (SpriteView view : views){
         view.setVisible(b);
         view.repaint();
      }
      
   }
}