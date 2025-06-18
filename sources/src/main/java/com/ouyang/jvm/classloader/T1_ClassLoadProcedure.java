package com.ouyang.jvm.classloader;

public class T1_ClassLoadProcedure {

    public static void main(String[] args) {
        System.out.println(T.count);
    }
    static class T{
        /**
         * 1. 如下顺序
         * public static int count = 2;
         * public static T t = new T();
         * 结果是3
         * 2. 如下顺序
         * public static T t = new T();
         * public static int count = 2;
         * 结果是2
         */
        public static int count = 2;
        public static T t = new T();
        private T(){
            count++;
        }
    }
}
