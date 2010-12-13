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

import java.util.*;
import com.google.common.base.*;
import com.partydj.player.*;
import com.partydj.util.json.*;

/**
 * 
 **/
public class MediaRequest implements Comparable<MediaRequest>, JSONSerializable {

   public static final String VOTERS = "voters";
   public static final Function<MediaRequest, MediaFile> TO_MEDIA_FILE = new Function<MediaRequest, MediaFile>() {
      @Override public MediaFile apply(MediaRequest input) {
         return input.getMediaFile();
      }
   };

   private MediaFile file;
   private Set<String> voters = new LinkedHashSet();
   
   public static MediaRequest create(MediaFile file, String from) {
      return new MediaRequest(file, from);
   }
   
   private MediaRequest(MediaFile file, String from) {
      this.file = file;
      this.voters.add(from);
   }
   
   public MediaFile getMediaFile() {
      return file;
   }
   public Set<String> getVoters() {
      return voters;
   }
   public int getVotes() {
      return voters.size();
   }
   public void vote(String from) {
      voters.add(from);
   }
   
   //This will favor request with a greater count
   @Override public int compareTo(MediaRequest o) {
      int otherCount = o != null ? o.voters.size() : 0;
      return otherCount - voters.size();
   }
   
   @Override public JSONObject toJSON() {
      JSONObject json = file.toJSON();
      JSONArray voterJSON = new JSONArray();
      voterJSON.addAll(voters);
      json.put(VOTERS, voterJSON);
      return json;
   }
   
}
