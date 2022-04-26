package com.ouyangliuy.jvm.oop;

import com.ouyangliuy.utils.MyUnsafe;
import sun.misc.Unsafe;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;

public class NewOopTest {
    public static void main(String[] args) throws Exception {
        // 1 new
        Test t1 = new Test("t1");
        System.out.println(t1);
        // 2 clone
        Test t2 = (Test) t1.clone();
        System.out.println(t2 == t1);
        t2.name = "t2";
        System.out.println(t2);

        // 3 反射
        Class<?> aClass = Class.forName("com.ouyangliuy.jvm.oop.Test");
        // 注意，这里使用的无参构造，如果Test中没有则抛异常。因此该方法在JDK9中已经被弃用
        Test t31 = (Test) aClass.newInstance();
        t31.name = "t31";
        System.out.println(t31);
        Constructor<Test> constructor = Test.class.getConstructor(String.class);
        Test t32 = constructor.newInstance("t32");
//        t32.name = "t32";
        System.out.println(t32);
        // 4 序列化
        {
            //t1 开始序列化
            String path = "object.txt";
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(t1);
            outputStream.flush();
            outputStream.close();
            //t4 开始反序列化
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            Test t4 = (Test) inputStream.readObject();
            inputStream.close();
            System.out.println(t4 == t1);
            t4.name = "t4";
            System.out.println(t4);
        }
        // 5 unsafe
        Unsafe unsafe = MyUnsafe.getTheUnsafe();
        Test t5 = (Test) unsafe.allocateInstance(Test.class);
        t5.name = "t5";
        System.out.println(t5);
    }
}


