package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class DesktopScreenGrabber implements ScreenGrabber{

   Robot robot;
   DesktopScreenGrabber() throws AWTException{
      robot = new Robot();
   }
   
   @Override
   public BufferedImage grab() {
      Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
      return robot.createScreenCapture(area);
   }

}

