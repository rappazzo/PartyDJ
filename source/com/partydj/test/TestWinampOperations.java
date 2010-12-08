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
package com.partydj.test;

import java.io.*;
import org.cmc.music.metadata.*;
import org.cmc.music.myid3.*;
import com.partydj.player.*;
import com.qotsa.jni.controller.*;

/**
 * 
 **/
public class TestWinampOperations {

   /**
    * @param args
    */
   public static void main(String[] args) {
      try {
         File sampleDir = new File("C:/Users/mrappazzo/Music/Artists/Testament/The Ritual");
         File[] samples = sampleDir.listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
               return name.endsWith("mp3");
            }
         });
         
         Winamp winamp = new Winamp();
         winamp.ensureAvailable();
         
         //setup
         WinampController.clearPlayList();
         WinampController.appendToPlayList(samples[0].getAbsolutePath());
         WinampController.appendToPlayList(samples[1].getAbsolutePath());
         WinampController.appendToPlayList(samples[2].getAbsolutePath());
         
         winamp.ensurePlaying();
         MediaFile queued = MediaFile.create(samples[3]);
         int waitTime = winamp.addToQueue(queued);
         System.out.println("Wait time for " + queued.getMetadata().getSongTitle() + ": "+waitTime);
         
         int beforeNext = winamp.getPlayQueueSize();
         WinampController.nextTrack();
         int afterNext = winamp.getPlayQueueSize();
         System.out.println("QueueSize -> beforeNext: "+beforeNext+", afterNext: "+afterNext);
         
         WinampController.stop();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
