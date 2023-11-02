import java.lang.reflect.Method;
import java.util.*;


public class JavaSEPolymorphicTypeFinder extends PolymorphismAnalyzerHelper implements PolymorphismAnalyzer {

    public JavaSEPolymorphicTypeFinder(Set<Class<?>> classesToAnalyze) {
        super(classesToAnalyze);
    }

    @Override
    public Map<Class<?>, Set<Class<?>>> calculatePolymorphicDegrees() {

        // keep set of visited classes if current iteration class is inside continue
        Set<Class<?>> visitedClasses = new HashSet<>();
        for (Class<?> clazz : this.classesToAnalyze) {
            if (visitedClasses.contains(clazz)) {
                continue;
            }
            visitedClasses.addAll(calculatePolymorphicDegreesRecursively(clazz, new ArrayList<>()));
        }
//        clearDoubleEntriesInInterfacesAndObject();
        return this.polymorphicDegrees;
    }

    private Set<Class<?>> calculatePolymorphicDegreesRecursively(Class<?> clazz, List<MethodInfo> subClassMethods) {
        Set<Class<?>> inheritanceClassGraph = new LinkedHashSet<>();

        if (clazz == null ) {    // Need to reach top of inheritance level to pass methods all the way
            return inheritanceClassGraph;
        }


        Method[] currentClassMethods = clazz.getDeclaredMethods();
        List<MethodInfo> passingMethods = concatMethodsAndExcludeOverride(subClassMethods, extractMethodsSignature(currentClassMethods));

        // Add all the superclasses and interfaces to inheritanceClassGraph
        inheritanceClassGraph.addAll(calculatePolymorphicDegreesRecursively(clazz.getSuperclass(), passingMethods));
        for (Class<?> iface : clazz.getInterfaces()) {
            inheritanceClassGraph.addAll(calculatePolymorphicDegreesRecursively(iface, passingMethods));
        }

        if (inheritanceClassGraph.isEmpty()) {  // Reached top of inheritance level -> add methods
            addTopLvlReceivedMethods(clazz, passingMethods);
        }

        // Add current class to inheritanceClassGraph
        inheritanceClassGraph.add(clazz);

        this.polymorphicDegrees.put(clazz, inheritanceClassGraph);

        return inheritanceClassGraph;
    }

    @Override
    public Map<Class<?>, Map<String, List<MethodInfo>>> getTopLvlReceivedMethods() {
        return topLvlReceivedMethods;
    }
}
