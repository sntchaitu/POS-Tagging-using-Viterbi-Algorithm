import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.sound.sampled.ReverbType;

import org.xml.sax.ext.LexicalHandler;

//program documentation

//POS tagging is done as below steps

//entire trainindata is loaded and each word is converted to lower case and morphroot is applied
//and bigram probabilies and lexcian prob are stored in hashmap
//along with them the count of each lexican and a unique no for each lexican is also stored for computation purpose
//Now when the test string is load with new data each lexicann is sored into an string array
//viterbi algorithm is applied for each states based on with what prob it will come to that state from previous state
//Intermediate results are displayed and final tag for word lexian is displayed.



public class Taging {

	public LinkedHashMap<String,Integer> posCode  = new LinkedHashMap<String,Integer>();
	public LinkedHashMap<String,Integer> LexicanCode = new LinkedHashMap<String,Integer>();
	
	public LinkedHashMap<Integer,String> revposCode  = new LinkedHashMap<Integer,String>();
	
	public LinkedHashMap<String,Integer> posCount  = new LinkedHashMap<String,Integer>();
	public LinkedHashMap<String,Integer> LexicanCount = new LinkedHashMap<String,Integer>();
	
	
	//array list to store input data
	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	
	
	public String corpusCleaning(String s)
	{
		String s1 = new String();
		for (int i=0;i<1;i++)
		{
			if(s.endsWith("ses")||s.endsWith("xes"))
			{
				s1 =s.substring(0,s.length()-2);
			}
			else if(s.endsWith("zes"))
			{
				s1 =s.substring(0,s.length()-1);
			}
			else if(s.endsWith("ches")||s.endsWith("shes"))
			{
				s1 =s.substring(0,s.length()-2);
			}
			else if(s.endsWith("men"))
			{
				s1 =s.substring(0,s.length()-2)+"an";
			}
			else if(s.endsWith("ies"))
			{
				s1 =s.substring(0,s.length()-3)+"y";
			}
			else
			{
				s1 = s;
			}
						
		}
		
		return s1;
		
	}
	
	public ArrayList<String> debuggingData() throws IOException
	{
		// Open the file
		FileInputStream fstream = new FileInputStream("e.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		//Read File Line By Li
		ArrayList<String> data1 = new ArrayList<String>();
		while ((strLine = br.readLine()) != null)  
		{
			String[] lines0 = strLine.split(" ");
			
			for (int i =0;i<lines0.length;i++)
			{
				String x = lines0[i];
				if(lines0[i].contains("[") && lines0[i].contains("]") )
				{
//					str.substring(str.indexOf("[") + 1, str.indexOf("]"));
					String result = lines0[i].substring(lines0[i].indexOf("[")+1, lines0[i].indexOf("]"));
					if(!isNumeric(result.trim()))
					{
						data1.add(result.trim());
					
					}
				}
			}
			
		}
		//System.out.println(data1.size());
		return data1;
	}
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public ArrayList<ArrayList<String>> loadTrainingData() throws IOException
	{
		
		
		
		
		// Open the file
		FileInputStream fstream = new FileInputStream("train.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		
		
		//counter to have an enum for each unique Parts of speech variable
		Integer posUniqueCounter = 0;
		
		//counter to have an enum for each unique lexican 
		Integer LexicanUniqueCounter = 0;
		
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)  
		{
		  // Print the content on the console
		
			if(strLine.trim().isEmpty())
			{
				ArrayList<String> lines = new ArrayList<String>();
				data.add(lines);
				
			}
			else if (!strLine.trim().isEmpty())
			{
			 String[] lines0 = strLine.split(" ");
			 
			 lines0[0] = lines0[0].toLowerCase();
			 lines0[1] = lines0[1].toLowerCase();
			 //perform required corpus cleansing
			 String []lines = new String[2];
			 
			  lines[0] = corpusCleaning(lines0[0]);
			 lines[1] = lines0[1];
			 ArrayList<String> temp= new ArrayList<String>();
			 if(!LexicanCode.containsKey(lines[0].toLowerCase()))
			 {
				 //example 
				 //if word confidence is not present and that word is first word then that word is added with LexicanUniqueCounter  = 0 and LexicanUniqueCounter is incremented by 1
				 //Now if the word report is not present then that word is second word then that word is added with LexicanUniqueCounter  = 1 and LexicanUniqueCounter is incremented by 1
				 //.................
				 LexicanCode.put(lines[0].toLowerCase(), LexicanUniqueCounter);
				 LexicanUniqueCounter++;
		 
			 }
			 
			 
			 if (!LexicanCount.containsKey(lines[0].toLowerCase()))
			 {
				//set  the count that lexican  word to zero
				 LexicanCount.put(lines[0].toLowerCase(), 1);
			 }
			 else if (LexicanCount.containsKey(lines[0].toLowerCase()))
			 {
				 //increment that lexican count by 1
				 Integer value  = LexicanCount.get(lines[0].toLowerCase());
				 LexicanCount.put(lines[0].toLowerCase(),++value);
			 }
			 
			 if(!posCode.containsKey(lines[1]))
			 {
				 posCode.put(lines[1], posUniqueCounter);
				 posUniqueCounter++;
			 }

			 if(!posCount.containsKey(lines[1]))
			 {
				//set the count of that pos word to 0
				 posCount.put(lines[1], 1);
			 }

			 else if(posCount.containsKey(lines[1]))
			 {
				//increment value by 1
				 Integer value  = posCount.get(lines[1]);
				 posCount.put(lines[1],++value);
			 }
			 
			 temp.add(lines[0].toLowerCase());temp.add(lines[1]);
			 data.add(temp);
		  }
		}

		//Close the input stream
		br.close();
		//System.out.println(data.size());
			
		//fill revPosCode LinkedHashMap i.e., given a number it will give its corresponding POS word
		String[] revposcode = new String[posCode.size()];
		for (Entry<String, Integer> entry : posCode.entrySet()) {
		    String key1 = entry.getKey();
		    Integer value1 = entry.getValue();
		    revposCode.put(value1, key1);
		    
		}
		//System.out.println("on count is"+LexicanCount.get("on"));
		return data;
	
	}
	
	public static void main(String[] args) throws IOException 
	{
		// TODO Auto-generated method stub
		
		Taging t1 = new Taging();
		t1.loadTrainingData();
		//create an array to store the counts of POS vs Lexcian
		//example how many lexican"escort " are with NN
		//how many lexcian 'The' with Det
		
		System.out.println("total lexican"+t1.LexicanCount.size());
		
		int totalPosCount = t1.posCode.size();
		int totalLexicanCount = t1.LexicanCode.size();
		int maxSentenceLength = 100;
		
		int[][] array1 = new int[totalPosCount][totalLexicanCount];
		double[][] LexicanProbability = new double[totalPosCount][totalLexicanCount];
		double[][] BigramProbability  = new double[totalPosCount][maxSentenceLength];
		//create and array to store transition count of POS at each position
		//i.e.,
		int[][] array2 = new int[totalPosCount][maxSentenceLength];
		
		
		//go through the entire data and compute the array1(lexcian count for each POS for eg., how many times report is Noun,how many times report is verb) 
		//and array2(POS count for each position i.e., how many times does that POS came at position 1 .......)
		int posIndex = 0;
	    int sentenceCount = 1;	
		for (int i = 0;i<t1.data.size();i++)
		 {
			if(t1.data.get(i).isEmpty())
			{
				posIndex = 0;
				sentenceCount++;
			}
			else
			{
				//System.out.println("posindex"+posIndex);
				
				int pos = t1.posCode.get(t1.data.get(i).get(1));
				//System.out.println("pos"+pos);
				array2[pos][posIndex]++;
				posIndex++;
			
				int lexpos = t1.LexicanCode.get(t1.data.get(i).get(0));
				array1[pos][lexpos]++;
			}
		 }
		
		//define a HashMap to store bi-gram probabilities
		
		 HashMap<String, Double>  bigramProb=  new  HashMap<String, Double>();
		 HashMap<String, Double>  transCount=  new  HashMap<String, Double>();
		 //compute the transition counts
		 for (int i = 0;i<t1.data.size();i++)
		 {
			if(t1.data.get(i).isEmpty())
			{
				String lastPos = t1.data.get(i-1).get(1);
				String curPos  = "qe";
				int pos = t1.posCode.get(lastPos);
				String key1 = curPos+"|"+lastPos;
				//Double value1 = (double)(array2[pos][posIndex-1]/(double)(t1.posCount.get(lastPos)));
				if(!transCount.containsKey(key1))
				{
					transCount.put(key1, 1.0);
				}
				else if(transCount.containsKey(key1))
				{
					double val = transCount.get(key1)+1;
					transCount.put(key1, val);
				}
				posIndex = 0;
			}
			else
			{
				if(i==0||posIndex==0)
				{
					//here qo means empty string
					String lastPos = "q0";
					String currentPos = t1.data.get(i).get(1);
					int pos = t1.posCode.get(currentPos);
					String key1 = currentPos+"|"+lastPos;
//					if (currentPos.equalsIgnoreCase("nn") && lastPos.equalsIgnoreCase("q0"))
//					{
//						System.out.println("here");
//					}
					//Double value1 = (double) (array2[pos][posIndex]/(double)(sentenceCount));
					if(!transCount.containsKey(key1))
					{
						transCount.put(key1, 1.0);
					}
					else if(transCount.containsKey(key1))
					{
						double val = transCount.get(key1)+1;
						transCount.put(key1, val);
					}
				}
				else
				{
					String lastPos = t1.data.get(i-1).get(1).toLowerCase();
					String currentPos = t1.data.get(i).get(1).toLowerCase();
					String key1 = currentPos+"|"+lastPos;
					int pos1 = t1.posCode.get(currentPos);
					int pos2 = t1.posCode.get(lastPos);
					//Double value1 =   (double) (Math.min(array2[pos1][posIndex],array2[pos2][posIndex-1])/(double)t1.posCount.get(lastPos));
					if(!transCount.containsKey(key1))
					{
						transCount.put(key1, 1.0);
					}
					else if(transCount.containsKey(key1))
					{
						double val = transCount.get(key1)+1;
						transCount.put(key1, val);
					}
				}
				
				posIndex++;
				
			}
		 }
		
		
		//compute the Bigram Probabilities
		for (int i = 0;i<t1.data.size();i++)
		 {
			if(t1.data.get(i).isEmpty())
			{
				String lastPos = t1.data.get(i-1).get(1);
				String curPos  = "qe";
				int pos = t1.posCode.get(lastPos);
				String key1 = curPos+"|"+lastPos;
				//Double value1 = (double)(array2[pos][posIndex-1]/(double)(t1.posCount.get(lastPos)));
				Double value1 = (double)(transCount.get(key1)/(double)(t1.posCount.get(lastPos)));
				if(!bigramProb.containsKey(key1))
				{
					bigramProb.put(key1, value1);
				}
				posIndex = 0;
			}
			else
			{
				if(i==0||posIndex==0)
				{
					//here qo means empty string
					String lastPos = "q0";
					String currentPos = t1.data.get(i).get(1);
					int pos = t1.posCode.get(currentPos);
					String key1 = currentPos+"|"+lastPos;
//					Double value1 = (double) (array2[pos][posIndex]/(double)(sentenceCount));
					//System.out.println(key1);
					if(key1.equalsIgnoreCase("nn|q0"))
					{
//						System.out.println("here");
//						System.out.println(transCount.get(key1));
//						System.out.println(t1.posCount.get(lastPos));
					}
					Double value1 = (double)(transCount.get(key1)/(double)(sentenceCount));
					if(!bigramProb.containsKey(key1))
					{
						bigramProb.put(key1, value1);
					}
				}
				else
				{
					String lastPos = t1.data.get(i-1).get(1).toLowerCase();
					String currentPos = t1.data.get(i).get(1).toLowerCase();
					String key1 = currentPos+"|"+lastPos;
					int pos1 = t1.posCode.get(currentPos);
					int pos2 = t1.posCode.get(lastPos);
					//Double value1 =   (double) (Math.min(array2[pos1][posIndex],array2[pos2][posIndex-1])/(double)t1.posCount.get(lastPos));
					Double value1 = (double)(transCount.get(key1)/(double)(t1.posCount.get(lastPos)));
					if(!bigramProb.containsKey(key1))
					{
						bigramProb.put(key1, value1);
					}
				}
				
				posIndex++;
				
			}
		 }
		System.out.println("NN|DT is"+bigramProb.get("nn|dt"));
		System.out.println("VBD|NNP is"+bigramProb.get("vbd|nnp"));
		System.out.println("NNP|DT is"+bigramProb.get("nnp|dt"));
//print the bigrams
//		for (Entry<String, Double> entry : bigramProb.entrySet()) {
//	    String key1 = entry.getKey();
//	    Double value1 = entry.getValue();
//	   System.out.println(key1);
//	    
//	}
		
		
//		//store the prob of those bigram which are not present in array2
//		
//				
//				for (Entry<String, Integer> entry : t1.posCode.entrySet()) {
//				    String key1 = entry.getKey();
//				    Integer value1 = entry.getValue();
//				    key1 = key1+"-"+"q0";
//				    if (!bigramProb.containsKey(key1))
//				    {
//				    	bigramProb.put(key1,0.0001);
//				    }
//				    
//				}
//		
		//compute the Lexican probability array
		for (int i =0;i<array1.length;i++)
		{
			String posName = t1.revposCode.get(i);
			int pos = t1.posCode.get(posName);
			int poscount = t1.posCount.get(posName);
			
			for(int j = 0;j<array1[i].length;j++)
			{
				LexicanProbability[i][j] = (double) (array1[i][j]/(double)poscount);
				//LexicanProbability[i][j] = Math.round(LexicanProbability[i][j]*10000.0)/10000.0;
			}
		}
		
		//System.out.println("hi");
		double  default_value = 0.0001;
		
		//String test_string = "John saw the man on the hill with a telescope .";
		String test_string = "";
		//Load the input file
		
		// read first arguement as inputfile name
		  String fname ="";
	      if( args.length > 0 ) {
	         fname = args[ 0 ];
	      }
	      else
	      {
	    	  System.out.println("please enter file name as command line arguement");
	      }
				
				FileInputStream fstream = new FileInputStream(fname);
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String strLine;
				//Read File Line By Li
				ArrayList<String> data1 = new ArrayList<String>();
				//String testString = "";
				
				while ((strLine = br.readLine()) != null)  
				{
					test_string +=strLine;
					test_string += " ";
//					String[] lines0 = strLine.split(" ");
//					for (int i =0;i<lines0.length;i++)
//					{
//						data1.add(lines0[i]);
//					}
						
//					for (int i =0;i<lines0.length;i++)
//					{
//						String x = lines0[i];
//						if(lines0[i].contains("[") && lines0[i].contains("]") )
//						{
////							str.substring(str.indexOf("[") + 1, str.indexOf("]"));
//							String result = lines0[i].substring(lines0[i].indexOf("[")+1, lines0[i].indexOf("]"));
//							if(!isNumeric(result.trim()))
//							{
//								data1.add(result.trim());
//							
//							}
//						}
//					}
					
				}
		
		
		
		//String test_string = "flying plane can be dangerous .";
		//viterbi Algorithm
	    System.out.println("corpus analyzer and viterbi algorithm tagger");
	    System.out.println("Uniersity of central Florida");
	    System.out.println("CAP 5636-Advanced AI(Fall 2015) second semester");
	    System.out.println();
	    System.out.println();
		System.out.println("Corpus Features");
	    System.out.println("Total # tags :"+t1.posCount.size());
	    System.out.println("Total # bigrams :"+bigramProb.size());
	    System.out.println("Total # lexicals :"+LexicanProbability[0].length);
		//System.out.println(t1.data);
	    //t1.debuggingData();
		
		System.out.println("\n\nTest Set tokens found in corpus\n\n");
		
		System.out.println(test_string);
		String[] data3 = test_string.split(" ");
		for(int i =0;i<data3.length;i++)
		{
			if (t1.LexicanCount.containsKey(t1.corpusCleaning(data3[i].toLowerCase())))
			{
				    String output = data3[i]+" :"; 
					int lexicancode = t1.LexicanCode.get(t1.corpusCleaning(data3[i].toLowerCase()));
					for(int m = 0;m<LexicanProbability.length;m++)
					{
						for(int n = 0;n<LexicanProbability[m].length;n++)
						{
							if(n==lexicancode)
							{
								if (LexicanProbability[m][n] >0.0)
								{
									output+= t1.revposCode.get(m);
									output += "("+String.valueOf(Math.round(LexicanProbability[m][n]*10000.0)/10000.0)+")  ";
								}
							}
						}
					}
					System.out.println(output);
					System.out.println();
			}
			
		}
					
					
					//viterbi algorithm
					
					//frame an array where each row corresponds to a single POS and each column corresponds to a lexcian of the test string
					int testLexicanCount = data3.length; 
					double[][] v = new double[totalPosCount][testLexicanCount];
					int[][] parent = new int[totalPosCount][testLexicanCount];
					
					for(int k =0;k<data3.length;k++)
					{
						int flag = 0;
						double prob_lexican = 0.0;
						String lex = t1.corpusCleaning(data3[k].toLowerCase());
						if (k==0)
						{
							
														
							for (int a = 0; a<t1.posCount.size();a++)
							{
								double prob_bigram  = 0.0001;
								if(t1.LexicanCode.containsKey(lex))
								{
									int lexcode = t1.LexicanCode.get(lex);
									prob_lexican = LexicanProbability[a][lexcode];
								}
								else
								{
									prob_lexican = 1.0;
								}
								//if()
								String key1 = t1.revposCode.get(a);
								key1 = key1+"|"+"q0";
								if(bigramProb.containsKey(key1))
								{
									 prob_bigram = bigramProb.get(key1);
								}
								v[a][k] = prob_lexican*prob_bigram;
								
							}
						}
						else
						{
							
							//String lex = data3[k].toLowerCase();
							//System.out.println("lex is"+lex);
							int lexcode = 0;
							if (t1.LexicanCode.containsKey(lex))
							{	
								 lexcode = t1.LexicanCode.get(lex);
							}
							else
							{
//								int last =t1.LexicanCode.size();
//								
//								t1.LexicanCode.put(lex, last);
//								lexcode = last;
								prob_lexican = 1.0;
								
								
							}
							for(int a = 0;a<t1.posCount.size();a++)
							{
								//for each previous state 
								double v1 = v[a][k-1];
								if(v1>0)
								{
									for(int b = 0;b<t1.posCount.size();b++)
									{
										//compute the prob of coming to this state
										double prob_bigram = 0.0001;
										String key1 = t1.revposCode.get(a);
										String key2 = t1.revposCode.get(b);
										String key3 = key2+"|"+key1;
										if(bigramProb.containsKey(key3))
										{
											prob_bigram = bigramProb.get(key3);
										}
										else
										{
											prob_bigram = 0.0001;
										}
										if(t1.LexicanCode.containsKey(lex))
										{
											prob_lexican = LexicanProbability[b][lexcode];
										}
										else
										{
											prob_lexican = 1.0;
											String key4 = "NN".toLowerCase()+"|"+key1;
											prob_bigram = bigramProb.get(key4);
										}
										double total_value = v1*prob_bigram*prob_lexican;
										if(total_value>=v[b][k])
										{
											v[b][k] = total_value;
											//store the parent in seperate array
											parent[b][k] = a;
										}
									}
								}
							}
							
						}
					}
					
							
			
		
		
		//display the tags along with lexican labels
					
		for(int k  = 0;k<data3.length;k++)
		{
			//System.out.print(data3[k]+":  ");
			String op = new String();
			for (int b = 0;b<t1.posCount.size();b++)
			{
				if(v[b][k]>0)
				{
					op+= " "+t1.revposCode.get(b)+"("+v[b][k] +")  ";
				}
			}
			System.out.print(op);
			System.out.println();
		}
		
		
		//display the tags along with lexican labels
		System.out.println();
		System.out.println();
		System.out.println();
			for(int k  = 0;k<data3.length;k++)
			{
				System.out.print(data3[k]+":  ");
				String op = new String();
				double max = 0.0;
				for (int b = 0;b<t1.posCount.size();b++)
				{
					
					if(v[b][k]>max)
					{
						max= v[b][k];
						op= t1.revposCode.get(b);
					}
				}
				System.out.print(op);
				System.out.println();
			}
		
	}

}
