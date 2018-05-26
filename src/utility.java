/**
 * utility.java
 * Purpose: This class contains the implementation of several common methods used in the program.
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */


public class utility {
/**
   * This method calculates the LCS between two strings (the implementation of this method is from: Implementation from: http://rosettacode.org/wiki/Longest_common_subsequence#Dynamic_Programming_2)
   * @param a the first string
  *  @param b: the second string 
  *  
  *  @return integer: the LCS value
   */
	public int calculate_LCS(String a, String b)
	{
		 int[][] lengths = new int[a.length()+1][b.length()+1];
		 
		 for (int i = 0; i < a.length(); i++)
		     for (int j = 0; j < b.length(); j++)
		         if (a.charAt(i) == b.charAt(j))
		             lengths[i+1][j+1] = lengths[i][j] + 1;
		         else
		             lengths[i+1][j+1] = Math.max(lengths[i+1][j], lengths[i][j+1]);
		 
		  // read the substring out from the matrix
		 StringBuffer sb = new StringBuffer();
		 for (int x = a.length(), y = b.length();
		     x != 0 && y != 0; ) {
		     if (lengths[x][y] == lengths[x-1][y])
	            x--;
	        else if (lengths[x][y] == lengths[x][y-1])
	            y--;
	        else {
		        assert a.charAt(x-1) == b.charAt(y-1);
		        sb.append(a.charAt(x-1));
		        x--;
	            y--;
		      }//else
	   }	 
	 return sb.reverse().toString().length();
	}
	
	 /**
  * This method multiplies two matrices
  * @param first: the first matrix
 *  @param second: the second matrix 
 *  
 *  @return double: the multiplication LCS value or null in case of empty matrix or unmatchable sizes.
  */
	public static double[][] multMatrix(double[][] first, double[][] second) 
    {
    	//Check for any problem
    	if (first.length == 0 || second.length == 0)
    		return null;
        if (first[0].length != second.length)
                    return null;
          
        double[][] result = new double[first.length][second[0].length]; // multiplication result
                   
        for (int i = 0; i < first.length; ++i)
        	for (int j = 0; j < second[0].length; ++j) 
        	{
        		double sum = 0;
                for (int k = 0; k < second.length; ++k)
                	sum += first[i][k] * second[k][j];
                
                result[i][j] = sum;
             }//for
        return result;
    }
    
/**
 * This method transposes a matrix (write the rows of A as the columns of A; written as a T on the top of matrix name)
 * @param matrix: the  matrix
*  
*  @return  the resulting matrix or null in case of empty matrix. 
*/   
    public static double[][] transposeMatrix(double[][] matrix) 
    {
    	//Check for if it is empty 
    	if (matrix.length == 0) 
    		return null;
           
        double[][] result = new double[matrix[0].length][matrix.length];
              
        for (int i = 0; i < result.length; ++i) 
                for (int j = 0; j < result[i].length; ++j) 
                      result[i][j] = matrix[j][i];                    
         
        return result;
    }

    /**
     * This method normalize the matrix:
     * 1) Divide each cell by the sum of item in its row; sum of each row is 1.
     * 2) Must check if the sum is equal to zero; hem the item must be zero (i.e. no 0/0).
     * @param matrix: the  matrix
    *  
    *  @return  Normalized matrix  
    */  

	public static double[][] normalize_matrix (double[][] matrix) {
		
		int rows, cols;
		double[][] normalized = null;
		
		rows = matrix.length;
		if (rows > 0)
			cols = matrix[0].length;
		else
		{
			cols = 0;
		}
		
		normalized = new double[rows][cols];
		
		for (int i=0; i<rows; i++)
		{
			double sum = 0;
			for (int j = 0; j< cols; j++)
				sum += matrix[i][j];
			if (sum!=0)
				for (int j =0; j< cols; j++)
					normalized [i][j] = (double) matrix [i][j] / (double) sum;		
		}
		return normalized;
	}
	
	 /**
     * This method normalize the array :
    * 1- Divide each cell by the sum of item in its row; sum of each row is 1.
    * 2- Must check if the sum is equal to zero; hem the item must be zero (i.e. no 0/0)
    * 
    *  @param Array: the  array
    *  
    *  @return  normalized array  
    */
	public static double[][]  normalize_array(double[][] Array) {
		
		int index = Array.length;
		double[][]normalized = new double[index][1];
		double sum = 0;
		
		for (int i=0; i<index; i++)
			sum += Array[i][0];
	
		if (sum!=0)
			for (int i=0; i<index; i++)
				normalized [i][0] = (double) Array[i][0] / (double) sum;		
		else
			normalized=Array;  
			
		return normalized;		
	}
	
	 /**
 * This method adds two matrices
 * @param first: the first matrix
*  @param second: the second matrix 
*  
*  @return double: the addition result if every thing is correct or null otherwise 
*/
	static double [][] addMatrix (double [][] first, double [][] second)
	{
		double [][] sum = null;
		if (first.length == second.length)
		{
			if ((first.length > 0)  && (second.length > 0))
			{
				if (first[0].length == second[0].length)
				{
					sum = new double [first.length][first[0].length]; 
					for (int i = 0; i< first.length; i++)
						for (int j = 0; j< first[0].length; j++)
							sum[i][j] = first [i][j] + second [i][j]; 
				}//if
				else 
					System.out.println("The dimensions are not compatible");
			}
			else
				System.out.println("One or both matrices are empty");
		}
		else 
			System.out.println("The dimensions are not compatible");
									
		return sum;
	}
	
	 /**
	 * This method returns the scalar multiplication 
	 * @param matrix: the matrix
	*  @param c: the constant
	*  
	*  @return double: the multiplication results matrix  
	*/
	public static double[][] multScalar(double[][] matrix, double c) 
    {
    	//Check for any problem
    	if (matrix.length == 0)
    		return matrix;
        for (int i=0; i<matrix.length; i++)
        	for (int j = 0; j<matrix[0].length; j++)
        		matrix [i][j] = c * matrix [i][j];
        
        return matrix;
    }
}
