public class Student {
    String name;
    String account;
    Integer age;

    public Student() {
    }

    public Student(String name) {
        this.name = name;
    }

    public void doSomething(String str) {
        this.name = str;
//        System.out.println("name," + name);
    }

    public void sayInt() {
        System.out.println("name," + name);
    }
}
