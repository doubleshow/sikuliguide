package org.sikuli.guide;

import java.awt.event.ActionEvent;
import java.io.File;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.mockito.Mockito.*;

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

   @Test
   public void testSaveAndLoadStory() throws Exception{
      
      Story story = new Story();
      story.addStep(FixtureFactory.createStepWithRelationships());
      story.addStep(FixtureFactory.createStep1());
      story.addStep(FixtureFactory.createStep());
      
      BundlePersisterSupport bps = new BundlePersisterSupport();
      
      BundleableDocumentEditor editor = mock(BundleableDocumentEditor.class);
//      when(editor.getBundleableDocument()).thenReturn(story);
//      bps.saveAction.actionPerformed(new ActionEvent(editor,0,"save"));
            
      Story emptyStory = new Story();
      when(editor.getBundleableDocument()).thenReturn(emptyStory);
      bps.loadAction.actionPerformed(new ActionEvent(editor,0,"load"));
      
      //bps.g
      
      
//      story.bundleSaveLoadSupport.bundlePath = new File("testbundle");
//      story.bundleSaveLoadSupport.saveStory(story);
//      //story.bundleSaveLoadSupport.readStory(story);
//      Story story1 = story.bundleSaveLoadSupport.loadStory();
      //Circle c = FixtureFactory.createCircle();
      //FlagText c = FixtureFactory.createFlagText();
//      ContextImage c = FixtureFactory.createContextImage();
//      Step c = FixtureFactory.createStep();   
//      
     // story.bundleSaveLoadSupport.saveStepImages(story);
      
      //testHelper(story, story);

      //c.get
   }
   
//   <T> void testHelper(Story story, T c) throws Exception{
//      story.bundleSaveLoadSupport.save(c);
//      T c1 = (T) story.bundleSaveLoadSupport.load(c.getClass());
//      story.bundleSaveLoadSupport.save(c1);      
//   }
   
}


