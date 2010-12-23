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

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.cmc.music.metadata.*;
import org.cmc.music.myid3.*;
import com.partydj.server.*;
import com.partydj.util.*;
import com.partydj.util.json.*;

/**
 * $MR TODO: make the metadata a local interface - implement with the 3rd party lib
 **/
public class MediaFile implements JSONSerializable {

   public static final String FILENAME = "fileName";
   public static final String METADATA = "metadata";
   public static final String ARTIST = "artist";
   public static final String ALBUM = "album";
   public static final String TITLE = "title";
   public static final String TRACK = "track";
   public static final String YEAR = "year";
   public static final String LENGTH = "length";
   

   private File file;
   Future<IMusicMetadata> parser;
   private IMusicMetadata metadata = null;
   
   private static final ExecutorService METADATA_PARSER_POOL = Executors.newFixedThreadPool(5, NamedThreadFactory.createDaemonFactory("Metadata Parser"));
   private static Map<String, MediaFile> CACHE = new HashMap();
   
   public static final Comparator<MediaFile> SORT_BY_ARTIST_ALBUM_TITLE = new Comparator<MediaFile>() {
      @Override public int compare(MediaFile o1, MediaFile o2) {
         String o1Comparable = o1.getMetadata() != null ? o1.getSimpleName() : null;
         String o2Comparable = o2.getMetadata() != null ? o2.getSimpleName() : null;
         if (o1Comparable != null && o2Comparable != null) {
            return o1Comparable.compareTo(o2Comparable);
         } else if (o1Comparable == null && o2Comparable != null) {
            return 1;
         } else if (o1Comparable != null && o2Comparable == null) {
            return -1;
         }
         return 0;
      }
   };
   
   public static final Comparator<MediaFile> SORT_BY_TITLE = new Comparator<MediaFile>() {
      @Override public int compare(MediaFile o1, MediaFile o2) {
         String o1Title = o1.getMetadata() != null ? o1.getMetadata().getSongTitle() : null;
         String o2Title = o2.getMetadata() != null ? o2.getMetadata().getSongTitle() : null;
         if (o1Title != null && o2Title != null) {
            return o1Title.compareTo(o2Title);
         } else if (o1Title == null && o2Title != null) {
            return 1;
         } else if (o1Title != null && o2Title == null) {
            return -1;
         }
         return 0;
      }
   };
   
   static class MetadataParser implements Callable<IMusicMetadata> {
      File source;
      MetadataParser(File source) {
         this.source = source;
      }
      @Override public IMusicMetadata call() throws Exception {
         IMusicMetadata metadata;
         try {
            MusicMetadataSet metadataSet = new MyID3().read(source);
            metadata = metadataSet.getSimplified();
         } catch (Exception e) {
            metadata = new MusicMetadata(source.getName());
            try {
               File file = source;
               String fileName = file.getName();
               fileName = file.getName().substring(0, fileName.lastIndexOf("."));
               String[] parts = fileName.split("\\s*[-]\\s*");
               metadata.setSongTitle(parts[parts.length - 1]);
               int index = 0;
               if (parts.length > index + 1) {
                  if (parts[index].matches("\\d+")) {
                     String num = parts[index];
                     if (num.charAt(0) == '0') {
                        num = num.substring(1);
                     }
                     metadata.setTrackNumber(Integer.valueOf(num), "");
                  }
                  if (parts.length > index + 1) {
                     metadata.setArtist(parts[index++]);
                  }
               }
               if (parts.length > index + 1) {
                  metadata.setArtist(parts[index++]);
               }
            } catch (Exception ignore) {}
         }
         return metadata;
      }
   }
  
   public static MediaFile create(String source) {
      if (CACHE.containsKey(source)) {
         return CACHE.get(source);
      }
      return source != null ? create(new File(source)) : null;
   }
   public static MediaFile create(File source) {
      if (source != null && source.exists()) {
         String cacheKey = source.getAbsolutePath();
         if (CACHE.containsKey(cacheKey)) {
            return CACHE.get(cacheKey);
         }
         Future<IMusicMetadata> parser = METADATA_PARSER_POOL.submit(new MetadataParser(source));
         
         MediaFile file = new MediaFile(source, parser);
         CACHE.put(cacheKey, file);
         return file;
      }
      return null;
   }
   public static MediaFile fromJSON(JSONObject json) {
      String fileName = json.getString(FILENAME);
      return create(fileName);
   }
   
   private MediaFile(File file, Future<IMusicMetadata> parser) {
      this.file = file;
      this.parser = parser;
   }

   public File getFile() {
      return file;
   }
   
   public IMusicMetadata getMetadata() {
      if (metadata == null) {
         try {
            metadata = parser.get();
         } catch (Exception e) {
            metadata = new MusicMetadata(getFile().getName());
         }
         ensureMetadata();
      }
      return metadata;
   }
   
   public static final Pattern TITLE_AND_TRACK = Pattern.compile("^0*(\\d+)\\s(\\s*-\\s*)?(.*)$");
   public static final Pattern TRACK_NUM = Pattern.compile("^0*(\\d\\d+)");
   private void ensureMetadata() {
      try {
         File file = getFile();
         String fileName = file.getName();
         fileName = file.getName().substring(0, fileName.lastIndexOf("."));
         String[] parts = fileName.split("\\s*[-]\\s*");
         
         if (metadata.getSongTitle() == null) {
            String title = parts[parts.length - 1];
            Matcher m = TITLE_AND_TRACK.matcher(title);
            if (m.find()) {
               try {
                  title = m.group(3);
               } catch (Exception ignore) {}
               //ignore the track
            }
            metadata.setSongTitle(title);
         }
         if (metadata.getArtist() == null) {
            String poolFile = Config.config().getProperty(ConfigKeys.MUSIC_POOL);
            String[] path = file.getAbsolutePath().substring(poolFile.length()).split("[\\\\/]");
            if (path.length > 3) {
               metadata.setArtist(path[path.length - 3]);
               if (metadata.getAlbum() == null) {
                  metadata.setAlbum(path[path.length - 2]);
               }
            } else if (path.length > 2) {
               metadata.setArtist(path[path.length - 2]);
            }
         }
         int offset = 0;
         if (parts.length > 1 && TRACK_NUM.matcher(parts[0]).find()) {
            offset++;
         }
         if (metadata.getArtist() == null && parts.length > offset+1) {
            metadata.setArtist(parts[offset]);
         }
         if (metadata.getAlbum() == null && parts.length > offset+2) {
            metadata.setAlbum(parts[offset+1]);
         }
      } catch (Exception ignore) {}
      
   }
   public String getLengthDisplay() {
      Number seconds = getMetadata().getDurationSeconds();
      if (seconds != null) {
         return Etc.getTimeDurationDisplay(seconds.intValue());
      }
      return null;
   }
   
   @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((file == null) ? 0 : file.hashCode());
      return result;
   }
   
   @Override public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      MediaFile other = (MediaFile)obj;
      if (file == null) {
         if (other.file != null) return false;
      } else if (!file.equals(other.file)) return false;
      return true;
   }
   
   @Override public String toString() {
      return getFile().getAbsolutePath();
   }
   
   public String getSimpleName() {
      return getMetadata() != null ? getMetadata().getArtist() + " - " + getMetadata().getAlbum() + " - " + getMetadata().getSongTitle() : "".intern();
   }
   
   public JSONObject toJSON() {
      JSONObject json = new JSONObject();
      json.put(FILENAME, getFile().getAbsolutePath());

      IMusicMetadata md = getMetadata();
      JSONObject metadata = new JSONObject();
      metadata.put(ARTIST, md.getArtist());
      metadata.put(ALBUM, md.getAlbum());
      String title = md.getSongTitle();
      if (title == null) {
         title = getFile().getName();
      }
      metadata.put(TITLE, title);
      metadata.put(TRACK, md.getPartOfSetIndex());
      metadata.put(YEAR, md.getYear());
      metadata.put(LENGTH, getLengthDisplay());
      json.put(METADATA, metadata);

      return json;
   }
   
}
