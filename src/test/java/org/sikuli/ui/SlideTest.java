package org.sikuli.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class SlideTest  {


   private Slide slide = new DefaultSlide();

   private ChangeListener mockedChangeListener = mock(ChangeListener.class);
   private UndoableEditListener mockedUndoableEditListener = mock(UndoableEditListener.class);
   
   @Before
   public void setUp(){
      slide.addChangeListener(mockedChangeListener);
      slide.addUndoableEditListener(mockedUndoableEditListener);
   }
   
   @Test
   public void testSetName(){     
      slide.setName("Some name");      
      verify(mockedChangeListener).stateChanged((ChangeEvent)any());
      verify(mockedUndoableEditListener).undoableEditHappened((UndoableEditEvent)any());
   }
   
   @Test
   public void testSetNameUndo(){
      
      String oldName = slide.getName();
      
      slide.setName("Some name");
      
      ArgumentCaptor<UndoableEditEvent> argument = ArgumentCaptor.forClass(UndoableEditEvent.class);
      
      verify(mockedUndoableEditListener).undoableEditHappened(argument.capture());
      
      argument.getValue().getEdit().undo();
      
      assertThat(slide.getName(), equalTo(oldName));            
   }
   
   
}
