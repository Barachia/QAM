package hmi.qam.encode;

import info.debatty.java.stringsimilarity.SetBasedStringSimilarity;

public class WeightedCMU extends CMU {

    private Weight weight;

    public WeightedCMU(Weight weight){
        super();
        this.weight = weight;
    }


    @Override
    public double getSimilarity(SetBasedStringSimilarity s, String target, String real) {
        return weight.getSimilarity(s,this,target,real);
    }

}
