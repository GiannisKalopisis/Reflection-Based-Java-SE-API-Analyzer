import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JavaSEOverloadFinder {

    public Map<Class<?>, Map<String, List<MethodInfo>>> getTopLvlReceivedMethods() {
        return topLvlReceivedMethods;
    }

    public void setTopLvlReceivedMethods(Map<Class<?>, Map<String, List<MethodInfo>>> testMap) {
        this.topLvlReceivedMethods = testMap;
    }

    private Map<Class<?>, Map<String, List<MethodInfo>>> topLvlReceivedMethods;
    private Map<MethodInfo, Integer> overloadDegreeMap;
    private final Map<Class<?>, Set<Class<?>>> polymorphicDegrees;

    public JavaSEOverloadFinder(Map<Class<?>, List<MethodInfo>> receivedMethods, Map<Class<?>, Set<Class<?>>> polymorphicDegrees) {
        this.overloadDegreeMap = new HashMap<>();
        this.polymorphicDegrees = polymorphicDegrees;
        this.topLvlReceivedMethods = new HashMap<>();

        receivedMethods.forEach((key, value) -> {
            this.topLvlReceivedMethods.put(key, groupByMethodName(value));
        });
    }

    private Map<String, List<MethodInfo>> groupByMethodName(List<MethodInfo> methodInfoList) {
        Map<String, List<MethodInfo>> methodInfoMap = new HashMap<>();

        for (MethodInfo methodInfo : methodInfoList) {
            String methodName = methodInfo.getMethodName();

            // Get the list of MethodInfo objects with the same methodName
            List<MethodInfo> groupList = methodInfoMap.computeIfAbsent(methodName, k -> new ArrayList<>());

            groupList.add(methodInfo);
        }
        return methodInfoMap;
    }

    public void calculateOverloadDegree() {
        this.topLvlReceivedMethods.forEach((topLvlClass, groupedMethods) -> {
            groupedMethods.forEach((methodName, methodList) -> {    // i.e (bar, [{bar, [], class1}, {bar, [int, String], class2}]) ->
                Map<MethodInfo, Boolean> methodFirstDefined = findTopHierarchyMethodDeclarations(methodList);
                Map<MethodInfo, Integer> overloadDegreeCounter = methodFirstDefined.entrySet()
                        .stream()
                        .filter(Map.Entry::getValue)
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> 1, (a, b) -> a, HashMap::new));
                calculateOverloadCounter(methodFirstDefined, overloadDegreeCounter);
                sumUpSameClassMethods(overloadDegreeCounter);
                this.overloadDegreeMap = mergeMaps(this.overloadDegreeMap, overloadDegreeCounter);
            });
        });
    }

    // merge maps and check if have same entries with different values (foo -> 1 : from inter1, foo -> 2 : from Object)
    // keep the biggest value
    public Map<MethodInfo, Integer> mergeMaps(Map<MethodInfo, Integer> globalOverloadMap,
                                              Map<MethodInfo, Integer> currentOverloadMap) {
        Map<MethodInfo, Integer> returnMap = new HashMap<>();

        for (Map.Entry<MethodInfo, Integer> globalEntry : globalOverloadMap.entrySet()) {
            MethodInfo globalKey = globalEntry.getKey();
            Integer globalValue = globalEntry.getValue();
            boolean merged = false;

            for (Map.Entry<MethodInfo, Integer> currentEntry : currentOverloadMap.entrySet()) {
                MethodInfo currentKey = currentEntry.getKey();
                Integer currentValue = currentEntry.getValue();

                // If hava same METHOD_NAME and CLASS_NAME, keep the biggest value
                if (globalKey.getMethodName().equals(currentKey.getMethodName()) && globalKey.getClassInfo().getName().equals(currentKey.getClassInfo().getName())) {
                    returnMap.put(globalKey, Math.max(globalValue, currentValue));
                    merged = true;
                    break;
                }
                // If hava same METHOD_NAME and different CLASS_NAME
                else if (globalKey.getMethodName().equals(currentKey.getMethodName()) && !globalKey.getClassInfo().getName().equals(currentKey.getClassInfo().getName())) {
                    // If they have common classes in their hierarchy, keep the entry with the biggest value
                    if (haveCommonHierarchy(globalKey.getClassInfo(), currentKey.getClassInfo())) {
                        if (globalValue > currentValue) {
                            returnMap.put(globalKey, globalValue);
                        } else {
                            returnMap.put(currentKey, currentValue);
                        }
                    }
                    // If they have disjoint sets of hierarchy classes, keep both entries
                    else {
                        returnMap.put(currentKey, currentValue);
                    }
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                returnMap.put(globalKey, globalValue);
            }
        }

        // Add any entries from currentOverloadMap that were not processed
        currentOverloadMap.entrySet().stream()
                .filter(entry -> !returnMap.containsKey(entry.getKey()))
                .forEach(entry -> returnMap.put(entry.getKey(), entry.getValue()));

        return returnMap;
    }

    private boolean haveCommonHierarchy(Class<?> class1, Class<?> class2) {
        Set<Class<?>> hierarchy1 = this.polymorphicDegrees.get(class1);
        Set<Class<?>> hierarchy2 = this.polymorphicDegrees.get(class2);

        return hierarchy1.stream().anyMatch(hierarchy2::contains);
    }

    public void sumUpSameClassMethods(Map<MethodInfo, Integer> overloadDegreeCounter) {
        Map<MethodInfo, Integer> newOverloadDegreeCounter = new HashMap<>();

        // Iterate through the original map to count MethodInfo objects
        for (Map.Entry<MethodInfo, Integer> method : overloadDegreeCounter.entrySet()) {
            MethodInfo methodInfo = method.getKey();

            boolean alreadyExists = false;
            for (Map.Entry<MethodInfo, Integer> newMethod : newOverloadDegreeCounter.entrySet()) {
                MethodInfo newMethodInfo = newMethod.getKey();

                if (methodInfo.getMethodName().equals(newMethodInfo.getMethodName()) &&
                        methodInfo.getClassInfo().getName().equals(newMethodInfo.getClassInfo().getName())) {
                    // If the MethodInfo object already exists in the new map, increment the count
                    newOverloadDegreeCounter.put(newMethodInfo, newMethod.getValue() + 1);
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                // If the MethodInfo object doesn't exist in the new map, add it
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
}
