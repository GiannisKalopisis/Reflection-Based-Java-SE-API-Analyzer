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
        for (Module module : modules) {
            System.out.println(module.getName());
        }
    }
}
