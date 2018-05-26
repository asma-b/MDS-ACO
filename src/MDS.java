/**
 * MDS.java
 * Purpose: This is the main class. It applies the summarization process on the specified corpus.It repeats the summarization
 *  process several times based on the number of runs.
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.awt.Toolkit;
import java.io.*;
import java.util.*;
import edu.stanford.nlp.ling.*;

public class MDS {
	
	/**
	   *  This function creates the summary file and prints its input sentences.
	   *  @param summary_sentences_number: numer of sentences 
	   *  @param text_obj: the details of the input text
	   *  @param FileName: the name of file
	   *  @param output_folder: the folder name where the summary is created 
	   *  @param summarizer_obj: the summarizer
	     */
	static void create_summary_file(List<Integer> summary_sentences_number, Text text_obj, String FileName, String output_folder, Summarizer summarizer_obj, String input_folder) throws FileNotFoundException, UnsupportedEncodingException
	{		
		
		PrintWriter writer;

		//Generate the summary in the "output" folder
		System.out.println("Generating the summary of the documents in folder "+input_folder+"...\n");
		writer = new PrintWriter("output/"+output_folder+"/"+FileName);
		
		for (int q=0; q<summary_sentences_number.size();q++)
		{
			//Printing the summary sentences, one sentence per line.
			writer.println(text_obj.seg_text_sent_list.get((summary_sentences_number.get(q))).sent);
		}			
		writer.close();
		System.out.println("----------------------------------------");
			
	}
	
	/**
	   *  This function selects summary sentences by calling the summarizer
	   *  @param summarizer_obj: the summarizer
	   *  @param stop_iteration: number of iterations 
	   *  @param text_obj: the details of the input text
	   *   @param summary_limit: maximum summary length
	   *   @param size_unit: the unit of summary length (word, byte, etc.)
	   *   @param first_city_number: the initial positions of ants
	   *   
	   *   @return summary sentences
	   */
	static List<Integer> Maximize_objective(Summarizer summarizer_obj , int stop_iteration, Text text_obj, int summary_limit, String size_unit,List<Integer> first_city_number)  
	{	
		System.out.println("Summarizing ....");
		
		//To store the summarization output 
		List<sorted_list> summary_sentences = new ArrayList<sorted_list>();
		
		//Make a backup of the sentence_toBe_ranked
		Hashtable<Integer,sentence_coverage_obj> sentence_toBe_ranked_backUP=new Hashtable<Integer,sentence_coverage_obj>();	
				
		Enumeration<Integer> enumKey3;
				
		enumKey3 = summarizer_obj.sentence_toBe_ranked.keys();
		while(enumKey3.hasMoreElements()) 
		{
			int aSentence = enumKey3.nextElement();
			sentence_coverage_obj obj = new sentence_coverage_obj();
			obj.score = summarizer_obj.sentence_toBe_ranked.get(aSentence).score;					
			obj.tokens.putAll(summarizer_obj.sentence_toBe_ranked.get(aSentence).tokens);
			sentence_toBe_ranked_backUP.put(aSentence, obj);	
		}
		
		List<Integer> summary_sentecnes_number = new ArrayList<Integer>();
	
		//Apply the greedy summarization (to be used to set the pheromone value
		summary_sentences=summarizer_obj.calculate_coverage_result( text_obj,summary_limit, size_unit);
				
		//Calculate the total score divided by the number of the selected sentences of the greedy summary to be used for setting the initial value of the pheromone
		double total_greedy_score =0; 
		for (int x=0; x< summary_sentences.size(); x++)
			total_greedy_score+= summary_sentences.get(x).score;
		int greedy_sent_number =  summary_sentences.size();
		
		//Set the list backup - if we want to use it later
		summarizer_obj.sentence_toBe_ranked.clear();
		summarizer_obj.sentence_toBe_ranked.putAll(sentence_toBe_ranked_backUP);
			
		//Apply the summarization using ACS
		summary_sentecnes_number=summarizer_obj.calculate_coverage_result_by_ant( stop_iteration, text_obj,summary_limit, size_unit, first_city_number, total_greedy_score, greedy_sent_number);			
		
		//Return summary sentences
		return (summary_sentecnes_number);
	}

	/**
	   *  This function summarizes the documents based on the objective after extracting the features from the text
	   *  @param stop_iteration: number of iterations 
	   *  @param cosine_tf_isf_graph: cosine similarity values
	   *  @param text_obj: the details of the input text
	   *   @param summary_limit: maximum summary length
	   *   @param  tf_isf: the TF-ISF matrix
	   *   @param FileName: summary file name
	   *   @param size_unit: the unit of summary length (word, byte, etc.)
	   *   @param output_folder: the name of folder
	   *   @param first_city_number: the initial positions of ants
	   *   
	   */
	static void summarize(int stop_iteration, graph cosine_tf_isf_graph, Text text_obj, int summary_limit, List<Hashtable<String,Float>>  tf_isf, String FileName ,String size_unit, String output_folder, List<Integer> first_city_number, String input_folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		 //To compute the content score
		content_score contentScore_obj = new content_score(cosine_tf_isf_graph.graph_matrix, tf_isf, text_obj,0,0,0); 
				
		//summary sentence identifier 
		List<Integer> summary_sentences_number = new ArrayList<Integer>(); 	

		//to be used to select the sentences that maximize the coverage
		Summarizer summarizer_obj = new Summarizer(); 
			
		//Compute the content scores
		contentScore_obj.apply_reinforcement(summarizer_obj, stop_iteration, text_obj, summary_limit, size_unit, first_city_number);
		
		//Begin the summarization
		summary_sentences_number = Maximize_objective( summarizer_obj, stop_iteration,  text_obj,  summary_limit,  size_unit, first_city_number);  	
	
		//Create summary file	
		create_summary_file(summary_sentences_number,  text_obj,  FileName, output_folder, summarizer_obj, input_folder);
	}
	
	/**
	   *  This function starts the summarization process  
	   *  @param iteration: number of iterations 
	   *  @param run: number of runs
	   *  @param dataset:  corpus name
	 */

	static void experiment(int iteration, int run,  String dataset)  
	{	
		double a =0;
		try {				
				//----------------READING THE LIST OF STOP WORDS-------------
				String FileName_stop;   //The name of list of stop words file
				Hashtable<String,Integer> stopWords = new Hashtable<String,Integer>();   //to store stop words list
				 
				if ((dataset.equals("Multiling_2011_Arabic")))
				{
					FileName_stop="arabicStop.txt";  //Abu El-Ekhair's stop words list
					ReadArabicTextFile textFile = new ReadArabicTextFile(FileName_stop);
					stopWords = textFile.ReadFile( );  
				}
				else
				{
					FileName_stop="englishStop.txt";
					ReadTextFile textFile = new ReadTextFile(FileName_stop);
					stopWords = textFile.ReadFile( );  
				}
				
				//----------------READING THE INPUT TEXT AND SET THE OUTPUT FOLDER------------- 
				//Reading the corpus folders (clusters of documents) (i.e. the docsets)	
				
				String output_folder = ""; 					 //The output folder of the resulting summaries
				String documents_to_be_summarized_folder; 	//The path of the corpus folder
		
				if (dataset.equals("Multiling_2011"))
				{
					documents_to_be_summarized_folder = "Multiling_2011/";			//MultiLing 2011 dataset (English)
					output_folder = "output_multiling2011";
				}
				else if (dataset.equals("Multiling_2011_Arabic"))
				{
					documents_to_be_summarized_folder = "Multiling_2011_Arabic/";			//MultiLing 2011 dataset (Arabic)
					output_folder = "output_multiling2011_Arabic";
				}
				else
				{
					documents_to_be_summarized_folder = "tests/";					//For quick testig
					output_folder = "tests";
				}
							
				File clusters_folder = new File(documents_to_be_summarized_folder);
				File[] listOfFolders = clusters_folder.listFiles();	
				
				for (File cluster : listOfFolders) 
				{
					//Start reading each docset and summarizing them. 
					 if (cluster.isDirectory()) 
					 { 		
						  String folder_name=cluster.getName();
						 
							 
						  System.out.println("Start summarizing the documents in folder "+folder_name);
						  
						  System.out.println("Opening folder "+folder_name+"\n");
						  
						  //Reading the documents inside each cluster folder
							readXMLfile ReadXMLFile=null;  							//Object to read the XML file
							Read_Multiling2011 Read_Multiling2011_file= null;		//Object to read the Multiling 2011  file (Arabic or English)
							
							//Other variables
							List<Integer> first_city_number = new ArrayList<Integer>();    //list of the number of first sentences from each document in a docset; 
													 
							graph cosine_tf_isf_graph = null;		 //similarity between each two sentences
							CoreNLP_Obj NLPObj;     			  	//To call the CoreNLP library
						    Text result_obj = new Text();	  		//Store every things related to the text				
							tf_idf tf_idf_matrix = null;   			//2d matrix to store tf-idf score
							int current_number_of_text_sentences;
						
							//Begin reading the files inside the docset folder 
						    String full_folder_path=clusters_folder+"/"+folder_name+"/";
						    current_number_of_text_sentences = 1;		
							File folder = new File(full_folder_path);
								
							File[] listOfFiles = folder.listFiles();
							
							//Reading each documents
							for (File file : listOfFiles) {
							   if (file.isFile()) 
							   { 
								  String Fname=file.getName();
									   
								  //Read the document (XML file) and Apply CoreNLP
								  if (file.getName().startsWith("APW")|| file.getName().startsWith("NYT") || file.getName().startsWith("X") || (file.getName().startsWith("M")) || (file.getName().startsWith("LA"))|| (file.getName().startsWith("FBIS")) || (file.getName().startsWith("SJMN")) || (file.getName().startsWith("FT")) ||(file.getName().startsWith("AP"))||(file.getName().startsWith("WSJ")) ) //not hidden file
								   {								
									  System.out.println("-Reading file = "+file.getName());	
										  
									 if ((dataset.equals("Multiling_2011_Arabic")))
										 NLPObj = new CoreNLP_Obj( "tokenize,ssplit,pos");   //Remove the lemma; not supported by CoreNLP for Arabic
									 else 													
										 NLPObj = new CoreNLP_Obj( "tokenize,ssplit,pos,lemma");   //English
										  
									 if (documents_to_be_summarized_folder.equals("Multiling_2011/")|| (documents_to_be_summarized_folder.equals("Multiling_2011_Arabic/")) )    //Handle Multiling 2011 files
									   {
										  Read_Multiling2011_file = new Read_Multiling2011( full_folder_path+ Fname);
										  Read_Multiling2011_file.ReadFile();
										  NLPObj.apply(Read_Multiling2011_file.temp_text);
									   }
								
									  result_obj.text_sentences = NLPObj.annotation.get(CoreAnnotations.SentencesAnnotation.class);  //save the results of the preprocessing stage
									  first_city_number.add(current_number_of_text_sentences-1);  //To be used to identify the first sentence of each documents. It will be used later to set the ants positions. Begin with zero.
									  current_number_of_text_sentences=result_obj.build_text_result(stopWords, Fname,current_number_of_text_sentences);//Complete the preprocessing step (eliminate the stop words)									 				   
								  }
									   
								 //Handle other datasets  
								   else if (file.getName().startsWith("X")  || file.getName().startsWith("F") || file.getName().startsWith("L") || file.getName().startsWith("APW")|| file.getName().startsWith("NYT") ) //not hidden
								   {
									
										
									   System.out.println("File name ="+file.getName());	
									   //Read the XML file	   
									   ReadXMLFile = new readXMLfile( full_folder_path+ Fname);
									   ReadXMLFile.ReadFile();
						       						
										NLPObj = new CoreNLP_Obj( "tokenize,ssplit,pos,lemma");
										NLPObj.apply(ReadXMLFile.temp_text);
										result_obj.text_sentences = NLPObj.annotation.get(CoreAnnotations.SentencesAnnotation.class);  
										first_city_number.add(current_number_of_text_sentences-1);  //begin with zero
										current_number_of_text_sentences=result_obj.build_text_result(stopWords, Fname,current_number_of_text_sentences);	   
									 }//end else									
								  }//end if	   
								}// end for
							
								//Extracting the feature: compute the tf-isf value for the current docset content.
								
								System.out.println("Building the internal representation of the input text and scoring the words ....");
								//compute the idf list	
								result_obj.compute_idf();  								
								 	
								//compute the tf-idf matrix
								int temp=result_obj.seg_text_sent_list.size();
								tf_idf_matrix = new tf_idf(temp,result_obj.documents,result_obj.df );		
								tf_idf_matrix.compute_tf_idf_matrix(result_obj); 
									
								//Compute cosine similarity values. Cosine similarities will be store here between every sentences in a graph
								cosine_tf_isf_graph = new graph(temp); 	//temp is the number of sentences
								tf_idf_matrix.calculate_cosine_based_score(cosine_tf_isf_graph);
									
								//Preparing the output by state the file name of the summaries as well as the required length
								String summarizer;				//The name of the summarizer (prototype[# of iterations]run[run#]antE[1]a[coherence weight] (e.g. prototype100run5ant1)
								int a_label = (int) (a*100);   
								String summary_file_name=""; 	  	//To store the summary name
								int summary_length=0;				//To store the required summary size 
								String size_unit = ""; 				//To store the summary size unit (words or characters)
								String docset;
							
								String	TYPE_SIZE_DOCSELECTOR =".M.100.T.";
							
								if ((documents_to_be_summarized_folder.equals("Multiling_2011/"))  ||  (documents_to_be_summarized_folder.equals("Multiling_2011_Arabic/"))  )  //Handle Multiling 2011 files
								{
									 //Setting the summary length.
									 summary_length =250;  //words
									 size_unit = "word"; 
									 
									 //Get the docset name (i.e. folder name)
									 docset = folder_name.substring(0, 1).toUpperCase()+folder_name.substring(1,4);		 
								}
								else //for the quick test   
								{
									 //Setting the summary length
									 //summary_length is in byte (i.e. character)(it include every things); 
									 summary_length =100;  
									 size_unit = "charecter"; 
									 
									 docset = folder_name.substring(0, 1).toUpperCase()+folder_name.substring(1,6);//Need to be tested
								}
							
								//Set the name of the summary file: DOCSET.TYPE.SIZE.DOCSELECTOR.SUMMARIZER
								for (int r=1; r<=run; r++) //For each run
								{	
									summarizer ="prototype"+iteration+"run"+r+"ant"+"E1"+"a"+a_label; 
									summary_file_name = docset+TYPE_SIZE_DOCSELECTOR+summarizer;   
									summarize( iteration, cosine_tf_isf_graph, result_obj, summary_length,tf_idf_matrix.tf_idf, summary_file_name,size_unit, output_folder,first_city_number, folder_name);
								}//for
					 		}//if				   
					}//for
				}
		catch (Exception e) {
				e.printStackTrace();
			    }	
		
		System.out.println("Experiment is done"); 
	}	
	

    /**
     * The main method begins execution.
     *
     * @param args not used
     */
	public static void main(String[] args) throws IOException 
	{		
		boolean input_error = false;
	
		//Number of iterations
		int iteration = 100;	
		
		//Number of runs
		int run = 1; 		
			
		
		String dataset = ""; 
		dataset = "tests";
		//Corpus
	/*  if (Integer.parseInt(args[0])==2)
			 dataset = "Multiling_2011_Arabic";
		else if	(Integer.parseInt(args[0])==1)	
			 dataset = "Multiling_2011";
		else
			input_error=true;*/
		
	  // System.out.println(dataset );
	   
		if (!input_error) 				//Check input errors
		{
			Public.corpora = dataset;
			experiment(iteration, run, dataset);	//Call the summarization experiment 	
		}//if  
		else
			System.out.println("Wrong input(s). Run the code again with the correct inputs.");
	}
}

