/**
 * document.java
 * Purpose: This class represents a document
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.io.Serializable;
import java.util.Hashtable;

public class document implements Serializable  {
	private static final long serialVersionUID = 1L;
	int first_sent_index;  														
	int last_sent_index;
	Hashtable<String,Integer> tf_text = new Hashtable<String,Integer>();
}
