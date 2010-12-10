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
package com.partydj.io;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import com.partydj.io.servlet.*;
import com.partydj.server.*;
import com.partydj.util.*;

/**
 * 
 **/
public class Http {

   public static final String NAME = "http";
   public static final String PORT = "port";

   protected int port = 80;
   private final List<SimpleServlet> servlets;

   private static final ExecutorService WORKER_POOL = Executors.newCachedThreadPool(NamedThreadFactory.createDaemonFactory("Http Worker #"));
   
   public static Http create() {
      Http http = new Http();
      http.init(Config.config());
      return http;
   }

   private Http() {
      servlets = new ArrayList();
   }
   private void init(Config config) {
      Integer altPort = config.getIntegerProperty(NAME, PORT);
      if (altPort != null) {
         this.port = altPort;
      }
      //File servlet should ALWAYS be last in the servlet list
      servlets.add(new PlayerServlet());
      servlets.add(new FileServlet());
   }
   
   public void start() {
      Executors.newSingleThreadExecutor(NamedThreadFactory.createDaemonFactory("Http Acceptor")).execute(new Acceptor());
   }
   
   class Acceptor implements Runnable {
      @Override public void run() {
         ServerSocket socket;
         try {
            socket = new ServerSocket(port);
         } catch (IOException e) {
            System.out.println("Unable to open a socket on port "+ port);
            e.printStackTrace();
            throw new RuntimeException(e);
         }
         while (true) {
            try {
               WORKER_POOL.execute(new Worker(socket.accept()));
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   class Worker implements Runnable {

      private Socket listener;

      private Worker(Socket listener) {
         this.listener = listener;
      }

      @Override public synchronized void run() {
         try {
            InputStream inStream = listener.getInputStream();
            PrintStream outStream = new PrintStream(listener.getOutputStream());
            listener.setSoTimeout(0);
            listener.setTcpNoDelay(true);
            DefaultHttpServletRequest servletRequest = DefaultHttpServletRequest.create(inStream);
            try {
               if (servletRequest != null) {
                  for (SimpleServlet servlet : servlets) {
                     if (servlet.accept(servletRequest, listener)) {
                        servlet.handle(servletRequest, outStream, listener);
                        break;
                     }
                  }
               }
            } finally {
               listener.close();
            };
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

   }
   
}
