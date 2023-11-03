package Analyzer;

import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The `JavaSEPartialAnalyzer` class is an implementation of the `JavaSEAnalyzer` interface, designed to partially analyze
 * modules, packages, and types within the Java SE (Standard Edition) runtime environment.
 * <p>
 * This class focuses on discovering and extracting module information, package names, and the association between
 * modules and their exported packages.
 * <p>
 * While it can identify modules and packages, it does not attempt to locate and analyze types (classes) within the modules.
 */
public class JavaSEPartialAnalyzer implements JavaSEAnalyzer{

    private final List<ModuleReference> moduleList;
    private final List<String> packageList;
    private final Map<ModuleReference, List<String>> packagesPerModuleMap;
    private final Set<Class<?>> types;

    public JavaSEPartialAnalyzer() {
        this.moduleList = new ArrayList<>();
        this.packageList = new ArrayList<>();
        this.packagesPerModuleMap = new HashMap<>();
        this.types = new HashSet<>();
    }

    /**
     * Finds and adds unique modules from the Java SE runtime to the `moduleList`.
     */
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

    /**
     * Finds and adds package names starting with "java." or "javax." from the discovered modules to the `packageList`.
     */
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

    /**
     * Finds and maps packages starting with "java." or "javax." per module, populating the `packagesPerModuleMap`.
     */
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

    /**
     * The `findTypes` method is not implemented in this class (placeholder).
     */
    @Override
    public void findTypes() {
        // Not implemented method
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
