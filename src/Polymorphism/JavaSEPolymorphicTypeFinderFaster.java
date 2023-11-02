package Polymorphism;
import Helper.*;

import java.util.*;
import java.util.stream.Collectors;

public class JavaSEPolymorphicTypeFinderFaster  extends PolymorphismAnalyzerHelper implements PolymorphismAnalyzer {

    private Set<Class<?>> topLvlClasses;

    public JavaSEPolymorphicTypeFinderFaster(Set<Class<?>> classesToAnalyze) {
        super(classesToAnalyze);
        this.topLvlClasses = new HashSet<>();
    }

    @Override
    public Map<Class<?>, Set<Class<?>>> calculatePolymorphicDegrees() {
        // keep set of visited classes if current iteration class is inside continue
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
