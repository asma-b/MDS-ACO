/**
 * ACS.java
 * Purpose: This class represents the implementation of the ACS algorithm.
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class ACS {
	int m;													 	//Number of ants 
	double beta, rho, q0, alpha; 								//ACS parameters
	ant_graph ACS_graph;										//The graph of ACS
	int iteration_limit;										//The stop condition; maximum number of iterations
	List <ant> ant_list = new ArrayList<ant> ();				//The list of ant objects
	Set<Integer> active_ant = new HashSet();					//To keep tack of which ants that need to complete their tours; the ant that reaches the summary limit will be removed
	double pheromone_start_value;								//Staring value of the pheromone 
	List<Integer> ant_positions = new ArrayList<Integer> ();  	//To keep the first city of each ant
	List<Integer> best_so_far_tour = new ArrayList<Integer>();	//Ordered list of best so far tour cities visited
	double best_so_far_tour_score = -1000;						//The total score of best so far tour
	double termination_value;
	
	/**
   *  Constructor to initialize the ACS algorithm
   *  @param sentence_toBe_ranked: set of sentences that can be added to the summary
   *  @param number_of_iteration: number of iterations
   *  @param pheromone_initial_value: the initial value of the pheromone trails
   *  @param first_city_number: List of ants' first positions
   *  @param Text:the input text details
     */
	ACS (Hashtable<Integer,sentence_coverage_obj> sentence_toBe_ranked, int number_of_iteration, double pheromone_initial_value, List<Integer> first_city_number, Text result_obj)
	{
		//Set ACS parameters
		beta = 2;
		alpha =0.1;
		rho =0.1;
		q0 = 0.9;
		
		//Set the number of iterations 
		iteration_limit = number_of_iteration; 
		
		//Set the initial values of pheromone trails
		pheromone_start_value = pheromone_initial_value;
			
		//Set the number of ants; an ant on each sentence
		m = result_obj.seg_text_sent_list.size();  	
		
		//Preparing the first positions of ants
		Enumeration<String> enumKey1;
		enumKey1 = result_obj.documents.keys();
		while(enumKey1.hasMoreElements()) 
		{
			int first, last;
			String aDocument = enumKey1.nextElement();
						
			//Get the first and the last sentence in the document
			first = result_obj.documents.get(aDocument).first_sent_index;
			last =result_obj.documents.get(aDocument).last_sent_index;
				
			//Add the position of each ant
			for (int i= first; i<=last; i++)					
				ant_positions.add(i);	
			
		}//while
	}
	
	
	 /**
    *  A method to create the ants and set the first position for each of them
    * @param Text:the input text details
   *  @param word_score: word scores details
   *  @param sentence_toBe_ranked: set of sentences that can be added to the summary
   *  @param size_unit: the unit of summary length
   *  @param summary_limit: maximum summary size
    */
	void create_ants( Text result_obj,  Hashtable<String,word_coverage_obj> word_score, Hashtable<Integer,sentence_coverage_obj> sentence_toBe_ranked, String size_unit, int summary_limit)
	{
		ant_list.clear();							
		active_ant.clear();
		
		ant ant_obj;
		int total_sent_num = result_obj.seg_text_sent_list.size(); //get the number of sentences
							
		//Create the ants
		for (int i = 0; i < m; i++)
		{		
			ant_obj = new ant (ant_positions.get(i), total_sent_num, sentence_toBe_ranked, summary_limit);
			best_city aCity = new best_city ();
			aCity.city_index = ant_positions.get(i);	//Set the position
			aCity.city_score = sentence_toBe_ranked.get(ant_positions.get(i)).score;	//coverage heuristic 
		
			ant_obj.add_city(aCity);  									//Add the city to ant's path
			ant_obj.local_update_content_score(word_score); 			//Remove the added sentence as well as its words's 
			ant_obj.local_update_capacity(size_unit, result_obj);		//Update the remaining summary size 
			ant_list.add(ant_obj);										//Add the ant
			active_ant.add(i);											//Activate the ant 
		}//for
		
		
	}
	

	/**
   *  A method to search for the solution using ACS algorithm
   *  @return list of integer: the summary; list of sentences
   *  @param Text:the input text details
   *  @param word_score: word scores details
   *  @param sentence_toBe_ranked: set of sentences that can be added to the summary
   *  @param size_unit: the unit of summary length
   *  @param summary_limit: maximum summary size
   */
	List<Integer> ASC_search(Text result_obj,  Hashtable<String,word_coverage_obj> word_score, Hashtable<Integer,sentence_coverage_obj> sentence_toBe_ranked, String size_unit, int summary_limit)
	{	
		
		int total_sent_num = result_obj.seg_text_sent_list.size();
		int best_ant = 0;
		int current_iteration = 0;  		//the current number of iterations	
		
		//Add the ants + add the first sentence to each ant as a starting vertex
		create_ants( result_obj,  word_score,  sentence_toBe_ranked,  size_unit,  summary_limit);
		
		//Build the graph  (from the actual one from the second objective)
		ACS_graph = new ant_graph(total_sent_num, pheromone_start_value); 	
	
		do {	
			current_iteration++;		//Increment the number of iterations
			
			if (current_iteration!=1)
				create_ants(  result_obj,  word_score,  sentence_toBe_ranked,  size_unit,  summary_limit);			
				
			int cycle =1;
	
			while ((!active_ant.isEmpty()) && (cycle <= total_sent_num)) //total_sent_num is equal to n (i.e. number of cities)
			{	
				//Begin a cycle 	
				cycle++;  //Increment cycle number for the next loop
					
				for (int j=0; j<m; j++)  // Search next city for each ant 	
				{
					if (active_ant.contains(j))  //Check if the ant has more space to include more sentence or it is inactive
					{		
						ant current_ant_obj = ant_list.get(j);
							
						if (current_ant_obj.to_be_vistited.size()!=0) //Check if it has more neighbors to visit
						{
							//Get the next city or -1 if no sentence found
							best_city best_city_obj = current_ant_obj.get_next_city( result_obj,  size_unit,  summary_limit, ACS_graph,  beta, q0);							
								
							if (best_city_obj.city_index != -1)
							{	
								current_ant_obj.add_city(best_city_obj);							//Add sentence number to the tour of the ant 
								current_ant_obj.local_update_content_score(word_score); 			//remove the selected sentence as well as its words' content scores
								current_ant_obj.local_update_capacity(size_unit, result_obj);		//Update the remaining summary length for this ant 
								ant_list.set(j,current_ant_obj);									//Update the ant
							}//if
							else
								active_ant.remove(j);	
						}//if
					}//if
				}//for
				
				
				//Apply the local updating rule
				for (int l=0; l<m; l++)
				{			
					if (active_ant.contains(l)) 
					{
						int current, previous;
						int tour_length = ant_list.get(l).tour.size();
						current = ant_list.get(l).tour.get(tour_length-1);
						previous = ant_list.get(l).tour.get(tour_length-2);
						double new_value =(1-rho)*( ACS_graph.A_graph.get_edge(previous,current));
						new_value+= (rho) * (pheromone_start_value);
						ACS_graph.A_graph.update_edge(previous,current, new_value);
					}//if
				}//for
			}//while				
				
			//Apply the global updating rule
			double best_tour_score = -1; 	//The score of the iteration best solution
			List<Integer> best_tour;		//The iteration best solution
					
			//Get the iteration best solution
			for (int x=0; x< m; x++)  //Each ant tour
			{	
				double current_tour_score = ant_list.get(x).tour_score;
		
				if (current_tour_score > best_tour_score)
				{
					best_tour_score = current_tour_score;  
					best_ant = x;			
				}//if
			}//for
			
			best_tour = ant_list.get(best_ant).tour;
			
			//Update the best tour so far by compare it to the best score in the current iteration
			if (best_tour_score > best_so_far_tour_score)
			{
				best_so_far_tour_score = best_tour_score;
				best_so_far_tour.clear();
				for (int y=0; y< best_tour.size(); y++)
				{
					Integer aCity = new Integer (best_tour.get(y));
					best_so_far_tour.add(aCity);
				}//for
			}//if
		}while (current_iteration < iteration_limit); //Check the stop condition
		
		return (best_so_far_tour);   //Return the summary; list of sentences
	}
}
