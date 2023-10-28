import java.util.Map;
import java.util.Set;

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

    public static void printModulesToTerminal(Map<Class<?>, Set<Class<?>>> modules, int topN) {
        modules.entrySet()
                .stream()
                .limit(topN <= 0 ? modules.size() : Math.min(topN, modules.size())) // Math.min(topN, modules.size()) is used to avoid IndexOutOfBoundsException
                .forEach(module -> System.out.println(module.getKey().getName() + ": " + module.getValue().size()));
    }
}
