/**
 * Read_Multiling2011.java
 * Purpose: This class reads the files of the corpus of MultiLing 2011 Pilot
 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class Read_Multiling2011 {	
    String path;   					//The file path including its name
    String temp_text=null;			//to store temporarily all the text sentences before segmentation
    
	/**
     *  Constructor to initialize path of the file
     *  @param file_path: file path
     */
    public Read_Multiling2011(String file_path)
	{
		path = file_path;
		temp_text=null;		
	}
    
	/**
     *  This method reads the input file and stores its content
     */
    public void ReadFile() throws ParserConfigurationException, SAXException, IOException
    {	
    	BufferedReader br = new BufferedReader(new FileReader(path));
    	try 
    	{   	   
    		StringBuilder sb = new StringBuilder();

    		String line = br.readLine();        			//Read the first line; the headline (The headline begins with a date)
    		
    		line = br.readLine();							//Read a blank line
    		line = br.readLine();							//Read the date
    		line = br.readLine();							//Read a blank line    
    		
    		while (line != null)							//Read the text; the paragraph are separated by lines. but I did not use that. I only take all the text sentences altogether.
    		{
	   	        sb.append(line);
	   	        if (!line.equals(""))
	   	        	sb.append(' ');
	   	        line = br.readLine();
    		}
    	   
    		temp_text = sb.toString();
    		
    	} finally {
    	    br.close();
    	}
	}
}
