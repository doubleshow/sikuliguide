package org.sikuli.guide;

import java.awt.Point;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.event.EventListenerList;


import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;



interface StepPlayerListener {      
   void stepCompleted(Step step);
   void stepFailed(Step step);
}

class PlayerEvent { 
   final static String STARTED = "started";
   final static String FINISHED = "finished";
   final static String PAUSED = "paused";
   final static String ABORTED = "aborted";
   
   private String type;
   private StoryPlayer player;
   PlayerEvent(StoryPlayer player, String type){
      this.setPlayer(player);
      this.setType(type);
   }
   
   public void setType(String type) {
      this.type = type;
   }

   public String getType() {
      return type;
   }
   
   StoryPlayer getPlayer(){
      return player;
   }

   public void setPlayer(StoryPlayer player) {
      this.player = player;
   }
   
}

interface PlayerListener extends EventListener {
   void playerStarted(PlayerEvent event);
   void playerFinished(PlayerEvent event);
   void playerPaused(PlayerEvent event);
   void playerAborted(PlayerEvent event);
}

public interface StoryPlayer{
   void play(List<Step> steps);
   void stop();
   
   void addPlayerListener(PlayerListener listener);
   void removePlayerListener(PlayerListener listener);
}

abstract class AbstractStoryPlayer implements StoryPlayer{

   private void fireEvent(String type){
      PlayerEvent event = new PlayerEvent(this, type);
      Object[] listeners = listenerList.getListenerList();
      for (int i=0; i<listeners.length; i+=2) {
         if (listeners[i]==PlayerListener.class) {            
            if (type.equals(PlayerEvent.STARTED)){
               ((PlayerListener)listeners[i+1]).playerStarted(event);               
            }else if (type.equals(PlayerEvent.FINISHED)){
               ((PlayerListener)listeners[i+1]).playerFinished(event);
            }else if (type.equals(PlayerEvent.PAUSED)){
               ((PlayerListener)listeners[i+1]).playerPaused(event);
            }else if (type.equals(PlayerEvent.ABORTED)){
               ((PlayerListener)listeners[i+1]).playerAborted(event);
            }
         }
      }

   }
   
   protected void firePlayerStarted(){
      fireEvent(PlayerEvent.STARTED);
   }
   
   protected void firePlayerAborted(){
      fireEvent(PlayerEvent.ABORTED);
   }
   
   protected void firePlayerPaused(){
      fireEvent(PlayerEvent.PAUSED);
   }
   
   protected void firePlayerFinished(){
      fireEvent(PlayerEvent.FINISHED);
   }

   
   private EventListenerList listenerList = new EventListenerList();   
   @Override
   public void addPlayerListener(PlayerListener l) {
      listenerList.add(PlayerListener.class, l);
   }
   
   @Override
   public void removePlayerListener(PlayerListener l) {
      listenerList.remove(PlayerListener.class, l);
   }
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
