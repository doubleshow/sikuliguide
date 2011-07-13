package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

class StepThumbView extends JPanel {
   
   private Step _step;   
   private void updateStep(){      
      
      removeAll();
      
      for (Sprite sprite : _step.getSprites()){       
         SpriteView view = ViewFactory.createView(sprite);
         add(view); 
      }      
      
      ContextImageView view = ViewFactory.createView(_step.getContextImage());
      add(view);
   }
   
   
   @Override
   public void paintChildren(Graphics g0){
      Graphics2D g = (Graphics2D) g0;
      if (_step == null)
         return;
        
        Dimension size = getSize();
        Dimension contentSize = new Dimension(_step.getContextImage().getWidth(), _step.getContextImage().getHeight());
       
        float scalex = 1f* size.width / contentSize.width;
        float scaley = 1f* size.height / contentSize.height;
        float minscale = Math.min(scalex,scaley);

        int height = (int) (contentSize.height * minscale);
        int width = (int) (contentSize.width * minscale);

        int x = size.width/2 - width/2;
        int y = size.height/2 - height/2; 
        
        g.translate(x,y);
        g.scale(minscale,minscale);
        
        super.paintChildren(g);
   }
   
   public void setStep(Step step){
      _step = step;
      updateStep();
   }
   
   StepThumbView(){
      setLayout(null);      
      // It turns out these are useful after all
      // so that it works on Windows
      setBackground(null);  
   }
   
   StepThumbView(Step step){
      setLayout(null);      
      // It turns out these are useful after all
      // so that it works on Windows
      setBackground(null);
      
      _step = step;
      updateStep();
   }
}

public class StepView extends JComponent {

   protected Step _step;   
   private void updateStep(){      
      
      removeAll();
      
      for (Sprite sprite : _step.getSprites()){       
         SpriteView view = ViewFactory.createView(sprite);
         add(view); 
      }      
      
      ContextImageView view = ViewFactory.createView(_step.getContextImage());
      add(view);
   }
   
   
   
   public void setStep(Step step){
      _step = step;
      updateStep();
   }
   
   StepView(){
      setLayout(null);      
      // It turns out these are useful after all
      // so that it works on Windows
      setBackground(null);  
   }
   
   StepView(Step step){
      setLayout(null);      
      // It turns out these are useful after all
      // so that it works on Windows
      setBackground(null);
      
      _step = step;
      updateStep();
      //((JPanel)getContentPane()).setBackground(null);      
      //Env.getOSUtil().setWindowOpaque(this, false);
      
      //setBounds(100,100,500,500);
//
//   _screenRegionSelector = new ScreenRegionSelector(getBounds());
//   _screenRegionSelector.addListener(new ScreenRegionSelectorListener(){
//
//      @Override
//      public void selectionChanged(Rectangle region) {
//         setBounds(region);       
//         _regionModel.setBounds(region);
//      }
//      
//   });
//   
//      _regionModel.setBounds(getBounds());
   }
}
