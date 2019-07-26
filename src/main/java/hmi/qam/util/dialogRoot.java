package hmi.qam.util;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name="qa_root")
@XmlAccessorType(XmlAccessType.FIELD)
public class dialogRoot {

    @XmlElement(name="topic")
    private String topic;

    @XmlElementWrapper(name="dialogs")
    @XmlElement(name="dialog")
    private List<Dialog> dialogs;

    public dialogRoot(){
        this.topic = "Alice";
        this.dialogs = new ArrayList<>();
    }


    public void setDialogs(List<Dialog> dialogs){
        this.dialogs = dialogs;
    }

    public List<Dialog> getDialogs(){
        return this.dialogs;
    }


}
