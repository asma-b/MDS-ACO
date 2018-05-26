/**
 * Sentence.java
 * Purpose: This class represents a sentence
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */


import java.io.Serializable;
import java.util.Hashtable;

public class Sentence implements Serializable  {
	private static final long serialVersionUID = 1L;
	public boolean first=false;   				//1 if it is the first sentence in the document and 0 otherwise
    public String docNum;  				 //document number
    public int senNum;   				//sentence number within the text (start from 1 for the first sentence).
    public String sent;					//the sentence content
    Hashtable<String,Integer> tokens =  new Hashtable<String,Integer>(); //its words
    double score=0;    					 //to store the sentence ranking score
    int word_number = 0;			//number of words in the sentences including the stop words and numbers 
}
