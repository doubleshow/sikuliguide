package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class StoryListView extends JList {
   
   class MyCellRenderer extends JPanel implements ListCellRenderer {
//      final static ImageIcon longIcon = new ImageIcon("long.gif");
//      final static ImageIcon shortIcon = new ImageIcon("short.gif");

      // This is the only method defined by ListCellRenderer.
      // We just reconfigure the JLabel each time we're called.
      
      StepThumbView _stepView = new StepThumbView();
      
      MyCellRenderer(){         
         setLayout(new GridBagLayout());
         setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         
         _stepView.setPreferredSize(new Dimension(100,100));
         
         GridBagConstraints c = new GridBagConstraints();
         c.anchor = GridBagConstraints.CENTER;
         add(_stepView,c);
         validate();
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
         setPreferredSize(new Dimension(150,150));
         
         if (isSelected){
            setBorder(BorderFactory.createLineBorder(Color.red, 5));
         }else{
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         }
         
          return this;
      }

 
  }

   
   Story _story;
   StoryListView(Story story){
      _story = story;
      updateStory();
      
      setCellRenderer(new MyCellRenderer());
   }
   
   void updateStory(){      
      setListData(_story.getSteps().toArray());      
   }
   
   
   //void setStory()
}
