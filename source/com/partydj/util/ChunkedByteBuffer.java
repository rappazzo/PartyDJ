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

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import sun.io.*;

public class ChunkedByteBuffer implements Serializable {

   public static final int DEFAULT_CHUNK_SIZE = 4096;
   public static final int DEFAULT_NUMBER_OF_CHUNKS = 16;
   
   int chunkSize;
   byte array[][];
   int startingChunks;
   int lastChunk;
   int firstFree;
   String charsetName = Charset.forName("ISO-8859-1").name(); // String for serialization

   /**
    * Create a new ChunkedByteBuffer with the passed incremental chunkSize
    * and number of starting chunks.  The number of starting chunks is mearly the
    * size of the chunk holder array - which will have to grow whenever 
    * chunkSize * startingChunks of data is presented.  At that time the growth is
    * simply of the main array
    */
   public ChunkedByteBuffer(int chunkSize, int startingChunks) {
      //rather than assertions just fix bad arguments
      startingChunks = Math.max(startingChunks, 1);
      chunkSize = Math.max(chunkSize, 64);
      lastChunk = 0;
      firstFree = 0;
      array = new byte[startingChunks][];
      this.chunkSize = chunkSize;
      this.startingChunks = startingChunks;
      this.array[0] = new byte[chunkSize];
   }

   public ChunkedByteBuffer(int chunkSize) {
      this(chunkSize, DEFAULT_NUMBER_OF_CHUNKS);
   }

   public ChunkedByteBuffer() {
      this(DEFAULT_CHUNK_SIZE, DEFAULT_NUMBER_OF_CHUNKS);
   }

   public int size() {
      return length();
   }

   public int length() {
      return (lastChunk * chunkSize) + firstFree;
   }

   public int getChunkSize() {
      return chunkSize;
   }

   public Charset getCharset() {
      return Charset.forName(charsetName);
   }

   public void setCharset(Charset charset) {
      if (charset != null) {
         this.charsetName = charset.name();
      }
   }

   /**
    * remove the last byte appended to the buffer.  if the buffer is empty, return -1
    */
   public byte unappend() {
      if (length() <= 0) {
         return -1;
      }
      firstFree--;
      if (firstFree < 0) {
         firstFree = chunkSize - 1;
         lastChunk--;
      }
      return array[lastChunk][firstFree];
   }

   /**
    * remove the last n bytes appended to the buffer.  if the buffer is empty, return an empty array;
    */
   public byte[] unappend(int n) {
      int length = length();
      byte[] value = new byte[n < length ? n : length];
      for (int i = value.length - 1; i >= 0 ; i--) {
         firstFree--;
         if (firstFree < 0) {
            firstFree = chunkSize - 1;
            lastChunk--;
         }
         value[i] = array[lastChunk][firstFree];
      }
      return value;
   }

   /**
    * Append a single byte to the buffer - if there is space on the current
    * chunk then whoopee this is easy - else regrow to make room
    */
   public ChunkedByteBuffer append(byte value) {
      //boolean lastchunk = lastChunk + 1 == array.length;
      byte chunk[];
      if (firstFree < chunkSize) {
         chunk = array[lastChunk];
      } else {
         if (lastChunk + 1 == array.length) {
            expandCapacity();
         }
         chunk = array[++lastChunk];
         if (chunk == null) {
            chunk = array[lastChunk] = new byte[chunkSize];
         }
         firstFree = 0;
      }
      chunk[firstFree++] = value;
      return this;
   }

   /**
    * Convenience method to append a String with default encoding to the buffer
    */
   public ChunkedByteBuffer append(String value) throws UnsupportedEncodingException {
      if (value != null && value.length() > 0) {
         return append(value.getBytes(getCharset().name()));
      } else {
         return this;
      }
   }
   
   /**
    * Convenience method to append a String to the buffer
    */
   public ChunkedByteBuffer append(String value, String enc) throws UnsupportedEncodingException {
      if (value != null && value.length() > 0) {
         return append(value.getBytes(enc));
      } else {
         return this;
      }
   }
   
   /**
    * Convenience method to append an entire byte[] area to the buffer
    */
   public ChunkedByteBuffer append(byte bytes[]) {
      if (bytes != null) {
         return append(bytes, 0, bytes.length);
      } else {
         return this;
      }
   }
   
   /**
    * Append a byte[] area to the buffer - using System.arrayCopy we can move
    * the array into the chunks directly
    */
   public ChunkedByteBuffer append(byte bytes[], int start, int length) {
      if (bytes != null && length > 0) {
         int copyfrom = start;
         byte chunk[] = array[lastChunk];
         int available = chunkSize - firstFree;
         while (length > 0) {
            if (available > length) {
               available = length;
            }
            System.arraycopy(bytes, copyfrom, array[lastChunk], firstFree, available);
            length -= available;
            copyfrom += available;
            if (length > 0) {
               if (lastChunk + 1 == array.length) {
                  expandCapacity();
               }
               chunk = array[++lastChunk];
               if (chunk == null) {
                  chunk = array[lastChunk] = new byte[chunkSize];
               }
               available = chunkSize;
               firstFree = 0;
            }
         }
         firstFree += available;
      }
      return this;
   }

   /**
    * Convenience method to append a java.nio ByteBuffer to the buffer
    */
   public ChunkedByteBuffer append(ByteBuffer buf) {
      if (buf != null && buf.remaining() > 0) {
         int buflen = buf.remaining();
         byte chunk[] = array[lastChunk];
         int available = chunkSize - firstFree;
         while (buflen > 0) {
            if (available > buflen) {
               available = buflen;
            }
            buf.get(array[lastChunk], firstFree, available);
            buflen -= available;
            if (buflen > 0) {
               if (lastChunk + 1 == array.length) {
                  expandCapacity();
               }
               chunk = array[++lastChunk];
               if (chunk == null) {
                  chunk = array[lastChunk] = new byte[chunkSize];
               }
               available = chunkSize;
               firstFree = 0;
            }
         }
         firstFree += available;
      }
      return this;
   }

   /**
    * Read the contents of the InputStream into the buffer
    */
   public ChunkedByteBuffer append(InputStream in) throws IOException {
      return append(in, false);
   }

   /**
    * Read the contents of the InputStream into the buffer
    */
   public ChunkedByteBuffer append(InputStream in, boolean checkForAvailability) throws IOException {
      if (in != null) {
         int bytesRead = 0;
         byte chunk[] = array[lastChunk];
         int available = 0;
         while (bytesRead != -1) {
            available = chunkSize - firstFree;
            bytesRead = !checkForAvailability || in.available() > 0 ? in.read(array[lastChunk], firstFree, available) : -1; 
            if (bytesRead > 0) {
               if (available == bytesRead) {
                  if (lastChunk + 1 == array.length) {
                     expandCapacity();
                  }
                  chunk = array[++lastChunk];
                  if (chunk == null) {
                     chunk = array[lastChunk] = new byte[chunkSize];
                  }
                  available = chunkSize;
                  firstFree = 0;
               } else {
                  firstFree += bytesRead;
               }
            }
         }
      }
      return this;
   }

   /**
    * Read the contents of ChunkedByteBuffer (using the ChunkedByteBuffer's inputstream) into the buffer
    */
   public ChunkedByteBuffer append(ChunkedByteBuffer buffer) {
      try {
         return append(buffer.toInputStream());
      } catch (IOException ioe) {
         throw new RuntimeException("Error reading from passed ChunkedByteBuffer.toInputStream() source in to current chunkedbuffer.", ioe);
      }
   }

   private void expandCapacity() {
      //reconsider growth strategy
      int i = array.length;
      byte newarray[][] = new byte[(i + 1) * 2][];
      System.arraycopy(array, 0, newarray, 0, i);
      array = newarray;
   }

   /**
    * Write the contents of the buffer to the OutputStream
    */
   public void writeTo(OutputStream out) throws IOException {
      int length = length();
      int stopChunk = length / chunkSize;
      int stopColumn = length % chunkSize;

      for (int i = 0; i < stopChunk; i++) {
         out.write(array[i], 0, chunkSize);
      }

      if (stopColumn > 0) {
         out.write(array[stopChunk], 0, stopColumn);
      }
   }

   public byte byteAt(int pos) {
      if ((pos < 0) || (pos >= length())) {
         throw new IndexOutOfBoundsException("Requested byteAt position " + pos + " is out of bounds on ChunkedByteBuffer");
      }
      int startChunk = pos / chunkSize;
      return array[startChunk][pos % chunkSize];
   }
   
   /**
    * Create a OutputStream with direct write access to the data in the buffer, this allows classes
    * to write directly into the buffer via the OutputStream interface
    */
   public OutputStream toOutputStream() {
      return new OutputStream() {
         boolean closed = false;
         
         public void write(int b) throws IOException {
            ensureOpen();
            append((byte)b);
         }         

         public void write(byte b[], int off, int len) throws IOException {
            ensureOpen();
            append(b, off, len);
         }

         public void close() throws IOException {
            closed = true;
         }
         
         private void ensureOpen() throws IOException {
            if (closed) {
               throw new IOException("ChunkedByteBuffer OutputStream Closed by request");
            }
         }         
      };
   }
   
   /**
    * Create a InputStream with access to the data in the buffer, this allows classes
    * to read directly out of the buffer
    */
   public InputStream toInputStream() {
      return new InputStream() {
         int index = 0;
         int marked = 0;
         boolean closed = false;
         boolean eos = false;

         private void ensureOpen() throws IOException {
            if (closed) {
               throw new IOException("ChunkedByteBuffer InputStream Closed by request");
            }
         }
         public int read() throws IOException {
            ensureOpen();
            //already reached end-of-stream
            if (eos) {
               return -1;
            }            
            int end = length();
            //indicate the end-of-stream
            if (index >= end) {
               eos = true;
               return -1;
            }
            byte buf[] = new byte[1];
            getBytes(index / chunkSize,  index % chunkSize, 1, buf, 0);
            index ++;
            return buf[0] & 0xff;
         }
         public int read(byte buf[]) throws IOException {
            return read(buf, 0, buf.length);
         }
         public int read(byte buf[], int off, int len) throws IOException {
            ensureOpen();
            //already reached end-of-stream
            if (eos) {
               return -1;
            }            
            int end = length();
            //indicate the end-of-stream
            if (index >= end) {
               eos = true;
               return -1;
            }
            len = Math.min(len, end - index);
            getBytes(index / chunkSize,  index % chunkSize, len, buf, off);
            index += len;
            return len;
         } 
         public boolean ready() throws IOException {
            ensureOpen();
            return true;
         }
         public void close() throws IOException {
            ensureOpen();
            closed = true;       
         }
         public void reset() throws IOException {
            ensureOpen();
            index = marked;
            eos = false;
         }
         public void mark(int readAheadLimit) {
            try {
               ensureOpen();
               marked = index;
            } catch (IOException e) {
               //no-op
            }
         }
         public boolean markSupported() {
            return true;
         }
         public long skip(int ns) {
            if (index >= length()) {
               return 0;
            }           
            long n = Math.min(length() - index, ns);
            index += n;
            return n;
         }
                    
      };
   }

   /**
    * Return a the buffer as a byte array. 
    */
   public byte[] toByteArray() {
      byte[] outBytes = new byte[size()];
      getBytes(0, length(), outBytes, 0);
      return outBytes;
   }
   
   /**
    * return a 'substring' ChunkedByteBuffer. 
    */
   public ChunkedByteBuffer subBuffer(int beginIndex) {
      return subBuffer(beginIndex, this.length());
   }
   
   /**
    * return a 'substring' ChunkedByteBuffer. 
    */
   public ChunkedByteBuffer subBuffer(int beginIndex, int endIndex) {
      return subBuffer(this.getChunkSize(), beginIndex, endIndex);
   }
   
   /**
    * return a 'substring' ChunkedByteBuffer.  This could possibly be made more efficient.
    */
   public ChunkedByteBuffer subBuffer(int newChunkSize, int beginIndex, int endIndex) {
      if (beginIndex < 0) {
         throw new IndexOutOfBoundsException("Cannot create a sub buffer that starts before " + beginIndex + ".");
      }
      if (endIndex > this.length()) {
         throw new IndexOutOfBoundsException("Cannot create a sub buffer that ends after " + endIndex + ".");
      }
      if (beginIndex > endIndex) {
         throw new IndexOutOfBoundsException("Cannot create a sub buffer with a negative length.");
      }
      ChunkedByteBuffer newBuffer = new ChunkedByteBuffer(newChunkSize, ((endIndex - beginIndex) * 2) / newChunkSize);
      int currentIndex = beginIndex;
      //can this be done faster?  maybe if newChunksize == this.getChunkSize() and if newChunkSize % beginIndex == 0?
      while (currentIndex < endIndex) {
         newBuffer.append(this.byteAt(currentIndex++));
      }
      return newBuffer;
   }
   
   /**
    * get the chunk of the bytes which corresponds with the given index
    */
   public byte[] getChunk(int i) {
      return array[i];
   }
   
   /**
    * Make a copy of the bytes from the buffer from the designated begining and length
    * into the specified dest 
    */
   public int getBytes(int srcBegin, int srcEnd, byte[] dest, int destBegin) {
      if (srcBegin < 0) {
         throw new ArrayIndexOutOfBoundsException(srcBegin);
      }
      if (srcEnd > length()) {
         throw new ArrayIndexOutOfBoundsException(srcEnd);
      }
      if (srcBegin > srcEnd) {
         throw new ArrayIndexOutOfBoundsException(srcEnd - srcBegin);
      }
      return getBytes(srcBegin / chunkSize, srcBegin % chunkSize, srcEnd - srcBegin, dest, destBegin);
   }

   private int getBytes(int startChunk, int startColumn, int length, byte[] dest, int destBegin) {
      int stop = (startChunk * chunkSize) + startColumn + length;
      int stopChunk = stop / chunkSize;
      int stopColumn = stop % chunkSize;

      int appended = 0;
      for (int i = startChunk; i < stopChunk; i++) {
         int size = chunkSize - startColumn;
         System.arraycopy(array[i], startColumn, dest, destBegin, size);
         destBegin = destBegin + size;
         startColumn = 0;
         appended += size;
      }

      if (stopColumn > 0) {
         System.arraycopy(array[stopChunk], startColumn, dest, destBegin, stopColumn - startColumn);
         appended += stopColumn - startColumn;
      }
      return appended;
   }
   
   /**
    * Create a copy of the current data converted to a ChunkedCharBuffer
    * the ChunkedCharBuffer need not necessarily be the same length as the 
    * byte buffer, depending on the ByteToChar conversion used
    * 
    * @param enc  The name of a supported character encoding 
    * @see ByteToCharConverter
    */
   public ChunkedCharBuffer toChunkedCharBuffer(String enc) throws UnsupportedEncodingException {
      return toChunkedCharBuffer(Charset.forName(enc));
   }
      
   /**
    * Create a copy of the current data converted to a ChunkedCharBuffer
    * the ChunkedCharBuffer need not necessarily be the same length as the 
    * byte buffer, depending on the ByteToChar conversion used
    * 
    * @param charset  The name of a supported character encoding 
    * @see ByteToCharConverter
    */
   public ChunkedCharBuffer toChunkedCharBuffer(Charset charset) throws UnsupportedEncodingException {
      try {
         ChunkedCharBuffer charBuffer = new ChunkedCharBuffer(chunkSize, startingChunks);
         charBuffer.setCharset(charset);
         charBuffer.append(new InputStreamReader(toInputStream(), charset));
         return charBuffer;
      } catch (UnsupportedEncodingException e) {
         throw e;
      } catch (IOException e) {
         throw new RuntimeException("Bug in ChunkedCharBuffer.append(Reader) or ChunkedByteBuffer.toInputStream()", e);
      }
   }
      
   /**
    * Create a copy of the current data converted to a ChunkedCharBuffer
    * the ChunkedCharBuffer need not necessarily be the same length as the 
    * byte buffer, depending on the ByteToChar conversion used
    * 
    * This method uses the default charset for this ChunkedByteBuffer
    * @see ByteToCharConverter
    */
   public ChunkedCharBuffer toChunkedCharBuffer() {
      try {
         return toChunkedCharBuffer(getCharset());
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("UnsupportedEncoding " + charsetName, e);
      }
   }
   
   /**
    * Return a String representation of the byte buffer using the specified byte encoding 
    * 
    * @param enc  The name of a supported character encoding 
    * @see ByteToCharConverter
    */
   public String toString(String enc) throws UnsupportedEncodingException {
      return toChunkedCharBuffer(enc).toString();
   }

   /**
    * Return a String representation of the byte buffer using the default byte-to-char 
    * converter specified from ByteToCharConverter.getDefault()
    * 
    * @see ByteToCharConverter
    */
   @Override public String toString() {
      return toChunkedCharBuffer().toString();
   }

}