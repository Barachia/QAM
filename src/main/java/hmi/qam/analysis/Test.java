package hmi.qam.analysis;

import hmi.qam.util.CMUDict;
import hmi.qam.util.Encode;
import org.apache.commons.text.similarity.CosineDistance;

import java.util.*;

public class Test {

    private static String question = "what did the white rabbit say";

    //CMU parameters
    Map<String,String> dict = null;
    Map<String,String> phones = null;
    List<String> symbols = null;
    Map<String,String> vp = null;
    //Metaphone parameters
    Encode e = null;



    public static void main(String[] args){
        Test t = new Test();
        //t.testNgrams();
        //t.test(true,true, false);
        t.testTwo();


    }

    public void testTwo(){
        String a = "her oldest edison";
        String b = "how old is alice";
        String aEn = e.getDoubleMetaphoneSentence(a);
        String bEn = e.getDoubleMetaphoneSentence(b);
        System.out.println(similarity(aEn,bEn));
    }

    public void testNgrams(){
        Encode e = new Encode();
        List<List<String>> xList = new ArrayList();
        List<List<String>> yList = new ArrayList();
        String x = "what did the white rabbit say";
        String y = "what the two armed robot say";
        String xDM = e.getDoubleMetaphoneSentence(x);
        String yDM = e.getDoubleMetaphoneSentence(y);
        String xCMU = this.getCMUDictSentence(x);
        String yCMU = this.getCMUDictSentence(y);

        List<String> names = new ArrayList();
        names.add("unigram");
        names.add("bigram");
        names.add("biphoneDM");
        names.add("biphoneCMU");
        names.add("triphoneDM");
        names.add("triphoneCMU");


        List<String> unigramX = e.getUnigrams(x);
        List<String> unigramY = e.getUnigrams(y);

        List<String> bigramX = e.getTwograms(x);
        List<String> bigramY = e.getTwograms(y);
        List<String> biphoneXDM = e.getTwograms(xDM);
        List<String> biphoneYDM = e.getTwograms(yDM);
        List<String> biphoneXCMU = e.getTwograms(xCMU);
        List<String> biphoneYCMU = e.getTwograms(yCMU);

        List<String> triphoneXDM = e.getTrigrams(xDM);
        List<String> triphoneYDM = e.getTrigrams(yDM);
        List<String> triphoneXCMU = e.getTrigrams(xCMU);
        List<String> triphoneYCMU = e.getTrigrams(yCMU);

        xList.add(unigramX);
        yList.add(unigramY);
        xList.add(bigramX);
        yList.add(bigramY);
        xList.add(biphoneXDM);
        yList.add(biphoneYDM);
        xList.add(biphoneXCMU);
        yList.add(biphoneYCMU);
        xList.add(triphoneXDM);
        yList.add(triphoneYDM);
        xList.add(triphoneXCMU);
        yList.add(triphoneYCMU);


        System.out.println("Similarities");
        for(int i=0;i<xList.size();i++){
            List<String> list1 = xList.get(i);
            List<String> list2 = yList.get(i);
            System.out.println("Similarity (" + names.get(i) + "): " + similarity(list1,list2));
        }



    }

    public Test(){
        dict = CMUDict.getDict();
        phones = CMUDict.getPhones();
        symbols = CMUDict.getSymbols();
        vp = CMUDict.getVp();
        System.out.println("Success in loading!");
        e = new Encode();

    }

    /**
     * Compute the cosine similarity between two strings
     * @param a, the first sentence to compare
     * @param b, the second sentence to compare
     * @return, the similarity between the two strings
     */
    public double similarity(String a, String b){
        CosineDistance distance = new CosineDistance();
        return (1-distance.apply(a,b));
    }

    public double similarity(List<String> a, List<String> b){
        double sum = 0;
        for(int j=0;j<a.size();j++){
            sum = sum + similarity(a.get(j),b.get(j));
        }
        return sum/a.size();
    }

    public void test(boolean cmu, boolean dm, boolean m3){
        String cmuPhonetic = "";
        String dmPhonetic = "";
        String m3Phonetic = "";
        Scanner in = new Scanner(System.in);
        String text = "";
        System.out.println("Please provide input");
        while(in.hasNextLine() && text != "exit"){
            text = in.nextLine();
            System.out.println("String: " + similarity(text,question) + "\n"
                    + question + "\n"
                    + text + "\n");
            if(cmu) {
                cmuPhonetic = getCMUDictSentence(text);
                System.out.println("CMU: " + similarity(cmuPhonetic,getCMUDictSentence(question)) + "\n"
                        + getCMUDictSentence(question) + "\n"
                        + cmuPhonetic + "\n");
            }
            if(dm) {
                dmPhonetic = e.getDoubleMetaphoneSentence(text);
                System.out.println("DMP: " + similarity(dmPhonetic, e.getDoubleMetaphoneSentence(question)) + "\n"
                        + e.getDoubleMetaphoneSentence(question) + "\n"
                        + dmPhonetic + "\n");
            }
            if(m3) {
                m3Phonetic = e.getMetaphone3Sentence(text);
                System.out.println("MP3: " + similarity(m3Phonetic, e.getMetaphone3Sentence(question)) + "\n"
                        + e.getMetaphone3Sentence(question) + "\n"
                        + m3Phonetic + "\n");
            }
        }
    }

    /**
     * Helper for converting a full sentence to a CMU phonetic representation (Oct, 2018)
     * @param sentence, the sentence to convert
     * @return the phonetic representation
     */
    public String getCMUDictSentence(String sentence){
        String cmuPhonetic = "";
        StringTokenizer tokenizer = new StringTokenizer(sentence);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            cmuPhonetic = cmuPhonetic + dict.get(word);
        }
        return cmuPhonetic;
    }

}
