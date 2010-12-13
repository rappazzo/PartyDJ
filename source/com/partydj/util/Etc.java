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
package com.partydj.util;

import java.util.*;

/**
 * 
 **/
public class Etc {
   
   public static String getTimeDurationDisplay(int seconds) {
      StringBuilder length = new StringBuilder();
      length.append(seconds/60);
      length.append(':');
      int secs = seconds % 60;
      if (secs < 10) {
         length.append("0");
      }
      length.append(secs);
      return length.toString();
   }
   
   public static String join(Collection<String> c, String delim) {
      StringBuilder joined = new StringBuilder();
      if (c != null) {
         for (String s : c) {
            if (joined.length() > 0) {
               joined.append(delim);
            }
            joined.append(s);
         }
      }
      return joined.toString();
   }
}
