package org.apache.lucene.store;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.Random;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util._TestUtil;

/**
 * Tests MMapDirectory's MultiMMapIndexInput
 * <p>
 * Because Java's ByteBuffer uses an int to address the
 * values, it's necessary to access a file >
 * Integer.MAX_VALUE in size using multiple byte buffers.
 */
public class TestMultiMMap extends LuceneTestCase {
  File workDir;
  
  @Override
  protected void setUp() throws Exception {
      super.setUp();
      workDir = new File(TEMP_DIR, "TestMultiMMap");
      workDir.mkdirs();
  }
  
  public void testRandomChunkSizes() throws Exception {
    Random random = newRandom();
    for (int i = 0; i < 10; i++)
      assertChunking(random, nextInt(random, 10, 100));
  }
  
  private void assertChunking(Random random, int chunkSize) throws Exception {
    File path = File.createTempFile("mmap" + chunkSize, "tmp", workDir);
    path.delete();
    path.mkdirs();
    MMapDirectory dir = new MMapDirectory(path);
    dir.setMaxChunkSize(chunkSize);
    // we will map a lot, try to turn on the unmap hack
    if (MMapDirectory.UNMAP_SUPPORTED)
      dir.setUseUnmap(true);
    IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
    writer.setMaxBufferedDocs(nextInt(random, 100, 1000));
    Document doc = new Document();
    Field docid = new Field("docid", "0", Field.Store.YES, Field.Index.NOT_ANALYZED);
    Field junk = new Field("junk", "", Field.Store.YES, Field.Index.NOT_ANALYZED);
    doc.add(docid);
    doc.add(junk);
    
    int numDocs = 1000;
    for (int i = 0; i < numDocs; i++) {
      docid.setValue("" + i);
      junk.setValue(randomUnicodeString(random));
      writer.addDocument(doc);
    }
    writer.optimize();
    writer.close();
    
    IndexReader reader = IndexReader.open(dir, true);
    
    int numAsserts = 100;
    for (int i = 0; i < numAsserts; i++) {
      int docID = random.nextInt(numDocs);
      assertEquals("" + docID, reader.document(docID).get("docid"));
    }
    reader.close();
    dir.close();
  }
  
  /** start and end are BOTH inclusive */
  public static int nextInt(Random r, int start, int end) {
    return start + r.nextInt(end-start+1);
  }

  /** Returns random string, including full unicode range. */
  public static String randomUnicodeString(Random r) {
    return randomUnicodeString(r, 20);
  }

  public static String randomUnicodeString(Random r, int maxLength) {
    final int end = r.nextInt(maxLength);
    if (end == 0) {
      // allow 0 length
      return "";
    }
    final char[] buffer = new char[end];
    for (int i = 0; i < end; i++) {
      int t = r.nextInt(5);

      if (0 == t && i < end - 1) {
        // Make a surrogate pair
        // High surrogate
        buffer[i++] = (char) nextInt(r, 0xd800, 0xdbff);
        // Low surrogate
        buffer[i] = (char) nextInt(r, 0xdc00, 0xdfff);
      }
      else if (t <= 1) buffer[i] = (char) r.nextInt(0x80);
      else if (2 == t) buffer[i] = (char) nextInt(r, 0x80, 0x800);
      else if (3 == t) buffer[i] = (char) nextInt(r, 0x800, 0xd7ff);
      else if (4 == t) buffer[i] = (char) nextInt(r, 0xe000, 0xffff);
    }
    return new String(buffer, 0, end);
  }
}
