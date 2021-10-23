////////////////////Start of Readme///////////////////////////////
Information and Retrieval Web Search Assignment 1

student id: 21331969
Name	  :Lokesh Selvakumar
strand	  : Intelligent systems

//////////////////////kickstart///////////////////////////////////
type the following commands in order:
"sudo su -"  	  - it will take you to the root directory.
"cd lucene_IRWS/" - navigating to the main project directory .
"ls" 		  - displays the following folders.

////////////////////folder_structure//////////////////////////////
CranfieldDatset - it has cranfield document and given query document.
luceneBM25Index - the index folder for BM25 similarity.
LuceneIndex 	- the index folder for VSM similarity.
ProcessedFiles  - used to store the preprocessed files from Lucene.
src 		- source code to be executed for this assignment
target 		- maven compiled jar files reside in this directory
trec_eval 	- trec eval files

/////////////////////INSTRUCTIONS/////////////////////////////////
Type the following instructions:
your current directory should be "root@selvakul:~/lucene_IRWS# "
1. mvn package 
2. cd target
3. cp CranfieldDataIndexing-jar-with-dependencies.jar ~/lucene_IRWS
4. cp PreprocessorCranfield-jar-with-dependencies.jar ~/lucene_IRWS
5. cp LuceneSearch-jar-with-dependencies.jar ~/lucene_IRWS
6. cd ..
7. java -jar PreprocessorCranfield-jar-with-dependencies.jar
8. java -jar CranfieldDataIndexing-jar-with-dependencies.jar
9. java -jar LuceneSearch-jar-with-dependencies.jar
10. cd trec_eval
11. ./trec_eval -q -m measure ~/lucene_IRWS/cranfieldDataset/cranqrel ~/lucene_IRWS/cran_21331969.results

///////////////////EXPLANATION/////////////////////////////////////
root@selvakul:~/lucene_IRWS# => this is the main project directory
1st command tells maven packages the jars of the application into target folder by reading the pom.xml file.
2nd command used to navigate to target directory
3,4,5 copies maven built jars into main project directory lucene_IRWS
6 navigate into main project directory
7 Preprocess cran.all.1400 from CranfieldDatset folder,splits it into 1400 documents and stores it in processedFiles directory.
8 creates indexes for VSE and BM25, stores the corresponding index files in luceneIndex and luceneBM25Index folders respectively.
9 executes the jar responsible for querying predefined queries in lucene indexes created in step 8 and writes the scores in cran_21331969.results file.
step 10 navigating to trec_eval directory
step 11 is the code to check the result file of the application cran_21331969.results with the given relevance file cranqrel

/////////////////ADDITIONAL COMMANDS///////////////////////////////
rm -rf <filename> => removes the given file
rm -d <directory> => remove an empty directory
rm -R <directory> => removes the non empty directory.
mkdir => creates new directory.

//////////////////END OF README/////////////////////////////////////





