/***
 **  @(#) TradeCard.com 1.0
 **
 **  Copyright (c) 1999 TradeCard, Inc. All Rights Reserved.
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
 **  @author Copyright (c) 1999 TradeCard, Inc. All Rights Reserved.
 **
 **/

package com.partydj.util;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

/**
 * A ChunkedCharBuffer operates similarly to a java.lang.StringBuffer. However,
 * it requires less contiguous memory to manage large datasets by aligning
 * chunks of contiguous memory to maintain the whole buffer. ChunkedCharBuffers
 * are NOT THREAD SAFE, for performance reasons the buffer assumes that it is
 * being operated on with a single thread
 */
public class ChunkedCharBuffer implements CharSequence, Appendable {

   public static final int DEFAULT_CHUNK_SIZE = 4096;
   public static final int DEFAULT_NUMBER_OF_CHUNKS = 16;
   public static final int MIN_CHUNK_SIZE = 64;
   public static final int MIN_NUMBER_OF_CHUNKS = 1;
   
   public static final int MAX_RECOMMENDED_CHUNK_SIZE = DEFAULT_CHUNK_SIZE * 16;

   int chunkSize;
   char chunks[][];
   int lastChunk;
   int firstFree;
   int hash = 0;
   boolean copyOnWrite = false;

   //default byte encoding - a string because charset is not serializable
   String charsetName = CharsetConstants.UTF8.name();

   /**
    * Static Builders offer optimizations based on initial input
    */
   public static ChunkedCharBuffer of(String startsWith) {
      ChunkedCharBuffer buffer;
      if (startsWith == null) {
         buffer = new ChunkedCharBuffer();
      } else {
         int chunkSize = Math.min(startsWith.length(), DEFAULT_CHUNK_SIZE * 8);
         buffer = new ChunkedCharBuffer(chunkSize, Math.max((startsWith.length() / chunkSize) + 1, DEFAULT_NUMBER_OF_CHUNKS));
         buffer.append(startsWith);
      }
      return buffer;
   }
   /**
    * Static Builders offer optimizations based on initial input
    */
   public static ChunkedCharBuffer immutableOf(String startsWith) {
      ChunkedCharBuffer buffer;
      if (startsWith == null) {
         buffer = new ChunkedCharBuffer();
      } else {
         int chunkSize = Math.min(startsWith.length(), DEFAULT_CHUNK_SIZE * 8);
         buffer = new ChunkedCharBuffer(chunkSize, Math.max((startsWith.length() / chunkSize) + 1, DEFAULT_NUMBER_OF_CHUNKS));
         buffer.append(startsWith);
      }
      return buffer.getImmutableHandle();
   }
   
   
   
   /**
    * Create a new ChunkedCharBuffer with the passed incremental chunkSize and
    * number of starting chunks. The number of starting chunks is mearly the
    * size of the chunk holder array - which will have to grow whenever
    * chunkSize * startingChunks of data is presented. At that time the growth
    * is simply of the main array
    * @see init each constructor must call init once
    */
   public ChunkedCharBuffer(int chunkSize, int startingChunks) {
      init(chunkSize, startingChunks);
   }

   public ChunkedCharBuffer(int chunkSize) {
      init(chunkSize, DEFAULT_NUMBER_OF_CHUNKS);
   }

   public ChunkedCharBuffer() {
      init(DEFAULT_CHUNK_SIZE, DEFAULT_NUMBER_OF_CHUNKS);
   }

   public ChunkedCharBuffer(String startsWith) {
      if (startsWith == null) {
         init(DEFAULT_CHUNK_SIZE, DEFAULT_NUMBER_OF_CHUNKS);
      } else {
         int chunkSize = Math.min(startsWith.length(), DEFAULT_CHUNK_SIZE * 8);
         init(chunkSize, Math.max((startsWith.length() / chunkSize) + 1, DEFAULT_NUMBER_OF_CHUNKS));
         append(startsWith);
      }
   }
   
   private ChunkedCharBuffer(boolean uninitialized) {
      //inaccessible non-initializing constructor for access from immutable subclass
   }

   /**
    * Init must be called by each constructor to initialize the structures
    */
   private void init(int chunkSize, int startingChunks) {
      //rather than assertions just fix bad arguments
      startingChunks = Math.max(startingChunks, MIN_NUMBER_OF_CHUNKS);
      chunkSize = Math.max(chunkSize, MIN_CHUNK_SIZE);
      lastChunk = 0;
      firstFree = 0;
      chunks = new char[startingChunks][];
      this.chunkSize = chunkSize;
      this.chunks[0] = new char[chunkSize];
      copyOnWrite = false;
   }

   public final int size() {
      return length();
   }

   @Override public final int length() {
      return (lastChunk * chunkSize) + firstFree;
   }

   /**
    * @return true if the current length is zero
    */
   public final boolean isEmpty() {
      return firstFree == 0 && lastChunk == 0;
   }

   public final int getChunkSize() {
      return chunkSize;
   }

   public final Charset getCharset() {
      return Charset.forName(charsetName);
   }

   //@note this method modifies the buffer
   public void setCharset(Charset charset) {
      beforeMod(false);
      if (charset != null) {
         this.charsetName = charset.name();
      }
   }

   /**
    * Append a single char to the buffer - if there is space on the current
    * chunk then whoopee this is easy - else regrow to make room
    */
   @Override public ChunkedCharBuffer append(char value) {
      beforeMod(true);
      chunks[lastChunk][firstFree++] = value;
      return this;
   }

   public ChunkedCharBuffer append(int i) {
      return append(String.valueOf(i));
   }

   /**
    * Convenience method to append a StringBuilder to the buffer
    */
   public ChunkedCharBuffer append(StringBuilder buf) {
      int remaining = buf.length();
      if (buf != null && remaining > 0) {
         int available = 0;
         int copyfrom = 0;
         while (remaining > 0) {
            available = beforeMod(true);
            if (available > remaining) {
               available = remaining;
            }
            buf.getChars(copyfrom, copyfrom + available, chunks[lastChunk], firstFree);
            remaining -= available;
            copyfrom += available;
            firstFree += available;
         }
      }
      return this;
   }

   /**
    * Convenience method to append a StringBuffer to the buffer
    */
   public ChunkedCharBuffer append(StringBuffer buf) {
      int remaining = buf.length();
      if (buf != null && remaining > 0) {
         int available = 0;
         int copyfrom = 0;
         while (remaining > 0) {
            available = beforeMod(true);
            if (available > remaining) {
               available = remaining;
            }
            buf.getChars(copyfrom, copyfrom + available, chunks[lastChunk], firstFree);
            remaining -= available;
            copyfrom += available;
            firstFree += available;
         }
      }
      return this;
   }

   /**
    * Convenience method to append a java.nio CharBuffer to the buffer
    */
   public ChunkedCharBuffer append(CharBuffer buf) {
      if (buf != null) {
         int remaining = buf.length();
         int available = 0;
         while (remaining > 0) {
            available = beforeMod(true);
            if (available > remaining) {
               available = remaining;
            }
            buf.get(chunks[lastChunk], firstFree, available);
            remaining -= available;
            firstFree += available;
         }
      }
      return this;
   }

   /**
    * Append a string to the buffer - try to disassemble the string in the most
    * optimal size (==chunkSize) and reassemble onto the buffer
    */
   public ChunkedCharBuffer append(String value) {
      if (value != null && value.length() > 0) {
         return append(value, 0, value.length());
      } else {
         return this;
      }
   }

   /**
    * Append a string to the buffer - try to disassemble the string in the most
    * optimal size (==chunkSize) and reassemble onto the buffer
    */
   public ChunkedCharBuffer append(String value, int copyfrom, int strlen) {
      if (value != null && value.length() > 0) {
         int available = 0;
         while (strlen > 0) {
            available = beforeMod(true);
            if (available > strlen) {
               available = strlen;
            }
            value.getChars(copyfrom, copyfrom + available, chunks[lastChunk], firstFree);
            strlen -= available;
            copyfrom += available;
            firstFree += available;
         }
      }
      return this;
   }

   /**
    * Appends the specified character sequence to this buffer
    */
   @Override public ChunkedCharBuffer append(CharSequence csq){
      return append(csq, 0, csq.length());
   }

   /**
    * Appends a subsequence of the specified character sequence to this buffer
    */
   @Override public ChunkedCharBuffer append(CharSequence csq, int start, int end) {
      int seqlen = csq.length();
      //assertions as defined in java.lang.Appendable
      if (start < 0 || end < 0 || start > end || end > seqlen) {
         throw new IndexOutOfBoundsException("Assertions for start and end failed. Start:" + start + ", End:" + end);
      }
      int inputlen = end - start;
      if (inputlen > 0) {
         //chars remaining in current chunk
         int available = chunkSize - firstFree;
         //chars which will not fit in the current chunk
         int overflow = inputlen - available;
         //the number of additional chunks that will be required for the overflow
         int addlchunks = overflow > 0 ? (chunkSize % overflow) + 1 : 0;
         //the index into the sequence
         int seqidx = start;

         //prepare for mod
         beforeMod(false);
         //first pass fill the remainder of the lastChunk
         for (; seqidx < available && seqidx < end; seqidx++) {
            chunks[lastChunk][firstFree++] = csq.charAt(seqidx);
         }
         //second, work over the even sized chunks to complete the overflow
         for (int i = 0; i < addlchunks; i++) {
            beforeMod(true);
            for (int j = 0; j < chunkSize && overflow > 0; j++) {
               overflow--;
               chunks[lastChunk][firstFree++] = csq.charAt(seqidx++);
            }
         }
      }
      return this;
   }

   /**
    * Convenience method to append an entire char[] area to the buffer
    */
   public ChunkedCharBuffer append(char chars[]) {
      if (chars != null && chars.length > 0) {
         return append(chars, 0, chars.length);
      } else {
         return this;
      }
   }

   /**
    * Append a char[] area to the buffer - using System.arrayCopy we can move
    * the array into the chunks directly
    */
   public ChunkedCharBuffer append(char chars[], int start, int length) {
      if (chars != null && chars.length > 0) {
         int copyfrom = start;
         int available = 0;
         while (length > 0) {
            available = beforeMod(true);
            if (available > length) {
               available = length;
            }
            System.arraycopy(chars, copyfrom, chunks[lastChunk], firstFree, available);
            length -= available;
            copyfrom += available;
            firstFree += available;
         }
      }
      return this;
   }

   /**
    * Read the contents of the reader into the buffer
    */
   public ChunkedCharBuffer append(Reader reader) throws IOException {
      if (reader != null) {
         int charsRead = 0;
         int available = 0;
         while (charsRead != -1) {
            available = beforeMod(true);
            charsRead = reader.read(chunks[lastChunk], firstFree, available);
            if (charsRead > 0) {
               firstFree += charsRead;
            }
         }
      }
      return this;
   }

   /**
    * Read the contents of the passed ChunkedCharBuffer into this buffer
    */
   public ChunkedCharBuffer append(ChunkedCharBuffer buffer) {
      beforeMod(false);
      buffer.fillChunkedCharBuffer(this, 0, 0, buffer.length());
      return this;
   }
  
   /**
    * Internal method which must be called before any modification is made to the buffer
    * 
    * @param expand, grow the capacity of the buffer as necessary to hold at least one more char
    * @return the number of chars available for writing on the lastChunk, i.e. the current capacity
    */
   private int beforeMod(boolean expand) {
      //set the cached hash to zero
      hash = 0;
      //if copyOnWrite has been set, issue copy, then unset the indicator
      if (copyOnWrite) {
         int chunkcount = lastChunk + 1;
         char[][] chunksCopy = new char[chunkcount][];
         for (int i = 0; i < chunkcount; i++) {
            char[] copy = new char[chunkSize];
            System.arraycopy(chunks[i], 0, copy, 0, chunkSize);
            chunksCopy[i] = copy;
         }
         //reassign the chunks variable
         chunks = chunksCopy;
         copyOnWrite = false;
      }
      //expand the capacity of the buffer if requested
      if (expand) {
         //the amount of space available in the last chunk
         int available = chunkSize - firstFree;
         if (available == 0) {
            if (lastChunk + 1 == chunks.length) {
               //reconsider main array growth strategy
               int len = chunks.length;
               char newarray[][] = new char[(len + 1) * 2][];
               System.arraycopy(chunks, 0, newarray, 0, len);
               chunks = newarray;
            }
            //add a data chunk
            chunks[++lastChunk] = new char[chunkSize];
            available = chunkSize;
            firstFree = 0;
         }
         return available;
      } else {
         return chunkSize - firstFree;
      }
   }

   /**
    * Return an immutable handle to the data in this buffer.  This call is not inherently expensive,
    * as it does not require a copy of the data.  However, after this call, this buffer is put into
    * CopyOnWrite mode.  In this mode, any future modification to the buffer will first trigger a copy
    * and then apply the update.
    * @see beforeModification
    */
   public ChunkedCharBuffer getImmutableHandle() {
      copyOnWrite = true;
      return new ImmutableChunkedCharBuffer(this);
   }
   
   /**
    * Allocate a substring from the chunked buffer to the end
    * @param start - the starting index
    */
   public String substring(int start) {
      return substring(start, length());
   }

   /**
    * Allocate a substring from the chunked buffer
    * @param start - the starting index
    * @param end - the end index
    */
   public String substring(int start, int end) {
      if (start < 0) {
         throw new StringIndexOutOfBoundsException(start);
      }
      if (end > length()) {
         throw new StringIndexOutOfBoundsException(end);
      }
      if (start > end) {
         throw new StringIndexOutOfBoundsException(end - start);
      }
      return fillStringBuilder(new StringBuilder(end - start), start / chunkSize, start % chunkSize, end - start).toString();
   }

   /**
    * Allocate a ChunkedCharBuffer from the chunked buffer to the end
    * @param start - the starting index
    */
   public ChunkedCharBuffer subbuffer(int start) {
      return subbuffer(start, length());
   }

   /**
    * Allocate a ChunkedCharBuffer from the chunked buffer
    * @param start - the starting index
    * @param end - the end index
    */
   public ChunkedCharBuffer subbuffer(int start, int end) {
      if (start < 0) {
         throw new StringIndexOutOfBoundsException(start);
      }
      if (end > length()) {
         throw new StringIndexOutOfBoundsException(end);
      }
      if (start > end) {
         throw new StringIndexOutOfBoundsException(end - start);
      }
      ChunkedCharBuffer subBuffer = new ChunkedCharBuffer(chunkSize, (end - start) / chunkSize);
      subBuffer.setCharset(getCharset());
      return fillChunkedCharBuffer(subBuffer, start / chunkSize, start % chunkSize, end - start);
   }

   /**
    * This sequence is a read only view into this ChunkedCharBuffer changes to the buffer are
    * reflected in the sequence, and may damage the integrity of the sequence, for a copy of
    * the buffer use substring
    * @see java.lang.CharSequence#subSequence(int, int)
    */
   @Override public CharSequence subSequence(final int start, final int end) {
      final ChunkedCharBuffer buffer = this;
      if (start < 0) {
         throw new StringIndexOutOfBoundsException(start);
      }
      if (end > length()) {
         throw new StringIndexOutOfBoundsException(end);
      }
      if (start > end) {
         throw new StringIndexOutOfBoundsException(end - start);
      }
      //return a read only view into the chunked char buffer
      return new CharSequence() {

         @Override public int length() {
            return end - start;
         }

         @Override public char charAt(int index) {
            return buffer.charAt(index + start);
         }

         @Override public CharSequence subSequence(int subStart, int subEnd) {
            return buffer.subSequence(start + subStart, start + subEnd);
         }

         @Override public String toString() {
            return buffer.substring(start, end);
         }

         @Override public int hashCode() {
            return buffer.hashCode();
         }

         @Override public boolean equals(Object obj) {
            if (this == obj) {
               return true;
            }
            if (obj == null) {
               return false;
            }
            try {
               CharSequence other = (CharSequence)obj;
               int length = length();
               if (other.length() != length) {
                  return false;
               }
               for (int i = 0; i < length; i++) {
                  if (charAt(i) != other.charAt(i)) {
                     return false;
                  }
               }
            } catch (ClassCastException e) {
               return false;
            }
            return true;
         }
      };
   }

   /**
    * Create a new string from the entire chunked buffer
    */
   @Override public String toString() {
      int length = length();
      return fillStringBuilder(new StringBuilder(length), 0, 0, length).toString();
   }

   /**
    * Write the contents of the buffer to the writer, defensively protect the buffer
    * when copyOnWrite semantics are enabled
    */
   public void writeTo(Writer writer) throws IOException {
      int length = length();
      int stopChunk = length / chunkSize;
      int stopColumn = length % chunkSize;
      if (copyOnWrite) {
         //making a working buffer to protect the current data state
         char[] workbuffer = new char[chunkSize];
         for (int i = 0; i < stopChunk; i++) {
            System.arraycopy(chunks[i], 0, workbuffer, 0, chunkSize);
            writer.write(workbuffer, 0, chunkSize);
         }
         if (stopColumn > 0) {
            System.arraycopy(chunks[stopChunk], 0, workbuffer, 0, stopColumn);
            writer.write(workbuffer, 0, stopColumn);
         }
      } else {
         //risky approach, give away our data arrays
         for (int i = 0; i < stopChunk; i++) {
            writer.write(chunks[i], 0, chunkSize);
         }
         if (stopColumn > 0) {
            writer.write(chunks[stopChunk], 0, stopColumn);
         }
      }
   }

   @Override public char charAt(int pos) {
      if ((pos < 0) || (pos >= length())) {
         throw new StringIndexOutOfBoundsException(pos);
      }
      int startChunk = pos / chunkSize;
      return chunks[startChunk][pos % chunkSize];
   }

   /**
    * Returns this after resulting replacing all occurrences of oldChar in this
    * buffer with newChar.
    */
   public ChunkedCharBuffer replace(char oldChar, char newChar) {
      beforeMod(false);
      int length = length();
      for (int pos = 0; pos < length; pos++) {
         int startChunk = pos / chunkSize;
         if (chunks[startChunk][pos % chunkSize] == oldChar) {
            chunks[startChunk][pos % chunkSize] = newChar;
         }
      }
      return this;
   }

   /**
    * Replaces the character at the specific location with the given newChar.
    * @note this method modifies the buffer
    */
   public ChunkedCharBuffer replaceCharAt(int pos, char newChar) {
      beforeMod(false);
      if (pos < 0 || pos > length()) {
         if ((pos < 0) || (pos >= length())) {
            throw new StringIndexOutOfBoundsException(pos);
         }
      }
      chunks[pos / chunkSize][pos % chunkSize] = newChar;
      return this;
   }

   /**
    * Return a new ChunkedCharBuffer which contains the same data as this, but reversed
    */
   public ChunkedCharBuffer reverse() {
      ChunkedCharBuffer reversedBuffer = new ChunkedCharBuffer(this.chunkSize, this.chunks.length);
      for (int i = length() - 1; i >= 0; i--) {
         reversedBuffer.append(charAt(i));
      }
      return reversedBuffer;
   }

   /**
    * Create a reader with access to the data in the buffer, this allows classes
    * to read directly out of the buffer. This reader will only read within the
    * bounds specified.
    * @param beginIndex inclusive
    * @param endIndex exclusive
    */
   public Reader toRangeReader(final int beginIndex, final int endIndex) {
      if (beginIndex < 0) {
         throw new StringIndexOutOfBoundsException(beginIndex);
      }
      if (endIndex > length()) {
         throw new StringIndexOutOfBoundsException("end index is greater than length: " + endIndex + " > " + length());
      }
      if (beginIndex > endIndex) {
         throw new StringIndexOutOfBoundsException("begin index is greater than endindex: " + beginIndex + " > " + endIndex);
      }
      return new Reader() {
         int index = beginIndex;
         int marked = 0;
         boolean closed = false;
         boolean eos = false;

         private void ensureOpen() throws IOException {
            if (closed) {
               throw new IOException("ChunkedCharBuffer Reader Closed by request");
            }
         }

         @Override public int read() throws IOException {
            ensureOpen();
            //already reached end-of-stream
            if (eos) {
               return -1;
            }
            int end = endIndex;
            //indicate the end-of-stream
            if (index >= end) {
               eos = true;
               return -1;
            }
            char cbuf[] = new char[1];
            getChars(index / chunkSize, index % chunkSize, 1, cbuf, 0);
            index++;
            return cbuf[0];
         }

         @Override public int read(char cbuf[]) throws IOException {
            return read(cbuf, 0, cbuf.length);
         }

         @Override public int read(char cbuf[], int off, int len) throws IOException {
            ensureOpen();
            //already reached end-of-stream
            if (eos) {
               return -1;
            }
            int end = endIndex;
            //indicate the end-of-stream
            if (index >= end) {
               eos = true;
               return -1;
            }
            len = Math.min(len, end - index);
            getChars(index / chunkSize, index % chunkSize, len, cbuf, off);
            index += len;
            return len;
         }

         @Override public boolean ready() throws IOException {
            ensureOpen();
            return true;
         }

         /**
          * Closing a previously closed stream has no effect.
          * @see java.io.Reader
          */
         @Override public void close() throws IOException {
            //ensureOpen();
            closed = true;
         }

         @Override public void reset() throws IOException {
            ensureOpen();
            index = marked;
            eos = false;
         }

         @Override public void mark(int readAheadLimit) throws IOException {
            ensureOpen();
            marked = index;
         }

         @Override public boolean markSupported() {
            return true;
         }

         public long skip(int ns) {
            if (index >= endIndex) {
               return 0;
            }
            long n = Math.min(endIndex - index, ns);
            index += n;
            return n;
         }
         
         @Override public String toString() {
            return String.format("ChunkedCharBuffer.RangeReader[%d,%d]", Integer.valueOf(beginIndex), Integer.valueOf(endIndex));
         }

      };
   }

   /**
    * Create a reader with access to the data in the buffer, this allows classes
    * to read directly out of the buffer
    */
   public Reader toReader() {
      return toRangeReader(0, length());
   }

   /**
    * Create a Writer with direct write access to the data in the buffer, this allows classes
    * to write directly into the buffer via the Writer interface
    */
   public Writer toWriter() {
      return new Writer(this) {
         boolean closed = false;

         @Override public void write(int c) throws IOException {
            ensureOpen();
            super.write(c);
         }

         @Override public void write(char cbuf[], int off, int len) throws IOException {
            ensureOpen();
            ChunkedCharBuffer.this.append(cbuf, off, len);
         }

         @Override public void write(String str, int off, int len) throws IOException {
            ensureOpen();
            ChunkedCharBuffer.this.append(str, off, len);
         }

         @Override public void close() throws IOException {
            closed = true;
         }

         @Override public void flush() throws IOException {
            //no-op
         }

         private void ensureOpen() throws IOException {
            if (closed) {
               throw new IOException("ChunkedCharBuffer Writer closed by request");
            }
         }
      };
   }

   /**
    * Create an InputStream with access to the data in the buffer, this allows
    * classes to read directly out of the buffer
    */
   public InputStream toInputStream() throws IOException {
      return toInputStream(Charset.forName(charsetName));
   }

   /**
    * Create an InputStream with access to the data in the buffer, this allows
    * classes to read directly out of the buffer
    */
   public InputStream toInputStream(Charset charset) throws IOException {
      return toChunkedByteBuffer(Charset.forName(charsetName)).toInputStream();
   }

   /**
    * Create a copy of the current data converted to a ChunkedByteBuffer
    */
   public ChunkedByteBuffer toChunkedByteBuffer(Charset charset) throws CharacterCodingException, UnsupportedEncodingException {
      //configure the encoder the way that string encoder does
      CharsetEncoder encoder = charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      float avgBytesPerChar = encoder.averageBytesPerChar();
      ChunkedByteBuffer cbb = new ChunkedByteBuffer((int)(chunkSize * avgBytesPerChar), chunks.length);
      cbb.setCharset(charset);

      int length = length();
      int stopChunk = length / chunkSize;
      int stopColumn = length % chunkSize;

      //encoding per char array chunk is much faster than the CharSequence interface to CharBuffer
      for (int i = 0; i < stopChunk; i++) {
         ByteBuffer bb = encoder.encode(CharBuffer.wrap(chunks[i], 0, chunkSize));
         cbb.append(bb);
      }
      if (stopColumn > 0) {
         ByteBuffer bb = encoder.encode(CharBuffer.wrap(chunks[stopChunk], 0, stopColumn));
         cbb.append(bb);
      }
      return cbb;

   }

   /**
    * Tests if this buffer starts with the specified prefix beginning
    * a specified index.
    * @param   prefix    the prefix
    * @param   toffset   where to begin looking in the string
    */
   public boolean startsWith(String prefix, int index) {
      int plen = prefix.length();
      int count = 0;
      if ((index < 0) || (index > (length() - plen))) {
         return false;
      }
      while (--plen >= 0) {
         if (unsafeCharAt(index++) != prefix.charAt(count++)) {
            return false;
         }
      }
      return true;
   }

   /**
    * Tests if this buffer starts with the specified prefix
    */
   public boolean startsWith(String prefix) {
      return startsWith(prefix, 0);
   }

   /**
    * Returns the index within this buffer of the first occurrence of the
    * specified substring, starting at the begining of this buffer
    * @param queryStr the substring to search for.
    */
   public int indexOf(String queryStr) {
      return indexOf(queryStr, 0);
   }

   /**
    * This version of the indexOf method is based on an algorithm by Thomas Wang,
    * details can be found at http://www.concentric.net/~Ttwang/tech/stringscan.htm
    * The algorithm should be faster than a simple left to right scanning approach.
    * 
    * The key to this faster scanning algorithm is to look at the last character of the pattern string first.
    * If the last pattern character matches, then we continue to search for pattern characters from left to right,
    * until the remaining pattern characters are matched.  The algorithm skips parts of the string when it determines
    * that no match could possibly be found over that stretch.
    * 
    * Two variables help speed the algorithm:
    * @var cache, which is a number representing a logical OR of all the queryStr characters
    * @var md2, is equal to the length of the queryStr except when the last char of the queryStr exists elsewhere in the
    * queryStr, in which case the value is the number of characters from the end of the string to that char
    * 
    * The cache is used for checking whether a character could possibly be in the queryStr.  By logical ANDing on any
    * char you can confirm if that character is possible/impossible as a component of a matching substring
    * 
    * The md2 is used as the base number for how many characters can be skipped by the algorithm moving past a last char
    * match
    * 
    */
   public int indexOf(String queryStr, int fromIndex) {
      //reuseable lens
      int querylen = queryStr.length();
      int length = length();

      //simple assertions
      if (fromIndex >= length) {
         return (querylen == 0 ? length : -1);
      }
      if (fromIndex < 0) {
         fromIndex = 0;
      }
      if (querylen == 0) {
         return fromIndex;
      }

      //working vars
      long cache = 0;
      int md2 = querylen;
      char lastQueryChar = queryStr.charAt(querylen - 1);
      for (int i = 0; i < querylen; i++) {
         char c = queryStr.charAt(i);
         //initialize the cache
         cache |= 1L << (c & 63);
         //calculate the md2, ignoring the last char
         if (lastQueryChar == c && i < (querylen - 1)) {
            md2 = querylen - (i + 1);
         }
      }

      //whether the md2 is less than the query len
      boolean shortmd2 = md2 < querylen;
      //the current chunk
      int chunk = ((querylen - 1) + fromIndex) / chunkSize;
      //index into the current chunk
      int i = ((querylen - 1) + fromIndex) % chunkSize;
      //overall index in the buffer
      int index = i + (chunk * chunkSize);
      //character used for checking
      char c = Character.MIN_VALUE;
      //how much to skip each iteration
      int skip = md2;

      //chunk loop
      while (chunk <= lastChunk) {
         //scan loop
         scan_loop: while (i < chunkSize) {
            c = chunks[chunk][i];
            if (lastQueryChar == c) {
               //last character matched, try to match the rest
               for (int j = 0; j < (querylen - 1); j++) {
                  //$TS optimize me, it isn't necessary to do the division to find the chunk each time
                  c = unsafeCharAt((index - querylen) + j + 1);
                  if (queryStr.charAt(j) != c) {
                     skip = md2;
                     if (shortmd2) {
                        int altskip = 1;
                        //see if the char is "impossible"
                        if (((cache & (1L << (c & 63))) == 0L)) {
                           altskip = j + 1;
                        }
                        //skip the max of md2 and impossible calc
                        skip = Math.max(md2, altskip);
                     }
                     //skip the indicated chars
                     i += skip;
                     index += skip;
                     continue scan_loop;
                  }
               }
               //full match
               return index - querylen + 1;
            } else if ((cache & (1L << (c & 63))) == 0L) {
               //the char is "impossible"
               i += querylen;
               index += querylen;
            } else {
               i += 1;
               index += 1;
            }
         }
         //advance the chunk
         chunk = (index / chunkSize);
         i = i % chunkSize;
      }
      //return not found
      return -1;
   }

   /**
    * Returns the index within this string of the first occurrence of the
    * specified character, starting the search at the begining of the buffer
    */
   public final int indexOf(char ch) {
      return indexOf(ch, 0);
   }
   
   /**
    * Returns the index within this string of the first occurrence of the
    * specified character, starting the search at the specified index. There is
    * no restriction on the value of fromIndex. If it is negative, it has the
    * same effect as if it were zero: this entire buffer may be searched. If it
    * is greater than the length of this buffer, it has the same effect as if it
    * were equal to the length of this string: -1 is returned.
    */
   public final int indexOf(char ch, int fromIndex) {
      int length = length();
      if (fromIndex < 0) {
         fromIndex = 0;
      } else if (fromIndex >= length) {
         return -1;
      }
      int chunk = fromIndex / chunkSize;
      int i = fromIndex % chunkSize;
      int index = i + (chunk * chunkSize);

      for (; chunk <= lastChunk; chunk++) {
         for (; index < length && i < chunkSize; i++, index++) {
            if (chunks[chunk][i] == ch) {
               return index;
            }
         }
         i = 0;
      }
      return -1;
   }

   /**
    * Returns the index within this string of the last occurrence of the
    * specified substring.
    */
   public final int lastIndexOf(String queryStr) {
      return lastIndexOf(queryStr, length());
   }

   /**
    * Returns the index within this string of the last occurrence of the
    * specified substring. There is no restriction on the value of fromIndex. If
    * it is negative, it has the same effect as if it were zero: this entire
    * buffer may be searched. If it is greater than the length of this buffer,
    * it has the same effect as if it were equal to the length of this string:
    * -1 is returned. When there is time to do so, further research should be
    * made into enhancing the performance of this method, current seek time is
    * nearly 4x times the equvilent call on String. The time is entirely spent
    * dealing with additional bounds checking required by the simple
    * implementation, perhaps an enhanced version could be more aggressive about
    * bounds checking, or could use a more advanced search algorithm such as
    * Boyer-Moore or Knuth-Morris-Pratt
    * @param queryStr the substring to search for
    * @param fromIndex the index to start the search from
    */
   public final int lastIndexOf(String queryStr, int fromIndex) {
      int querylen = queryStr.length();
      int length = length();

      if (fromIndex < 0) {
         return -1;
      }
      if (fromIndex > (length - querylen)) {
         fromIndex = length - querylen;
      }
      if (querylen == 0) {
         return fromIndex;
      }

      int min = querylen - 1;
      char lastQueryChar = queryStr.charAt(min);
      fromIndex += min;
      int chunk = fromIndex / chunkSize;
      int i = fromIndex % chunkSize;
      int index = i + (chunk * chunkSize);

      //iterating chunks
      while (chunk >= 0) {
         //look for first character
         while (index >= min && i >= 0 && chunks[chunk][i] != lastQueryChar) {
            i--;
            index--;
         }
         if (index < min) {
            return -1;
         }
         if (i == -1) {
            //wrap to the next chunk
            i = chunkSize - 1;
            chunk--;
         } else {
            //we found a first char match
            int j = i - 1;
            if (i == 0) {
               j = chunkSize - 1;
               i = chunkSize - 1;
               chunk--;
            } else {
               i--;
            }
            int followIndex = index - 1;
            int endIndex = followIndex - (querylen - 1);
            int k = querylen - 2;
            int followChunk = followIndex / chunkSize;
            while (followIndex >= endIndex) {
               while (j >= 0 && k >= 0 && followIndex >= 0) {
                  followIndex--;
                  if (chunks[followChunk][j--] != queryStr.charAt(k)) {
                     //look for first char again
                     break;
                  } else {
                     k--;
                  }
               }
               if (k == -1) {
                  //full match
                  return endIndex + 1;
               }
               if (j != -1) {
                  break;
               }
               //continue to the next chunk
               j = chunkSize - 1;
               followChunk--;
            }
            //we are pointing at a good i
            index--;
         }
      }
      return -1;
   }

   /**
    * Returns a java.util.regex.Matcher for this buffer.
    * @param regex the delimiting regular expression
    * @return Matcher for the matches against the buffer
    * @throws PatternSyntaxException if the regular expression's syntax is
    *         invalid
    * @see java.util.regex.Pattern
    * @see java.util.regex.Matcher
    */
   public final Matcher matcher(String regex) {
      return Pattern.compile(regex).matcher(this);
   }

   /**
    * The array returned by this method contains each substring of this buffer
    * that is terminated by another substring that matches the given expression
    * or is terminated by the end of the buffer. The substrings in the array are
    * in the order in which they occur in this buffer. If the expression does
    * not match any part of the input then the resulting array has just one
    * element, namely this string.
    * @param regex the delimiting regular expression
    * @return the array of strings computed by splitting this string around
    *         matches of the given regular expression
    * @throws PatternSyntaxException if the regular expression's syntax is
    *         invalid
    * @see java.util.regex.Pattern
    */
   public final String[] split(String regex) {
      return Pattern.compile(regex).split(this, 0);
   }

   /**
    * The array returned by this method contains each substring of this buffer
    * that is terminated by another substring that matches the given expression
    * or is terminated by the end of the buffer. The substrings in the array are
    * in the order in which they occur in this buffer. If the expression does
    * not match any part of the input then the resulting array has just one
    * element, namely this string.
    * @param regex the delimiting regular expression
    * @param limit the result threshold, as described above
    * @return the array of strings computed by splitting this string around
    *         matches of the given regular expression
    * @throws PatternSyntaxException if the regular expression's syntax is
    *         invalid
    * @see java.util.regex.Pattern
    */
   public final String[] split(String regex, int limit) {
      return Pattern.compile(regex).split(this, limit);
   }

   /**
    * Valid implmentation of the .equals method for ChunkedCharBuffer
    */
   @Override public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      try {
         ChunkedCharBuffer other = (ChunkedCharBuffer)obj;
         int length = length();
         if (other.length() != length) {
            return false;
         }
         int slowCheckFrom = 0;
         //we can optmize a lot if our chunksize is the same
         if (chunkSize == other.chunkSize) {
            for (int i = 0; i < lastChunk; i++) {
               if (!Arrays.equals(chunks[i], other.chunks[i])) {
                  return false;
               }
            }
            slowCheckFrom = lastChunk * chunkSize;
         }
         //this method of checking is much slower
         for (int i = slowCheckFrom; i < length; i++) {
            if (unsafeCharAt(i) != other.unsafeCharAt(i)) {
               return false;
            }
         }
      } catch (ClassCastException e) {
         return false;
      }
      return true;
   }

   /**
    * Fill the dest array with content from the buffer
    * @param srcBegin start index (inclusive) in the buffer
    * @param srcEnd end index (exclusive) in the buffer
    * @param dest the array to fill
    * @param destBegin start index in the destination array
    * @throws ArrayIndexOutOfBounds exception if the dest array cannot hold the requested data
    */
   public final void getChars(int srcBegin, int srcEnd, char[] dest, int destBegin) throws ArrayIndexOutOfBoundsException {
      if (srcBegin < 0) {
         throw new StringIndexOutOfBoundsException(srcBegin);
      }
      if (srcEnd > length()) {
         throw new StringIndexOutOfBoundsException(srcEnd);
      }
      if (srcBegin > srcEnd) {
         throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
      }
      getChars(srcBegin / chunkSize, srcBegin % chunkSize, srcEnd - srcBegin, dest, destBegin);
   }

   @Override
   public final int hashCode() {
      int h = hash;
      if (h == 0) {
         for (int i = 0; i < lastChunk - 1; i++) {
            for (char c : chunks[i]) {
               h = 31 * h + c;
            }
         }
         for (int i = 0; i < firstFree; i++) {
            char c = chunks[lastChunk][i];
            h = 31 * h + c;
         }
         hash = h;
      }
      return h;
   }

   /**
    * CharAt call which does no bounds checking, for internal calls that ensure safe boundary processing
    */
   private char unsafeCharAt(int pos) {
      return chunks[pos / chunkSize][pos % chunkSize];
   }
   
   private void getChars(int startChunk, int startColumn, int length, char[] dest, int destBegin) throws ArrayIndexOutOfBoundsException {
      int stop = (startChunk * chunkSize) + startColumn + length;
      int stopChunk = stop / chunkSize;
      int stopColumn = stop % chunkSize;

      for (int i = startChunk; i < stopChunk; i++) {
         int size = chunkSize - startColumn;
         System.arraycopy(chunks[i], startColumn, dest, destBegin, size);
         destBegin += size;
         startColumn = 0;
      }

      if (stopColumn > 0) {
         System.arraycopy(chunks[stopChunk], startColumn, dest, destBegin, stopColumn - startColumn);
      }
   }

   /**
    * Write the chunk data into the string bufffer, assume the buffer is well
    * allocated
    */
   private StringBuilder fillStringBuilder(StringBuilder sb, int startChunk, int startColumn, int length) {
      int stop = (startChunk * chunkSize) + startColumn + length;
      int stopChunk = stop / chunkSize;
      int stopColumn = stop % chunkSize;

      for (int i = startChunk; i < stopChunk; i++) {
         sb.append(chunks[i], startColumn, chunkSize - startColumn);
         startColumn = 0;
      }

      if (stopColumn > 0) {
         sb.append(chunks[stopChunk], startColumn, stopColumn - startColumn);
      }

      return sb;
   }

   /**
    * Write the chunk data into the ChunkedCharBuffer, assume the buffer is well
    * allocated
    */
   private ChunkedCharBuffer fillChunkedCharBuffer(ChunkedCharBuffer ccb, int startChunk, int startColumn, int length) {
      int stop = (startChunk * chunkSize) + startColumn + length;
      int stopChunk = stop / chunkSize;
      int stopColumn = stop % chunkSize;

      for (int i = startChunk; i < stopChunk; i++) {
         ccb.append(chunks[i], startColumn, chunkSize - startColumn);
         startColumn = 0;
      }

      if (stopColumn > 0) {
         ccb.append(chunks[stopChunk], startColumn, stopColumn - startColumn);
      }

      return ccb;
   }
   
   /**
    * All modifier methods of ChunkedCharBuffer MUST be implemented here to protect immutability
    */
   private class ImmutableChunkedCharBuffer extends ChunkedCharBuffer {
      public ImmutableChunkedCharBuffer(ChunkedCharBuffer source) {
         super(true);
         //copy initialization
         chunkSize = source.chunkSize;
         chunks = source.chunks;
         lastChunk = source.lastChunk;
         firstFree = source.firstFree;
         hash = source.hash;
         copyOnWrite = true;
         charsetName = source.charsetName;
      }
      
      public ImmutableChunkedCharBuffer() {
         //for serialization only (must be public)
      }
      
      public ChunkedCharBuffer unmodifiable() {
         throw new UnsupportedOperationException("Cannot change immutable chunked char buffer");
      }
      @Override public void setCharset(Charset charset) { unmodifiable();}
      @Override public ChunkedCharBuffer append(char value) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(char[] value)  { return unmodifiable();}
      @Override public ChunkedCharBuffer append(char[] chars, int start, int length) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(CharBuffer buf) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(CharSequence csq) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(CharSequence csq, int start, int end) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(ChunkedCharBuffer buffer) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(int i) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(Reader reader) throws IOException { return unmodifiable();}
      @Override public ChunkedCharBuffer append(String value) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(String value, int copyfrom, int strlen) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(StringBuilder buf) { return unmodifiable();}
      @Override public ChunkedCharBuffer append(StringBuffer buf) { return unmodifiable();}
      @Override public ChunkedCharBuffer replace(char oldChar, char newChar) { return unmodifiable();}
      @Override public ChunkedCharBuffer replaceCharAt(int pos, char newChar) { return unmodifiable();}

      @Override public ChunkedCharBuffer getImmutableHandle() {
         return this;
      }
   }
   
}
