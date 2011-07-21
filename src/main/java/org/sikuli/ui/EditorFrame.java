package org.sikuli.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.undo.UndoManager;

interface EditorFrameContent{
   UndoManager getUndoManager();
}

public class EditorFrame extends JFrame{

   EditorFrame(){
      setJMenuBar(new MenuBar());
   }
   
   EditorFrameContent content;
   void setContent(EditorFrameContent content){
      this.content = content;      
   }
   
   JMenuItem undoMenuItem;
   class MenuBar extends JMenuBar{

      MenuBar(){

         JMenu menu = new JMenu("Edit");
         add(menu);

         JMenuItem menuItem;
         undoMenuItem = menu.add(UndoManagerHelper.getUndoAction(getUndoManager()));
         undoMenuItem.setMnemonic('U');
         undoMenuItem.setName("Undo");
         
         //undoMenuItem = menuItem;
         
         menuItem = menu.add(UndoManagerHelper.getRedoAction(getUndoManager()));
         menuItem.setMnemonic('R');
         menuItem.setName("Redo");
        
      }
   }

   
   // todo: this is ungly, fix
   SlideDeckEditor editor;   
   private UndoManager undoManager;
   public void setUndoManager(final UndoManager undoManager) {
      this.undoManager = undoManager;      
      undoMenuItem.setAction(new AbstractAction("Undo"){

         @Override
         public void actionPerformed(ActionEvent e) {
            UndoManagerHelper.getUndoAction(undoManager).actionPerformed(e);
            editor.refresh();
            
         }
         
         @Override
         public boolean isEnabled(){
            return undoManager.canUndo();
         }
         
      });
   }
   
   public UndoManager getUndoManager(){
      return undoManager;
   }


   
   
}
