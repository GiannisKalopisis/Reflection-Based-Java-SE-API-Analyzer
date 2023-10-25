import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassMethodContainer {

    private final Set<Class<?>> upperHierarchyClasses;
    private final Map<Method, Integer> methodOverloadDegree;

    public ClassMethodContainer() {
        this.upperHierarchyClasses = new HashSet<>();
        this.methodOverloadDegree = new HashMap<>();
    }

    public void add(Class<?> clazz, Method[] newMethods) {
        this.upperHierarchyClasses.add(clazz);
        addMethodOverload(this.methodOverloadDegree, newMethods);
    }

    public void addAll(ClassMethodContainer inheritedClassMethodContainer, Method[] newMethods) {
        this.upperHierarchyClasses.addAll(inheritedClassMethodContainer.getUpperHierarchyClasses());
        addMethodOverload(inheritedClassMethodContainer.getMethodOverloadDegree(), newMethods);
    }

    public Map<Method, Integer> getMethodOverloadDegree() {
        return methodOverloadDegree;
    }

    private void addMethodOverload(Map<Method, Integer> inheritedClassMethodOverloadMap, Method[] newMethods) {
        for (Method newMethod : newMethods) {
            boolean foundOverload = false;
            for (Method inheritedMethod : inheritedClassMethodOverloadMap.keySet()) {
                if (isOverload(inheritedMethod, newMethod)) {
                    this.methodOverloadDegree.put(newMethod, this.methodOverloadDegree.get(newMethod) + 1);
                    foundOverload = true;
                    break;
                }
            }

            if (!foundOverload) {
                this.methodOverloadDegree.put(newMethod, 1);
            }
        }
    }

    /**
     * Method overloading in Java allows multiple methods in the same class to have
     * the same name while differing in their parameter lists. Two methods are
     * considered overloaded when they meet the following criteria:
     * <p>
     * 1. Same Method Name:
     *    - The methods must have the same name. Overloading involves multiple
     *      methods in the same class sharing a common name.
     * <p>
     * 2. Different Parameter Lists:
     *    - The parameter lists of the methods must differ in one or more of the
     *      following ways:
     *      - Different number of parameters.
     *      - Different data types of parameters.
     *      - Different order of parameters.
     *      - Use of varargs (variable-length argument list) in one method while
     *        not in the other.
     * <p>
     * 3. Same Class or Inheritance Hierarchy:
     *    - Overloaded methods can be defined within the same class or in a subclass.
     *      If a subclass defines a method with the same name but a different parameter
     *      list, it overloads the method in the superclass.
     */
    private boolean isOverload(Method inheritedMethod, Method currentClassMethod) {
        // if not have the same name, they are not overload
        if (!inheritedMethod.getName().equals(currentClassMethod.getName())) {
            return false;
        }

        Class<?>[] inheritedParams = inheritedMethod.getParameterTypes();
        Class<?>[] currentClassParams = currentClassMethod.getParameterTypes();

        // if we have different number of parameters, they are overload
        if (inheritedParams.length != currentClassParams.length) {
            return true;
        }

        // if we have different data types of parameters, they are overload
        for (int i = 0; i < inheritedParams.length; i++) {
            if (inheritedParams[i] != currentClassParams[i]) {
                return true;
            }
        }

        return false;
    }

    public void increaseMethodOverloadDegree(Method method) {
        this.methodOverloadDegree.put(method, this.methodOverloadDegree.get(method) + 1);
    }

    public Set<Class<?>> getUpperHierarchyClasses() {
        return upperHierarchyClasses;
    }
}
