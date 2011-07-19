package org.sikuli.guide;

import java.io.File;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class PersistenceTestCase {

   
   @Test
   public void testSaveAndLoad() throws Exception{
      
      DefaultSprite in = new DefaultSprite(5,5,10,10);
      
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);
  
      File tmp = new File("test.xml");
      
      serializer.write(in, tmp);
      
      File fin = new File("test.xml");
      DefaultSprite out = serializer.read(DefaultSprite.class, fin);
      
      assertThat(in.getName(), equalTo(out.getName()));
      assertThat(in.getX(), equalTo(out.getX()));
      
      
      
      //System.out.print(out);
      //Strategy strategy = new CycleStrategy("id","ref");
      //Serializer serializer = new Persister(strategy);

   
      //File fout = SaveLoadHelper.getXMLFileFromBundle(destBundle);
   }

   
}


