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
package com.partydj.io.servlet;

import java.net.*;
import java.util.*;
import javax.servlet.http.*;
import com.partydj.player.*;
import com.partydj.server.*;
import com.partydj.util.*;
import com.partydj.util.json.*;

/**
 * 
 **/
public class PlayerServlet extends BaseServlet {
   
   private static final String PLAYER = "player";
   
   enum PlayerAction {
      NOWPLAYING() {
         @Override public Object invoke(HttpServletRequest servletRequest) {
            return PartyDJ.getInstance().getPlayer().getCurrentlyPlaying();
         }
      },
      QUEUE() {
         @Override public Object invoke(HttpServletRequest servletRequest) {
            List<MediaFile> queue = PartyDJ.getInstance().getPlayer().getPlayQueue();
            String sizeString = servletRequest.getParameter("size");
            if (sizeString != null) {
               //accept an "only show the next 'n' songs"
               try {
                  int size = Integer.valueOf(sizeString).intValue();
                  if (size < queue.size()) {
                     return queue.subList(0, size);
                  }
               } catch(Exception e) {
                  //ignore
               }
            }
            return queue;
         }
      },
      SEARCH() {
         @Override public Object invoke(HttpServletRequest servletRequest) {
            return PartyDJ.getInstance().getSearchProvider().find(servletRequest.getParameterMap());
         }
      },
      REQUEST() {
         @Override public Object invoke(HttpServletRequest servletRequest) {
            final JSONObject actualResult = new JSONObject();
            String fileName = servletRequest.getParameter(MediaFile.FILENAME);
            if (fileName != null) {
               MediaFile file = MediaFile.create(fileName);
               if (file != null && file.getFile() != null && file.getFile().exists()) {
                  int estimatedWaitTime = PlaylistManager.INSTANCE.request(file, servletRequest.getRemoteAddr());
                  actualResult.put("wait", Etc.getTimeDurationDisplay(estimatedWaitTime));
               } else {
                  System.out.println(fileName + " does not exist.");
               }
            }
            return new JSONSerializable() {
               @Override public JSONObject toJSON() {
                  return actualResult;
               }
            };
         }
      },
      LISTREQUESTS() {
         @Override public Object invoke(HttpServletRequest servletRequest) {
            return PlaylistManager.INSTANCE.getRequests();
         }
      }
      ;
      
      abstract Object invoke(HttpServletRequest servletRequest);
      
      static PlayerAction get(String actionName) {
         try {
            return valueOf(actionName.toUpperCase());
         } catch (Exception e) {
            return null;
         }
      }
   }

   /**
    * Constructor
    */
   public PlayerServlet() {
      super(PLAYER);
   }
   
   @Override protected int getMinArguments() {
      return 2;
   }
   
   @Override protected String getContentType(HttpServletRequest servletRequest) {
      return "application/json";
   }
   
   private String format(Object operationResult) {
      if (operationResult instanceof JSONSerializable) {
         return ((JSONSerializable)operationResult).toJSON().toString();
      } else if (operationResult instanceof Collection) {
         Collection collection = (Collection)operationResult;
         if (collection.size() > 0) {
            if (collection.iterator().next() instanceof JSONSerializable) {
               JSONArray json = new JSONArray();
               for (Object item : collection) {
                  if (item != null) {
                     json.add(((JSONSerializable)item).toJSON());
                  }
               }
               return json.toString();
            } else {
               StringBuilder buf = new StringBuilder();
               for (Object item : collection) {
                  buf.append(format(item));
                  //separator?
               }
               return buf.toString();
            }
         }
         return new JSONArray().toString();
      } else {
         return String.valueOf(operationResult);
      }
   }
   
   @Override protected ChunkedByteBuffer getResponseContents(HttpServletRequest servletRequest, Socket httpConnection) {
      ChunkedByteBuffer result = new ChunkedByteBuffer();
      String uri = servletRequest.getRequestURI();
      String[] uriParts = uri.substring(1).split("[/\\ ]+");
      PlayerAction operation = PlayerAction.get(uriParts[1]);
      if (operation != null) {
         try {
            result.append(format(operation.invoke(servletRequest)).getBytes(CharsetConstants.UTF8.name()));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      return result;
   }

}
