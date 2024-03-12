package pl.edu.s28201.nai_01.service;

import lombok.Getter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import pl.edu.s28201.nai_01.exception.UnsupportedIrisException;
import pl.edu.s28201.nai_01.model.Iris;
import pl.edu.s28201.nai_01.model.IrisEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class IrisService {

    private int paramsNum;

    public List<IrisEntry> parseToIrises(List<String> irises) {
        List<IrisEntry> irisEntries = new ArrayList<>();
        for (String iris : irises) irisEntries.add(parseIris(iris));
        return irisEntries;
    }

    public IrisEntry parseIris(String iris) {
        String[] irisCase = iris.split(",");
        paramsNum = irisCase.length - 1;
        List<BigDecimal> irisAttributes = new ArrayList<>();
        for (int i = 0; i < irisCase.length - 1; i++) {
            BigDecimal attr = new BigDecimal(irisCase[i]);
            irisAttributes.add(attr);
        }
        Iris irisType = getIrisTypeFromString(irisCase[4]);

        return new IrisEntry(irisAttributes, irisType);
    }

    public Iris getIrisTypeFromString(String irisType) {
        return switch (irisType) {
            case "Iris-virginica" -> Iris.VIRGINICA;
            case "Iris-setosa" -> Iris.SETOSA;
            case "Iris-versicolor" -> Iris.VERSICOLOR;
            default -> throw new UnsupportedIrisException("Invalid Iris type: " + irisType);
        };
    }

}
