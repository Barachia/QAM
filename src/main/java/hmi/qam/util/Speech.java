package hmi.qam.util;

import SpeechAPIDemo.ExampleApp;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.intersection;
import static org.apache.commons.collections4.CollectionUtils.subtract;

public class Speech {

    public static void main(String[] args) throws IOException {

        Encode encode = new Encode();

        String asrText = "how all this follows";
        String realText = "how old is alice";
        String phoneticAsrText = encode.getDoubleMetaphoneEncoding(asrText);
        String phoneticRealText = encode.getDoubleMetaphoneEncoding(realText);
        List<String> l = new ArrayList();

        List<String> asrUni = encode.getUnigrams(asrText);
        List<String> asrTwo = encode.getTwograms(asrText);
        List<String> asrTri = encode.getTrigrams(asrText);

        List<String> realUni = encode.getUnigrams(realText);
        List<String> realTwo = encode.getTwograms(realText);
        List<String> realTri = encode.getTrigrams(realText);

        List<String> phoneticAsrUni = encode.getUnigrams(phoneticAsrText);
        List<String> phoneticAsrTwo = encode.getTwograms(phoneticAsrText);
        List<String> phoneticAsrTri = encode.getTrigrams(phoneticAsrText);

        List<String> phoneticRealUni = encode.getUnigrams(phoneticRealText);
        List<String> phoneticRealTwo = encode.getTwograms(phoneticRealText);
        List<String> phoneticRealTri = encode.getTrigrams(phoneticRealText);

        System.out.println("done");
        List<String> similarityUni = (List<String>) intersection(asrUni,realUni);
        List<String> differenceUni = (List<String>) subtract(asrUni, realUni);

        List<String> similarityTwo = (List<String>) intersection(asrTwo,realTwo);
        List<String> differenceTwo = (List<String>) subtract(asrTwo, realTwo);

        List<String> similarityTri = (List<String>) intersection(asrTri,realTri);
        List<String> differenceTri = (List<String>) subtract(asrTri, realTri);

        System.out.println("Similar: " + asrUni);
        System.out.println("Different: " + realUni);

    }

    public static int computeOverlap(List<String> x, List<String> y){
        return 0;


    }



}
