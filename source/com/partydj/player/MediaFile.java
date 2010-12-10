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
import org.cmc.music.metadata.*;
import org.cmc.music.myid3.*;
import com.partydj.util.*;
import com.partydj.util.json.*;

/**
 * 
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
            //no metadata - default to file name
            metadata = new MusicMetadata(source.getName());
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
      }
      return metadata;
   }
   
   public String getLengthDisplay() {
      Number seconds = getMetadata().getDurationSeconds();
      if (seconds != null) {
         int len = seconds.intValue();
         StringBuilder length = new StringBuilder();
         length.append(len/60);
         length.append(':');
         length.append(len % 60);
         return length.toString();
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
   
   public String getSearchableString() {
      return getMetadata() != null ? getMetadata().getArtist() + " - " + getMetadata().getSongTitle() : "".intern();
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
