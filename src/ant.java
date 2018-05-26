/**
 * ant.java
 * Purpose: This class represents the ant object
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class ant {

	int first_city;									//First city in the ant's path
	int current_city;								//Ant's current position (city)
	List<Integer> tour = new ArrayList<Integer>();	//Ant's path (solution) 
	double tour_score = 0;							//The total score of the path 
	
	//the set of the cities that remain to be visited
	Hashtable<Integer,sentence_coverage_obj> to_be_vistited=new Hashtable<Integer,sentence_coverage_obj>();			

	int remaining_summary_length ; //Remaining summary length
	
	/**
	*  Constructor to initialize the ant object 
	*  @param first_city_number: List of ants' first positions
	*  @param total_sent_num: number of sentences
	*  @param sentence_toBe_ranked: set of sentences that can be added to the summary
	*  @param summary_limit: maximium summary length
	*/
	ant(int first_city_number, int total_sent_num, Hashtable<Integer,sentence_coverage_obj> sentence_toBe_ranked, int summary_limit)
	{
		first_city = first_city_number;						//Set the first position	
		
		//Create the neighbors for this ant		
		Enumeration<Integer> enumKey3;
		enumKey3 = sentence_toBe_ranked.keys();

		while(enumKey3.hasMoreElements()) 
		{
			int aSentence = enumKey3.nextElement();
			sentence_coverage_obj obj = new sentence_coverage_obj(); 		//to track the coverage score while moving
			obj.score = sentence_toBe_ranked.get(aSentence).score;					
			obj.tokens.putAll(sentence_toBe_ranked.get(aSentence).tokens);
			to_be_vistited.put(aSentence, obj);								 //to track the vertices that can move to... 
		}//while
		
		remaining_summary_length = summary_limit;		//the remaining length to the summary to be full
	}
	
	/**
	*  This method is for removing the selected sentence as well as the scores of its words from the unselected sentences 
	*  @param word_score: words scores object
	*/
	void local_update_content_score(Hashtable<String,word_coverage_obj> word_score)
	{	
		//Update the content scores
		sentence_coverage_obj selected_sentence =  new sentence_coverage_obj();  
	
		selected_sentence.score =  to_be_vistited.get(current_city).score;   //To be used to update the content coverage value
		selected_sentence.tokens.putAll(to_be_vistited.get(current_city).tokens);
		
		to_be_vistited.remove(current_city);   					// Remove the selected sentence	
		
		Enumeration<String> enumKey2 = selected_sentence.tokens.keys();
		while(enumKey2.hasMoreElements())  //each word
		{
			String aWord = enumKey2.nextElement();
			int aSentence2;     //sentence index
			for (int i=0; i<word_score.get(aWord).sentences_list.size(); i++)  //For each sentence that contains the word
			{							
				aSentence2 = word_score.get(aWord).sentences_list.get(i);
				if(to_be_vistited.containsKey(aSentence2))  
				{				
					if (to_be_vistited.get(aSentence2).tokens.containsKey(aWord))//Check if the word is not already removed by another selected sentence
					{
						to_be_vistited.get(aSentence2).tokens.remove(aWord); //update the tokens				
						to_be_vistited.get(aSentence2).score -= word_score.get(aWord).score; //update the score
					}//if
				}//if		
			}//for
		}//while	
	}
	
	/**
	*  This method is for updating the remaining summary length for this ant 
	*  @param size_unit: the unit of summary length
    *  @param Text:the input text details
	*/
	void local_update_capacity(String size_unit, Text result_obj)
	{		
		int current_city_length;
		
		if (size_unit.equals("charecter"))
			current_city_length = result_obj.seg_text_sent_list.get(current_city).sent.length();	
		else  //words
		{
			 String[] words = result_obj.seg_text_sent_list.get(current_city).sent.split("\\s+"); // match one or more spaces
			 current_city_length= words.length;
		}//else	
		remaining_summary_length -=current_city_length;
	}
	
	/**
	*  This method searches for the next best sentence (i.e. city)
    *  @param Text:the input text details
	*  @param size_unit: the unit of summary length
    *  @param summary_limit: maximum summary size
    *  @param ACS_graph: The internal graph representation of input text
    *  @param beta: an ACS's parameter
     * @param Q0:  an ACS's parameter
     * 
     * @return best city (i.e., sentence)
	*/
	best_city get_next_city(Text result_obj, String size_unit, int summary_limit, ant_graph ACS_graph, double beta, double q0)
	{
		double best_heu = 0; //to be use to calculate the best tour		
		
		best_city best_city_obj = new best_city();
		//best sentence related variables
		int bestSentence = -1;				
		double bestSentence_score = -1;	
		
		//Current sentence related variables
		int candidate_sentence = -1;
		int candidate_sentence_length = 0;
		double candidate_sentence_score = 0;

		double denominator = 0.0, nominator =0.0;
		double current_pher, candidate_heu;
		
		Random random1 = new Random();
		double q =  random1.nextDouble(); 
		
		Random random2 = new Random();
		double random_eq1; 
	
		if (q <= q0)  //equation 3 (exploitation) 
		{
			Enumeration<Integer> enumKey ;
			enumKey = to_be_vistited.keys();
			
			while(enumKey.hasMoreElements()) 
			{			
				candidate_sentence = enumKey.nextElement();
		
				//Check if it fits within the remaining length
				if (size_unit.equals("charecter"))
					candidate_sentence_length = result_obj.seg_text_sent_list.get(candidate_sentence).sent.length();	
				else  //words
				{	
					 String[] words = result_obj.seg_text_sent_list.get(candidate_sentence).sent.split("\\s+"); // match one or more spaces
					 candidate_sentence_length= words.length;
				}//else
				
				if (candidate_sentence_length > remaining_summary_length)
				{
					to_be_vistited.remove(candidate_sentence); 			//remove this sentence
				}//if
				
				else   //Make the comparison between the current sentence and the best-so-far sentence
				{		
					//Get the nominator (eq. 3)
					
					//Get the pheromone of the candidate edge
					current_pher = ACS_graph.A_graph.get_edge(current_city,candidate_sentence); 
					
					//Get the heuristic of the candidate edge 
					double covg_heu = (to_be_vistited.get(candidate_sentence).score);
					 
					candidate_heu =  covg_heu;
					
					nominator = (current_pher) *  Math.pow( (candidate_heu), beta ) ;
					
					if (nominator > bestSentence_score)  //Best than the best sentence, then update it
					{						
						bestSentence = candidate_sentence;
						bestSentence_score = nominator;    	//from the rule	 (includes the pheromone)
						best_heu = candidate_heu; 			//to be use to calculate the best tour(does not include the pheromone))
					}//if
				}//else	
			}//while
		}//if; end eq. 3
		
		else		//equation 1 (biased exploration)
		{
			//Get the denominator first
			Enumeration<Integer> enumKey2 ;
			enumKey2 = to_be_vistited.keys();
			denominator = 0.0;
			while(enumKey2.hasMoreElements()) 
			{
				Integer another_sentence = enumKey2.nextElement();
				
				double covg_heu = (to_be_vistited.get(another_sentence).score);					//coverage heuristic and possibly the first sentence
				candidate_heu = covg_heu;			//heuristic
				current_pher = ACS_graph.A_graph.get_edge(current_city,another_sentence);		//pheromone
				denominator+=( (current_pher) * (Math.pow( (candidate_heu), beta ))) ;	
			}//wile
			
			 random_eq1 =  random2.nextDouble(); 
				
			//Begin the comparison with the random number random_eq1; if no one found, then take the largest
			Enumeration<Integer> enumKey ;
			enumKey = to_be_vistited.keys();
				
			while(enumKey.hasMoreElements()) 
			{			
				candidate_sentence = enumKey.nextElement();
				
				//Check if it fits within the remaining length
				if (size_unit.equals("charecter"))
					candidate_sentence_length = result_obj.seg_text_sent_list.get(candidate_sentence).sent.length();	
				else  //words
				{	
					 String[] words = result_obj.seg_text_sent_list.get(candidate_sentence).sent.split("\\s+"); // match one or more spaces
					 candidate_sentence_length= words.length;
				}//else
				
				if (candidate_sentence_length > remaining_summary_length)
					to_be_vistited.remove(candidate_sentence); 			//remove this sentence
				
				//Make the comparison between the current sentence and the random number
				else 
				{
					//Pick the largest one; (i.e. nearest to the random number)
					//Get the nominator (eq. 1)
					
					//Get the pheromone of the candidate edge
					current_pher = ACS_graph.A_graph.get_edge(current_city,candidate_sentence); 
					
					//Get the heuristic of the candidate edge 
					double covg_heu = to_be_vistited.get(candidate_sentence).score;	
					candidate_heu = covg_heu;			//heuristic				
					nominator = (current_pher) * Math.pow( (candidate_heu),  beta ) ;				
					candidate_sentence_score = (double) nominator / (double) denominator;
				
					if (candidate_sentence_score >= random_eq1)  //Best than the best sentence, then update it
					{					
						bestSentence = candidate_sentence;
						best_city_obj.city_index = bestSentence;
						best_city_obj.city_score = candidate_heu;
						return best_city_obj;		
					}//if
					else
					{  
						bestSentence = candidate_sentence;
						bestSentence_score = nominator;  
						best_heu = candidate_heu;		
					}//else
				}//else	
			}//while	
		}	
		
		best_city_obj.city_index= bestSentence;
		best_city_obj.city_score = best_heu;
			
		return best_city_obj;
	}
	
	/**
	*  This method adds a city to the ant tour and set all the related data
     * @param aCity:  city to be added
	*/
	void add_city(best_city aCity)
	{
		current_city = aCity.city_index;					//Set the current city where the ant stand
		Integer toure_obj = new Integer (current_city);
		tour.add(toure_obj)	;								//add the selected path to the tour
		tour_score+= aCity.city_score;						 //Update the tour score		
	}
}
