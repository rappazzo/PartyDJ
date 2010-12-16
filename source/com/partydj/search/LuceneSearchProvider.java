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
package com.partydj.search;

import java.util.*;
import java.util.regex.*;
import org.apache.commons.codec.language.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.*;
import org.cmc.music.metadata.*;
import com.partydj.player.*;
import com.partydj.util.*;

/**
 * 
 **/
public class LuceneSearchProvider implements SearchProvider {

   public static final DoubleMetaphone INDEX_ENCODER = new DoubleMetaphone();
   Map<Document, MediaFile> indexToMediaFile = new HashMap();
   
   private static final Directory index = new RAMDirectory();
   private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
   private static final IndexWriter indexWriter = makeIndexWriter();
   private static IndexWriter makeIndexWriter() {
      try {
         return new IndexWriter(index, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   @Override public void addToSearchIndex(MediaFile file) {
      IMusicMetadata metadata = file.getMetadata();
      try {
         Document doc = new Document();
         if (metadata.getArtist() != null) {
            doc.add(new Field(MediaFile.ARTIST, INDEX_ENCODER.encode(metadata.getArtist()), Field.Store.YES, Field.Index.ANALYZED));
         }
         if (metadata.getAlbum() != null) {
            doc.add(new Field(MediaFile.ALBUM, INDEX_ENCODER.encode(metadata.getAlbum()), Field.Store.YES, Field.Index.ANALYZED));
         }
         doc.add(new Field(MediaFile.TITLE, INDEX_ENCODER.encode(metadata.getSongTitle()), Field.Store.YES, Field.Index.ANALYZED));
         doc.add(new Field(ANY, INDEX_ENCODER.encode(file.getSimpleName()), Field.Store.YES, Field.Index.ANALYZED));
         indexWriter.addDocument(doc);
         indexToMediaFile.put(doc, file);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   //for now only use the first value
   @Override public Collection<MediaFile> find(Map<String, Collection<String>> queryParameters) {
      StringBuilder queryString = new StringBuilder();
      if (queryString == null || queryString.length() == 0) {
         return Collections.emptyList();
      } else {
         String any = getFirst(queryParameters, ANY);
         if (any != null) {
            queryString.append(INDEX_ENCODER.encode(any));
         } else {
            //$MR todo
         }
         List<MediaFile> found = new ArrayList();
         try {
            Query query = new QueryParser(Version.LUCENE_CURRENT, ANY, analyzer).parse(queryString.toString());
            Integer hitsPerPage = getFirstInteger(queryParameters, MAX_RESULTS);
            if (hitsPerPage == null) {
               hitsPerPage = DEFAULT_MAX_RESULTS;
            }
            IndexSearcher searcher = new IndexSearcher(index, true);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
            //$MR jaro-winlker scorer
            searcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            
            for (ScoreDoc hit : hits) {
               Document d = searcher.doc(hit.doc);
               found.add(indexToMediaFile.get(d));
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
         return found;
      }
   }

   private static Integer getFirstInteger(Map<String, Collection<String>> map, String key) {
      String string = getFirst(map, key);
      if (string != null) {
         try {
            return Integer.valueOf(string);
         } catch (Exception e) {
         }
      }
      return null;
   }
   private static String getFirst(Map<String, Collection<String>> map, String key) {
      Collection<String> c = map.get(key);
      if (c != null && c.size() > 0) {
         return c.iterator().next();
      }
      return null;
   }

}
