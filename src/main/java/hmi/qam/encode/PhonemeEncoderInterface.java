package hmi.qam.encode;

import info.debatty.java.stringsimilarity.SetBasedStringSimilarity;

public interface PhonemeEncoderInterface {

    /**
     * All encoded strings should have phonetic representation for a full sentence with spaces between sounds
     * @param sentence, the sentence to retrieve a phonetic representation for
     * @return a String with the encoded sentence
     */
    String getEncoded(String sentence);

    /**
     * All strings should be NOT encoded in this method
     * @param s, the similarity measure to use
     * @param target, the word to compare to
     * @param real, the predicted word for comparison
     * @return the similarity score with 0 being totally dissimilar and 1 being exactly the same
     */
    double getSimilarity(SetBasedStringSimilarity s, String target, String real);
}
