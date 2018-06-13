package hmi.qam.util;

import org.apache.commons.codec.language.DoubleMetaphone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Encode {

    private Metaphone3 m3;
    private DoubleMetaphone m2;

    public Encode() {
        m3 = new Metaphone3();
        m2 = new DoubleMetaphone();
    }

    /**
     * Set encoding with dynamic length
     * @param text
     * @return
     */
    public String getEncoding(String text){
        return getEncoding(text, text.trim().length());
    }

    public String getWordEncoding(String text){
        return getWordEncoding(text,text.trim().length());
    }

    public String getWordEncoding(String text, int length){
        String words = "";
        Pattern p = Pattern.compile("\\b\\w*\\b");
        Matcher matcher = p.matcher(text);
        while(matcher.find()){
            words = words + getEncoding(matcher.group(),length);
        }
        return words;
    }

    /**
     * Set Metaphone 3 encoding with fixed length
     * @param text, the text to encode
     * @param length, the length of the original text
     * @return the M3 encoded string of the original text with given length
     */
    public String getEncoding(String text, int length){
        m3.SetWord(text);
        m3.m_metaphLength = length;
        m3.Encode();
        return m3.GetMetaph();
    }

    /**
     * Set DoubleMetaphone with variable length
     * @param text, the text to encode
     * @return, the M2 encoded string of the original text
     */
    public String getDoubleMetaphoneEncoding(String text){
        return getDoubleMetaphoneEncoding(text, text.trim().length());
    }

    public String getDoubleMetaphoneEncoding(String text, int length){
        m2.setMaxCodeLen(length);
        m2.encode(text);
        return m2.doubleMetaphone(text);
    }

}
