package hmi.qam.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;


public class CSV {


    public static List<QAPairs> readFile(String filename){
        try {

            Reader reader2 = Files.newBufferedReader(Paths.get(filename));
            CsvToBean<QAPairs> qapairs = new CsvToBeanBuilder(reader2)
                    .withType(QAPairs.class)
                    .withSeparator(';')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return qapairs.parse();

            } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
