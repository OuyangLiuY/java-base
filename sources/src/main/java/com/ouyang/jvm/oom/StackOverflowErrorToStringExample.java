package com.ouyang.jvm.oom;

/**
 * 循环依赖会导致，stackOverflowError
 * 因此：spring需要解决循环依赖问题
 */
public class StackOverflowErrorToStringExample {

    public static void main(String[] args) {
        A obj = new A();
        System.out.println(obj);
    }
}

class A {
    private int aValue;
    private B bInstance = null;

    public A() {
        aValue = 0;
        bInstance = new B();
    }

    @Override
    public String toString() {
        return "<" + aValue + ", " + bInstance + ">";
    }
}

class B {
    private int bValue;
    private A aInstance = null;

    public B() {
        bValue = 10;
        aInstance = new A();
    }

    @Override
    public String toString() {
        return "<" + bValue + ", " + aInstance + ">";
    }
}