package pl.edu.s28201.nai_01.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.edu.s28201.nai_01.repository.IrisRepository;
import pl.edu.s28201.nai_01.service.DistanceService;
import pl.edu.s28201.nai_01.service.FileService;

import java.io.BufferedReader;
import java.io.File;
import java.util.List;

@Controller
public class ProgramController {

    private final FileService fileService;
    private final IrisRepository irisRepository;
    private final DistanceService distanceService;
    private final BufferedReader console;

    @Autowired
    public ProgramController(FileService fileService, IrisRepository irisRepository, DistanceService distanceService, BufferedReader console) {
        this.fileService = fileService;
        this.irisRepository = irisRepository;
        this.distanceService = distanceService;
        this.console = console;
    }

    public void startProgram() {
        requestTestFile();

    }

    @SneakyThrows
    private void requestTestFile() {
        System.out.print("Enter the path to your train file: ");
        String pathToFile = console.readLine().trim();
        File trainFile = new File(pathToFile);

        while (!trainFile.exists()) {
            System.out.println("Incorrect file path.");
            System.out.print("Enter the path to your train file: ");
            pathToFile = console.readLine().trim();
            trainFile = new File(pathToFile);
        }

        parseToIrises(fileService.readTrainFile(trainFile));
    }

    private void parseToIrises(List<String> irises) {

    }
}
