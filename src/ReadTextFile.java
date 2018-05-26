
/**
 * ReadTextFile.java
 * Purpose: This class read the English stop word list text file and applies stemming process (PorterStemmer) on the stop words.
 *
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Hashtable;

public class ReadTextFile {
	private String path;
	Integer dummayValue;
	
	/**
	   *  Constructor to initialize file path
	   *  @param file_path: the path of stop words list file
	 */
	public ReadTextFile(String file_path)
	{
		path = file_path;
	}

	/**
	   *  This method reads the text file
	   *  @return list of the stop words
	 */
	public Hashtable<String,Integer> ReadFile() throws IOException 
	{
		String aLine, stemmed;
		FileReader fr = new FileReader(path);
		BufferedReader textReader = new BufferedReader(fr);
		Hashtable<String,Integer> stopWords = new Hashtable<String,Integer>(); 
		Stemmer s;
		
		while ( ( aLine = textReader.readLine( ) ) != null )   //read line
		{
			dummayValue = new Integer(0);
			
			 s = new Stemmer();  //stemmer object  
   	    	 for (int k = 0; k <aLine.length() ; k++) 
	    	      s.add(aLine.charAt(k));
   	    	 s.stem();
   	    	 stemmed = s.toString();
   	    	 stopWords.put(stemmed, dummayValue);  //add the stemmed stop word to list
		}//while		
		
		textReader.close( );
		return stopWords;
	}
}
