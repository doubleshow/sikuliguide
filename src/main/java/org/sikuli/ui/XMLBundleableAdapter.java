package org.sikuli.ui;

import java.io.File;
import java.io.IOException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

public class XMLBundleableAdapter implements Bundleable{
   private Strategy strategy = new CycleStrategy("id","ref");
   private Serializer serializer = new Persister(strategy);    

   Object object;
   public XMLBundleableAdapter(Object object){
      this.object = object;
   }

   @Override
   public void writeToBundle(File bundlePath) throws IOException {
      try {
         serializer.write(object, new File(bundlePath, "data.xml"));
      } catch (Exception e) {
         throw new IOException("can not write to bundle path: " + bundlePath.getAbsolutePath());
      }         
   }

   @Override
   public void readFromBundle(File bundlePath) throws IOException {
      try{
         serializer.read(object, new File(bundlePath, "data.xml"));
      } catch (Exception e) {
         throw new IOException("can not read from bundle path: " + bundlePath.getAbsolutePath() + " because " + e.getMessage());
      }         
   }
}