import java.util.List;

public class MethodInfo {

    private String methodName;
    private List<String> methodParametersTypes;
    private String className;

    public MethodInfo(String methodName, List<String> methodParametersTypes, String className) {
        this.methodName = methodName;
        this.methodParametersTypes = methodParametersTypes;
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getMethodParametersTypes() {
        return methodParametersTypes;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return methodName + ", " + methodParametersTypes + ", " + className;
    }
}
