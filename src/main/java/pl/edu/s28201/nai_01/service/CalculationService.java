package pl.edu.s28201.nai_01.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import pl.edu.s28201.nai_01.model.IrisEntry;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CalculationService {

    public boolean isInteger(String string) {
        if (string == null) return false;
        try {
            Integer i = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean allInt(String... ints) {
        for (String s : ints) {
            if (!isInteger(s)) return false;
        }
        return true;
    }

    public boolean isDecimal(String s) {
        if (s == null) return false;
        try {
            BigDecimal d = new BigDecimal(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean allDecimals(String... decimals) {
        for (String s : decimals) {
            if (!isDecimal(s)) return false;
        }
        return true;
    }

    @SneakyThrows
    public BigDecimal getMinFromSet(Set<BigDecimal> keys) {
        if (keys.isEmpty()) throw new IllegalAccessException("No elements in keyset.");
        return keys.stream().min(BigDecimal::compareTo).get();

    }

    public BigDecimal distanceOf(IrisEntry iris, IrisEntry irisEntry) {
        List<BigDecimal> entryAttributes = irisEntry.getAttributes();
        List<BigDecimal> irisAttributes = iris.getAttributes();
        List<BigDecimal> distanceSquared = new ArrayList<>();

        for (int i = 0; i < entryAttributes.size(); i++) {
            BigDecimal distance = entryAttributes.get(i).subtract(irisAttributes.get(i));
            BigDecimal squared = distance.multiply(distance);

            distanceSquared.add(squared);
        }

        BigDecimal underSqrt = new BigDecimal(0);

        for (BigDecimal square : distanceSquared) {
            underSqrt = underSqrt.add(square);
        }

        return underSqrt.sqrt(MathContext.DECIMAL128);
    }

    public List<BigDecimal> stringsToDecimalList(String[] attributes) {
        List<BigDecimal> decimals = new ArrayList<>();

        for (String d : attributes) {
            decimals.add(new BigDecimal(d));
        }

        return decimals;
    }

    public String getAttributesStringFromAttributes(List<BigDecimal> decimals) {
        StringBuilder attrLine = new StringBuilder();

        for (BigDecimal d : decimals) {
            attrLine.append(d).append(",");
        }

        return attrLine.substring(0, attrLine.length() - 1);
    }
}
