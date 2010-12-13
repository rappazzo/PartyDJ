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
public class FileServlet extends BaseServlet {
   
   public static final String PAGE = "page";
   
   public static final String DOC_ROOT = "./web";
   public static final String[] HTML_INDEX_FILES = new String[] {"index.html", "index.htm"};

   public FileServlet() {
      this(null);
   }
   
   public FileServlet(String servletAcceptKey) {
      super(servletAcceptKey);
   }
   
   @Override protected int getMinArguments() {
      return 1;
   }
   
   @Override public void handle(HttpServletRequest servletRequest, PrintStream outStream, Socket httpConnection) {
      try {
         super.handle(servletRequest, outStream, httpConnection);
      } catch (UnservableException e) {
         outStream.print("HTTP/1.0 " + e.getHttpErrorMessageType() + " unable to handle: ");
         try {
            outStream.write(((DefaultHttpServletRequest)servletRequest).getRequestBytes().toByteArray());
            outStream.write(new byte[]{(byte)'\r', (byte)'\n'});
            outStream.flush();
         } catch (IOException io) {
            e.printStackTrace();
         }
      }
   }
   
   @Override protected String getContentType(HttpServletRequest servletRequest) {
      String request = servletRequest.getRequestURI().toLowerCase();
      if (request.endsWith("css")) {
         return "text/css";
      } else if (request.endsWith("js")) {
         return "text/javascript";
      } else if (request.endsWith("jpg")) {
         return "image/jpeg";
      } else if (request.endsWith("png")) {
         return "image/png";
      }
      return servletRequest.getContentType();
   }

   @Override protected ChunkedByteBuffer getResponseContents(HttpServletRequest servletRequest, Socket httpConnection) {
      ChunkedByteBuffer response = new ChunkedByteBuffer();
      try {
         String uri = servletRequest.getRequestURI();
         String[] uriParts = uri.substring(1).split("[/\\ ]+");
         
         StringBuilder docPath = new StringBuilder();
         for (int i = 0; i < uriParts.length; i++) {
            docPath.append("/");
            docPath.append(uriParts[i]);
         }
         File fileToServe = new File(DOC_ROOT + docPath.toString());
         if (fileToServe.isDirectory()) {
            File[] listing = fileToServe.listFiles(new FilenameFilter() {
               public boolean accept(File dir, String name) {
                  boolean accepted = false;
                  for (String acceptedType : HTML_INDEX_FILES) {
                     accepted = name.equalsIgnoreCase(acceptedType);
                     if (accepted) {
                        break;
                     }
                  }
                  return accepted;
               }
            });
            if (listing.length > 0) {
               fileToServe = listing[0];
            }
         }
         if (fileToServe.exists() && fileToServe.isFile()) {
            long fileLen = fileToServe.length();
            //use the file length to set the buffer size
            int chunkSize = ChunkedCharBuffer.DEFAULT_CHUNK_SIZE;
            int numChunks = ChunkedCharBuffer.DEFAULT_NUMBER_OF_CHUNKS;
            if (fileLen > chunkSize * numChunks) {
               numChunks = new Long((fileLen / chunkSize) + 1).intValue();
            }
            ChunkedCharBuffer fileToServeContents = new ChunkedCharBuffer(chunkSize, numChunks);
            FileReader fr = new FileReader(fileToServe);
            fileToServeContents.append(fr);
            fr.close(); // Release the file lock
            response = fileToServeContents.toChunkedByteBuffer(CharsetConstants.UTF8);
         } else {
            throw new UnservableException(HttpConstants.HTTP_NOT_FOUND);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return response;
   }

   class UnservableException extends RuntimeException {
      
      int httpErrorMessageType;

      public UnservableException(int message) {
         super(String.valueOf(message));
         httpErrorMessageType = message;
      }
      public UnservableException(int message, Throwable cause) {
         super(String.valueOf(message), cause);
         httpErrorMessageType = message;
      }
      
      public int getHttpErrorMessageType() {
         return httpErrorMessageType;
      }

   }
   
}
