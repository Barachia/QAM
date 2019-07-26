package hmi.qam.util;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

public class Dialog {

    @XmlAttribute(name="id")
    private int id;

    private List<String> questions;


    private List<String> answers;

    public Dialog(){
        id = 0;
        this.questions = new ArrayList<>();
        this.answers = new ArrayList<>();
    }

    @XmlElementWrapper(name="questionlist")
    @XmlElement(name="question")
    public void setQuestions(List<String> questions){
        this.questions = questions;
    }


    @XmlElementWrapper(name="answerlist")
    @XmlElement(name="answer")
    public void setAnswers(List<String> questions){
        this.answers = answers;
    }

    public List<String> getQuestions(){
        return this.questions;
    }

    public List<String> getAnswers(){
        return this.answers;
    }

    public int getId(){
        return this.id;
    }
}
