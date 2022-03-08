package com.ouyangliuy.jvm.classloader;

import com.ouyangliuy.jvm.Hello;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;

public class CustomClass extends ClassLoader {


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        File file = new File("E:/workspace/source/java-base/target/classes/com/ouyangliuy/jvm/", name.replace(".", "/") + ".class");
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b = 0;
            while ((b = fis.read()) != 0) {
                baos.write(b);
            }
            byte[] bytes = baos.toByteArray();
            baos.close();
            fis.close();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.loadClass(name);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        ClassLoader customClass = new CustomClass();
        Class<?> aClass = customClass.loadClass("com.ouyangliuy.jvm.Hello");
        System.out.println(aClass);
        Hello hello = (Hello) aClass.getDeclaredConstructor().newInstance();
        hello.sayHello();
        System.out.println(customClass.getClass().getClassLoader());

    }
}
