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
   
   class StepCellRenderer extends JPanel implements ListCellRenderer {

      StepThumbView _stepView = new StepThumbView();
      JLabel _indexLabel = new JLabel();
      JPanel wrapper = new JPanel();   // paint border color for thumbview

      StepCellRenderer(){         
         setLayout(new MigLayout("insets 0"));
         setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

         _indexLabel.setOpaque(false);
         _indexLabel.setBackground(null);
         
         wrapper = new JPanel();
         wrapper.setLayout(new MigLayout("insets 2"));
         _stepView.setPreferredSize(new Dimension(100,100));
         _stepView.setOpaque(true);
         _stepView.setBackground(Color.white);
         wrapper.add(_stepView, "dock center");
         
         add(_indexLabel);
         add(wrapper, "dock center");
         
      }

      Color tileBorderColor = new Color(0.8f,0.8f,0.8f);
      
      
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
         
         // cf: http://www.w3schools.com/tags/ref_color_tryit.asp
         Color lightYellow = new Color(255,255,224);
         Color saddleBrown = new Color(139,69,19);
         Color gold = new Color(255,215,0);
         
         
         wrapper.setBackground(tileBorderColor);
         
         if (isSelected){

            setBackground(gold);
            setBorder(BorderFactory.createMatteBorder(3,10,3,10,gold));
   
            if (cellHasFocus){
               wrapper.setBackground(gold.darker().darker());
            }
               
         }else{
            setBackground(null);
            setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
         }
         
         

         return this;
         
      }

   }

   StoryListView(){
      setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      setCellRenderer(new StepCellRenderer());
      setDragEnabled(false);
   }


   public Story getStory() {
      return (Story) getSlideDeck();
   }
   
}
