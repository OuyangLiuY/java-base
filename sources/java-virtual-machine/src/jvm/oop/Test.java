package jvm.oop;

import java.io.Serializable;

public class Test implements Cloneable, Serializable {
    String name;
    public Test(String name) {
        this.name = name;
    }
    public Test() {
    }
    @Override
    public String toString() {
        return "Test{" +
                "name='" + name + '\'' +
                '}';
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}