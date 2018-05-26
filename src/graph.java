/**
 * directed_graph.java
 * Purpose: This class represents weighted and undirected graph
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.io.Serializable;

public class graph implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	//sentences X sentences
	double[][] graph_matrix;
	
	//only edges with weight more than threshold will be added to the graph
	double threshold = 0;   
	
	//The degree of each vertex
	int[] vertex_degree;

	/**
	   *  Constructor to initialize the graph (with the threshold parameter)
	   *  @param size: number of nodes
	   *  @param thresholdValue: the threshold value of edge's weight
	*/ 
	 graph(int size, double thresholdValue)
	 {
		// Create the arrays (graph and degree)
		 graph_matrix = new double[size][size];
		
		 //Initialize each element in both arrays to zero	
		for (int i=0; i<size;i++) 
			for (int j=0; j<size;j++) 
				graph_matrix[i][j]=0;

		vertex_degree = new int[size];
		for (int i=0; i<size;i++)
			vertex_degree[i] = 0;
		
		//Set the threshold value
		threshold = thresholdValue;
	}
		 
		/**
	   *  Constructor to initialize the graph (without the threshold parameter (default value = zero))
	   *  @param size: number of nodes
	*/
	 graph(int size)
	 {
		// Create the arrays (graph and degree)
		graph_matrix = new double[size][size];
			
		 //Initialize each element in both arrays to zero	
		for (int i=0; i<size;i++)  
			for (int j=0; j<size;j++)  
				graph_matrix[i][j]=0;
		
		vertex_degree = new int[size];
		for (int i=0; i<size;i++)
			vertex_degree[i] = 0;
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
	void add_edge(int vertex1,int vertex2, double weight)
	{
		
		if ((weight>threshold) && (graph_matrix[vertex1][vertex2] ==0))
		{
			//Add an edge	
			graph_matrix[vertex1][vertex2] = graph_matrix[vertex2][vertex1] = weight;	
			vertex_degree[vertex1]++;
			vertex_degree[vertex2]++;
		}
	}
	
	/**
	   *  this method removes an edge from one vertex to another one 
	   *  @param vertex1: the first vertex
	   *  @param vertex2: the second vertex
	*/
	void remove_edge(int vertex1,int vertex2)
	{
		graph_matrix[vertex1][vertex2] = graph_matrix[vertex2][vertex1] = 0;
		vertex_degree[vertex1]--;
		vertex_degree[vertex2]--;
	}
	
	/**
	   *  this method returns the degree of a vertex   
	   *  @param vertex: the vertex
	   *  
	   *  @return int: vertex's degree
	*/
	int degree_of(int vertex)
	{
		return vertex_degree[vertex];
	}

	/**
	   *  this method updates the weight of an edge from one vertex to another one  
	   *  @param vertex1: the first vertex
	   *  @param vertex2: the second vertex
	*/
	void update_edge(int vertex1,int vertex2, double weight)
	{
			
		if (weight<=threshold)
			System.out.println("The edge between "+(vertex1+1)+" and "+(vertex2+1)+" has not been updated; the weight is less than or equal the threshold.");
		else
		{		
			if (graph_matrix[vertex1][vertex2] != 0)  //An edge already exists
			{
				//update the weight
				graph_matrix[vertex1][vertex2] = graph_matrix[vertex2][vertex1] = weight;	
			}//if
			else
			{									
				//Add an edge				
				graph_matrix[vertex1][vertex2] = graph_matrix[vertex2][vertex1] = weight;	
				vertex_degree[vertex1]++;
				vertex_degree[vertex2]++;
			}//else
		}//else			
	}
}
