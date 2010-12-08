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
import java.util.*;

/**
 * 
 **/
public class Config {

   static Config INSTANCE = null;

   public static final String SEPARATOR = ".";
   public static final String JOIN_SEPARATOR = "\\s*,\\s*";
   
   Properties config;

   static Config create(String configFileName) {
      INSTANCE = new Config();
      try {
         INSTANCE.config = new Properties();
         INSTANCE.config.load(new FileInputStream(configFileName));
      } catch (IOException e) {
         throw new IllegalStateException("Error loading Config File", e);
      }
      return INSTANCE;
   }
   
   public static Config config() {
      return INSTANCE;
   }
   
   /**
    * return a property from the config - empty strings are resolved to null
    */
   public String getProperty(String... key) {
      String value = config.getProperty(buildPropertyKey(key));
      if (value == null || value.length() == 0) {
         value = null;
      }
      return value;
   }
   
   /**
    * build a property key from the given variable key arguements
    */
   public String buildPropertyKey(String... keys) {
      if (keys != null && keys.length > 0) {
         if (keys.length == 1) {
            return keys[0];
         } else {
            StringBuilder propertyKey = new StringBuilder();
            for (String key : keys) {
               if (propertyKey.length() > 0) {
                  propertyKey.append(SEPARATOR);
               }
               propertyKey.append(key);
            }
            return propertyKey.toString();
         }
      }
      return null;
   }
   
   /**
    * return an Integer property from the config
    */
   public Integer getIntegerProperty(String... key) {
      String stringProp = getProperty(buildPropertyKey(key));
      if (stringProp != null) {
         try {
            return Integer.valueOf(stringProp);
         } catch (NumberFormatException e) {
            //ignore
         }
      }
      return null;
   }
   
   /**
    * return an instantiated class from the class name in the config key
    */
   public Object getClassProperty(String... key) {
      String className = getProperty(buildPropertyKey(key));
      if (className != null) {
         try {
            Class clazz = Class.forName(className);
            return clazz.newInstance();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      return null;
   }
   
   /**
    * return a primitive int property from the config
    */
   public int getIntProperty(String... key) {
      Integer integerProp = getIntegerProperty(buildPropertyKey(key));
      return integerProp != null ? integerProp.intValue() : -1;
   }
   
   /**
    * return an array of properties from the config
    */
   public String[] getMultiValueProperty(String... key) {
      String joined = getProperty(buildPropertyKey(key));
      return joined != null ? joined.split(JOIN_SEPARATOR) : null;
   }
   
   /**
    * return a list of properties from the config
    */
   public List<String> getMultiValuePropertyAsList(String... key) {
      String[] array = getMultiValueProperty(buildPropertyKey(key));
      return array != null ? Arrays.asList(array) : null;
   }

   public boolean getBooleanProperty(String... key) {
      return Boolean.valueOf(getProperty(key)).booleanValue();
   }

}
