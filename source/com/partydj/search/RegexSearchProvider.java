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
package com.partydj.search;

import java.util.*;
import java.util.regex.*;
import com.partydj.player.*;
import com.partydj.server.*;
import com.partydj.util.*;

/**
 * 
 **/
public class RegexSearchProvider implements SearchProvider {
   
   public static final String QUERY_PARAM = "any";
   
   List<MediaFile> indexed = new ArrayList();
   
   @Override public void addToSearchIndex(MediaFile file) {
      indexed.add(file);
   }

   @Override public List<MediaFile> find(Map<String, Collection<String>> queryParameters) {
      String query = "("+Etc.join(queryParameters.get(QUERY_PARAM), "|") +")";
      if (query == null || query.length() == 0) {
         return Collections.unmodifiableList(indexed);
      } else {
         Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
         List<MediaFile> found = new ArrayList();
         for (MediaFile file : indexed) {
            if (pattern.matcher(file.getSimpleName()).find()) {
               found.add(file);
            }
         }
         return found;
      }
   }

}
