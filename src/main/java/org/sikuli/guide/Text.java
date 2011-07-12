package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

interface Text extends Sprite {
   
   public void setText(String text);
   public String getText();
   
   public final int padding = 2;    
   static public final String PROPERTY_TEXT = "text";
}

@Root
class DefaultText 
   extends DefaultSprite implements Text{
   
   @Element
   private String text;
   
   private int maximumWidth = Integer.MAX_VALUE;
   private int fontSize = 12;

   public DefaultText(String text) {
      super();
      setText(text);
      setBackground(Color.yellow);
   }

   public DefaultText() {
      super();
      setBackground(Color.yellow);
   }

   
   @Override   
   public void setText(String text) {
      this.pcs.firePropertyChange(PROPERTY_TEXT, this.text, this.text = text);
   }

   @Override
   public String getText() {
      return text;
   }
   
   public void setMaximumWidth(int maximumWidth) {
      this.maximumWidth = maximumWidth;
   }

   public int getMaximumWidth() {
      return maximumWidth;
   }

   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
   }

   public int getFontSize() {
      return fontSize;
   }

}

class TextView extends SpriteView {
   JPanel panel;
   JLabel label;
   
   Text _textSprite;
   
   public TextView(Text model){
      super(model);   
      _textSprite = model;
      updateText();
   }
   
   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      super.propertyChange(evt);
      updateText();
   }
   
   //@Override
   protected void updateText(){
      if (label == null){         
         panel = new JPanel();
         label = new JLabel();         
         panel.setLayout(null);
         panel.add(label);
         add(panel);
      }

      //TextSprite _textSprite;
      label.setText(_textSprite.getText());
      label.setSize(label.getPreferredSize());
      label.setLocation(_textSprite.padding,_textSprite.padding);      
      panel.setOpaque(true);
      panel.setBackground(_textSprite.getBackground());

      setLayout(null);
      
      Dimension size = label.getPreferredSize();
      size.width = size.width + 2*_textSprite.padding;
      size.height = size.height + 2*_textSprite.padding;
      panel.setSize(size);
      //setActualSize(size);
      
      setSize(size);
      
      _sprite.setWidth(size.width);
      _sprite.setHeight(size.height);
      
      updateBounds();
      updateStyle();
   }
   
}