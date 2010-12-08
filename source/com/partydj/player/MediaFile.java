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
import java.util.regex.*;
import org.cmc.music.metadata.*;
import org.cmc.music.myid3.*;

/**
 * 
 **/
public class MediaFile {

   private File file;
   private IMusicMetadata metadata;
   
   public static MediaFile create(String source) {
      return source != null ? create(new File(source)) : null;
   }
   public static MediaFile create(File source) {
      if (source != null && source.exists()) {
         IMusicMetadata metadata;
         try {
            MusicMetadataSet metadataSet = new MyID3().read(source);
            metadata = metadataSet.getSimplified();
         } catch (Exception e) {
            //no metadata - default to file name
            metadata = new MusicMetadata(source.getName());
         }
         return new MediaFile(source, metadata);
      }
      return null;
   }
   
   private MediaFile(File file, IMusicMetadata metadata) {
      this.file = file;
      this.metadata = metadata;
   }

   public File getFile() {
      return file;
   }
   
   public IMusicMetadata getMetadata() {
      return metadata;
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
      return file.getAbsolutePath();
   }
   
   public String getSimpleName() {
      return getMetadata() != null ? getMetadata().getArtist() +" - "+ getMetadata().getAlbum() + " - " + getMetadata().getSongTitle() : "".intern();
   }
   
}
