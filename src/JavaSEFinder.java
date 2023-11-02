import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaSEFinder implements JavaSEAnalyzer{

    private final List<ModuleReference> moduleList;
    private final List<String> packageList;
    private final Map<ModuleReference, List<String>> packagesPerModuleMap;
    private final Set<Class<?>> types;

    public JavaSEFinder() {
        this.moduleList = new ArrayList<>();
        this.packageList = new ArrayList<>();
        this.packagesPerModuleMap = new HashMap<>();
        this.types = new HashSet<>();
    }

    @Override
    public void findUniqueModules() {
        ModuleFinder finder = ModuleFinder.ofSystem();
        Set<ModuleReference> moduleReferences = finder.findAll();
        moduleReferences.stream()
                .filter(moduleReference ->
                        (moduleReference.descriptor().name().startsWith("java.") ||
                                moduleReference.descriptor().name().startsWith("javax.")) &&
                                !moduleReference.descriptor().name().contains("smartcardio"))
                .sorted(Comparator.comparing(moduleReference -> moduleReference.descriptor().name()))
                .forEach(this.moduleList::add);
    }

    public void findPackages() {
        this.moduleList
                .stream()
                .flatMap(module -> module.descriptor().packages().stream())
                .filter(pkg -> pkg.startsWith("java.") || pkg.startsWith("javax."))
                .sorted(Comparator.naturalOrder())
                .forEach(this.packageList::add);
    }

    public Set<String> getAllPackages() {
        Set<String> allPackages = new HashSet<>();
        this.packagesPerModuleMap.values().forEach(allPackages::addAll);
        return allPackages;
    }

    @Override
    public void findPackagesPerModule() {
        for (ModuleReference mdl : this.moduleList) {
            List<String> packageList = Stream.of(mdl)
                    .flatMap(module -> module.descriptor().packages().stream())
                    .filter(pkg -> pkg.startsWith("java.") || pkg.startsWith("javax."))
                    .sorted(Comparator.naturalOrder())
                    .collect(Collectors.toCollection(ArrayList::new));
            this.packagesPerModuleMap.put(mdl, packageList);
        }
    }

    @Override
    public void findTypes() {
        // Couldn't implement this method
    }

    @Override
    public Set<Class<?>> getAllTypes() {
        return this.types;
    }

    @Override
    public void printTotalResults() {
        System.out.println("Total modules: " + this.moduleList.size());
        if (this.packageList.isEmpty()) {
            System.out.println("Total packages: " + this.packagesPerModuleMap.values().stream().mapToInt(List::size).sum());
        } else {
            System.out.println("Total packages: " + this.packageList.size());
        }
        System.out.println("Total types: " + this.types.size());
    }
}
