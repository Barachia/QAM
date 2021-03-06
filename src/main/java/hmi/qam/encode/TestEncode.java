package hmi.qam.encode;


import hmi.qam.matcher.ToolSet;
import hmi.qam.util.Dialog;
import hmi.qam.util.dialogRoot;
import info.debatty.java.stringsimilarity.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class TestEncode {

    public static void main(String[] args){
        //testEncodings();
        testQuestions();

    }

    private static void testQuestions(){
        //String questions = "qa/alice_questions.xml";
        String questions ="qa/hai_alice_questions.xml";
        TestEncode test = new TestEncode();
        M2 code = new M2();
        SetBasedStringSimilarity cosine = new Cosine();
        dialogRoot root = test.loadDialogRoot(questions);
        String a = "what happened after alice fell";
        Map<String,String> testQuestions = new HashMap();
        //Map<String,String> testQuestions = test.questions();
        testQuestions.put(a,"0043");
        //Map<String,Double> result = test.testQuestions(a, root, cosine, code);
        //int rank = test.retrieveRank(result,"0043");
        Map<String, Map<String,Double>> results = test.testAllSimilarities(a,root);
        Map<String,Integer> method = test.bestRank(results,"0043");
        Map<String, Map<String,Integer>> bestMethods = test.testQuestionRankings(root);
        System.out.println("Method: " + method);

    }

    private Map<String,Map<String,Integer>> testQuestionRankings(dialogRoot root){
        Map<String,Map<String,Integer>> bestResults = new HashMap();
        Map<String,String> questions = this.questions();
        for(String question : questions.keySet()){
            Map<String, Map<String,Double>> results = this.testAllSimilarities(question,root);
            Map<String,Integer> method = this.bestRank(results,questions.get(question));
            //System.out.println("Method: " + method);
            bestResults.put(question,method);
        }
        return bestResults;
    }

    private Map<String,String> questions(){
        Map<String,String> questions = new HashMap();
        //String q1 = "How long was Alice falling?";
        String q1 = "yes hello how long was alice folly";
        String q2 = "Who is the author of the book?";
        String q3 = "Was Alice based on the author’s life?";
        String q4 = "Why was the Queen angry with Alice?";
        String q5 = "What did the White Rabbit say";
        String q6 = "Tell me about the orange marmalade";
        String q7 = "What other books did the author write";
        String q8 = "How old is Alice?";
        String q9 = "Is the Cheshire Cat the cat with a grin?";
        String q10 = "Can you tell me about movies of Alice in Wonderland?";
        questions.put(q1,"0011");
        questions.put(q2,"0017");
        questions.put(q3,"0024");
        questions.put(q4,"0061");
        questions.put(q5,"0037");
        questions.put(q6,"0026");
        questions.put(q7,"0014");
        questions.put(q8,"0044");
        questions.put(q9,"0083");
        questions.put(q10,"0040");
        return questions;
    }

    private Map<String,Integer> bestRank(Map<String,Map<String,Double>> list, String index){
        String bestMethod = "grapheme";
        Integer bestRank = -1;
        Map<String,Integer> map = new HashMap();
        for(String method : list.keySet()){
            int rank = retrieveRank(list.get(method),index);
            if(rank >bestRank){
                bestRank = rank;
                bestMethod = method;
            }
        }
        map.put(bestMethod,bestRank);
        return map;
    }

    private int retrieveRank(Map<String, Double> list, String index){
        int rank = -1;
        int count = list.size();
        Iterator<String> it = list.keySet().iterator();
        while(it.hasNext()){
            String temp = it.next();
            if(temp.equals(index)){
                rank = count;
                break;
            }
            count--;
        }
        return rank;
    }


    public Map<String,Map<String, Double>> testAllSimilarities(String question, dialogRoot questions){
        Map<String,Map<String,Double>> results = new HashMap();
        for(SetBasedStringSimilarity s : this.getSims()){
            for(PhonemeEncoderInterface pei : this.getPEIS()){
                results.put(s.getClass().getSimpleName()+pei.getClass().getSimpleName(),this.testQuestions(question,questions,s,pei));
            }
            results.put(s.getClass().getSimpleName(),this.testBaselineQuestions(question,questions));
        }
        return results;
    }

    public List<SetBasedStringSimilarity> getSims(){
        ArrayList<SetBasedStringSimilarity> sims = new ArrayList();
        SetBasedStringSimilarity cosine = new Cosine();
        SetBasedStringSimilarity jaccard = new Jaccard();
        SetBasedStringSimilarity qgram = new QGram();
        SetBasedStringSimilarity sorensen = new SorensenDice();
        sims.add(cosine);
        sims.add(jaccard);
        sims.add(qgram);
        sims.add(sorensen);
        return sims;
    }

    /**
     * Retrieve all Phonetic encoders
     * @return the encoders;
     */
    public List<PhonemeEncoderInterface> getPEIS(){
        M2 code1 = new M2();
        M3 code2 = new M3();
        CMU code3 = new CMU();
        Weight weight = new Weight();
        WeightedCMU code4 = new WeightedCMU(weight);
        WeightedM2 code5 = new WeightedM2(weight);
        WeightedM3 code6 = new WeightedM3(weight);
        ArrayList<PhonemeEncoderInterface> codes = new ArrayList();
        codes.add(code1);
        codes.add(code2);
        codes.add(code3);
        codes.add(code4);
        codes.add(code5);
        codes.add(code6);
        return codes;
    }

    /**
     * Test questions with a specific question.
     * @param question: the question to compare with
     * @param store, the dialog store to look up the question
     * @param s, the similarity measure
     * @param code, the phonetic encoding to be used
     * @return a hashmap containing the questions and their score
     */
    public Map<String,Double> testQuestions(String question, dialogRoot store, SetBasedStringSimilarity s, PhonemeEncoderInterface code){
        Map<String,Double> similarities = new TreeMap();
        for(Dialog other : store.getDialogs()){
            String a = question;
            double max = 0;
            String b = "";
            for(int j=0; j<other.getQuestions().size();j++){
                b = other.getQuestions().get(j);
                if(code.getSimilarity(s,a,b) > max){
                    max = code.getSimilarity(s,a,b);
                }
            }
            similarities.put(other.getId(),max);
        }
        return similarities.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue,newValue) -> oldValue, LinkedHashMap::new));

    }

    /**
     * Method for retrieving scores without the encoding
     * @param question, the question to encode
     * @param store, the questions to compare with
     * @return, the list of probabilities
     */
    public Map<String,Double> testBaselineQuestions(String question, dialogRoot store){
        Map<String,Double> similarities = new HashMap();
        for(Dialog other : store.getDialogs()){
            String a = question;
            double max = 0;
            String b = "";
            for(int j=0; j<other.getQuestions().size();j++){
                b = other.getQuestions().get(j);
                if(ToolSet.similarity(a,b)> max){
                    max = ToolSet.similarity(a,b);
                }
            }
            similarities.put(other.getId(),max);
        }
        return similarities.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue,newValue) -> oldValue, LinkedHashMap::new));


    }

    /**
     * Load the QA file
     * @param filename, the name of the QA file
     * @return the DialogStore
     */
    public dialogRoot loadDialogRoot(String filename){
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(dialogRoot.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            InputStream file = getClass().getClassLoader().getResourceAsStream(filename);
            dialogRoot store = (dialogRoot) jaxbUnmarshaller.unmarshal(file);
            return store;

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void testEncodings(){
        TestEncode testEncode = new TestEncode();
        List<PhonemeEncoderInterface> codes = testEncode.getPEIS();
        List<SetBasedStringSimilarity> sims = testEncode.getSims();


        String a = "what did the white rabbit say";
        String b = "what the two armed robot say";

        SetBasedStringSimilarity cosine = new Cosine();
        System.out.println("No coding: a - " + a + " b - " + b);
        System.out.println("Grapheme: " + cosine.similarity(a,b));

        System.out.println("No coding: a - " + a + " b - " + b);
        System.out.println("Ngram: " + ToolSet.similarity(a,b));

        for(PhonemeEncoderInterface code : codes){
            System.out.println("Code a: " + code.getEncoded(a) + "Code b: " + code.getEncoded(b));
            System.out.println("Code: " + code.getClass().getCanonicalName() + " - " + code.getSimilarity(cosine,a,b));
        }
    }
}
