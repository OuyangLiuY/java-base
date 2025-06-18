package com.ouyang.juc;

/**
 * DCL单例
 */
public class T_DCL_Singleton {

    private static volatile T_DCL_Singleton object;

    public static T_DCL_Singleton getObject(){
        if (object == null){
            synchronized (T_DCL_Singleton.class){
                if(object == null){
                    object = new T_DCL_Singleton();
                }
            }
        }
        return object;
    }
    public static void main(String[] args){
        getObject();
    }
}
