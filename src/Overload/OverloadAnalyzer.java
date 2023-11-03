package Overload;

/**
 * The `OverloadAnalyzer` interface defines a contract for classes that perform the analysis of method overloading
 * within a given context. Classes implementing this interface must provide a method for calculating the overload degree
 * of methods, indicating how overloaded a set of methods is and potentially identifying the most overloaded methods.
 */
public interface OverloadAnalyzer {

    void calculateOverloadDegree();
}
