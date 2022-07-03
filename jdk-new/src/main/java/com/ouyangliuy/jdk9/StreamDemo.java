package com.ouyangliuy.jdk9;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

public class StreamDemo {
    public static void main(String[] args) {
//        testTakeWhile();
//        testDropWhile();
//        testOfNullable();
//        testNewIterate();
        testOptionalStream();
    }
    public static void testTakeWhile(){
        List<Integer> list = Arrays.asList(1, 89, 63, 45, 72, 65, 41, 65, 82, 35, 95, 100);
        // 从头开始取所有奇数,直到遇见一个偶数为止
        list.stream().takeWhile(e-> e%2==1).forEach(System.out::println);

    }

    /**
     * 测试Stream新增dropWhile方法
     * dropWhile  从头开始删除满足条件的数据,直到遇见第一个不满足的位置,并保留剩余元素
     */
    public static void testDropWhile(){
        List<Integer> list = Arrays.asList(2, 86, 63, 45, 72, 65, 41, 65, 82, 35, 95, 100);
        // 删除流开头所有的偶数,直到遇见奇数为止.后面的数据都保留
        list.stream().dropWhile(e-> e%2==0 ).forEach(System.out::println);

    }


    /**
     * 测试Stream新增ofNullable方法
     * ofNullable 允许创建Stream流时,只放入一个null
     */
    public static void testOfNullable(){
        // of方法获取流 ,允许元素中有多个null值
        Stream<Integer> stream1 = Stream.of(10, 20, 30, null);
        // 如果元素中只有一个null,是不允许的
        Stream<Integer> stream2 = Stream.of(null);
        // JAVA9中,如果元素为null,返回的是一个空Stream,如果不为null,返回一个只有一个元素的Stream
        Stream<Integer> stream3 = Stream.ofNullable(null);
    }
    /**
     * 测试Stream新增iterate方法
     * iterate指定种子数,指定条件和迭代方式来获取流
     */
    public static void testNewIterate(){
        //JAVA8通过 generate方法获取一个Stream
        Stream.generate(Math::random).limit(10).forEach(System.out::println);
        //JAVA8 通过iterate获取一个Stream,从种子seed开始，每个多加2
        Stream.iterate(0,t-> t+2).limit(10).forEach(System.out::println);
        //JAVA9通过重载iterate获取Stream，从0开始，每次在上一个加1，最终小于10，
        Stream.iterate(0,t -> t<10,t-> t+1).forEach(System.out::println);
    }

    /**
     * Optional类新增Stream方法,可以将一个Optional转换为Stream
     */
    public static void testOptionalStream(){
        List<Integer> list =new ArrayList<>();
        Collections.addAll(list,10,5,45,95,36,85,47);
        Optional<Integer> max = list.stream().max((a, b) -> a - b);
        System.out.println(max.orElse(100));
        Optional<List<Integer>> optional=Optional.ofNullable(list);

        // 通过optional的Stream方法获取一个Stream
        Stream<List<Integer>> stream = optional.stream();
        // 以为内部的每个元素也是一个List,通过flatMap方法,将内部的List转换为Stream后再放入一个大Stream
        stream.flatMap(Collection::stream).forEach(System.out::println);
    }

    
}
