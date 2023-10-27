import java.lang.reflect.Method;
import java.util.*;

public class JavaSEPolymorphicTypeFinder {

    // TODO: Pass to every bottom level a map<method signature -> degree> and update it at every level

    private final List<Class<?>> classesToAnalyze;
    private final Map<Class<?>, Set<Class<?>>> polymorphicDegrees;
    private final Map<Class<?>, List<MethodInfo>> receivedMethods;

    public JavaSEPolymorphicTypeFinder(List<Class<?>> classesToAnalyze) {
        this.polymorphicDegrees = new HashMap<>();
        this.receivedMethods = new HashMap<>();
        this.classesToAnalyze = classesToAnalyze;
    }

    public Map<Class<?>, Set<Class<?>>> calculatePolymorphicDegrees() {
        for (Class<?> clazz : this.classesToAnalyze) {
            calculatePolymorphicDegreesRecursively(clazz, new ArrayList<>());
        }
        return polymorphicDegrees;
    }

    private Set<Class<?>> calculatePolymorphicDegreesRecursively(Class<?> clazz, List<MethodInfo> subClassMethods) {
        Set<Class<?>> currentSet = new LinkedHashSet<>();

        // Not work for passing over methods
//        if (clazz == null || polymorphicDegrees.containsKey(clazz)) {
//            return clazz == null ? currentSet : polymorphicDegrees.get(clazz);
//        }
        if (clazz == null) {
            return currentSet;
        }

//        Set<Method> currentClassMethods = new HashSet<>(Arrays.asList(clazz.getDeclaredMethods()));
//        List<MethodInfo> currentClassMethodsInfo;
//        if (this.receivedMethods.get(clazz) == null || this.receivedMethods.get(clazz).isEmpty()) {
//            currentClassMethodsInfo = getMethodsParametersTypes(currentClassMethods);
//        } else {
//            currentClassMethodsInfo = this.receivedMethods.get(clazz);
//        }
//        List<MethodInfo> passingMethods = concatMethodsInfo(subClassMethods, currentClassMethodsInfo);
//        this.receivedMethods.put(clazz, passingMethods);

        // Add all the superclasses and interfaces to currentSet
        currentSet.addAll(calculatePolymorphicDegreesRecursively(clazz.getSuperclass(), null));
        for (Class<?> iface : clazz.getInterfaces()) {
            currentSet.addAll(calculatePolymorphicDegreesRecursively(iface, null));
        }

        // Add current class to currentSet
        currentSet.add(clazz);

        this.polymorphicDegrees.put(clazz, currentSet);

        return currentSet;
    }

    private List<MethodInfo> concatMethodsInfo(List<MethodInfo> subClassMethods, List<MethodInfo> currentClassMethods) {
        List<MethodInfo> concatMethods = new ArrayList<>(currentClassMethods);
        for (MethodInfo methodInfo : subClassMethods) {
            boolean found = false;
            for (MethodInfo concatMethod : currentClassMethods) {
                // If method with same className already exists,
                // it means that methods of that class already have been added from other path
                if (methodInfo.getClassName().equals(concatMethod.getClassName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                concatMethods.add(methodInfo);
            }
        }
        return concatMethods;
    }

    private List<MethodInfo> getMethodsParametersTypes(Set<Method> methodSet) {
        List<MethodInfo> methodInfoList = new ArrayList<>();
        for (Method method : methodSet) {
            List<String> methodParametersTypes = new ArrayList<>();
            for (Class<?> parameterType : method.getParameterTypes()) {
                methodParametersTypes.add(parameterType.getName());
            }
            methodInfoList.add(new MethodInfo(method.getName(), methodParametersTypes, method.getDeclaringClass().getName()));
        }
        return methodInfoList;
    }

    public Map<Class<?>, List<MethodInfo>> getReceivedMethods() {
        return receivedMethods;
    }
}
