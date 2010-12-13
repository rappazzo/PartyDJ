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

import java.io.*;
import com.partydj.io.*;
import com.partydj.player.*;
import com.partydj.search.*;

/**
 * 
 **/
public class PartyDJ {
   private static PartyDJ INSTANCE;
   
   private Player player = null;
   private Object monitor = new Object();
   private SearchProvider searchProvider = null;
   
   private PartyDJ() {
      PartyDJ.INSTANCE = this;
   }
   public static PartyDJ getInstance() {
      return INSTANCE;
   }
   
   /**
    * @return the player
    */
   public Player getPlayer() {
      if (this.player == null) {
         this.player = (Player)Config.config().getClassProperty(ConfigKeys.PLAYER_CLASS);
      }
      return this.player;
   }
   
   /**
    * @return the player
    */
   public SearchProvider getSearchProvider() {
      if (this.searchProvider == null) {
         if (Config.config().getProperty(ConfigKeys.SEARCH_PROVIDER) != null) {
            this.searchProvider = (SearchProvider)Config.config().getClassProperty(ConfigKeys.SEARCH_PROVIDER);
         } else {
            this.searchProvider = new RegexSearchProvider();
         }
      }
      return this.searchProvider;
   }
   
   public void run() {
      String poolFile = Config.config().getProperty(ConfigKeys.MUSIC_POOL);
      PlaylistManager.INSTANCE.start(new File(poolFile));
      Http.create().start();

      try {
         synchronized (monitor) {
            while (true) {
               monitor.wait();
            }
         }
      } catch (InterruptedException e) {
         //ignore -- this is how it shuts down.
      }
   }

   public static void main(String[] args) {
      if (args.length < 1) {
         System.out.println("Missing Config");
         System.exit(1);
      }
      Config.create(args[0]);
      PartyDJ partyDJ = new PartyDJ();
      partyDJ.run();
   }
   
}
