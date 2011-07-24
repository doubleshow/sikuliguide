package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.sikuli.ui.SlideDeckListView;

import net.miginfocom.swing.MigLayout;

public class StoryListView extends SlideDeckListView {
   
   class MyCellRenderer extends JPanel implements ListCellRenderer {
      // final static ImageIcon longIcon = new ImageIcon("long.gif");
      // final static ImageIcon shortIcon = new ImageIcon("short.gif");

      // This is the only method defined by ListCellRenderer.
      // We just reconfigure the JLabel each time we're called.

      StepThumbView _stepView = new StepThumbView();
      JLabel _indexLabel = new JLabel();

      MyCellRenderer(){         
         setLayout(new MigLayout("","[center]"));
         setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

         _stepView.setPreferredSize(new Dimension(100,100));

//         GridBagConstraints c = new GridBagConstraints();
//         c.anchor = GridBagConstraints.CENTER;
         add(_stepView, "wrap");
         add(_indexLabel);
         
//         c = new GridBagConstraints();
//         c.anchor = GridBagConstraints.BASELINE_LEADING;
//         add(new JLabel("Test"),c);
         //validate();
      }

      public Component getListCellRendererComponent(
            JList list,              // the list
            Object value,            // value to display
            int index,               // cell index
            boolean isSelected,      // is the cell selected
            boolean cellHasFocus)    // does the cell have focus
      {

         Step step = (Step) value;
         _stepView.setStep(step);
         _stepView.setPreferredSize(new Dimension(100,100));
         setPreferredSize(new Dimension(120,120));
         _indexLabel.setText(""+(index+1));
         
         if (isSelected){
            setBorder(BorderFactory.createLineBorder(Color.red, 5));
         }else{
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         }

         return this;
      }


   }

   
//   class MyCellRenderer extends JPanel implements ListCellRenderer {
////      final static ImageIcon longIcon = new ImageIcon("long.gif");
////      final static ImageIcon shortIcon = new ImageIcon("short.gif");
//
//      // This is the only method defined by ListCellRenderer.
//      // We just reconfigure the JLabel each time we're called.
//      
//      StepThumbView _stepView = new StepThumbView();
//      
//      MyCellRenderer(){         
//         setLayout(new GridBagLayout());
//         setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
//         
//         _stepView.setPreferredSize(new Dimension(100,100));
//         
//         GridBagConstraints c = new GridBagConstraints();
//         c.anchor = GridBagConstraints.CENTER;
//         add(_stepView,c);
//         validate();
//      }
//
//      public Component getListCellRendererComponent(
//        JList list,              // the list
//        Object value,            // value to display
//        int index,               // cell index
//        boolean isSelected,      // is the cell selected
//        boolean cellHasFocus)    // does the cell have focus
//      {
//         
//         Step step = (Step) value;
//         _stepView.setStep(step);
//         setPreferredSize(new Dimension(150,150));
//         
//         if (isSelected){
//            setBorder(BorderFactory.createLineBorder(Color.red, 5));
//         }else{
//            setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
//         }
//         
//          return this;
//      }
//
// 
//  }

  // Story _story;
   StoryListView(){
      setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      setCellRenderer(new MyCellRenderer());
      setCellRenderer(new MyCellRenderer());
   }
   
//   void updateStory(){      
//      setListData(_story.getSteps().toArray());      
//   }
   
   
   //void setStory()
}
