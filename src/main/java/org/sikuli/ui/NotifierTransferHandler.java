package org.sikuli.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

class NotifierTransferHandler extends TransferHandler {
   
   public int getSourceActions(JComponent c){ 
      return COPY_OR_MOVE; 
   } 

   public boolean canImport(JComponent comp, DataFlavor[] transferFlavors){ 
      for(int i = 0; i<transferFlavors.length; i++){ 
         if(transferFlavors[i].equals(DataFlavor.stringFlavor)) 
            return true; 
      } 
      return false; 
   } 
   
   protected void acceptOrReject(Transferable t, boolean accept){ 
      try{ 
         if(t.isDataFlavorSupported(TransferNotifier.NOTIFICATION_FLAVOR)){ 
            TransferNotifier notifier = (TransferNotifier)t.getTransferData(TransferNotifier.NOTIFICATION_FLAVOR); 
            if(accept) 
               notifier.transferAccepted(); 
            else 
               notifier.transferRejected(); 
         } 
      } catch(UnsupportedFlavorException e){ 
         e.printStackTrace(); // impossible 
      } catch(IOException e){ 
         e.printStackTrace(); 
      } 
   } 
}

//@author Santhosh Kumar T - santhosh@in.fiorano.com
interface TransferNotifier{ 
   public static DataFlavor NOTIFICATION_FLAVOR = 
      new DataFlavor(TransferNotifier.class, TransferNotifier.class.getName()); 

   public void transferAccepted(); 
   public void transferRejected(); 
} 

//@author Santhosh Kumar T - santhosh@in.fiorano.com
abstract class TransferNotifierProxy implements Transferable, TransferNotifier{ 
   Transferable delegate; 

   public TransferNotifierProxy(Transferable delegate){ 
      this.delegate = delegate; 
   } 

   public DataFlavor[] getTransferDataFlavors(){ 
      DataFlavor delegateFlavors[] = delegate.getTransferDataFlavors(); 
      DataFlavor flavors[] = new DataFlavor[delegateFlavors.length + 1]; 
      System.arraycopy(delegateFlavors, 0, flavors, 0, delegateFlavors.length); 
      flavors[flavors.length-1] = NOTIFICATION_FLAVOR; 
      return flavors; 
   } 

   public boolean isDataFlavorSupported(DataFlavor flavor){ 
      return flavor.equals(NOTIFICATION_FLAVOR) 
      || delegate.isDataFlavorSupported(flavor); 
   } 

   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException{ 
      return flavor.equals(NOTIFICATION_FLAVOR) 
      ? this 
            : delegate.getTransferData(flavor); 
   } 

   protected void clearClipBoard(){ 
      // how to clear the clipboard contents ? 
      Toolkit.getDefaultToolkit().getSystemClipboard() 
      .setContents(new StringSelection(""), null); 
   } 

   public void transferAccepted(){ 
      //clearClipBoard(); 
   } 

   public void transferRejected(){ 
     // clearClipBoard(); 
   } 
}



//@author Santhosh Kumar T - santhosh@in.fiorano.com
class ListClipboardOwner implements ClipboardOwner{ 
 // used as JList's client property 
 public static final String CLIP_BOARD_OWNER = "ClipBoardOwner"; 

 private JList list; 
 private int index; 

 public ListClipboardOwner(JList list){ 
    this.list = list; 
    index = list.getSelectedIndex(); 
    list.putClientProperty(CLIP_BOARD_OWNER, this); 
    list.paintImmediately(list.getCellBounds(index, index)); 
 } 

 public int getIndex(){ 
    return index; 
 } 

 public void lostOwnership(Clipboard clipboard, Transferable contents){ 
    if(list.getClientProperty(CLIP_BOARD_OWNER)==this) 
       list.putClientProperty(CLIP_BOARD_OWNER, null); 
    list.paintImmediately(list.getCellBounds(index, index)); 
 } 
}