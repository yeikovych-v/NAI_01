package pl.edu.s28201.nai_01.controller;

import com.google.common.collect.ArrayListMultimap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.edu.s28201.nai_01.exception.UnsupportedEntryTypeException;
import pl.edu.s28201.nai_01.model.Entry;
import pl.edu.s28201.nai_01.repository.EntryRepository;
import pl.edu.s28201.nai_01.service.CalculationService;
import pl.edu.s28201.nai_01.service.FileService;
import pl.edu.s28201.nai_01.service.EntryService;

import java.io.BufferedReader;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Controller
public class ProgramController {

    private final FileService fileService;
    private final EntryRepository entryRepository;
    private final CalculationService calculationService;
    private final BufferedReader console;
    private final EntryService entryService;
    private int k;

    @Autowired
    public ProgramController(FileService fileService, EntryRepository entryRepository, CalculationService calculationService, BufferedReader console, EntryService entryService) {
        this.fileService = fileService;
        this.entryRepository = entryRepository;
        this.calculationService = calculationService;
        this.console = console;
        this.entryService = entryService;
    }

    public void startProgram() {
        File trainFile = requestTestFile();

        entryRepository.addAll(entryService.parseToEntries(fileService.readTrainFile(trainFile)));

        k = requestK();

        activateConsole();
    }

    // ----------------- CONSOLE -------------------
    @SneakyThrows
    private void activateConsole() {
        System.out.println("Use 'help' command for reference.");
        System.out.print("Type your command here: ");
        String command = console.readLine();
        System.out.println();

        while (!command.equalsIgnoreCase("exit")) {
            if (!executeCommand(command)) System.out.println("Invalid command. Use 'help' command for reference.");
            System.out.print("Type your command here: ");
            command = console.readLine();
            System.out.println();
        }
    }

    @SneakyThrows
    private int requestK() {
        System.out.print("Enter number K: ");
        String strK = console.readLine().trim();
        System.out.println();

        while (!calculationService.isInteger(strK) || !calculationService.isValidK(Integer.parseInt(strK), entryRepository.entryCount())) {
            System.out.println("K value cannot be less than 1 or more than train size.");
            System.out.print("Enter number K: ");
            strK = console.readLine().trim();
            System.out.println();
        }

        return Integer.parseInt(strK);
    }

    @SneakyThrows
    private File requestTestFile() {
        System.out.print("Enter the path to your train file: ");
        String pathToFile = console.readLine().trim();
        File trainFile = new File(pathToFile);
        System.out.println();

        while (!trainFile.exists()) {
            System.out.println("Incorrect file path.");
            System.out.print("Enter the path to your train file: ");
            pathToFile = console.readLine().trim();
            trainFile = new File(pathToFile);
            System.out.println();
        }

        return trainFile;
    }

    // ----------------- COMMANDS -------------------

    private boolean executeCommand(String command) {
        command = command.trim();
        String[] splitCommands = command.split("\\s");
        return switch (splitCommands[0]) {
            case "help" -> executeHelp();
            case "testf" -> {
                if (splitCommands.length != 2) yield false;
                yield executeTestF(splitCommands[1].trim());
            }
            case "test" -> {
                if (splitCommands.length != (entryService.getParamsNum() + 1)) yield false;
                yield executeTest(splitCommands);
            }
            case "setk" -> {
                if (splitCommands.length != 2) yield false;
                yield executeSetK(splitCommands[1]);
            }
            default -> false;
        };
    }

    private boolean executeSetK(String strK) {
        if (!calculationService.isInteger(strK)) return false;
        if (!calculationService.isValidK(Integer.parseInt(strK), entryRepository.entryCount())) {
            System.out.println("K value cannot be less than 1 or more than train size.");
            return false;
        }
        k = Integer.parseInt(strK);
        return true;
    }

    private boolean executeTest(String[] commands) {
        String[] attributes = Arrays.copyOfRange(commands, 1, commands.length);
        if (!calculationService.allDecimals(attributes)) return false;
        return executeTest(calculationService.stringsToDecimalList(attributes));
    }

    private boolean executeTest(List<BigDecimal> decimals) {
        String computedSet = findResultSet(new Entry(decimals), entryRepository.findAll());
        String preparedAttributes = calculationService.getAttributesStringFromAttributes(decimals);
        System.out.println("Entry with attributes: {" + preparedAttributes + "} belongs to " + computedSet);
        return true;
    }

    private boolean executeTestF(String testPath) {
        try {
            File testFile = new File(testPath);
            if (!testFile.exists()) {
                System.out.println("File with given path does not exist.");
                return false;
            }

            List<Entry> testEntries = entryService.parseToEntries(fileService.readTrainFile(testFile));
            int matchingCount = 0;
            for (Entry Entry : testEntries) {
                String computedSet = findResultSet(Entry, testEntries);
                String preparedAttributes = calculationService.getAttributesStringFromAttributes(Entry.getAttributes());
                System.out.println("Entry with attributes: {" + preparedAttributes + "} computed to be {"
                        + computedSet + "}, actually belongs to {" + Entry.getEntryType() + "}");
                if (Entry.getEntryType().equals(computedSet)) matchingCount++;
            }

            BigDecimal accuracy = BigDecimal.valueOf((double) matchingCount).divide(new BigDecimal(testEntries.size()), MathContext.DECIMAL128)
                    .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
            System.out.println("Accuracy in given test(in percent): " + accuracy.doubleValue() + "%");

            return true;
        } catch (Exception e) {
            System.out.println("File with given path does not exist.");
            return false;
        }
    }

    private boolean executeHelp() {
        System.out.println("Commands List: ------------------------------->");
        System.out.println("help   <> list available commands.");
        System.out.println("testf ../path/to/file.txt  <>  prints the label for each case and checks for accuracy.");
        System.out.println("test firstNum secondNum thirdNum fourthNum  <>  prints the label for the given case.");
        System.out.println("setk n  <>  sets k value to n.");
        System.out.println("---------------------------------------------->");
        System.out.println();
        return true;
    }

    // ----------------- LOGIC -------------------

    private String findResultSet(Entry entry, List<Entry> trainData) {
        List<Entry> entries = new ArrayList<>(trainData);

        ArrayListMultimap<BigDecimal, Entry> distanceMultimap = ArrayListMultimap.create();

        entries.forEach(e -> {
            BigDecimal distance = calculationService.distanceOf(e, entry);
            distanceMultimap.put(distance, e);
        });

        List<Entry> finalSubset = getEntrySubset(distanceMultimap);

        return getMostCommonEntryFromSubset(finalSubset);
    }

    private String getMostCommonEntryFromSubset(List<Entry> finalSubset) {
        Map<String, Long> entryTypeMap = new HashMap<>();

        for (String entryType : entryRepository.findAllEntryTypes()) {
            entryTypeMap.put(entryType, finalSubset.stream().filter(e -> e.getEntryType().equals(entryType)).count());
        }

        String mostCommonEntryType = null;
        long maxCount = 0;

        for (String entryType : entryTypeMap.keySet()) {
            long curVal = entryTypeMap.get(entryType);
            if (maxCount < curVal) {
                maxCount = curVal;
                mostCommonEntryType = entryType;
            }
        }

        if (mostCommonEntryType == null) throw new UnsupportedEntryTypeException("Unable to determine entry type.");

        return mostCommonEntryType;
    }

    private List<Entry> getEntrySubset(ArrayListMultimap<BigDecimal, Entry> distanceMultimap) {
        Set<BigDecimal> keys = distanceMultimap.keySet();
        int leftToFind = k;

        List<Entry> subset = new ArrayList<>();

        while (leftToFind > 0 && !keys.isEmpty()) {
            BigDecimal biggest = calculationService.getMinFromSet(keys);
            List<Entry> entries = distanceMultimap.get(biggest);
            for (Entry entry : entries) {
                if (leftToFind == 0) break;
                subset.add(entry);
                leftToFind--;
            }
            keys.remove(biggest);
        }

        return subset;
    }
}
