package hmi.qam.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hmi.qam.util.QAMMiddleware;
import info.debatty.java.stringsimilarity.JaroWinkler;
import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.GenericMiddlewareLoader;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class OnlineResponder {

    private static Middleware middleware;
    private static QAMMiddleware mwQAM;

    private static Logger logger = LoggerFactory.getLogger(OnlineResponder.class.getName());


    private static void initMW(Properties ps) {
        //Properties ps = new Properties();
        //ps.put("iTopic", "/topic/dummyIn");
        //ps.put("oTopic", "/topic/isDump");

        GenericMiddlewareLoader gml = new GenericMiddlewareLoader("nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader", ps);
        middleware = gml.load();
        mwQAM = new QAMMiddleware("qam.properties");
    }


    public static void main(String[] args) throws JMSException {
        String filename = System.getProperty("user.dir") + "\\QA.xml";
        String dr_file = "defaultanswers.txt";
        QA tqa = new QA(filename, dr_file);
        //Middleware mw = new ActiveMQMiddleware("tcp://localhost:61616", "speechFinal", "speechResponse");

        //String [] speechArguments = new String[20];
        //speechArguments[0] = "nlspraak.ewi.utwente.nl:8890";
        //ExampleApp.main(speechArguments);
//
//        try {
//            Runtime.getRuntime().exec("java -jar ../SpeechAPIDemo/SpeechAPIDemo-1.0/SpeechAPIDemo-1.0.jar nlspraak.ewi.utwente.nl:8890");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        boolean exit = false;

        String attName = "type";
        String attValue = "certain";
        DialogStore store = tqa.getStore();

        String prop = "qam.properties";
        GenericMiddlewareLoader.setGlobalPropertiesFile(prop);
        Properties ps = new Properties();
        try {
            ps.load(OnlineResponder.class.getClassLoader().getResourceAsStream(prop));
        } catch (IOException e) {
            System.out.println("failed properties");
            e.printStackTrace();
        }
        initMW(ps);

        //ActiveMQ stuff
//        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
//        Connection conn = connectionFactory.createConnection();
//        conn.start();
//        Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        Destination dest = sess.createTopic("SampleTopic");
//        MessageConsumer cons = sess.createConsumer(dest);
//        Message message;


        System.out.flush();
        final Scanner sis = new Scanner(System.in);
        String previousQuery = " ";
        String query = "";
        while(!query.equals("exit")){
//            if(sis.hasNextLine()) {
//                query = sis.nextLine().toLowerCase();
//            }
            if(mwQAM.isConnected()){
                if(mwQAM.hasMessage()){
                    String message = mwQAM.getMessage();
                    //System.out.println(message);
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JsonNode node = mapper.readTree(message);
                        String type = node.path("type").asText();
                        query = node.get("text").asText();
                        if(type.equals("final")){
                            previousQuery = query;
                            //System.out.println("Query: " + query);
                            Pair<Dialogs, Double> match = null;
                            try {
                                match = store.getBestMatchingDialogAndScore(query, "grapheme",true, new JaroWinkler(),0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Pair<Dialogs, Double> encoded = null;
                            try {
                                encoded = store.getBestMatchingDialogAndScore(query, "dm", true, new JaroWinkler(),0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            List l = store.retrieveQueries(query);
                            String bestQuery;
                            String answer;
                            String encoded_query;
                            String encoded_answer;
                            if (match.getKey() == null) {
                                bestQuery = encoded_query = "?";
                                answer = encoded_answer = store.RandomDefaultAnswer();
                            } else {
                                if (match.getValue() >= 0.05) {
                                    bestQuery = match.getKey().getQuestion(0);
                                    encoded_query = encoded.getKey().getQuestion(0);
                                    //answer = tr.store.answerString(match.getKey(), attName, attValue);
                                    List<AnswerType> answers = match.getKey().answers;
                                    int randomNum = ThreadLocalRandom.current().nextInt(0, answers.size());
                                    answer = answers.get(randomNum).answer;
                                    List<AnswerType> encoded_answers = encoded.getKey().answers;
                                    randomNum = ThreadLocalRandom.current().nextInt(0, encoded_answers.size());
                                    encoded_answer = encoded_answers.get(randomNum).answer;
                                } else {
                                    bestQuery = encoded_query = "?";
                                    answer = encoded_answer = store.RandomDefaultAnswer();
                                }
                            }
                            //writer.write(answer + "\n");
                            //List<Pair<Dialogs, Double>> queries = tr.store.retrieveQueries(query);
                            //System.out.println("Queries and scores: " + queries.get(0).getLeft() + queries.get(0).getRight().toString());
                            logger.info("Question: " + query);
                            logger.debug("Best match query: (U) {} and (E) {} \n",bestQuery, encoded_query);
                            logger.debug("Score: (U) {} and (E) {} \n", match.getValue(), encoded.getValue());
                            logger.info("Best answer: (U) {} and (E) {} \n", answer, encoded_answer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
    }


    public static void Log(String s) {
        logger.debug("\n===\n{}\n===", s);
    }
}


//                message = cons.receive();
//                //Reconstruct the Bytesmessage to a String using ASCII
//                if (message instanceof BytesMessage && message != null) {
//                    StringBuilder sb = new StringBuilder();
//                    BytesMessage bytesMessage = (BytesMessage) message;
//                    for (int i = 0; i < bytesMessage.getBodyLength(); i++) {
//                        sb.append(Character.toChars(bytesMessage.readByte()));
//                    }
//                    query = sb.toString();
//                }







