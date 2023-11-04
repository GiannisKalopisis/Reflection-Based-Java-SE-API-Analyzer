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

        int topN = Helper.Utils.topNParameterParser(args);

        /*
         * Analyze JavaSE (modules, packages, types)
         */

        long analyzeTime, polymorphicTime, overloadTime;

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
        IOHelper.printTopNPolymorphicTypes(classPolymorphicDegreeMap, topN);

        polymorphicTime = Utils.endTimeCounter();

        /*
         * Overload degree
         */

        Utils.startTimeCounter();

        JavaSEOverloadFinder overloadFinder = new JavaSEOverloadFinder(polymorphicTypeFinder.getTopLvlReceivedMethods(), classPolymorphicDegreeMap);
        overloadFinder.calculateOverloadDegree();
        IOHelper.printTopNOverloadedMethods(overloadFinder.getOverloadDegreeMapByMethodName(), topN);

        overloadTime = Utils.endTimeCounter();

        IOHelper.printTime(analyzeTime, polymorphicTime, overloadTime);
    }
}