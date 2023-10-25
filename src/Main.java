import java.util.*;


public class Main {

    public static void main(String[] args) {

//        int topN = Utils.topNParameterParser(args);

//        JavaSEFinder javaSEFinder = new JavaSEFinder();
//        javaSEFinder.findUniqueModules();
//        javaSEFinder.findPackagesPerModule();
//        javaSEFinder.findPackages();

        List<String> test = new ArrayList<>();
        test.add("java.lang.String");
        test.add("java.lang.Integer");
        test.add("java.lang.Double");
        test.add("java.lang.Float");
        Set<Class<?>> classSet = new HashSet<>();
        Map<Class<?>, Integer> polymorphicCounter = new HashMap<>();

        for (String className : test) {
            try {
                classSet.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found: " + className);
            }
        }

        for (Class<?> clazz : classSet) {
            polymorphicCounter.put(clazz, 0);
        }

        for (Class<?> clazz : classSet) {
            Class<?> superClass = clazz.getSuperclass();
            Class<?>[] interfaces = clazz.getInterfaces();
            System.out.println("\n" + clazz.getName() + " extends " + superClass.getName());
            for (Class<?> anInterface : interfaces) {
                System.out.println("\t" + clazz.getName() + " implements " + anInterface.getName());
            }
        }

        System.out.println();
        classSet.forEach(System.out::println);
        System.out.println();
        System.out.println();
        System.out.println();


        List<Class<?>> classesToAnalyze = new ArrayList<>();
        //            classesToAnalyze.add(Class.forName("java.lang.String"));
//            classesToAnalyze.add(Class.forName("java.lang.Double"));
//            classesToAnalyze.add(Class.forName("java.lang.Integer"));
//            classesToAnalyze.add(Class.forName("java.lang.Float"));
//            classesToAnalyze.add(Class.forName("java.text.DateFormat$Field"));
//            classesToAnalyze.add(Class.forName("java.text.DateFormatSymbols"));
//            classesToAnalyze.add(Class.forName("java.text.DecimalFormatSymbols"));
//            classesToAnalyze.add(Class.forName("java.text.NumberFormat$Field"));
//            classesToAnalyze.add(Class.forName("java.text.spi.DateFormatSymbolsProvider"));
//            classesToAnalyze.add(Class.forName("java.text.spi.DecimalFormatSymbolsProvider"));
//            classesToAnalyze.add(Class.forName("java.text.spi.NumberFormatProvider"));
//            classesToAnalyze.add(Class.forName("java.util.AbstractList$Itr"));
//        classesToAnalyze.add(TestClass1.class);
//        classesToAnalyze.add(TestClass2.class);
        classesToAnalyze.add(MyClass.class);
        classesToAnalyze.add(CombinedInterface.class);

        JavaSEPolymorphicTypeFinder polymorphicTypeFinder = new JavaSEPolymorphicTypeFinder(classesToAnalyze);
        Map<Class<?>, Set<Class<?>>> polymorphicDegrees = polymorphicTypeFinder.calculatePolymorphicDegrees();
        Map<Class<?>, Set<Class<?>>> sortedMap = Utils.sortByCollectionSizeDescending(polymorphicDegrees);

        System.out.println("Polymorphic degrees:");
        for (Map.Entry<Class<?>, Set<Class<?>>> entry : sortedMap.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue().size());
        }

//        System.out.println("\nReceived methods:");
//        Map<Class<?>, List<MethodInfo>> receivedMethods = polymorphicTypeFinder.getReceivedMethods();
//        for (Map.Entry<Class<?>, List<MethodInfo>> entry : receivedMethods.entrySet()) {
//            System.out.println(entry.getKey().getName() + ": ");
//            for (MethodInfo methodInfo : entry.getValue()) {
//                System.out.println("\t" + methodInfo.toString());
//            }
//        }

//        javaSEFinder.printModulePackageMap();
//
//        IOHelper.printModulesNumberResults(javaSEFinder.getModuleList());
    }
}