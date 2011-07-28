package org.sikuli.ui;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class Bundler {

   final static String SAVE = "save";
   final static String OPEN = "open";
   public class BundlerAction extends AbstractAction {

      @Override
      public void actionPerformed(ActionEvent e) {

         if (!(e.getSource() instanceof BundleableDocumentOwner)){
            return;
         }

         BundleableDocumentOwner editor = (BundleableDocumentOwner) e.getSource();
         BundleableDocument document = editor.getBundleableDocument();

         try {

            if (e.getActionCommand().equals(SAVE)){
               save(document);
            }else if (e.getActionCommand().equals(OPEN)){
               BundleableDocument newDocument = document.getClass().newInstance();
               open(newDocument);
               editor.setBundleableDocument(newDocument);
            }


         } catch (Exception e1) {
            e1.printStackTrace();
         }


      }

      void save(BundleableDocument source) throws Exception {
         if (getBundlePath() == null){
            File bundlePath = chooseBundlePathToSave();
            if (bundlePath == null) // user cancel
               return;
            setBundlePath(bundlePath);
         }
         
         
         prepareBundlePathToSave();            
         clearBundle();
         source.writeToBundle(bundlePath);
         for (Bundleable b : source.getBundleables()){
            b.writeToBundle(getBundlePath());            
         }
      }

      void open(BundleableDocument source) throws Exception {
         if (chooseBundlePathToOpen() == null)
            return;  // user cancel
         source.readFromBundle(bundlePath);
         for (Bundleable b : source.getBundleables()){
            b.readFromBundle(getBundlePath());            
         }
      }
   }
   
   

   public Action getSaveAction(){ 
      return new BundlerAction() {   
         {
            putValue(Action.ACTION_COMMAND_KEY, SAVE);
            putValue(Action.NAME, "saveAction");
         }
      };
   }

   public Action getLoadAction(){ 
      return new BundlerAction() {
         {
            putValue(Action.ACTION_COMMAND_KEY, OPEN);
            putValue(Action.NAME, "loadAction");
         }
      };
   }

   File bundlePath;   
   File getBundlePath(){
      return bundlePath;
   }
   void setBundlePath(File bundlePath){
      this.bundlePath = bundlePath;
   }
   
   

   void clearBundle(){
      // TODO: delete only those image files no longer
      // referred to by the story
      for (File file : getBundlePath().listFiles(new FileFilter(){

         @Override
         public boolean accept(File f) {
            return f.getAbsolutePath().endsWith("png");
         }

      })){

         file.delete();

      }      
   }

   File chooseBundlePathToSave(){
      
      // if MAC
      FileDialog fd = new FileDialog(new JFrame(), "Some message", FileDialog.SAVE);      
      fd.setFilenameFilter( new GeneralFileFilter("sikuli","Sikuli source files (*.sikuli)"));      
      fd.setVisible(true);
      if (fd.getFile() == null)
         return null;
      else
         return new File(fd.getDirectory(), fd.getFile());         
   }
   
   File chooseBundlePathToOpen(){
      FileDialog fd = new FileDialog(new JFrame(), "Some message", FileDialog.LOAD);
      fd.setFilenameFilter( new GeneralFileFilter("sikuli","Sikuli source files (*.sikuli)"));
      fd.setVisible(true);

      if(fd.getFile() == null)
         return null;
      else
         return new File(fd.getDirectory(), fd.getFile());
   }

   void prepareBundlePathToSave(){
//      if (getBundlePath() == null){
//         // TODO: popup a dialog tochoose a bundlepath
//         
//         FileDialog fd = new FileDialog(new JFrame(), "Some message", FileDialog.SAVE);
////         for(GeneralFileFilter filter: filters)
////            fd.setFilenameFilter(filter);
//         fd.setVisible(true);
////         if(fd.getFile() == null)
////            return null;
////         return new File(fd.getDirectory(), fd.getFile());
//         
//         File bundlePath = new File(fd.getDirectory(), fd.getFile());         
//         setBundlePath(bundlePath);
//      }


      if (!getBundlePath().exists()){
         getBundlePath().mkdir();
      }
   }
}