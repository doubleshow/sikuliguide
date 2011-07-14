package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

import com.sun.awt.AWTUtilities;


public class StepOverlayView extends JWindow {
   
   Step _step;   
   StepOverlayView(Step step){
      
      setStep(step);
      
      setLayout(null);      
      // It turns out these are useful after all
      // so that it works on Windows
      setBackground(null);
      ((JPanel)getContentPane()).setBackground(null);
      AWTUtilities.setWindowOpaque(this, false);

      
      setBounds(0,0,1200,800);
      setAlwaysOnTop(true);
   }
   
   void setStep(Step step){
      _step = step;
      for (Sprite sprite : _step.getSprites()){       
         SpriteView view = ViewFactory.createView(sprite);
         add(view); 
      }      
   }

}
