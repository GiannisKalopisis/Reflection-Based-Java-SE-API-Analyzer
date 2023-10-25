public class TestClass2 extends MyClass implements FirstInterface{

    public void yetAnotherMethod(int test) {
        System.out.println("Yet another method implementation with parameter");
    }

    @Override
    public void someMethod() {
        System.out.println("Some method implementation");
    }

    protected void toString(int test, String test2) {
    }
}
