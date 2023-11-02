package Overload;

import Polymorphism.MethodInfo;

import java.util.*;
import java.util.stream.Collectors;


public class JavaSEOverloadFinder {

    private final Map<Class<?>, Map<String, List<MethodInfo>>> topLvlReceivedMethods;
    private final Map <String, Map<MethodInfo, Integer>> overloadDegreeMapByMethodName;
    private final Map<Class<?>, Set<Class<?>>> polymorphicDegrees;

    public JavaSEOverloadFinder(Map<Class<?>, Map<String, List<MethodInfo>>> receivedMethods, Map<Class<?>, Set<Class<?>>> polymorphicDegrees) {
        this.polymorphicDegrees = polymorphicDegrees;
        this.overloadDegreeMapByMethodName = new HashMap<>();
        this.topLvlReceivedMethods = receivedMethods;
//        receivedMethods.forEach((key, value) -> this.topLvlReceivedMethods.put(key, Helper.Utils.groupByMethodName(value)));
    }

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

    private boolean haveCommonHierarchy(Class<?> class1, Class<?> class2) {
        Set<Class<?>> hierarchy1 = this.polymorphicDegrees.get(class1);
        Set<Class<?>> hierarchy2 = this.polymorphicDegrees.get(class2);

        return hierarchy1.stream().anyMatch(hierarchy2::contains);
    }

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

    private Map<MethodInfo, Boolean> findTopHierarchyMethodDeclarations(List<MethodInfo> methodInfoList) {
        Map<MethodInfo, Boolean> methodInfoFirstDefinedBooleanMap = new HashMap<>();
        Set<Class<?>> methodClassesSet = methodInfoList.stream()
                .map(MethodInfo::getClassInfo)
                .collect(Collectors.toSet());
        for (MethodInfo methodInfo : methodInfoList) {
            // Gia ka8e mia apo tis klaseis sth lista toy hierarchy bres an oristhke ekei to sugkekrimeno method
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
