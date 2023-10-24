import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IOHelper {

    public static void printEntryMessage() {
        System.out.println("""

                       __       ___   ____    ____  ___             ___      .______    __         ___      .__   __.      ___       __      ____    ____  ________   _______ .______       __   __   __ \s
                      |  |     /   \\  \\   \\  /   / /   \\           /   \\     |   _  \\  |  |       /   \\     |  \\ |  |     /   \\     |  |     \\   \\  /   / |       /  |   ____||   _  \\     |  | |  | |  |\s
                      |  |    /  ^  \\  \\   \\/   / /  ^  \\         /  ^  \\    |  |_)  | |  |      /  ^  \\    |   \\|  |    /  ^  \\    |  |      \\   \\/   /  `---/  /   |  |__   |  |_)  |    |  | |  | |  |\s
                .--.  |  |   /  /_\\  \\  \\      / /  /_\\  \\       /  /_\\  \\   |   ___/  |  |     /  /_\\  \\   |  . `  |   /  /_\\  \\   |  |       \\_    _/      /  /    |   __|  |      /     |  | |  | |  |\s
                |  `--'  |  /  _____  \\  \\    / /  _____  \\     /  _____  \\  |  |      |  |    /  _____  \\  |  |\\   |  /  _____  \\  |  `----.    |  |       /  /----.|  |____ |  |\\  \\----.|__| |__| |__|\s
                 \\______/  /__/     \\__\\  \\__/ /__/     \\__\\   /__/     \\__\\ | _|      |__|   /__/     \\__\\ |__| \\__| /__/     \\__\\ |_______|    |__|      /________||_______|| _| `._____|(__) (__) (__)\s
                                                                                                                                                                                                         \s
                """);
    }

    public static void printModulesNumberResults(List<?> modules) {
        System.out.println("Number of Modules: " + modules.size());
    }

    public static void printModulesToTerminal(List<Module> modules) {
        modules.forEach(module -> System.out.println(module.getName()));
    }

    public static ArrayList<String> readInputFile(String inputFilePath) {
        ArrayList<String> inputData = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String line = reader.readLine();
            while (line != null) {
                inputData.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't find file " + inputFilePath);
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("I/O error while trying to read from file " + inputFilePath);
            e.printStackTrace();
            System.exit(-1);
        }
        return inputData;
    }
}
