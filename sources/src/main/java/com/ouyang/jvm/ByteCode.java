package com.ouyang.jvm;

public class ByteCode {

    public void say(){
        System.out.println("byteCode");
    }
    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());
        System.out.println();
    }
}
