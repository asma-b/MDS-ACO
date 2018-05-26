/**
 * sentence_coverage_obj.java
 * Purpose: This class represents the sentence's current coverage score well as the covered words
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */
import java.io.Serializable;
import java.util.Hashtable;
public class sentence_coverage_obj implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	    Hashtable<String,Integer> tokens =  new Hashtable<String,Integer>();  // Words that are covered by this sentence and not yet covered by selected sentences 
	    double score;     //The current sentence coverage score
}
