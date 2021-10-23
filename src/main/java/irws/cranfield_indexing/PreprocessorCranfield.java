package irws.cranfield_indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * This file is used to preprocess the given cranfield dataset and split them into individual documents
 * 
 * @author lokesh selvakumar
 *
 */
public class PreprocessorCranfield {
	
	/**
	 * This method is used to generate documents with different IDs and write the String data using printwriter
	 * 
	 * @param StringbuilderObject
	 * @param IdIncrement
	 * @param currentLine
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void process_files(StringBuilder StringbuilderObject,Integer IdIncrement, String currentLine) throws FileNotFoundException, UnsupportedEncodingException {
		File currentFile = new File("processedFiles/cranfieldDocument_"+IdIncrement+".txt");
		PrintWriter writerObject = new PrintWriter(currentFile, "UTF-8");
		writerObject.println(StringbuilderObject.toString());
		writerObject.close();
		StringbuilderObject.delete(0, StringbuilderObject.length());
		StringbuilderObject.append(currentLine + "\n");
	}
	/**
	 * Main method that starts the execution of this program
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String cranfieldDataSet = "cranfieldDataset/cran.all.1400" ; 
		BufferedReader readerObject = new BufferedReader(new FileReader(new File(cranfieldDataSet)));
		String currentLine;
		StringBuilder StringbuilderObject = new StringBuilder();
		int IdIncrement = 1;
		while((currentLine = readerObject.readLine()) != null){
			if(currentLine.startsWith(".I")){
				if(StringbuilderObject.length()!= 0){
					process_files(StringbuilderObject,IdIncrement,currentLine);
					System.out.println("processed file_cranfieldDocument_ "+IdIncrement);
					IdIncrement++;
				}
			}
			else {
			StringbuilderObject.append(currentLine + "\n");
			}
		}
		process_files(StringbuilderObject,IdIncrement,currentLine);
	}

}
