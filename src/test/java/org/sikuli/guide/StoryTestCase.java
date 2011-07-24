package org.sikuli.guide;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import org.uispec4j.Panel;
import org.sikuli.ui.Slide;

class ScrollPaneFrame extends JFrame {
   
   ScrollPaneFrame(JComponent comp){
      
      JScrollPane scrollPane = new JScrollPane(comp);
      add(scrollPane);         
   }
         
}


public class StoryTestCase {
   
   private FrameFixture window;
   
   @After
   public void tearDown() {
      window.cleanUp();
   }
   
   @Before 
   public void setUp() {
      
      Step step = FixtureFactory.createStep();      
      final StepView view = new StepView(step);
      
//      ScrollPaneFrame f = new ScrollPaneFrame(comp);
//      f.setSize(width,height);
//      f.setLocation(100,100);
//      f.setVisible(true);
//      f.validate();

      ScrollPaneFrame f = GuiActionRunner.execute(new GuiQuery<ScrollPaneFrame>() {
          protected ScrollPaneFrame executeInEDT() {
            return new ScrollPaneFrame(view);  
          }
      });
      
      window = new FrameFixture(f);
      window.show(); // shows the frame to test
    }
   
   @Test
   public void testStepEditView() {
    
      window.resizeTo(new Dimension(800,600));
      window.panel("Text").click();
      window.panel("Flag").click();
      //window.
      //window.button("copyButton").click();
      
      
      
      //Panel panel = new Panel(view);
      //panel.getPanel("Text");
      
      //panel.getSwingComponents(TextView.class, "Text")[0].;
      
      //panel.getPanel("Text").;
      
      //panel.getButton("text").c
      
//      panel.getTable().contentEquals(...);
//      ...
//      panel.getButton("Apply").click();
//      
//      ViewTester.viewInScrollPane(view,600,480);
      
   }
   
   @Test
   public void testStoryListView() throws InterruptedException {

      Story story = new Story();
      story.addStep(FixtureFactory.createStep());
      story.addStep(FixtureFactory.createStep());
      story.addStep(FixtureFactory.createStep());
      
      
      //StoryListView view = new StoryListView(story);
      
      
      //ViewTester.viewInScrollPane(view);


   }
   
   
   @Test
   public void testAddStep() {
      
      Step mockedStep1 = mock(Step.class);
      
      Story story = new Story();
      
      story.addStep(mockedStep1);      

      assertThat(story.getSteps(), hasItem(mockedStep1));
      assertThat(story.getSteps().size(), equalTo(1));

      Step mockedStep2 = mock(Step.class);
      story.addStep(mockedStep2);      

      assertThat(story.getSteps(), hasItems(mockedStep1, mockedStep2));
      assertThat(story.getSteps().get(0), sameInstance(mockedStep1));
      assertThat(story.getSteps().get(1), sameInstance(mockedStep2));
      assertThat(story.getSteps().size(), equalTo(2));
      
   }
   
   @Test
   public void testRemoveStep() {

      Step mockedStep2 = mock(Step.class);
      Step mockedStep1 = mock(Step.class);
      
      Story story = new Story();
      
      story.addStep(mockedStep1);  
      story.addStep(mockedStep2);  

      story.removeStep(mockedStep2);
      
      assertThat(story.getSteps().size(), equalTo(1));
      assertThat(story.getSteps(), not(hasItem(mockedStep2)));
      assertThat(story.getSteps(), hasItem(mockedStep1));
   }
   
   
   @Test
   public void testAddStep_ListenerShouldBeNotified(){
      
      Step mockedStep = mock(Step.class);
      
      Story story = new Story();

      ListDataListener mockedListDataListener = mock(ListDataListener.class);
      story.addListDataListener(mockedListDataListener);

      story.addStep(mockedStep);      
      
      ListDataEvent e1 = new ListDataEvent(story, ListDataEvent.INTERVAL_ADDED, 0, 0);      
      verify(mockedListDataListener).intervalAdded(refEq(e1));
    
      story.addStep(mockedStep);
      
      ListDataEvent e2 = new ListDataEvent(story, ListDataEvent.INTERVAL_ADDED, 1, 1);
      verify(mockedListDataListener).intervalAdded(refEq(e2));

      verify(mockedListDataListener, times(2)).intervalAdded((ListDataEvent) any());

   }
   
   @Test
   public void testRemoveStep_ListenerShouldBeNotified(){
      
      Step mockedStep1 = mock(Step.class);
      Step mockedStep2 = mock(Step.class);
      
      Story story = new Story();


      story.addStep(mockedStep1);
      story.addStep(mockedStep2);
      
      ListDataListener mockedListDataListener = mock(ListDataListener.class);
      story.addListDataListener(mockedListDataListener);
      
      story.removeStep(mockedStep2);
      
      ListDataEvent e1 = new ListDataEvent(story, ListDataEvent.INTERVAL_REMOVED, 1, 1);      
      verify(mockedListDataListener).intervalRemoved(refEq(e1));
    
      story.removeStep(mockedStep1);
      
      ListDataEvent e2 = new ListDataEvent(story, ListDataEvent.INTERVAL_REMOVED, 0, 0);
      verify(mockedListDataListener).intervalRemoved(refEq(e2));

      verify(mockedListDataListener, times(2)).intervalRemoved((ListDataEvent) any());

   }
   
}

class ViewTester {
   
   static class ScrollPaneFrame extends JFrame {
      
      ScrollPaneFrame(JComponent comp){
         
         JScrollPane scrollPane = new JScrollPane(comp);
         add(scrollPane);         
      }
            
   }
   
   static void viewInScrollPane(JComponent comp){
      ScrollPaneFrame f = new ScrollPaneFrame(comp);
      f.setSize(200,500);
      f.setLocation(100,100);
      f.setVisible(true);
      f.validate();
      
      Object lock = new Object();
      synchronized(lock){
         try {
            lock.wait();
         } catch (InterruptedException e) {
         }
      }
   }
   
   static void viewInScrollPane(JComponent comp, int width, int height){
      ScrollPaneFrame f = new ScrollPaneFrame(comp);
      f.setSize(width,height);
      f.setLocation(100,100);
      f.setVisible(true);
      f.validate();
      
      Object lock = new Object();
      synchronized(lock){
         try {
            lock.wait();
         } catch (InterruptedException e) {
         }
      }
   }
   
   
}


