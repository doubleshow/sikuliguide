package org.sikuli.guide;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.junit.Test;
import org.sikuli.guide.StepView;


import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FixtureFactory {
   
   static Text createText(){
      Text t = new DefaultText("Text");            
      t.setX(100);
      t.setY(100);
      return t;
   }  
   
   static Circle createCircle(){      
      Circle c = new Circle(20,20,100,100);
      c.setForeground(Color.red);
      return c;
   }
   
   static FlagText createFlagText(){      
      FlagText f = new FlagText("Flag");
      f.setLocation(200,100);
      f.setForeground(Color.black);
      return f;
   }
   
   
   final static String ROOT = "src/test/resources";
   
   static Step createStep(){
      Step step = new Step();            
      
      final Text txt = FixtureFactory.createText();
      final Circle circle = FixtureFactory.createCircle();
      final FlagText flag = FixtureFactory.createFlagText();
      
      step.addSprite(txt);      
      step.addSprite(circle);
      step.addSprite(flag);
      
      try {
         ContextImage contextImage = new ContextImage(new File(ROOT, "screen.png"));
         step.setContextImage(contextImage);
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      return step;
   }

}

public class ViewTestCase {
   

   @Test
   public void testStepThumbView() throws InterruptedException {
      
      Step step = FixtureFactory.createStep();
      
      StepView view = new StepView(step);
      
      JFrame f = new JFrame();
      f.setLayout(new BoxLayout(f.getContentPane(),BoxLayout.X_AXIS));
      f.setSize(500,500);
      f.setVisible(true);      
      f.getContentPane().add(view);      
      f.validate();
      
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }
      
   }

   @Test
   public void testCreateStep() throws InterruptedException{
      
      Step step = new Step();            
      
      final Text txt = FixtureFactory.createText();
      final Circle circle = FixtureFactory.createCircle();
      final FlagText flag = FixtureFactory.createFlagText();

      step.addSprite(txt);      
      step.addSprite(circle);
      step.addSprite(flag);
      
      StepView view = new StepView(step);
          
      JFrame f = new JFrame();
      f.setLayout(new BoxLayout(f.getContentPane(),BoxLayout.X_AXIS));
      f.setSize(500,500);
      f.setVisible(true);      
      f.getContentPane().add(view);      
      f.validate();

      
      Timer t = new Timer(50, new ActionListener(){
         
         boolean toggle = true;
         int counter = 0;

         @Override
         public void actionPerformed(ActionEvent e) {
            circle.setX(circle.getX() + 5);
            circle.setY(circle.getY() + 5);
            circle.setWidth(circle.getWidth() + 5);
            circle.setHeight(circle.getHeight() - 5);
            
            txt.setX(txt.getX() + 10);
            txt.setText("counter " + (counter++));
            
            if (toggle)
               txt.setForeground(Color.green);
            else
               txt.setForeground(Color.red);
            
            toggle = !toggle;
         }
         
      });
      t.start();
      
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }
   }
   

   
   
   @Test
   public void testView() throws InterruptedException{
      
      JFrame f = new JFrame();
      f.setLayout(new BoxLayout(f.getContentPane(),BoxLayout.X_AXIS));
      f.setSize(500,500);
      f.setVisible(true);
      
      
      final Sprite s = new DefaultSprite(10,10,100,200);
      SpriteView sv = new SpriteView(s);
      
      StepView v = new StepView(null);
      v.add(sv);
      
      final Text txt = new DefaultText("Some text");
      txt.setX(100);
      txt.setY(200);
      
      //step.addSprite(txt);
            
      TextView tv = new TextView(txt);
      v.add(tv);
      
      final PointingText ptxt = new DefaultPointingText("Pointing", PointingText.DIRECTION_EAST);
      ptxt.setX(200);
      ptxt.setY(220);
      
      FlagTextView fv = new FlagTextView(ptxt);      
      v.add(fv);
      
      final Circle circle = new Circle(10,10,100,100);
      circle.setForeground(Color.green);
      CircleView cv = new CircleView(circle);
      
      v.add(cv);
      
      f.getContentPane().add(v);      
      f.validate();
      
      Timer t = new Timer(50, new ActionListener(){
         
         boolean toggle = true;
         int counter = 0;

         @Override
         public void actionPerformed(ActionEvent e) {
            s.setX(s.getX() + 5);
            s.setY(s.getY() + 5);
            s.setWidth(s.getWidth() + 5);
            s.setHeight(s.getHeight() - 5);
            
            txt.setX(txt.getX() + 10);
            txt.setText("counter " + (counter++));
            
            if (toggle)
               s.setForeground(Color.green);
            else
               s.setForeground(Color.red);
            
            toggle = !toggle;
         }
         
      });
      t.start();
      
      Timer t2 = new Timer(200, new ActionListener(){
         @Override
         public void actionPerformed(ActionEvent e) {
            ptxt.setY(ptxt.getY() + 2);
            ptxt.setDirection((ptxt.getDirection() + 1) % 5);
            
            circle.setWidth(circle.getWidth()+2);
            circle.setForeground(circle.getForeground().darker());
         }
         
      });
      t2.start();
      
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }
      
      
   }

}
