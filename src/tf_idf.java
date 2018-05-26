/**
 * tf_idf.java
 * Purpose: This class calculates the tf-idf matrix between documents and words. Also, it calculates
 *  and store the total cosine similarity for each sentence.
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;


public class tf_idf implements Serializable  {
	private static final long serialVersionUID = 1L;
	List<Hashtable<String,Float>> tf_idf=new ArrayList<Hashtable<String,Float>>();	 //2d matrix to store tf-isf
	
	/**
	   *  Constructor to initialize each element with zero
	   *  @param sen_num: number of sentences
	   *  @param documents: input documents 
	   *  @param df: the df values
	     */
		 public tf_idf(int sen_num, Hashtable<String,document> documents, Hashtable<String,Integer> df)   
		{
			Float zero;
			Enumeration<String> enumKey;
			Hashtable<String,Float> sentence = null;

			for (int i=0; i<sen_num;i++)  				 //for each documents
			{
				enumKey = df.keys();  					//traverse the frequency hashtable to get the words
			
				sentence= new Hashtable<String,Float>();
		
				while(enumKey.hasMoreElements())     //each word 
				{
					String key = enumKey.nextElement();		
					zero = new Float(0.0);
					sentence.put(key, zero);				
				}//while		
				tf_idf.add(sentence);
			}//for
		}
	/**
	   *  This method calculates the cosine similarity between two vectors
	   *  @param vector1: the first vector
	   *  @param vector2: the second vector
	   *  
	   *  @return double: cosine similarity value
	   */
	double cosine(Hashtable<String,Float>vector1, Hashtable<String,Float>vector2)
	{
		 double dotProduct = 0.0;
		 double norm1 = 0.0;
		 double norm2 = 0.0;
		 Enumeration<String> enumKey = vector1.keys();
		 
		 double cosine_value = 0.0;
		  
		 while(enumKey.hasMoreElements()) 
		 {
			String key = enumKey.nextElement();
		        
			 dotProduct += vector1.get(key) * vector2.get(key);
		     norm1 += Math.pow(vector1.get(key), 2);
		     
		     norm2 += Math.pow(vector2.get(key), 2);
		 }//while   
		 
		 double dem =  (Math.sqrt(norm1) * Math.sqrt(norm2));
		 
		 if (dem!=0)
			 	 cosine_value = dotProduct / dem;
		 
		 return cosine_value;
	}	
	
	/**
	   *  This method fills the graph with the similarity between each two sentences (if not less than the threshold). Also, it 
	   *  calculates and fills sentence_cosine_scores with total similarity score	 
	   *  @param cosine_tf_isf_graph: cosine similarity matrix (graph)
	   */
	void calculate_cosine_based_score(graph cosine_tf_isf_graph)
	{ 
		double single_score;

		for (int first=0; first<tf_idf.size();first++)     //Each sentence in the matrix
		{	
			for (int second=first; second<tf_idf.size();second++)     //with each other sentence  
			{
				//Retrieve the vectors of two sentences and calculate the cosine similarity between them
				single_score = cosine(tf_idf.get(first),tf_idf.get(second));  
				
				//Add it to the graph				
				cosine_tf_isf_graph.add_edge(first, second, single_score);
			}//for	
		}//for	
	}
	
	/**
	   *  This method computes the tf-isf values
	   *  @param text_obj: the details of input text
	   */
	public void compute_tf_idf_matrix(Text text_obj) 
	{
		int tf;
		float idf;
		Enumeration<String> enumKey;
		Hashtable<String,Integer> Tokens;
		
		for (int i=0; i<tf_idf.size();i++)    //for each sentence 
		{	
			//get the sentence's tokens
			Tokens = text_obj.seg_text_sent_list.get(i).tokens;
			
			enumKey = Tokens.keys();   			
			while(enumKey.hasMoreElements())       			//each word inside this sentence (aToken)
			{
				String aToken = enumKey.nextElement();		
				tf = Tokens.get(aToken);  					 //The TF value of the token
				idf = text_obj.idf.get(aToken);  			//The ISF value of the token
				tf_idf.get(i).put(aToken, (tf*idf));		//Compute and store a tf-isf score					
			}//while
		}//for
	}
}
