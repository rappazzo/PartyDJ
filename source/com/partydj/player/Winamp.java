/***
 **  @(#) TradeCard.com 1.0
 **
 **  Copyright (c) 2010 TradeCard, Inc. All Rights Reserved.
 **
 **
 **  THIS COMPUTER SOFTWARE IS THE PROPERTY OF TradeCard, Inc.
 **
 **  Permission is granted to use this software as specified by the TradeCard
 **  COMMERCIAL LICENSE AGREEMENT.  You may use this software only for
 **  commercial purposes, as specified in the details of the license.
 **  TRADECARD SHALL NOT BE LIABLE FOR ANY  DAMAGES SUFFERED BY
 **  THE LICENSEE AS A RESULT OF USING OR MODIFYING THIS SOFTWARE IN ANY WAY.
 **
 **  YOU MAY NOT DISTRIBUTE ANY SOURCE CODE OR OBJECT CODE FROM THE TradeCard.com
 **  TOOLKIT AT ANY TIME. VIOLATORS WILL BE PROSECUTED TO THE FULLEST EXTENT
 **  OF UNITED STATES LAW.
 **
 **  @version 1.0
 **  @author Copyright (c) 2010 TradeCard, Inc. All Rights Reserved.
 **
 **/
package com.partydj.player;

import java.util.*;
import javax.sound.sampled.*;
import com.qotsa.exception.*;
import com.qotsa.jni.controller.*;

/**
 * 
 **/
public class Winamp implements Player {
   
   @Override public int getTotalQueueLengthInSeconds() {
      Collection<MediaFile> currentQueue = getPlayQueue();
      int totalSeconds = 0;
      for (MediaFile track : currentQueue) {
         try {
            totalSeconds += track.getMetadata().getDurationSeconds().intValue();
         } catch (Exception e) {
            AudioFileFormat baseFileFormat;
            try {
               baseFileFormat = AudioSystem.getAudioFileFormat(track.getFile());
               Map properties = baseFileFormat.properties();
               totalSeconds += (Long) properties.get("duration");
            } catch (Exception e1) {
               totalSeconds += 5;
            }
         }
      }
      return totalSeconds;
   }
   
   @Override public int addToQueue(MediaFile media) {
      int waitTime = getTotalQueueLengthInSeconds();
      try {
         WinampController.appendToPlayList(media.getFile().getAbsolutePath());
      } catch (Exception e) {
         throw new RuntimeException("Exception adding to winamp queue", e);
      }
      return waitTime;
   }

   @Override public void ensureAvailable() {
      int status = -1;
      try {
         status = WinampController.getStatus();
      } catch (InvalidHandle e) {
         //not running - handled next
      }
      try {
         if (status == -1) {
            WinampController.run();
         }
         int waitTime = 0;
         final int maxWaitTime = 10000;
         while (waitTime < maxWaitTime && status == -1) {
            Thread.sleep(2000);
            try {status = WinampController.getStatus();} catch (InvalidHandle e) {}
         }
      } catch (Exception e) {
         throw new RuntimeException("Exception starting winamp", e);
      }
   }

   @Override public void ensurePlaying() {
      if (!isPlaying()) {
         try {
            WinampController.play();
         } catch (InvalidHandle e) {
            throw new RuntimeException("Exception issuing play command to winamp", e);
         }
      }
   }

   @Override public MediaFile getCurrentlyPlaying() {
      try {
         return MediaFile.create(WinampController.getFileNamePlaying());
      } catch (InvalidHandle e) {
         throw new RuntimeException("Exception getting currently playing winamp track", e);
      }
   }

   @Override public int getCurrentlyPlayingTimeRemaining() {
      try {
         MediaFile info = getCurrentlyPlaying();
         int length = info.getMetadata().getDurationSeconds().intValue();
         return WinampController.getTime(WinampController.TIMELENGTH) - length;
      } catch (Exception e) {
         throw new RuntimeException("Exception determining currently playing track's time remaining in winamp", e);
      }
   }

   @Override public MediaFile getNextInQueue() {
      try {
         int pos = WinampController.getListPos();
         int length = WinampController.getPlayListLength();
         return pos < length ? MediaFile.create(WinampController.getFileNameInList(pos + 1)) : null;
      } catch (Exception e) {
         throw new RuntimeException("Exception getting next in queue winamp track", e);
      }
   }

   @Override public List<MediaFile> getPlayQueue() {
      List<MediaFile> queue = new ArrayList();
      try {
         int pos = WinampController.getListPos();
         int length = WinampController.getPlayListLength();
         for (;pos < length; pos++) {
            queue.add(MediaFile.create(WinampController.getFileNameInList(pos)));
         }
      } catch (Exception e) {
         throw new RuntimeException("Exception getting winamp queue", e);
      }
      return queue;
   }

   @Override public int getPlayQueueSize() {
      try {
         int pos = WinampController.getListPos();
         int length = WinampController.getPlayListLength();
         return length - pos;
      } catch (InvalidHandle e) {
         throw new RuntimeException("Exception getting winamp queue size", e);
      }
   }

   @Override public boolean isAvailable() {
      int status = -1;
      try {
         status = WinampController.getStatus();
      } catch (InvalidHandle e) {
         //ignore
      }
      return status != -1;
   }

   @Override public boolean isPaused() {
      int status = -1;
      try {
         status = WinampController.getStatus();
      } catch (InvalidHandle e) {
         //ignore
      }
      return status == WinampController.PAUSED;
   }

   @Override public boolean isPlaying() {
      int status = -1;
      try {
         status = WinampController.getStatus();
      } catch (InvalidHandle e) {
         //ignore
      }
      return status == WinampController.PLAYING;
   }

   @Override public boolean isStopped() {
      int status = -1;
      try {
         status = WinampController.getStatus();
      } catch (InvalidHandle e) {
         //ignore
      }
      return status == WinampController.STOPPED;
   }

}
