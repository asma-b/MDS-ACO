/**
 * Text.java
 * Purpose: This class holds all details of the input text
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

import java.io.Serializable;

public class Text implements Serializable  {
	private static final long serialVersionUID = 1L;
	Hashtable<String,Integer> words_in_first_sentences = new Hashtable<String,Integer>();   //List of first words in first sentences (the values in the list is dummy values)
	List<CoreMap> text_sentences;											//input sentences
	List<Sentence> seg_text_sent_list=new ArrayList<Sentence>();	    	//The segmented text sentences 
	Hashtable<String,Integer> df = new Hashtable<String,Integer>();   		 //The df values
	Hashtable<String,Float> idf = new Hashtable<String,Float>();  			// The isf value for each word
	Hashtable<String,document> documents=new Hashtable<String,document>();	//To link each document by its name with their sentences

	/**
	   *  This method computes the df values based on sf
	   *  @param sf: list of sf
	   */
	public void compute_df( Hashtable<String,Integer> sf)  
	{
		Enumeration<String> enumKey = sf.keys();
		
		while(enumKey.hasMoreElements()) 
		{
			int Myfreq;
			String key = enumKey.nextElement();  
			if(df.containsKey(key))				 //not a new token
			{
				Myfreq = df.get(key)+1;					
				df.put(key,Myfreq);				 
			}//if
			else 								//new token, this is the first document that contains this token
			{
				Myfreq = new Integer(1);	
				df.put(key,Myfreq);	
			}//else
		}
	}
	

	/**
	   *  This method computes the isf values based on sf values in result class
	   */
	public void compute_idf()  
	{
		Enumeration<String> enumKey;
		Integer df_value = new Integer(0);
		double idf_value = new Float(0);
		enumKey = df.keys();
			
		while(enumKey.hasMoreElements()) 
		{
			String aWord = enumKey.nextElement();
			df_value= df.get(aWord);
			idf_value = 1+(Math.log10(documents.size()/(double)df_value));
			idf.put(aWord, (float) idf_value);
		}//while
	}
	
	/**
	   *  This method computes  the word frequency to segmented sentences		
	   */
	void compute_word_frequency(Hashtable<String,Integer>  wordFreq_text, String lemma)
	{
		Integer freq = new Integer(1);	 
		if(!wordFreq_text.containsKey(lemma))  		  //New token
		{
			 freq = new Integer(1);				    
			 wordFreq_text.put(lemma,freq);	
		}//if
		else											//Not new
		{
			freq = wordFreq_text.get(lemma)+1;
			wordFreq_text.put(lemma,freq);	
		}//else
	}
	
	/**
	   *  This method computes the word frequency to segmented sentences (text) in a certain document (TF)	
	   *  @param lemma: word
	   *  @param d: input document
	 */ 
	  void compute_TF(String lemma, document d )
	  {
		  Integer freq = new Integer(1);
		  if(!d.tf_text.containsKey(lemma))	//New token
		   {
				 freq = new Integer(1);				    
				 d.tf_text.put(lemma,freq);	
		   }//if
		 else								//Not new
			 {
				freq = d.tf_text.get(lemma)+1;
				d.tf_text.put(lemma,freq);	
			}//else
	  }
	  
		/**
	   *  This method stems the token
	   *  @param lemma: word
	   *  @return String: stemmed word
	 */ 
	  //Stem the token
	  String stem_token (String lemma)
	    { 
			Stemmer s = new Stemmer();  //stemmer object
			String stemmed; 
			  
	    	for (int k = 0; k <lemma.length() ; k++) 
	    		s.add(lemma.charAt(k));
	    	    	
	    	  s.stem();
	    	  stemmed = s.toString();
	    	  lemma = stemmed;     
	    	  
	    	  return lemma;
	    }	
	  
		/**
	   *  This method stores the output of the pre-processed text
	   *  @param stopWords: list of stop words
	   *  @param Fname: document name
	   *  @param current_number_text_sentence: sentence number
	   *  
	   *  @return integer: sentence number
	 */ 
		public int build_text_result(Hashtable<String,Integer> stopWords, String Fname,  int current_number_text_sentence) 
		{
			Hashtable<String,Integer> wordFreq_text = new Hashtable<String,Integer>();  //The frequency of each token in the document text
			
			Integer freq, freq2;
			Sentence aSentence = null; 
			ArrayCoreMap sentence;
		    List<CoreLabel> tokens;
		    boolean needToStem;
		     
		    Hashtable<String,Integer> sf = new Hashtable<String,Integer>();    //The sf value for a current document to be used to compute the df
		    
		    int previous_number_text_sentence = current_number_text_sentence;
		    
			 document d = new document();
			 d.first_sent_index=previous_number_text_sentence-1;
			
			//Extract segmented sentences from text
			 if (text_sentences != null && text_sentences.size() > 0) 
			 { 	
				 boolean a_first_sentence = true;  //to be change after setting the first one
			      for (int i=0; i<text_sentences.size();i++)
			      {
			    	  aSentence = new Sentence(); 
			    	  aSentence.docNum = Fname;	//Document number (i.e. file name) of segmented sentence 
			    	  aSentence.senNum = current_number_text_sentence;	 //Order of the sentence inside the text section
			    	  aSentence.first = a_first_sentence;
			    	  
					  //Get the segmented sentence
			    	  sentence = (ArrayCoreMap) text_sentences.get(i);
			    	  aSentence.sent = sentence.toString();
			    	  
			    	  current_number_text_sentence++;
			    
			    	//Extract tokens 
			    	  tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
			    	  
			    	  if ((Public.corpora.equals("Multiling_2011_Arabic") ))
			    	  {
			    		  //Handle Arabic text
			    		  for (CoreLabel token: tokens) 
						  {
			    			  //1) Extract the token the token
			    			  String aToken = token.toString();
			    			  aToken = ((aToken.split("-"))[0]);	
			    				 
							  if (aToken.matches(".*[ء-ي0-9]+.*")) //if it is a word (i.e., alphanumeric), not symbol such as full stop
								  aSentence.word_number++;  //increase the number f words

							  boolean hasAtLeastOneAlpha;
							   	
							   		hasAtLeastOneAlpha = aToken.matches(".*[ء-ي0-9]+.*"); // this is for only alphanumeric
							   
							   	 //2) Check if it is not a stop word
							     if ((stopWords.get(aToken)==null) )  //not a stop word
							     {
							    	//3) check if there is a need to apply stemming (7Khoja’s stemmer) on the token
									//Conditions: Has only letters
								    if ( aToken.matches("[ء-ي]+")) //need to be stemmed
								    {
								    	   ArabicStemmer Stemmer=new ArabicStemmer();
								    	   aToken = Stemmer.stemWord(aToken);
								    }
								    //4)Add the token if it is alphanumeric; e.g. do not add token with only full stop
								    if (hasAtLeastOneAlpha)
								    {
								    	 //Add the token (w) to sentence (s) and compute TF(w,s)
								    	  if(!aSentence.tokens.containsKey(aToken)) 	 //new token
									     {
									    		 //Add the token to sentence's token hashtable
										    	 freq = new Integer(1);	
										    	 freq2 = new Integer(1);	
										    	 aSentence.tokens.put(aToken,freq);
					
										    	 if (a_first_sentence)
										    		 words_in_first_sentences.put(aToken, 0);						    	 
										    	 
												 //Compute the (sf) value for this token
										    	 if(!sf.containsKey(aToken)) 	//New, then add it to the list
										    		 sf.put(aToken,freq2);
										    	 else 	 //Not new, then increase its value in the list
										    	 {
										    		freq2 = sf.get(aToken)+1;					
													sf.put(aToken,freq2);
										    	 }//else
									     	}//if
											else  //Not new, increase its frequency inside the sentence
											{
												freq = aSentence.tokens.get(aToken)+1;					
												aSentence.tokens.put(aToken,freq);	
											}//else				    			  
								
								    	  	//Compute the word frequency to segmented sentences	
								    	   compute_word_frequency(wordFreq_text, aToken);
								    	  
										   //Compute the word frequency to segmented sentences (text) in a certain document (TF)
								    	  compute_TF( aToken,  d );  
								    }//if
							     }//if						   	 		    		  
						  }//for
			    	  }//if
			    	  else  //handle English text
			    	  {
			    		  for (CoreLabel token: tokens) 
						  {							   
							 // Extracting lemma (CoreNLP)
						   	 String lemma = new String();
						   	 lemma = token.get(CoreAnnotations.LemmaAnnotation.class); 		  
						   	 
							  if (lemma.matches(".*[a-zA-Z0-9]+.*")) //if it is a word (i.e., alphanumeric), not symbol such as full stop
								  aSentence.word_number++;  //increase the number f words
							  
							  boolean hasAtLeastOneAlpha;
						   	  hasAtLeastOneAlpha = lemma.matches(".*[a-zA-Z0-9]+.*"); // this is for only alphanumeric
						    		
						    // Applying stemming (PorterStemmer) on the lemmas
						    // Conditions: (1) Has only letters  (2) begin with small letter
					
						    needToStem = (lemma.matches("[a-zA-Z]+")) && (!Character.isUpperCase(lemma.charAt(0)));//the second condition makes some examples fails such as i
						    	      
						    if (needToStem)
						    	lemma = stem_token (lemma);
					
						     if ((stopWords.get(lemma)==null) && (hasAtLeastOneAlpha))       //3) Eliminate the stop words and symbols
						     {
						    	 //Add the token (w) to sentence (s) and compute TF(w,s)
						    	  if(!aSentence.tokens.containsKey(lemma)) 	 //new token
							     {
						    		 //Add the token to sentence's token hashtable
							    	 freq = new Integer(1);	
							    	 freq2 = new Integer(1);	
							    	 aSentence.tokens.put(lemma,freq);
							  
							    	 if (a_first_sentence)
							    		 words_in_first_sentences.put(lemma, 0);
							    	
							    	 //Compute the (sf) value for this token
							    	 if(!sf.containsKey(lemma)) 	//New, then add it to the list
							    		 sf.put(lemma,freq2);
							    	else 	 //Not new, then increase its value in the list
							    	{
							    		freq2 = sf.get(lemma)+1;					
										sf.put(lemma,freq2);
							    	 }//else
							     }//if
								else  //Not new, increase its frequency inside the sentence
								{
									freq = aSentence.tokens.get(lemma)+1;					
									aSentence.tokens.put(lemma,freq);	
								}//else				    			  
						
						    	//Compute the word frequency to segmented sentences	
						    	compute_word_frequency(wordFreq_text, lemma);
						    	  
								//Compute the word frequency to segmented sentences (text) in a certain document (TF)
						    	compute_TF( lemma,  d ); 
						   	  }//if
						    }//for  
			    	  	}//else
				    	  (seg_text_sent_list).add(aSentence);		   
				    	  a_first_sentence = false;  //next sentence not a first sentence in the document
				     }//for
				}//if
			    
			    d.last_sent_index=current_number_text_sentence-2;    //begin with zero
			
			    //build the df hash table from sf
			    compute_df(sf);
			    
				documents.put(Fname, d);
				
			    return current_number_text_sentence;
		}
}
