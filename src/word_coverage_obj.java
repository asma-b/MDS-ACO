/**
 * word_coverage_obj.java
 * Purpose: This class represents content score of a word as well as the sentences that include this word
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.util.ArrayList;
import java.util.List;

public class word_coverage_obj {
	 //Store list of sentences index that is include this words 
	 List<Integer> sentences_list = new ArrayList<Integer>();    
	 
	//to store the word score from the enforcement algorithm
	 double score;     
}