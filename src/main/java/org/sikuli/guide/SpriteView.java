package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
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


@Root
class SpriteArrayList {
   @ElementList
   List<Sprite> elements = new ArrayList<Sprite>();   

   String toXML(){
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);
      Writer writer = new StringWriter();
      try {
         serializer.write(this, writer);
         return writer.toString(); 
      } catch (Exception e) {
         return "";
      }
   }

}


class SpriteTransferHandler extends TransferHandler{
      
   DataFlavor serialSpriteFlavor = new DataFlavor(Sprite.class, "Sprite");
   DataFlavor serialArrayListFlavor = new DataFlavor(SpriteArrayList.class, "SpriteArrayList");
   DataFlavor serializedXMLStringFlavor = new DataFlavor(SpriteArrayList.class, "serializedXMLStringFlavor");  
   
   //Sprite spriteBeingTransferred;
   static Point insertOffset = new Point();
   static boolean alreadyCut = true;
   
   protected Transferable createTransferable(JComponent c) {
      System.out.println("SpriteTransfer: createTransferable");
      if (c instanceof SpriteView) {
         System.out.println("SpriteView"); 
         
         SpriteView source = (SpriteView) c;
         Sprite copy = SerialClone.clone(source.getSprite());  
         insertOffset.setLocation(0,0);
         alreadyCut = false;
         return new SpriteTransferable(copy);
         
      }else if (c instanceof StepEditView){
         System.out.println("StepEditView");
         
         StepEditView editView = (StepEditView) c;
         SpriteView source = editView.selectionTool.getSelectedSpriteView();
         Sprite copy = SerialClone.clone(source.getSprite());  
         insertOffset.setLocation(0,0);//copy.getX(), copy.getY());
         alreadyCut = false;
         
         
         List<Sprite> sprites = editView.selectionTool.getSelectedSprites();
         
//         if (sprites.size() == 1){
//            return new SpriteTransferable(copy);            
//         }else{
            return new ArrayListTransferable<Sprite>(sprites);            
//         }
      }
      return null;
   }
   
   
   private boolean hasSerialSpriteFlavor(DataFlavor[] flavors) {
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
   
   private boolean hasStringFlavor(DataFlavor[] flavors) {
      for (int i = 0; i < flavors.length; i++) {
         if (flavors[i].equals(DataFlavor.stringFlavor)) {
            return true;
         }
      }
      return false;
   }


   
   @Override
   public boolean canImport(TransferSupport support){
      System.out.println("SpriteTransfer: canImport");      
      DataFlavor[] flavors = support.getDataFlavors();
      if (hasSerialSpriteFlavor(flavors)) {
         return true;
      }
      if (hasSerialArrayListFlavor(flavors)) {
         return true;
      }
      if (hasStringFlavor(flavors)){
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
      
      if (source instanceof StepEditView){
         StepEditView stepView = (StepEditView) source;

         try {
            List<Sprite> sprites;
            if (hasSerialSpriteFlavor(support.getDataFlavors())){
               sprites = new ArrayList<Sprite>();
               Sprite sprite = (Sprite) transferable.getTransferData(serialSpriteFlavor);
               sprites.add(sprite);
            }else if (hasSerialArrayListFlavor(support.getDataFlavors())){
               sprites = (ArrayList<Sprite>) transferable.getTransferData(serialArrayListFlavor);
            }else if (hasStringFlavor(support.getDataFlavors())){
               sprites = new ArrayList<Sprite>();               
               String string = (String) transferable.getTransferData(DataFlavor.stringFlavor);
               Strategy strategy = new CycleStrategy("id","ref");
               Serializer serializer = new Persister(strategy);
               System.out.println("Try to import a sprite from this string: " + string);
               try {
                  ArrayListTransferable t = serializer.read(ArrayListTransferable.class, string);
                  sprites = (List<Sprite>) t.data;
                  System.out.println("Sprite imported from clipboard as string");
                  //sprites.add(sprite);
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }else{
               return false;
            }
           
            insertOffset.x += 10;
            insertOffset.y += 10;         
            List<Sprite> spritesToPaste = new ArrayList<Sprite>();
            for (Sprite sprite : sprites){
                        
               // TODO: how come object is not serialized but still the same instance
               Sprite copy = SerialClone.clone(sprite);
               copy.setX(sprite.getX() + insertOffset.x);
               copy.setY(sprite.getY() + insertOffset.y);
               spritesToPaste.add(copy);
            }
            
            stepView.spritesPasted(spritesToPaste);
            
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
      
      if (source instanceof StepEditView){
         StepEditView stepView = (StepEditView) source;
         Step step = stepView.getStep();
         
         if (action == MOVE && !alreadyCut){
         
            for (SpriteView spriteView : stepView.selectionTool.getSelectedSpriteViews()){
               Sprite sprite = spriteView.getSprite();
               stepView.spriteCut(sprite);
            }

            alreadyCut = true;
            // purposely shifted so it would be added to the old location
            insertOffset.x -= 10;
            insertOffset.y -= 10;
         }
         
      }
      
      
      super.exportDone(source, data, action);
   }

   public int getSourceActions(JComponent c) {
      return COPY_OR_MOVE;
   }
   
   @Root
   static public class ArrayListTransferable<T> implements Transferable {
      @ElementList
      List<T> data;

      
      public ArrayListTransferable(){         
      }
      
      public ArrayListTransferable(List<T> alist) {
         data = alist;
      }

      public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException {
         if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
         }
         
         if (flavor.equals(serialArrayListFlavor)){
            return data;         
         }else if (flavor.equals(DataFlavor.stringFlavor)){
            // TODO: very messy, needs refactoring
            Strategy strategy = new CycleStrategy("id","ref");
            Serializer serializer = new Persister(strategy);
            Writer writer = new StringWriter();
            try {
               serializer.write(this, writer);
               return writer.toString();
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }

//            SpriteArrayList al = new SpriteArrayList();
//            al.elements = (List<Sprite>) data;
//            al.toXML();
//            return al.toXML();
         }
         return data;
      }
     
      DataFlavor serialArrayListFlavor = new DataFlavor(SpriteArrayList.class, "SpriteArrayList");


      public DataFlavor[] getTransferDataFlavors() {
         return new DataFlavor[] { DataFlavor.stringFlavor, 
               serialArrayListFlavor };
      }

      public boolean isDataFlavorSupported(DataFlavor flavor) {
         if (serialArrayListFlavor.equals(flavor)) {
            return true;
         }else if (flavor.equals(DataFlavor.stringFlavor)){
            return true;
         }
         return false;
      }
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
         
         if (flavor.equals(serialSpriteFlavor)){
            return sprite;
         }else if (flavor.equals(serializedXMLStringFlavor)){
            return sprite.toXML();
         }else if (flavor.equals(DataFlavor.stringFlavor)){
            return sprite.toXML();
         }
         return sprite;
      }

      @Override
      public DataFlavor[] getTransferDataFlavors() {
         return new DataFlavor[] {serialSpriteFlavor,serializedXMLStringFlavor,DataFlavor.stringFlavor}; 
      }

      @Override
      public boolean isDataFlavorSupported(DataFlavor flavor) {
         DataFlavor[] flavors = getTransferDataFlavors();
         for (DataFlavor supportedFlavor : flavors){
            if (supportedFlavor.equals(flavor)) {
               return true;
            }
         }         
         return false;
      }
      
   }
}



class SpriteView extends JPanel implements PropertyChangeListener {
      
   protected Sprite _sprite;
      
   private float opacity = 1.0f;
   

   
   public SpriteView(Sprite sprite){
      _sprite = sprite;
      _sprite.addPropertyChangeListener(this);      
      updateBounds();
      updateStyle();
      updateName();
      
      setOpaque(false); // need this to get fading out work right for all views
      
      //setFocusable(true);
      //setDragEnabled(true);
      
//      setTransferHandler(new SpriteTransferHandler());
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
      
      Dimension minSize = getMinimumSize();
      setSize(Math.max(minSize.width, _sprite.getWidth()),
            Math.max(minSize.height, _sprite.getHeight()));
   }

   
   //BufferedImage spriteImage; <-- don't do this because this blows up the heap   
   public void paint(Graphics g){
      
      // render the component in an offscreen buffer (TODO: with shadow)
      
      BufferedImage spriteImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);      
      Graphics2D g2 = spriteImage.createGraphics();     
      g2.setRenderingHints(((Graphics2D) g).getRenderingHints());
      g2.setClip(g.getClip());
      super.paint(g2);
      
      // so that we can paste it with translucent effects
      Graphics2D g2d = (Graphics2D) g;      
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,getOpacity()));
      g2d.drawImage(spriteImage,0,0,null,null);


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

   public void setOpacity(float opacity) {
      this.opacity = opacity;
   }

   public float getOpacity() {
      return opacity;
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
      else if (sprite instanceof ContextImage)
         return new ContextImageView((ContextImage)sprite);

      return new SpriteView(sprite);
   }      
}