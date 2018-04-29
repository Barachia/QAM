package hmi.qam;

import hmi.qam.util.Encode;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.Levenshtein;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import java.io.*;
import java.net.URL;


public class Main {

    public static void main (String[] args) {

        testMetaphone();




    }

    public void readFiles() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        //BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResource("B1-cleaned.xml")));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream file = classLoader.getResourceAsStream("B1-cleaned.xml");

        Document doc = builder.parse(file);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/dialogue/part");

        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for(int i = 0; i < nl.getLength(); i++){
            //System.out.println(nl.item(i).getTextContent());
        }
        for(int i = 1; i<= 1; i++){
            InputStream annotation = classLoader.getResourceAsStream("B"+i+"-asr.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(annotation));
            String data ="";
            String line;
            while((line = br.readLine()) != null){
                String xml = line.split(";")[2];
                InputSource is = new InputSource(new StringReader(xml));
                Document asr = builder.parse(is);
                xpath = xPathfactory.newXPath();
                expr = xpath.compile("/ASR_output/transcriptions/text/text()");
                data = data + "\n" +  expr.evaluate(asr).toLowerCase();
            }
            String path =  "B"+i+"-asr-cleaned.txt";
            //writeToFile(path,data);
        }
    }

    public static void testMetaphone(){
        Levenshtein l = new Levenshtein();
        JaroWinkler j = new JaroWinkler();

        Encode k = new Encode();

        String a = " When was Alice falling, she grabbed a jar";
        String b = "gwen was of his fooling the good the";
        System.out.println("a: " + a);
        System.out.println("b: " + b);
        System.out.println("Similarity: " + j.similarity(a,b));

        String c = k.getDoubleMetaphoneEncoding(a);
        System.out.println(c);


        String aKey = k.getEncoding(a);
        String bKey = k.getEncoding(b,aKey.length());
        System.out.println("aKey: " + aKey);
        System.out.println("bKey: " + bKey);
        System.out.println("Similarity: " + j.similarity(aKey,bKey));
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


}
