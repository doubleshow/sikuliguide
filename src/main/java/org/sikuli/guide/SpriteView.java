package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

class SerialClone {
    public static <T> T clone(T x) {
   try {
       return cloneX(x);
   } catch (IOException e) {
       throw new IllegalArgumentException(e);
   } catch (ClassNotFoundException e) {
       throw new IllegalArgumentException(e);
   }
    }

    private static <T> T cloneX(T x) throws IOException, ClassNotFoundException {
   ByteArrayOutputStream bout = new ByteArrayOutputStream();
   CloneOutput cout = new CloneOutput(bout);
   cout.writeObject(x);
   byte[] bytes = bout.toByteArray();
   
   ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
   CloneInput cin = new CloneInput(bin, cout);

   @SuppressWarnings("unchecked")  // thanks to Bas de Bakker for the tip!
   T clone = (T) cin.readObject();
   return clone;
    }

    private static class CloneOutput extends ObjectOutputStream {
   Queue<Class<?>> classQueue = new LinkedList<Class<?>>();

   CloneOutput(OutputStream out) throws IOException {
       super(out);
   }

   @Override
   protected void annotateClass(Class<?> c) {
       classQueue.add(c);
   }

   @Override
   protected void annotateProxyClass(Class<?> c) {
       classQueue.add(c);
   }
    }

    private static class CloneInput extends ObjectInputStream {
   private final CloneOutput output;

   CloneInput(InputStream in, CloneOutput output) throws IOException {
       super(in);
       this.output = output;
   }

      @Override
   protected Class<?> resolveClass(ObjectStreamClass osc)
   throws IOException, ClassNotFoundException {
       Class<?> c = output.classQueue.poll();
       String expected = osc.getName();
       String found = (c == null) ? null : c.getName();
       if (!expected.equals(found)) {
      throw new InvalidClassException("Classes desynchronized: " +
         "found " + found + " when expecting " + expected);
       }
       return c;
   }

      @Override
      protected Class<?> resolveProxyClass(String[] interfaceNames)
   throws IOException, ClassNotFoundException {
          return output.classQueue.poll();
      }
    }
}
class SpriteTransferHandler extends TransferHandler{
      
   DataFlavor serialSpriteFlavor = new DataFlavor(Sprite.class, "Sprite");
   
   //Sprite spriteBeingTransferred;
   static Point insertLocation = new Point();
   static boolean alreadyCut = true;
   
   protected Transferable createTransferable(JComponent c) {
      System.out.println("SpriteTransfer: createTransferable");
      if (c instanceof SpriteView) {
         SpriteView source = (SpriteView) c;
         Sprite copy = SerialClone.clone(source.getSprite());  
         insertLocation.setLocation(copy.getX(), copy.getY());
         alreadyCut = false;
         return new SpriteTransferable(copy);
      }
      return null;
   }
   
   private boolean hasLocalArrayListFlavor(DataFlavor[] flavors) {
      if (serialSpriteFlavor == null) {
         return false;
      }

      for (int i = 0; i < flavors.length; i++) {
         if (flavors[i].equals(serialSpriteFlavor)) {
            return true;
         }
      }
      return false;
   }

   
   @Override
   public boolean canImport(TransferSupport support){
      System.out.println("SpriteTransfer: canImport");      
      DataFlavor[] flavors = support.getDataFlavors();
      if (hasLocalArrayListFlavor(flavors)) {
         return true;
      }
      return false;
   }
   
   @Override
   public boolean importData(TransferSupport support){
      System.out.println("SpriteTransfer: importData");      
      JComponent source = (JComponent) support.getComponent();
      Transferable transferable = support.getTransferable();
      System.out.println("Transferable: " + transferable);
      if (!canImport(support)){
         return false;
      }
      
      if (source instanceof SpriteView){
         SpriteView spriteView = (SpriteView) source; 
         try {
            Sprite sprite = null;
            if (hasLocalArrayListFlavor(support.getDataFlavors())){
               sprite = (Sprite) transferable.getTransferData(serialSpriteFlavor);
            }else{
               return false;
            }
            
            StepEditView stepView = (StepEditView) SwingUtilities.getAncestorOfClass(StepEditView.class, spriteView);
            if (stepView == null)
               return false;            
            Step step = stepView.getStep();
            
            // TODO: how come object is not serialized but still the same instance
            insertLocation.x += 10;
            insertLocation.y += 10;
            Sprite copy = SerialClone.clone(sprite);
            copy.setX(insertLocation.x);
            copy.setY(insertLocation.y);
            System.out.println("SpriteTransfer: addSprite");      
            //step.addSprite(copy);
            //stepView.paste(copy);
            
            stepView.spritePasted(copy);
            
            //stepView.selectSpriteView(spriteView);
            
         } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
      return true;
   }


   @Override
   protected void exportDone(JComponent source, Transferable data, int action) {
      
      if (source instanceof SpriteView){
         SpriteView spriteView = (SpriteView) source; 
         Sprite sprite = spriteView.getSprite();
         //spriteView.getParent();
         
         StepView stepView = (StepView) SwingUtilities.getAncestorOfClass(StepView.class, spriteView);
         if (stepView == null)
            return;
         
         if (action == MOVE && !alreadyCut){         
            Step step = stepView.getStep();         
            step.removeSprite(sprite);    
            alreadyCut = true;
            
            // purposely shifted so it would be added to the old location
            insertLocation.x -= 10;
            insertLocation.y -= 10;

         }
         
      }
      
      
      super.exportDone(source, data, action);
   }

   public int getSourceActions(JComponent c) {
      return COPY_OR_MOVE;
   }

   
   class SpriteTransferable implements Transferable {

      Sprite sprite;
      public SpriteTransferable(Sprite sprite) {
         this.sprite = sprite;
      }

      @Override
      public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
         if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
         }
         return sprite;
      }

      @Override
      public DataFlavor[] getTransferDataFlavors() {
         return new DataFlavor[] {serialSpriteFlavor}; 
      }

      @Override
      public boolean isDataFlavorSupported(DataFlavor flavor) {
         if (serialSpriteFlavor.equals(flavor)) {
            return true;
         }
         return false;
      }
      
   }
}

class SpriteView extends JPanel implements PropertyChangeListener {
      
   protected Sprite _sprite;
      
//   StepView getStep(){
//      return SwingUtilities.getAncestorOfClass(StepView.class, this);
//   }
   
   public SpriteView(Sprite sprite){
      _sprite = sprite;
      _sprite.addPropertyChangeListener(this);      
      updateBounds();
      updateStyle();
      updateName();
      setFocusable(true);
      //setDragEnabled(true);
      
      
      
      setTransferHandler(new SpriteTransferHandler());
      
      ActionMap map = getActionMap();
      map.put(TransferHandler.getCutAction().getValue(Action.NAME),
              TransferHandler.getCutAction());
      map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
              TransferHandler.getCopyAction());
      map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
              TransferHandler.getPasteAction());
      
      InputMap imap = this.getInputMap();
      imap.put(KeyStroke.getKeyStroke("meta X"),
          TransferHandler.getCutAction().getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("meta C"),
          TransferHandler.getCopyAction().getValue(Action.NAME));
      imap.put(KeyStroke.getKeyStroke("meta V"),
          TransferHandler.getPasteAction().getValue(Action.NAME));

//      
//      
//      addKeyListener(new KeyAdapter(){
//
//         @Override
//         public void keyPressed(KeyEvent k){
//            System.out.println("here too");
//
////            if (k.getKeyCode() == KeyEvent.VK_C){
////               TransferHandler.getCutAction().actionPerformed(new ActionEvent(SpriteView.this,1,""));
////
////            } else if (k.getKeyCode() == KeyEvent.VK_V){
////               TransferHandler.getPasteAction().actionPerformed(new ActionEvent(SpriteView.this,1,""));
////               
//////               TransferHandler handler = getTransferHandler();                
//////               handler.
////               
////            }
//         }
//         
//         
//      });
      
   }

   // Update the view based on the current attributes of the associated model
   void updateName(){
      setName(_sprite.getName());
   }
   
   protected void updateStyle(){
      setForeground(((StyledSprite) _sprite).getForeground());
      setBackground(((StyledSprite) _sprite).getBackground());
   }
   
   protected void updateBounds(){
      setLocation(_sprite.getX(), _sprite.getY());
      setSize(_sprite.getWidth(), _sprite.getHeight());
   }
   
   public void paint(Graphics g){
      
      // render the component in an offscreen buffer with shadow
      BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = image.createGraphics();      
      super.paint(g2);
      
      Graphics2D g2d = (Graphics2D) g;      
//      ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,_sprite.getOpacity()));
      g2d.drawImage(image,0,0,null,null);


//      if (_model.isSelected()){
//          Rectangle r = getBounds();
//          g2d.setColor(getForeground());
//          g2d.drawRect(0,0,r.width-1,r.height-1);
//      }
      
      // Debug draw
      
//      Rectangle r = getBounds();
//      g2d.setColor(Color.red);
//      g2d.drawRect(0,0,r.width-1,r.height-1);
      
   }

   public Sprite getModel() {
      return _sprite;
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      
      if (evt.getPropertyName().equals(Sprite.PROPERTY_X)){         
         updateBounds();
      } else if (evt.getPropertyName().equals(Sprite.PROPERTY_Y)){      
         updateBounds();
      } else if (evt.getPropertyName().equals(Sprite.PROPERTY_WIDTH)){
         updateBounds();
      } else if (evt.getPropertyName().equals(Sprite.PROPERTY_HEIGHT)){
         updateBounds();
      } else if (evt.getPropertyName().equals(StyledSprite.PROPERTY_BACKGROUND) ||
            evt.getPropertyName().equals(StyledSprite.PROPERTY_FOREGROUND)){
         updateStyle();   
      } else if (evt.getPropertyName().equals(StyledSprite.PROPERTY_OPACITY)){

      } else if (evt.getPropertyName().equals(Sprite.PROPERTY_NAME)){
         updateName();
      } else {  
         return;
      }
      
   }

   public Sprite getSprite() {
      return _sprite;
   }
}


class ViewFactory {
   
   public static ContextImageView createView(ContextImage contextImage){
      return new ContextImageView(contextImage);
   }
   
   public static SpriteView createView(Sprite sprite){
      if (sprite instanceof FlagText)
         return new FlagTextView((FlagText)sprite);
      else if (sprite instanceof Target)
         return new TargetView((Target)sprite);
      else if (sprite instanceof Text)
         return new TextView((Text)sprite);
      else if (sprite instanceof Circle)
         return new CircleView((Circle)sprite);

      return new SpriteView(sprite);
   }      
}