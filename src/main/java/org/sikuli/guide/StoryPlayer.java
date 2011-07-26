package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.sikuli.cv.FindResult;

import com.sun.awt.AWTUtilities;


interface StepPlayerListener {      
   void stepCompleted(Step step);
   void stepFailed(Step step);
}


class SpriteAnimator {
   static Animator moveTo(Sprite sprite, Point p){
      Animator anim;
      anim = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).build();
      anim.addTarget(PropertySetter
            .getTargetTo(sprite, "x", new AccelerationInterpolator(0.5, 0.5), p.x));
      anim.addTarget(PropertySetter
            .getTargetTo(sprite, "y", new AccelerationInterpolator(0.5, 0.5), p.y));
      anim.start();
      return anim;
   }
}

class SpriteGroupViewAnimator {   
   
   private static final SwingTimerTimingSource f_animationTimer = new SwingTimerTimingSource();
   static {
      AnimatorBuilder.setDefaultTimingSource(f_animationTimer);
      f_animationTimer.init();
   }
   
   static Animator fadeIn(SpriteViewGroup g){
      Animator anim;
      anim = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).build();
      anim.addTarget(PropertySetter
            .getTargetTo(g, "opacity", new AccelerationInterpolator(0.5, 0.5), 1.0f));
      anim.start();
      return anim;
   }
   
   static Animator fadeOut(SpriteViewGroup g){
      Animator anim;
      anim = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).build();
      anim.addTarget(PropertySetter
            .getTargetTo(g, "opacity", new AccelerationInterpolator(0.5, 0.5), 0.0f));
      anim.start();
      return anim;
   }   
}

public class StoryPlayer extends JWindow {
   
   
   Step _stepModel;
   final Rectangle _regionModel = new Rectangle();
   
   
   ScreenGrabber _screenGrabber;
   //ScreenRegionSelector _screenRegionSelector;
   
   
   private void initAppearance(){
      setLayout(null);      
      // It turns out these are useful after all
      // so that it works on Windows
      setBackground(null);
      ((JPanel)getContentPane()).setBackground(null);
      AWTUtilities.setWindowOpaque(this, false);
      setBounds(0,0,800,600);
   }
   
   StoryPlayer() throws AWTException{      
      
      initAppearance();
      
      _screenGrabber = new DesktopScreenGrabber();

//      _screenRegionSelector = new ScreenRegionSelector(getBounds());
//      _screenRegionSelector.addListener(new ScreenRegionSelectorListener(){
//
//         @Override
//         public void selectionChanged(Rectangle region) {
//            setBounds(region);       
//            _regionModel.setBounds(region);
//         }
//         
//      });
      
      _regionModel.setBounds(getBounds());

   }
   
   void stop(){
//      for (SklTracker tracker : _trackers){
//         tracker.stopTracking();
//      }
      stopFailureTimer();

   //  remove existing objs (of the previous step) from the content pane
      getContentPane().removeAll();
      repaint();
   }
   
   @Override
   public void setVisible(boolean visible){
      
//      _screenRegionSelector.setVisible(visible);
//      _screenRegionSelector.setAlwaysOnTop(visible);

      super.setVisible(visible);
      //_screenRegionSelector.toBack();
      
   }
   
   
   class ViewHelper {
   
      Map<Sprite,SpriteView> spriteToSpriteView = new HashMap<Sprite,SpriteView>();
      
      Container contentPane = getContentPane();
      
      Step step;
      private void setStep(Step step){
         this.step = step;
         clear();
         
         // create view for each sprite
         for (Sprite sprite : step.getSprites()){
            
            // except for ContextImage
            if (sprite instanceof ContextImage)
               continue;
            
            add(sprite);                        
         }
               
         for (Relationship r : step.getRelationships()){
            r.update(r.getDependent());
         }
      }
      
      private void clear(){
         contentPane.removeAll();
      }
      
      private void add(Sprite sprite){
         SpriteView view = ViewFactory.createView(sprite);         
         contentPane.add(view);            
         spriteToSpriteView.put(sprite, view);
      }
      
      private void setDependentsVisible(Sprite sprite, boolean visible){                   
         for (Relationship rel : step.getRelationships(sprite)){
            if (sprite == rel.getParent()){
               Sprite dependent = rel.getDependent();
               SpriteView dependentView = spriteToSpriteView.get(dependent);
               dependentView.setVisible(visible);
            }
         }
      }
      
      private SpriteView getView(Sprite sprite){
         return spriteToSpriteView.get(sprite);
      }
      
      private List<SpriteView> getDependentViews(Sprite sprite){
         List<SpriteView> views = new ArrayList<SpriteView>();
         for (Relationship rel : step.getRelationships()){
            if (sprite == rel.getParent()){
               Sprite dependent = rel.getDependent();
               SpriteView dependentView = spriteToSpriteView.get(dependent);
               views.add(dependentView);
            }
         }
         return views;
      }
      
      private void setVisible(Sprite sprite, boolean visible){
         setVisible(sprite, visible, false);
      }
            
      private void setVisible(Sprite sprite, boolean visible, boolean includingDependents){
         spriteToSpriteView.get(sprite).setVisible(visible);
         if (includingDependents)
            setDependentsVisible(sprite, visible);
      }
      
      private SpriteViewGroup getSpriteViewGroupForDependents(Sprite parent){
         SpriteViewGroup g = new SpriteViewGroup();         
         //g.views.add(viewHelper.getView(parent));
         g.views.addAll(viewHelper.getDependentViews(parent)); 
         return g;
      }
   
   }
   ViewHelper viewHelper = new ViewHelper();
   
   private static final SwingTimerTimingSource f_animationTimer = new SwingTimerTimingSource();
   static {
      AnimatorBuilder.setDefaultTimingSource(f_animationTimer);
      f_animationTimer.init();
   }

   // an instance per target
   class SingleTargetTrackerListener implements TrackerListener { 

      SpriteViewGroup group = null;
      SingleTargetTrackerListener(Target target){
         group = viewHelper.getSpriteViewGroupForDependents(target);
      }
      
      @Override
      public void targetFoundFirstTime(Target target, FindResult match) {

         target.setX(match.x);
         target.setY(match.y);
         
         group.setOpacity(0f);
         group.setVisible(true);         
         SpriteGroupViewAnimator.fadeIn(group);
      }

      @Override
      public void targetFoundAgain(Target target, FindResult match) {
         group.setVisible(true);
         SpriteGroupViewAnimator.fadeIn(group);
         SpriteAnimator.moveTo(target, match.getLocation());
      }

      @Override
      public void targetNotFound(Target target) {
         SpriteGroupViewAnimator.fadeOut(group);
      }
      
   };
   
   void play(Step step){
      
//      _screenRegionSelector.setVisible(true);
//      _screenRegionSelector.setAlwaysOnTop(true);

      
      // make a copy of the step model
      //Step stepModelCopy = null;
//      try {
//       //  stepModelCopy = (Step) stepModel.clone();
//      } catch (CloneNotSupportedException e1) {
//         return;
//      }
      

      viewHelper.setStep(step);

      for (Target target : step.getTargets()){ 
         
         // hide all dependents initially         
         viewHelper.setVisible(target, false, true);

         // set the tracker
         Tracker tracker = new Tracker(target);
         tracker.setScreenGrabber(_screenGrabber);
         tracker.start();         
         tracker.addTrackerListener(new SingleTargetTrackerListener(target));
         
      }
      
      setVisible(true);
      toFront();
      setAlwaysOnTop(true);
      repaint();

//      _trackers.clear();
//      numPatternsToVerify = 0;
//      numPatternsVerified = 0;
//      for (Sprite model : stepModelCopy.getModels()){
//         if (model instanceof SklAnchorModel){
//            
//            SklAnchorModel anchor = (SklAnchorModel) model;
//
//            // set up the pattern image
//            BufferedImage image = stepModelCopy.getReferenceImageModel().getImage();
//            BufferedImage patternImage = image.getSubimage(anchor.getX(), anchor.getY(), anchor.getWidth(), anchor.getHeight());            
//            Pattern pattern = new Pattern(patternImage);
//
//            SklTracker tracker = new SklTracker(pattern);
//            _trackers.add(tracker);
//            tracker.setRegionModel(_regionModel);
//            tracker.start();
//            
//            if (anchor.getCommand() == SklAnchorModel.ASSERT_COMMAND){
//               
//               Verifier c = new Verifier(anchor);
//               anchor.setOpacity(0.0f);
//               tracker._listener = c;
//               numPatternsToVerify += 1;
//               
//            } else if (anchor.getCommand() == SklAnchorModel.CLICK_COMMAND){
//               Debug.info("click");
//               Clicker c = new Clicker(anchor);
//               tracker._listener = c;
//               
//            } else if (anchor.getCommand() == SklAnchorModel.TYPE_COMMAND){
//               Debug.info("type");
//               Typer c = new Typer(anchor, (String) anchor.getArgument());
//               tracker._listener = c;
//            }
//
//         }
//      }
      
      // if there's some active anchor (being tracked)
//      if (_trackers.size() > 0){
//         // starts timer
//         startFailureTimer();
//      }else{         
//         stop();
//         fireStepCompleted();
//      }
   }


   
   Timer failureTimer;
   void startFailureTimer(){
      failureTimer = new Timer(10000, new ActionListener(){

         @Override
         public void actionPerformed(ActionEvent arg0) {
            stop();
            fireStepFailed();
         }         
      });
      failureTimer.setRepeats(false);
      failureTimer.start();

   }
   
   void stopFailureTimer(){
      if (failureTimer != null)
         failureTimer.stop();
   }

   
   ArrayList<StepPlayerListener> listeners = new ArrayList<StepPlayerListener>();
   void addListener(StepPlayerListener listener){
      listeners.add(listener);
   }
   
   void allPatternsVerified(){
      stopFailureTimer();
      fireStepCompletedLater(2);
   }
   
   
   void fireStepCompletedLater(float secs){
      Timer t = new Timer((int) (secs*1000), new ActionListener(){

         @Override
         public void actionPerformed(ActionEvent e) {
            fireStepCompleted();            
         }
         
      });
      t.setRepeats(false);
      t.start();
   }
   
   void fireStepCompleted(){
      for (StepPlayerListener listener : listeners){
         listener.stepCompleted(_stepModel);
      }
   }
   
   void fireStepFailed(){
      for (StepPlayerListener listener : listeners){
         listener.stepFailed(_stepModel);
      }
   }

   
   @Override
   public void paint(Graphics g){
      super.paint(g);
      Graphics2D g2d = (Graphics2D) g;
      Rectangle r = getBounds();
      g2d.setColor(Color.green);
      g2d.setStroke(new BasicStroke(3f));
      g2d.drawRect(2,2,r.width-4,r.height-4);
   }
   
   int numPatternsToVerify = 0;
   int numPatternsVerified = 0;
   void patternVerified(){
      numPatternsVerified += 1;
      //Debug.info("[StepPlayer] " + numPatternsVerified + " of " + numPatternsToVerify + " patterns verified");
      
      if (numPatternsVerified == numPatternsToVerify)
         allPatternsVerified();
   }
   
//   class Verifier implements SklTrackerListener {
//      
//      private SklAnchorModel _anchor;
//      
//      Verifier(SklAnchorModel anchor){
//         _anchor = anchor;
//      }
//
//
//      @Override
//      public void patternFoundFirstTime(Object source, Region match) {
//         
//         // translate to window location
//         Point loc = new Point();
//         Point o = getLocation();
//         loc.x = match.x - o.x;
//         loc.y = match.y - o.y;
//         
//         _anchor.setLocation(loc.x, loc.y);
//         _anchor.setSize(match.w, match.h);
//         
//         SklAnimation anim = SklAnimationFactory.createFadeinAnimation(_anchor);
//         anim.setListener(new SklAnimationListener(){
//
//            @Override
//            public void animationCompleted() {
//               patternVerified();
//            }
//            
//         });
//         anim.start();
//         
//         ((SklTracker) source).stopTracking();         
//      }
//
//      @Override
//      public void patternFoundAgain(Object source, Region match) {
//         
//      }
//
//      @Override
//      public void patternNotFound(Object source) {
//         
//      }
//      
//      
//      
//      
//   }
//   
//   class Clicker implements SklTrackerListener {
//      
//      private SklAnchorModel _anchor;
//     
//      Clicker(SklAnchorModel anchor){
//         _anchor = anchor;
//      }
//
//      @Override
//      public void patternFoundAgain(Object source, Region match) {
//      }
//
//      @Override
//      public void patternFoundFirstTime(Object source, Region match) {
//         
//         // translate to window location
//         Point loc = new Point();
//         Point o = getLocation();
//         loc.x = match.x - o.x;
//         loc.y = match.y - o.y;
//         
//         _anchor.setLocation(loc.x, loc.y);
//         _anchor.setSize(match.w, match.h);
//         
//         
//         stopFailureTimer();
//         
//         SklAnimation anim = SklAnimationFactory.createFadeinAnimation(_anchor);
//         anim.setListener(new SklAnimationListener(){
//
//            @Override
//            public void animationCompleted() {
//               fireStepCompletedLater(1);
//            }
//            
//         });
//         anim.start();
//         
//
//         try {
//            (new Screen()).click(match, 0);
//         } catch (FindFailed e) {
//         }
//
//         ((SklTracker) source).stopTracking();
//      }
//
//      @Override
//      public void patternNotFound(Object source) {
//         SklAnimationFactory.createFadeoutAnimation(_anchor).start();
//      }
//   }
//   
//   class Typer implements SklTrackerListener {
//      
//      private SklAnchorModel _anchor;
//      private String _stringToType;
//     
//      Typer(SklAnchorModel anchor, String toType){
//         _anchor = anchor;
//         _stringToType = toType;
//      }
//      
//
//      @Override
//      public void patternFoundAgain(Object source, Region match) {
//      }
//
//      @Override
//      public void patternFoundFirstTime(Object source, Region match) {
//         
//         // translate to window location
//         Point loc = new Point();
//         Point o = getLocation();
//         loc.x = match.x - o.x;
//         loc.y = match.y - o.y;
//         
//         _anchor.setLocation(loc.x, loc.y);
//         _anchor.setSize(match.w, match.h);
//         
//         
//         stopFailureTimer();
//         
//         SklAnimation anim = SklAnimationFactory.createFadeinAnimation(_anchor);
//         anim.setListener(new SklAnimationListener(){
//
//            @Override
//            public void animationCompleted() {
//               fireStepCompletedLater(1);
//            }
//            
//         });
//         anim.start();
//         
//         try {
//            //(new Screen()).paste(match, _stringToType);
//            (new Screen()).type(match, _stringToType, 0);
//         } catch (FindFailed e) {
//         }
//
//         ((SklTracker) source).stopTracking();
//      }
//
//      @Override
//      public void patternNotFound(Object source) {
//         SklAnimationFactory.createFadeoutAnimation(_anchor).start();
//      }
//   }
   
   void setStep(Step stepModel){
      _stepModel = stepModel;
   }

}
