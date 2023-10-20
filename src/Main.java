import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.lang.reflect.Type;
import java.util.*;

import java.lang.Module;
import java.util.List;
import java.util.stream.Collectors; // Import the Collectors class

public class Main {
    public static void main(String[] args) {
        IOHelper.printEntryMessage();

        int topN = Utils.topNParameterParser(args);

        JavaSEModulesFinder javaModulesFinder = new JavaSEModulesFinder();
        List<Module> modulesList = javaModulesFinder.findUniqueAPIModules(ModuleLayer.boot().modules());
        javaModulesFinder.sortModules();

        ModuleFinder finder = ModuleFinder.ofSystem();
        Set<ModuleReference> moduleReferences = finder.findAll();
        List<ModuleReference> mdlRef = moduleReferences.stream()
                .filter(moduleReference ->
                        (moduleReference.descriptor().name().startsWith("java.") ||
                                moduleReference.descriptor().name().startsWith("javax.")) &&
                                !moduleReference.descriptor().name().contains("smartcardio"))
                .sorted(Comparator.comparing(moduleReference -> moduleReference.descriptor().name()))
                .toList();

//        Utils.sortList(mdlRef, Comparator.comparing(m -> m.descriptor().name()));

        List<String> frontierPackages = mdlRef
                .stream()
                .flatMap(module -> module.descriptor().packages().stream())
                .filter(pkg -> pkg.startsWith("java.") || pkg.startsWith("javax."))
                .collect(Collectors.toCollection(ArrayList::new));

//        Utils.sortList(frontierPackages, Comparator.naturalOrder());
        List<Type> testGenericSuperclass = new ArrayList<>();
        for (ModuleReference value : mdlRef) {
            List<ModuleReference> test = new ArrayList<>(Collections.singleton(value));
            List<String> tempPkgList = test.stream()
                    .flatMap(module -> module.descriptor().packages().stream())
                    .filter(pkg -> pkg.startsWith("java.") || pkg.startsWith("javax.") || pkg.startsWith("org."))
                    .collect(Collectors.toCollection(ArrayList::new));
            Utils.sortList(tempPkgList, Comparator.naturalOrder());
            System.out.println("Module \"" + value.descriptor().name() + "\"");
            for (String s : tempPkgList) {
                System.out.println("\t" + s);
//                Package pkg = Package.getPackage(s);
//                Class<?> cls = pkg.getClass();
//                Collections.addAll(testGenericSuperclass, cls.getGenericInterfaces());
            }
            System.out.println("--------------------------------------\n");
        }

//            Package p = Package.getPackage(packageName);

        System.out.println("\npackages: " + frontierPackages.size());

//        for (Type type: testGenericSuperclass) {
//            System.out.println(type.getTypeName());
//        }


//        IOHelper.printModulesToTerminal(javaModulesFinder.getApiModuleList());
        IOHelper.printModulesNumberResults(mdlRef);
    }
}