package Analyzer;

import java.util.Set;

/**
 * The `JavaSEAnalyzer` interface defines a set of methods that need to be implemented for analyzing Java SE modules and packages.
 * Implementing classes are responsible for finding unique modules, packages per module, types, and for providing total results.
 * <p>
 * This interface serves as a contract for classes that perform analysis on Java SE structures and is used to ensure
 * consistency in analyzing Java SE codebases.
 */
public interface JavaSEAnalyzer {

    void findUniqueModules();

    void findPackagesPerModule();

    void findTypes();

    Set<Class<?>> getAllTypes();

    void printTotalResults();
}
