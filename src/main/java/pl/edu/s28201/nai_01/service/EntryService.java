package pl.edu.s28201.nai_01.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import pl.edu.s28201.nai_01.model.Entry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class EntryService {

    private int paramsNum;

    public List<Entry> parseToEntries(List<String> entries) {
        List<Entry> newEntries = new ArrayList<>();
        for (String entry : entries) newEntries.add(parseEntry(entry));
        return newEntries;
    }

    public Entry parseEntry(String entryString) {
        String[] entryCase = entryString.split(",");
        paramsNum = entryCase.length - 1;
        List<BigDecimal> entryAttributes = new ArrayList<>();
        for (int i = 0; i < entryCase.length - 1; i++) {
            BigDecimal attr = new BigDecimal(entryCase[i]);
            entryAttributes.add(attr);
        }
        String entryType = entryCase[4];

        return new Entry(entryAttributes, entryType);
    }

//    public String getEntryTypeFromString(String entryType) {
//        return switch (entryType) {
//            case "Iris-virginica" -> Iris.VIRGINICA;
//            case "Iris-setosa" -> Iris.SETOSA;
//            case "Iris-versicolor" -> Iris.VERSICOLOR;
//            default -> throw new UnsupportedIrisException("Invalid Entry type: " + entryType);
//        };
//    }

}
