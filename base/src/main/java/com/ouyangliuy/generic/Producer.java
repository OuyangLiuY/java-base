package com.ouyangliuy.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Producer<T extends Object> {
    public T producer;


    /**
     * 对于类中泛型，是不能定义为static的
     * @return T
     */
    public T getProducer() {
        return producer;
    }

    public void setProducer(T producer) {
        this.producer = producer;
    }


    /**
     * 泛型方法
     * @param list
     * @param <E>
     * @return E
     */
    public <E> E getProducer(ArrayList<E> list){
        return list.get(getIdx(list.size()));
    }

    /**
     * 可变参数的静态方法
     * @param e
     * @param <E>
     * @return E
     */
    public static  <E> E getProducer(E... e){
        System.out.println(Arrays.toString(e));
        return e[getIdx(e.length)];
    }

    /**
     * 自定义多种类型泛型的打印
     * @param e
     * @param x
     * @param v
     * @param <E>
     * @param <X>
     * @param <V>
     */
    public static <E,X,V> void printType(E e,X x,V v) {
        System.out.println(e + "\t" + e.getClass().getSimpleName());
        System.out.println(x + "\t" + x.getClass().getSimpleName());
        System.out.println(v + "\t" + v.getClass().getSimpleName());
    }

    private static int getIdx(int size) {
        return new Random().nextInt(size);
    }


    public static void main(String[] args) {
        Producer<Integer> producer = new Producer<>();
        producer.setProducer(100);
        System.out.println("使用泛型类获取结果="+producer.getProducer());
        System.out.println("---------------");
        ArrayList<String> strList = new ArrayList<>();
        strList.add("计算机");
        strList.add("计算机数据结构");
        strList.add("计算机组成原理");
        strList.add("计算机网络");
        String p1 = producer.getProducer(strList);
        System.out.println("使用泛型方法获取str结果="+p1 + ",class="+p1.getClass());
        ArrayList<Integer> intList = new ArrayList<>();
        intList.add(1000);
        intList.add(3000);
        intList.add(5000);
        intList.add(1000);
        Integer p2 = producer.getProducer(intList);
        System.out.println("使用泛型方法获取int结果="+p2 +",class="+p2.getClass());
        System.out.println("---------------");
        Integer p3 = Producer.getProducer(1, 2, 3, 4, 5, 6, 7);
        System.out.println(p3);
        String p4 = Producer.getProducer("a","b","c","d","e");
        System.out.println(p4);

        Producer.printType(1,3,4);
        Producer.printType("aa",true,100);


    }

}
