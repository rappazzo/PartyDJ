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
import com.partydj.player.*;

/**
 * 
 **/
public class PartyDJ {
   private static PartyDJ INSTANCE;
   
   private Config config;
   private Player player;
   
   private PartyDJ(Config config) {
      PartyDJ.INSTANCE = this;
   }
   public static PartyDJ getInstance() {
      return INSTANCE;
   }
   
   /**
    * @return the config
    */
   Config getConfig() {
      return config;
   }
   
   /**
    * @return the player
    */
   public Player getPlayer() {
      return this.player;
   }
   
   public void run() {
      String poolFile = config.getProperty(ConfigKeys.MUSIC_POOL);
      PlaylistManager.INSTANCE.start(new File(poolFile));
   }

   public static void main(String[] args) {
      if (args.length < 1) {
         System.out.println("Missing Config");
         System.exit(1);
      }
      PartyDJ partyDJ = new PartyDJ(Config.create(args[0]));
      partyDJ.run();
   }
   
}
