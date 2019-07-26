package hmi.qam.encode;

import info.debatty.java.stringsimilarity.SetBasedStringSimilarity;

public class WeightedM3 extends M3 {

    private Weight weight;

    public WeightedM3(Weight weight){
        super();
        this.weight = weight;
    }

    @Override
    public double getSimilarity(SetBasedStringSimilarity s, String target, String real) {
       return weight.getSimilarity(s,this,target,real);
    }
}
