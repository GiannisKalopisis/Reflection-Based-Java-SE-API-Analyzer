import java.util.List;

public class MethodInfo {

    private String methodName;
    private List<String> methodParametersTypes;
    private Class<?> classInfo;

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
}
