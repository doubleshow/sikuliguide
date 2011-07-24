package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import org.sikuli.ui.Slide;
import org.sikuli.ui.SlideEditView;

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

public class StepView extends SlideEditView {

   protected Step _step;   
   
   JPanel contentLayer = new JPanel();
   
   private void updateStep(){            
      contentLayer.removeAll();
      
      if (_step == null)
         return;
      
      
      //System.out.println("StepView:updateStep: adding " + _step.getSprites().size() + " steps");
      for (Sprite sprite : _step.getSprites()){       
         SpriteView view = ViewFactory.createView(sprite);
        // System.out.println(sprite.getClass() + " : " + sprite);         
         contentLayer.add(view,0); 
      }      
      
      ContextImage contextImage = _step.getContextImage();
      ContextImageView view = ViewFactory.createView(_step.getContextImage());
      view.setLocation(contextImage.getX(),contextImage.getY());
      contentLayer.add(view);
      repaint(); 
   }
   
   
   @Override
   public void refresh() {
      super.refresh();
      updateStep();
   }

   public void setStep(Step step){
      _step = step;
      setSlide(step);
      updateStep();
   }
   
   @Override
   public void setSlide(Slide slide) {
      super.setSlide(slide);
      _step = (Step) slide;
      refresh();
   }



   StepView(){
      setLayout(null);      
      // It turns out these are useful after all
      // so that it works on Windows
      setBackground(null);  
      
      contentLayer.setLayout(null);
      contentLayer.setSize(640,480);//_step.getContextImage().getWidth(), _step.getContextImage().getHeight());
      add(contentLayer);
      
      
      setPreferredSize(new Dimension(640,480));
      
      
      
      setTransferHandler(new SpriteTransferHandler());
      addKeyListener(new KeyAdapter(){
         
         @Override
         public void keyPressed(KeyEvent k){
            System.out.println("here");
            if (k.getKeyCode() == KeyEvent.VK_C){
               TransferHandler handler = getTransferHandler(); 
               handler.exportToClipboard((JComponent) k.getSource(), Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.COPY);
            }
         }
      });

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


   public Step getStep() {
      return _step;
   }
}
