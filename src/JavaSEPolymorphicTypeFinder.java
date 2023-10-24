import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaSEPolymorphicTypeFinder {

    private final List<Class<?>> classesToAnalyze;
    private final Map<Class<?>, Integer> polymorphicDegrees;

    public JavaSEPolymorphicTypeFinder(List<Class<?>> classesToAnalyze) {
        this.polymorphicDegrees = new HashMap<>();
        this.classesToAnalyze = classesToAnalyze;
    }

    public Map<Class<?>, Integer> calculatePolymorphicDegrees() {
        for (Class<?> clazz : this.classesToAnalyze) {
            calculatePolymorphicDegreesRecursively(clazz);
        }
        return polymorphicDegrees;
    }

    private int calculatePolymorphicDegreesRecursively(Class<?> clazz) {
        if (clazz == null || this.polymorphicDegrees.containsKey(clazz)) {
            return clazz == null ? 0 : this.polymorphicDegrees.getOrDefault(clazz, 0);
        }

        int superclassesCount = calculatePolymorphicDegreesRecursively(clazz.getSuperclass());
        int interfacesCount = 0;

        for (Class<?> iface : clazz.getInterfaces()) {
            interfacesCount += calculatePolymorphicDegreesRecursively(iface);
        }

        int polymorphicDegree = 1 + superclassesCount + interfacesCount;

        this.polymorphicDegrees.put(clazz, this.polymorphicDegrees.getOrDefault(clazz, 0) + polymorphicDegree);

        return polymorphicDegree;
    }

    private Map<Class<?>, Integer> getPolymorphicDegrees() {
        return this.polymorphicDegrees;
    }
}
