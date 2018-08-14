package hmi.qam.util;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;

public class NLP {

    public static String removeStopWords(String string) throws IOException
    {
        StandardAnalyzer ana = new StandardAnalyzer();
        TokenStream tokenStream = ana.tokenStream("query", new StringReader(string));
        StringBuilder sb = new StringBuilder();
        tokenStream = new StopFilter(tokenStream, ana.STOP_WORDS_SET);
        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken())
        {
            String term = token.toString();
            sb.append(term + " ");
        }
        return sb.toString();
    }

}
