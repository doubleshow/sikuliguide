package org.sikuli.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

class TestSlideEditView extends SlideEditView {
   
   JTextField editField = new JTextField(20);   
   TestSlideEditView(){
      super();
      add(editField);
      editField.setName("EditField");
      
      JButton btn =new JButton("Update");
      btn.setName("Update");
      btn.addActionListener(new ActionListener(){

         @Override
         public void actionPerformed(ActionEvent arg0) {
            getSlide().setName(editField.getText());
         }
         
      });
      
      add(btn);
   }
      
   @Override
   public void refresh(){
      if (getSlide()==null){
         editField.setText("");
      }else{
         editField.setText(getSlide().getName());
      }
   }
}

public class SlideDeckEditorTest {

   public static class FixtureBase {
      
      protected Slide slide0;
      protected Slide slide1;
      protected Slide newSlide;
      protected SlideDeck slideDeck;
      protected List<Slide> slides;
      protected ActionEvent mockedActionEvent;
      
      protected SlideDeckEditor editor;
      protected FrameFixture window;
      protected JListFixture fListView;
      protected JPanelFixture fEditView;
      
      @Before
      public void setUp(){

         editor = new SlideDeckEditor();
         editor.setPreferredSize(new Dimension(640,480));
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
   

   public static class WithMockedFixtures extends FixtureBase {
      
      @Before
      public void setUp(){
         super.setUp();
         
         slide0 = mock(Slide.class);
         slide1 = mock(Slide.class);
         newSlide = mock(Slide.class);
         slideDeck = mock(SlideDeck.class);
         slides = new ArrayList<Slide>();
         mockedActionEvent = mock(ActionEvent.class);
                  
         when(slide0.toString()).thenReturn("Slide 0");
         when(slide1.toString()).thenReturn("Slide 1");
         when(newSlide.toString()).thenReturn("New Slide");

         slides.add(slide0);
         slides.add(slide1);
         when(slideDeck.getSlides()).thenReturn(slides);
         
         editor.setSlideDeck(slideDeck);         
      }
      
   }
   
   public static class UserSelectionTest extends WithMockedFixtures {
      
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
   
   public static class UserEditInteractionText extends WithConcreteFixtures {

      @Test
      public void testCutAndPasteAndUndo() throws InterruptedException{

         int n = slideDeck.size();
         
         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_X);
         fListView.releaseKey(KeyEvent.VK_META);

         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);                 
         
         editor.undoAction().actionPerformed(mockedActionEvent);         
         fListView.requireItemCount(n-1);
         
         editor.undoAction().actionPerformed(mockedActionEvent);         
         fListView.requireItemCount(n);
      }
      
      @Test
      public void testCopyAndPasteAndUndo() throws InterruptedException{

         int n = slideDeck.size();
         
         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_C);
         fListView.releaseKey(KeyEvent.VK_META);

         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);                 
         
         editor.undoAction().actionPerformed(mockedActionEvent);         
         fListView.requireItemCount(n);         
      }
      
      @Test
      public void testCutAndPasteAndPaste() throws InterruptedException{
         
         int n = slideDeck.size();
         
         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_X);
         fListView.releaseKey(KeyEvent.VK_META);
         fListView.requireItemCount(n-1);

         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);        
         fListView.requireItemCount(n);
         
         assertThat(slideDeck.getSlides().get(0), sameInstance(slide1));
         
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);
         fListView.requireItemCount(n+1);         
      }
      
      @Test
      public void testCopyAndPasteAndPaste() throws InterruptedException{
         
         int n = slideDeck.size();
         
         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_C);
         fListView.releaseKey(KeyEvent.VK_META);
         fListView.requireItemCount(n);

         fListView.clickItem(1);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);        
         fListView.requireItemCount(n+1);
         
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);
         fListView.requireItemCount(n+2);         
      }

      
      @Test
      public void testClickToSelectAndDeleteSlide(){
         int n = slideDeck.getSlides().size();
         
         fListView.clickItem(0);
         fListView.pressAndReleaseKeys(KeyEvent.VK_DELETE);

         fListView.requireItemCount(n-1);
         assertThat(editor.getSlideDeck().size(), equalTo(n-1));
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
      
      @Test
      public void testEditTheContentOfSlide() throws InterruptedException{
         JTextComponentFixture editField = new JTextComponentFixture(window.robot, ((TestSlideEditView) editView).editField);         
         editField.enterText("new");
         window.button("Update").click();                  
      }
   }
   
   public static class ManualTest extends WithConcreteFixtures {
      
      @Test 
      public void testManualy() throws InterruptedException{
         Object lock = new Object();
         synchronized(lock){
            lock.wait();
         }

      }
      
      @Test 
      public void testManualy2() throws InterruptedException{
         
         EditorFrame ef = new EditorFrame();
         ef.setUndoManager(editor.undoManager);
         ef.editor = editor;
         
         slideDeck.remove(0);
         editor.refresh();
         
         assertThat(editor.undoManager, notNullValue());
         
//         editor.undoManager.undo();         
//         editor.refresh();
//         
         ef.getContentPane().add(editor);
         ef.pack();        
         ef.setVisible(true);
         
         
         Object lock = new Object();
         synchronized(lock){
            lock.wait();
         }

      }
      
      @Test
      public void testAddSlideSetNameThenUndoBoth() throws InterruptedException{         
         Slide slide = new DefaultSlide();
         slide.setName("Original name");
         
         int n = slideDeck.size();
         
         slideDeck.add(slide);
         editor.refresh();
         
         slide.setName("New name");
         
         editor.undoAction().actionPerformed(mockedActionEvent);
         editor.undoAction().actionPerformed(mockedActionEvent);
         
         assertThat(slideDeck.size(), equalTo(n));
         assertThat(slide.getName(), equalTo("Original name"));
         
      }     
      
      @Test
      public void testRemoveSlideThenUndo() throws InterruptedException{         
         int n = slideDeck.size();         
         slideDeck.remove(1);         
         editor.undoAction().actionPerformed(mockedActionEvent);         
         assertThat(slideDeck.size(), equalTo(n));
      }     

      
      @Test
      public void testEditAnSlide() throws InterruptedException{         
         Slide slide = new DefaultSlide();
         slide.setName("Oriinal name");
         slideDeck.add(slide);
         editor.refresh();         
         slide.setName("A different name");
      }     
   }   
   
   public static class EditActionsTest extends WithMockedFixtures {

      @Test
      public void testSetCustomSlideDeckListView() throws InterruptedException{
         
         SlideDeckListView view = new SlideDeckListView();
         view.setCellRenderer(new TestCellRenderer());
         
         editor.setSlideDeckListView(view);
         
         (new JListFixture(window.robot,editor.listView)).selectItem(0);      
         assertThat(editor.editView.getSlide(), sameInstance(slide0));

         (new JListFixture(window.robot,editor.listView)).selectItem(1);
         assertThat(editor.editView.getSlide(), sameInstance(slide1));
         
      }
      
   
      @Test
      public void testDeleteSelectedSlideAction() {

         int n = slideDeck.size();

         editor.getEditActionFactory().deleteSelectedSlideAction().actionPerformed(mockedActionEvent);
         fListView.requireItemCount(n-1);

         editor.undoAction().actionPerformed(mockedActionEvent);
         fListView.requireItemCount(n);
      }
      
      @Test
      public void testAddNewSlideAction() throws InterruptedException{

         int n = slides.size();
         
         editor.getEditActionFactory().addNewSlideToEndAction(newSlide).actionPerformed(mockedActionEvent);
         
         assertThat(slideDeck.size(), equalTo(n+1));
         
         fListView.requireItemCount(n+1);
         fListView.requireSelection(n);
      }
      


   }
   
   

   
   public static class WithConcreteFixtures extends WithMockedFixtures { 
      
      SlideEditView editView = new TestSlideEditView();

   
      @Before
      public void setUp(){
         super.setUp();
         
         slide0 = new DefaultSlide();
         slide0.setName("Slide 0");
         slide1 = new DefaultSlide();
         slide1.setName("Slide 1");
         
         slideDeck = new DefaultSlideDeck();
         slideDeck.add(slide0);
         slideDeck.add(slide1);
         editor.setSlideDeck(slideDeck);         
         editor.setSlideEditView(editView);
      }


      
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
