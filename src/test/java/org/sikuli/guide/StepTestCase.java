package org.sikuli.guide;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

public class StepTestCase {

   @Test
   public void testSetContextImage(){
            
      AbstractContextImage contextImage = mock(AbstractContextImage.class);
      
      
      Step step = new Step();
      
      step.setContextImage(contextImage);
      
      assertThat(step.getContextImage(), sameInstance(contextImage));
      
   }
}
