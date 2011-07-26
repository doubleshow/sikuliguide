package org.sikuli.guide;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.sikuli.cv.FindResult;
import org.sikuli.cv.TemplateFinder;

interface TrackerListener {

//   void patternAnchored(Object source);
   void targetFoundFirstTime(Target target, FindResult match);
   void targetFoundAgain(Target target, FindResult match);
   void targetNotFound(Target target);

}
//
//class SklClicker implements SklTrackerListener, Transition{
//   
//   private SklAnchorModel _anchor;
//  
//   SklClicker(SklAnchorModel anchor){
//      _anchor = anchor;
//   }
//
//   @Override
//   public void patternFoundAgain(Object source, Region match) {
//   }
//
//   @Override
//   public void patternFoundFirstTime(Object source, Region match) {
//      _anchor.setLocation(match.x, match.y);
//      _anchor.setSize(match.w, match.h);
//      SklAnimationFactory.createFadeinAnimation(_anchor).start();
//
//      try {
//         (new Screen()).click(match, 0);
//      } catch (FindFailed e) {
//      }
//
//      if (transitionListener != null)
//         transitionListener.transitionOccurred(this);
//            
//      ((SklTracker) source).stopTracking();
//   }
//
//   @Override
//   public void patternNotFound(Object source) {
//      SklAnimationFactory.createFadeoutAnimation(_anchor).start();
//   }
//
//   public void setAnchor(SklAnchorModel anchor) {
//      this._anchor = anchor;
//   }
//
//   public SklAnchorModel getAnchor() {
//      return _anchor;
//   }
//   
//   TransitionListener transitionListener;
//   
//   @Override
//   public String waitForTransition(TransitionListener transitionListener) {
//      this.transitionListener = transitionListener;
//      return null;
//   }
//   
//}
//
//class SklVisibilityCheckerGroup implements Transition {
//   TransitionListener transitionListener;
//   
//   
//   ArrayList<SklVisibilityChecker> _checkers = new ArrayList<SklVisibilityChecker>();
//   
//   
//   
//   @Override
//   public String waitForTransition(TransitionListener transitionListener) {
//      this.transitionListener = transitionListener;
//      return null;
//   }
//
//
//   int found = 0;
//   public void oneMoreFound() {
//      found += 1;
//      if (found == _checkers.size() && transitionListener != null){
//         
//         // Wait for one second before transitioning
//         Timer timer = new Timer(1000, new ActionListener(){
//
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//               transitionListener.transitionOccurred(SklVisibilityCheckerGroup.this);
//            }
//         
//         });
//         timer.setRepeats(false);
//         timer.start();
//      }
//   }
//}
//
//class SklVisibilityChecker implements SklTrackerListener {
//   
//   private SklAnchorModel _anchor;
//   private SklVisibilityCheckerGroup _group;
//  
//   SklVisibilityChecker(SklVisibilityCheckerGroup group, SklAnchorModel anchor){
//      _anchor = anchor;
//      _group = group;
//      _group._checkers.add(this);
//   }
//
//   @Override
//   public void patternFoundAgain(Object source, Region match) {
//   }
//
//   @Override
//   public void patternFoundFirstTime(Object source, Region match) {
//      _anchor.setSize(match.w, match.h);
//      SklAnimationFactory.createFadeinAnimation(_anchor).start();
//      
//      SklAnimation anim = SklAnimationFactory.createMoveToAnimation(_anchor, new Point(match.x,match.y));
//      anim.setListener(new SklAnimationListener(){
//
//         @Override
//         public void animationCompleted() {
//            _group.oneMoreFound();
//         }         
//      });
//      
//      anim.start();
//      SklAnimationFactory.createMoveToAnimation(_anchor, new Point(match.x,match.y)).start();
//            
//      ((SklTracker) source).stopTracking();
//      
//   }
//
//   @Override
//   public void patternNotFound(Object source) {
//      SklAnimationFactory.createFadeoutAnimation(_anchor).start();
//   }
//
//   public void setAnchor(SklAnchorModel anchor) {
//      this._anchor = anchor;
//   }
//
//   public SklAnchorModel getAnchor() {
//      return _anchor;
//   }
//   
//
//   
//}

//class SklAnchorTracker implements SklTrackerListener{
//   
//   private SklAnchorModel _anchor;
//  
//   SklAnchorTracker(SklAnchorModel anchor){
//      _anchor = anchor;
//   }
//
//   @Override
//   public void patternFoundAgain(Object source, Region match) {
//      // make it visible
//      SklAnimationFactory.createFadeinAnimation(_anchor).start();
//
//      //               int dest_x = newMatch.x + newMatch.w/2;
//      //               int dest_y = newMatch.y + newMatch.h/2;
//
//      int destx = match.x;
//      int desty = match.y;
//
//      Debug.log("[Tracker] Pattern is moving to: (" + destx + "," + desty + ")");
//
//      Point newLocation = new Point(destx, desty);
//      SklAnimationFactory.createMoveToAnimation(_anchor, newLocation).start();
//   }
//
//   @Override
//   public void patternFoundFirstTime(Object source, Region match) {
//      _anchor.setLocation(match.x, match.y);
//      _anchor.setSize(match.w, match.h);
//      SklAnimationFactory.createFadeinAnimation(_anchor).start();
//   }
//
//   @Override
//   public void patternNotFound(Object source) {
//      SklAnimationFactory.createFadeoutAnimation(_anchor).start();
//   }
//
//   public void setAnchor(SklAnchorModel anchor) {
//      this._anchor = anchor;
//   }
//
//   public SklAnchorModel getAnchor() {
//      return _anchor;
//   }
//   
//   
//}
interface ScreenGrabber {   
   BufferedImage grab();   
}

public class Tracker extends Thread {

//   Pattern _pattern;
   FindResult _lastMatch;
//   Region _screen;
//   Pattern _centerPattern;
   
   TemplateFinder finder = new TemplateFinder();
   private ScreenGrabber _screenGrabber;
   
   Target _target;
   
   Rectangle _regionModel;
   
//   public Tracker(Target target){
//      //this(new Pattern(patternModel.getImageUrl()));
//   }

   public void setRegion(Rectangle regionModel){
      _regionModel = regionModel;
   }
   
//   Region _region = new Region(0,0,0,0);
//   public Rectangle getRegion(){      
//      _region.setRect(_regionModel);
//      return _region;
//   }
   
   FindResult findTarget(){
      //System.out.println("[Tracker] find");
      BufferedImage inputImage = _screenGrabber.grab();
      BufferedImage targetImage = _target.getBufferedImage();
      return finder.findFirstGoodMatch(inputImage, targetImage);
   }
   
   
   BufferedImage crop(BufferedImage src, Rectangle rect){
      BufferedImage dest = new BufferedImage((int)rect.getWidth(), (int)rect.getHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics g = dest.getGraphics();
      g.drawImage(src, 0, 0, (int)rect.getWidth(), (int)rect.getHeight(), (int)rect.getX(), (int)rect.getY(),  (int)rect.getX() +  (int)rect.getWidth(),  (int)rect.getY() + (int)rect.getHeight(), null);
      g.dispose();
      return dest;
   }

   
   FindResult findTargetInRegion(Rectangle region){
      //System.out.println("[Tracker] find in region");
      BufferedImage inputImage = _screenGrabber.grab();
      BufferedImage regionImage = crop(inputImage, region);
      BufferedImage targetImage = _target.getBufferedImage();
      
      return finder.findFirstGoodMatch(regionImage, targetImage);
   }
   
   FindResult findCenterOfTargetInRegion(Rectangle region){
      BufferedImage inputImage = _screenGrabber.grab();
      BufferedImage regionImage = crop(inputImage, region);
      BufferedImage targetImage;
      
      // sample a d by d region in the center of the pattern
      int d = 10;
      if (d < _lastMatch.width && d < _lastMatch.height){

         Rectangle area = new Rectangle(_lastMatch);
         area.x = _lastMatch.width/2 - d/2;
         area.y = _lastMatch.height/2 - d/2;
         area.width = d;
         area.height = d;

         targetImage  = crop(_target.getBufferedImage(), area);
      }else{
         
         targetImage = _target.getBufferedImage();
      }
      
      return finder.findFirstGoodMatch(regionImage, targetImage);
   }
   
   public Tracker(Target target){
      _target = target;
      
//      _screen = new Screen();
//      _pattern = pattern;
//      
//      try {
//         BufferedImage image;
//         BufferedImage center;
//         image = pattern.getImage();
//         int w = image.getWidth();
//         int h = image.getHeight();
//         center = image.getSubimage(w/4,h/4,w/2,h/2);         
//         _centerPattern = new Pattern(center);
//      } catch (IOException e) {
//         e.printStackTrace();
//      }      
   }
   
   List<TrackerListener> _listeners = new ArrayList<TrackerListener>();   
   public void addTrackerListener(TrackerListener listener){
      _listeners.add(listener);
   }
     
   
   boolean isPatternStillThereInTheSameLocation(){
      try {
         sleep(1000);
      } catch (InterruptedException e) {
      }      
      
      FindResult m = findCenterOfTargetInRegion(_lastMatch);
//    TODO: verify the center is till at the center of the target
      return m != null;      
   }


   boolean running;
   public void run(){
      running = true;

      // Looking for the target for the first time
      //System.out.println("[Tracker] started");
      
      _lastMatch = null;
      while (running && (_lastMatch == null)){      
         _lastMatch = findTarget();         
      }
      
      // this means the tracker has been stopped before the pattern is found
      if (_lastMatch == null)
         return;
      
      notifyTargetFoundFirstTime(_lastMatch);
               
      while (running){

         if (_lastMatch != null && isPatternStillThereInTheSameLocation()){
            continue;
         }

         // try for at least 1.0 sec. to have a better chance of finding the
         // new position of the pattern.
         // the first attempt often fails because the target is only a few
         // pixels away when the screen capture is made and it is still
         // due to occlusion by foreground annotations      
         // however, it would mean it takes at least 1.0 sec to realize
         // the pattern has disappeared and the referencing annotations should
         // be hidden
         FindResult newMatch = findTarget();
         if (newMatch == null && _lastMatch != null){
            notifyTargetNotFound();
         }

         if (newMatch != null){
            notifyTargetFoundAgain(newMatch);
         }

         _lastMatch = newMatch;
      } 
   }

   private void notifyTargetFoundFirstTime(FindResult match) {
      for (TrackerListener listener : _listeners){
         listener.targetFoundFirstTime(_target,match);
      }
      System.out.println("Target found first time!");
      
//      Animator anim;
//      anim = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).build();
//      anim.addTarget(PropertySetter
//            .getTargetTo(_target, "x", new AccelerationInterpolator(0.5, 0.5), match.x));
//      anim.addTarget(PropertySetter
//            .getTargetTo(_target, "y", new AccelerationInterpolator(0.5, 0.5), match.y));
//      anim.start();
//      
//      _target.setX(match.x);
//      _target.setY(match.y);
//      _target.setWidth(match.width);
//      _target.setHeight(match.height);
//      _target.setFound(true);
   }
   

   
   private void notifyTargetNotFound() {
      for (TrackerListener listener : _listeners){
         listener.targetNotFound(_target);
      }
      System.out.println("Target not found");
//      _target.setFound(false);
   }
   
   private void notifyTargetFoundAgain(FindResult match) {
      for (TrackerListener listener : _listeners){
         listener.targetFoundAgain(_target,match);
      }
      System.out.println("Target found again");

//      _target.setX(match.x);
//      _target.setY(match.y);
//      _target.setWidth(match.width);
//      _target.setHeight(match.height);
//      _target.setFound(true);
   }

   public void stopTracking(){
      running = false;
   }

   public void setScreenGrabber(ScreenGrabber screenGrabber) {
      this._screenGrabber = screenGrabber;
   }

   public ScreenGrabber getScreenGrabber() {
      return _screenGrabber;
   }
  
}
