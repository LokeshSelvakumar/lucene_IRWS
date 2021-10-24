package irws.cranfield_indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

/**
 * LuceneSearchclass is used to search the lucene index and print the cran_21331969.results file
 * 
 * @author lokesh Selvakumar
 *
 */
public class LuceneSearch {

	/**
	 * getScoreForQueries method is used to print the query result scores in the desired format
	 * 
	 * @param queryParser
	 * @param StringbuilderObject
	 * @param luceneSearcherObject
	 * @param writerObject
	 * @param indexIncrement
	 * @param similarity
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void getScoreForQueries(QueryParser queryParser,StringBuilder StringbuilderObject,IndexSearcher luceneSearcherObject,
			PrintWriter writerObject,int  indexIncrement,String similarity) throws ParseException, IOException {
		Query queryString = queryParser.parse(StringbuilderObject.toString());
		TopDocs topDocs = luceneSearcherObject.search(queryString, 10);
		ScoreDoc[] scoredocObjects = topDocs.scoreDocs;
		for(ScoreDoc sd:scoredocObjects)
		{
			int queryNumber = sd.doc;
			writerObject.println((indexIncrement+1) + " Q0 " + queryNumber + "  " + sd.score + " 0 "+ similarity);   
			System.out.println((indexIncrement+1) + " Q0 " + queryNumber + "  " + sd.score + " 0 " + similarity);
		}
		if(similarity.equals("BM25"))
		{
			StringbuilderObject.delete(0, StringbuilderObject.length());
		}
	}

	/**
	 * Main method that runs upon invocation of this class
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//index reader objects for VSM and BM25
		IndexReader indexReaderObject = DirectoryReader.open(FSDirectory.open(Paths.get("luceneIndex")));
		IndexReader indexReaderObjectBM25 = DirectoryReader.open(FSDirectory.open(Paths.get("luceneBM25Index")));

		//index Searcher objects for VSM and BM25
		IndexSearcher luceneSearcherObject = new IndexSearcher(indexReaderObject);
		IndexSearcher luceneSearcherObjectBM25 = new IndexSearcher(indexReaderObjectBM25);
		luceneSearcherObjectBM25.setSimilarity(new BM25Similarity(1.2f, 0.5f));

		//English analyser object
		CharArraySet newStopSet = CharArraySet.copy(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
		newStopSet.add("above");
		newStopSet.add("after");
		newStopSet.add("about");
		newStopSet.add("can");
		newStopSet.add("do");
		newStopSet.add("because");
		newStopSet.add("how");
		newStopSet.add("get");
		newStopSet.add("more");
		newStopSet.add("from");
		newStopSet.add("would");
		newStopSet.add("so");
		newStopSet.add("we");
		newStopSet.add("most");
		newStopSet.add("while");
		newStopSet.add("which");
		newStopSet.add("you");
		newStopSet.add("when");
		Analyzer englishAnalyzerObject = new EnglishAnalyzer(newStopSet);
		//output file
		File file = new File("cran_21331969_VSM.results");
		File file2 = new File("cran_21331969_BM25.results");
		PrintWriter writerObject = new PrintWriter(file, "UTF-8");
		PrintWriter writerfile2Object  = new PrintWriter(file2, "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("cranfieldDataset/cran.qry")));
		String currentLine;
		int indexIncrement = 0;
		//query parser object
		QueryParser queryParser = new QueryParser("file_contents", englishAnalyzerObject);
		//queryParser.setAllowLeadingWildcard(true);
		StringBuilder StringbuilderObject = new StringBuilder();

		//reads all the queries from the cran.qry file and searches the index 
		while ((currentLine = bufferedReader.readLine()) != null) {
			if(currentLine.startsWith(".I") ) {
				if(StringbuilderObject.length()!= 0) {
					{
						getScoreForQueries(queryParser,StringbuilderObject,luceneSearcherObject,writerObject,indexIncrement,"STANDARD");
						getScoreForQueries(queryParser,StringbuilderObject,luceneSearcherObjectBM25,writerfile2Object,indexIncrement,"BM25");
					}
					indexIncrement ++;
				}
			}
			else if(!currentLine.startsWith(".W") ){
				currentLine = currentLine.replaceAll("[^a-zA-Z0-9]", " ");  
				StringbuilderObject.append(currentLine);
			}
		}
		getScoreForQueries(queryParser,StringbuilderObject,luceneSearcherObject,writerObject,indexIncrement,"STANDARD");
		getScoreForQueries(queryParser,StringbuilderObject,luceneSearcherObjectBM25,writerfile2Object,indexIncrement,"BM25");
		writerObject.close();
		writerfile2Object.close();
		bufferedReader.close();
	}

}