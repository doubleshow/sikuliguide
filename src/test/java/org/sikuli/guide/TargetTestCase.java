package org.sikuli.guide;

import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;

public class TargetTestCase {


   private static File ROOT = new File("src/test/resources");

//   @Test
//   public void testBeingTracked() throws IOException, AWTException, InterruptedException{
//
//      Tracker tracker = new Tracker(target);
//      ScreenGrabber screenGrabber = new DesktopScreenGrabber();
//      tracker.setScreenGrabber(screenGrabber);      
//      tracker.start();
//
//      //      tracker
//
//      Object lock = new Object();
//      synchronized(lock){
//         lock.wait();
//      }
//
//   }

//   private Circle circle = new Circle(0,0,0,0);//(Circle.class);
//   private Target target;
//   private AbstractContextImage contextImage = mock(AbstractContextImage.class);
//
//   @Before
//   public void setUp() throws IOException{
//
//      BufferedImage image = ImageIO.read(new File(ROOT, "systemPreferencesWindow.png"));
//
//      when(contextImage.getBufferedImage()).thenReturn(image);
//
//      target = new ContextTarget(contextImage);
//      target.setX(265);
//      target.setY(190);
//      target.setWidth(100);
//      target.setHeight(50);
//
//   }



//
//   Timer t = null;
//   @Test
//   public void testTrackedTargetFollowedByACircle() throws AWTException, InterruptedException{      
////      Relationship r = new SideRelationship(target, circle, Side.SURROUND);
////      circle.setOpacity(0f);
//
////      List<Sprite> s = new ArrayList<Sprite>();
////      s.add(circle);
////      Step step = mock(Step.class);      
////      when(step.getSprites()).thenReturn(s);
////      StepOverlayView view = new StepOverlayView(step);
////      view.setVisible(true);
//
//
//      Tracker tracker = new Tracker(target);
//      ScreenGrabber screenGrabber = new DesktopScreenGrabber();
//      tracker.setScreenGrabber(screenGrabber);      
//      tracker.start();
//
//      final Point originalLocation = new Point();
//      tracker.addTrackerListener(new TrackerListener(){
//         @Override
//         public void targetFoundFirstTime(Target target, FindResult match) {
//            originalLocation.x = match.x;
//            originalLocation.y = match.y;            
//         }
//         @Override
//         public void targetFoundAgain(Target target, FindResult match) {
//         }
//         @Override
//         public void targetNotFound(Target target) {
//         }         
//      });
//
//      final MockedSystemPreferencesWindow win = new MockedSystemPreferencesWindow();
//      win.setLocation(20,20);
//      win.setVisible(true);
//
//      final Object lock = new Object();
//
//      
//      Thread th = new Thread(){
//         public void run(){
//            try {
//               Thread.sleep(2000);
//               
//               Point p = win.getLocation();
//               win.setLocation(p.x + 50, p.y + 40);
//               Thread.sleep(2000);
//
//               p = win.getLocation();
//               win.setLocation(p.x + 50, p.y + 40);
//               Thread.sleep(2000);
//
//               p = win.getLocation();
//               win.setLocation(p.x + 50, p.y + 40);
//               Thread.sleep(2000);
//
//               synchronized(lock){
//                  lock.notify();
//               }
//               
//            } catch (InterruptedException e) {
//               e.printStackTrace();
//            }
//         }
//         
//      };
//      th.start();
//
//      synchronized(lock){         
//         lock.wait();   // wait until the window has moved 3 times
//      }      
//
//      assertThat(target.getX(), equalTo(originalLocation.x + 50*3));
//      assertThat(target.getY(), equalTo(originalLocation.y + 40*3));
//
//
//   }
}

