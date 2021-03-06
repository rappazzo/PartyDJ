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
import com.google.common.collect.*;
import com.partydj.player.*;
import com.partydj.util.*;

/**
 * 
 **/
public enum PlaylistManager {
   INSTANCE;
   
   public static final int MIN_QUEUE_SIZE = 3;
   
   private static final ScheduledExecutorService CHECKER_POOL = Executors.newScheduledThreadPool(1, NamedThreadFactory.createDaemonFactory("Playlist Manager"));
//   private static ExecutorService SONG_POOL_BUILDER_POOL = Executors.newFixedThreadPool(5, NamedThreadFactory.createDaemonFactory("Playlist Song Pool Builder"));

   private QueueChecker CHECKER = new QueueChecker();
   
   private List<MediaRequest> requestQueue = Collections.synchronizedList(new LinkedList());
   private List<MediaFile> songPool = new ArrayList(); //this is made concurrent after initialization
   private int poolPointer = 0;
   private Map<MediaFile, Integer> requestCount = new ConcurrentHashMap<MediaFile, Integer>();
   private Map<MediaFile, Long> lastPlayed = new ConcurrentHashMap<MediaFile, Long>();
   private Collection<MediaFile> history = new ConcurrentLinkedQueue();
   private SortedSetMultimap<String, Long> skips = TreeMultimap.create();

   void start() {
      String poolFile = Config.config().getProperty(ConfigKeys.MUSIC_POOL);
      File songPoolSource = new File(poolFile);
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
         songPool = new CopyOnWriteArrayList(songPool);
      }
      int next = queueNext(MIN_QUEUE_SIZE - player.getPlayQueueSize());
      CHECKER_POOL.schedule(CHECKER, next, TimeUnit.SECONDS);
   }
   
   static final FileFilter POOL_FILE_FILTER = new FileFilter() {
      private final Pattern songFilePattern = Pattern.compile("\\.mp3$|\\.m4a$", Pattern.CASE_INSENSITIVE);
      private static final long MIN_SIZE = 349525; //third of a meg
      @Override public boolean accept(File pathname) {
         return (!isSymlink(pathname) && pathname.isDirectory()) || (pathname.length() >= MIN_SIZE && songFilePattern.matcher(pathname.getName()).find());
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
   
   private void createPoolFromDirectory(final File songPoolSource) {
//      SONG_POOL_BUILDER_POOL.execute(new Runnable() {
//         @Override public void run() {
            System.out.println("Adding From: " + songPoolSource.getAbsolutePath());
            File[] files = songPoolSource.listFiles(POOL_FILE_FILTER);
            for (File file : files) {
               if (file.isDirectory()) {
                  createPoolFromDirectory(file);
               } else {
                  addToPool(MediaFile.create(file));
               }
            }
//         }
//      });
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
                     addToPool(file);
                  }
               }
            }
         }
      } catch (Exception e) {
         System.out.println("Error reading playlist file: " + e.getMessage());
         e.printStackTrace();
      }
   }
   
   public Collection<MediaRequest> getRequests() {
      return Collections.unmodifiableCollection(requestQueue);
   }

   public int request(MediaFile mediaFile, String from) {
      if (mediaFile != null && allowQueue(mediaFile)) {
         Player player = PartyDJ.getInstance().getPlayer();
         int totalSeconds = player.getTotalQueueLengthInSeconds();
         MediaRequest existingRequest = null;
         for (MediaRequest requestItem : requestQueue) {
            if (requestItem.getMediaFile().equals(mediaFile)) {
               existingRequest = requestItem;
               break;
            }
            try {
               totalSeconds += requestItem.getMediaFile().getMetadata().getDurationSeconds().intValue();
            } catch (Exception e) {
               AudioFileFormat baseFileFormat;
               try {
                  baseFileFormat = AudioSystem.getAudioFileFormat(requestItem.getMediaFile().getFile());
                  Map properties = baseFileFormat.properties();
                  totalSeconds += (Long) properties.get("duration");
               } catch (Exception e1) {
                  totalSeconds += 5;
               }
            }
         }
         if (existingRequest != null) {
            existingRequest.vote(from);
         } else {
            increaseRequestCount(mediaFile);
            requestQueue.add(MediaRequest.create(mediaFile, from));
         }
         Collections.sort(requestQueue);
         if (!player.isPlaying()) {
            CHECKER_POOL.schedule(CHECKER, 10, TimeUnit.MILLISECONDS);
         }
         return totalSeconds;
      }
      return 0;
   }
   
   public boolean canSkip(String who) {
      Integer maxSkipsPerHour = Config.config().getIntegerProperty(ConfigKeys.MAX_SKIPS_PER_HOUR);
      if (maxSkipsPerHour != null) {
         long now = System.currentTimeMillis();
         long ago = now - 1000 * 60 * 60; //1 hour
         SortedSet<Long> userSkips = skips.get(who);
         if (userSkips.isEmpty()) {
            return true;
         } else {
            //first remove/cleanup stale entries
            Iterator<Long> it = userSkips.iterator();
            while (it.hasNext() && it.next().longValue() < ago) {
               it.remove();
            }
            if (userSkips.size() < maxSkipsPerHour) {
               return true;
            }
         }
      }
      return false;
   }
   
   public void skipToNext(String who) {
      if (canSkip(who)) {
         Player player = PartyDJ.getInstance().getPlayer();
         player.skipToNextInQueue();
         skips.put(who, Long.valueOf(System.currentTimeMillis()));
      }
   }
   
   public void addToPool(MediaFile file) {
      if (songPool == null) {
         songPool = new ArrayList();
      }
      songPool.add(file);
      PartyDJ.getInstance().getSearchProvider().addToSearchIndex(file);
   }
   
   protected MediaFile getNextMediaFile() {
      if (!requestQueue.isEmpty()) {
         Iterator<MediaRequest> requestIt = requestQueue.iterator();
         MediaRequest request = requestIt.next();
         requestIt.remove();
         return request.getMediaFile();
      } else if (!songPool.isEmpty()) {
         MediaFile next = songPool.get(poolPointer++);
         if (poolPointer >= songPool.size()) {
            poolPointer = 0;
            Collections.shuffle(songPool);
         }
         return next;
      }
      return null;
   }
   
   private int increaseRequestCount(MediaFile file) {
      Integer currentRequestCount = requestCount.get(file);
      if (currentRequestCount == null) {
         currentRequestCount = Integer.valueOf(0);
      }
      requestCount.put(file, Integer.valueOf(currentRequestCount.intValue() + 1));
      return currentRequestCount.intValue() + 1;
   }
   
   private int getRequestCount(MediaFile file) {
      Integer currentRequestCount = requestCount.get(file);
      if (currentRequestCount == null) {
         currentRequestCount = Integer.valueOf(0);
      }
      return currentRequestCount.intValue();
   }
   
   protected final int queueNext(int size) {
      boolean added = false;
      int next = 5;
      Player player = PartyDJ.getInstance().getPlayer();
      if (size > 0) {
         for (int i = 0; i < size; i++) {
            MediaFile file = getNextMediaFile();
            if (file != null) {
               history.add(file);
               next = player.addToQueue(file);
               added = true;
            }
         }
      }
      if (!added) {
         next = 15;
      }
      if (!player.isPaused()) {
         player.ensurePlaying();
      }
      return next;
   }
   
   private boolean allowQueue(MediaFile file) {
      int playCount = getRequestCount(file);
      long lastPlayed = getLastPlayed(file);
      return file != null &&
         (getMaxAllowedPlayCount() == null || playCount < getMaxAllowedPlayCount().intValue()) &&
         (lastPlayed == -1 || lastPlayed > getReplayTimeThreshhold())
      ;
   }

   /**
    * @return the last time (in minutes) that the given song was played OR -1 if it has not yet been played
    */
   public long getLastPlayed(MediaFile file) {
      Long lastTS = lastPlayed.get(file);
      return lastTS != null ? ((System.currentTimeMillis() - lastTS.intValue()) / 60000) : -1;
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
         int next = 15;
         try {
            Player player = PartyDJ.getInstance().getPlayer();
            next = queueNext(MIN_QUEUE_SIZE - player.getPlayQueueSize());
         } catch (Throwable t) {
            try {
               Player player = PartyDJ.getInstance().getPlayer();
               if (!player.isAvailable()) {
                  player.ensureAvailable();
                  System.out.println("Player was not available, but not is ready.  Requeueing in 2 seconds.");
                  next = 2;
               } else {
                  System.out.println("MAJOR error in queue checker.  Rescheduling for 1 minute from now and crossing fingers.");
                  next = 60;
               }
            } catch (Exception e) {
               System.out.println("Player cannot be made available!  Maybe someone will do somethign to change that in the next 5 minutes.");
               next = 300;
            }
         } finally {
            CHECKER_POOL.schedule(CHECKER, next, TimeUnit.SECONDS);
         }
      }
   }
   
}
