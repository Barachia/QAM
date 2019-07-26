package hmi.qam.matcher;

import java.util.*;
import java.io.*;

import hmi.qam.util.NLP;
import info.debatty.java.stringsimilarity.Cosine;
import info.debatty.java.stringsimilarity.StringSimilarityInterface;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DialogStore is a store containing Dialogs elements
 * that contains matching questiona and answers pairs
 * The main public method is bestMatch.
 * The DialogStore is created and filled by reading an xml file with a DomDialogParser
 */
@XmlRootElement(name="dialogs")
public class DialogStore{

  private List<Dialogs> dialogs;
  private static final String DEFAULT_ANSWER = "I do not understand what you mean.";
  //private Encode e;
  ArrayList<String> defaultAnswers = new ArrayList<String>();

  public DialogStore(){
      this("defaultanswers.txt");
  }
  
  public DialogStore(InputStream dfstream) {
		dialogs = new ArrayList();
		//this.e = new Encode();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(dfstream));
			String line;
			while ((line = br.readLine()) != null)
			{
				defaultAnswers.add(line);
			}
		}
		catch (FileNotFoundException e)
		{
			System.out.println("default answers file not found");
		}
		catch (IOException e)
		{
			System.out.println("IOexception");
		}
	}
  
   public DialogStore(String dr_file){
    dialogs = new ArrayList();
    //this.e = new Encode();
    // Load dictionary of default answers
    //String dafilename = System.getProperty("user.dir") + "\\resources\\defaultanswers.txt";
    String dafilename = dr_file;
    System.out.println("Default replies: " + dr_file);
    try {
      BufferedReader br = new BufferedReader(new FileReader(dafilename));
      String line;
      while ((line = br.readLine()) != null)
      {
        defaultAnswers.add(line);
      }
    }
    catch (FileNotFoundException e)
    {
      System.err.println("Dialogue store: file not found: " + e);
      defaultAnswers.add(DEFAULT_ANSWER);
    }
    catch (IOException e)
    {
      System.err.println("IOexception: " + e);
      defaultAnswers.add(DEFAULT_ANSWER);
    }
  }

  //get a random default answer from the list of default answers
  public String RandomDefaultAnswer()
  {
    if (defaultAnswers.size() == 0)
    {
      return DEFAULT_ANSWER;
    }
    return defaultAnswers.get((int)(Math.random() * (defaultAnswers.size())));
  }

  public void add(Dialogs d){ dialogs.add(d);
  }

  public int size(){
    return dialogs.size();
  }


  public String xml(){
    String result="<dialoglist>\n";
    for (int i=0; i< dialogs.size();i++){
      result += (dialogs.get(i)).xml();
    }
    result += "</dialoglist>\n";
    return result;
  }

  /**
   * @return String that is answer of dialog with given id and attribute name and attribute value
   */
  public String answerString(Dialogs d, String attName, String attValue){
    if (d == null) {return RandomDefaultAnswer();};
    for(int j=0;j<d.answerSize();j++){
      AnswerType at = d.getAnswer(j);
      String value = at.valueOfAttribute(attName);
      if ((value!=null) && (value.equals(attValue)))
        return at.answer;
    }
    return RandomDefaultAnswer();
  }

  /**
   * @return String that is best answer to given question in a dialog that satisfies given name and attribute value
   */
  public String bestMatch(String question, String attName, String attValue){
    String answer = "";
    Dialogs d = getBestMatchingDialog(question);
    if (d!=null){
      answer = answerString(d, attName , attValue );
    }else{
      answer = RandomDefaultAnswer();
    }
    return answer;
  }

  /**
   * Computes for a query the best match with the comparable queries
   * @return the Dialogs in this DialogStore with a question that best matches the given query
   */
  public Dialogs getBestMatchingDialog(String query, int ngram){
    Dialogs bestDialogs = null;
    double bestMatch = -0.1;
    for(int i=0;i<dialogs.size();i++){
      Dialogs d = dialogs.get(i);
      for(int j=0;j<d.questionSize();j++){
        String q = d.getQuestion(j);
        double match = similarity(query, q,ngram);
        if (match>bestMatch){
          bestMatch = match;
          bestDialogs = d;
        }
      }
    }
    if (bestMatch == 0) {
      return null;
    }
    return bestDialogs;
  }

    /**
     * Computes for a query the best match with the comparable queries
     * @return the Dialogs in this DialogStore with a question that best matches the given query
     */
    public Dialogs getBestMatchingDialog(String query){
       return getBestMatchingDialog(query,0);
    }

  
  /**
     * @param query, the user query
   * @return the Dialogs in this DialogStore with a question that best matches the given query
   * By default the method uses no encoding, no stopword removal and Cosine similarity
   */
  public Pair<Dialogs,Double> getBestMatchingDialogAndScore(String query, int ngram){
      try {
          return getBestMatchingDialogAndScore(query,"grapheme",false, new Cosine(), ngram);
      } catch (IOException e) {
          e.printStackTrace();
      }
      return null;
  }


    public Pair<Dialogs,Double> getBestMatchingDialogAndScore(String query){
        return getBestMatchingDialogAndScore(query,0);
    }


  /**
   * @param query, the user query to find a match for
   * @param encode, could be 'grapheme', 'cmu', 'wcmu', 'dm' or 'm3'
   * @param stopwords, true if to remove stopwords from the query and queries matched against
   * @param s, the String similarity you would like to use, for example JaroWinkler or CosineSimilarity
   * @param ngram, the number of ngrams for ngram comparison. If 0, no ngrams are used
   * @return the Dialogs in this DialogStore with a question that best matches the given query.
   */
  public Pair<Dialogs,Double> getBestMatchingDialogAndScore(String query, String encode, boolean stopwords, StringSimilarityInterface s, int ngram) throws IOException {
      Dialogs bestDialogs = null;
      double bestMatch = -0.1;
      double match = 0.0;
      String a;
      String b;
      StringSimilarityInterface sim = s;
      if(stopwords){
          query = NLP.removeStopWords(query);
      }
      for(int i=0;i<dialogs.size();i++){
          Dialogs d = dialogs.get(i);
          for(int j=0;j<d.questionSize();j++){
              String q = d.getQuestion(j);
              if(stopwords){
                  q = NLP.removeStopWords(q);
              }
              a = query;
              b = q;
              match = DialogStore.similarity(a,b,ngram);
              if (match>bestMatch){
                  bestMatch = match;
                  bestDialogs = d;
              }
          }
      }
      if (bestMatch == 0) {
          return Pair.of(null, 0.0);
      }
      return Pair.of(bestDialogs,bestMatch);
  }
  
  /**
   * Method for retrieving all queries with their matching score
   * @param query, the user query
   * @return , the list of queries with similarity queries to the user query
   */
  public List<Pair<Dialogs, Double>> retrieveQueries(String query, StringSimilarityInterface similarityInterface, int ngram){
      
      List<Pair<Dialogs,Double>> queries = new ArrayList();
      for(int i=0;i<dialogs.size();i++){
          Dialogs d = dialogs.get(i);
          double max = 0.0;
          for(int j=0;j<d.questionSize();j++){
              String q = d.getQuestion(j);
              double match = similarity(query,q,ngram);
              if(match > max)
                  max = match;
          }
          queries.add(Pair.of(d,max));
      }
      Collections.sort(queries,new DialogComparator().reversed());
      return queries;
  }

    public List<Pair<Dialogs, Double>> retrieveQueries(String query){
      return this.retrieveQueries(query,new Cosine(),0);
    }


    /**
     * Computes similarity between two Strings
     * The current implementation computes the relative size of the intersection of the sets of n-grams
     * of words in the two given strings
     * @return a value in [0,1] the similarity between two given Strings str1 and str2
     */
  public static double similarity(String str1, String str2, int maxGramSize){
    List<String> ngrams1 = ToolSet.generateNgramsUpto(str1, maxGramSize);
    List<String> ngrams2 = ToolSet.generateNgramsUpto(str2, maxGramSize);
    Set<String> interset = intersection(ngrams1,ngrams2);
    //System.out.println("Intersection="+interset.toString());
    Set<String> union = union(ngrams1,ngrams2);
    //System.out.println("Union="+union.toString());
    double len1 = interset.size();
    double len2 = union.size();
    return len1/len2;
  }

    /**
     * Computes similarity between two Strings
     * The current implementation computes the relative size of the intersection of the sets of n-grams
     * of words in the two given strings
     * @return a value in [0,1] the similarity between two given Strings str1 and str2
     */
//    public double weightedSimilarity(String str1, String str2, int maxGramSize){
//        List<String> ngrams1 = ToolSet.generateNgramsUpto(str1, maxGramSize);
//        List<String> ngrams2 = ToolSet.generateNgramsUpto(str2, maxGramSize);
//        Set<String> intersect = intersection(ngrams1,ngrams2);
//        Set<String> union = union(ngrams1,ngrams2);
//        Iterator<String> it = intersect.iterator();
//        List<Double> weights = new ArrayList();
//        if(it.hasNext()){
//            String next = it.next();
//            weights.add(this.e.getWeight(next,true));
//        }
//        while(it.hasNext()){
//            String next = it.next();
//            weights.add(this.e.getWeight(next,false));
//        }
//        double weight = weights.stream().collect(Collectors.summingDouble(d -> d))/weights.size();
//        double len1 = intersect.size();
//        double len2 = union.size();
//        return weight*(len1/len2);
//    }

  private static Set<String> intersection(List<String> lst1, List<String> lst2){
    Set<String> s1 = new TreeSet<String>(lst1);
    Set<String> s2 = new TreeSet<String>(lst2);
    s1.retainAll(s2);
    return s1;
  }

  private static Set<String> union (List<String> lst1, List<String> lst2){
    Set<String> s1 = new TreeSet<String>(lst1);
    Set<String> s2 = new TreeSet<String>(lst2);
    s1.addAll(s2);
    return s1;
  }
  
  public class DialogComparator implements Comparator<Pair<Dialogs,Double>>{
      @Override
      public int compare(Pair<Dialogs,Double> p1, Pair<Dialogs,Double> p2){
          return p1.getValue().compareTo(p2.getValue());
      }
  }

  public List<Dialogs> getDialogs(){
        return this.dialogs;
  }

  @XmlElement(name="dialog")
  public void setListOfDialogs(ArrayList<Dialogs> listOfDialogs){
      this.dialogs = listOfDialogs;
  }

}