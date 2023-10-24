import java.util.*;

public class JavaSEPolymorphicTypeFinder {

    // TODO: Pass to every bottom level a map<method signature -> degree> and update it at every level

    private final List<Class<?>> classesToAnalyze;
    private final Map<Class<?>, Set<Class<?>>> polymorphicDegrees;

    public JavaSEPolymorphicTypeFinder(List<Class<?>> classesToAnalyze) {
        this.polymorphicDegrees = new HashMap<>();
        this.classesToAnalyze = classesToAnalyze;
    }

    public Map<Class<?>, Set<Class<?>>> calculatePolymorphicDegrees() {
        for (Class<?> clazz : this.classesToAnalyze) {
            calculatePolymorphicDegreesRecursively(clazz);
        }
        return polymorphicDegrees;
    }

    private Set<Class<?>> calculatePolymorphicDegreesRecursively(Class<?> clazz) {
        Set<Class<?>> currentSet = new HashSet<>();
        if (clazz == null || polymorphicDegrees.containsKey(clazz)) {
            return clazz == null ? currentSet : polymorphicDegrees.get(clazz);
        }

        // Add all the superclasses and interfaces to currentSet
        currentSet.addAll(calculatePolymorphicDegreesRecursively(clazz.getSuperclass()));
        for (Class<?> iface : clazz.getInterfaces()) {
            currentSet.addAll(calculatePolymorphicDegreesRecursively(iface));
        }

        // Add current class to currentSet
        currentSet.add(clazz);

        this.polymorphicDegrees.put(clazz, currentSet);

        return currentSet;
    }
}
