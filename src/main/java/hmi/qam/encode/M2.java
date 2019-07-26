package hmi.qam.encode;

import info.debatty.java.stringsimilarity.SetBasedStringSimilarity;
import org.apache.commons.codec.language.DoubleMetaphone;

import java.util.StringTokenizer;

public class M2 implements PhonemeEncoderInterface {

    private DoubleMetaphone code;

    public M2(){
        this.code = new DoubleMetaphone();
    }

    @Override
    public String getEncoded(String sentence) {
        StringTokenizer tokenizer = new StringTokenizer(sentence);
        String r = "";
        while (tokenizer.hasMoreTokens()) {
            String next = tokenizer.nextToken();
            code.setMaxCodeLen(next.length());
            r = r + code.encode(next);
        }
        return r.replaceAll(""," ");
    }

    @Override
    public double getSimilarity(SetBasedStringSimilarity s, String target, String real) {
        return s.similarity(this.getEncoded(target),this.getEncoded(real));
    }
}
