package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.sikuli.ui.DefaultSlide;
import org.sikuli.ui.DefaultSlideDeck;
import org.sikuli.ui.Slide;
import org.sikuli.ui.SlideDeck;
import org.sikuli.ui.SlideDeckEditor;
import org.sikuli.ui.SlideDeckListView;

public class StoryEditorTestCase {

   public static class FixtureBase {

      protected Slide slide0;
      protected Slide slide1;
      protected Slide slide2;
      protected Slide newSlide;
      protected SlideDeck slideDeck;
      protected List<Slide> slides;
      protected ActionEvent mockedActionEvent;

      protected StoryEditor editor;
      protected FrameFixture window;
      protected JListFixture fListView;
      protected JPanelFixture fEditView;

      @Before
      public void setUp(){

         editor = new StoryEditor();
         editor.setPreferredSize(new Dimension(640,480));
         JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            protected JFrame executeInEDT() {
               return new JFrame();  
            }
         });
         frame.setContentPane(editor);
         frame.pack();



         slide0 = FixtureFactory.createStep();         
         slide0.setName("Step 0");
         slide1 = FixtureFactory.createStep1();
         slide1.setName("Step 1");
         slide2 = FixtureFactory.createStep1();
         slide2.setName("Step 2");

         
//         slideDeck = new DefaultSlideDeck();
//         slideDeck.insertElementAt(new DefaultSlide("Slide 0"),0);
//         slideDeck.insertElementAt(new DefaultSlide("Slide 1"),0);
//         slideDeck.insertElementAt(new DefaultSlide("Slide 2"),0);

       slideDeck = new Story();
         slideDeck.insertElementAt(slide0,0);
         slideDeck.insertElementAt(slide1,1);
         slideDeck.insertElementAt(slide2,2);

         editor.setSlideDeck(slideDeck);
         
         editor.setSlideEditView(new StepEditView());

         SlideDeckListView listView = new SlideDeckListView();
         listView.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
         //listView.setCellRenderer(new TestStepCellRenderer());
         listView.setCellRenderer(new MyCellRenderer());
         editor.setSlideDeckListView(listView);

         
         window = new FrameFixture(frame);
         window.show(); // shows the frame to test
      }

      @After
      public void tearDown(){
         window.cleanUp();
      }

      @Test
      public void testDeleteSlidesThenUndo() throws InterruptedException {         
         int n = slideDeck.getSize();
         slideDeck.removeElement(slide0);       
         slideDeck.removeElement(slide1);      
         editor.undoAction().actionPerformed(mockedActionEvent);
         editor.undoAction().actionPerformed(mockedActionEvent);
         assertThat(slideDeck.getSize(), equalTo(n));         
      }
      
      @Test
      public void testDeleteASpriteThenUndo() throws InterruptedException {         
         //int n = slideDeck.size();
         //slideDeck.remove(0);       
         //slideDeck.remove(0);
         ((Step)slide0).removeSprite(0);
         ((Step)slide0).removeSprite(0);
         ((Step)slide0).removeSprite(0);
         editor.undoAction().actionPerformed(mockedActionEvent);
         editor.undoAction().actionPerformed(mockedActionEvent);
         editor.undoAction().actionPerformed(mockedActionEvent);

         //         editor.undoAction().actionPerformed(mockedActionEvent);
//         assertThat(slideDeck.size(), equalTo(n));        
         
         Object lock = new Object();
         synchronized(lock){
            lock.wait();         
         }

      }
      
      @Test
      public void testManually() throws InterruptedException{

//         editor.getEditActionFactory().addNewSlideToEndAction(slide1).actionPerformed(null);


         Object lock = new Object();
         synchronized(lock){
            lock.wait();         
         }
      }
   }
}


class TestStepCellRenderer extends JLabel implements ListCellRenderer{

   JLabel label = new JLabel();
   TestStepCellRenderer(){         
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

      Step step = (Step) value;

      label.setText(""+step.getSprites().size() + " sprites");

      setPreferredSize(new Dimension(80,80));

      if (isSelected){
         setBorder(BorderFactory.createLineBorder(Color.red, 5));
      }else{
         setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      }

      return this;
   }   
}

class MyCellRenderer extends JPanel implements ListCellRenderer {
   // final static ImageIcon longIcon = new ImageIcon("long.gif");
   // final static ImageIcon shortIcon = new ImageIcon("short.gif");

   // This is the only method defined by ListCellRenderer.
   // We just reconfigure the JLabel each time we're called.

   StepThumbView _stepView = new StepThumbView();

   MyCellRenderer(){         
      setLayout(new GridBagLayout());
      setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

      _stepView.setPreferredSize(new Dimension(100,100));

      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      add(_stepView,c);
      validate();
   }

   public Component getListCellRendererComponent(
         JList list,              // the list
         Object value,            // value to display
         int index,               // cell index
         boolean isSelected,      // is the cell selected
         boolean cellHasFocus)    // does the cell have focus
   {

      Step step = (Step) value;
      _stepView.setStep(step);
      _stepView.setPreferredSize(new Dimension(100,100));
      setPreferredSize(new Dimension(120,120));
      
      if (isSelected){
         setBorder(BorderFactory.createLineBorder(Color.red, 5));
      }else{
         setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      }

      return this;
   }


}
