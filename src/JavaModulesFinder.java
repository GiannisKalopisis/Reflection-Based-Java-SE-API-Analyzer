import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.*;
import java.util.stream.Collectors;

public class JavaModulesFinder {

    private final Set<Module> allModule;
    private final List<Module> apiModuleList;
    private final Comparator<Module> moduleReferenceComparator;

    public JavaModulesFinder() {
        this.allModule = ModuleLayer.boot().modules();
        this.apiModuleList = findUniqueAPIModules();
        this.moduleReferenceComparator = Comparator.comparing(Module::getName);
    }

    private List<Module> findUniqueAPIModules() {
        return allModule
                .stream()
                .filter(module -> module.getName().startsWith("java.") || module.getName().startsWith("javax."))    //.map(Module::getName)
                .collect(Collectors.toCollection(ArrayList::new));    //.toList(); creates ImmutableCollection and sort do not work
    }

    public void sortModules() {
        Utils.sortList(this.apiModuleList, moduleReferenceComparator);
    }

    public List<Module> getApiModuleList() {
        return apiModuleList;
    }
}
