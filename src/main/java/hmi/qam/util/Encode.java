package hmi.qam.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import edu.stanford.nlp.util.ConfusionMatrix;
import hmi.qam.encode.Weight;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Encode {

    private Metaphone3 m3;
    private DoubleMetaphone m2;
    private Weight weight;

    private Analyzer analyzer;

    Map<String,String> dict;
    Map<String,String> phones;
    List<String> symbols;
    Map<String,String> vp;
    List<List<String>> initialConsonants;
    List<List<String>> finalConsonants;


    public Encode() {
        m3 = new Metaphone3();
        m2 = new DoubleMetaphone();
        this.dict = CMUDict.getDict();
        this.phones = CMUDict.getPhones();
        this.symbols = CMUDict.getSymbols();
        this.vp = CMUDict.getVp();
        analyzer = new StandardAnalyzer();
    }


    /**
     * Set M2 with variable length
     * @param text, the text to encode
     * @return, the M2 encoded string of the original text
     */
    public String getDoubleMetaphoneEncoding(String text){
        return getDoubleMetaphoneEncoding(text, text.length());
    }

    /**
     * Get the double metaphone for a fixed length
     * @param text, the text to encode
     * @param length, the length of the double metaphone
     * @return the double metaphone
     */
    public String getDoubleMetaphoneEncoding(String text, int length){
        m2.setMaxCodeLen(length);
        m2.encode(text);
        return m2.doubleMetaphone(text);
    }

    /**
     * Encoding a sentence as double metaphone
     * @param sentence
     * @return the double metaphone encoding
     */
    public String getDoubleMetaphoneSentence(String sentence){
        String m2Phonetic = "";
        StringTokenizer tokenizer = new StringTokenizer(sentence);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            if(m2Phonetic == ""){
                m2Phonetic = m2Phonetic + this.getDoubleMetaphoneEncoding(word);
            }
            else{
                m2Phonetic = m2Phonetic + " " + this.getDoubleMetaphoneEncoding(word);
            }

        }
        return m2Phonetic;
    }

    /**
     * Retrieve n-grams. Example: text = "a b c d e" n = 3
     * [a b c, b c d, c d e]
     * @param text, the text to turn into an n-gram
     * @param n, the n of n-gram
     * @return, a list of n-grams
     */
    public List<String> getNGrams(String text, int n){
        if(n == 1){
            return Arrays.asList(text.split(" "));
        }
        List l = new ArrayList();
        Analyzer analyzer = new WhitespaceAnalyzer();
        TokenStream ts = analyzer.tokenStream("sentence",text);
        ShingleFilter shingle = new ShingleFilter(ts,n,n);
        if(n > 1){
            shingle.setOutputUnigrams(false);
        }
        CharTermAttribute charTermAttribute = shingle.addAttribute(CharTermAttribute.class);
        try {
            shingle.reset();
            while(shingle.incrementToken()){
                String token = charTermAttribute.toString();
                l.add(token);
            }
            return l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }

    public List<String> getUnigrams(String text){
        return getNGrams(text, 1);
    }

    public List<String> getTwograms(String text){
        return getNGrams(text, 2);
    }

    public List<String> getTrigrams(String text){
        return getNGrams(text, 3);
    }

    /**
     * Get the Metaphone3 encoding for a singular word
     * @param word, the word
     * @return the Metaphone3 encoding
     */
    public String getMetaphone3(String word){
        return getMetaphone3(word,word.length());
    }

    /**
     * Get the Metaphone3 encoding for a single sentence
     * @param sentence, the sentence
     * @return the Metaphone3
     */
    public String getMetaphone3Sentence(String sentence){
        String m3Phonetic = "";
        StringTokenizer tokenizer = new StringTokenizer(sentence);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            m3Phonetic = m3Phonetic + " " + getMetaphone3(word);
        }
        return m3Phonetic;
    }

    public String getMetaphone3Sentence(String sentence, int length) {
        String m3Phonetic = "";
        StringTokenizer tokenizer = new StringTokenizer(sentence);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            m3Phonetic = m3Phonetic + " " + getMetaphone3(word,length);
        }
        return m3Phonetic;
    }

    private String getMetaphone3(String word, int length) {
        m3.SetWord(word);
        m3.SetKeyLength(length);
        m3.Encode();
        return m3.GetMetaph();
    }

    /**
     * Helper for converting a full sentence to a CMU phonetic representation (Oct, 2018)
     * @param sentence, the sentence to convert
     * @return the phonetic representation
     */
    public String getCMUDictSentence(String sentence){
        String cmuPhonetic = "";
        StringTokenizer tokenizer = new StringTokenizer(sentence.trim().toLowerCase());
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            cmuPhonetic = cmuPhonetic + " " + dict.get(word);
        }
        return cmuPhonetic.trim();
    }

    /**
     * Helper for converting a full sentence to a CMU phonetic representation
     * @param word, the sentence to convert
     * @return the phonetic representation
     */
    public List<String> getCMUDictSentenceList(String word){
        List<String> cmuPhonetic = new ArrayList();
        String pWord = this.getCMUDictSentence(word);
        StringTokenizer tokenizer = new StringTokenizer(pWord);
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            cmuPhonetic.add(token);
        }
        return cmuPhonetic;
    }

    public List<List<String>> getCMUDictSentenceWordList(String sentence){
        List<List<String>> cmuPhoneticSentence = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(sentence);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            cmuPhoneticSentence.add(getCMUDictSentenceList(word));
        }
        return cmuPhoneticSentence;
    }


    /**
     * Compute the weights for the phonetic characters
     * @param word, which should be in ARPA representation the word
     * @return the weights in order of the characters
     */
    private List<Double> getWeights(String word){
        List<Double> weights = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(word);
        if(tokenizer.hasMoreTokens()){
            String initial = tokenizer.nextToken();
            double weight = 1.0;
            if(this.getWeight(initial,true) != null){
                    weight = this.getWeight(initial,true);
            }
            weights.add(weight);
        }
        while(tokenizer.hasMoreTokens()){
            String mid = tokenizer.nextToken();
            double weight = 1.0;
            if(this.getWeight(mid,false)!=null){
                weight = this.getWeight(mid,false);
            }
            weights.add(weight);
        }
        return weights;
    }

    /**
     * Compute the weight for one phonetic character
     * @param character, the character to retrieve the weight for
     * @param initial, if it's an initial consonant
     * @return the weight for the specific character
     */
    public Double getWeight(String character, boolean initial){
        double sum = 0;
        double counter = 0;
        StringTokenizer tokenizer = new StringTokenizer(character);
        while(tokenizer.hasMoreTokens()){
            double weight = 1.0;
            counter++;
            String s = tokenizer.nextToken();
            if(initial){
                if(this.weight.getInitialWeights().get(s) != null){
                    weight = this.weight.getInitialWeights().get(s);
                }
                sum = sum + weight;
            }
            else{
                if(this.weight.getFinalWeights().get(s) != null){
                    weight = this.weight.getFinalWeights().get(s);
                }
                sum = sum + weight;
            }
        }
        return sum/counter;
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

    /**
     * Method that calculates from two lists of phonemes the similarity.
     * E.g. R AE 1 B and D AE M
     * @param predicted, the string list with predicted phonemes
     * @param actual, the actual phonemes
     * @return the weight, based on the work of Woods et al. (2009)
     */
    public double weightedSimilarity(List<String> predicted, List<String> actual){
        double weight = 1;
        int size = Math.min(predicted.size(),actual.size());
        for(int i=0;i<size;i++){
            String phoneme = predicted.get(i);
            if(this.weight.getInitialWeights().containsKey(phoneme)){
                weight = this.weight.getInitialWeights().get(phoneme);
            }
        }
        return weight;
    }

    public double weightedSentenceSimilarity(List<List<String>> predicted, List<List<String>> actual){
        double weight = 1;
        int size = Math.min(predicted.size(),actual.size());
        for(int i = 0; i <size;i++){
            List<String> predictedWord = predicted.get(i);
            List<String> actualWord = actual.get(i);
            //similarity = similarity + weightedSimilarity(predictedWord,actualWord);
            weight = weight + weightedSimilarity(predictedWord,actualWord);
        }
        String p =  predicted.stream().map(String::valueOf).collect(Collectors.joining());
        String a = actual.stream().map(String::valueOf).collect(Collectors.joining());
        double similarity = (weight/predicted.size()) * this.similarity(p, a);
        return similarity;
    }

    public double weightedSentenceSimilarity(String predicted, String actual){
        List<List<String>> cmuWeightedPredictedSentence = this.getCMUDictSentenceWordList(predicted);
        List<List<String>> cmuWeightedActualSentence = this.getCMUDictSentenceWordList(actual);
        String cmuPredicted = this.getCMUDictSentence(predicted);
        String cmuActual = this.getCMUDictSentence(actual);
        double weight = 1;
        int size = Math.min(cmuWeightedPredictedSentence.size(),cmuWeightedActualSentence.size());
        for(int i = 0; i <size;i++){
            List<String> predictedWord = cmuWeightedPredictedSentence.get(i);
            List<String> actualWord = cmuWeightedActualSentence.get(i);
            //similarity = similarity + weightedSimilarity(predictedWord,actualWord);
            weight = weight + weightedSimilarity(predictedWord,actualWord);
        }
        double similarity = (weight/cmuWeightedPredictedSentence.size()) * this.similarity(cmuPredicted, cmuActual);
        return similarity;
    }


    /**
     * Compute similarity between a list of predicted words and the actual words
     * @param predicted, could be ASR output or text with typos
     * @param actual, reference text in a database, such as question answering
     * @return, the similarity between the lists.
     */
    public double similarity(List<String> predicted, List<String> actual){
        double sum = 0;
        for(int j=0;j<predicted.size();j++){
            sum = sum + similarity(predicted.get(j),actual.get(j));
        }
        return sum/predicted.size();
    }

    public static void main(String[] args) {
        Encode encode = new Encode();
        String predicted = "what the two armed robot say";
        String actual = "what did the white rabbit say";
        String cmuPredicted = encode.getCMUDictSentence(predicted);
        String cmuActual = encode.getCMUDictSentence(actual);
        String dmPredicted = encode.getDoubleMetaphoneSentence(predicted);
        String dmActual = encode.getDoubleMetaphoneEncoding(actual);
        //List<String> cmuWeightedPredicted = encode.getCMUDictSentenceList(predicted);
        //List<String> cmuWeightedActual = encode.getCMUDictSentenceList(actual);
        //List<List<String>> cmuWeightedPredictedSentence = encode.getCMUDictSentenceWordList(predicted);
        //List<List<String>> cmuWeightedActualSentence = encode.getCMUDictSentenceWordList(actual);

        double similarity1 = encode.similarity(predicted,actual);
        double similarity2 = encode.similarity(cmuPredicted,cmuActual);
        double similarity3 = encode.weightedSentenceSimilarity(predicted,actual);
        //double similarity4 = encode.weightedSentenceSimilarity(cmuWeightedPredictedSentence,cmuWeightedActualSentence);

        System.out.println("Done!");




    }

}
