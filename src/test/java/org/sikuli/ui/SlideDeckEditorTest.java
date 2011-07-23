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
   JLabel nameLabel = new JLabel();
   TestSlideEditView(){
      super();
      add(nameLabel);
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
   public void setName(String name){
      super.setName(name);
      nameLabel.setText(name);
   }

   @Override
   public void refresh(){
      super.refresh();
      if (getSlide()==null){
         editField.setText("");
         nameLabel.setText("NULL");         
      }else{
         editField.setText(getSlide().getName());
         nameLabel.setText("Name:" + getSlide().getName());         
      }
   }
}

public class SlideDeckEditorTest {

   public static class FixtureBase {

      protected Slide slide0;
      protected Slide slide1;
      protected Slide slide2;
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
         slide2 = mock(Slide.class);
         newSlide = mock(Slide.class);
         slideDeck = mock(SlideDeck.class);
         slides = new ArrayList<Slide>();
         mockedActionEvent = mock(ActionEvent.class);

         when(slide0.toString()).thenReturn("Slide 0");
         when(slide1.toString()).thenReturn("Slide 1");
         when(slide2.toString()).thenReturn("Slide 2");
         when(newSlide.toString()).thenReturn("New Slide");

         slides.add(slide0);
         slides.add(slide1);
         when(slideDeck.getSlides()).thenReturn(slides);

         editor.setSlideDeck(slideDeck);         
      }

   }

   public static class UserSlideSelectionTest extends WithConcreteFixtures {

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
      
      @Test
      public void testClickToSelectAndDeleteSlide(){
         int n = slideDeck.getSlides().size();

         fListView.clickItem(0);
         fListView.pressAndReleaseKeys(KeyEvent.VK_DELETE);

         fListView.requireItemCount(n-1);
         assertThat(editor.getSlideDeck().getSize(), equalTo(n-1));
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

   public static class UserCutCopyPasteTest extends WithConcreteFixtures {

      
      @Test
      public void testCut_AnotherSlideMustBeSelected_1() throws InterruptedException{
         // given slide 0 and slide 1         
         
         // when cut slide 0
         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_X);
         fListView.releaseKey(KeyEvent.VK_META);
         
         // slide 1 is selected
         assertThat(editor.editView.getSlide(), sameInstance(slide1));         
      }
      
      @Test
      public void testCut_AnotherSlideMustBeSelected_2() throws InterruptedException{
         // given slide 0 and slide 1
         
         // when cut slide 1
         fListView.clickItem(1);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_X);
         fListView.releaseKey(KeyEvent.VK_META);
         
         // slide 0 should be selected
         assertThat(editor.editView.getSlide(), sameInstance(slide0));         
      }
      
      @Test
      public void testCut_NoSlideShouldBeSelectedWhenEmpty() throws InterruptedException{
         // given slide 0 and slide 1
         
         // when cut slide 1 and slide 0
         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_SHIFT);
         fListView.pressAndReleaseKeys(KeyEvent.VK_DOWN);
         fListView.releaseKey(KeyEvent.VK_SHIFT);
         
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_X);
         fListView.releaseKey(KeyEvent.VK_META);
         
         // nothing should be selected
         assertThat(editor.editView.getSlide(), nullValue());         
      }
      
      @Test
      public void testCutTwoSlides_SlideAfterTheSecondSlideMustBeSelected() throws InterruptedException{
         // given slide 0 and slide 1 and slide 2
         slideDeck.insertElementAt(slide2, 2);
         
         // when cut slide 0 and slide 1 
         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_SHIFT);
         fListView.pressAndReleaseKeys(KeyEvent.VK_DOWN);
         fListView.releaseKey(KeyEvent.VK_SHIFT);
         
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_X);
         fListView.releaseKey(KeyEvent.VK_META);
         
         // slide 2 should be selected
         assertThat(editor.editView.getSlide(), sameInstance(slide2));
         
         fListView.requireSelection(0);
      }

      
      @Test
      public void testCutAndPasteInTheSamePlace() throws InterruptedException{
         // given slide 0 and slide 1 and slide 2
         slideDeck.insertElementAt(slide2, 2);
         
         // when cut slide 1 
         fListView.clickItem(1);
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_X);
         fListView.releaseKey(KeyEvent.VK_META);
         
         // slide 2 should be selected
         assertThat(editor.editView.getSlide(), sameInstance(slide2));
         
         // when paste
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);

         // a copy of slide 1 should be selected
         assertThat(editor.editView.getSlide().getName(), equalTo(slide1.getName()));
         

      }
      
      
      
      @Test
      public void testCutAndPasteTwoSlidesAndBothSlidesShouldBeSelected() throws InterruptedException{
         // given slide 0 and slide 1 and slide 2
         slideDeck.insertElementAt(slide2, 2);
         
         // when cut slide 0 and 1
         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_SHIFT);
         fListView.pressAndReleaseKeys(KeyEvent.VK_DOWN);
         fListView.releaseKey(KeyEvent.VK_SHIFT);
         
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_X);
         fListView.releaseKey(KeyEvent.VK_META);
         
         // slide 2 should be selected
         assertThat(editor.editView.getSlide(), sameInstance(slide2));
         
         // when paste
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);
         
         fListView.requireItemCount(3);

         // two slides should be selected
         fListView.requireSelectedItems(1,2);
         
      }
      
      @Test
      public void testCutAndPasteTwoSlidesTwice() throws InterruptedException{
         // given slide 0 and slide 1 and slide 2
         slideDeck.insertElementAt(slide2, 2);
         
         // when cut slide 0 and 1
         fListView.clickItem(0);
         fListView.pressKey(KeyEvent.VK_SHIFT);
         fListView.pressAndReleaseKeys(KeyEvent.VK_DOWN);
         fListView.releaseKey(KeyEvent.VK_SHIFT);
         
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_X);
         fListView.releaseKey(KeyEvent.VK_META);
         
         // slide 2 should be selected
         assertThat(editor.editView.getSlide().getName(), equalTo(slide2.getName()));
         
         // when paste 
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);
         
         fListView.requireItemCount(3);

         // when paste again
         fListView.pressKey(KeyEvent.VK_META);
         fListView.pressAndReleaseKeys(KeyEvent.VK_V);
         fListView.releaseKey(KeyEvent.VK_META);
         
         fListView.requireItemCount(5);

         assertThat(editor.editView.getSlide().getName(), equalTo(slide0.getName()));
         
         // 2 0 1 0 1
//          last two slides should be slide 0 and slide 1
         assertThat(((Slide)slideDeck.getElementAt(4)).getName(), equalTo(slide1.getName()));
         assertThat(((Slide)slideDeck.getElementAt(3)).getName(), equalTo(slide0.getName()));
         
      }
   }
   
   
   
   public static class UserSelectionTest1 extends WithConcreteFixtures {

      
      @Test
      public void testCutAndPasteAndUndo() throws InterruptedException{

         int n = slideDeck.getSize();

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

         int n = slideDeck.getSize();

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

         int n = slideDeck.getSize();

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

         int n = slideDeck.getSize();

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

         slideDeck.removeElement(slide0);
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


   }   

   public static class EditActionsTest extends WithConcreteFixtures {

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
      public void testDeleteTwoSelectedSlides(){
         int n = slideDeck.getSize();
         
         fListView.selectItems(0,1);

         editor.getEditActionFactory().deleteSelectedSlideAction().actionPerformed(mockedActionEvent);         
         fListView.requireItemCount(n-2);
      }


      @Test
      public void testDeleteSelectedSlideActionAndUndo() {
         int n = slideDeck.getSize();
         
         fListView.selectItem(0);

         editor.getEditActionFactory().deleteSelectedSlideAction().actionPerformed(mockedActionEvent);         
         fListView.requireItemCount(n-1);

         editor.undoAction().actionPerformed(mockedActionEvent);
         fListView.requireItemCount(n);
      }
      
      @Test
      public void testDeleteSelectedSlideAndUndoTwice() {
         int n = slideDeck.getSize();
         
         fListView.selectItem(0);

         editor.getEditActionFactory().deleteSelectedSlideAction().actionPerformed(mockedActionEvent);         
         fListView.requireItemCount(n-1);

         editor.getEditActionFactory().deleteSelectedSlideAction().actionPerformed(mockedActionEvent);         
         fListView.requireItemCount(n-2);

         editor.undoAction().actionPerformed(mockedActionEvent);
         fListView.requireItemCount(n-1);

         editor.undoAction().actionPerformed(mockedActionEvent);
         fListView.requireItemCount(n);
      }

      @Test
      public void testAddNewSlideAction() throws InterruptedException{
         int n = slides.size();         
         editor.getEditActionFactory().addNewSlideToEndAction(newSlide).actionPerformed(mockedActionEvent);

         assertThat(slideDeck.getSize(), equalTo(n+1));         
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
         slide2 = new DefaultSlide();
         slide2.setName("Slide 2");

         slideDeck = new DefaultSlideDeck();
         slideDeck.insertElementAt(slide0,0);
         slideDeck.insertElementAt(slide1,1);
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
