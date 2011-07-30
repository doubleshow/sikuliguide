package org.sikuli.ui;

import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SlideDeckListView extends JList {

   public SlideDeckListView(){
      super();
      setDragEnabled(false);
   }
   
   void setSlideDeck(Deck slideDeck){
      setModel(slideDeck);
   }
   
   protected Deck getSlideDeck(){
      return (Deck) getModel();
   }   
}
