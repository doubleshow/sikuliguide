package org.sikuli.guide;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class StepTestCase {

//   @Test
//   public void testAddRelationship(){
//      
//      Relationship rel = mock(Relationship.class);
//      
//      Step step = new Step();
//      step.addRelationship(rel);
//      
//      assertThat(step.getRelationships(), hasItem(rel));
//   }


//   @Test
//   public void testAddRelationship_SpritesMustBeAlreadyPartOfTheStep(){
//      Sprite s1 = mock(Sprite.class);
//      Sprite s2 = mock(Sprite.class);
//
//      Relationship rel = mock(Relationship.class);
//      when(rel.getParent()).thenReturn(s1);
//      when(rel.getDependent()).thenReturn(s2);
//
//      
//      
//
//   }
   
   private Step step = new Step();
   private Relationship existingRelationship1 = mock(Relationship.class);   
   private Relationship existingRelationship2 = mock(Relationship.class);   
   private Sprite parent = mock(Sprite.class);
   private Sprite dependent1 = mock(Sprite.class);
   private Sprite dependent2 = mock(Sprite.class);
   
   @Before
   public void setUp(){      
      when(existingRelationship1.getParent()).thenReturn(parent);
      when(existingRelationship1.getDependent()).thenReturn(dependent1);

      when(existingRelationship2.getParent()).thenReturn(parent);      
      when(existingRelationship2.getDependent()).thenReturn(dependent2);      
      
      step.addRelationship(existingRelationship1);      
      step.addRelationship(existingRelationship2);
   }
   
   @Test
   public void testAddOneNewRelationship(){      
      Relationship newRelationship = mock(Relationship.class);      
      step.addRelationship(newRelationship);      
      assertThat(step.getRelationships(), hasItem(newRelationship));
   }
   
   @Test
   public void testRemoveRelationshipsForASprite_parent(){
      step.removeRelationships(parent);
      assertThat(step.getRelationships().size(), equalTo(0));
   }
   
   @Test
   public void testRemoveRelationshipsForASprite_dependent1(){
      step.removeRelationships(dependent1);
      assertThat(step.getRelationships(), not(hasItem(existingRelationship1)));
      assertThat(step.getRelationships(), hasItem(existingRelationship2));
   }

   @Test
   public void testRemoveRelationshipsForASprite_dependent2(){
      step.removeRelationships(dependent2);
      assertThat(step.getRelationships(), not(hasItem(existingRelationship2)));
      assertThat(step.getRelationships(), hasItem(existingRelationship1));
   }   
   
   @Test
   public void testRemoveRelationshipsForASprite_nontExistingSprite(){
      Sprite s = mock(Sprite.class);
      step.removeRelationships(s);
      assertThat(step.getRelationships(), hasItems(existingRelationship1,existingRelationship2));
   }   
   
   
   @Test
   public void testRemoveOneExistingRelationship(){      
      step.removeRelationship(existingRelationship1);
      assertThat(step.getRelationships(), not(hasItem((existingRelationship1))));
   }
 
}
