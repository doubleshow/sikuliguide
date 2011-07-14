package org.sikuli.guide;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;

public class RelationshipTestCase {
   
   @Test
   public void testSideRelationship_DependentAboveParent(){
      
      Sprite p = new DefaultSprite(400,400,100,100);
      Sprite d = new DefaultSprite(0,0,80,100);
      
      Relationship r = new SideRelationship(p,d, SideRelationship.Side.ABOVE);       
      r.update();
            
      assertThat(d.getY(), equalTo(p.getY()-d.getHeight()));
      assertThat(d.getX(), equalTo(p.getX()+10));      
   }

   @Test
   public void testSideRelationship_DependentShouldUpdateWhenParentMoved(){
      
      Sprite p = new DefaultSprite(400,400,100,100);
      Sprite d = new DefaultSprite(0,0,80,100);
      
      Relationship r = new SideRelationship(p,d, SideRelationship.Side.ABOVE);

      p.setX(500);      
      assertThat(d.getY(), equalTo(p.getY()-d.getHeight()));
      assertThat(d.getX(), equalTo(p.getX()+10));      
      
      p.setX(300);      
      assertThat(d.getY(), equalTo(p.getY()-d.getHeight()));
      assertThat(d.getX(), equalTo(p.getX()+10));      
      
   }
   
   
}
