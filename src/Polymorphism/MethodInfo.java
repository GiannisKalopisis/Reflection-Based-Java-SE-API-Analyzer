package Polymorphism;

import java.util.List;
import java.util.Objects;

public class MethodInfo {

    private final String methodName;
    private final List<String> methodParametersTypes;
    private final Class<?> classInfo;

    public MethodInfo(String methodName, List<String> methodParametersTypes, Class<?> classInfo) {
        this.methodName = methodName;
        this.methodParametersTypes = methodParametersTypes;
        this.classInfo = classInfo;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getMethodParametersTypes() {
        return methodParametersTypes;
    }

    public Class<?> getClassInfo() {
        return classInfo;
    }

    @Override
    public String toString() {
        return methodName + ", " + methodParametersTypes + ", " + classInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MethodInfo otherMethodInfo = (MethodInfo) obj;
        return Objects.equals(methodName, otherMethodInfo.methodName)
                && Objects.equals(methodParametersTypes, otherMethodInfo.methodParametersTypes)
                && Objects.equals(classInfo, otherMethodInfo.classInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, methodParametersTypes, classInfo);
    }
}
