package org.sikuli.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SlideDeckEditorTest {

   
   private SlideDeckEditor editor;
   private JFrame frame;
   
   private Slide slide0 = mock(Slide.class);
   private Slide slide1 = mock(Slide.class);
   private Slide newSlide = mock(Slide.class);
   private SlideDeck slideDeck = mock(SlideDeck.class);
   private List<Slide> slides = new ArrayList<Slide>();
   
   private FrameFixture window;
   private JListFixture fListView;
   private JPanelFixture fEditView;
   
   @Before
   public void setUp(){
      editor = new SlideDeckEditor();
      editor.setPreferredSize(new Dimension(640,480));
      
      when(slide0.toString()).thenReturn("Slide 0");
      when(slide1.toString()).thenReturn("Slide 1");
      when(newSlide.toString()).thenReturn("New Slide");

      slides.add(slide0);
      slides.add(slide1);
      when(slideDeck.getSlides()).thenReturn(slides);
      
      editor.setSlideDeck(slideDeck);
      
      JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
         protected JFrame executeInEDT() {
           return new JFrame();  
         }
     });
      frame.setContentPane(editor);
      frame.pack();
     
     window = new FrameFixture(frame);
     window.show(); // shows the frame to test

//     SlideDeckListView view = new SlideDeckListView();
//     editor.setSlideDeckListView(view);

      
     fListView = new JListFixture(window.robot, editor.listView);
     fEditView = new JPanelFixture(window.robot, editor.editView);
     

   }
   
   @After
   public void tearDown(){
      window.cleanUp();
   }
   
//   @Test
//   public void testManually() throws InterruptedException{         
//      Object lock = new Object();
//      synchronized(lock){
//         lock.wait();
//      }
//   }
   


   public static class FixtureBase {
      protected Slide slide0 = mock(Slide.class);
      protected Slide slide1 = mock(Slide.class);
      protected Slide newSlide = mock(Slide.class);
      protected SlideDeck slideDeck = mock(SlideDeck.class);
      protected List<Slide> slides = new ArrayList<Slide>();
      protected ActionEvent event = mock(ActionEvent.class);
      
      protected SlideDeckEditor editor;
      protected FrameFixture window;
      protected JListFixture fListView;
      protected JPanelFixture fEditView;
      
      @Before
      public void setUp(){
         editor = new SlideDeckEditor();
         editor.setPreferredSize(new Dimension(640,480));
         
         when(slide0.toString()).thenReturn("Slide 0");
         when(slide1.toString()).thenReturn("Slide 1");
         when(newSlide.toString()).thenReturn("New Slide");

         slides.add(slide0);
         slides.add(slide1);
         when(slideDeck.getSlides()).thenReturn(slides);
         
         editor.setSlideDeck(slideDeck);
         
         JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            protected JFrame executeInEDT() {
              return new JFrame();  
            }
        });
         frame.setContentPane(editor);
         frame.pack();
        
        window = new FrameFixture(frame);
        window.show(); // shows the frame to test

        fListView = new JListFixture(window.robot, editor.listView);
        fEditView = new JPanelFixture(window.robot, editor.editView);
      }
      
      @After
      public void tearDown(){
         window.cleanUp();
      }
   }
   
   public static class UserSelectionTest extends FixtureBase {
      
      @Before
      public void setUp(){
         super.setUp();
      }
      
      @Test
      public void testSetToAnExistingSlideDeck(){
         
         assertThat(editor.getSlideDeck(), notNullValue());
         
         fListView.requireItemCount(slides.size());
         fListView.requireSelection(0);
      }
      
      @Test
      public void testClickToSelectSlide(){
         fListView.clickItem(0);      
         assertThat(editor.editView.getSlide(), sameInstance(slide0));
         
         fListView.clickItem(1);      
         assertThat(editor.editView.getSlide(), sameInstance(slide1));      
      }
      
      
      @Test
      public void testRefreshPreserveSelection(){
         fListView.selectItem(1);
         editor.refresh();
         fListView.requireSelection(1);      
      }
      
   }
   
   public static class UserEditInteractionText extends FixtureBase {
      
      @Test
      public void testClickToSelectAndDeleteSlide(){
         int n = slideDeck.getSlides().size();
         
         fListView.clickItem(0);
         fListView.pressAndReleaseKeys(KeyEvent.VK_DELETE);

         fListView.requireItemCount(n-1);
         assertThat(editor.getSlideDeck().getSlides().size(), equalTo(n-1));
      }
      
      @Test
      public void testClickToSelectAndDeleteSlideUntilEmpty(){
         fListView.clickItem(0);
         fListView.pressAndReleaseKeys(KeyEvent.VK_DELETE);
         fListView.clickItem(0);
         fListView.pressAndReleaseKeys(KeyEvent.VK_DELETE);

         fListView.requireItemCount(0);
         
         assertThat(editor.editView.getSlide(), nullValue());
      }
      
      
   }
   
   public static class EditActionsTest extends FixtureBase {
      
      @Before
      public void setUp(){
         super.setUp();
         slideDeck = new DefaultSlideDeck();
         slideDeck.add(slide0);
         slideDeck.add(slide1);
         editor.setSlideDeck(slideDeck);
      }
      
   
      @Test
      public void testDeleteSelectedSlideAction() {

         int n = slideDeck.size();

         editor.getEditActionFactory().deleteSelectedSlideAction().actionPerformed(event);
         fListView.requireItemCount(n-1);

         editor.undoAction().actionPerformed(event);
         fListView.requireItemCount(n);
      }
      
      @Test
      public void testAddNewSlideAction(){

         int n = slides.size();
         
         editor.getEditActionFactory().addNewSlideToEndAction(newSlide).actionPerformed(event);
         
         assertThat(slideDeck.size(), equalTo(n+1));
         
         fListView.requireItemCount(n+1);
         fListView.requireSelection(n);
      }
      
      

   }
   
   
   class TestCellRenderer extends JLabel implements ListCellRenderer{
      
      JLabel label = new JLabel();
      TestCellRenderer(){         
         setLayout(new GridBagLayout());
         setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         
         //_thumbView.setPreferredSize(new Dimension(140,140));        
         
         GridBagConstraints c = new GridBagConstraints();
         c.anchor = GridBagConstraints.CENTER;
         add(label,c);
         validate();
      }
      @Override
      public Component getListCellRendererComponent(
            JList list,              // the list
            Object value,            // value to display
            int index,               // cell index
            boolean isSelected,      // is the cell selected
            boolean cellHasFocus)    // does the cell have focus
          {
             
             Slide slide = (Slide) value;

             label.setText(""+slide);
             
             setPreferredSize(new Dimension(80,80));
             
             if (isSelected){
                setBorder(BorderFactory.createLineBorder(Color.red, 5));
             }else{
                setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
             }
             
              return this;
          }   
   }
   
   @Test
   public void testSetCustomSlideDeckListView() throws InterruptedException{
      
      SlideDeckListView view = new SlideDeckListView();
      view.setCellRenderer(new TestCellRenderer());

      final SlideEditView ev = new SlideEditView();
      ev.add(new JLabel(){
         
         public String getText(){
            return "edit:" + ev.getSlide();
         }
         
      });
      
      editor.setSlideDeckListView(view);
      editor.setSlideEditView(ev);
      
      (new JListFixture(window.robot,editor.listView)).selectItem(0);      
      assertThat(editor.editView.getSlide(), sameInstance(slide0));

      (new JListFixture(window.robot,editor.listView)).selectItem(1);
      assertThat(editor.editView.getSlide(), sameInstance(slide1));
      
      
   }
   
   
}
