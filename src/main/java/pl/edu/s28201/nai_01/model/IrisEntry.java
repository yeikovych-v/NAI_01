package pl.edu.s28201.nai_01.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IrisEntry {

    private BigDecimal first;
    private BigDecimal second;
    private BigDecimal third;
    private BigDecimal fourth;
    private Iris flowerType;
}
