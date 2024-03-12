package pl.edu.s28201.nai_01.controller;

import com.google.common.collect.ArrayListMultimap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.edu.s28201.nai_01.model.Iris;
import pl.edu.s28201.nai_01.model.IrisEntry;
import pl.edu.s28201.nai_01.repository.IrisRepository;
import pl.edu.s28201.nai_01.service.CalculationService;
import pl.edu.s28201.nai_01.service.FileService;
import pl.edu.s28201.nai_01.service.IrisService;

import java.io.BufferedReader;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Controller
public class ProgramController {

    private final FileService fileService;
    private final IrisRepository irisRepository;
    private final CalculationService calculationService;
    private final BufferedReader console;
    private final IrisService irisService;
    private int k;

    @Autowired
    public ProgramController(FileService fileService, IrisRepository irisRepository, CalculationService calculationService, BufferedReader console, IrisService irisService) {
        this.fileService = fileService;
        this.irisRepository = irisRepository;
        this.calculationService = calculationService;
        this.console = console;
        this.irisService = irisService;
    }

    public void startProgram() {
        File trainFile = requestTestFile();

        irisRepository.addAll(irisService.parseToIrises(fileService.readTrainFile(trainFile)));

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

        while (!calculationService.isInteger(strK) || !calculationService.isValidK(Integer.parseInt(strK))) {
            System.out.println("K should be integer and bigger than 0.");
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
                if (splitCommands.length != (irisService.getParamsNum() + 1)) yield false;
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
        if (!calculationService.isValidK(Integer.parseInt(strK))) {
            System.out.println("K value cannot be less than 1.");
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
        Iris computedSet = findResultSet(new IrisEntry(decimals), irisRepository.findAll());
        String preparedAttributes = calculationService.getAttributesStringFromAttributes(decimals);
        System.out.println("Iris with attributes: {" + preparedAttributes + "} belongs to " + computedSet.toString());
        return true;
    }

    private boolean executeTestF(String testPath) {
        try {
            File testFile = new File(testPath);
            if (!testFile.exists()) {
                System.out.println("File with given path does not exist.");
                return false;
            }

            List<IrisEntry> testIrises = irisService.parseToIrises(fileService.readTrainFile(testFile));
            int matchingCount = 0;
            for (IrisEntry iris : testIrises) {
                Iris computedSet = findResultSet(iris, testIrises);
                String preparedAttributes = calculationService.getAttributesStringFromAttributes(iris.getAttributes());
                System.out.println("Iris with attributes: {" + preparedAttributes + "} computed to be {"
                        + computedSet.toString() + "}, actually belongs to {" + iris.getFlowerType().toString() + "}");
                if (iris.getFlowerType().equals(computedSet)) matchingCount++;
            }

            BigDecimal accuracy = BigDecimal.valueOf((double) matchingCount).divide(new BigDecimal(testIrises.size()), MathContext.DECIMAL128)
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

    private Iris findResultSet(IrisEntry irisEntry, List<IrisEntry> trainData) {
        List<IrisEntry> irises = new ArrayList<>(trainData);

        ArrayListMultimap<BigDecimal, IrisEntry> distanceMultimap = ArrayListMultimap.create();

        irises.forEach(iris -> {
            BigDecimal distance = calculationService.distanceOf(iris, irisEntry);
            distanceMultimap.put(distance, iris);
        });

        List<IrisEntry> finalSubset = getIrisSubset(distanceMultimap);

        return getMostCommonIrisFromSubset(finalSubset);
    }

    private Iris getMostCommonIrisFromSubset(List<IrisEntry> finalSubset) {
        long setosaCount = finalSubset.stream().filter(iris -> iris.getFlowerType().equals(Iris.SETOSA)).count();
        long virginicaCount = finalSubset.stream().filter(iris -> iris.getFlowerType().equals(Iris.VIRGINICA)).count();
        long versicolorCount = finalSubset.stream().filter(iris -> iris.getFlowerType().equals(Iris.VERSICOLOR)).count();

        if (virginicaCount == setosaCount && setosaCount == versicolorCount) return Iris.VERSICOLOR;
        if (Math.max(virginicaCount, setosaCount) < versicolorCount) return Iris.VERSICOLOR;
        if (Math.max(versicolorCount, virginicaCount) < setosaCount) return Iris.SETOSA;
        if (Math.max(versicolorCount, setosaCount) < virginicaCount) return Iris.VIRGINICA;

        return null;
    }

    private List<IrisEntry> getIrisSubset(ArrayListMultimap<BigDecimal, IrisEntry> distanceMultimap) {
        Set<BigDecimal> keys = distanceMultimap.keySet();
        int leftToFind = k;

        List<IrisEntry> subset = new ArrayList<>();

        while (leftToFind > 0) {
            BigDecimal biggest = calculationService.getMinFromSet(keys);
            List<IrisEntry> irises = distanceMultimap.get(biggest);
            for (IrisEntry iris : irises) {
                if (leftToFind == 0) break;
                subset.add(iris);
                leftToFind--;
            }
            keys.remove(biggest);
        }

        return subset;
    }
}
