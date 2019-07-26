package hmi.qam;

import hmi.qam.util.CSV;
import hmi.qam.util.Encode;
import hmi.qam.util.QAPairs;
import info.debatty.java.stringsimilarity.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {

    public static void main (String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {

//        ArrayList<NodeList> actual = readFiles();
//        ArrayList<NodeList> prediction = writeFiles();
//        System.out.println(actual.toArray().toString() + prediction.toArray().toString());
//        testMetaphone();
        calculateScores();
//        QAPParser();
//        testCurl();


    }

    public static void testCurl(){

    }

    private static ArrayList<NodeList> writeFiles() throws ParserConfigurationException, IOException, XPathExpressionException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath;
        XPathExpression expr;
        ArrayList<NodeList> array = new ArrayList();

        for(int i = 1; i<= 8; i++){
            File f = new File(System.getProperty("user.dir") + "\\ASR\\"+"B" + i + "-asr.txt");
            if(f.exists()) {
                try (InputStream annotation = new FileInputStream(f)) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(annotation));
                    String data = "";
                    String line;
                    while ((line = br.readLine()) != null) {
                        String xml = line.replaceAll("&lt;UNK&gt;","[UNK]");
                        xml = xml.split(";")[2];
                        InputSource is = new InputSource(new StringReader(xml));
                        Document asr = builder.parse(is);
                        xpath = xPathfactory.newXPath();
                        expr = xpath.compile("/ASR_output/transcriptions/text/text()");
                        NodeList nl = (NodeList) expr.evaluate(asr, XPathConstants.NODESET);
                        array.add(nl);
                        data = data + "\n" + expr.evaluate(asr).toLowerCase();
                    }
                    String path = "B" + i + "-asr-cleaned.txt";
                    //writeToFile(path, data);
                }
            }
        }
        for(int i = 1; i<= 8; i++){
            File f = new File(System.getProperty("user.dir") + "\\ASR\\"+"C" + i + "-asr.txt");
            if(f.exists()) {
                try (InputStream annotation = new FileInputStream(f)) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(annotation));
                    String data = "";
                    String line;
                    while ((line = br.readLine()) != null) {
                        String xml = line.replaceAll("&lt;UNK&gt;","[UNK]");
                        xml = xml.split(";")[2];
                        InputSource is = new InputSource(new StringReader(xml));
                        Document asr = builder.parse(is);
                        xpath = xPathfactory.newXPath();
                        expr = xpath.compile("/ASR_output/transcriptions/text/text()");
                        data = data + "\n" + expr.evaluate(asr).toLowerCase();
                    }
                    String path = "C" + i + "-asr-cleaned.txt";
                    //writeToFile(path, data);
                }
            }
        }
        return array;
    }

    public static ArrayList<NodeList> readFiles() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr;
        ArrayList<NodeList> list = new ArrayList();
        for(int i = 1; i <= 8; i++){
            File f = new File(System.getProperty("user.dir") + "\\ASR\\" + "B"+i+"-cleaned.xml");
            if(f.exists()){
                try(InputStream file = new FileInputStream(f)){
                    Document doc = builder.parse(file);
                    expr = xpath.compile("/dialogue/part");
                    NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    list.add(nl);
                }
            }
        }
        for(int i = 1; i <= 8; i++){
            File f = new File(System.getProperty("user.dir") + "\\ASR\\" + "C"+i+"-cleaned.xml");
            if(f.exists()){
                try(InputStream file = new FileInputStream(f)){
                    Document doc = builder.parse(file);
                    expr = xpath.compile("/dialogue/part");
                    NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    list.add(nl);
                }
            }
        }
        return list;
    }

    public static void testMetaphone(){
        Levenshtein l = new Levenshtein();
        JaroWinkler j = new JaroWinkler();

        Encode k = new Encode();

        //String a = " When was Alice falling, she grabbed a jar";
        //String b = "gwen was of his fooling the good the";
        String a = "for how long was alice falling";
        String b = "how long was Enniskillen";
        System.out.println("a: " + a);
        System.out.println("b: " + b);
        System.out.println("Similarity: " + j.similarity(a,b));

        String c = k.getDoubleMetaphoneEncoding(a);
        String d = k.getDoubleMetaphoneEncoding(b, a.length());
        System.out.println("Double for a: " + c);
        System.out.println("Double for b: " + d);
        System.out.println("Double similarity: " + j.similarity(c,d));


        String aKey = k.getMetaphone3Sentence(a);
        String bKey = k.getMetaphone3Sentence(b, a.length());
        System.out.println("aKey: " + aKey);
        System.out.println("bKey: " + bKey);
        System.out.println("M3 Similarity: " + j.similarity(aKey,bKey));
    }

    public static void writeToFile(String filename, String text){

        File file = new File(System.getProperty("user.dir") + "/ASR/" + filename);
        file.getParentFile().mkdirs();
        System.out.println(file.getAbsolutePath());

        try (FileOutputStream fop = new FileOutputStream(file)) {

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = text.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void QAPParser(){
        String csv = System.getProperty("user.dir") + "\\ASR\\QAPairs.csv";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<QAPairs> qap = CSV.readFile(csv);
        int index = 0;
        try{
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.newDocument();

            Element rootElement = d.createElement("qa_root");
            d.appendChild(rootElement);

            for(QAPairs pair : qap){
                Element dialog = d.createElement("dialog");
                dialog.setAttribute("id",String.valueOf(index));
                rootElement.appendChild(dialog);
                Element qlist = d.createElement("questionlist");
                dialog.appendChild(qlist);
                Element question = d.createElement("question");
                qlist.appendChild(question);
                question.appendChild(d.createTextNode(pair.getActual()));
                Element alist = d.createElement("answerlist");
                dialog.appendChild(alist);
                Element answer = d.createElement("answer");
                answer.appendChild(d.createTextNode(pair.getAnswer()));
                alist.appendChild(answer);
                index++;
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(d);
            StreamResult result = new StreamResult(new File(System.getProperty("user.dir") + "\\QA.xml"));
            transformer.transform(source,result);

            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source,consoleResult);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

    public static void calculateScores(){

        String csv = System.getProperty("user.dir") + "\\ASR\\QAPairs.csv";
        List<QAPairs> qap = CSV.readFile(csv);
        Encode k = new Encode();

        computeScores(qap, new NGram(), k);

    }

    /**
     * Method that computes the scores for each question compared to each other question, for each similarity measure and each type of encoding
     * @param qap, the QA pairs
     * @param sim, the similarity metrics to be used
     * @param k, the encoding object for encoding the different types
     */
    private static void computeScores(List<QAPairs> qap, StringSimilarityInterface sim, Encode k){

        List<StringSimilarityInterface> simMeasures = new ArrayList();
        simMeasures.add(new JaroWinkler());
        simMeasures.add(new JaroWinkler(-1));
        simMeasures.add(new NGram(1));
        simMeasures.add(new NGram(2));
        simMeasures.add(new NGram(3));
        simMeasures.add(new Jaccard());
        simMeasures.add(new Cosine());
        //For spellchecking
        simMeasures.add(new Damerau());
        simMeasures.add(new QGram(1));
        simMeasures.add(new QGram(2));
        simMeasures.add(new QGram(3));
        simMeasures.add(new LongestCommonSubsequence());
        simMeasures.add(new Levenshtein());
        simMeasures.add(new SorensenDice());

        int encoded = 0;
        int string = 0;
        int equal = 0;
        //double improvement = 0.0;
        DecimalFormat df = new DecimalFormat("#.###");
        //List of questions: For all the questions we want to measure
        // List of other questions: how they compare to all other questions
        //  List of similarity measure: based on several similarity measures
        //   List of encoding measure: by using different types of encodings        
        df.setRoundingMode(RoundingMode.CEILING);
        Map<QAPairs,Map<QAPairs,Map<QAPairs,Map<String,Double>>>> similarities = new HashMap();
        for(QAPairs p : qap){
            Map<QAPairs,Map<QAPairs,Map<String,Double>>> questions = new HashMap();
            for(int i=0; i<qap.size();i++){
                Map<QAPairs,Map<String,Double>> comparisons = new HashMap();                
                for(StringSimilarityInterface j : simMeasures){
                    Map<String,Double> similarity = new HashMap();
                    similarity.put("grapheme",j.similarity(p.getActual(),p.getPredicted()));
                    String dmActual = k.getDoubleMetaphoneEncoding(p.getActual());
                    String dmPredicted = k.getDoubleMetaphoneEncoding(p.getPredicted());
                    similarity.put("dm",j.similarity(dmActual,dmPredicted));
                    String m3Actual = k.getMetaphone3Sentence(p.getActual());
                    String m3Predicted = k.getMetaphone3Sentence(p.getPredicted());
                    similarity.put("m3",j.similarity(m3Actual,m3Predicted));
                    String cmuActual = k.getCMUDictSentence(p.getActual());
                    String cmuPredicted = k.getCMUDictSentence(p.getPredicted());
                    similarity.put("cmu",j.similarity(cmuActual,cmuPredicted));
                    similarity.put("wcmu",k.weightedSentenceSimilarity(cmuPredicted,cmuPredicted));
                    comparisons.put(qap.get(i),similarity);
                }
                questions.put(qap.get(i),comparisons);
            }
            similarities.put(p,questions);
        }

            //System.out.printf("Performance %s (String/Encoded/Equal): %s/%s/%s\n",j.getClass().toString(),string, encoded, equal);
            //System.out.printf("Improvement: %s", improvement/ qap.size());
            encoded = 0;
            string = 0;
            equal = 0;
            //improvement = 0.0;


    }




}
