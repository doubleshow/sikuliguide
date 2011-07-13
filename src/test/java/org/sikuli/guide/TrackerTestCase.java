package org.sikuli.guide;

import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.sikuli.cv.FindResult;

public class TrackerTestCase {
   
   
   private Tracker tracker;
   
   private static File ROOT = new File("src/test/resources/tracker");
   private TrackerListener listener = mock(TrackerListener.class);
   private ScreenGrabber grabber = mock(ScreenGrabber.class);    
   private Target target = mock(Target.class);

   private BufferedImage frame1;
   private BufferedImage frame2;
   private BufferedImage frame3;

   
   @Before
   public void setUp() throws IOException{
      
      frame1 = ImageIO.read(new File(ROOT,"frameSystemPrefsWindow.png"));
      frame2 = ImageIO.read(new File(ROOT,"frameSystemPrefsWindowInAnotherLocation.png"));
      frame3 = ImageIO.read(new File(ROOT,"frameNoSystemPrefsWindow.png"));

      BufferedImage targetImage = ImageIO.read(new File(ROOT,"softwareupdate.png"));
      when(target.getImage()).thenReturn(targetImage);
      
      tracker = new Tracker(target);
      tracker.setScreenGrabber(grabber);
      tracker.addTrackerListener(listener);
   }

   @Test
   public void testFindFirstTime() throws InterruptedException{
      when(grabber.grab()).thenReturn(frame1);
      tracker.start();
      Object lock = new Object();
      synchronized(lock){
         lock.wait(2000);
     }
      verify(listener).targetFoundFirstTime((Target)any(), (FindResult) anyObject());
   }
   
   @Test
   public void testFindFirstTimeAndMoved() throws InterruptedException{
      when(grabber.grab()).thenReturn(frame1).thenReturn(frame2);
      tracker.start();
      Object lock = new Object();
      synchronized(lock){
         lock.wait(2000);
     }
      verify(listener).targetFoundFirstTime((Target)any(), (FindResult) anyObject());
      verify(listener, never()).targetNotFound((Target)any());
      verify(listener).targetFoundAgain((Target)any(), (FindResult) anyObject());
   }
   
   @Test
   public void testNeverFound() throws InterruptedException{
      when(grabber.grab()).thenReturn(frame3);
      tracker.start();
      Object lock = new Object();
      synchronized(lock){
         lock.wait(2000);
     }
      verify(listener, never()).targetFoundFirstTime((Target)any(), (FindResult) anyObject());
      verify(listener, never()).targetNotFound((Target)any());
      verify(listener, never()).targetFoundAgain((Target)any(), (FindResult) anyObject());
   }
   
   @Test
   public void testInitiallyNotFoundButFoundLater() throws InterruptedException{
      when(grabber.grab()).thenReturn(frame3).thenReturn(frame1);
      tracker.start();
      Object lock = new Object();
      synchronized(lock){
         lock.wait(2000);
     }
      verify(listener).targetFoundFirstTime((Target)any(), (FindResult) anyObject());
   }
   
   @Test
   public void testFindFirstTimeAndVanished() throws InterruptedException{
      when(grabber.grab()).thenReturn(frame1).thenReturn(frame3);
      tracker.start();
      Object lock = new Object();
      synchronized(lock){
         lock.wait(3000);
     }
      verify(listener).targetFoundFirstTime((Target)any(), (FindResult) anyObject());
      verify(listener).targetNotFound((Target)any());
      verify(listener,never()).targetFoundAgain((Target)any(), (FindResult) anyObject());
   }
   
   @Test
   public void testFindFirstTime_TargetShouldBeUpdated() throws InterruptedException{
      when(grabber.grab()).thenReturn(frame1);
      
      final FindResult found = new FindResult();
      tracker.addTrackerListener(new TrackerListener(){
   
         @Override
         public void targetFoundFirstTime(Target target, FindResult match) {
            found.x = match.x;
            found.y = match.y;
         }

         @Override
         public void targetFoundAgain(Target target, FindResult match) {
            // TODO Auto-generated method stub
            
         }

         @Override
         public void targetNotFound(Target target) {
            // TODO Auto-generated method stub
            
         }
         
      });
      
      tracker.start();
      Object lock = new Object();
      synchronized(lock){
         lock.wait(3000);
     }
      
      verify(target).setX(found.x);
      verify(target).setFound(true);
      
   }
   

}
