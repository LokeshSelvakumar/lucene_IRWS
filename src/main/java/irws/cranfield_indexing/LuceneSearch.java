package irws.cranfield_indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
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

public class LuceneSearch {

	public static void getScoreForQueries(QueryParser queryParser,StringBuilder StringbuilderObject,IndexSearcher luceneSearcherObject,
			PrintWriter writerObject,int  indexIncrement,String similarity) throws ParseException, IOException {
		Query queryString = queryParser.parse(StringbuilderObject.toString());
		TopDocs topDocs = luceneSearcherObject.search(queryString, 1);
		ScoreDoc[] scoredocObjects = topDocs.scoreDocs;
		for(ScoreDoc sd:scoredocObjects)
		{
			int queryNumber = sd.doc;
			writerObject.println((indexIncrement+1) + " Q_No " + queryNumber + ",  " + sd.score + ",  "+ similarity);   
			System.out.println((indexIncrement+1) + " Q_No " + queryNumber + ",  " + sd.score + ",  " + similarity);
		}
		if(similarity.equals("BM25"))
		{
			StringbuilderObject.delete(0, StringbuilderObject.length());
		}
	}

	public static void main(String[] args) throws Exception {
		IndexReader indexReaderObject = DirectoryReader.open(FSDirectory.open(Paths.get("luceneIndex")));
		IndexReader indexReaderObjectBM25 = DirectoryReader.open(FSDirectory.open(Paths.get("luceneBM25Index")));

		IndexSearcher luceneSearcherObject = new IndexSearcher(indexReaderObject);
		IndexSearcher luceneSearcherObjectBM25 = new IndexSearcher(indexReaderObjectBM25);
		luceneSearcherObjectBM25.setSimilarity(new BM25Similarity());

		Analyzer englishAnalyzerObject = new EnglishAnalyzer();
		File file = new File("cran.results");
		PrintWriter writerObject = new PrintWriter(file, "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("cranfieldDataset/cran.qry")));
		String currentLine;
		int indexIncrement = 0;
		QueryParser queryParser = new QueryParser("file_contents", englishAnalyzerObject);
		queryParser.setAllowLeadingWildcard(true);
		StringBuilder StringbuilderObject = new StringBuilder();
		while ((currentLine = bufferedReader.readLine()) != null) {
			if(currentLine.startsWith(".I") ) {
				if(StringbuilderObject.length()!= 0) {
					{
						getScoreForQueries(queryParser,StringbuilderObject,luceneSearcherObject,writerObject,indexIncrement,"VSM");
						getScoreForQueries(queryParser,StringbuilderObject,luceneSearcherObjectBM25,writerObject,indexIncrement,"BM25");
					}
					indexIncrement ++;
				}
			}
			else if(!currentLine.startsWith(".W") ){
				StringbuilderObject.append(currentLine + "\n");
			}
		}
		getScoreForQueries(queryParser,StringbuilderObject,luceneSearcherObject,writerObject,indexIncrement,"VSM");
		getScoreForQueries(queryParser,StringbuilderObject,luceneSearcherObjectBM25,writerObject,indexIncrement,"BM25");
	}

}