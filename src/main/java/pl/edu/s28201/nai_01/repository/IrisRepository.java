package pl.edu.s28201.nai_01.repository;

import org.springframework.stereotype.Repository;
import pl.edu.s28201.nai_01.model.Iris;
import pl.edu.s28201.nai_01.model.IrisEntry;

import java.util.HashMap;
import java.util.Map;

@Repository
public class IrisRepository {

    private final Map<Iris, IrisEntry> decisionMatrix = new HashMap<>();


}
