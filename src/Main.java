import java.util.*;


public class Main {

    public static void main(String[] args) {

//        int topN = Utils.topNParameterParser(args);

        /*
         * Analyze JavaSE (modules, packages, types)
         */

        long totalTime = 0;

        Utils.startTimeCounter();
        JavaSEAnalyzer javaSEAnalyzer = new JavaSEFinderWithoutLibrary();

        javaSEAnalyzer.findUniqueModules();
        javaSEAnalyzer.findPackagesPerModule();
        javaSEAnalyzer.findTypes();
        javaSEAnalyzer.printTotalResults();

        totalTime += Utils.endTimeCounter();

        /*
         * Polymorphic degree
         */
        System.out.println("\n");
        Utils.startTimeCounter();

//        PolymorphismAnalyzer polymorphicTypeFinder = new JavaSEPolymorphicTypeFinder(javaSEAnalyzer.getAllTypes());
        PolymorphismAnalyzer polymorphicTypeFinder = new JavaSEPolymorphicTypeFinderFaster(javaSEAnalyzer.getAllTypes());
        Map<Class<?>, Set<Class<?>>> classPolymorphicDegreeMap = polymorphicTypeFinder.calculatePolymorphicDegrees();
        IOHelper.printTopNPolymorphicTypesToTerminal(classPolymorphicDegreeMap, 100);

        totalTime += Utils.endTimeCounter();

        /*
         * Overload degree
         */

        Utils.startTimeCounter();

        JavaSEOverloadFinder overloadFinder = new JavaSEOverloadFinder(polymorphicTypeFinder.getTopLvlReceivedMethods(), classPolymorphicDegreeMap);
        overloadFinder.calculateOverloadDegree();
        IOHelper.printTopNMethods(100, overloadFinder.getOverloadDegreeMapByMethodName());

        totalTime += Utils.endTimeCounter();
        Utils.printTime(totalTime);
    }
}