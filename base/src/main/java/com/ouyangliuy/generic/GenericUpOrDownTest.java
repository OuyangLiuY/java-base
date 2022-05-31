package com.ouyangliuy.generic;

import java.util.ArrayList;

/**
 * 对于上限通配符，要求泛型的类型，只能是实参类型，或实参类型的子类类型。
 * 对于下限通配符，要求泛型的类型，只能是实参类型，或实参类型的父类类型。
 */
public class GenericUpOrDownTest {
    public static class Animal {}

    public static class Cat extends Animal {}

    public static class BlackCat extends Cat {}

    public static class WhiteCat extends Cat {}

    public static void main(String[] args) {
        ArrayList<Animal> animals = new ArrayList<>();
        ArrayList<Cat> cats = new ArrayList<>();
        ArrayList<BlackCat> blackCats = new ArrayList<>();
        ArrayList<WhiteCat> whiteCats = new ArrayList<>();
//        cats.addAll(animals);   // 报错。参数类型上限是cat
//        upShow(animals);          // 报错。参数类型上限是cat
        cats.addAll(cats);
        cats.addAll(whiteCats);
        cats.addAll(blackCats);
        upShow(cats);
        upShow(blackCats);
        upShow(whiteCats);

        downShow(animals);
        downShow(cats);
//        downShow(blackCats);    // 报错，参数类型下限是cat

        ArrayList<? extends Cat> eCat = new ArrayList<>();
        Cat cat = eCat.get(0);
        //eCat.add(new BlackCat());
        ArrayList<? super Cat> sCat = new ArrayList<>();
        sCat.add(new BlackCat());
        sCat.add(new Cat());
        sCat.add(new WhiteCat());
        Object object = sCat.get(0);
    }

    /**
     * 泛型上限通配符，传递的集合类型，只能是Cat或Cat的子类类型。
     *
     * @param cats
     */
    private static void upShow(ArrayList<? extends Cat> cats) {
        // 报错，不能给定义上限通配符的集合添加任何元素(null除外)，即使是Cat类型
//        cats.add(new Animal());
//        cats.add(new Cat());
//        cats.add(new BlackCat());
        cats.forEach(System.out::println);
    }

    /**
     * 泛型下限通配符，要求集合元素，只能是Cat或Cat的父类类型。
     *
     * @param cats
     */
    private static void downShow(ArrayList<? super Cat> cats) {
        cats.add(new Cat());    // 不报错，可以给cat添加自己，或子类
        cats.add(new BlackCat());
        cats.add(new WhiteCat());
        cats.forEach(System.out::println);
    }

}
