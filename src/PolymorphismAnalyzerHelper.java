import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

abstract class PolymorphismAnalyzerHelper {

    protected final Set<Class<?>> classesToAnalyze;
    protected Map<Class<?>, Set<Class<?>>> polymorphicDegrees;
    protected final Map<Class<?>, Map<String, List<MethodInfo>>> topLvlReceivedMethods;

    protected PolymorphismAnalyzerHelper(Set<Class<?>> classesToAnalyze) {
        this.classesToAnalyze = classesToAnalyze;
        this.polymorphicDegrees = new HashMap<>();
        this.topLvlReceivedMethods = new HashMap<>();
    }

//    protected void addTopLvlReceivedMethods(Class<?> clazz, List<MethodInfo> methods) {
//        this.topLvlReceivedMethods.computeIfAbsent(clazz, k -> new ArrayList<>())
//                .addAll(methods.stream()
//                        .filter(method ->
//                                this.topLvlReceivedMethods.get(clazz).stream()
//                                        .noneMatch(existing ->
//                                                        existing.getMethodName().equals(method.getMethodName()) &&
//                                                                existing.getMethodParametersTypes().equals(method.getMethodParametersTypes())
//                                                // Do not want to check the class name
//                                                // Only need to check if method have same method name and parameters types
//                                                // && existing.getClassInfo().getName().equals(method.getClassInfo().getName())
//                                        )
//                        )
//                        .toList()
//                );
//    }

    protected void addTopLvlReceivedMethods(Class<?> clazz, List<MethodInfo> inheritedMethods) {
        Map<String, List<MethodInfo>> methodsMap = this.topLvlReceivedMethods.computeIfAbsent(clazz, k -> new HashMap<>());

        for (MethodInfo inheritedMethod : inheritedMethods) {
            List<MethodInfo> topLvlMethods = methodsMap.computeIfAbsent(inheritedMethod.getMethodName(), k -> new ArrayList<>());
            List<MethodInfo> newMethodList = concatMethodsAndExcludeOverride(List.of(inheritedMethod), topLvlMethods);
            methodsMap.put(inheritedMethod.getMethodName(), newMethodList);
        }
    }

    protected List<MethodInfo> concatMethodsAndExcludeOverride(List<MethodInfo> subClassMethods, List<MethodInfo> currentClassMethods) {
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

    protected List<MethodInfo> extractMethodsSignature(Method[] methods) {
        return Arrays.stream(methods)
                .map(method -> new MethodInfo(
                        method.getName(),
                        parameterTypesToStringList(method.getParameterTypes()),
                        method.getDeclaringClass()
                ))
                .collect(Collectors.toList());
    }

    protected static List<String> parameterTypesToStringList(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(Class::getName)
                .collect(Collectors.toList());
    }
}
