package com.ouyang.thread;


import com.ouyang.util.MyUnsafe;
import sun.misc.Unsafe;


public class T_DCL_Singleton_Unsafe {
    private static T_DCL_Singleton_Unsafe object;
    public static final Unsafe UNSAFE = MyUnsafe.getTheUnsafe();

    public static T_DCL_Singleton_Unsafe getObject() {
        if (object == null) {
            synchronized (T_DCL_Singleton_Unsafe.class) {
                T_DCL_Singleton_Unsafe cur = new T_DCL_Singleton_Unsafe();
                UNSAFE.storeFence();
                object = cur;
            }
        }
        return object;
    }

    public static void main(String[] args) {
        getObject();
    }
}
