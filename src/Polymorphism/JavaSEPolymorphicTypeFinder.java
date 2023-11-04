package Polymorphism;

import java.lang.reflect.Method;
import java.util.*;

/**
 * The `JavaSEPolymorphicTypeFinder` class is responsible for analyzing polymorphism and type hierarchies within a Java SE context.
 * It extends the `PolymorphismAnalyzerHelper` and implements the `PolymorphismAnalyzer` interface, providing an analysis
 * of polymorphic degrees for classes and maintaining a map that associates classes with their received methods.
 * <p>
 * This class recursively explores class hierarchies to determine polymorphic degrees and type relationships between classes.
 */
public class JavaSEPolymorphicTypeFinder extends PolymorphismAnalyzerHelper implements PolymorphismAnalyzer {

    public JavaSEPolymorphicTypeFinder(Set<Class<?>> classesToAnalyze) {
        super(classesToAnalyze);
    }

    /**
     * Calculates the polymorphic degrees of classes and their type hierarchies.
     * It explores the class hierarchy for each class in the set, avoiding revisiting classes
     * and accumulating polymorphic degrees along the way. Visited classes already calculated
     * their polymorphic degrees and there is no need to revisit them.
     *
     * @return A map associating classes with their polymorphic degrees and type hierarchies.
     */
    @Override
    public Map<Class<?>, Set<Class<?>>> calculatePolymorphicDegrees() {

        Set<Class<?>> visitedClasses = new HashSet<>();
        for (Class<?> clazz : this.classesToAnalyze) {
            if (visitedClasses.contains(clazz)) {
                continue;
            }
            visitedClasses.addAll(calculatePolymorphicDegreesRecursively(clazz, new ArrayList<>()));
        }
        return this.polymorphicDegrees;
    }

    /**
     * Recursively calculates the polymorphic degrees and type hierarchies of a class.
     * It traverses the inheritance hierarchy, accumulating methods to superclasses and interfaces.
     * It needs to traverse the hierarchy to the top level to pass methods all the way.
     *
     * @param clazz         The class to calculate the polymorphic degree for.
     * @param subClassMethods A list of methods from subclasses to include in the analysis.
     * @return A set of classes representing the type hierarchy of the input class.
     */
    private Set<Class<?>> calculatePolymorphicDegreesRecursively(Class<?> clazz, List<MethodInfo> subClassMethods) {
        Set<Class<?>> inheritanceClassGraph = new LinkedHashSet<>();

        if (clazz == null ) {    // Need to reach top of inheritance level to pass methods all the way
            return inheritanceClassGraph;
        }


        Method[] currentClassMethods = clazz.getDeclaredMethods();
        List<MethodInfo> passingMethods = concatMethodsAndExcludeOverride(subClassMethods, extractMethodsSignature(currentClassMethods));

        // Add all the superclasses and interfaces to inheritanceClassGraph
        inheritanceClassGraph.addAll(calculatePolymorphicDegreesRecursively(clazz.getSuperclass(), passingMethods));
        for (Class<?> iface : clazz.getInterfaces()) {
            inheritanceClassGraph.addAll(calculatePolymorphicDegreesRecursively(iface, passingMethods));
        }

        if (inheritanceClassGraph.isEmpty()) {  // Reached top of inheritance level -> add methods
            addTopLvlReceivedMethods(clazz, passingMethods);
        }

        // Add current class to inheritanceClassGraph
        inheritanceClassGraph.add(clazz);

        this.polymorphicDegrees.put(clazz, inheritanceClassGraph);

        return inheritanceClassGraph;
    }

    @Override
    public Map<Class<?>, Map<String, List<MethodInfo>>> getTopLvlReceivedMethods() {
        return topLvlReceivedMethods;
    }
}
