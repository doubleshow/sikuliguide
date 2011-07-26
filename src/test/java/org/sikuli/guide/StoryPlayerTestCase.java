package org.sikuli.guide;

import java.awt.AWTException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StoryPlayerTestCase {


   Step step;
   StoryPlayer player; 
   
   @Before
   public void setUp() throws AWTException{      
      step = FixtureFactory.createStepWithRelationships();      
      
      player = new StoryPlayer();
   }
   
   
   @After
   public void tearDown(){
      
   }
   
   @Test
   public void testPlayASingleStep() throws InterruptedException{
         
      player.play(step);
            
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }

      
   }
   
   
   
   
}
