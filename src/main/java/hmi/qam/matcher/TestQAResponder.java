package hmi.qam.matcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.tuple.Pair;

public class TestQAResponder{

  //de qa parser etc
  private final DomDialogsParser ddp;
  private final DialogStore store;

  public TestQAResponder(String filename) {
    this.ddp = new DomDialogsParser(filename);
    this.store = this.ddp.getDialogStore();
  }
  
   public TestQAResponder(String filename, String dr) {
    this.ddp = new DomDialogsParser(filename, dr);
    this.store = this.ddp.getDialogStore();
  }
   
    public TestQAResponder(InputStream stream) {
        this.ddp = new DomDialogsParser(stream);
        this.store = this.ddp.getDialogStore();
    }
    
    public TestQAResponder(InputStream stream, InputStream dfstream) {
        this.ddp = new DomDialogsParser(stream, dfstream);
        this.store = this.ddp.getDialogStore();
    }

  public TestQAResponder() {
    this.ddp = new DomDialogsParser("E:\\GitHub\\ARIA-DialogueManagement\\dist\\data");
    this.store = this.ddp.getDialogStore();
  }
  /**
   * Don't use this method please
   * @param args 
   */
  public static void main(String[] args) throws IOException{
      String filename = System.getProperty("user.dir") + "\\QA.xml";
      String dr_file = "defaultanswers.txt";
      TestQAResponder tr = new TestQAResponder(filename, dr_file);
      String query = "Hallo";
      System.out.print("Question:");
      System.out.flush();
      String attName = "type";
      String attValue = "certain";

    while ((query = Console.readString().toLowerCase()) !="exit") {
        Pair<Dialog, Double> match = tr.store.getBestMatchingDialogAndScore(query);
        String bestQuery;
        String answer;
        if (match.getKey() == null) {
            bestQuery = "?";
            answer = tr.store.RandomDefaultAnswer();
        } else {
            if (match.getValue() >= 0.05) {
                bestQuery = match.getKey().getQuestion(0);
                //answer = tr.store.answerString(match.getKey(), attName, attValue);
                List<AnswerType> answers = match.getKey().answers;
                int randomNum = ThreadLocalRandom.current().nextInt(0, answers.size());
                answer = answers.get(randomNum).answer;
            } else {
                bestQuery = "?";
                answer = tr.store.RandomDefaultAnswer();
            }
        }
        //writer.write(answer + "\n");
        //List<Pair<Dialog, Double>> queries = tr.store.retrieveQueries(query);
        //System.out.println("Queries and scores: " + queries.get(0).getLeft() + queries.get(0).getRight().toString());
        System.out.println("Best match query: " + bestQuery);
        System.out.println("Score: " + match.getValue());
        System.out.println("Best answer :" + answer);
        System.out.print("Question:");
    }
  }



  public String findMatchingQuery(String query, String type, String value) {
    return this.store.bestMatch(query, type, value);
  }

  private Dialog findMatchingAnswer(String query) {
    return this.store.getBestMatchingDialog(query);
  }

  private String returnAnswer(Dialog sentence, String type, String value) {
    return this.store.answerString(sentence, type, value);
  }

  public String findAndReturn(String query, String type, String value) {
    Dialog d = this.findMatchingAnswer(query);
    if(type == null) {
      type = "type";
    }

    if(value == null) {
      value = "certain";
    }

    return this.returnAnswer(d, type, value);
  }

    public DialogStore getStore() {
        return store;
    }
  
  

}