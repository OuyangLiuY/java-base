package com.ouyangliuy.generic;

import java.lang.reflect.Method;

public class InfoImpl implements Info<Integer> {
    @Override
    public Integer info(Integer val) {
        return val;
    }

    public static void main(String[] args) {
        Class<InfoImpl> clazz = InfoImpl.class;
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName() + ":" + method.getReturnType().getSimpleName());
        }
    }

}
interface Info <T>{
    T info(T val);
}