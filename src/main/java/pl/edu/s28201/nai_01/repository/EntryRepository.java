package pl.edu.s28201.nai_01.repository;

import org.springframework.stereotype.Repository;
import pl.edu.s28201.nai_01.model.Entry;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EntryRepository {

    private final List<Entry> decisionMatrix = new ArrayList<>();
    private final List<String> entryTypes = new ArrayList<>();
    public void addAll(List<Entry> entries) {
        decisionMatrix.addAll(entries);
        entryTypes.addAll(getEntryTypesFromEntries(entries));
    }

    private List<String> getEntryTypesFromEntries(List<Entry> entries) {
        List<String> entryTypes = new ArrayList<>();
        for (Entry e : entries) {
            if (!entryTypes.contains(e.getEntryType())) entryTypes.add(e.getEntryType());
        }
        return entryTypes;
    }

    public List<Entry> findAll() {
        return decisionMatrix;
    }

    public List<String> findAllEntryTypes() {
        return entryTypes;
    }

    public int entryCount() {
        return decisionMatrix.size();
    }
}
