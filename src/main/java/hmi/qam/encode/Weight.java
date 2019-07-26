package hmi.qam.encode;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import hmi.qam.util.Encode;
import info.debatty.java.stringsimilarity.SetBasedStringSimilarity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Weight {

    private final Map<String, Double> initialWeights;
    private final Map<String, Double> finalWeights;

    public Weight() {
        this("consonant/initialConsonantARPA.csv","consonant/finalConsonantARPA.csv");
    }

    public Weight(String initialC, String finalC){
        this.initialWeights = this.weights(this.loadMatrix(initialC));
        this.finalWeights = this.weights(this.loadMatrix(finalC));
    }

    private List<List<String>> loadMatrix(String filename){
        List<List<String>> lines = new ArrayList();
        InputStreamReader reader = new InputStreamReader(Encode.class.getClassLoader().getResourceAsStream(filename), StandardCharsets.UTF_8);
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .build();
        try (CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(parser)
                .build();) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                lines.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  lines;
    }

    private Map<String, Double> weights(List<List<String>> matrix){
        Map<String,Double> weights = new HashMap();
        List<String> labels = matrix.get(0);
        for(int i=1;i<matrix.size();i++){
            List<String> row = matrix.get(i).subList(1,matrix.size());
            double sum = row.stream().mapToInt(string -> Integer.parseInt(string)).sum();
            double tp = Double.valueOf(row.get(i-1));
            weights.put(labels.get(i), tp / sum);
        }
        return weights;
    }

    public Map<String,Double> getInitialWeights(){
        return this.initialWeights;
    }

    public Map<String,Double> getFinalWeights(){
        return this.finalWeights;
    }

    public double getSimilarity(SetBasedStringSimilarity s, PhonemeEncoderInterface e, String target, String real) {
        double sim = 0.0;
        int counter = 0;
        StringTokenizer wordTokenizer = new StringTokenizer(target);
        while(wordTokenizer.hasMoreTokens()){
            String word = wordTokenizer.nextToken();
            String code = e.getEncoded(word);
            StringTokenizer phonemeTokenizer = new StringTokenizer(code);
            boolean first = true;
            while (phonemeTokenizer.hasMoreTokens()) {
                String phoneme = phonemeTokenizer.nextToken();
                counter++;
                if(first && this.getInitialWeights().get(phoneme) != null){
                    sim += this.getInitialWeights().get(phoneme);
                }
                else if(this.getFinalWeights().get(phoneme) != null){
                    sim += this.getFinalWeights().get(phoneme);
                }
                else{
                    sim += 1;
                }
                first = false;
            }
        }
        return (sim/counter) * s.similarity(e.getEncoded(target),e.getEncoded(real));
    }
}
