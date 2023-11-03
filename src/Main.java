import Analyzer.JavaSEAnalyzer;
import Analyzer.JavaSEModuleInfoAnalyzer;
import Helper.IOHelper;
import Helper.Utils;
import Overload.JavaSEOverloadFinder;
import Polymorphism.JavaSEPolymorphicTypeFinderFaster;
import Polymorphism.PolymorphismAnalyzer;

import java.util.*;


public class Main {

    public static void main(String[] args) {

//        int topN = Helper.Utils.topNParameterParser(args);

        /*
         * Analyze JavaSE (modules, packages, types)
         */

        long analyzeTime = 0, polymorphicTime = 0, overloadTime = 0;

        Utils.startTimeCounter();
        JavaSEAnalyzer javaSEAnalyzer = new JavaSEModuleInfoAnalyzer();

        javaSEAnalyzer.findUniqueModules();
        javaSEAnalyzer.findPackagesPerModule();
        javaSEAnalyzer.findTypes();
        javaSEAnalyzer.printTotalResults();

        analyzeTime = Utils.endTimeCounter();

        /*
         * Polymorphic degree
         */
        Utils.startTimeCounter();

//        Polymorphism.PolymorphismAnalyzer polymorphicTypeFinder = new Polymorphism.JavaSEPolymorphicTypeFinder(javaSEAnalyzer.getAllTypes());
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
        IOHelper.printTopNOverloadedMethods(100, overloadFinder.getOverloadDegreeMapByMethodName());

        overloadTime = Utils.endTimeCounter();

        IOHelper.printTime(analyzeTime, polymorphicTime, overloadTime);
    }
}