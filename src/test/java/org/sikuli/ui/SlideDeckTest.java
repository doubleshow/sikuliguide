package org.sikuli.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SlideDeckTest  {


   private SlideDeck slideDeck = new DefaultSlideDeck();
   private UndoableEditListener listener = mock(UndoableEditListener.class);
   private ListDataListener listDataListener = mock(ListDataListener.class);
   
   private JList list = new JList();
   private FrameFixture window;
   
   @Before
   public void setUp(){
      slideDeck.addUndoableEditListener(listener);
      slideDeck.addListDataListener(listDataListener);
      
      list.setModel(slideDeck);      
      
      
      
      JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
         protected JFrame executeInEDT() {
            return new JFrame();  
         }
      });
      frame.setContentPane(list);
      frame.pack();

      window = new FrameFixture(frame);
      window.show(); // shows the frame to test
   }
   
   @After
   public void tearDown(){
      window.cleanUp();
   }
   
   @Test
   public void testAdd() throws InterruptedException{
      int n = slideDeck.getSize();
      slideDeck.insertElementAt(mock(Slide.class),n);      
      assertThat(slideDeck.getSize(), equalTo(n+1));       
      verify(listener).undoableEditHappened((UndoableEditEvent) any());    
            
      ListDataEvent e1 = new ListDataEvent(slideDeck, ListDataEvent.INTERVAL_ADDED, n, n);      
      verify(listDataListener).intervalAdded(refEq(e1));
      
   }
   
   @Test
   public void testAddAndRemove() throws InterruptedException{
      
      Slide slide0 = mock(Slide.class);
      Slide slide1 = mock(Slide.class);
      
      slideDeck.insertElementAt(slide0, 0);
      slideDeck.insertElementAt(slide1, 1);

      int n = slideDeck.getSize();
      slideDeck.removeElement(slide1);
      
      assertThat(slideDeck.getSize(), equalTo(n-1));       
      verify(listener, atLeast(2)).undoableEditHappened((UndoableEditEvent) any());
      
      ListDataEvent e1 = new ListDataEvent(slideDeck, ListDataEvent.INTERVAL_REMOVED, n-1, n-1);      
      verify(listDataListener).intervalRemoved(refEq(e1));

   }
   
   
}
