package com.ouyang.jvm.gc;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class HelloGC {
    public static void main(String[] args) {
        System.out.println("HelloGC!");
        String str = "xxx";
        System.out.println(Optional.ofNullable(str).orElse(""));
        str = null;
        System.out.println(Optional.ofNullable(null).orElse("1"));
        List list = new LinkedList();
        for(;;) {
            byte[] b = new byte[1024*1024];
            list.add(b);
        }

    }
}
