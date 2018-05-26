
/**
 * ReadTextFile.java
 * Purpose: This class read the Arabic stop word list text file
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Hashtable;

public class ReadArabicTextFile {
	private String path;
	Integer dummayValue;	
	
	/**
   *  Constructor to initialize file path
   *  @param file_path: the path of stop words list file
   */
	public ReadArabicTextFile(String file_path)
	{
		path = file_path;
		
	}

	/**
	   *  This method reads the text file
	   *  @return list of the stop words
	 */
	public Hashtable<String,Integer> ReadFile() throws IOException 
	{
		String aLine;
		FileReader fr = new FileReader(path);
		BufferedReader textReader = new BufferedReader(fr);
		Hashtable<String,Integer> stopWords = new Hashtable<String,Integer>(); 
		
		while ( ( aLine = textReader.readLine( ) ) != null ) //rea
		{
			dummayValue = new Integer(0);
   	    	 stopWords.put(aLine, dummayValue);  //add the stemmed stop word to the hash table
		}		
		textReader.close( );
		return stopWords;
	}
}
