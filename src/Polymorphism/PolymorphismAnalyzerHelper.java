package Polymorphism;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PolymorphismAnalyzerHelper is an abstract class that serves as a helper for analyzing polymorphism in a set of classes.
 * It provides methods for collecting and processing method information, identifying polymorphic degrees, and organizing top-level
 * received methods for different classes. It is used from implementations of finding polymorphic degrees of the Java SE API.
 * <p>
 * This class is designed to be extended and used as a base for specific polymorphism analysis implementations.
 */
abstract class PolymorphismAnalyzerHelper {

    /**
     * Set of classes/interfaces to be analyzed for polymorphism (all types of the Java 17 SE API)
     */
    protected final Set<Class<?>> classesToAnalyze;

    /**
     * A map that stores the inherit chain of each type. The keys are type (classes/interfaces), and the values are sets
     * of associated inherited types.
     */
    protected Map<Class<?>, Set<Class<?>>> polymorphicDegrees;

    /**
     * A map that stores top-level received methods for different classes. The keys are classes, and the values are maps that store method names
     * and corresponding lists of MethodInfo objects.
     */
    protected final Map<Class<?>, Map<String, List<MethodInfo>>> topLvlReceivedMethods;

    /**
     * Constructs a PolymorphismAnalyzerHelper with the specified set of classes to analyze.
     *
     * @param classesToAnalyze The set of classes to be analyzed for polymorphism.
     */
    protected PolymorphismAnalyzerHelper(Set<Class<?>> classesToAnalyze) {
        this.classesToAnalyze = classesToAnalyze;
        this.polymorphicDegrees = new HashMap<>();
        this.topLvlReceivedMethods = new HashMap<>();
    }

    /**
     * Adds top-level received methods for a given class along with their inherited methods (excluding override methods).
     *
     * @param clazz             The class for which top-level received methods are added.
     * @param inheritedMethods  The list of inherited methods to be added.
     */
    protected void addTopLvlReceivedMethods(Class<?> clazz, List<MethodInfo> inheritedMethods) {
        Map<String, List<MethodInfo>> methodsMap = this.topLvlReceivedMethods.computeIfAbsent(clazz, k -> new HashMap<>());

        for (MethodInfo inheritedMethod : inheritedMethods) {
            List<MethodInfo> topLvlMethods = methodsMap.computeIfAbsent(inheritedMethod.getMethodName(), k -> new ArrayList<>());
            List<MethodInfo> newMethodList = concatMethodsAndExcludeOverride(List.of(inheritedMethod), topLvlMethods);
            methodsMap.put(inheritedMethod.getMethodName(), newMethodList);
        }
    }

    /**
     * Concatenates lists of MethodInfo objects and excludes overridden methods. Check the equality of the name and
     * parameters types. In case of override keep the method of current class and not hte subclasses.
     *
     * @param subClassMethods     The list of methods from a subclass.
     * @param currentClassMethods The list of methods from the current class.
     * @return A list of MethodInfo objects, including the highest in the inheritance hierarchy chain.
     */
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

    /**
     * Extracts the signature of methods from an array of Method objects. Keep the 'signature' at a list of MethodInfo.
     *
     * @param methods The array of Method objects to extract signatures from.
     * @return A list of MethodInfo objects representing the extracted method signatures.
     */
    protected List<MethodInfo> extractMethodsSignature(Method[] methods) {
        return Arrays.stream(methods)
                .map(method -> new MethodInfo(
                        method.getName(),
                        parameterTypesToStringList(method.getParameterTypes()),
                        method.getDeclaringClass()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Converts an array of Class objects representing parameter types into a list of their types.
     *
     * @param parameterTypes The array of Class objects representing parameter types.
     * @return A list of parameter types.
     */
    protected static List<String> parameterTypesToStringList(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(Class::getName)
                .collect(Collectors.toList());
    }
}
