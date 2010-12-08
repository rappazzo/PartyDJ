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
package com.partydj.util;

import java.util.*;

/**
 * @author mrappazz
 *
 * This class is not thread safe.
 **/
public class CircularByteBuffer {
   
   private byte[] buffer;
   private int position = 0;
   
   public CircularByteBuffer() {
      this(64);
   }
   public CircularByteBuffer(int bufferSize) {
      buffer = new byte[bufferSize];
      Arrays.fill(buffer, (byte)-1);
   }
   
   /**
    * get a copy of the data in the buffer (0 indexed)
    */
   public byte[] getCurrentBytes() {
      return getCurrentBytes(buffer.length);
   }
   
   /**
    * get the buffer size
    */
   public int size() {
      return buffer.length;
   }
   
   /**
    * get a copy of the data in the buffer (0 indexed)
    * @param n - get the last n bytes
    */
   public byte[] getCurrentBytes(int n) {
      byte[] curBuffer = new byte[n];
      for (int i = 0; i < n; i++) {
         curBuffer[i] = buffer[(i + (buffer.length - n) + position) % buffer.length];
      }
      return curBuffer;
   }
   
   public void resizeSize(int newSize) {
      if (newSize != buffer.length) {
         byte[] newBuffer = getCurrentBytes(newSize);
         position = 0;
         if (newSize > buffer.length) {
            Arrays.fill(newBuffer, buffer.length - 1, newSize, (byte)-1);
         }
         buffer = newBuffer;
      }
   }
   
   /**
    * add a byte to the buffer
    */
   public CircularByteBuffer append(byte b) {
      buffer[position++] = b;
      if (position >= buffer.length) {
         position = 0;
      }
      return this;
   }
   
   /**
    * add a byte to the buffer
    * @return the byte that was overwritten
    */
   public CircularByteBuffer append(byte[] b) {
      if (b.length >= buffer.length) {
         System.arraycopy(b, b.length - buffer.length, buffer, 0, buffer.length);
         position = 0;
      } else {
         for (int i = 0; i < b.length; i++) {
            buffer[(position + i) % buffer.length] = b[i];
         }
         position = (position + b.length) % buffer.length;
      }
      return this;
   }
   
   /**
    * get the current byte
    */
   public byte currentByte() {
      return buffer[position];
   }
   
   /**
    * get the current byte
    */
   public byte byteAt(int offset) {
      return buffer[(position + offset) % buffer.length];
   }
   
   @Override public boolean equals(Object obj) {
      return isEqualTo(((CircularByteBuffer)obj).getCurrentBytes());
   }
   
   /**
    * return if the given byte array holds the same contents as this buffer
    */
   public boolean isEqualTo(byte[] other) {
      if (other.length != buffer.length) {
         return false;
      }
      for (int i = 0; i < buffer.length; i++) {
         if (this.byteAt(i) != other[i]) {
            return false;
         }
      }
      return true;
   }

}
