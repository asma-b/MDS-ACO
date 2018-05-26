/**
 * sorted_list.java
 * Purpose: This class is implemented to hold items that need to be sorted
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

public class sorted_list implements Comparable{	 	
	    public int senNum;   //sentence number within the text (starts from 1 for the first sentence).
	    Double score;     	//sentence ranking score
	    int length;			//sentence length
	    
		/**
	     *  Constructor to initialize the ACS algorithm
	     *  @param no: sentence number
	     *  @param score: sentence score
	     *  @param length: sentence length
	     */
	    public sorted_list(int no, Double score, int length){
	        this.senNum = no;
	        this.score = score;
	        this.length = length;
	    }

		/**
	     *  Compare two sentences based on their scores
	     *  @param o: the other sentence
	     *
	     *  @return 0, 1, or -1 based on the comparison result
	     */
	    public int compareTo(Object o) {

	    	sorted_list f = (sorted_list)o;

	    	if (score.doubleValue() > f.score.doubleValue()) {
	    		return 1;
	    	}
	    	else if (score.doubleValue() <  f.score.doubleValue()) {
	    		return -1;
	    	}
	    	else {
	    		return 0;
    }

}


}