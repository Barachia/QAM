package hmi.qam.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CMUDict {

    private static final String location = "cmudict";

    private static final Logger logger =
            LoggerFactory.getLogger(CMUDict.class);

    public static final Map<String, String> getDict() {
        logger.info("Loading cmudict.dict");
        Stream<String> stream = CMUDict.getDictStream();
        Map<String, String> dictMap = new HashMap<String, String>();
        stream.forEach(line -> {
            String[] parts = line.split("\\s+", 2);
            dictMap.put(parts[0], parts[1]);
        });
        return dictMap;
    }

    public static final Stream<String> getDictStream() {
        Stream<String> dictStream = CMUDict.resourceAsStream(location + "/cmudict.dict");
        return dictStream;
    }

    public static final String getDictString() {
        String dictString = CMUDict.resourceAsString(location + "/cmudict.dict");
        return dictString;
    }

    public static final Map<String, String> getPhones() {
        logger.info("Loading cmudict.phones");
        Stream<String> stream = CMUDict.getPhonesStream();
        Map<String, String> phonesMap = new HashMap<String, String>();
        stream.forEach(line -> {
            String[] parts = line.split("\\s+", 2);
            phonesMap.put(parts[0], parts[1]);
        });
        return phonesMap;
    }

    public static final Stream<String> getPhonesStream() {
        Stream<String> phonesStream = CMUDict.resourceAsStream(location + "/cmudict.phones");
        return phonesStream;
    }

    public static final String getPhonesString() {
        String phonesString = CMUDict.resourceAsString(location + "/cmudict.phones");
        return phonesString;
    }

    public static final List<String> getSymbols() {
        logger.info("Loading cmudict.symbols");
        Stream<String> stream = CMUDict.getSymbolsStream();
        List<String> symbolsList = new ArrayList<String>();
        stream.forEach(line -> {
            symbolsList.add(line);
        });
        return symbolsList;
    }

    public static final Stream<String> getSymbolsStream() {
        Stream<String> symbolsStream = CMUDict.resourceAsStream(location + "/cmudict.symbols");
        return symbolsStream;
    }

    public static final String getSymbolsString() {
        String symbolsString = CMUDict.resourceAsString(location + "/cmudict.symbols");
        return symbolsString;
    }

    public static final Map<String, String> getVp() {
        logger.info("Loading cmudict.vp");
        Stream<String> stream = CMUDict.getVpStream();
        Map<String, String> vpMap = new HashMap<String, String>();
        stream.forEach(line -> {
            String[] parts = line.split("\\s+", 2);
            String wordString = parts[0];
            vpMap.put(wordString, parts[1]);
        });
        return vpMap;
    }

    public static final Stream<String> getVpStream() {
        Stream<String> vpStream = CMUDict.resourceAsStream(location + "/cmudict.vp");
        return vpStream;
    }

    public static final String getVpString() {
        String vpString = CMUDict.resourceAsString(location + "/cmudict.vp");
        return vpString;
    }

    /**
     * Get The CMU Dictionary license as a string.
     *
     * @return string of The CMU Dictionary license file
     *
     */
    public static final String getLicenseString() {
        return CMUDict.resourceAsString(location + "/LICENSE");
    }

    private static final Stream<String> resourceAsStream(final String resource) {
        InputStream in = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Stream<String> resourceStream = reader.lines();
        return resourceStream;
    }

    private static final String resourceAsString(final String resource) {
        // Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        Stream<String> stream = CMUDict.resourceAsStream(resource);
        String resourceString = stream.collect(Collectors.joining("\n"));
        return resourceString;
    }



}