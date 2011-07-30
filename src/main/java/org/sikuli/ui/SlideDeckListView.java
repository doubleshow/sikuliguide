package org.sikuli.ui;

import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SlideDeckListView extends JList {

   public SlideDeckListView(){
      super();
      setDragEnabled(true);
      setDropMode(DropMode.INSERT);
      setTransferHandler(new ArrayListTransferHandler<Slide>());
   }
   
   void setSlideDeck(Deck slideDeck){
      setModel(slideDeck);
   }
   
   protected Deck getSlideDeck(){
      return (Deck) getModel();
   }   
}

//
////@author Santhosh Kumar T - santhosh@in.fiorano.com
//class SlideDeckListViewTransferHandler extends NotifierTransferHandler{ 
//
//   public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException{ 
//      int clipboardAction = getSourceActions(comp) & action; 
//      if(clipboardAction!=NONE){ 
//
//         SlideDeckListView listView = (SlideDeckListView)comp; 
//         SlideDeck slideDeck = listView.getSlideDeck();
//         Slide slide = (Slide) listView.getSelectedValue();         
//         Transferable t = new SlideSelection(slideDeck, slide, action);
//         
//         if (clipboardAction == MOVE)
//            slideDeck.remove(slide);
//         
//         if(t!=null){ 
//            try{ 
//               clip.setContents(t, new ListClipboardOwner((JList)comp)); 
//               exportDone(comp, t, clipboardAction); 
//               return; 
//            } catch(IllegalStateException ise){ 
//               exportDone(comp, t, NONE); 
//               throw ise; 
//            } 
//         } 
//      } 
//      exportDone(comp, null, NONE); 
//   } 
//
//   public boolean importData(JComponent comp, Transferable t){ 
//      System.out.println("importData");
//      try{ 
//
//         for(int i = 0; i<t.getTransferDataFlavors().length; i++){ 
//          System.out.println(t.getTransferDataFlavors()[i]);
//         } 
//
//         
//         
//         Slide slide = (Slide) t.getTransferData(new DataFlavor(Slide.class, DataFlavor.javaRemoteObjectMimeType));
//         System.out.println("got slide from transfer:" + slide);
//
//         SlideDeckListView listView = (SlideDeckListView)comp; 
//         SlideDeck slideDeck = listView.getSlideDeck();         
//         int index = listView.getSelectedIndex();
//         slideDeck.add(index+1, slide);                
//         
//         acceptOrReject(t, true); 
//         return true; 
//      } catch(Exception e){ 
//         e.printStackTrace();
//         acceptOrReject(t, false); 
//         return false; 
//      } 
//   } 
//
//
//}
//
//
//class TransferableSlide implements Transferable{
//
//   Slide slide;
//   TransferableSlide(Slide slide){
//      this.slide = slide;
//   }
//
//   DataFlavor slideObjectFlavor = new DataFlavor(Slide.class, DataFlavor.javaRemoteObjectMimeType);
//   
//   @Override
//   public Object getTransferData(DataFlavor flavor)
//   throws UnsupportedFlavorException, IOException {
//      
//      if (flavor.equals(DataFlavor.stringFlavor)){
//         return slide.toString();
//      }else if (flavor.equals(slideObjectFlavor)){
//         return slide;
//      }      
//      return null;
//   }
//
//   @Override
//   public DataFlavor[] getTransferDataFlavors() {
//      return new DataFlavor[]{slideObjectFlavor, DataFlavor.stringFlavor};
//   }
//
//   @Override
//   public boolean isDataFlavorSupported(DataFlavor flavor) {
//      return true;
//   }
//
//}
//
//class SlideSelection extends TransferNotifierProxy{
//   
//   //private JList list; 
//   private int index;
//   
//   SlideDeck slideDeck;
//   Slide slide;
//   int action;
//   
//   boolean alreadyCut = false;
//   
//   public SlideSelection(SlideDeck slideDeck, Slide slide, int action){
//      super(new TransferableSlide(slide)); 
//      this.slideDeck = slideDeck;
//      this.slide = slide;
//      this.action = action;
//   } 
//
//   public void transferAccepted(){ 
//      System.out.println("TransferAccepted");
//      System.out.println(slideDeck);
//      System.out.println(slideDeck.size());
//      
//      if (action == TransferHandler.MOVE && !alreadyCut){      
//        // slideDeck.remove(slide);
//         alreadyCut = true;
//      }else{
//         //slideDeck.getSlides().remove(slide);
//      }
//      System.out.println(slideDeck.size());      
////      DefaultListModel model = (DefaultListModel)list.getModel();
////      model.removeElementAt(index);
//      //list.setLis
//      //super.transferAccepted(); 
//   }   
//}
//
////@author Santhosh Kumar T - santhosh@in.fiorano.com
//class ListItemSelection extends TransferNotifierProxy{ 
//   private JList list; 
//   private int index; 
//   private boolean alreadyCut = false;
//
//   SlideDeck slideDeck;
//   public ListItemSelection(JList list, SlideDeck slideDeck){
//      super(new StringSelection(((Slide)list.getSelectedValue()).getName())); 
//      this.slideDeck = slideDeck;
//      this.list = list; 
//      index = list.getSelectedIndex(); 
//   } 
//
//   public void transferAccepted(){ 
//      System.out.println("TransferAccepted");
//      if (!alreadyCut){
//         System.out.println("Removed");         
//         slideDeck.remove(index);
//      }
//      alreadyCut = true;
//      super.transferAccepted(); 
//   } 
//} 
