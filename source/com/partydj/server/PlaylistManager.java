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
package com.partydj.server;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import javax.sound.sampled.*;
import com.partydj.player.*;
import com.partydj.util.*;

/**
 * 
 **/
public enum PlaylistManager {
   INSTANCE;
   
   public static final int MIN_QUEUE_SIZE = 3;
   
   private static final ScheduledExecutorService CHECKER_POOL = Executors.newScheduledThreadPool(1, NamedThreadFactory.createDaemonFactory("Playlist Manager"));
   private QueueChecker CHECKER = new QueueChecker();
   
   private Queue<MediaFile> requestQueue = new ConcurrentLinkedQueue();
   //$MR this should probably be concurrent:
   private List<MediaFile> songPool = new ArrayList();
   private int poolPointer = 0;
   private Map<MediaFile, Integer> playCount = new ConcurrentHashMap<MediaFile, Integer>();
   private Map<MediaFile, Long> lastPlayed = new ConcurrentHashMap<MediaFile, Long>();
   private Collection<MediaFile> history = new ConcurrentLinkedQueue();

   void start(File songPoolSource) {
      Player player = PartyDJ.getInstance().getPlayer();
      player.ensureAvailable();
      if (songPoolSource != null && songPoolSource.exists()) {
         if (songPoolSource.isDirectory()) {
            createPoolFromDirectory(songPoolSource);
         } else {
            createPoolFromPlaylist(songPoolSource);
         }
         if (Config.config().getBooleanProperty(ConfigKeys.RANDOMIZE_POOL)) {
            Collections.shuffle(songPool);
         }
      }
      queueNext(MIN_QUEUE_SIZE - player.getPlayQueueSize());
   }
   
   public List<MediaFile> find(String regex) {
      if (regex == null || regex.length() == 0) {
         return Collections.unmodifiableList(songPool);
      } else {
         Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
         List<MediaFile> found = new ArrayList();
         for (MediaFile file : songPool) {
            if (pattern.matcher(file.getSearchableString()).find()) {
               found.add(file);
            }
         }
         return found;
      }
   }
   
   static final FileFilter POOL_FILE_FILTER = new FileFilter() {
      Pattern songFilePattern = Pattern.compile("\\.mp3$|\\.m4a$", Pattern.CASE_INSENSITIVE);
      @Override public boolean accept(File pathname) {
         return (!isSymlink(pathname) && pathname.isDirectory()) || songFilePattern.matcher(pathname.getName()).find();
      }
      boolean isSymlink(File file) {
         try {
            File canon;
            if (file.getParent() == null) {
               canon = file;
            } else {
               File canonDir = file.getParentFile().getCanonicalFile();
               canon = new File(canonDir, file.getName());
            }
            return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   };
   
   private List<MediaFile> createPoolFromDirectory(File songPoolSource) {
      File[] files = songPoolSource.listFiles(POOL_FILE_FILTER);
      for (File file : files) {
         if (file.isDirectory()) {
            createPoolFromDirectory(file);
         } else {
            songPool.add(MediaFile.create(file));
         }
      }
      return null;
   }
   
   private void createPoolFromPlaylist(File songPoolSource) {
      try {
         ChunkedCharBuffer fileContents = new ChunkedCharBuffer();
         final FileReader fr = new FileReader(songPoolSource);
         try {
            fileContents.append(fr);
         } finally {
            fr.close();
         }
         if (fileContents.length() > 0) {
            for (String fileName : fileContents.split("\\s*\\r?\\n\\s*")) {
               if (!fileName.startsWith("#")) { //ignore comments - this will make playlist files usable (if they include the fill path to the file)
                  MediaFile file = MediaFile.create(fileName);
                  if (file != null) {
                     songPool.add(file);
                  }
               }
            }
         }
      } catch (Exception e) {
         System.out.println("Error reading playlist file: " + e.getMessage());
         e.printStackTrace();
      }
   }

   public int request(MediaFile file) {
      if (file != null && allowQueue(file)) {
         Player player = PartyDJ.getInstance().getPlayer();
         int totalSeconds = player.getTotalQueueLengthInSeconds();
         for (MediaFile requestItem : requestQueue) {
            try {
               totalSeconds += requestItem.getMetadata().getDurationSeconds().intValue();
            } catch (Exception e) {
               System.out.println("Error determining track time for " + requestItem);
               AudioFileFormat baseFileFormat;
               try {
                  baseFileFormat = AudioSystem.getAudioFileFormat(requestItem.getFile());
                  Map properties = baseFileFormat.properties();
                  totalSeconds += (Long) properties.get("duration");
               } catch (Exception e1) {
                  totalSeconds += 5;
               }
            }
         }
         requestQueue.add(file);
         if (!player.isPlaying()) {
            CHECKER_POOL.schedule(CHECKER, 10, TimeUnit.MILLISECONDS);
         }
         return totalSeconds;
      }
      return 0;
   }
   
   public void addToPool(MediaFile file) {
      if (songPool == null) {
         songPool = new ArrayList();
      }
      songPool.add(file);
   }
   
   protected MediaFile getNextMediaFile() {
      if (!requestQueue.isEmpty()) {
         return requestQueue.poll();
      } else if (!songPool.isEmpty()) {
         MediaFile next = songPool.get(poolPointer++);
         if (poolPointer >= songPool.size()) {
            poolPointer = 0;
         }
         return next;
      }
      return null;
   }
   
   private int increasePlayCount(MediaFile file) {
      Integer currentPlayCount = playCount.get(file);
      if (currentPlayCount == null) {
         currentPlayCount = Integer.valueOf(0);
      }
      playCount.put(file, Integer.valueOf(currentPlayCount.intValue() + 1));
      return currentPlayCount.intValue() + 1;
   }
   
   private int getPlayCount(MediaFile file) {
      Integer currentPlayCount = playCount.get(file);
      if (currentPlayCount == null) {
         currentPlayCount = Integer.valueOf(0);
      }
      return currentPlayCount.intValue();
   }
   
   protected final void queueNext(int size) {
      boolean added = false;
      int next = 5;
      Player player = PartyDJ.getInstance().getPlayer();
      if (size > 0) {
         for (int i = 0; i < size; i++) {
            MediaFile file = getNextMediaFile();
            if (file != null && allowQueue(file)) {
               increasePlayCount(file);
               history.add(file);
               next = player.addToQueue(file);
               added = true;
            }
         }
      }
      if (!added) {
         next = 15;
      }
      player.ensurePlaying();
      CHECKER_POOL.schedule(CHECKER, next, TimeUnit.SECONDS);
   }
   
   private boolean allowQueue(MediaFile file) {
      int playCount = getPlayCount(file);
      int requestCount = 0;
      for (MediaFile request : requestQueue) {
         if (file.equals(request)) {
            requestCount++;
         }
      }
      long lastPlayed = getLastPlayed(file);
      return file != null &&
         (getMaxAllowedPlayCount() == null || playCount < getMaxAllowedPlayCount().intValue()) &&
         (lastPlayed == -1 || lastPlayed > getReplayTimeThreshhold())
      ;
   }

   public long getLastPlayed(MediaFile file) {
      Long lastTS = lastPlayed.get(file);
      return lastTS != null ? System.currentTimeMillis() - lastTS.intValue() : -1;
   }

   private Integer getMaxAllowedPlayCount() {
      return Config.config().getIntegerProperty(ConfigKeys.MAX_ALLOWED_PLAY_COUNT);
   }
   
   private int getReplayTimeThreshhold() {
      return Config.config().getIntProperty(ConfigKeys.REPLAY_TIME_THRESHHOLD);
   }
   
   public Collection<MediaFile> getHistory() {
      return Collections.unmodifiableCollection(history);
   }

   class QueueChecker implements Runnable {
      @Override public void run() {
         Player player = PartyDJ.getInstance().getPlayer();
         queueNext(MIN_QUEUE_SIZE - player.getPlayQueueSize());
      }
   }
   
}
