package org.sikuli.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SlideDeckTest  {


   private SlideDeck slideDeck = new DefaultSlideDeck();
   private UndoableEditListener listener = mock(UndoableEditListener.class);
   
   @Before
   public void setUp(){
      slideDeck.addUndoableEditListener(listener);
   }
   
   @Test
   public void testAdd(){
      int n = slideDeck.size();
      slideDeck.add(mock(Slide.class));      
      assertThat(slideDeck.size(), equalTo(n+1));       
      verify(listener).undoableEditHappened((UndoableEditEvent) any());      
   }
   
   @Test
   public void testAddAndRemove(){
      slideDeck.add(mock(Slide.class));

      int n = slideDeck.size();
      slideDeck.remove(n-1);
      
      assertThat(slideDeck.size(), equalTo(n-1));       
      verify(listener, times(2)).undoableEditHappened((UndoableEditEvent) any());      
   }
   
   
}
