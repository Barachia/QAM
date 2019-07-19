package hmi.qam.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import edu.stanford.nlp.util.ConfusionMatrix;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.text.similarity.CosineDistance;
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

    private final Map<String, Double> initialWeights;
    private final Map<String, Double> finalWeights;
    private Metaphone3 m3;
    private DoubleMetaphone m2;

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
        this.initialConsonants = this.loadMatrix("consonant/initialConsonantARPA.csv");
        this.finalConsonants = this.loadMatrix("consonant/finalConsonantARPA.csv");
        this.initialWeights = this.weights(this.initialConsonants);
        this.finalWeights = this.weights(this.finalConsonants);
        analyzer = new StandardAnalyzer();
    }


    /**
     * Set DoubleMetaphone with variable length
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
     * TODO
     * @param text
     * @return
     */
    private String weighted(String text){
        for(List<String> predInitCon : this.initialConsonants){
            for(String actualInitCon : predInitCon){

            }
        }
        //Check for initial phoneme if there is a match.
        //If there is a match, assign a weight.
        //If not, then weight remains 1.
        //Check for every other phoneme if there is a match.
        String s = "";
        return s;
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
            cmuPhonetic = cmuPhonetic + " " + dict.get(word);
        }
        return cmuPhonetic;
    }

    /**
     * Helper for converting a full sentence to a CMU phonetic representation
     * @param sentence, the sentence to convert
     * @return the phonetic representation
     */
    public List<String> getCMUDictSentenceList(String sentence){
        List<String> cmuPhonetic = new ArrayList();
        String pSentence = this.getCMUDictSentence(sentence);
        StringTokenizer tokenizer = new StringTokenizer(pSentence);
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            cmuPhonetic.add(word);
        }
        return cmuPhonetic;
    }

    private List<List<String>> loadMatrix(String filename){
        List<List<String>> lines = new ArrayList();
        InputStreamReader reader = new InputStreamReader(Encode.class.getClassLoader().getResourceAsStream(filename), StandardCharsets.UTF_8);
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .build();
        try (CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(parser)
                .build();) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                lines.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  lines;
    }

    private Map<String, Double> weights(List<List<String>> matrix){
        Map<String,Double> weights = new HashMap();
        List<String> labels = matrix.get(0);
        for(int i=1;i<matrix.size();i++){
            List<String> row = matrix.get(i).subList(1,matrix.size());
            double sum = row.stream().collect(Collectors.summingInt(string -> Integer.parseInt(string)));
            double tp = Double.valueOf(row.get(i-1));
            weights.put(labels.get(i), tp / sum);
        }
        return weights;
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
     * @return the weighted similarity, based on the work of Woods et al. (2009)
     */
    public double weightedSimilarity(List<String> predicted, List<String> actual){
        CosineDistance distance = new CosineDistance();
        double similarity = 1;
        for(int i=0;i<predicted.size();i++){
            double weight = 1;
            String phoneme = predicted.get(i);
            String actualPhoneme = actual.get(i);
            if(this.initialWeights.containsKey(phoneme)){
                weight = this.initialWeights.get(phoneme);
            }
            similarity = similarity * weight * distance.apply(phoneme,actualPhoneme);
        }
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
        List<String> cmuWeightedPredicted = encode.getCMUDictSentenceList(predicted);
        List<String> cmuWeightedActual = encode.getCMUDictSentenceList(actual);

        double similarity1 = encode.similarity(predicted,actual);
        double similarity2 = encode.similarity(cmuPredicted,cmuActual);
        double similarity3 = encode.weightedSimilarity(cmuWeightedPredicted,cmuWeightedActual);






    }

}
