import java.util.*;


public class Main {

    public static void main(String[] args) {

//        int topN = Utils.topNParameterParser(args);

        /*
         * Analyze JavaSE (modules, packages, types)
         */

        long analyzeTime = 0, polymorphicTime = 0, overloadTime = 0;

        Utils.startTimeCounter();
        JavaSEAnalyzer javaSEAnalyzer = new JavaSEFinderWithoutLibrary();

        javaSEAnalyzer.findUniqueModules();
        javaSEAnalyzer.findPackagesPerModule();
        javaSEAnalyzer.findTypes();
        javaSEAnalyzer.printTotalResults();

        analyzeTime = Utils.endTimeCounter();

        /*
         * Polymorphic degree
         */
        System.out.println("\n");
        Utils.startTimeCounter();

//        PolymorphismAnalyzer polymorphicTypeFinder = new JavaSEPolymorphicTypeFinder(javaSEAnalyzer.getAllTypes());
        PolymorphismAnalyzer polymorphicTypeFinder = new JavaSEPolymorphicTypeFinderFaster(javaSEAnalyzer.getAllTypes());
        Map<Class<?>, Set<Class<?>>> classPolymorphicDegreeMap = polymorphicTypeFinder.calculatePolymorphicDegrees();
        IOHelper.printTopNPolymorphicTypesToTerminal(classPolymorphicDegreeMap, 100);

        polymorphicTime = Utils.endTimeCounter();

        /*
         * Overload degree
         */

        Utils.startTimeCounter();

        JavaSEOverloadFinder overloadFinder = new JavaSEOverloadFinder(polymorphicTypeFinder.getTopLvlReceivedMethods(), classPolymorphicDegreeMap);
        overloadFinder.calculateOverloadDegree();
        IOHelper.printTopNMethods(100, overloadFinder.getOverloadDegreeMapByMethodName());

        overloadTime = Utils.endTimeCounter();
        
        IOHelper.printTime(analyzeTime, polymorphicTime, overloadTime);
    }
}