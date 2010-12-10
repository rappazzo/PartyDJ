/***
 **  @(#) TradeCard.com 1.0
 **
 **  Copyright (c) 2009 TradeCard, Inc. All Rights Reserved.
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
 **  @author Copyright (c) 2009 TradeCard, Inc. All Rights Reserved.
 **
 **/
package com.partydj.util.json;

import java.io.*;
import java.util.*;

/**
 * @author mrappazzo
 *
 *
 **/
public class SortedKeyJsonSerializer {

   /**
    * 
    */
   public SortedKeyJsonSerializer() {
   }
   
   public static void write(JSONObject json, Writer out) {
      JSONObject sorted = createSortedKeyJSON(json);
      writeJSON(json, out);
   }
   
   protected static JSONObject createSortedKeyJSON(JSONObject json) {
      if (json.delegate() instanceof SortedMap) {
         return json;
      }
      return JSONObject.newSortedKeyInstance();
   }
   
   public static String writeToString(JSONObject json) {
      StringWriter writer = new StringWriter();
      write(json, writer);
      return writer.toString();
   }
   
   public static Writer writeJSON(JSONObject json, Writer writer) throws JSONException {
      try {
         boolean b = false;
         writer.write('{');

         Iterator<Map.Entry<String, Object>> it = json.entrySet().iterator();
         while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            writer.write(JSONObject.quote(entry.getKey()));
            writer.write(':');
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
               writeJSON(createSortedKeyJSON((JSONObject)value), writer);
            } else if (value instanceof JSONArray) {
               ((JSONArray)value).write(writer);
            } else {
               JSONObject.valueToWriter(writer, value);
            }
            if (it.hasNext()) {
               writer.write(',');
            }
         }
         writer.write('}');
         return writer;
      } catch (IOException e) {
         throw new JSONException(e);
      } finally {
         try {
            writer.flush();
         } catch (Exception e) {
            throw new IllegalStateException("Cannot flush writer during json serialization.", e);
         }
      }
   }

}
