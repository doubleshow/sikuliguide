package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StoryPlayerTestCase {


   Step step;
   StoryPlayer player; 
   
   @Before
   public void setUp() throws AWTException{      
      step = FixtureFactory.createStepWithRelationships();      
      
      player = new DefaultStoryPlayer();
   }
   
   
   @After
   public void tearDown(){
      
   }
   
   @Test
   public void testPlayASingleStep() throws InterruptedException{
         
      List<Step> steps = new ArrayList<Step>();
      steps.add(step);
      steps.add(step);
      player.play(steps);      
//      Timer timer = new Timer(2000, new ActionListener(){
//
//         @Override
//         public void actionPerformed(ActionEvent arg0) {
//            player.stop();
//         }         
//      });
//      timer.start();
//      
            
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }

      
   }
   
   
   
   
}
