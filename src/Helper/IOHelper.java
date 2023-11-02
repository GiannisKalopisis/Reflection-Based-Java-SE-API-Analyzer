package Helper;

import Polymorphism.MethodInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Helper.IOHelper - A utility class for input and output operations.
 * <p>
 * This class provides methods for printing messages, displaying top N polymorphic types and overloaded methods
 * to the terminal, and printing top N methods from a map of Polymorphism.MethodInfo objects.
 */
public class IOHelper {

    /**
     * Prints an entry message to the terminal.
     */
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

    /**
     * Prints the top N polymorphic types to the terminal.
     *
     * @param modules A map of classes and their associated polymorphic types.
     * @param topN    The number of top polymorphic types to print. A value of 0 prints all types.
     */
    public static void printTopNPolymorphicTypesToTerminal(Map<Class<?>, Set<Class<?>>> modules, int topN) {
        System.out.println("\nTop " + topN + " polymorphic types: ");
        Utils.sortByCollectionSizeDescending(modules)
                .entrySet()
                .stream()
                .limit(topN <= 0 ? modules.size() : Math.min(topN, modules.size())) // Math.min(topN, modules.size()) is used to avoid IndexOutOfBoundsException
                .forEach(module -> System.out.println(module.getKey().getName() + ": " + module.getValue().size()));
    }

    /**
     * Prints the top N methods with the highest overload degree from a one dimension map.
     *
     * @param methods A map of methods and their associated overload counts.
     * @param topN    The number of top overloaded methods to print. A value of 0 prints all methods.
     */
    public static void printTopNOverloadedMethodsToTerminal(Map<MethodInfo, Integer> methods, int topN) {
        System.out.println("\nTop " + topN + " overloaded methods: ");
        methods.entrySet()
                .stream()
                .sorted(Map.Entry.<MethodInfo, Integer>comparingByValue().reversed()
                        .thenComparing(entry -> entry.getKey().getMethodName()))
                .limit(topN <= 0 ? methods.size() : Math.min(topN, methods.size())) // Math.min(topN, methods.size()) is used to avoid IndexOutOfBoundsException
                .forEach(method -> System.out.println(method.getKey().getMethodName() + " (" + method.getKey().getClassInfo().getName() + ") : " + method.getValue()));
    }

    /**
     * Prints the top N methods with the highest overload degree from a two dimensions map.
     *
     * @param topN                          The number of top overloaded methods to print. A value of 0 prints all methods.
     * @param overloadDegreeMapByMethodName A map of method names and their associated Polymorphism.MethodInfo objects and overload degrees.
     */
    public static void printTopNMethods(int topN, Map<String, Map<MethodInfo, Integer>> overloadDegreeMapByMethodName) {
        // Flatten the map into a list of Polymorphism.MethodInfo-Integer pairs
        List<Map.Entry<MethodInfo, Integer>> methodInfoList = overloadDegreeMapByMethodName
                .values()
                .stream()
                .flatMap(methodInfoMap -> methodInfoMap.entrySet().stream())
                .sorted(Map.Entry.<MethodInfo, Integer>comparingByValue().reversed()
                        .thenComparing(entry -> entry.getKey().getMethodName()))
                .toList();

        methodInfoList.stream()
                .limit(topN <= 0 ? methodInfoList.size() : topN)
                .forEach(entry -> {
                    MethodInfo method = entry.getKey();
                    int value = entry.getValue();
                    System.out.println(method.getMethodName() + " (" + method.getClassInfo().getName() + ") : " + value);
                });
    }


    public static void printTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");
        String formattedTime = sdf.format(new Date(time));
        System.out.println("Elapsed Time: " + formattedTime);
    }

    private static String formatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");
        return sdf.format(new Date(time));
    }

    public static void printTime(long analyzeTime, long polymorphicTime, long overloadTime) {
        System.out.println("\nAnalyze time: " + formatTime(analyzeTime) + " ms");
        System.out.println("Counting Polymorphism time: " + formatTime(polymorphicTime) + " ms");
        System.out.println("Counting Overload time: " + formatTime(overloadTime) + " ms");
        System.out.println("Total time: " + formatTime(analyzeTime + polymorphicTime + overloadTime) + " ms");
    }
}
