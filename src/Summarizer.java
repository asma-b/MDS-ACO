/**
 * Summarizer.java
 * Purpose: This class selects the sentences based on the coverage of the weighted words by calling the ACS summarizer
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class Summarizer {

	//Words scores
	Hashtable<String,word_coverage_obj> word_score=new Hashtable<String,word_coverage_obj>();	
	
	//Sentence list to be ranked
	Hashtable<Integer,sentence_coverage_obj> sentence_toBe_ranked=new Hashtable<Integer,sentence_coverage_obj>();	
	
	//Ranked sentences
	List<sorted_list> sentence_degree_scores = new ArrayList<sorted_list>();
	
	/**
	   *  This method builds the initial value of the list of sentences along with their content score object
	   *  @param text_obj: The details of input text
	   */
	public void bulid_temp_sentence_list(Text text_obj)
	{
		//Building sentence_toBe_ranked 
		
		sentence_coverage_obj aSentence;    
		int sent_index; 
		
		for (int i=0; i< text_obj.seg_text_sent_list.size();i++)
		{  
			aSentence = new sentence_coverage_obj();
			double total_coverage_score = 0;			//The total score for each sentence
					
			//Sentence index (Key)  
			sent_index = (text_obj.seg_text_sent_list.get(i).senNum)-1;  
						
			//Value (words list)
			aSentence.tokens= new Hashtable<String,Integer>();
			aSentence.tokens.putAll(text_obj.seg_text_sent_list.get(i).tokens);
					
			//Value (coverage score)
			Enumeration<String> enumKey = aSentence.tokens.keys();
			while(enumKey.hasMoreElements()) 
			{
				String aWord = enumKey.nextElement();
				total_coverage_score += word_score.get(aWord).score;
			}//while
			aSentence.score = total_coverage_score;
					
			//Add the object 
			sentence_toBe_ranked.put(sent_index, aSentence);
		}//for	
	}//for
	
	/**
	   *  This method builds the initial value of the list of words, which contains: Content score for this word and
	   *   the list of sentences that contain this word.
	   *   @param current_v: the score of each word
	   *   @param sw: sentence-to-word relationship matrix
	   *   @param words_list: list of words
	   */	
	
	public void bulid_word_list(double[][] current_v, double [][] sw, List<String> words_list)
	{
		for (int i=0; i<current_v.length;i++)   //for each word
		{			
			word_coverage_obj aWord = new word_coverage_obj();
			aWord.score  = current_v[i][0];
			
			for (int j=0; j<sw.length; j++)  //each sentence
				if (sw[j][i] != 0)
					aWord.sentences_list.add(j);
			
			word_score.put(words_list.get(i), aWord);
		}//for
	}
	
	/**
	   *  This method undertakes the selection process of summary sentences based on the word coverage scores using ACS
	   *   @param stop_iteration: number of iteration
	   *   @param text_obj: the details of input text
	   *   @param summary_limit: maximum summary length
	   *   @param size_unit: the unit of summary length (word, byte, etc.)
	   *   @param first_city_number: the initial positions of ants
	   *   @param total_greedy_score: the overall content score of the greedy summary
	   *   @param greedy_sent_number: the number of sentences in the greedy selected summary
	   *   
	   *   @return list of summary sentences
	   */	
	public List<Integer> calculate_coverage_result_by_ant(int stop_iteration, Text text_obj, int summary_limit, String size_unit, List<Integer> first_city_number, double total_greedy_score, int greedy_sent_number)
	{	
		List<Integer> summary_sentecnes_number = new ArrayList<Integer>(); //result: only summary sentences; sorted for the order
				
		double pheromone_initial_value = (double) total_greedy_score /  (double) greedy_sent_number; //The initial value of the pheromone = total score of sentences in the greedy summary multiplied by (1/number of sentences in the summary)
			
		ACS ACS_obj = new ACS (sentence_toBe_ranked, stop_iteration, pheromone_initial_value, first_city_number, text_obj);  
		summary_sentecnes_number = ACS_obj.ASC_search(text_obj, word_score, sentence_toBe_ranked, size_unit, summary_limit);
			    
		return (summary_sentecnes_number);
	}
		
	/**
	   *  This method undertakes the selection process of summary sentences based on the word coverage scores using greedy approach
	   *   @param text_obj: the details of input text
	   *   @param summary_limit: maximum summary length
	   *   @param size_unit: the unit of summary length (word, byte, etc.)
	   *   
	   *   @return list of summary sentences
	   */	
	public List<sorted_list> calculate_coverage_result(Text text_obj, int summary_limit, String size_unit)
	{	
		int bestSentence;			//best sentence index
		double current_best_score;	//best score
		int remaining_summary_length = summary_limit;	//the remaining byte to the summary to be full
		int current_sentence_length = 0;
		int current_best_length =0;		
		List<sorted_list> sentence_coverage_scores = new ArrayList<sorted_list>(); //result: only summary sentences	
		double covg_heu;
		double candidate_heu;
		
		while(!sentence_toBe_ranked.isEmpty())   //while there are sentences to be chosen
		{
			//Get the sentence with the highest score (greedy+linear search)
			Enumeration<Integer> enumKey3 = sentence_toBe_ranked.keys();
			Integer aSentence = enumKey3.nextElement();
			
			if (size_unit.equals("charecter"))
				current_sentence_length = text_obj.seg_text_sent_list.get(aSentence).sent.length();
			
			else  //words
			{
				String[] words = text_obj.seg_text_sent_list.get(aSentence).sent.split("\\s+"); // match one or more spaces
				 current_sentence_length= words.length;
			}//else

			
			if (current_sentence_length > remaining_summary_length) 
			{
				//remove this sentence
				sentence_toBe_ranked.remove(aSentence);
			}//if
			else
			{
				bestSentence = aSentence;
				covg_heu = sentence_toBe_ranked.get(bestSentence).score; 
				current_best_score = covg_heu;
		
				if (size_unit.equals("charecter"))
					current_best_length = text_obj.seg_text_sent_list.get(bestSentence).sent.length();
			                          
				else  //words
				{
				 String[] words = text_obj.seg_text_sent_list.get(aSentence).sent.split("\\s+"); // match one or more spaces
				 current_best_length= words.length;
				}//else
			
				while(enumKey3.hasMoreElements()) 
				{
					aSentence = enumKey3.nextElement();
					if (size_unit.equals("charecter"))
						current_sentence_length = text_obj.seg_text_sent_list.get(aSentence).sent.length();                      
					else  //words
					{
						 String[] words = text_obj.seg_text_sent_list.get(aSentence).sent.split("\\s+"); // match one or more spaces
						 current_sentence_length= words.length;
					}//else
					
					 covg_heu = sentence_toBe_ranked.get(bestSentence).score;					//coverage heuristic and possibly the first sentence				
					 candidate_heu =  covg_heu;
					 if (current_sentence_length > remaining_summary_length)
						 sentence_toBe_ranked.remove(aSentence);
					
					else if ((candidate_heu)>current_best_score)
					{
						bestSentence = aSentence;
						current_best_score = candidate_heu;
						
						if (size_unit.equals("charecter"))
							current_best_length = text_obj.seg_text_sent_list.get(bestSentence).sent.length();                          
						else  //words
						{
							 String[] words = text_obj.seg_text_sent_list.get(bestSentence).sent.split("\\s+"); // match one or more spaces
							 current_best_length= words.length;
						}//else
					}//else
			}//while
				
			//Add the sentence
			remaining_summary_length -=current_best_length;
			sorted_list sorted_list_obj = new sorted_list(bestSentence+1, current_best_score, current_best_length); 
			sentence_coverage_scores.add(sorted_list_obj);
		
			//Update other score
			sentence_coverage_obj selected_sentence =  new sentence_coverage_obj();
			selected_sentence =  sentence_toBe_ranked.get(bestSentence);   
			
			//delete the selected sentence from sentence_toBe_ranked
			sentence_toBe_ranked.remove(bestSentence);
			Enumeration<String> enumKey2 = selected_sentence.tokens.keys();
			while(enumKey2.hasMoreElements())  //each word
			{
				String aWord = enumKey2.nextElement();
				int aSentence2;     //sentence index
				for (int i=0; i<word_score.get(aWord).sentences_list.size(); i++)  //each sentence that contains the word
				{						
					aSentence2 = word_score.get(aWord).sentences_list.get(i);
					if(sentence_toBe_ranked.containsKey(aSentence2))  
					{
						//update the tokens
						sentence_toBe_ranked.get(aSentence2).tokens.remove(aWord);
						//update the score
						sentence_toBe_ranked.get(aSentence2).score -= word_score.get(aWord).score;
					}//if	
				}//for
			}//while
		}//else
	}//while
	return (sentence_coverage_scores);
	}
}
