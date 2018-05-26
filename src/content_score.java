/**
 * content_score.java
 * Purpose: This class computes the content scores for each words. It is based on the approach proposed 
 * by Wan et al. (Wan, Xiaojun, Jianwu Yang, and Jianguo Xiao. "Towards an iterative reinforcement approach for
 * simultaneous document summarization and keyword extraction." ACL. Vol. 7. 2007.) Nevertheless, several differences
 * exist between the approach of Wan et al. and our implemented method, such as the use the words scores to
 * maximize the objective. 

 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class content_score {

	int w_size=0, s_size=0;
	
	static double[][] ww; //to store word-to-word relationship scores
	static double[][] ss; //to store sentence-to-sentence relationship scores
	static double[][] sw; //to store sentence-to-word relationship scores
	
	//Threshold value for each relationships
	double threshold_ww;
	double threshold_sw;
	double threshold_ss;
	
	//To keep tack of word index
	List<String> words_list = new ArrayList<String>(); 
		
	/**
	*  Constructor
	*  @param similiarty_matrix: cosine similarity matrix 
	*  @param tf_isf: TF-ISF matrix
	*  @param Text:the input text details
	*  @param thresholdWW: the threshold of the word-to-word relationship
	*  @param thresholdSW: the threshold of the sentence-to-word relationship
	*  @param thresholdSS: the threshold of the sentence-to-sentence relationship
    */
	content_score(double[][] similiarty_matrix, List<Hashtable<String,Float>>  tf_isf , Text result_obj,  double thresholdWW, double thresholdSW,double thresholdSS)
	{
		//Fill the sizes
		s_size = similiarty_matrix.length;	
		w_size = result_obj.idf.size();	
		
		//Set the thresholds
		threshold_ww = thresholdWW;
		threshold_sw = thresholdSW;
		threshold_ss = thresholdSS;
		
		//Create a utility object
		utility u = new utility();
		
		//Variables to store string
		String s1, s2;
		
		//to traverse the word frequency list
		Enumeration<String> enumKey = result_obj.idf.keys();
		
		//Set the sizes			
		ww = new double[w_size][w_size];
		sw = new double[s_size][w_size];
		ss = new double[s_size][s_size];	
		
		//1-Create the SS graph; only get the cosine.	
		for (int i = 0; i < s_size; i++) //each sentence (row)
		{
			ss[i][i] = 0;  //No unary edges
				
			for (int j = i+1; j < s_size; j++)     //other sentence [column]
			{				
				if (similiarty_matrix[i][j]>threshold_sw)
	           		ss[i][j] = ss[j][i] = similiarty_matrix[i][j];    
				else
					ss[i][j] = ss[j][i] = 0;
			}//for	      
		}//for

		//2-Create the WW graph + create list of words (String) so we can then refer to them, because we can not sure that the hash is always in this order
		while(enumKey.hasMoreElements()) 
		{
			String key = enumKey.nextElement();
			words_list.add(key);
		}//while
		
		for (int i = 0; i < w_size; i++) //each word
		{
			s1 = words_list.get(i);				   //get first the word in i index
			ww[i][i] = 0;						   //No unary edges
			for (int j = i+1; j < w_size; j++)     
			{			
				s2 = words_list.get(j);			   //get the second word in j index
				double sim = (double)  (u.calculate_LCS(s1,s2)) /(double)  (s1.length()+s2.length());
				
				if (sim > threshold_ww)
					ww[i][j] = ww[j][i] = sim;
				else
					ww[i][j] = ww[j][i] = 0;
             }//for		      
		}//for
	
		//3-Create the WS graph; I will use the list of word list
		for (int i = 0; i < w_size; i++) //each word (col)
		{	
			s1 = words_list.get(i);  //get the word
			//System.out.println("\n Word= "+ s1);
			for (int j = 0; j < s_size; j++)     //each sentence (row)
				{					
					Hashtable<String,Integer> tokens= result_obj.seg_text_sent_list.get(j).tokens; //get sentences' tokens
					
					if (tokens.containsKey(s1))
					{
						double sum = 0;  //Denominator	
						Hashtable<String,Float> aSentence = new Hashtable<String,Float> ();  
						aSentence = tf_isf.get(j);			 //Retrieve word tf-isf od thw words in the sentence
						enumKey = aSentence.keys();			//For traverse each word in a specific sentence
						
						//Calculate the denominator
						while(enumKey.hasMoreElements()) 
						{
							String key = enumKey.nextElement();
							sum += aSentence.get(key);				
						}//while
						
						//Make the division
						sw[j][i] =(double) (aSentence.get(s1))/(double)(sum);   //found in the sentence
					}//if
					else
						sw[j][i] = 0;	//not found in the sentence
		            }//for			      
			}//for
	}

	/**
	*  This method applies the reinforcement algorithm to get the score for each sentence and word
	*  
	*  @param summarizer_obj: the summarizer object 
	*  @param stop_iteration: number of iterations
	*  @param Text:the input text details
   *   @param size_unit: the unit of summary length
   *   @param summary_limit: maximum summary size
   *   @param first_city_number: ants' initial positions
    */
	void apply_reinforcement(Summarizer summarizer_obj, int stop_iteration, Text result_obj, int summary_limit, String size_unit,List<Integer> first_city_number)  
	{	
		int words, sentences;
		double alpha, beta, error_threshold;
		boolean  no_error;
		
		//Retrieve the sizes
		sentences = ss.length;
		words = ww.length;
				
		double[][] current_u = new double[sentences][1];
		double[][] current_v = new double[words][1];
	    double[][] previous_u;
	    double[][] previous_v;	
		
		//Set alpha, beta, and error
		alpha= beta = 0.5;
		error_threshold = 0.0001;
		
		//Preparing the three matrices
		double[][] transposed_normalize_U = new double[sentences][sentences];
		double[][] transposed_normalized_V = new double[words][words];
		double[][] transposed_normalized_W_T = new double[words][sentences];
		double[][] transposed_normalized_W = new double[sentences][words];
		
		
		transposed_normalize_U = utility.transposeMatrix(utility.normalize_matrix(ss));	
		transposed_normalized_V = utility.transposeMatrix(utility.normalize_matrix(ww));	
		transposed_normalized_W = utility.transposeMatrix(utility.normalize_matrix(sw));
		transposed_normalized_W_T = utility.transposeMatrix(utility.normalize_matrix(utility.transposeMatrix(sw)));
		
		//Preparing the ranking arrays (u and v); used in the computation
		for (int i = 0; i < words; ++i) 
	          current_v[i][0] = 1.0;
		            
	    for (int i = 0; i < sentences; ++i)
	         current_u[i][0] = 1.0;
	
		//Start the iterative process
		do{		
			
			no_error = true;
			previous_u = current_u;
			previous_v = current_v;  
		    
			//First part (compute current u)		
           current_u = utility.addMatrix(utility.multScalar((utility.multMatrix(transposed_normalize_U, previous_u)),alpha),  utility.multScalar((utility.multMatrix(transposed_normalized_W_T, previous_v)),beta));
      
            //Normalize u
            current_u = utility.normalize_array(current_u);	
  
            //Second part (compute current v)            
            current_v = utility.addMatrix(utility.multScalar((utility.multMatrix(transposed_normalized_V, previous_v)),alpha),  utility.multScalar((utility.multMatrix(transposed_normalized_W, previous_u)),beta));

            //Normalize v
            current_v = utility.normalize_array(current_v);	
  
            for (int i = 0; (i<current_u.length) && (no_error); i++)
            	if (Math.abs(current_u [i][0]-previous_u[i][0])>=error_threshold)
            		no_error = false;
	         
            for (int i = 0; (i<current_v.length) && (no_error); i++)
            	if (Math.abs(current_v [i][0]-previous_v[i][0])>=error_threshold)
            		no_error = false;           
   	
		}while (!no_error);

		//Prepare the summarizer by creating the required initial data for maximizing the coverage 
		
		//Apply the first sentence overlap on the word if this this feature has been chosen
		if (Public.first_sentence_overlap_weight!=1)
		{
			for (int i=0; i<current_v.length;i++)   //for each word
			{
				if (result_obj.words_in_first_sentences.containsKey(words_list.get(i)))
					current_v[i][0] = Public.first_sentence_overlap_weight * current_v[i][0];
			}//for
		}//if
		summarizer_obj.bulid_word_list(current_v, sw, words_list);
		summarizer_obj.bulid_temp_sentence_list(result_obj);
	}	
}
