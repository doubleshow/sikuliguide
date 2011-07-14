package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.junit.Test;
import org.sikuli.cv.FindResult;
import org.sikuli.guide.SideRelationship.Side;
import org.sikuli.guide.StepView;


import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FixtureFactory {
   
   static Text createText(){
      Text t = new DefaultText("Text");            
      t.setX(100);
      t.setY(100);
      t.setName("Text");
      return t;
   }  
   
   static Circle createCircle(){      
      Circle c = new Circle(20,20,100,100);
      c.setForeground(Color.red);
      c.setName("Circle");
      return c;
   }
   
   static FlagText createFlagText(){      
      FlagText f = new FlagText("Flag");
      f.setLocation(200,100);
      f.setForeground(Color.black);
      f.setName("Flag");
      return f;
   }
   
   static ContextImage createContextImage(){
      try {
         return new ContextImage(new File(ROOT, "screen.png"));
      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;
   }
   
   final static String ROOT = "src/test/resources";
   
   static Step createStep(){
      Step step = new Step();            
      
      final Text txt = FixtureFactory.createText();
      final Circle circle = FixtureFactory.createCircle();
      final FlagText flag = FixtureFactory.createFlagText();
      final Target target = FixtureFactory.createTarget();
      
      step.addSprite(txt);      
      step.addSprite(circle);
      step.addSprite(flag);
      step.addSprite(target);
      
      
      try {
         ContextImage contextImage = new ContextImage(new File(ROOT, "screen.png"));
         step.setContextImage(contextImage);
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      return step;
   }

   public static Target createTarget() {
      Target target = mock(Target.class);
      when(target.getX()).thenReturn(265);
      when(target.getY()).thenReturn(190);
      when(target.getWidth()).thenReturn(50);
      when(target.getHeight()).thenReturn(30);
      return target;
   }

}

public class ViewTestCase {
   
   private static File ROOT = new File("src/test/resources");

   
   @Test
   public void testSoundIcon() throws InterruptedException, IOException, AWTException{
      
      Step step = new Step();      
      
      
      Circle circle = new Circle(0,0,0,0);
      circle.setForeground(Color.red);
      step.addSprite(circle);
      
      
      
      BufferedImage image = ImageIO.read(new File(ROOT, "soundIcon.png"));
         
      Target target = new DefaultTarget(image);
      Relationship r = new SideRelationship(target, circle, Side.SURROUND);
      
      Tracker tracker = new Tracker(target);
      tracker.setScreenGrabber(new DesktopScreenGrabber());
      tracker.start();
      
      StepOverlayView view = new StepOverlayView(step);
      view.setVisible(true);
   
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }

   }
   
   @Test
   public void testStepOverlayView() throws InterruptedException{
      
      Step step = FixtureFactory.createStep();
      
      StepOverlayView view = new StepOverlayView(step);
      view.setVisible(true);
   
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }

   }
   
   
   @Test
   public void testTracker() throws IOException, InterruptedException, AWTException {
      
      BufferedImage targetImage = ImageIO.read(new File("src/test/resources/tracker", "mouse.png")); 
      
      Target target = mock(Target.class);
      when(target.getBufferedImage()).thenReturn(targetImage);

      Tracker tracker = new Tracker(target);
      
      tracker.setScreenGrabber(new ScreenGrabber(){

         Robot robot = new Robot();
         
         @Override
         public BufferedImage grab() {
            Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            return robot.createScreenCapture(area);
         }
         
      });
      
      tracker.addTrackerListener(new TrackerListener(){

         @Override
         public void targetFoundFirstTime(Target target, FindResult match) {
            
            System.out.println("m : " + match);            
         }

         @Override
         public void targetFoundAgain(Target target, FindResult match) {
         }

         @Override
         public void targetNotFound(Target target) {
         }
         
      });
      
      
      tracker.start();
      
      
      
      
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }

   }

   @Test
   public void testStepEditView() throws InterruptedException {
      
      Step step = FixtureFactory.createStep();
      
      StepEditView view = new StepEditView(step);
      
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
