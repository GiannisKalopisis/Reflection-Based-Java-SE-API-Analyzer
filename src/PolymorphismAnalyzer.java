import java.util.List;
import java.util.Map;
import java.util.Set;


public interface PolymorphismAnalyzer {

    Map<Class<?>, Set<Class<?>>> calculatePolymorphicDegrees();

    Map<Class<?>, Map<String, List<MethodInfo>>> getTopLvlReceivedMethods();

}
