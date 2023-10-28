import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class JavaSEPolymorphicTypeFinder {

    // TODO: Pass to every bottom level a map<method signature -> degree> and update it at every level

    private final List<Class<?>> classesToAnalyze;
    private final Map<Class<?>, Set<Class<?>>> polymorphicDegrees;
    private final Map<Class<?>, List<MethodInfo>> topLvlReceivedMethods;

    public JavaSEPolymorphicTypeFinder(List<Class<?>> classesToAnalyze) {
        this.polymorphicDegrees = new HashMap<>();
        this.topLvlReceivedMethods = new HashMap<>();
        this.classesToAnalyze = classesToAnalyze;
    }

    public Map<Class<?>, Set<Class<?>>> calculatePolymorphicDegrees() {
        for (Class<?> clazz : this.classesToAnalyze) {
            calculatePolymorphicDegreesRecursively(clazz, new ArrayList<>());
        }
        return polymorphicDegrees;
    }

    private Set<Class<?>> calculatePolymorphicDegreesRecursively(Class<?> clazz, List<MethodInfo> subClassMethods) {
        Set<Class<?>> inheritanceClassChain = new LinkedHashSet<>();
        // Not work for passing over methods
//        if (clazz == null || polymorphicDegrees.containsKey(clazz)) {
//            return clazz == null ? inheritanceClassChain : polymorphicDegrees.get(clazz);
//        }
        if (clazz == null) {
            return inheritanceClassChain;
        }

        Method[] currentClassMethods = clazz.getDeclaredMethods();
        List<MethodInfo> passingMethods = concatMethodsAndExcludeOverride(subClassMethods, extractMethodsSignature(currentClassMethods));

        // Add all the superclasses and interfaces to inheritanceClassChain
        inheritanceClassChain.addAll(calculatePolymorphicDegreesRecursively(clazz.getSuperclass(), passingMethods));
        for (Class<?> iface : clazz.getInterfaces()) {
            inheritanceClassChain.addAll(calculatePolymorphicDegreesRecursively(iface, passingMethods));
        }

        if (inheritanceClassChain.isEmpty()) {
            addTopLvlReceivedMethods(clazz, passingMethods);
        }

        // Add current class to inheritanceClassChain
        inheritanceClassChain.add(clazz);

        this.polymorphicDegrees.put(clazz, inheritanceClassChain);

        return inheritanceClassChain;
    }

//    public void addTopLvlReceivedMethods(Class<?> clazz, List<MethodInfo> methods) {
//        if (!this.topLvlReceivedMethods.containsKey(clazz)) {
//            this.topLvlReceivedMethods.put(clazz, methods);
//        } else {
//            List<MethodInfo> currentList = this.topLvlReceivedMethods.get(clazz);
//            List<MethodInfo> concatenatedList = new ArrayList<>(currentList);
//            for (MethodInfo methodInfo : methods) {
//                boolean isDuplicate = false;
//                for (MethodInfo concatenatedMethod : currentList) {
//                    if (concatenatedMethod.getMethodName().equals(methodInfo.getMethodName()) &&
//                        concatenatedMethod.getMethodParametersTypes().equals(methodInfo.getMethodParametersTypes()) &&
//                        concatenatedMethod.getClassName().equals(methodInfo.getClassName())) {
//                        isDuplicate = true;
//                        break;
//                    }
//                }
//                if (!isDuplicate) {
//                    concatenatedList.add(methodInfo);
//                }
//            }
//            this.topLvlReceivedMethods.put(clazz, concatenatedList);
//        }
//    }

    public void addTopLvlReceivedMethods(Class<?> clazz, List<MethodInfo> methods) {
        this.topLvlReceivedMethods.computeIfAbsent(clazz, k -> new ArrayList<>())
                .addAll(methods.stream()
                        .filter(method ->
                                this.topLvlReceivedMethods.get(clazz).stream()
                                        .noneMatch(existing ->
                                                existing.getMethodName().equals(method.getMethodName()) &&
                                                existing.getMethodParametersTypes().equals(method.getMethodParametersTypes()) &&
                                                existing.getClassInfo().getName().equals(method.getClassInfo().getName())
                                        )
                        )
                        .toList()
                );
    }


    private List<MethodInfo> concatMethodsAndExcludeOverride(List<MethodInfo> subClassMethods, List<MethodInfo> currentClassMethods) {
        List<MethodInfo> result = new ArrayList<>(currentClassMethods); // In case of override, keeping the highest in inheritance hierarchy chain method

        for (MethodInfo subClassMethod : subClassMethods) {
            // Must not have the same method name and parameters types
            boolean isOverridden = currentClassMethods.stream()
                    .anyMatch(currentClassMethod ->
                            currentClassMethod.getMethodName().equals(subClassMethod.getMethodName()) &&
                            currentClassMethod.getMethodParametersTypes().equals(subClassMethod.getMethodParametersTypes())
                    );

            if (!isOverridden) {
                result.add(subClassMethod);
            }
        }

        return result;
    }

    public List<MethodInfo> extractMethodsSignature(Method[] methods) {
        return Arrays.stream(methods)
                .map(method -> new MethodInfo(
                        method.getName(),
                        parameterTypesToStringList(method.getParameterTypes()),
                        method.getDeclaringClass()
                ))
                .collect(Collectors.toList());
    }

    private static List<String> parameterTypesToStringList(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(Class::getName)
                .collect(Collectors.toList());
    }

    public Map<Class<?>, List<MethodInfo>> getTopLvlReceivedMethods() {
        return topLvlReceivedMethods;
    }
}
