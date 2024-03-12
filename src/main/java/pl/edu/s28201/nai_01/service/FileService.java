package pl.edu.s28201.nai_01.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    @SneakyThrows
    public List<String> readTrainFile(File trainFile) {
        List<String> entries = new ArrayList<>();

        BufferedReader fileRead = new BufferedReader(new FileReader(trainFile));
        String line;
        while ((line = fileRead.readLine()) != null) {
            entries.add(line);
        }

        return entries;
    }
}
