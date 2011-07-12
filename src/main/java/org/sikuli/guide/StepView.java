package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StepView extends JComponent {

   private Step _step;   
   private void updateStep(){      
      for (Sprite sprite : _step.getSprites()){       
         SpriteView view = ViewFactory.createView(sprite);
         add(view); 
      }      
      
      ContextImageView view = ViewFactory.createView(_step.getContextImage());
      add(view);
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
