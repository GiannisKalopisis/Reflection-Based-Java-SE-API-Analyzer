package Polymorphism;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The `PolymorphismAnalyzer` interface defines a set of methods that allow implementing classes to perform polymorphism analysis.
 * Implementing classes are responsible for calculating polymorphic degrees and retrieving top-level received methods for analysis.
 * <p>
 * This interface serves as a contract for classes that provide polymorphism analysis functionality, enabling flexibility and customization.
 */
public interface PolymorphismAnalyzer {

    /**
     * Calculates and returns the set of classes for the inheritance chain to calculate polymorphic degrees.
     *
     * @return A map where the keys are classes, and the values are sets of associated polymorphic types.
     */
    Map<Class<?>, Set<Class<?>>> calculatePolymorphicDegrees();

    /**
     * Retrieves a map of top-level received methods for different classes.
     *
     * @return A map where the keys are classes, and the values are maps that store method names and corresponding lists of MethodInfo objects.
     */
    Map<Class<?>, Map<String, List<MethodInfo>>> getTopLvlReceivedMethods();

}
