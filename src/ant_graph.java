/**
 * ant_graph.java
 * Purpose: This class represents the graph representation of the input text for the ACS algorithm
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

public class ant_graph {
	directed_graph A_graph; //The directed graph
	
	/**
	 *  Constructor to create and initialize the graph 
	 *  @param total_sentence_num: number of sentences
	 *  @param pheromone_start_value: the initial value of the pheromone trails
	 */
	ant_graph (int total_sentence_num, double pheromone_start_value)
	{		
		A_graph = new directed_graph(total_sentence_num, pheromone_start_value);
	}
}
