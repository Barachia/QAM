package hmi.qam.encode;

import info.debatty.java.stringsimilarity.SetBasedStringSimilarity;

import java.util.StringTokenizer;

public class M3 implements PhonemeEncoderInterface {

    private Metaphone3 code;

    public M3(){
        this.code = new Metaphone3();
    }

    @Override
    public String getEncoded(String sentence) {
        StringTokenizer tokenizer = new StringTokenizer(sentence);
        String r = "";
        while (tokenizer.hasMoreTokens()) {
            String next = tokenizer.nextToken();
            code.SetKeyLength(next.length());
            code.SetWord(next);
            code.Encode();
            r = r + code.GetMetaph();
        }
        return r.replaceAll(""," ");
    }

    @Override
    public double getSimilarity(SetBasedStringSimilarity s, String target, String real) {
        return s.similarity(this.getEncoded(target),this.getEncoded(real));
    }

}
