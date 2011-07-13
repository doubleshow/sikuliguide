package org.sikuli.guide;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


import org.fest.swing.core.ComponentDragAndDrop;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JPanelFixture;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StepEditViewTestCase {

   private FrameFixture window;
   private Text text = FixtureFactory.createText();
   private Circle circle = FixtureFactory.createCircle();
   private FlagText flag = FixtureFactory.createFlagText();
   private ContextImage contextImage = FixtureFactory.createContextImage();
   private StepEditView stepView;
   private Step step;
   
   
   private JPanelFixture textPropertyEditor;
   private JPanelFixture textView;
   private JPanelFixture flagView;
   private JPanelFixture circleView;
   private JPanelFixture controlBox;
   private JLabelFixture contextImageView;
      
   @After
   public void tearDown() {
      window.cleanUp();
   }
   
   @Before 
   public void setUp() {
      
      step = new Step();
      step.addSprite(text);
      step.addSprite(circle);
      step.addSprite(flag);
      step.setContextImage(contextImage);
      
      stepView = new StepEditView(step);
      
      ScrollPaneFrame f = GuiActionRunner.execute(new GuiQuery<ScrollPaneFrame>() {
          protected ScrollPaneFrame executeInEDT() {
            return new ScrollPaneFrame(stepView);  
          }
      });
      
      window = new FrameFixture(f);
      window.show(); // shows the frame to test
      window.resizeTo(new Dimension(800,600));
      
      textPropertyEditor = window.panel("TextPropertyEditor");
      textView = window.panel("Text");
      flagView = window.panel("Flag");
      circleView = window.panel("Circle");
      controlBox = window.panel("ControlBox");
      contextImageView = window.label("ContextImage");
   }
   
   @Test
   public void testClickToSelectASpite() {    
      textView.click();      
      assertThat(stepView.getSelectedSprite(), sameInstance((Sprite)text));   
      
      controlBox.requireVisible();
   }
   
   @Test
   public void testClickOnContextImageToUnSelect(){
     
      textView.click();      
      assertThat(stepView.getSelectedSprite(), sameInstance((Sprite)text));      

      contextImageView.click();
      assertThat(stepView.getSelectedSprite(), nullValue());
      
   }
   
   @Test
   public void testClickOnContextImageToUnSelect_ControlBoxShouldBeInvisible(){
     
      textView.click();      
      contextImageView.click();
      
      controlBox.requireNotVisible();      
   }
   
   @Test
   public void testClickOnContextImageToUnSelectAndSelectAnotherSprite_ControlBoxShouldBeVisible(){     
      textView.click();      
      contextImageView.click();
      flagView.click();      
      controlBox.requireVisible();
   }

   
   @Test
   public void testClickToSelectSpriteOneAfterAnother() {
    
      textView.click();      
      assertThat(stepView.getSelectedSprite(), sameInstance((Sprite)text));
      
      flagView.click();      
      assertThat(stepView.getSelectedSprite(), sameInstance((Sprite)flag));
      
   }
   
   @Test
   public void testPressESCtoUnselect(){
      
      textView.click();            
      textView.focus();
      textView.pressAndReleaseKeys(KeyEvent.VK_ESCAPE);  

      assertThat(stepView.getSelectedSprite(), nullValue());     
   }
   
   @Test
   public void testDragText_BothViewAndModelShouldMove(){

      textView.click();            
      //textView.d();
      
      Component t = textView.target;      
      Component c = contextImageView.target;
      
      ComponentDragAndDrop cdd = new ComponentDragAndDrop(window.robot);
      cdd.drag(t, new Point(10,10));
      cdd.drop(c, new Point(50,40));
      
      Point newLocation = t.getLocation();
      
      assertThat(newLocation.x, equalTo(40));
      assertThat(text.getX(), equalTo(40));

      assertThat(newLocation.y, equalTo(30));
      assertThat(text.getY(), equalTo(30));

   }
   
   @Test
   public void testPressDeleteToRemoveTheSelectedSprite() {
    
      textView.click();            
      textView.focus();
      textView.pressAndReleaseKeys(KeyEvent.VK_DELETE);  

      assertThat(step.getSprites(), not(hasItem((Sprite)text)));      
   }
   
   @Test
   public void testDoubleClickToBringUpTextPropertyEditor(){      
      textView.doubleClick();      
      textPropertyEditor.requireVisible();
   }
   
   @Test
   public void testDoubleClickToBringUpTextPropertyEditor_andAnother(){      
      textView.doubleClick();      
      textPropertyEditor.requireVisible();
      
      flagView.doubleClick();      
      textPropertyEditor.requireVisible();
      textPropertyEditor.textBox().requireText(flag.getText());
   }

   
   @Test
   public void testDoubleClickOnNonTextSprite_ShouldNotBringUpTextPropertyEditor(){      
      circleView.doubleClick();      
      textPropertyEditor.requireNotVisible();
   }

   
   @Test
   public void testEnterNewTextInTextPropertyEditor_TextShouldBeUpdated(){      
      textView.doubleClick();      
      textPropertyEditor.textBox().enterText("New Text");
      textPropertyEditor.textBox().pressAndReleaseKeys(KeyEvent.VK_ENTER);
      
      assertThat(text.getText(), equalTo("New Text"));
   }
   
   @Test
   public void testEnterNewTextInTextPropertyEditorAndPressESCToCancel_TextShouldNotBeUpdated(){      
      String oldText = text.getText();
      
      textView.doubleClick();      
      textPropertyEditor.textBox().enterText("New");
      textPropertyEditor.textBox().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
      
      assertThat(text.getText(), equalTo(oldText));
   }
   
   @Test
   public void testEnterNewTextInTextPropertyEditorAndClickOnContextImage_TextShouldBeUpdated(){      
      
      textView.doubleClick();      
      textPropertyEditor.textBox().enterText("New");
      contextImageView.click();
      
      assertThat(text.getText(), equalTo("New"));
   }

   @Test
   public void testEnterNewTextInTextPropertyEditorAndClickOnAnotherSprite_TextShouldBeUpdated(){      
      
      textView.doubleClick();      
      textPropertyEditor.textBox().enterText("New");
      flagView.click();
      
      assertThat(text.getText(), equalTo("New"));   
      
      textPropertyEditor.requireNotVisible();
   }

   
   GenericTypeMatcher<JPanel> withNameIgnoringVisibility(final String name){
      return new GenericTypeMatcher<JPanel>(JPanel.class, false){
         @Override
         protected boolean isMatching(JPanel p) {
            return p.getName() != null && p.getName().equals(name);
         }         
      };
   }
   
   @Test
   public void testPressESCToCloseTextPropertyEditor(){      
      textView.doubleClick();      
      textPropertyEditor.requireVisible();
      textPropertyEditor.textBox().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);

      textPropertyEditor.requireNotVisible();         
   }
   
   @Test
   public void testPressDeleteToRemoveTheSelectedSprite_ControlBoxShouldBeInvisible() {
    
      textView.click();            
      textView.focus();
      textView.pressAndReleaseKeys(KeyEvent.VK_DELETE);  

      window.panel(new GenericTypeMatcher<JPanel>(JPanel.class, false){
         @Override
         protected boolean isMatching(JPanel p) {
            return p.getName() != null && p.getName().equals("ControlBox");
         }         
      }).requireNotVisible();      
      
   }
   
}
