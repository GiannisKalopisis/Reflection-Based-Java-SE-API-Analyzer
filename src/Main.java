import java.util.*;

import java.lang.Module;
import java.util.List;
import java.util.stream.Collectors; // Import the Collectors class

public class Main {
    public static void main(String[] args) {
        System.out.println("\n" +
                "       __       ___   ____    ____  ___             ___      .______    __         ___      .__   __.      ___       __      ____    ____  ________   _______ .______       __   __   __  \n" +
                "      |  |     /   \\  \\   \\  /   / /   \\           /   \\     |   _  \\  |  |       /   \\     |  \\ |  |     /   \\     |  |     \\   \\  /   / |       /  |   ____||   _  \\     |  | |  | |  | \n" +
                "      |  |    /  ^  \\  \\   \\/   / /  ^  \\         /  ^  \\    |  |_)  | |  |      /  ^  \\    |   \\|  |    /  ^  \\    |  |      \\   \\/   /  `---/  /   |  |__   |  |_)  |    |  | |  | |  | \n" +
                ".--.  |  |   /  /_\\  \\  \\      / /  /_\\  \\       /  /_\\  \\   |   ___/  |  |     /  /_\\  \\   |  . `  |   /  /_\\  \\   |  |       \\_    _/      /  /    |   __|  |      /     |  | |  | |  | \n" +
                "|  `--'  |  /  _____  \\  \\    / /  _____  \\     /  _____  \\  |  |      |  |    /  _____  \\  |  |\\   |  /  _____  \\  |  `----.    |  |       /  /----.|  |____ |  |\\  \\----.|__| |__| |__| \n" +
                " \\______/  /__/     \\__\\  \\__/ /__/     \\__\\   /__/     \\__\\ | _|      |__|   /__/     \\__\\ |__| \\__| /__/     \\__\\ |_______|    |__|      /________||_______|| _| `._____|(__) (__) (__) \n" +
                "                                                                                                                                                                                          \n");

        int topN = IOHelper.topNParameterParser(args);

        JavaModulesFinder javaModulesFinder = new JavaModulesFinder();
        javaModulesFinder.sortModules();

        List<Module> test = new ArrayList<>(Collections.singleton(javaModulesFinder.getApiModuleList().get(0)));

        List<String> frontierPackages = javaModulesFinder.getApiModuleList()
                .stream()
                .flatMap(module -> module.getPackages().stream())
                .filter(pkg -> pkg.startsWith("java.") || pkg.startsWith("javax."))
                .collect(Collectors.toCollection(ArrayList::new));

        Utils.sortList(frontierPackages, Comparator.naturalOrder());

//        for (String pkg : frontierPackages) {
//            Package p = Package.getPackage(pkg);
//            System.out.println(p.getName());
//        }

//            Package p = Package.getPackage(packageName);

        System.out.println("\npackages: " + frontierPackages.size());


//        IOHelper.printModulesToTerminal(javaModulesFinder.getApiModuleList());
        IOHelper.printModulesNumberResults(javaModulesFinder.getApiModuleList());
    }
}