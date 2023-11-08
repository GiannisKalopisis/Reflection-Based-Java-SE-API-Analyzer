# Reflection-Based-Java-SE-API-Analyzer

![Java](https://img.shields.io/badge/Java-17-brightgreen)

This Java project aims to provide insights into the Java Standard Edition (SE) API using Reflection techniques. It answers several key questions related to the API, including the total count of modules, packages, and types, and identifies the top-N polymorphic types and overloaded methods within the API. You can specify the value of N as a parameter when running the program.

Here you can find the standards that this project is based on: [Java 17 Oracle](https://docs.oracle.com/en/java/javase/17/docs/api/)

## Table of Contents

- [Compilation and Execution](#compilation-and-execution)
- [Execution Example](#execution-example)
- [Implementation Logic](#implementation-logic)
  - [Polymorphic Types](#polymorphic-types)
  - [Overloaded Methods](#overloaded-methods)
- [Conclusion](#conclusion)

## Compilation and Execution

To compile and execute the program, follow the steps below:

### Compilation

Compile the Java program by running the following command from the project directory `src`:

```shell
javac Analyzer/*.java Helper/*.java Overload/*.java Polymorphism/*.java Main.java
```
This will generate the necessary .class files.

### Execution
To execute the program and provide a value for N, use the following command:

```shell
java Main N
```
Replace **N** with the desired value when running the program. This will print the results based on the provided parameter. If you want to print all the polymorphic types and overloaded methods, you can set N to less than or equal to 0. If you don't provide the N parameter, the default value is 5.

### Cleanup
After the execution is complete, you can remove the generated .class files and clean the project directory using the following command:

```shell
rm Analyzer/*.class Helper/*.class Overload/*.class Polymorphism/*.class Main.class
```
This will remove the compiled .class files, leaving the project directory tidy.

### Execution Example

Here's an execution example with the top-N parameter set to 10:

```shell
java Main 10
```

![Execution example](https://github.com/GiannisKalopisis/Reflection-Based-Java-SE-API-Analyzer/blob/master/images/execution_example.png)


## Implementation Logic
In this section, we will describe the logic and algorithms used in different parts of the project.

### Polymorphic Types
The project offers two different implementations to calculate the top-N polymorphic types:

1. **JavaSEPolymorphicTypeFinder**: This implementation recursively traverses the type hierarchy graph in Java. It propagates methods upward in the hierarchy for each type. At the end of this process, the final classes (those that do not extend or implement other types) contain all the overloaded methods. This implementation traverses the entire subgraph of the hierarchy for each type.

2. **JavaSEPolymorphicTypeFinderFaster**: Similar to the first implementation, this one recursively traverses the type hierarchy graph but stops when it encounters a class that has already been calculated. After calculating the polymorphic types, it sorts them in ascending order and passes the methods to the appropriate final classes. This optimization ensures that all classes with a polymorphic degree of 2 include all classes with a polymorphic degree of 1, and so on.

Both implementations use efficient data structures for fast method lookup and propagation.

### Overloaded Methods
The implementation for calculating the most overloaded methods also adheres to an interface, ensuring code extensibility through multiple implementations.

This particular implementation, from the Polymorphism stage, accepts a data structure of the form ```Map<Class<?>, Map<String, List<MethodInfo>>>```, which associates top-level classes with a map that groups method names and their respective lists of method information. This map is used to organize and analyze methods for the calculation of overload degrees. 

The algorithm's logic is that it calculates the overload degree of methods by analyzing method hierarchies and definitions. For each method, it computes the overload degree based on its class's hierarchy, taking method inheritance and redefinitions into account.

The algorithm for calculating overload degrees of methods involves the following steps:

- For each top-level class, iterate through its grouped methods (grouped by name).
    - For each method name, iterate through its method list.
    - For each method name, iterate through its method list.
        - For each method in the method list, check if it is first defined in its hierarchy.
        - If it is first defined, set the overload degree to 1.
        - Calculate the overload degree for the rest of the methods.
        - Summarize methods of the same class.
        - Merge the overload degree map with the existing map from other top-level classes.


## Conclusion

In conclusion, this project aimed to develop a Java program that utilizes Reflection techniques to provide comprehensive insights into the Java Standard Edition (SE) API. The program's core objectives were to determine the total count of modules, packages, and types within the Java SE API and to identify the top-N polymorphic types and overloaded methods. The program featured two distinct implementations for calculating the top-N polymorphic types, with the second implementation, "JavaSEPolymorphicTypeFinderFaster," representing an improvement in terms of performance by optimizing the traversal of the type hierarchy. Additionally, the program employed an algorithm to calculate overload degrees for methods, considering method hierarchies, inheritance, and redefinitions. Overall, this project offered a deep exploration of the Java SE API's structure and characteristics, shedding light on its complexity and polymorphism through the application of Reflection, and the efficient algorithmic implementations provided valuable insights into this multifaceted API.

