package Polymorphism;
import Helper.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * The `JavaSEPolymorphicTypeFinderFaster` class is responsible for analyzing polymorphism and type hierarchies within a Java SE context.
 * It extends the `PolymorphismAnalyzerHelper` and implements the `PolymorphismAnalyzer` interface, providing efficient
 * polymorphic degree calculations and maintaining a map that associates top-level classes with their received methods.
 * This class performs a recursive analysis to determine polymorphic degrees of classes and their type hierarchies.
 */
public class JavaSEPolymorphicTypeFinderFaster  extends PolymorphismAnalyzerHelper implements PolymorphismAnalyzer {

    /**
     * A set that stores top-level classes to be analyzed.
     */
    private Set<Class<?>> topLvlClasses;

    public JavaSEPolymorphicTypeFinderFaster(Set<Class<?>> classesToAnalyze) {
        super(classesToAnalyze);
        this.topLvlClasses = new HashSet<>();
    }

    /**
     * Calculates the polymorphic degrees of classes and their type hierarchies.
     * It performs a recursive analysis to determine the polymorphic degrees and organizes the type hierarchies. It also
     * calls the `addMethodsToTopLvlHierarchyClasses` method to add the methods to the top level hierarchy classes.
     * Visited classes already calculated their polymorphic degrees and there is no need to revisit them.
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
            visitedClasses.addAll(calculatePolymorphicDegreesRecursively(clazz));
        }
        this.topLvlClasses = this.topLvlReceivedMethods.keySet();
        this.polymorphicDegrees = Utils.sortByCollectionSizeAscending(this.polymorphicDegrees);
        addMethodsToTopLvlHierarchyClasses();
        return this.polymorphicDegrees;
    }

    /**
     * Recursively calculates the polymorphic degrees and type hierarchies of a class. Keep also the top-level classes.
     *
     * @param clazz The class to calculate the polymorphic degree for.
     * @return A set of classes representing the type hierarchy of the input class.
     */
    private Set<Class<?>> calculatePolymorphicDegreesRecursively(Class<?> clazz) {
        Set<Class<?>> inheritanceClassGraph = new LinkedHashSet<>();
        if (clazz == null || polymorphicDegrees.containsKey(clazz)) {
            return clazz == null ? inheritanceClassGraph : polymorphicDegrees.get(clazz);
        }

        // Add all the superclasses and interfaces to inheritanceClassGraph
        inheritanceClassGraph.addAll(calculatePolymorphicDegreesRecursively(clazz.getSuperclass()));
        for (Class<?> iface : clazz.getInterfaces()) {
            inheritanceClassGraph.addAll(calculatePolymorphicDegreesRecursively(iface));
        }

        if (inheritanceClassGraph.isEmpty()) {  // Reached top of inheritance level -> add empty methods list
            this.topLvlReceivedMethods.computeIfAbsent(clazz, k -> new HashMap<>());
        }

        inheritanceClassGraph.add(clazz);

        this.polymorphicDegrees.put(clazz, inheritanceClassGraph);

        return inheritanceClassGraph;
    }

    /**
     * Adds methods to top-level hierarchy classes based on common classes and inheritance hierarchy.
     * - Get the map sorted by the polymorphic degree in ascending order.
     * - Firstly add all the methods from the top level classes that have polymorphic degree 1.
     * - Secondly add all the methods from the top level classes that have polymorphic degree 2.
     * - Thirdly add all the methods from the top level classes that have polymorphic degree 3.
     * - And so on...
     * <p>
     * The logic is that all the classes that have polymorphic degree 2, must include all the classes
     * that have polymorphic degree 1, the classes that have polymorphic degree 3, must include all the classes
     * that have polymorphic degree 2... and so on.
     */
    private void addMethodsToTopLvlHierarchyClasses() {

        for (Map.Entry<Class<?>, Set<Class<?>>> entry : this.polymorphicDegrees.entrySet()) {

            if (entry.getValue().size() > 1) {
                Set<Class<?>> commonClasses = this.topLvlClasses.stream()
                        .filter(entry.getValue()::contains)
                        .collect(HashSet::new, HashSet::add, HashSet::addAll);
                for (Class<?> inheritanceClass : commonClasses) {
                    addTopLvlReceivedMethods(inheritanceClass, extractMethodsSignature(entry.getKey().getDeclaredMethods()));
                }
            } else {
                this.topLvlReceivedMethods.put(entry.getKey(), Utils.groupByMethodName(extractMethodsSignature(entry.getKey().getDeclaredMethods())));
            }
        }
    }

    /**
     * This method sort the polymorphicDegrees map by the size of the value (Set<Class<?>>) in ascending order.
     */
    public void sortPolymorphicDegreesInPlace() {
        this.polymorphicDegrees = polymorphicDegrees.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().size()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /** This method check if all the classes from polymorphicDegrees that have polymorphic degree 1 are the top level classes,
     *  or if there are some classes that are not top level classes and have polymorphic degree 1.
     */
    private void checkPolymorphicDegreesOneAndTopLvlClasses() {
        this.polymorphicDegrees.forEach((clazz, inheritanceClassGraph) -> {
            if (inheritanceClassGraph.size() == 1) {
                if (!this.topLvlClasses.contains(clazz)) {
                    System.out.println("Class " + clazz.getName() + " has polymorphic degree 1 but is not a top level class");
                }
            }
        });
    }

    @Override
    public Map<Class<?>, Map<String, List<MethodInfo>>> getTopLvlReceivedMethods() {
        return this.topLvlReceivedMethods;
    }
}
