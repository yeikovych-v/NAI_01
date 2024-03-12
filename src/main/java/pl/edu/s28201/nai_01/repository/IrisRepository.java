package pl.edu.s28201.nai_01.repository;

import org.springframework.stereotype.Repository;
import pl.edu.s28201.nai_01.model.IrisEntry;

import java.util.ArrayList;
import java.util.List;

@Repository
public class IrisRepository {

    private final List<IrisEntry> decisionMatrix = new ArrayList<>();

    public void addAll(List<IrisEntry> irisEntries) {
        decisionMatrix.addAll(irisEntries);
    }

    public List<IrisEntry> findAll() {
        return decisionMatrix;
    }
}
