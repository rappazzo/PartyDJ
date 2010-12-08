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

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * 
 **/
public class NamedThreadFactory implements ThreadFactory {
   
   private static final AtomicInteger poolNumber = new AtomicInteger(1);
   final ThreadGroup group;
   final AtomicInteger threadNumber = new AtomicInteger(1);
   final String namePrefix;
   private boolean daemon = false;
   private int priority = Thread.NORM_PRIORITY;
      
   public static NamedThreadFactory createDaemonFactory(String namePrefix) {
      return new NamedThreadFactory(namePrefix, true);
   }
   
   public static NamedThreadFactory create(String namePrefix) {
      return new NamedThreadFactory(namePrefix, false);
   }
   
   private NamedThreadFactory(String namePrefix) {
      SecurityManager s = System.getSecurityManager();
      this.group = (s != null)? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = namePrefix != null ? namePrefix : "(Un)NamedThreadPool[" + poolNumber.getAndIncrement()+"]";
   }

   private NamedThreadFactory(String namePrefix, boolean daemon) {
      this(namePrefix);
      this.daemon = daemon;
   }

   private NamedThreadFactory(String namePrefix, boolean daemon, int priority) {
      this(namePrefix);
      this.daemon = daemon;
      this.priority = priority;
   }

   @Override public Thread newThread(Runnable r) {
       Thread t = new Thread(group, r, namePrefix +"["+ threadNumber.getAndIncrement()+"]", 0);
       if (t.isDaemon() != daemon) {
           t.setDaemon(daemon);
       }
       if (t.getPriority() != priority) {
           t.setPriority(priority);
       }
       return t;
   }

}