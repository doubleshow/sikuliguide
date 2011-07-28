package org.sikuli.ui;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SlideEditView extends JPanel implements ChangeListener {

   protected SlideEditView(){      
   }

   private Slide slide;
   public Slide getSlide() {
      return slide;
   }

   public void setSlide(final Slide slide){
      this.slide = slide;
      if (slide != null){
         slide.addChangeListener(this);
      }
      refresh();
      repaint();
   }
      
   public void refresh(){
   }

   public void stateChanged(ChangeEvent e) {
      refresh();
      repaint();
      validate();
   }

}