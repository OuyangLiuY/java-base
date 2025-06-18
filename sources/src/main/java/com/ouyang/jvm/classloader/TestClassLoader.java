package com.ouyang.jvm.classloader;




import com.ouyang.jvm.Hello;

import java.lang.reflect.InvocationTargetException;

public class TestClassLoader {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        ClassLoader customClass = new CustomClass();
        Class<?> aClass = customClass.loadClass("jvm.Hello");
        System.out.println(aClass);
        Hello hello = (Hello) aClass.getDeclaredConstructor().newInstance();
        hello.sayHello();
        System.out.println(customClass.getClass().getClassLoader());

    }
}
