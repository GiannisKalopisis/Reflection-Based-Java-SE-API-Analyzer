import java.util.*;

public class JavaSEPolymorphicTypeFinder {

    private final List<Class<?>> classesToAnalyze;
    private final Map<Class<?>, Integer> polymorphicDegrees;
    private final Set<Class<?>> processedInterfaces;

    public JavaSEPolymorphicTypeFinder(List<Class<?>> classesToAnalyze) {
        this.polymorphicDegrees = new HashMap<>();
        this.processedInterfaces = new HashSet<>();
        this.classesToAnalyze = classesToAnalyze;
    }

    public Map<Class<?>, Integer> calculatePolymorphicDegrees() {
        for (Class<?> clazz : this.classesToAnalyze) {
            calculatePolymorphicDegreesRecursively(clazz);
        }
        return polymorphicDegrees;
    }

    private Set<Class<?>> calculatePolymorphicDegreesRecursively(Class<?> clazz) {
        Set<Class<?>> currentSet = new HashSet<>();
        if (clazz == null) {
            return currentSet;
        }

        currentSet.addAll(calculatePolymorphicDegreesRecursively(clazz.getSuperclass()));
        for (Class<?> iface : clazz.getInterfaces()) {
            Set<Class<?>> interfacesSet = calculatePolymorphicDegreesRecursively(iface);
            currentSet.addAll(interfacesSet);
        }

        currentSet.add(clazz);

        this.polymorphicDegrees.put(clazz, currentSet.size());

        return currentSet;
    }
}
