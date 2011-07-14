package org.sikuli.guide;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

public class TrackerWithDesktopScreenGrabberTestCase {
   
   private static File ROOT = new File("src/test/resources/tracker");
   
   private Tracker tracker;   
   private TrackerListener listener = mock(TrackerListener.class);
   private Target target = mock(Target.class);
   private MockedSystemPreferencesWindow win = new MockedSystemPreferencesWindow();
   private Object lock = new Object();

   
   @Before
   public void setUp() throws IOException, AWTException{
      
      BufferedImage targetImage = ImageIO.read(new File(ROOT,"softwareupdate.png"));
      when(target.getBufferedImage()).thenReturn(targetImage);
      
      ScreenGrabber screenGrabber = new DesktopScreenGrabber();
      
      tracker = new Tracker(target);
      tracker.setScreenGrabber(screenGrabber);
      tracker.addTrackerListener(listener);
      
      tracker.start();
      win.setLocation(20,20);
   }
   
   @After
   public void tearDown() throws InterruptedException{
      win.dispose();
      synchronized(lock){         
         lock.wait(1000);   // wait until window is all disappeared
      }      
   }

   
   class MockedSystemPreferencesWindow extends JWindow{
      MockedSystemPreferencesWindow(){
         BufferedImage image;
         try {
            image = ImageIO.read(new File(ROOT, "systemPreferencesWindow.png"));
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel(icon);
            add(label);
            setSize(image.getWidth(),image.getHeight());
            validate();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   @Test
   public void testMockedWindowAppearsThanMoves() throws InterruptedException{      

      Thread th = new Thread(){
         public void run(){
            try {
               win.setVisible(true);
               Thread.sleep(2000);
               
               Point p = win.getLocation();
               win.setLocation(p.x + 50, p.y + 40);
               Thread.sleep(2000);

               synchronized(lock){
                  lock.notify();
               }
               
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
         
      };
      th.start();

      synchronized(lock){         
         lock.wait();   // wait until simulated actions are completed
      }      
      
      ArgumentCaptor<Integer> argumentx = ArgumentCaptor.forClass(Integer.class);
      ArgumentCaptor<Integer> argumenty = ArgumentCaptor.forClass(Integer.class);

      verify(target, times(2)).setX(argumentx.capture());
      verify(target, times(2)).setY(argumenty.capture());
      
      List<Integer> xs = argumentx.getAllValues();
      List<Integer> ys = argumenty.getAllValues();
      
      assertThat(xs.get(1) - xs.get(0), equalTo(50));
      assertThat(ys.get(1) - ys.get(0), equalTo(40));
   }

   @Test
   public void testMockedWindowAppearsThanMovesThreeTimes() throws InterruptedException{      

      Thread th = new Thread(){
         public void run(){
            try {
               win.setVisible(true);
               Thread.sleep(2000);
               
               Point p = win.getLocation();
               win.setLocation(p.x + 50, p.y + 40);
               Thread.sleep(2000);

               p = win.getLocation();
               win.setLocation(p.x + 50, p.y + 40);
               Thread.sleep(2000);

               p = win.getLocation();
               win.setLocation(p.x + 50, p.y + 40);
               Thread.sleep(2000);

               synchronized(lock){
                  lock.notify();
               }
               
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
         
      };
      th.start();

      synchronized(lock){         
         lock.wait();   // wait until simulated actions are completed
      }      

      verify(target, times(4)).setX(anyInt());
      verify(target, times(4)).setY(anyInt());
      verify(target, never()).setFound(false);
   }
   
   @Test
   public void testMockedWindowNeverAppears() throws InterruptedException{      
      win.setVisible(false);
      synchronized(lock){         
         lock.wait(2000);   // wait until simulated actions are completed
      }      
      verify(target, never()).setFound(true);
   }
   
   @Test
   public void testMockedWindowAppearsThanVanishes() throws InterruptedException{      
      Thread th = new Thread(){
         public void run(){
            try {
               win.setVisible(true);
               Thread.sleep(2000);
               
               win.setVisible(false);
               Thread.sleep(3000);

               synchronized(lock){
                  lock.notify();
               }
               
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
         
      };
      th.start();    
      
      synchronized(lock){         
         lock.wait();   // wait until simulated actions are completed
      }      

      
      verify(target, times(1)).setFound(true);
      verify(target, times(1)).setFound(false);
   }
   
   
   @Test
   public void testMockedWindowAppearsThanVanishesThanAppearsAgain() throws InterruptedException{      
      Thread th = new Thread(){
         public void run(){
            try {
               win.setVisible(true);
               Thread.sleep(2000);
               
               win.setVisible(false);
               Thread.sleep(3000);

               win.setVisible(true);
               Thread.sleep(2000);
               
               synchronized(lock){
                  lock.notify();
               }
               
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
         
      };
      th.start();    
      
      synchronized(lock){         
         lock.wait();   // wait until simulated actions are completed
      }      
      
      verify(target, times(2)).setFound(true);
      verify(target, times(1)).setFound(false);
   }
}


