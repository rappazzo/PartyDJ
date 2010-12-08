/***
 **
 ** This library is free software; you can redistribute it and/or
 ** modify it under the terms of the GNU Lesser General Public
 ** License as published by the Free Software Foundation; either
 ** version 2.1 of the License, or (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 ** Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public
 ** License along with this library; if not, write to the Free Software
 ** Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 **
 **/
package com.partydj.io.servlet;

import java.io.*;
import java.net.*;
import javax.servlet.http.*;
import com.partydj.util.*;

/**
 * @author mrappazz
 *
 *
 **/
public class RssServlet extends BaseServlet {
   
   
   private static final String RSS_TEMPLATE = //ARGS: url, items
      "<?xml version=\"1.0\"?> " +
      "<rss version=\"2.0\"> " +
      "   <channel> " +
      "      <title>Soundboard</title> " +
      "      <link>%s</link> " +
      "      <description>Soundboard command history.</description> " +
      "      <language>en-us</language> " +
      "      <pubDate>2010-09-28</pubDate> " +
      "      <lastBuildDate>2010-09-28</lastBuildDate> " +
      "      %s " +
      "   </channel> " +
      "</rss> " +
      "";

   public RssServlet() {
   }

   @Override public boolean accept(HttpServletRequest servletRequest, Socket httpConnection) {
      if (servletRequest != null ) {
         String uri = servletRequest.getRequestURI();
         return uri != null && uri.length() >= 4 && uri.substring(uri.length() - 4).equalsIgnoreCase(".xml");
      }
      return false;
   }
   
   @Override public void handle(HttpServletRequest servletRequest, PrintStream outStream, Socket httpConnection) {
      ChunkedCharBuffer rssItems = new ChunkedCharBuffer();
      //$MR TODO
//      for (History.HistoryEntry history : History.getHistory()) {
//         rssItems.append(history.toRssItem());
//      }
      try {
         Writer writer = new PrintWriter(outStream);
         if (rssItems.length() > 0) {
            ChunkedByteBuffer feed = new ChunkedByteBuffer();
            feed.append(String.format(RSS_TEMPLATE, "http://soundboard/", rssItems.toString()).getBytes());
            createResponseHeader(feed.length()).writeTo(outStream);
            feed.writeTo(outStream);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      
   }

}
