import java.util.List;

public class IOHelper {

    public static int topNParameterParser(String[] args) {
        int topN;
        try {
            topN = Integer.parseInt(args[0]);
            System.out.println("Top-N parameter: " + topN);
        } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException exception) {
            System.err.println(exception);
            System.out.println("Default top-N parameter: 5");
            topN = 5;
        }
        return topN;
    }

    public static void printModulesNumberResults(List<Module> modules) {
        System.out.println("Number of Modules: " + modules.size());
    }

    public static void printModulesToTerminal(List<Module> modules) {
        for (Module module : modules) {
            System.out.println(module.getName());
        }
    }
}
