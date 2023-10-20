import java.util.*;
import java.util.stream.Collectors;

public class JavaSEModulesFinder {

    private final List<Module> apiModuleList;
    private final Comparator<Module> moduleReferenceComparator;

    public JavaSEModulesFinder() {
//        this.apiModuleList = findUniqueAPIModules(ModuleLayer.boot().modules());
        this.apiModuleList = new ArrayList<>();
        this.moduleReferenceComparator = Comparator.comparing(Module::getName);
    }

    public List<Module> findUniqueAPIModules(Set<Module> allModules) {
        allModules
                .stream()
                .filter(module -> module.getName().startsWith("java.") || module.getName().startsWith("javax."))    //.map(Module::getName)
                .forEach(this.apiModuleList::add);    //.toList(); creates ImmutableCollection and sort do not work
        return this.apiModuleList;
    }

    public void sortModules() {
        Utils.sortList(this.apiModuleList, moduleReferenceComparator);
    }

    public List<Module> getApiModuleList() {
        return apiModuleList;
    }
}
