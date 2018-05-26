/**
 * directed_graph.java
 * Purpose: This class represents weighted and directed graph
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */
import java.io.Serializable;

public class directed_graph implements Serializable  {
	private static final long serialVersionUID = 1L;

	//Sentences X sentences matrix
	double[][] graph_matrix;
	
	//Weight threshold; only edges with weight more than threshold will be added to the graph
	double threshold = 0;   
	
	/**
	   *  Constructor to initialize the graph (with the threshold parameter)
	   *  @param size: number of nodes
	   *  @param thresholdValue: the threshold value of edge's weight
	   *  @param initial_value: initial value of edges
	*/
	
	directed_graph(int size, double thresholdValue, double initial_value)
	 {
		//Create the arrays (graph and degree)
		 graph_matrix = new double[size][size];
		
		 //Initialize each element in both arrays to zero	
		for (int i=0; i<size;i++) 
			for (int j=0; j<size;j++) 		
				graph_matrix[i][j]= initial_value;

		//Set the threshold
		threshold = thresholdValue;
	}
		 
	/**
	   *  Constructor to initialize the graph (without the threshold parameter (default value = zero))
	   *  @param size: number of nodes
	   *  @param initial_value: initial value of edges
	*/
	directed_graph(int size, double initial_value)
	 {
		graph_matrix = new double[size][size];
			
		for (int i=0; i<size;i++) 
			for (int j=0; j<size;j++)  
				graph_matrix[i][j]=  initial_value;
	}
	 
	/**
	   *  this method sets the threshold value
	   *  @param thresholdValue: the threshold value of edge's weight
	*/
	void set_threshold (double thresholdValue)
	{
		threshold = thresholdValue;
	}	
	
	/**
	   *  this method adds an edge from one vertex to another one 
	   *  @param vertex1: the first vertex
	   *  @param vertex2: the second vertex
	   *  @param weight: edge's weight
	*/
	void add_edge(int vertex1,int vertex2, double weight)  //the edge from vertex 1 (row) to vertex 2 (column)
	{		
		if (weight<=threshold)
			System.out.println("The edge between "+(vertex1)+" and "+(vertex2)+" has not been added; the weight is less than or equal the threshold.");
		else
		{		
			if (graph_matrix[vertex1][vertex2] != 0)  //An edge already exists
			{
				if(graph_matrix[vertex1][vertex2] < weight)	
					graph_matrix[vertex1][vertex2] = weight;				
			}
			else		
				graph_matrix[vertex1][vertex2] = weight;	
		}	
	}
	
	/**
	   *  this method removes an edge from one vertex to another one 
	   *  @param vertex1: the first vertex
	   *  @param vertex2: the second vertex
	*/
	void remove_edge(int vertex1,int vertex2)//the edge from vertex 1 (row) to vertex 2 (column)
	{
		graph_matrix[vertex1][vertex2] = 0;
	}
	
	/**
	   *  this method gets weight of an edge from one vertex to another one  
	   *  @param vertex1: the first vertex
	   *  @param vertex2: the second vertex
	   *  
	   *  @return double: edge's weight
	*/
	double get_edge(int vertex1,int vertex2)
	{
		return (graph_matrix[vertex1][vertex2] );
	}
	
	/**
	   *  this method updates the weight of an edge from one vertex to another one  
	   *  @param vertex1: the first vertex
	   *  @param vertex2: the second vertex
	*/
	void update_edge(int vertex1,int vertex2, double new_value)//the edge from vertex 1 (row) to vertex 2 (column)
	{
		graph_matrix[vertex1][vertex2] = new_value;	
	}
}
	


