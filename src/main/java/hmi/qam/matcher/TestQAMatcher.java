package hmi.qam.matcher;

import java.util.Scanner;

/**
 * This class can be used for testing a matching method
 * It requires a simple text file with sentences
 */
public class TestQAMatcher{

  public static void main(String[] args){
    //String filename = "questions.txt";  // should be stored in the resource/qamatcher directory
    //String filename = System.getProperty("user.dir") + "\\QA.xml";
    String filename = "D:\\Data\\QA Alice\\alice_questions.xml";
    QuestionMatcher qam = new QuestionMatcher(filename);
    //new QuestionGUI(qam);
    System.out.println("You can start the session:");
    Scanner scan = new Scanner(System.in);
    String input = "";
    while (scan.hasNextLine()){
        input = scan.nextLine();
      System.out.println("Best match :"+ qam.bestMatch(input));
      //System.out.print("Question:");
    }

  }


}