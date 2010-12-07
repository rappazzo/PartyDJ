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

/**
 * 
 **/
public interface Player {

   //status checks
   boolean isAvailable();
   boolean isPlaying();
   boolean isStopped();
   boolean isPaused();

   /**
    * Ensure that the player is available.
    * @throws IllegalStateException if the player cannot be made available
    */
   void ensureAvailable();
   
   /**
    * Ensure that the player is playing.  If the player is not playing, this will
    * issue a command to the player to begin playing
    */
   void ensurePlaying();
   
   /**
    * @return the size of the queue which is playing or remains to be played
    */
   int getPlayQueueSize();
   
   /**
    * get info about the currently playing track
    */
   MediaFile getCurrentlyPlaying();
   
   /**
    * get info about the next track in the queue
    * @return null if there is not a next element in the queue
    */
   MediaFile getNextInQueue();
   
   /**
    * get the time remaining (in seconds) for the currently playing track
    */
   int getCurrentlyPlayingTimeRemaining();
   
   /**
    * @return a collection of info about the media in the queue which is playing or remains to be played
    *         (the returned collection may be empty; a null value should NOT be expected)
    */
   Collection<MediaFile> getPlayQueue();
   
   /**
    * @retrun the queue length in seconds
    */
   int getTotalQueueLengthInSeconds();
   
   /**
    * add the given MediaFile to the queue
    * @return the estimated time (in seconds) before the given media will be played.
    */
   int addToQueue(MediaFile media);
   
}
