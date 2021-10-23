/*
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
package irws.cranfield_indexing;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

/** Simple command-line based search demo. */
public class SearchFile {

  private SearchFile() {}

  /** Simple command-line based search demo. */
  public static void main(String[] args) throws Exception {
    String usage =
      "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/  details.";
    if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
      System.out.println(usage);
      System.exit(0);
    }
   
    String indexVSM = "luceneIndex";
    String indexBM25 = "luceneBM25Index";
    String field = "file_contents";
    String queries = null;
    String queryString = null;
    int hitsPerPage = 10;
    
    
    IndexReader readerVSM = DirectoryReader.open(FSDirectory.open(Paths.get(indexVSM)));
    IndexReader readerBM25 = DirectoryReader.open(FSDirectory.open(Paths.get(indexBM25)));
    IndexSearcher searcherVSM = new IndexSearcher(readerVSM);
    IndexSearcher searcherBM25 = new IndexSearcher(readerBM25);
    EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer();
    BM25Similarity bm25Similarity = new BM25Similarity(1.2f, 0.5f);
    searcherBM25.setSimilarity(bm25Similarity);
    QueryParser parser = new QueryParser(field, englishAnalyzer);
      if (queries == null && queryString == null) {
    	File file = new File("cran.results");
          PrintWriter writer = new PrintWriter(file, "UTF-8");
          BufferedReader br = new BufferedReader(new FileReader(new File("cranfieldDataset/cran.qry")));
  			String line;
  			String line4 = "";
  			int i = 0;
  			
  			while ((line = br.readLine()) != null) {
  				if(line.contains(".I")) {
  					if(line4 != "")
  					{
  					Query query1 = parser.parse(line4);
	                  
	                  TopDocs topDocsVSM = searcherVSM.search(query1, 1);
	                  TopDocs topDocsBM25 = searcherBM25.search(query1, 1);
	                  ScoreDoc[] hitsVSM = topDocsVSM.scoreDocs;
	                  ScoreDoc[] hitsBM25 = topDocsBM25.scoreDocs;
	                  for(ScoreDoc sd:hitsVSM)
	                  {
	                	   int s = sd.doc - 2;
	                	   writer.println((i) + " Q0 " + s + " 0 " + sd.score + " STANDARD ");   
	                	   System.out.println((i) + " Q0 " + s + " 0 " + sd.score + " STANDARD ");            
	                  }
	                  for(ScoreDoc sd:hitsBM25)
	                  {
	                	   int s = sd.doc - 2;
							writer.println((i) + " Q0 " + s + " 0 " + sd.score + " BM25 ");   
	                	   System.out.println((i) + " Q0 " + s + " 0 " + sd.score + " BM25 ");	               
	                  }      
  					}
  					i = i + 1;
  					line4 = "";
  					continue;
  				}else if(line.contains(".W")){
  					continue;
  				} else {
  					line4 += line.replace("*", "");
  					line4 = line4.replace("?", "");
  				}
  			}
  			Query query1 = parser.parse(line4);
            
            TopDocs topDocsVSM = searcherVSM.search(query1, 10);
            TopDocs topDocsBM25 = searcherBM25.search(query1, 10);
            ScoreDoc[] hitsVSM = topDocsVSM.scoreDocs;
            ScoreDoc[] hitsBM25 = topDocsBM25.scoreDocs;
            for(ScoreDoc sd:hitsVSM)
            {
          	   int s = sd.doc;
          	   writer.println((i) + " Q0 " + s + " 0 " + sd.score + " STANDARD ");   
          	   System.out.println((i) + " Q0 " + s + " 0 " + sd.score + " STANDARD ");        
            }
            for(ScoreDoc sd:hitsBM25)
            {
          	   int s = sd.doc;
          	   writer.println((i) + " Q0 " + s + " 0 " + sd.score + " BM25 ");   
          	   System.out.println((i) + " Q0 " + s + " 0 " + sd.score + " BM25 ");        
            }
            writer.close();
                   
      }   
    readerVSM.close();
    readerBM25.close();
  }

}