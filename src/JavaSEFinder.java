import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaSEFinder {

    private final List<ModuleReference> moduleList;
    private final List<String> packageList;
    private final Map<ModuleReference, List<String>> modulePackageMap;
    private final Comparator<ModuleReference> moduleReferenceComparator;

    public JavaSEFinder() {
        this.moduleList = new ArrayList<>();
        this.packageList = new ArrayList<>();
        this.modulePackageMap = new HashMap<>();
        this.moduleReferenceComparator = Comparator.comparing(moduleReference -> moduleReference.descriptor().name());
    }

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

    public void findPackagesPerModule() {
        for (ModuleReference mdl : this.moduleList) {
            List<String> packageList = Stream.of(mdl)
                    .flatMap(module -> module.descriptor().packages().stream())
                    .filter(pkg -> pkg.startsWith("java.") || pkg.startsWith("javax.") || pkg.startsWith("org."))
                    .sorted(Comparator.naturalOrder())
                    .collect(Collectors.toCollection(ArrayList::new));
            this.modulePackageMap.put(mdl, packageList);
        }
    }

    public void printModulePackageMap() {
        modulePackageMap.forEach((moduleReference, packageList) -> {
            System.out.println("\nModule: " + moduleReference.descriptor().name());
            packageList.forEach(pkg -> System.out.println("\t" + pkg));
        });
    }

    public void findTypesInJavaSEAPIPackages() throws ClassNotFoundException {
        // TODO: find all types (class and interface) for every package and add them to a List<Class<?>>.
    }

    public void sortModules() {
        Utils.sortList(this.moduleList, moduleReferenceComparator);
    }

    public List<ModuleReference> getModuleList() {
        return moduleList;
    }

    public List<String> getPackageList() {
        return packageList;
    }

}
