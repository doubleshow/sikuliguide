package org.sikuli.guide;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.junit.Test;

public class TargetTestCase {
   
   
   private static File ROOT = new File("src/test/resources");
   
   @Test
   public void testBeingTracked() throws IOException, AWTException, InterruptedException{
            
      BufferedImage image = ImageIO.read(new File(ROOT, "systemPreferencesWindow.png"));
      
      AbstractContextImage contextImage = mock(AbstractContextImage.class);
      when(contextImage.getBufferedImage()).thenReturn(image);
      
      Target target = new ContextTarget(contextImage);
      target.setX(265);
      target.setY(190);
      target.setWidth(100);
      target.setHeight(100);
      
      Tracker tracker = new Tracker(target);
      ScreenGrabber screenGrabber = new DesktopScreenGrabber();
      tracker.setScreenGrabber(screenGrabber);      
      tracker.start();
      
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }

//      AbstractContextImage contextImage = mock(AbstractContextImage.class);
      
      
//      Step step = new Step();
//      
//      step.setContextImage(contextImage);
//      
//      assertThat(step.getContextImage(), sameInstance(contextImage));
      
   }
}
