package hmi.qam.util;

import com.opencsv.bean.CsvBindByName;

public class QAPairs {

    @CsvBindByName
    private String actual;

    @CsvBindByName
    private String predicted;

    @CsvBindByName
    private String answer;

    @CsvBindByName
    private String match;

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getPredicted() {
        return predicted;
    }

    public void setPredicted(String predicted) {
        this.predicted = predicted;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getMatch(){ return match; }

    public void setMatch(String match){this.match = match;}
}
