/**
 * CoreNLP_Obj.java
 * Purpose: This class applies the CoreNLP 

 * @version 1 
 * @author  Asma Al-Saleh
 * @since  1/1/18
 */

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Properties;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;


public class CoreNLP_Obj implements Serializable  {
	private static final long serialVersionUID = 1L;
	String NLPtasks;
	Annotation annotation;
	
	/**
	 * Constructor
	 *  @param Selected_tasks: preprocessing tasks
	 */
	public  CoreNLP_Obj(String Selected_tasks)
	{
		NLPtasks = Selected_tasks;
	}

	/**
	 * This method applies the CoreNLP
	 *  @param headline: input text 
	 */
	public  void apply(String headline)
	{
		System.out.println("Applying the COREnlp....\n");
		
		PrintStream err = System.err;
		System.setErr(new PrintStream(new OutputStream(){
			public void write(int b){
			}
			}));
		

		Properties props = new Properties();
		if ((Public.corpora.equals("Multiling_2011_Arabic") ))  //handle Arabic version
			props = StringUtils.argsToProperties(new String[]{"-props", "StanfordCoreNLP-arabic.properties"});    	
	
		props.put("annotators", NLPtasks);   //Choosing the required NLP tasks 
		
		StanfordCoreNLP  pipeline = new StanfordCoreNLP(props);	
		
		annotation = new Annotation(headline);				    
		pipeline.annotate(annotation);
		
		System.setErr(err);
		
	}
}
