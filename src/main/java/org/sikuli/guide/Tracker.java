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

import javax.swing.Timer;

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
   FindResult _match;
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
      System.out.println("[Tracker] find");
      BufferedImage inputImage = _screenGrabber.grab();
      BufferedImage targetImage = _target.getImage();
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
      System.out.println("[Tracker] find in region");
      BufferedImage inputImage = _screenGrabber.grab();
      BufferedImage regionImage = crop(inputImage, region);
      BufferedImage targetImage = _target.getImage();
      
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

      //Rectangle center = new Rectangle(_match);
      //center.grow(h, v)
//      center.x += center.w/4-2;
//      center.y += center.h/4-2;
//      center.w = center.w/2+4;
//      center.h = center.h/2+4;

      //FindResult m = center.exists(_centerPattern,0);
      //FindResult m = findTarget();//.exists(_centerPattern,0);
      
      FindResult m = findTargetInRegion(_match);

      if (m == null)
         System.out.println("[Tracker] Pattern is not seen in the same location.");
//      
      return m != null;
      
      // Debug.log("[Tracker] Pattern is still in the same location" + m);
   }


   boolean running;
   public void run(){
      running = true;

      // Looking for the target for the first time
      System.out.println("[Tracker] Looking for the target for the first time");
      
      _match = null;
      while (running && (_match == null)){      
         _match = findTarget();//doesPatternExist
         //_match = getRegion().exists(_pattern,0.5);         
      }

      // this means the tracker has been stopped before the pattern is found
      if (_match == null)
         return;
      
      //Debug.log("[Tracker] Pattern is found for the first time");
      notifyTargetFoundFirstTime(_match);
               
      while (running){

         if (_match != null && isPatternStillThereInTheSameLocation()){
            //Debug.log("[Tracker] Pattern is seen in the same location.");
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
         if (newMatch == null){
           // Debug.log("[Tracker] Pattern is not found on the screen");
            
            if (_match != null){
               notifyTargetNotFound();
            }
         }else {
           // Debug.log("[Tracker] Pattern is found in a new location: " + newMatch);
            notifyTargetFoundAgain(newMatch);
         }

         _match = newMatch;
      } 
   }

   private void notifyTargetFoundFirstTime(FindResult match) {
      for (TrackerListener listener : _listeners){
         listener.targetFoundFirstTime(_target,match);
      }
      _target.setX(_match.x);
      _target.setY(_match.y);
      _target.setWidth(_match.width);
      _target.setHeight(_match.height);
      _target.setFound(true);
   }
   
   private void notifyTargetNotFound() {
      for (TrackerListener listener : _listeners){
         listener.targetNotFound(_target);
      }
   }
   
   private void notifyTargetFoundAgain(FindResult match) {
      for (TrackerListener listener : _listeners){
         listener.targetFoundAgain(_target,match);
      }
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
