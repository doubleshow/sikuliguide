package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.sikuli.cv.FindResult;

import com.sun.awt.AWTUtilities;


interface Transition {
   void waitFor();
   void stop();
   void addTransitionListener(TransitionListener listener);
   void removeTransitionListener(TransitionListener listener);
}

class TransitionEvent {
   
   public TransitionEvent(Transition transition){
      this.transition = transition;
   }
   private Transition transition;

   public void setTransition(Transition transition) {
      this.transition = transition;
   }

   public Transition getTransition() {
      return transition;
   }
}

interface TransitionListener extends EventListener {   
   void transitionOccurred(TransitionEvent event);
}

interface TransitionGroup extends Transition{
   final static String OR = "or";
   final static String AND = "and";

   String getLogic();
   void add(Transition transition);
}

class DefaultTransitionGroup extends AbstractTransition implements TransitionGroup{

   List<Transition> _transitions = new ArrayList<Transition>();
   
   String _logic;
   DefaultTransitionGroup(String logic){
      this._logic = logic;
   }
   
   TransitionListener _individualTransitionListener = new TransitionListener(){

      @Override
      public void transitionOccurred(TransitionEvent event) {
         if (getLogic().equals(TransitionGroup.OR)){            
            stop();
            fireTransitionOccurred(new TransitionEvent(DefaultTransitionGroup.this));

         }else if (getLogic().equals(TransitionGroup.AND)){
            
            pendingCount--;
            if (pendingCount == 0){
               stop();
               fireTransitionOccurred(new TransitionEvent(DefaultTransitionGroup.this));
            }
         }
      }
      
   };

   private int pendingCount;
   
   @Override
   public void waitFor() {
      pendingCount = _transitions.size();
      for (Transition t : _transitions){
         t.addTransitionListener(_individualTransitionListener);
         t.waitFor();
      }       
   }

   @Override
   public void stop() {
      for (Transition t : _transitions){
         t.removeTransitionListener(_individualTransitionListener);
         t.stop();
      }      
   }

   @Override
   public String getLogic() {
      return _logic;
   }

   @Override
   public void add(Transition transition) {
      _transitions.add(transition);
   }
   
}

class ButtonTransition extends AbstractTransition{
   
   class Control extends JFrame{
      
      Control(){
         setLayout(new MigLayout());         
         JButton btn = new JButton("Next");
         btn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               stop();
               fireTransitionOccurred(new TransitionEvent(ButtonTransition.this));               
            }
            
         });
         add(btn,"dock center");
      }
   }
   Control _window = new Control();

   @Override
   public void waitFor() {
      _window.setVisible(true);
      _window.setAlwaysOnTop(true);    
      _window.pack();
   }

   @Override
   public void stop() {
      _window.setVisible(false);
      _window.dispose();      
   }
   
}

class TrackerTransition extends AbstractTransition{
   
   Tracker _tracker;
   TrackerTransition(Tracker tracker){
      _tracker = tracker;
   }
   
   private void fireTransitionTriggeredLater(int milliseconds){
      Timer timer = new Timer(milliseconds, new ActionListener(){

         @Override
         public void actionPerformed(ActionEvent e) {
            stop();
            fireTransitionOccurred(new TransitionEvent(TrackerTransition.this));
         }
         
      });      
      timer.setRepeats(false);
      timer.start();
   }
   TrackerListener _trackerListener = new TrackerListener(){
      
      @Override
      public void targetFoundFirstTime(TrackerEvent event) {
         event.getTracker().removeTrackerListener(this);
         fireTransitionTriggeredLater(5000);
      }

      @Override
      public void targetFoundAgain(TrackerEvent event) {         
      }

      @Override
      public void targetNotFound(TrackerEvent event) {
      }
   };

   @Override
   public void waitFor() {
      if (_tracker.isTargetFound())
         fireTransitionTriggeredLater(5000);
      else
         _tracker.addTrackerListener(_trackerListener);      
   }

   @Override
   public void stop() {
      _tracker.removeTrackerListener(_trackerListener);
   }
   
}

class TimeoutTransition extends AbstractTransition {

   Timer timer;
   final static int DEFAULT_TIMEOUT = 5000;
   
   TimeoutTransition(){
      this(DEFAULT_TIMEOUT);
   }
   
   TimeoutTransition(int milliseconds){
      timer = new Timer(milliseconds, new ActionListener(){
         @Override
         public void actionPerformed(ActionEvent e) {
            stop();
            fireTransitionOccurred(new TransitionEvent(TimeoutTransition.this));           
         }
         
      });
   }
   
   
   @Override
   public void waitFor() {
      timer.start();
   }

   @Override
   public void stop() {
      timer.stop();      
   }
   
}

abstract class AbstractTransition implements Transition {
   
   protected void fireTransitionOccurred(TransitionEvent event){
      System.out.println("[Transition Triggered] " + event.getTransition());
      Object[] listeners = listenerList.getListenerList();
      for (int i=0; i<listeners.length; i+=2) {
         if (listeners[i]==TransitionListener.class) {
            ((TransitionListener)listeners[i+1]).transitionOccurred(event);
         }
      }
   }
   
   transient private EventListenerList listenerList = new EventListenerList();   
   @Override
   public void addTransitionListener(TransitionListener l) {
      listenerList.add(TransitionListener.class, l);
   }
   
   @Override
   public void removeTransitionListener(TransitionListener l) {
      listenerList.remove(TransitionListener.class, l);
   }

}


public class DefaultStoryPlayer extends AbstractStoryPlayer  
implements StoryPlayer {
      
   OverlayWindow _window = new OverlayWindow();
   ViewHelper _viewHelper = new ViewHelper();

   Step _stepModel;
   final Rectangle _regionModel = new Rectangle();
   
   
   ScreenGrabber _screenGrabber;
   //ScreenRegionSelector _screenRegionSelector;
   
   
   class OverlayWindow extends JWindow {
   
      OverlayWindow(){
         initAppearance();
      }
      
      void start(){
         setVisible(true);
         toFront();
         setAlwaysOnTop(true);
         repaint();
      }

      
      private void initAppearance(){
         setLayout(null);      
         // It turns out these are useful after all
         // so that it works on Windows
         setBackground(null);
         ((JPanel)getContentPane()).setBackground(null);
         AWTUtilities.setWindowOpaque(this, false);
         setBounds(0,0,800,600);
      }
      
      @Override
      public void setVisible(boolean visible){
         
//         _screenRegionSelector.setVisible(visible);
//         _screenRegionSelector.setAlwaysOnTop(visible);

         super.setVisible(visible);
         //_screenRegionSelector.toBack();         
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


   }
   
   public DefaultStoryPlayer() throws AWTException{      
      
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
      
      //_regionModel.setBounds(getBounds());

   }
   
   @Override
   public void stop(){
      System.out.println("stopping");
//      for (SklTracker tracker : _trackers){
//         tracker.stopTracking();
//      }
      stopFailureTimer();

   //  remove existing objs (of the previous step) from the content pane
      //getContentPane().removeAll();
      //repaint();
      
      _viewHelper.clear();
      for (Tracker tracker : _trackers){
         tracker.stopTracking();
      }
      _window.setVisible(false);
   }
   
   
   
   class ViewHelper {
   
      Map<Sprite,SpriteView> spriteToSpriteView = new HashMap<Sprite,SpriteView>();
      
      Container contentPane = _window.getContentPane();
      
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
         g.views.addAll(_viewHelper.getDependentViews(parent)); 
         return g;
      }
   
   }
   
   private static final SwingTimerTimingSource f_animationTimer = new SwingTimerTimingSource();
   static {
      AnimatorBuilder.setDefaultTimingSource(f_animationTimer);
      f_animationTimer.init();
   }

   // an instance per target
   class SingleTargetTrackerListener implements TrackerListener { 

      SpriteViewGroup group = null;
      SingleTargetTrackerListener(Target target){
         group = _viewHelper.getSpriteViewGroupForDependents(target);
      }
      
      @Override
      public void targetFoundFirstTime(TrackerEvent event){
         Target target = event.getTarget();
         FindResult match = event.getFindResult();

         target.setX(match.x);
         target.setY(match.y);
         
         group.setOpacity(0f);
         group.setVisible(true);         
         SpriteGroupViewAnimator.fadeIn(group);
      }

      @Override
      public void targetFoundAgain(TrackerEvent event){
         Target target = event.getTarget();
         FindResult match = event.getFindResult();

         group.setVisible(true);
         SpriteGroupViewAnimator.fadeIn(group);
         SpriteAnimator.moveTo(target, match.getLocation());
      }

      @Override
      public void targetNotFound(TrackerEvent event){
         SpriteGroupViewAnimator.fadeOut(group);
      }
      
   };
   
   
   List<Tracker> _trackers = new ArrayList<Tracker>();
   
   
   private void play(Step stepToPlay){
      
      
//      _screenRegionSelector.setVisible(true);
//      _screenRegionSelector.setAlwaysOnTop(true);
      System.out.println("playing a step");
      
      // make a copy of the step model
      Step step;
      try {
         step = stepToPlay.createCopy();
      } catch (Exception e) {
         return;
      }

      _viewHelper.setStep(step);

      for (Target target : step.getTargets()){ 

         if (target instanceof ContextTarget){
            List<ContextImage> contextImages = (List<ContextImage>) step.getContextImages();
            ContextTarget contextTarget = (ContextTarget) target;
            
            // find the context image that contains the target
            // assume these context images are in z-order in the list, the first one on top
            // require target to be strictly inside a context image
            // TODO: relax this constraint, or force that in the editor...
            for (ContextImage image : contextImages){           
               if (image.getBounds().contains(contextTarget.getBounds())){               
                  contextTarget.setContextImage((DefaultContextImage) image);
                  break;
               }
            }            
         }
         
         if (target.getBufferedImage() == null){
            // skip this target if we can not obtain a buffered image
            continue;           
         }
         
         // hide all dependents initially         
         _viewHelper.setVisible(target, false, true);

         // set the tracker
         Tracker tracker = new Tracker(target);
         tracker.setScreenGrabber(_screenGrabber);
         tracker.start();         
         tracker.addTrackerListener(new SingleTargetTrackerListener(target));
         
         _trackers.add(tracker);
         
      }
      
      _window.start();
      
//    Transition transition = new ButtonTransition();
//    transition.addTransitionListener(new TransitionListener(){
//
//       @Override
//       public void transitionOccurred(TransitionEvent event) {
//          if (_playList.hasNext()){
//             playNext();
//          }else{
//             stop();
//          }
//       }
//       
//    });
//    transition.waitFor();
      
      TransitionGroup transitionGroup = new DefaultTransitionGroup(TransitionGroup.OR);
      transitionGroup.add(new TimeoutTransition(5000));
      transitionGroup.add(new ButtonTransition());
      //transitionGroup.add(new TrackerTransition(_trackers.get(0)));
      transitionGroup.addTransitionListener(new TransitionListener(){

         @Override
         public void transitionOccurred(TransitionEvent event) {
            if (_playList.hasNext()){
               playNext();
            }else{
               stop();
            }
         }
         
      });
      transitionGroup.waitFor();
      
      //firePlayerStarted();
      
      
      
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

   
   interface PlayList{
      void add(List<Step> steps);
      void rewind();
      Step next();
      boolean hasNext();
   }
   
   PlayList _playList = new PlayListImpl();
   class PlayListImpl extends ArrayList<Step> implements PlayList {

      int index = -1;
      
      @Override
      public void add(List<Step> steps) {
         addAll(steps);
      }

      @Override
      public Step next() {
         index++;
         Step nextStep = get(index);
         return nextStep;
      }

      @Override
      public boolean hasNext() {
         return index < size() - 1;
      }

      @Override
      public void rewind() {
         index = -1;
      }
     
   }
   
   private void playNext(){
      if (_playList.hasNext()){
         play(_playList.next());
      }
   }
   
   @Override
   public void play(List<Step> steps) {
      if (steps.isEmpty())
         return;
      _playList.add(steps);      
      playNext();
   }

}