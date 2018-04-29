package hmi.qam.util;

import org.apache.commons.codec.language.DoubleMetaphone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Encode {

    private Metaphone3 m3;
    private DoubleMetaphone m2;

    public Encode() {
        m3 = new Metaphone3();

    }

    /**
     * Set encoding with dynamic length
     * @param text
     * @return
     */
    public String getEncoding(String text){
        m3.SetWord(text);
        m3.m_metaphLength = text.trim().length();
        m3.Encode();
        return m3.GetMetaph();
    }

    public String getWordEncoding(String text){
        String words = "";
        Pattern p = Pattern.compile("\\b\\w*\\b");
        Matcher matcher = p.matcher(text);
        while(matcher.find()){
            words = words + getEncoding(matcher.group());
        }
        return words;
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
     * Set encoding with fixed length
     * @param text
     * @param length
     * @return
     */
    public String getEncoding(String text, int length){
        m3.SetWord(text);
        m3.m_metaphLength = length;
        m3.Encode();
        return m3.GetMetaph();
    }
    
    public String getDoubleMetaphoneEncoding(String text){
        m2.setMaxCodeLen(text.trim().length());
        m2.encode(text);
        return m2.toString();
    }
}
