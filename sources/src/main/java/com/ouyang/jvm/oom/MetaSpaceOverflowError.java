package com.ouyang.jvm.oom;

import java.util.ArrayList;
import java.util.List;

public class MetaSpaceOverflowError {
    public static void main(String[] args) {
        String str = "test";
        List<String> list =new ArrayList<>();
        while (true){
            String str1 = str+str;
            str = str1;
            list.add(str.intern());
        }
    }
}
