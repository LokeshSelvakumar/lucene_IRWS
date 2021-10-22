package irws.cranfield_indexing;
 
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
 
/**
 * 
 * CranfieldDataIndexing class is used to index all the crandfield dataset preprocessed files into lucene.
 * 
 * @author lokesh Selvakumar
 *
 */
public class CranfieldDataIndexing 
{
	/**
	 * Main method that runs upon invocation of this class
	 * @param args
	 */
    public static void main(String[] args)
    {
        final Path cranDataSetPath = Paths.get("processedFiles");
 
        try
        {
        	//English analyzer with the default stop words
        	Analyzer englishAnalyzer = new EnglishAnalyzer();
            //lucene index directory is set here
            Directory indexDirectory = FSDirectory.open( Paths.get("luceneIndex") );
            //configuring index writer with default VSM similarity and indexing docs using indexCranDataSetFiles method
            IndexWriterConfig indexConfig = new IndexWriterConfig(englishAnalyzer);
            indexConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            indexConfig.setSimilarity( new ClassicSimilarity());
            IndexWriter writerObject = new IndexWriter(indexDirectory, indexConfig);
            indexCranDataSetFiles(writerObject, cranDataSetPath);
            writerObject.close();
            
            Directory indexBM25 = FSDirectory.open( Paths.get("luceneBM25Index") );
            //configuring index writer with BM25 similarity and indexing docs using indexCranDataSetFiles method
            IndexWriterConfig indexConfigBM25 = new IndexWriterConfig(englishAnalyzer);
            indexConfigBM25 = new IndexWriterConfig(englishAnalyzer);
            indexConfigBM25.setOpenMode(OpenMode.CREATE_OR_APPEND);
            indexConfigBM25.setSimilarity( new BM25Similarity());
            IndexWriter writerObject2 = new IndexWriter(indexBM25, indexConfigBM25);
            indexCranDataSetFiles(writerObject2, cranDataSetPath);
            writerObject2.close();
        } 
        catch (IOException e) 
        {
        	System.err.println("indexing initial configuration failed");
            e.printStackTrace();
        }
    }
    
    /**
     * cranDataFileIndex is used to index the given file in lucene with the given basic file parameters
     * 
     * @param indexWriterObject
     * @param filePath
     * @param lastModified
     * @throws IOException
     */
    static void cranDataFileIndex(IndexWriter indexWriterObject, Path filePath, long lastModified) throws IOException 
    {
        try (InputStream stream = Files.newInputStream(filePath)) 
        {
            Document newDocument = new Document();
            newDocument.add(new StringField("path", filePath.toString(), Field.Store.YES));
            newDocument.add(new LongPoint("last_modified", lastModified));
            newDocument.add(new TextField("file_contents", new String(Files.readAllBytes(filePath)), Store.YES));
            System.out.println("adding " + filePath);
            indexWriterObject.updateDocument(new Term("path", filePath.toString()), newDocument);
        }
    }
     
    /**
     * 
     * indexCranDataSetFiles method handles the core functionality of iterating over all the files in the
     * given directory path and indexes it in lucene.
     * 
     * @param writerObject
     * @param filePath
     * @throws IOException
     */
    static void indexCranDataSetFiles(final IndexWriter writerObject, Path filePath) throws IOException 
    {
        if (Files.isDirectory(filePath)) 
        {
            Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() 
            {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes basicFileAttrObject) throws IOException 
                {
                    try
                    {
                        cranDataFileIndex(writerObject, filePath, basicFileAttrObject.lastModifiedTime().toMillis());
                    } 
                    catch (IOException ioExceptionObject) 
                    {
                        ioExceptionObject.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } 
        else
        {
            cranDataFileIndex(writerObject, filePath, Files.getLastModifiedTime(filePath).toMillis());
        }
    }
    
    
}