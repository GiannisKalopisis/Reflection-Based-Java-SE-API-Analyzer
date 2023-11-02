import java.util.Set;

public interface JavaSEAnalyzer {

    void findUniqueModules();

    void findPackagesPerModule();

    void findTypes();

    Set<Class<?>> getAllTypes();

    void printTotalResults();
}
