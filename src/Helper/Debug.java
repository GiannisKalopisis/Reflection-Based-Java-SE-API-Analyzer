package Helper;

import Polymorphism.MethodInfo;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;


/**
 * Helper.Debug - A utility class for debugging and analyzing method information.
 * <p>
 * This class provides methods for processing method information, printing sorted method parameters, and
 * obtaining method parameters from a given class and method name.
 */
public class Debug {

    /**
     * Processes method information, counting unique method parameter types lists and printing class and method details.
     *
     * @param methodName   The name of the method to process.
     * @param dataClassMap A map of class information and associated method information.
     */
    public static void processMethodInfo(String methodName, Map<Class<?>, Map<String, List<MethodInfo>>> dataClassMap) {
        Map<List<String>, Long> methodParamTypesCount = new HashMap<>();

        dataClassMap.forEach((classInfo, innerMap) -> {

            if (innerMap.containsKey(methodName)) {
                List<MethodInfo> methodInfoList = innerMap.get(methodName);

                methodInfoList.forEach(methodInfo -> {
                    List<String> methodParamTypes = methodInfo.getMethodParametersTypes();
                    methodParamTypesCount.put(methodParamTypes, methodParamTypesCount.getOrDefault(methodParamTypes, 0L) + 1);
                    System.out.println("Class: " + classInfo.getName());
                    System.out.println("Polymorphism.MethodInfo: " + methodInfo);
                });
            }
        });

        List<Map.Entry<List<String>, Long>> sortedParamTypesCount = methodParamTypesCount.entrySet().stream()
                .sorted(Comparator.<Map.Entry<List<String>, Long>>comparingLong(Map.Entry::getValue).reversed()
                        .thenComparing(entry -> String.join(", ", entry.getKey())))
                .toList();

        System.out.println("Unique Method Parameters Types Lists:");
        sortedParamTypesCount.forEach(entry -> {
            List<String> methodParamTypes = entry.getKey();
            long count = entry.getValue();
            System.out.println("Method Parameters Types: " + methodParamTypes + ", Count: " + count);
        });
    }

    /**
     * Prints sorted method parameters for a specific class and method name.
     *
     * @param className   The name of the class to analyze.
     * @param methodName  The name of the method to analyze.
     */
    public static void printSortedMethodParameters(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            List<List<String>> methodParametersList = getMethodParametersList(methodName, clazz);

            methodParametersList.sort(Comparator
                    .<List<String>>comparingInt(List::size)
                    .thenComparing(Object::toString)
            );

            System.out.println("\n\nSorted Method Parameters with Name '" + methodName + "':");
            int index = 0;
            for (List<String> params : methodParametersList) {
                System.out.println((index + 1) + ") " + params);
                index++;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the method parameter lists for a given class and method name.
     *
     * @param methodName The name of the method to analyze.
     * @param clazz      The class for which to obtain method parameter information.
     * @return A list of method parameter lists for the specified method.
     */
    private static List<List<String>> getMethodParametersList(String methodName, Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();

        return Arrays.stream(methods)
                .filter(method -> method.getName().equals(methodName))
                .map(method -> Arrays.stream(method.getParameterTypes())
                        .map(Class::getName)
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
    }
}
