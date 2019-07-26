package hmi.qam.util;

import info.debatty.java.stringsimilarity.*;

public class Similarity {

    private SetBasedStringSimilarity metric;
    private boolean weighted;
    private Encode code;

    public Similarity(){
        this(new Cosine(3),false);
    }

    public Similarity(boolean weighted){
        this(new Cosine(3),weighted);
    }

    public Similarity(SetBasedStringSimilarity metric){
        this(metric,false);
    }

    public Similarity(SetBasedStringSimilarity metric, boolean weighted){
        this.metric = metric;
        this.weighted = weighted;
    }

    public static void main(String[] args){
        Similarity s = new Similarity();
        Similarity ws = new Similarity(true);

        String a = "what did the white rabbit say";
        String b = "what the two armed robot say";

        double as = s.metric.similarity(a,b);
        double aws = ws.metric.similarity(a,b);


    }
}
