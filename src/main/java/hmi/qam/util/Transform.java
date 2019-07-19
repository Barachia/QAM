package hmi.qam.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.shingle.ShingleFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Transform {

    private Analyzer analyzer;
    private NGramTokenFilter ngramfilter;

    public List<String> generateNGrams(String text){
        List l = new ArrayList();
        try {
            TokenStream stream  = analyzer.tokenStream(null, new StringReader(text));
            ShingleFilter theFilter = new ShingleFilter(stream);
            theFilter.setOutputUnigrams(false);
            CharTermAttribute charTermAttribute = theFilter.addAttribute(CharTermAttribute.class);
            theFilter.reset();
            //stream.reset();
            while (theFilter.incrementToken()) {
                l.add(charTermAttribute.toString());
            }
            theFilter.end();
            theFilter.close();
        } catch (IOException e) {
            // not thrown b/c we're using a string reader...
            throw new RuntimeException(e);
        }
        return l;
    }

    public Transform(){
        this.analyzer = new StandardAnalyzer();

    }




}
