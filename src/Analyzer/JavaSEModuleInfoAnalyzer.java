package Analyzer;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The `JavaSEModuleInfoAnalyzer` class is an implementation of the `JavaSEAnalyzer` interface, designed for analyzing modules,
 * packages, and types within the Java SE (Standard Edition) runtime environment.
 * <p>
 * This class provides methods to find unique modules in the Java SE runtime, discover packages, identify packages per module,
 * and locate types (classes) within the modules.
 * <p>
 * It leverages the Java Platform Module System (JPMS) to access module and package information, and it collects class information
 * based on the exported packages of modules.
 */
public class JavaSEModuleInfoAnalyzer implements JavaSEAnalyzer{

    private final ModuleLayer bootLayer;
    private final Configuration bootConfig;
    private final List<Module> moduleList;
    private final Set<String> packageList;
    private final Map<Module, Set<String>> packagesPerModuleMap;
    private final Set<Class<?>> types;

    public JavaSEModuleInfoAnalyzer() {
        this.moduleList = new ArrayList<>();
        this.packageList = new HashSet<>();
        this.packagesPerModuleMap = new HashMap<>();
        this.types = new HashSet<>();
        this.bootLayer = ModuleLayer.boot();
        this.bootConfig = bootLayer.configuration();
    }

    /**
     * Finds and adds unique modules in the Java SE runtime to the `moduleList`.
     */
    @Override
    public void findUniqueModules() {
        ModuleLayer bootLayer = ModuleLayer.boot();
        bootLayer.modules().stream()
                .filter(m -> m.getName().startsWith("java."))
                .forEach(this.moduleList::add);
    }

    /**
     * Finds and adds packages starting with "java." or "javax." from the discovered modules to the `packageList`.
     */
    public void findPackages() {
        moduleList.forEach(m -> {
            Set<String> packages = m.getPackages()
                    .stream()
                    .filter(packageName -> packageName.startsWith("java.") || packageName.startsWith("javax.") )
                    .collect(Collectors.toSet());
            this.packageList.addAll(packages);
        });
    }

    public Set<String> getAllPackages() {
        Set<String> allPackages = new HashSet<>();
        this.packagesPerModuleMap.values().forEach(allPackages::addAll);
        return allPackages;
    }

    /**
     * Finds and maps packages starting with "java." or "javax." per module, populating the`packagesPerModuleMap`.
     */
    @Override
    public void findPackagesPerModule() {
        this.moduleList.forEach( m -> {
                    Set<String> packages = m.getPackages()
                            .stream()
                            .filter(packageName -> packageName.startsWith("java.") || packageName.startsWith("javax."))
                            .collect(Collectors.toSet());
                    this.packagesPerModuleMap.put(m, packages);
                });
    }

    /**
     * Finds and adds types (classes) from the modules to the `types` set.
     * Types are extracted from exported packages of modules.
     */
    @Override
    public void findTypes() {
        this.moduleList.forEach(m -> {
            Optional<ResolvedModule> resolved = this.bootConfig.findModule(m.getName());
            resolved.ifPresent(rm -> {
                ModuleReference ref = rm.reference();
                try (ModuleReader reader = ref.open()) {
                    reader.list().forEach(s -> {
                        try {
                            if (s.endsWith(".class") && !s.equals("module-info.class")) { // exclude non-class resources & the module-info class
                                String packageName = s.substring(0, s.lastIndexOf('/')).replace('/', '.');
                                if (m.isExported(packageName)) {
                                    String className = s.replace('/', '.').substring(0, s.length() - ".class".length());
                                    // Add the class to the existing map
                                    this.types.add(Class.forName(className));
                                }
                            }
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            ex.printStackTrace(System.out);
                        }
                    });
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace(System.out);
                }
            });
        });

    }

    @Override
    public Set<Class<?>> getAllTypes() {
        return this.types;
    }

    @Override
    public void printTotalResults() {
        System.out.println("Total modules: " + this.moduleList.size());
        if (this.packageList.isEmpty()) {
            System.out.println("Total packages: " + this.packagesPerModuleMap.values().stream().mapToInt(Set::size).sum());
        } else {
            System.out.println("Total packages: " + this.packageList.size());
        }
        System.out.println("Total types: " + this.types.size());
    }
}
