/***
 **  @(#) TradeCard.com 1.0
 **
 **  Copyright (c) 2007 TradeCard, Inc. All Rights Reserved.
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
 **  @author Copyright (c) 2007 TradeCard, Inc. All Rights Reserved.
 **
 **/
/*
 * Modified and repackage from org.json.simple per license @ http://www.json.org/license.html
 *
 * Copyright (c) 2002 JSON.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * The Software shall be used for Good, not Evil.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.partydj.util.json;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.*;
import com.google.common.collect.*;
import com.partydj.util.*;

/**
 * A JSONObject is an unordered collection of name/value pairs. Its
 * external form is a string wrapped in curly braces with colons between the
 * names and values, and commas between the values and names. The internal form
 * is an object having <code>get</code> and <code>strictGet</code> methods for
 * accessing the values by name, and <code>put</code> methods for adding or
 * replacing values by name. The values can be any of these types:
 * <code>Boolean</code>, <code>JSONArray</code>, <code>JSONObject</code>,
 * <code>Number</code>, <code>String</code>, or the <code>JSONObject.NULL</code>
 * object. A JSONObject constructor can be used to convert an external form
 * JSON text into an internal form whose values can be retrieved with the
 * <code>get</code> and <code>strictGet</code> methods, or to convert values into a
 * JSON text using the <code>put</code> and <code>toString</code> methods.
 * A <code>strictGet</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. A <code>get</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>strictGet()</code> methods return an
 * object, which you can cast or query for type. There are also typed
 * <code>get</code> and <code>strictGet</code> methods that do type checking and type
 * coersion for you.
 * <p>
 * The <code>put</code> methods adds values to an object. For example, <pre>
 *     myString = new JSONObject().put("JSON", "Hello, World!").toString();</pre>
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * the JSON sysntax rules.
 * The constructors are more forgiving in the texts they will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 *     before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 *     quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 *     or single quote, and if they do not contain leading or trailing spaces,
 *     and if they do not contain any of these characters:
 *     <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers
 *     and if they are not the reserved words <code>true</code>,
 *     <code>false</code>, or <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=></code> as well as
 *     by <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code> <small>(semicolon)</small> as
 *     well as by <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0-</code> <small>(octal)</small> or
 *     <code>0x-</code> <small>(hex)</small> prefix.</li>
 * <li>Comments written in the slashshlash, slashstar, and hash conventions
 *     will be ignored.</li>
 * </ul>
 * @author JSON.org
 * @version 2
 */
public class JSONObject extends ForwardingMap<String, Object> {

   private final Map<String, Object> delegate;
   private Object peer; //Handle to wrapping facade object
   
   /**
    * JSONObject.NULL is equivalent to the value that JavaScript calls null,
    * whilst Java's null is equivalent to the value that JavaScript calls
    * undefined
    */
   private static final class Null {

      /**
       * There is only intended to be a single instance of the NULL object,
       * so the clone method returns itself.
       * @return     NULL.
       */
      @Override protected final Object clone() {
         return this;
      }

      /**
       * A Null object is equal to the null value and to itself.
       * @param object    An object to test for nullness.
       * @return true if the object parameter is the JSONObject.NULL object
       *  or null.
       */
      @Override public boolean equals(Object object) {
         return object == null || object == this;
      }

      /**
       * Get the "null" string value.
       * @return The string "null".
       */
      @Override public String toString() {
         return "null";
      }

      @Override public int hashCode() {
         return 0;
      }
   }

   /**
    * It is sometimes more convenient and less ambiguous to have a
    * <code>NULL</code> object than to use Java's <code>null</code> value.
    * <code>JSONObject.NULL.equals(null)</code> returns <code>true</code>.
    * <code>JSONObject.NULL.toString()</code> returns <code>"null"</code>.
    */
   public static final Object NULL = new Null();

   /**
    * Construct an empty JSONObject.
    */
   public JSONObject() { //this should probably be private also...
      this(false);
      
   }
   
   private JSONObject(boolean sortedKeys) {
      this.delegate = (Map)(sortedKeys ? Maps.newTreeMap() : Maps.newLinkedHashMap());
   }
   
   
   /**
    * Construct a JSONObject from a subset of another JSONObject.
    * An array of strings is used to identify the keys that should be copied.
    * Missing keys are ignored.
    * @param jo A JSONObject.
    * @param names An array of strings.
    * @exception JSONException If a value is a non-finite number.
    */
   public static JSONObject newInstance(JSONObject jo, String[] names) throws JSONException {
      JSONObject result = new JSONObject();
      for (int i = 0; i < names.length; i += 1) {
         String key = names[i];
         Object value = jo.get(key);
         if (key != null && value != null) {
            result.put(key, value);
         }
      }
      return result;
   }

   /**
    * create a JSONObject from a reader
    */
   public static JSONObject newInstance(Reader reader) throws IOException {
      return newInstance(reader, false);
   }
   public static JSONObject newInstance(Reader reader, boolean sortKeys) throws IOException {
      return newInstance(new ChunkedCharBuffer().append(reader), sortKeys);
   }

   public static JSONObject newSortedKeyInstance() throws JSONException {
      return new JSONObject(true);
   }
   
   /**
    * Construct a JSONObject from an Object using bean getters.
    * It reflects on all of the public methods of the object.
    * For each of the methods with no parameters and a name starting
    * with <code>"get"</code> or <code>"is"</code> followed by an uppercase letter,
    * the method is invoked, and a key and the value returned from the getter method
    * are put into the new JSONObject.
    *
    * The key is formed by removing the <code>"get"</code> or <code>"is"</code> prefix. If the second remaining
    * character is not upper case, then the first
    * character is converted to lower case.
    *
    * For example, if an object has a method named <code>"getName"</code>, and
    * if the result of calling <code>object.getName()</code> is <code>"Larry Fine"</code>,
    * then the JSONObject will contain <code>"name": "Larry Fine"</code>.
    *
    * @param bean An object that has getter methods that should be used
    * to make a JSONObject.
    */
//   private JSONObject(Object bean) { //should this be deleted?
//      this();
//      Class klass = bean.getClass();
//      Method[] methods = klass.getMethods();
//      for (int i = 0; i < methods.length; i += 1) {
//         try {
//            Method method = methods[i];
//            String name = method.getName();
//            String key = "";
//            if (name.startsWith("get")) {
//               key = name.substring(3);
//            } else if (name.startsWith("is")) {
//               key = name.substring(2);
//            }
//            if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0) {
//               if (key.length() == 1) {
//                  key = key.toLowerCase();
//               } else if (!Character.isUpperCase(key.charAt(1))) {
//                  key = key.substring(0, 1).toLowerCase() + key.substring(1);
//               }
//               this.put(key, method.invoke(bean, (Object[])null));
//            }
//         } catch (Exception e) {
//            /* forget about it */
//         }
//      }
//   }

   /**
    * Construct a JSONObject from an Object, using reflection to find the
    * public members. The resulting JSONObject's keys will be the strings
    * from the names array, and the values will be the field values associated
    * with those keys in the object. If a key is not found or not visible,
    * then it will not be copied into the new JSONObject.
    * @param object An object that has fields that should be used to make a
    * JSONObject.
    * @param names An array of strings, the names of the fields to be obtained
    * from the object.
    */
   public static JSONObject newInstance(Object object, String names[]) {
      JSONObject result = new JSONObject();
      Class c = object.getClass();
      for (int i = 0; i < names.length; i += 1) {
         String name = names[i];
         try {
            Field field = c.getField(name);
            Object value = field.get(object);
            result.put(name, value);
         } catch (Exception e) {
            /* forget about it */
         }
      }
      return result;
   }

   /**
    * Construct a JSONObject from a source JSON text string.
    * This is the most commonly used JSONObject constructor.
    * @param source    A string beginning
    *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
    *  with <code>}</code>&nbsp;<small>(right brace)</small>.
    * @exception JSONException If there is a syntax error in the source string.
    */
   public static JSONObject newInstance(CharSequence source) throws JSONException {
      return newInstance(source, false);
   }
   public static JSONObject newInstance(CharSequence source, boolean sortKeys) throws JSONException {
      JSONObject result = new JSONObject(sortKeys);
      if (source != null) {
         source = source.toString().trim();
         if (source.length() > 0) {
            result.parse(new JSONTokener(source));
         }
      }
      return result;
   }
   
   /**
    * Construct a JSONObject from a subset of another JSONObject.
    * An array of strings is used to identify the keys that should be copied.
    * Missing keys are ignored.
    * @param jo A JSONObject.
    * @param names An array of strings.
    * @exception JSONException If a value is a non-finite number.
    */
   public static JSONObject newInstance(Map other) throws JSONException {
      JSONObject result = new JSONObject();
      result.putAll(other);
      return result;
   }

   /**
    * Construct a JSONObject from a JSONTokener.
    * @param x A JSONTokener object containing the source string.
    * @throws JSONException If there is a syntax error in the source string.
    */
   public static JSONObject newInstance(JSONTokener x) throws JSONException {
      JSONObject result = new JSONObject();
      result.parse(x);
      return result;
   }

   private void parse(JSONTokener x) {
      char c;
      String key;

      if (x.nextClean() != '{') {
         throw x.syntaxError("A JSONObject text must begin with '{'");
      }
      for (;;) {
         c = x.nextClean();
         switch (c) {
            case 0 :
               throw x.syntaxError("A JSONObject text must end with '}'");
            case '}' :
               return;
            default :
               x.back();
               key = x.nextValue().toString();
         }

         /*
          * The key is followed by ':'. We will also tolerate '=' or '=>'.
          */

         c = x.nextClean();
         if (c == '=') {
            if (x.next() != '>') {
               x.back();
            }
         } else if (c != ':') {
            throw x.syntaxError("Expected a ':' after a key");
         }
         put(key, x.nextValue());

         /*
          * Pairs are separated by ','. We will also tolerate ';'.
          */

         switch (x.nextClean()) {
            case ';' :
            case ',' :
               if (x.nextClean() == '}') {
                  return;
               }
               x.back();
               break;
            case '}' :
               return;
            default :
               throw x.syntaxError("Expected a ',' or '}'");
         }
      }
   }

   
   @Override protected Map<String, Object> delegate() {
      return delegate;
   }

   /**
    * Accumulate values under a key. It is similar to the put method except
    * that if there is already an object stored under the key then a
    * JSONArray is stored under the key to hold all of the accumulated values.
    * If there is already a JSONArray, then the new value is appended to it.
    * In contrast, the put method replaces the previous value.
    * @param key   A key string.
    * @param value An object to be accumulated under the key.
    * @return this.
    * @throws JSONException If the value is an invalid number
    *  or if the key is null.
    */
   public JSONObject accumulate(String key, Object value) throws JSONException {
      testValidity(value);
      Object o = get(key);
      if (o == null) {
         put(key, value instanceof JSONArray ? new JSONArray((JSONArray)value) : value);
      } else if (o instanceof JSONArray) {
         ((JSONArray)o).add(value);
      } else {
         put(key, new JSONArray(Arrays.asList(new Object[]{o, value})));
      }
      return this;
   }

   /**
    * Append values to the array under a key. If the key does not exist in the
    * JSONObject, then the key is put in the JSONObject with its value being a
    * JSONArray containing the value parameter. If the key was already
    * associated with a JSONArray, then the value parameter is appended to it.
    * @param key   A key string.
    * @param value An object to be accumulated under the key.
    * @return this.
    * @throws JSONException If the key is null or if the current value
    *  associated with the key is not a JSONArray.
    */
   public JSONObject append(String key, Object value) throws JSONException {
      testValidity(value);
      Object o = get(key);
      if (o == null) {
         put(key, new JSONArray().add(value));
      } else if (o instanceof JSONArray) {
         put(key, ((JSONArray)o).add(value));
      } else {
         throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
      }
      return this;
   }

   /**
    * Produce a string from a double. The string "null" will be returned if
    * the number is not finite.
    * @param  d A double.
    * @return A String.
    */
   static public String doubleToString(double d) {
      if (Double.isInfinite(d) || Double.isNaN(d)) {
         return "null";
      }
      // Shave off trailing zeros and decimal point, if possible.
      String s = Double.toString(d);
      if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
         int endIndex = s.length() - 1;
         while (s.charAt(endIndex) == '0' || s.charAt(endIndex) == '.') {
            endIndex--;
         }
         s = s.substring(0, endIndex);
      }
      return s;
   }

   /**
    * Get the value object associated with a key.
    *
    * @param key   A key string.
    * @return      The object associated with the key.
    * @throws   JSONException if the key is not found.
    */
   public Object strictGet(String key) throws JSONException {
      Object o = get(key);
      if (o == null) {
         throw new JSONException("JSONObject[" + quote(key) + "] not found.");
      }
      return o;
   }

   /**
    * Get the boolean value associated with a key.
    *
    * @param key   A key string.
    * @return      The truth.
    * @throws   JSONException
    *  if the value is not a Boolean or the String "true" or "false".
    */
   public boolean strictGetBoolean(String key) throws JSONException {
      Object o = strictGet(key);
      if (o.equals(Boolean.FALSE) || (o instanceof String && ((String)o).equalsIgnoreCase("false"))) {
         return false;
      } else if (o.equals(Boolean.TRUE) || (o instanceof String && ((String)o).equalsIgnoreCase("true"))) {
         return true;
      }
      throw new JSONException("JSONObject[" + quote(key) + "] is not a Boolean.");
   }

   /**
    * Get the double value associated with a key.
    * @param key   A key string.
    * @return      The numeric value.
    * @throws JSONException if the key is not found or
    *  if the value is not a Number object and cannot be converted to a number.
    */
   public double strictGetDouble(String key) throws JSONException {
      Object o = strictGet(key);
      try {
         return o instanceof Number ? ((Number)o).doubleValue() : Double.valueOf((String)o).doubleValue();
      } catch (Exception e) {
         throw new JSONException("JSONObject[" + quote(key) + "] is not a number.");
      }
   }

   /**
    * Get the int value associated with a key. If the number value is too
    * large for an int, it will be clipped.
    *
    * @param key   A key string.
    * @return      The integer value.
    * @throws   JSONException if the key is not found or if the value cannot
    *  be converted to an integer.
    */
   public int strictGetInt(String key) throws JSONException {
      Object o = strictGet(key);
      return o instanceof Number ? ((Number)o).intValue() : (int)strictGetDouble(key);
   }

   /**
    * Get the JSONArray value associated with a key.
    *
    * @param key   A key string.
    * @return      A JSONArray which is the value.
    * @throws   JSONException if the key is not found or
    *  if the value is not a JSONArray.
    */
   public JSONArray strictGetJSONArray(String key) throws JSONException {
      Object o = strictGet(key);
      if (o instanceof JSONArray) {
         return (JSONArray)o;
      }
      throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONArray.");
   }

   /**
    * Get the JSONObject value associated with a key.
    *
    * @param key   A key string.
    * @return      A JSONObject which is the value.
    * @throws   JSONException if the key is not found or
    *  if the value is not a JSONObject.
    */
   public JSONObject strictGetJSONObject(String key) throws JSONException {
      Object o = strictGet(key);
      if (o instanceof JSONObject) {
         return (JSONObject)o;
      }
      throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONObject.");
   }

   /**
    * Get the long value associated with a key. If the number value is too
    * long for a long, it will be clipped.
    *
    * @param key   A key string.
    * @return      The long value.
    * @throws   JSONException if the key is not found or if the value cannot
    *  be converted to a long.
    */
   public long strictGetLong(String key) throws JSONException {
      Object o = strictGet(key);
      return o instanceof Number ? ((Number)o).longValue() : (long)strictGetDouble(key);
   }

   /**
    * Get an array of field names from a JSONObject.
    *
    * @return An array of field names, or null if there are no names.
    */
   public static String[] getNames(JSONObject json) {
      int length = json.size();
      if (length == 0) {
         return null;
      }
      return json.keySet().toArray(new String[length]);
   }

   /**
    * Get an array of field names from an Object.
    *
    * @return An array of field names, or null if there are no names.
    */
   public static String[] getNames(Object object) {
      if (object == null) {
         return null;
      }
      Class klass = object.getClass();
      Field[] fields = ClassFieldWalker.getFields(klass);
      int length = fields.length;
      if (length == 0) {
         return null;
      }
      String[] names = new String[length];
      for (int i = 0; i < length; i += 1) {
         names[i] = fields[i].getName();
      }
      return names;
   }
   
   private static class ClassFieldWalker {
      static Map<Class, Field[]> cachedFields = new ConcurrentHashMap();

      public static void resetCache() {
         cachedFields = new ConcurrentHashMap();
      }
      
      /**
       * Return an array of visible fields for a class.
       * (Order is consistent but non-deterministic)
       */
      public static Field[] getFields(Class c) {
         //Check cache before starting recursive search
         Field[] cachedAnswer = cachedFields.get(c);
         if (cachedAnswer != null) {
            return cachedAnswer;
         }
         
         
         Set<Field> fields = new LinkedHashSet();
         findFields(c, fields, new HashSet(), true);
         
         return fields.toArray(new Field[fields.size()]);
      }

      /**
       * Return true if we didn't 'skip' any classes due to traversal
       */
      private static boolean findFields(Class clazz, Set<Field> fields, Set<Class> traversed, boolean skipCacheCheck) {
         if (traversed.contains(clazz)) {
            return false;
         } else {
            traversed.add(clazz);
         }

         //Check cache unless this is the first call (aka - skip cache check)
         if (!skipCacheCheck && cachedFields.containsKey(clazz)) {
            fields.addAll(Arrays.asList(cachedFields.get(clazz)));
            return true;
         }
         
         Set<Field> thisClassFields = new HashSet();
         
         //Get declared fields first
         thisClassFields.addAll(filterNonVisibleFields(clazz.getDeclaredFields()));

         boolean cache = true;
         
         //Recurse the supers
         Class[] interfaces = clazz.getInterfaces();
         for (Class inter : interfaces) {
            if (!findFields(inter, thisClassFields, traversed, false)) {
               cache = false;
            }
         }

         //Recurse the super
         if (!clazz.isInterface()) {
            Class superC = clazz.getSuperclass();
            if (superC != null) {
               if (!findFields(superC, thisClassFields, traversed, false)) {
                  cache = false;
               }
            }
         }
         
         //Only cache if we didn't 'skip' any classes during our walk
         if (cache) {
            cachedFields.put(clazz, thisClassFields.toArray(new Field[thisClassFields.size()]));
         }

         fields.addAll(thisClassFields);
         
         return true;
      }

      private static Collection<Field> filterNonVisibleFields(Field[] allFields) {
         if (allFields != null) {
            List<Field> filtered = new ArrayList(allFields.length);
            for (Field field : allFields) {
               if (Modifier.isPublic(field.getModifiers())) {
                  filtered.add(field);
               }
            }
            return filtered;
         } else {
            return Collections.emptyList();
         }
      }

   }

   /**
    * Get the string associated with a key.
    *
    * @param key   A key string.
    * @return      A string which is the value.
    * @throws   JSONException if the key is not found.
    */
   public String getStringStrict(String key) throws JSONException {
      return strictGet(key).toString();
   }

   /**
    * Determine if the JSONObject contains a specific key.
    * @param key   A key string.
    * @return      true if the key exists in the JSONObject.
    */
   public boolean has(String key) {
      return this.containsKey(key);
   }

   /**
    * Determine if the value associated with the key is null or if there is
    *  no value.
    * @param key   A key string.
    * @return      true if there is no value associated with the key or if
    *  the value is the JSONObject.NULL object.
    */
   public boolean isNull(String key) {
      return JSONObject.NULL.equals(get(key));
   }

   /**
    * Produce a JSONArray containing the names of the elements of this
    * JSONObject.
    * @return A JSONArray containing the key strings, or null if the JSONObject
    * is empty.
    */
   public JSONArray names() {
      JSONArray ja = new JSONArray();
      Iterator keys = keySet().iterator();
      while (keys.hasNext()) {
         ja.add(keys.next());
      }
      return ja.size() == 0 ? null : ja;
   }

   /**
    * Produce a string from a Number.
    * @param  n A Number
    * @return A String.
    * @throws JSONException If n is a non-finite number.
    */
   static public String numberToString(Number n) throws JSONException {
      if (n == null) {
         throw new JSONException("Null pointer");
      }
      testValidity(n);
      //Serialize BigDecimals as Strings, rest as numbers
      if (n instanceof BigDecimal) {
         //return quote(numberToString(n.toString()));
         return ((BigDecimal)n).toPlainString();
      } else {
         return numberToString(n.toString());
      }
   }
   
   /**
    * Produce a string from a Number.
    * @param  n A Number
    * @return A String.
    * @throws JSONException If n is a non-finite number.
    */
   static public String numberToString(String s) throws JSONException {
      // Shave off trailing zeros and decimal point, if possible.
      if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
         while (s.endsWith("0")) {
            s = s.substring(0, s.length() - 1);
         }
         if (s.endsWith(".")) {
            s = s.substring(0, s.length() - 1);
         }
      }
      return s;
   }

   /**
    * Get an optional value associated with a key.
    * @param key   A key string.
    * @return      An object which is the value, or null if there is no value.
    */
   @Override public Object get(Object key) {
      return key == null ? null : super.get(key);
   }

   /**
    * Get an optional boolean associated with a key.
    * It returns false if there is no such key, or if the value is not
    * Boolean.TRUE or the String "true".
    *
    * @param key   A key string.
    * @return      The truth.
    */
   public boolean getBoolean(String key) {
      return getBoolean(key, false);
   }

   /**
    * Get an optional boolean associated with a key.
    * It returns the defaultValue if there is no such key, or if it is not
    * a Boolean or the String "true" or "false" (case insensitive).
    *
    * @param key              A key string.
    * @param defaultValue     The default.
    * @return      The truth.
    */
   public boolean getBoolean(String key, boolean defaultValue) {
      try {
         return strictGetBoolean(key);
      } catch (Exception e) {
         return defaultValue;
      }
   }

   /**
    * Get an optional double associated with a key,
    * or NaN if there is no such key or if its value is not a number.
    * If the value is a string, an attempt will be made to evaluate it as
    * a number.
    *
    * @param key   A string which is the key.
    * @return      An object which is the value.
    */
   public double getDouble(String key) {
      return getDouble(key, Double.NaN);
   }

   /**
    * Get an optional double associated with a key, or the
    * defaultValue if there is no such key or if its value is not a number.
    * If the value is a string, an attempt will be made to evaluate it as
    * a number.
    *
    * @param key   A key string.
    * @param defaultValue     The default.
    * @return      An object which is the value.
    */
   public double getDouble(String key, double defaultValue) {
      try {
         Object o = get(key);
         return o instanceof Number ? ((Number)o).doubleValue() : new Double((String)o).doubleValue();
      } catch (Exception e) {
         return defaultValue;
      }
   }
   
   /**
    * Get an optional BigDecimal associated with a key,
    * or NaN if there is no such key or if its value is not a number.
    * If the value is a string, an attempt will be made to evaluate it as
    * a number.
    *
    * @param key   A string which is the key.
    * @return      An object which is the value.
    */
   public BigDecimal getBigDecimal(String key) {
      Object o = get(key);
      if (o != null) {
         if (o instanceof BigDecimal) {
            return (BigDecimal)o;
         } else if (o instanceof Number) {
            //Replace with BigDecimal for performance
            BigDecimal result = BigDecimal.valueOf(((Number)o).doubleValue());
            put(key, result);
            return result;
         } else {
            //Replace with BigDecimal for performance
            BigDecimal result = new BigDecimal(o.toString());
            put(key, result);
            return result;
         }
      } else {
         return null;
      }
   }

   /**
    * Get an optional int value associated with a key,
    * or zero if there is no such key or if the value is not a number.
    * If the value is a string, an attempt will be made to evaluate it as
    * a number.
    *
    * @param key   A key string.
    * @return      An object which is the value.
    */
   public int getInt(String key) {
      return getInt(key, 0);
   }

   /**
    * Get an optional int value associated with a key,
    * or the default if there is no such key or if the value is not a number.
    * If the value is a string, an attempt will be made to evaluate it as
    * a number.
    *
    * @param key   A key string.
    * @param defaultValue     The default.
    * @return      An object which is the value.
    */
   public int getInt(String key, int defaultValue) {
      try {
         return strictGetInt(key);
      } catch (Exception e) {
         return defaultValue;
      }
   }

   /**
    * Get an optional JSONArray associated with a key.
    * It returns null if there is no such key, or if its value is not a
    * JSONArray.
    *
    * @param key   A key string.
    * @return      A JSONArray which is the value.
    */
   public JSONArray getJSONArray(String key) {
      Object o = get(key);
      return o instanceof JSONArray ? (JSONArray)o : null;
   }

   /**
    * Get an optional JSONObject associated with a key.
    * It returns null if there is no such key, or if its value is not a
    * JSONObject.
    *
    * @param key   A key string.
    * @return      A JSONObject which is the value.
    */
   public JSONObject getJSONObject(String key) {
      Object o = get(key);
      return o instanceof JSONObject ? (JSONObject)o : null;
   }

   /**
    * Get an optional long value associated with a key,
    * or zero if there is no such key or if the value is not a number.
    * If the value is a string, an attempt will be made to evaluate it as
    * a number.
    *
    * @param key   A key string.
    * @return      An object which is the value.
    */
   public long getLong(String key) {
      return getLong(key, 0);
   }

   /**
    * Get an optional long value associated with a key,
    * or the default if there is no such key or if the value is not a number.
    * If the value is a string, an attempt will be made to evaluate it as
    * a number.
    *
    * @param key   A key string.
    * @param defaultValue     The default.
    * @return      An object which is the value.
    */
   public long getLong(String key, long defaultValue) {
      try {
         return strictGetLong(key);
      } catch (Exception e) {
         return defaultValue;
      }
   }

   /**
    * Get an optional string associated with a key.
    * It returns an empty string if there is no such key. If the value is not
    * a string and is not null, then it is coverted to a string.
    *
    * @param key   A key string.
    * @return      A string which is the value.
    */
   public String getString(String key) {
      return getString(key, "");
   }

   /**
    * Get an optional string associated with a key.
    * It returns the defaultValue if there is no such key.
    *
    * @param key   A key string.
    * @param defaultValue     The default.
    * @return      A string which is the value.
    */
   public String getString(String key, String defaultValue) {
      Object o = get(key);
      return o != null ? o.toString() : defaultValue;
   }

   /**
    * Put a key/boolean pair in the JSONObject.
    *
    * @param key   A key string.
    * @param value A boolean which is the value.
    * @return this.
    * @throws JSONException If the key is null.
    */
   public JSONObject put(String key, boolean value) throws JSONException {
      put(key, value ? Boolean.TRUE : Boolean.FALSE);
      return this;
   }

   /**
    * Put a key/double pair in the JSONObject.
    *
    * @param key   A key string.
    * @param value A double which is the value.
    * @return this.
    * @throws JSONException If the key is null or if the number is invalid.
    */
   public JSONObject put(String key, double value) throws JSONException {
      put(key, new Double(value));
      return this;
   }

   /**
    * Put a key/int pair in the JSONObject.
    *
    * @param key   A key string.
    * @param value An int which is the value.
    * @return this.
    * @throws JSONException If the key is null.
    */
   public JSONObject put(String key, int value) throws JSONException {
      put(key, new Integer(value));
      return this;
   }

   /**
    * Put a key/long pair in the JSONObject.
    *
    * @param key   A key string.
    * @param value A long which is the value.
    * @return this.
    * @throws JSONException If the key is null.
    */
   public JSONObject put(String key, long value) throws JSONException {
      put(key, new Long(value));
      return this;
   }

   /**
    * Put a key/value pair in the JSONObject. If the value is null,
    * then the key will be removed from the JSONObject if it is present.
    * @param key   A key string.
    * @param value An object which is the value. It should be of one of these
    *  types: Boolean, Double, Integer, JSONArray, JSONObject, Long, String,
    *  or the JSONObject.NULL object.
    * @return this.
    * @throws JSONException If the value is non-finite number
    *  or if the key is null.
    */
   @Override public Object put(String key, Object value) {
      if (key == null) {
         throw new JSONException("Null key.");
      }
      if (value != null) {
         testValidity(value);
         return super.put(key, wrapIfNeeded(value));
      } else {
         return remove(key);
      }
   }

   /**
    * Wrap objects in their JSON wrappers if required
    */
   protected Object wrapIfNeeded(Object src) {
      if (src instanceof Collection && !(src instanceof JSONArray)) {
         return new JSONArray((Collection)src);
      } else if (src instanceof Map && !(src instanceof JSONObject)) {
         return newInstance((Map)src);
      } else {
         return src;
      }
   }
   
   /**
    * Produce a string in double quotes with backslash sequences in all the
    * right places. A backslash will be inserted within </, allowing JSON
    * text to be delivered in HTML. In JSON text, a string cannot contain a
    * control character or an unescaped quote or backslash.
    * @param string A String
    * @return  A String correctly formatted for insertion in a JSON text.
    */
   public static String quote(String string) {
      if (string == null || string.length() == 0) {
         return "\"\"";
      }

      char b;
      char c = 0;
      int i;
      int len = string.length();
      StringBuffer sb = new StringBuffer(len + 4);
      String t;

      sb.append('"');
      for (i = 0; i < len; i += 1) {
         b = c;
         c = string.charAt(i);
         switch (c) {
            case '\\' :
            case '"' :
               sb.append('\\');
               sb.append(c);
               break;
            case '\'':
               sb.append('\\');
               sb.append(c);
               break;
            case '/' :
               if (b == '<') {
                  sb.append('\\');
               }
               sb.append(c);
               break;
            case '\b' :
               sb.append("\\b");
               break;
            case '\t' :
               sb.append("\\t");
               break;
            case '\n' :
               sb.append("\\n");
               break;
            case '\f' :
               sb.append("\\f");
               break;
            case '\r' :
               sb.append("\\r");
               break;
            case ',' :
               sb.append("\\u002c");
               break;
            default :
               if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
                  t = "000" + Integer.toHexString(c);
                  sb.append("\\u" + t.substring(t.length() - 4));
               } else {
                  sb.append(c);
               }
         }
      }
      sb.append('"');
      return sb.toString();
   }

   /**
    * Produce a string in double quotes with backslash sequences in all the
    * right places. A backslash will be inserted within </, allowing JSON
    * text to be delivered in HTML. In JSON text, a string cannot contain a
    * control character or an unescaped quote or backslash.
    * @param string A String
    * @return  A String correctly formatted for insertion in a JSON text.
    */
   public static void quote(Writer out, Reader in) {
      try {
         if (in == null) {
            out.write("\"\"");
         }
         char prev;
         char cur = 0;
         int asInt = 0;
         String t;

         out.write('"');
         asInt = in.read();
         while (asInt != -1) {
            prev = cur;
            cur = (char)asInt;
            switch (cur) {
               case '\\' :
               case '"' :
                  out.write('\\');
                  out.write(cur);
                  break;
               case '/' :
                  if (prev == '<') {
                     out.write('\\');
                  }
                  out.write(cur);
                  break;
               case '\b' :
                  out.write("\\b");
                  break;
               case '\t' :
                  out.write("\\t");
                  break;
               case '\n' :
                  out.write("\\n");
                  break;
               case '\f' :
                  out.write("\\f");
                  break;
               case '\r' :
                  out.write("\\r");
                  break;
               case ',' :
                  out.write("\\u002c");
                  break;
               default :
                  if (cur < ' ' || (cur >= '\u0080' && cur < '\u00a0') || (cur >= '\u2000' && cur < '\u2100')) {
                     t = "000" + Integer.toHexString(cur);
                     out.write("\\u" + t.substring(t.length() - 4));
                  } else {
                     out.write(cur);
                  }
            }
            asInt = in.read();
         }
         out.write('"');
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Throw an exception if the object is not allowed in a JSONObject.
    * Numbers cannot be NaN or infinite.
    * @param o The object to test.
    * @throws JSONException If o is a non-finite number.
    */
   static void testValidity(Object o) throws JSONException {
      if (o != null) {
         if (o instanceof Double) {
            if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
               throw new JSONException("JSON does not allow non-finite numbers.");
            }
         } else if (o instanceof Float) {
            if (((Float)o).isInfinite() || ((Float)o).isNaN()) {
               throw new JSONException("JSON does not allow non-finite numbers.");
            }
         }
      }
   }

   /**
    * Make a JSON text of this JSONObject. For compactness, no whitespace
    * is added. If this would not result in a syntactically correct JSON text,
    * then null will be returned instead.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @return a printable, displayable, portable, transmittable
    *  representation of the object, beginning
    *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
    *  with <code>}</code>&nbsp;<small>(right brace)</small>.
    */
   @Override public String toString() {
      return toChunkedCharBuffer().toString();
   }
   public ChunkedCharBuffer toChunkedCharBuffer() {
      try {
         ChunkedCharBuffer buf = new ChunkedCharBuffer(); //Try to keep only one copy in contigous memory
         Writer out = buf.toWriter();
         
         Iterator<Map.Entry> entries = (Iterator)entrySet().iterator();
         boolean writeComma = false;
         out.write("{");

         while (entries.hasNext()) {
            if (writeComma) {
               out.write(',');
            }
            Map.Entry o = entries.next();
            out.write(quote(o.getKey().toString()));
            out.write(':');
            valueToWriter(out, o.getValue());
            writeComma = true;
         }
         out.write('}');
         out.close();
         return buf;
      } catch (Exception e) {
         return null;
      }
   }

   public String prettyPrint() {
      return toString(3);
   }

   /**
    * Make a prettyprinted JSON text of this JSONObject.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    * @param indentFactor The number of spaces to add to each level of
    *  indentation.
    * @return a printable, displayable, portable, transmittable
    *  representation of the object, beginning
    *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
    *  with <code>}</code>&nbsp;<small>(right brace)</small>.
    * @throws JSONException If the object contains an invalid number.
    */
   public String toString(int indentFactor) throws JSONException {
      return toString(indentFactor, 0);
   }

   /**
    * Make a prettyprinted JSON text of this JSONObject.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    * @param indentFactor The number of spaces to add to each level of
    *  indentation.
    * @param indent The indentation of the top level.
    * @return a printable, displayable, transmittable
    *  representation of the object, beginning
    *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
    *  with <code>}</code>&nbsp;<small>(right brace)</small>.
    * @throws JSONException If the object contains an invalid number.
    */
   String toString(int indentFactor, int indent) throws JSONException {
      int i;
      int n = size();
      if (n == 0) {
         return "{}";
      }
      Iterator keys = keySet().iterator();
      StringBuffer sb = new StringBuffer("{");
      int newindent = indent + indentFactor;
      Object o;
      if (n == 1) {
         o = keys.next();
         sb.append(quote(o.toString()));
         sb.append(": ");
         sb.append(valueToString(this.get(o), indentFactor, indent));
      } else {
         while (keys.hasNext()) {
            o = keys.next();
            if (sb.length() > 1) {
               sb.append(",\n");
            } else {
               sb.append('\n');
            }
            for (i = 0; i < newindent; i += 1) {
               sb.append(' ');
            }
            sb.append(quote(o.toString()));
            sb.append(": ");
            sb.append(valueToString(this.get(o), indentFactor, newindent));
         }
         if (sb.length() > 1) {
            sb.append('\n');
            for (i = 0; i < indent; i += 1) {
               sb.append(' ');
            }
         }
      }
      sb.append('}');
      return sb.toString();
   }

   /**
    * Make a JSON text of an Object value. If the object has an
    * value.toJSONString() method, then that method will be used to produce
    * the JSON text. The method is required to produce a strictly
    * conforming text. If the object does not contain a toJSONString
    * method (which is the most common case), then a text will be
    * produced by other means. If the value is an array or Collection,
    * then a JSONArray will be made from it and its toJSONString method
    * will be called. If the value is a MAP, then a JSONObject will be made
    * from it and its toJSONString method will be called. Otherwise, the
    * value's toString method will be called, and the result will be quoted.
    *
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    * @param value The value to be serialized.
    * @return a printable, displayable, transmittable
    *  representation of the object, beginning
    *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
    *  with <code>}</code>&nbsp;<small>(right brace)</small>.
    * @throws JSONException If the value is or contains an invalid number.
    */
   static void valueToWriter(Writer out, Object value) throws JSONException {
      try {
         if (value == null || value.equals(null)) {
            out.write("null");
            return;
         }
         if (value instanceof JSONString) {
            Object o;
            try {
               o = ((JSONString)value).toJSONString();
            } catch (Exception e) {
               throw new JSONException(e);
            }
            if (o instanceof String) {
               out.write((String)o);
               return;
            }
            throw new JSONException("Bad value from toJSONString: " + o);
         }
         if (value instanceof Number) {
            out.write(numberToString((Number)value));
            return;
         }
         if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray) {
            out.write(value.toString());
            return;
         }
         if (value instanceof Map) {
            newInstance((Map)value).write(out);
            return;
         }
         if (value instanceof Collection) {
            new JSONArray((Collection)value).write(out);
            return;
         }
         if (value.getClass().isArray()) {
            new JSONArray(value).write(out);
            return;
         }
         if (value instanceof ChunkedCharBuffer) {
            quote(out, ((ChunkedCharBuffer)value).toReader());
            return;
         }
         if (value instanceof Reader) {
            quote(out, ((Reader)value));
            return;
         }
         out.write(quote(value.toString()));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Write a value to string (But try to be as careful with memory as possible)
    */
   public static String valueToString(Object value) {
      ChunkedCharBuffer buf = new ChunkedCharBuffer();
      valueToWriter(buf.toWriter(), value);
      return buf.toString();
   }
   
   /**
    * Make a prettyprinted JSON text of an object value.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    * @param value The value to be serialized.
    * @param indentFactor The number of spaces to add to each level of
    *  indentation.
    * @param indent The indentation of the top level.
    * @return a printable, displayable, transmittable
    *  representation of the object, beginning
    *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
    *  with <code>}</code>&nbsp;<small>(right brace)</small>.
    * @throws JSONException If the object contains an invalid number.
    */
   public static String valueToString(Object value, int indentFactor, int indent) throws JSONException {
      if (value == null || value.equals(null)) {
         return "null";
      }
      try {
         if (value instanceof JSONString) {
            Object o = ((JSONString)value).toJSONString();
            if (o instanceof String) {
               return (String)o;
            }
         }
      } catch (Exception e) {
         /* forget about it */
      }
      if (value instanceof Number) {
         return numberToString((Number)value);
      }
      if (value instanceof Boolean) {
         return value.toString();
      }
      if (value instanceof JSONObject) {
         return ((JSONObject)value).toString(indentFactor, indent);
      }
      if (value instanceof JSONArray) {
         return ((JSONArray)value).toString(indentFactor, indent);
      }
      if (value instanceof Map) {
         return newInstance((Map)value).toString(indentFactor, indent);
      }
      if (value instanceof Collection) {
         return new JSONArray((Collection)value).toString(indentFactor, indent);
      }
      if (value.getClass().isArray()) {
         return new JSONArray(value).toString(indentFactor, indent);
      }
      return quote(value.toString());
   }

   /**
    * Write the contents of the JSONObject as JSON text to a writer.
    * For compactness, no whitespace is added.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @return The writer.
    * @throws JSONException
    */
   public Writer write(Writer writer) throws JSONException {
      try {
         boolean b = false;
         writer.write('{');

         Iterator<Map.Entry<String, Object>> it = entrySet().iterator();
         while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            writer.write(quote(entry.getKey()));
            writer.write(':');
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
               ((JSONObject)value).write(writer);
            } else if (value instanceof JSONArray) {
               ((JSONArray)value).write(writer);
            } else {
               valueToWriter(writer, value);
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