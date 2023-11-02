package testCases;

class MyClass implements CombinedInterface{
    @Override
    public void someMethod(String test) {
        System.out.println("Some method implementation");
    }

    @Override
    public void someMethod() {

    }

    @Override
    public void anotherMethod() {
        System.out.println("Another method implementation");
    }

    @Override
    public void yetAnotherMethod() {
        System.out.println("Yet another method implementation");
    }

    private void yetAnotherMethod(String test) {
        System.out.println("Yet another method implementation with parameter");
    }

    @Override
    public void additionalMethod() {
        System.out.println("Additional method implementation");
    }

//    @Override
//    public void someMethod(int test) {
//
//    }

    public void foo() {

    }

    public void toString(int test, int test2) {
    }

//    @Override
//    public void additionalMethod(String test) {
//
//    }
}