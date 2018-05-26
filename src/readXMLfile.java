import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class readXMLfile {

	File fXmlFile;			
    String path;   		//DUC XML file
    String temp_text=null;			//to store temporarily all the text sentences before segmentation
	String temp_headline=null;		//to store temporarily all the headline sentences before segmentation
	String docNum=null;				//to store temporarily the number of the document(?? make sure)
	
    public readXMLfile(String file_path)
	{
		path = file_path;
		temp_text=null;			
		temp_headline=null;		
		docNum=null;	
	}
    
    public void ReadFile() throws ParserConfigurationException, SAXException, IOException
    {
    	DocumentBuilderFactory dbFactory;
    	DocumentBuilder	 dBuilder;
    	Document	 doc;
    	NodeList	 nList;
        Node nNode;
        
	  fXmlFile = new File(path);	
	  dbFactory = DocumentBuilderFactory.newInstance();
 	  //System.out.println(fXmlFile.getName());
	  dBuilder = dbFactory.newDocumentBuilder();
	  doc = dBuilder.parse(fXmlFile);
 	  doc.getDocumentElement().normalize();
 	 System.out.println("the path="+path);
 	 if ((path.charAt(6)=='F') ||(path.charAt(14)=='X'))
	    {

		 nList = doc.getElementsByTagName(doc.getDocumentElement().getNodeName());
		 
		 //why I need list, search for another way???!!!! check
		  nNode = nList.item(0);
		 
		if (nNode.getNodeType() == Node.ELEMENT_NODE) 
		{
			Element eElement = (Element) nNode;
				
			docNum = eElement.getElementsByTagName("DOCNO").item(0).getTextContent();
				
			temp_text=eElement.getElementsByTagName("TEXT").item(0).getTextContent().trim();
			temp_text = temp_text.replaceAll("(\\r|\\n)", " ");
				
			temp_headline=eElement.getElementsByTagName("HEADLINE").item(0).getTextContent().trim();
			temp_headline = temp_headline.replaceAll("(\\r|\\n)", " ");
			//System.out.println("The text = "+temp_text);
			}
	    }
 	 else 
 		 if (path.charAt(6)=='L')    //Must be only else if we need to read only one file; not all the files in a folder.
    {		          	 			
	  	 
	  	 docNum = doc.getElementsByTagName("DOCNO").item(0).getTextContent().trim();  //Extract file name
	  	 
	  	 //Extract and segment the headline sentences
		 nList = doc.getElementsByTagName("HEADLINE");
		 nNode = nList.item(0);
		 nList=nNode.getChildNodes();
		 Element eElement = null;
		 temp_headline="";
			for (int temp = 0; temp < nList.getLength(); temp++) {
			    nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					 eElement = (Element) nNode;
					
					//store the statements detail as well as remove the blanks before and after the string using trim
					
					temp_headline=temp_headline+eElement.getTextContent().trim();            //get statements inside P (headline or text tags)							
				}
				temp_headline= temp_headline+" ";
			}
			
			temp_headline = temp_headline.replaceAll("(\\r|\\n)", " ");
		
			 //Extract and segment the headline sentences
			nList = doc.getElementsByTagName("TEXT");
			 nNode = nList.item(0);
			 nList=nNode.getChildNodes();
			 eElement = null;
			 temp_text="";
				for (int temp = 0; temp < nList.getLength(); temp++) {
					 
				    nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {	 
						 eElement = (Element) nNode;
						 temp_text=temp_text+eElement.getTextContent().trim();    //get statements inside P (headline or text tags)
					}
					 temp_text= temp_text+" ";
				}								
				
				temp_text = temp_text.replaceAll("(\\r|\\n)", " ");
				
	}
 		 else
 		 { System.out.println(path);
 			 if ( (path.charAt(23)=='N') ||(path.charAt(23)=='A'))
 		    {

 			 nList = doc.getElementsByTagName(doc.getDocumentElement().getNodeName());
 			 
 			  nNode = nList.item(0);
 			 
 			if (nNode.getNodeType() == Node.ELEMENT_NODE) 
 			{
 				Element eElement = (Element) nNode;
 					
 				docNum = eElement.getElementsByTagName("DOCNO").item(0).getTextContent();
 					
 				temp_text=eElement.getElementsByTagName("TEXT").item(0).getTextContent().trim();
 				temp_text = temp_text.replaceAll("(\\r|\\n)", " ");
 					
 				//temp_headline=eElement.getElementsByTagName("HEADLINE").item(0).getTextContent().trim();
 				//temp_headline = temp_headline.replaceAll("(\\r|\\n)", " ");
 				//System.out.println("The text = "+temp_text);
 				}
 		    }
 		 }
	}
}
