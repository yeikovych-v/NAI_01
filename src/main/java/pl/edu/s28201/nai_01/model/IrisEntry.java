package pl.edu.s28201.nai_01.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class IrisEntry {

    private List<BigDecimal> attributes;
    private Iris flowerType;

    public IrisEntry(List<BigDecimal> attributes) {
        this.attributes = attributes;
    }
}
