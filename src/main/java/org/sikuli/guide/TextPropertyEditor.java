/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextPropertyEditor extends JPanel implements KeyListener {
   
   BufferedImage image;
   float scale;
   int w,h;
   
   private Text _text;
   JTextField _textField;
   
   public TextPropertyEditor()  {
       super();
       
       setName("TextPropertyEditor");
       
       _textField = new JTextField(20);       
       _textField.setSize(_textField.getPreferredSize());
       setSize(_textField.getPreferredSize());       
       
       add(_textField);
       
       // allow the text editor to move as the user moves the edited target
       //getTargetComponent().addFollower(this);
       
       _textField.addKeyListener(this);
       setFocusable(true);
       setOpaque(false);
       
       
       addFocusListener(new FocusListener(){

         @Override
         public void focusGained(FocusEvent arg0) {
         }

         @Override
         public void focusLost(FocusEvent arg0) {
            setVisible(false);            
         }
          
       });       
   }

   @Override
   public void keyPressed(KeyEvent k) {
//      System.out.println("[TextPropertyEditor] User pressed " + k.getKeyCode());

      
      if (k.getKeyCode() == KeyEvent.VK_ENTER){ 
         saveText();
         setVisible(false);     
                  
      }else if (k.getKeyCode() == KeyEvent.VK_ESCAPE){         
         //System.out.println("[TextPropertyEditor] User pressed ESCAPE");         
         setVisible(false);         
      }
   }
   

   
   @Override
   public void requestFocus(){
      _textField.requestFocus();
   }

   @Override
   public void keyReleased(KeyEvent arg0) {
   }

   @Override
   public void keyTyped(KeyEvent arg0) {
   }

   public void setTextSprite(Text textModel) {
      _text = textModel;      
      _textField.setText(_text.getText());
      _textField.setSize(_textField.getPreferredSize());
      
      Dimension size = _textField.getSize();
      size.width += 10;
      size.height += 10;
      setSize(size);
   }

   public Text getTextSprite() {
      return _text;
   }

   public void saveText() {
      String text = _textField.getText();
      if (text.isEmpty()){
         text = "";
      }
      
      if (_text != null){
         _text.setText(text);
      }
   }
      
   
}