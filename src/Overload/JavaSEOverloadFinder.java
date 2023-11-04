package Overload;

import Polymorphism.MethodInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The `JavaSEOverloadFinder` class is responsible for analyzing method overloading within a Java SE context.
 * It implements the `OverloadAnalyzer` interface and provides the functionality to calculate the overload degree
 * of methods and maintain a map that associates method names with their respective overload information.
 * <p>
 * This class considers class hierarchies and method definitions to calculate the overload degree, taking into account
 * inheritance and method redefinitions.
 */
public class JavaSEOverloadFinder implements OverloadAnalyzer {

    /**
     * A map that associates top-level classes with a map that groups method names and their respective method information lists.
     * This map is used to organize (by name) and analyze the methods for calculating overload degrees.
     */
    private final Map<Class<?>, Map<String, List<MethodInfo>>> topLvlReceivedMethods;

    /**
     * A map that associates method names with a map containing method information and their associated overload degrees.
     * This map stores the final overload degree information for each method name.
     */
    private final Map <String, Map<MethodInfo, Integer>> overloadDegreeMapByMethodName;

    /**
     * A map that associates classes with a set of classes that represent the polymorphic hierarchy of the associated class.
     * It is used to determine whether two classes have a common hierarchy type in their inheritance chain.
     */
    private final Map<Class<?>, Set<Class<?>>> polymorphicDegrees;

    public JavaSEOverloadFinder(Map<Class<?>, Map<String, List<MethodInfo>>> receivedMethods, Map<Class<?>, Set<Class<?>>> polymorphicDegrees) {
        this.polymorphicDegrees = polymorphicDegrees;
        this.overloadDegreeMapByMethodName = new HashMap<>();
        this.topLvlReceivedMethods = receivedMethods;
    }

    /**
     * Calculates the overload degree of methods by analyzing method hierarchies and definitions.
     * For each method, it calculates the overload degree based on its class's hierarchy.
     * It takes into account method inheritance and redefinitions.
     * <p>
     * The overload degree is calculated as follows:
     *      - For each top-level class, iterate through its grouped methods (grouped by name).
     *          - For each method name, iterate through its method list.
     *              - For each method in the method list, check if it is first defined in its hierarchy.
     *              - If it is first defined, make the overload degree of it 1.
     *              - Then calculate the overload degree of the rest methods.
     *              - Summarize methods of the same class.
     *              - Merge overload degree map and the existing map (from other top-level classes).
     */
    @Override
    public void calculateOverloadDegree() {
        this.topLvlReceivedMethods.forEach((topLvlClass, groupedMethods) -> {
            groupedMethods.forEach((methodName, methodList) -> {    // i.e (bar, [{bar, [], class1}, {bar, [int, String], class2}]) ->
                Map<MethodInfo, Boolean> methodTopHierarchyDefined = findTopHierarchyMethodDeclarations(methodList);
                Map<MethodInfo, Integer> overloadDegreeCounter = methodTopHierarchyDefined.entrySet()
                        .stream()
                        .filter(Map.Entry::getValue)
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> 1, (existingValue, newValue) -> existingValue, HashMap::new));
                calculateOverloadCounter(methodTopHierarchyDefined, overloadDegreeCounter);
                sumUpSameClassMethods(overloadDegreeCounter);
                mergeMaps(overloadDegreeCounter);
            });
        });
    }

    /**
     * Merges the calculated overload degrees of methods with the same method name, considering their classes and hierarchy.
     * <p>
     * Merge the maps as follow:
     *      - Iterate through all entries of the current map for the SAME METHOD NAME ONLY.
     *          - If the global map doesn't have the same method name, add all the current map to the global map.
     *          - Iterate through all entries of the global map for the SAME METHOD NAME ONLY.
     *              - If they have the same CLASS_NAME, keep the biggest value.
     *              - If they have different CLASS_NAME:
     *                  - If they have common classes in their hierarchy, keep the entry with the biggest value.
     *                  - If they have disjoint sets of hierarchy classes, keep both entries.
     *
     * @param currentOverloadMap The overload degree map of current class.
     */
    public void mergeMaps(Map<MethodInfo, Integer> currentOverloadMap) {

        // Iterate through all entries of the current map for the SAME METHOD NAME ONLY
        // Here will go if entries exists for this method name
        Map<MethodInfo, Integer> newMapForMethodName = new HashMap<>();
        String methodName = null;
        for (Map.Entry<MethodInfo, Integer> currentEntry : currentOverloadMap.entrySet()) {
            methodName = currentEntry.getKey().getMethodName();
            Map<MethodInfo, Integer> globalOverloadMap = this.overloadDegreeMapByMethodName.get(currentEntry.getKey().getMethodName());
            if (globalOverloadMap == null) {
                this.overloadDegreeMapByMethodName.put(currentEntry.getKey().getMethodName(), currentOverloadMap);
                return;
            }
            // Definitely there have the same MethodName
            for (Map.Entry<MethodInfo, Integer> globalEntry : globalOverloadMap.entrySet()) {
                // If they have the same CLASS_NAME, keep the biggest value
                if (globalEntry.getKey().getClassInfo().getName().equals(currentEntry.getKey().getClassInfo().getName())) {
                    if (globalEntry.getValue() > currentEntry.getValue()) {
                        newMapForMethodName.put(globalEntry.getKey(), globalEntry.getValue());
                    } else {
                        newMapForMethodName.put(currentEntry.getKey(), currentEntry.getValue());
                    }
                }
                // If they have different CLASS_NAME
                else {
                    // If they have common classes in their hierarchy, keep the entry with the biggest value
                    if (haveCommonHierarchy(globalEntry.getKey().getClassInfo(),currentEntry.getKey().getClassInfo())) {
                        if (globalEntry.getValue() > currentEntry.getValue()) {
                            newMapForMethodName.put(globalEntry.getKey(), globalEntry.getValue());
                        } else {
                            newMapForMethodName.put(currentEntry.getKey(), currentEntry.getValue());
                        }
                    }
                    // If they have disjoint sets of hierarchy classes, keep both entries
                    else {
                        newMapForMethodName.put(currentEntry.getKey(), currentEntry.getValue());
                        newMapForMethodName.put(globalEntry.getKey(), globalEntry.getValue());
                    }
                }
            }
        }
        this.overloadDegreeMapByMethodName.replace(Objects.requireNonNull(methodName), newMapForMethodName);
    }

    /**
     * Checks whether two classes have a common hierarchy type (class/interface) in their inheritance chain.
     *
     * @param class1 The first class.
     * @param class2 The second class.
     * @return `true` if the classes have a common hierarchy, `false` otherwise.
     */
    private boolean haveCommonHierarchy(Class<?> class1, Class<?> class2) {
        Set<Class<?>> hierarchy1 = this.polymorphicDegrees.get(class1);
        Set<Class<?>> hierarchy2 = this.polymorphicDegrees.get(class2);

        return hierarchy1.stream().anyMatch(hierarchy2::contains);
    }

    /**
     * Sums up the overload degrees of methods with the same class and method name.
     * It replaces the original map with a new map containing updated overload degrees.
     *
     * @param overloadDegreeCounter A map of method information and their associated overload degrees.
     */
    public void sumUpSameClassMethods(Map<MethodInfo, Integer> overloadDegreeCounter) {
        Map<MethodInfo, Integer> newOverloadDegreeCounter = new HashMap<>();

        // Iterate through the original map to count Polymorphism.MethodInfo objects
        for (Map.Entry<MethodInfo, Integer> method : overloadDegreeCounter.entrySet()) {
            MethodInfo methodInfo = method.getKey();

            boolean alreadyExists = false;
            for (Map.Entry<MethodInfo, Integer> newMethod : newOverloadDegreeCounter.entrySet()) {
                MethodInfo newMethodInfo = newMethod.getKey();

                if (methodInfo.getMethodName().equals(newMethodInfo.getMethodName()) &&
                        methodInfo.getClassInfo().getName().equals(newMethodInfo.getClassInfo().getName())) {
                    // If the Polymorphism.MethodInfo object already exists in the new map, increment the count
                    newOverloadDegreeCounter.put(newMethodInfo, newMethod.getValue() + 1);
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                // If the Polymorphism.MethodInfo object doesn't exist in the new map, add it
                newOverloadDegreeCounter.put(methodInfo, method.getValue());
            }
        }

        // Replace the original map with the new map
        overloadDegreeCounter.clear();
        overloadDegreeCounter.putAll(newOverloadDegreeCounter);
    }

    /**
     * Calculates the overload degree based on method hierarchies and method definitions. For each method that is not
     * firstly defined calculate the overload degree based on the hierarchy chain of its class.
     *
     * @param methodFirstDefined    A map of method information and a boolean indicating if they are first defined.
     * @param overloadDegreeCounter A map of method information and their associated overload degrees to be calculated.
     */
    private void calculateOverloadCounter(Map<MethodInfo, Boolean> methodFirstDefined, Map<MethodInfo, Integer> overloadDegreeCounter) {
        methodFirstDefined.forEach((methodInfo, isFirstDefined) -> {
            if (!isFirstDefined) {
                Set<Class<?>> superClasses = this.polymorphicDegrees.get(methodInfo.getClassInfo());
                overloadDegreeCounter.forEach((overloadedMethod, integer) -> {
                    if (superClasses.contains(overloadedMethod.getClassInfo())) {
                        overloadDegreeCounter.put(overloadedMethod, integer + 1);
                    }
                });
            }
        });
    }

    /**
     * Determines which is the highest hierarchy class that defined the method.
     *  - Get the class hierarchy set of the methods `methodClassesSet` (from which class came every method).
     *  - For each method, get class hierarchy list of the class that it was defined.
     *      - Check if the set of class hierarchy `methodClassesSet` has any class in common with the `currentMethodsClassHierarchyClasses`.
     *          - If it has, then the method is not first defined in its hierarchy.
     *          - If it doesn't have, then the method is first defined in its hierarchy.
     *
     * @param methodInfoList A list of method information.
     * @return A map indicating which methods are first defined in their hierarchies.
     */
    private Map<MethodInfo, Boolean> findTopHierarchyMethodDeclarations(List<MethodInfo> methodInfoList) {
        Map<MethodInfo, Boolean> methodInfoFirstDefinedBooleanMap = new HashMap<>();
        Set<Class<?>> methodClassesSet = methodInfoList.stream()
                .map(MethodInfo::getClassInfo)
                .collect(Collectors.toSet());
        for (MethodInfo methodInfo : methodInfoList) {
            Set<Class<?>> currentMethodsClassHierarchyClasses = this.polymorphicDegrees.get(methodInfo.getClassInfo());
            boolean isFirstDefined = true;
            for (Class<?> currentClass : currentMethodsClassHierarchyClasses) {
                if (methodClassesSet.contains(currentClass) && !currentClass.getName().equals(methodInfo.getClassInfo().getName())) {
                    isFirstDefined = false;
                    break;
                }
            }
            methodInfoFirstDefinedBooleanMap.put(methodInfo, isFirstDefined);
        }
        return methodInfoFirstDefinedBooleanMap;
    }

    public Map<String, Map<MethodInfo, Integer>> getOverloadDegreeMapByMethodName() {
        return overloadDegreeMapByMethodName;
    }
}
