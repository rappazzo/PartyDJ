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
import com.partydj.player.*;

/**
 * 
 **/
public interface SearchProvider {

   public static final String ANY = "any";
   public static final String MAX_RESULTS = "maxResults";
   public static final Integer DEFAULT_MAX_RESULTS = Integer.valueOf(50);
   
   /**
    * Add the given media file to the search index
    */
   public void addToSearchIndex(MediaFile file);
   
   /**
    * search the index for using the given query
    */
   public Collection<MediaFile> find(Map<String, Collection<String>> queryParameters);
   
}
