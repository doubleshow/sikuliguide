package org.sikuli.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.TransferHandler;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//
//class TestTranserHandler extends TransferHandler {
//   
//   @Override
//   protected void exportDone(JComponent source, Transferable data, int action) {
//
//      
//      
//      super.exportDone(source, data, action);
//   }
//
//   public int getSourceActions(JComponent c){ 
//      return LINK; 
//   } 
//   
//   public boolean canImport(TransferSupport info) {
//      
//      System.out.println("canImport called");
//      // for demo purposes, we'll only support drops and not clipboard paste
//      if (!info.isDrop()) {
//          return false;
//      }
//      
//      // we only support importing Strings
//      if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
//          return false;
//      }
//
//      // fetch the drop location (it's a JTree.DropLocation for JTree)
//      JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
//      System.out.println("dropped location:" + dl.getIndex());
//
//      //info.setDropAction(LINK);
//      
//      // we only support drops for valid paths in the tree
//      return true;//dl.getIndex() >= 0;
//  }
//   
//   @Override
//   public boolean importData(TransferSupport info) {
//      System.out.println("Got something dropped.");
//      try {
//         String s = (String) info.getTransferable().getTransferData(DataFlavor.stringFlavor);
//
//         JList list = (JList) info.getComponent();
//         int index = ((JList.DropLocation) info.getDropLocation()).getIndex();
//         
//         DefaultListModel listModel = (DefaultListModel) list.getModel();
//         listModel.add(index, s);
//         
//         // if source element is equal to dest element
//         //then it is a move action
//      
//      } catch (UnsupportedFlavorException ignored) {
//      } catch (IOException e) {
//      }
//      
//      
//      return true;
//   }
//   
//   public Transferable createTransferable(JComponent comp) {
//      System.out.println("Transferable to be created");
//      JList list = (JList) comp;
//      String str = (String) list.getSelectedValue();
//      return new  StringSelection(str);
//   }
//   
//}


class Person implements Serializable {
   
   String name;
   int id;
   
   DataFlavor serialFlavor = new DataFlavor(Person.class, DataFlavor.javaSerializedObjectMimeType);
  
   public String toString(){
      return String.format("[%d] %s: %s", id, name, hashCode());
   }

//   @Override
//   public Object getTransferData(DataFlavor flavor)
//         throws UnsupportedFlavorException, IOException {
//      return this;
//   }
//
//   @Override
//   public DataFlavor[] getTransferDataFlavors() {
//      return new DataFlavor[]{};
//   }
//
//   @Override
//   public boolean isDataFlavorSupported(DataFlavor flavor) {
//      return flavor.equals(serialFlavor);
//   }
   
}

class PersonCellRenderer extends JLabel implements ListCellRenderer{

   JLabel label = new JLabel();
   PersonCellRenderer(){         
      setLayout(new GridBagLayout());
      setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

      //_thumbView.setPreferredSize(new Dimension(140,140));        

      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      add(label,c);
      validate();
   }
   @Override
   public Component getListCellRendererComponent(
         JList list,              // the list
         Object value,            // value to display
         int index,               // cell index
         boolean isSelected,      // is the cell selected
         boolean cellHasFocus)    // does the cell have focus
   {

      Person person = (Person) value;

      label.setText(person.toString());

      setPreferredSize(new Dimension(150,50));
      //setSize(new Dimension(150,150));

      if (isSelected){
         setBorder(BorderFactory.createLineBorder(Color.red, 5));
      }else{
         setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      }

      return this;
   }   
}


public class DListTestCase {

   private DefaultMutableListModel<Person> listModel;
   private DList<Person> dList;

   protected FrameFixture window;
   private JFrame frame;

   @Before
   public void setUp(){
      
      dList = new DList();
      
      listModel = new DefaultMutableListModel<Person>();
      
//      String[] tags = new String[]{"apple","banana","coke","dog"}; 
//      for (int i=0;i<tags.length;++i){
//         ((DefaultListModel)listModel).addElement("" + i + " " + tags[i]);
//      }
//      dList.setModel(listModel);

      frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
         protected JFrame executeInEDT() {
            return new JFrame();  
         }
      });
      frame.setContentPane(dList);
      frame.pack();

      window = new FrameFixture(frame);
      window.show(); // shows the frame to test
   }


   @After
   public void tearDown(){
      window.cleanUp();
   }

   
   @Test
   public void testManuallyWithCustomClasses() throws InterruptedException{

      String[] names = new String[]{"Adam","Ben","Chris","David"}; 
      for (int i=0;i<names.length;++i){
         Person p = new Person();
         p.name = names[i];
         p.id = i;
         listModel.insertElementAt(p,i);
      }
      dList.setModel(listModel);
      dList.setCellRenderer(new PersonCellRenderer());
      dList.validate();
      frame.validate();
      
      
      
      frame.pack();
      
      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }

   }

   

   @Test
   public void testManually() throws InterruptedException{

      Object lock = new Object();
      synchronized(lock){
         lock.wait();
      }

   }

}
