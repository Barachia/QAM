package hmi.qam.encode;

import hmi.qam.util.CMUDict;
import info.debatty.java.stringsimilarity.SetBasedStringSimilarity;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class CMU implements PhonemeEncoderInterface {

    protected Map<String,String> dict;
    protected Map<String,String> phones;
    protected List<String> symbols;
    protected Map<String,String> vp;

    public CMU(){
        this.dict = CMUDict.getDict();
        this.phones = CMUDict.getPhones();
        this.symbols = CMUDict.getSymbols();
        this.vp = CMUDict.getVp();
    }

    @Override
    public String getEncoded(String sentence) {
        String cmuPhonetic = "";
        StringTokenizer tokenizer = new StringTokenizer(sentence.trim().toLowerCase());
        while(tokenizer.hasMoreTokens()){
            String word = tokenizer.nextToken();
            cmuPhonetic = cmuPhonetic + " " + dict.get(word);
        }
        return cmuPhonetic.trim();
    }

    @Override
    public double getSimilarity(SetBasedStringSimilarity s, String target, String real) {
        return s.similarity(this.getEncoded(target),this.getEncoded(real));
    }
}
