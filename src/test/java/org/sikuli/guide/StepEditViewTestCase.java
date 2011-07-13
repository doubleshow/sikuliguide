package org.sikuli.guide;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


import static org.fest.swing.core.matcher.FrameMatcher.*;

import org.fest.swing.core.ComponentDragAndDrop;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.NameMatcher;
import org.fest.swing.core.matcher.NamedComponentMatcherTemplate;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;


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
   }
   
   @Test
   public void testClickToSelectASpite() {    
      window.panel("Text").click();      
      assertThat(stepView.getSelectedSprite(), sameInstance((Sprite)text));   
      
      window.panel("ControlBox").requireVisible();
   }
   
   @Test
   public void testClickOnContextImageToUnSelect(){
     
      window.panel("Text").click();      
      assertThat(stepView.getSelectedSprite(), sameInstance((Sprite)text));      

      window.label("ContextImage").click();
      assertThat(stepView.getSelectedSprite(), nullValue());
      
   }
   
   @Test
   public void testClickOnContextImageToUnSelect_ControlBoxShouldBeInvisible(){
     
      window.panel("Text").click();      
      window.label("ContextImage").click();
      
      window.panel(new GenericTypeMatcher<JPanel>(JPanel.class, false){
         @Override
         protected boolean isMatching(JPanel p) {
            return p.getName() != null && p.getName().equals("ControlBox");
         }         
      }).requireNotVisible();      
   }
   
   @Test
   public void testClickOnContextImageToUnSelectAndSelectAnotherSprite_ControlBoxShouldBeVisible(){
     
      window.panel("Text").click();      
      window.label("ContextImage").click();
      window.panel("Flag").click();
      
      window.panel("ControlBox").requireVisible();
   }

   
   @Test
   public void testClickToSelectSpriteOneAfterAnother() {
    
      window.panel("Text").click();      
      assertThat(stepView.getSelectedSprite(), sameInstance((Sprite)text));
      
      window.panel("Flag").click();      
      assertThat(stepView.getSelectedSprite(), sameInstance((Sprite)flag));
      
   }
   
   @Test
   public void testPressESCtoUnselect(){
      
      window.panel("Text").click();            
      window.panel("Text").focus();
      window.panel("Text").pressAndReleaseKeys(KeyEvent.VK_ESCAPE);  

      assertThat(stepView.getSelectedSprite(), nullValue());     
   }
   
   @Test
   public void testDragText_BothViewAndModelShouldMove(){

      window.panel("Text").click();            
      //window.panel("Text").d();
      
      Component textView = window.panel("Text").target;      
      Component contextImageView = window.label("ContextImage").target;
      
      ComponentDragAndDrop cdd = new ComponentDragAndDrop(window.robot);
      cdd.drag(textView, new Point(10,10));
      cdd.drop(contextImageView, new Point(50,40));
      
      Point newLocation = textView.getLocation();
      
      assertThat(newLocation.x, equalTo(40));
      assertThat(text.getX(), equalTo(40));

      assertThat(newLocation.y, equalTo(30));
      assertThat(text.getY(), equalTo(30));

   }
   
   @Test(expected=org.fest.swing.exception.ComponentLookupException.class)
   public void testPressDeleteToRemoveTheSelectedSprite() {
    
      window.panel("Text").click();            
      window.panel("Text").focus();
      window.panel("Text").pressAndReleaseKeys(KeyEvent.VK_DELETE);  

      assertThat(step.getSprites(), not(hasItem((Sprite)text)));      
      window.panel("Text");     
      
   }
   
   @Test
   public void testPressDeleteToRemoveTheSelectedSprite_ControlBoxShouldBeInvisible() {
    
      window.panel("Text").click();            
      window.panel("Text").focus();
      window.panel("Text").pressAndReleaseKeys(KeyEvent.VK_DELETE);  

      window.panel(new GenericTypeMatcher<JPanel>(JPanel.class, false){
         @Override
         protected boolean isMatching(JPanel p) {
            return p.getName() != null && p.getName().equals("ControlBox");
         }         
      }).requireNotVisible();      
      
   }
   
}
