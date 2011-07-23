package org.sikuli.ui;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class DList<T> extends JList {


   DList(){
      setDragEnabled(true);
      setDropMode(DropMode.INSERT);
      setTransferHandler(new ArrayListTransferHandler<T>());
   }

}


/*
 * ArrayListTransferHandler.java is used by the 1.4 DragListDemo.java example.
 */

class ArrayListTransferHandler<T> extends TransferHandler {
   DataFlavor localArrayListFlavor, serialArrayListFlavor;

   String localArrayListType = DataFlavor.javaJVMLocalObjectMimeType
   + ";class=java.util.ArrayList";

   JList source = null;

   int[] indices = null;

   int addIndex = -1; //Location where items were added

   int addCount = 0; //Number of items added
   
   boolean isDragDrop = false;

   public ArrayListTransferHandler() {
      try {
         localArrayListFlavor = new DataFlavor(localArrayListType);
      } catch (ClassNotFoundException e) {
         System.out
         .println("ArrayListTransferHandler: unable to create data flavor");
      }
      serialArrayListFlavor = new DataFlavor(ArrayList.class, "ArrayList");
   }

   public boolean importData(TransferSupport support){
      System.out.println("importData");


      JList target = null;
      ArrayList<T> alist = null;

      JComponent c = (JComponent) support.getComponent();
      Transferable t = support.getTransferable();

      if (!canImport(c, t.getTransferDataFlavors())) {
         return false;
      }
      try {
         target = (JList) c;
         if (hasLocalArrayListFlavor(t.getTransferDataFlavors())) {
            System.out.println("localArrayList");
            alist = (ArrayList<T>) t.getTransferData(localArrayListFlavor);
         } else if (hasSerialArrayListFlavor(t.getTransferDataFlavors())) {
            System.out.println("serialArrayListFlavor");
            alist = (ArrayList<T>) t.getTransferData(serialArrayListFlavor);
         } else {
            return false;
         }
      } catch (UnsupportedFlavorException ufe) {
         System.out.println("importData: unsupported data flavor");
         return false;
      } catch (IOException ioe) {
         System.out.println("importData: I/O exception");
         return false;
      }

      //At this point we use the same code to retrieve the data
      //locally or serially.

      //We'll drop at the current selected index.
      int index = target.getLeadSelectionIndex();
      //int index = target.getSelectionIndex();

      if (support.isDrop()){
         // or the drop location
         index = ((JList.DropLocation) support.getDropLocation()).getIndex()-1;
         
         System.out.println("drop index: " + index);
         
         isDragDrop = true;  
         
         if (source.equals(target)) {
            if (indices != null && index >= indices[0] - 1
                  && index <= indices[indices.length - 1]) {
               indices = null;
               return true;
            }
         }
         
         MutableListModel<T> model = (MutableListModel<T>) source.getModel();
         for (int i = indices.length - 1; i >= 0; i--){
            // System.out.println("removing element at: " + indices[i]);
             model.removeElementAt(indices[i]);
          }

         
         MutableListModel<T> listModel = (MutableListModel<T>) target.getModel();

         int max = listModel.getSize();
         if (index < 0) {
            index = 0;  // insert to the front
         } else {
            index++;
            if (index > max) {
               index = max;
            }
         }
         addIndex = index;
         addCount = alist.size();
         listModel.insertElementsAt(alist, index);
         return true;
         
         //listModel.
      }else{
         isDragDrop = false;
      }

      //Prevent the user from dropping data back on itself.
      //For example, if the user is moving items #4,#5,#6 and #7 and
      //attempts to insert the items after item #5, this would
      //be problematic when removing the original items.
      //This is interpreted as dropping the same data on itself
      //and has no effect.
      if (source.equals(target)) {
         if (indices != null && index >= indices[0] - 1
               && index <= indices[indices.length - 1]) {
            indices = null;
            return true;
         }
      }
      
      MutableListModel<T> listModel = (MutableListModel<T>) target.getModel();

      int max = listModel.getSize();
      if (index < 0) {
         index = max;
      } else {
         index++;
         if (index > max) {
            index = max;
         }
      }
      addIndex = index;
      addCount = alist.size();
      listModel.insertElementsAt(alist, index);
      return true;
   }

   @Override
   public void exportToClipboard(JComponent comp, Clipboard clip, int action)
   throws IllegalStateException {
      addCount = 0;
      super.exportToClipboard(comp, clip, action);
   }

   protected void exportDone(JComponent c, Transferable data, int action) {
      //System.out.println("exportDone");
      // For Drop this is called after importData
      // For Cut this is called before importData
      
      System.out.println("action: " + action);
      if ((action == MOVE) && (indices != null) && (!isDragDrop)) {

         MutableListModel<T> model = (MutableListModel<T>) source.getModel();
         //       
         //       ArrayList<T> alist = null;
         //       try{
         //          if (hasLocalArrayListFlavor(data.getTransferDataFlavors())) {
         //             alist = (ArrayList<T>) data.getTransferData(localArrayListFlavor);
         //          } else if (hasSerialArrayListFlavor(data.getTransferDataFlavors())) {
         //             alist = (ArrayList<T>) data.getTransferData(serialArrayListFlavor);
         //          }
         //       } catch (UnsupportedFlavorException e) {
         //       } catch (IOException e) {
         //       }
         //
         //
         //       //addCount = alist.size();
         //System.out.println("Original:");
         for (int i = 0; i < indices.length; i++) {
           // System.out.println(indices[i]);
         }


         //      DefaultListModel model = (DefaultListModel) source.getModel();

         //If we are moving items around in the same list, we
         //need to adjust the indices accordingly since those
         //after the insertion point have moved.
         if (addCount > 0) {
            for (int i = 0; i < indices.length; i++) {
               if (indices[i] > addIndex) {
                  indices[i] += addCount;
               }
            }
         }
         
         //System.out.println("After:");
         for (int i = 0; i < indices.length; i++) {
            //System.out.println(indices[i]);
         }


         //System.out.println("addcount: "+addCount);
         for (int i = indices.length - 1; i >= 0; i--){
           // System.out.println("removing element at: " + indices[i]);
            model.removeElementAt(indices[i]);
         }

      }
      indices = null;
      addIndex = -1;
      addCount = 0;
   }

   private boolean hasLocalArrayListFlavor(DataFlavor[] flavors) {
      if (localArrayListFlavor == null) {
         return false;
      }

      for (int i = 0; i < flavors.length; i++) {
         if (flavors[i].equals(localArrayListFlavor)) {
            return true;
         }
      }
      return false;
   }

   private boolean hasSerialArrayListFlavor(DataFlavor[] flavors) {
      if (serialArrayListFlavor == null) {
         return false;
      }

      for (int i = 0; i < flavors.length; i++) {
         if (flavors[i].equals(serialArrayListFlavor)) {
            return true;
         }
      }
      return false;
   }

   public boolean canImport(JComponent c, DataFlavor[] flavors) {
      if (hasLocalArrayListFlavor(flavors)) {
         return true;
      }
      if (hasSerialArrayListFlavor(flavors)) {
         return true;
      }
      return false;
   }

   protected Transferable createTransferable(JComponent c) {
      if (c instanceof JList) {
         source = (JList) c;
         indices = source.getSelectedIndices();
         Object[] values = source.getSelectedValues();
         if (values == null || values.length == 0) {
            return null;
         }
         ArrayList<T> alist = new ArrayList<T>(values.length);
         for (int i = 0; i < values.length; i++) {
            Object o = values[i];
            T t = (T) o;
            //Object o1 = t.clone();
            alist.add(t);
         }
         return new ArrayListTransferable<T>(alist);
      }
      return null;
   }

   public int getSourceActions(JComponent c) {
      return COPY_OR_MOVE | LINK;
   }

   public class ArrayListTransferable<T> implements Transferable {
      ArrayList<T> data;

      public ArrayListTransferable(ArrayList<T> alist) {
         data = alist;
      }

      public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException {
         if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
         }
         return data;
      }

      public DataFlavor[] getTransferDataFlavors() {
         return new DataFlavor[] { //localArrayListFlavor,
               serialArrayListFlavor };
      }

      public boolean isDataFlavorSupported(DataFlavor flavor) {
         if (localArrayListFlavor.equals(flavor)) {
            return true;
         }
         if (serialArrayListFlavor.equals(flavor)) {
            return true;
         }
         return false;
      }
   }
}

