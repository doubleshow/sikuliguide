package org.sikuli.guide;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.junit.Test;

public class StoryTestCase {
   
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
