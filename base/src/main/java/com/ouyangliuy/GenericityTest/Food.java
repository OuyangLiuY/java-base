package com.ouyangliuy.GenericityTest;

public class Food {


    static class Fruit extends  Food{}
    static class Meat extends  Food{}

    static class Apple extends  Fruit{}
    static class Banana extends  Fruit{}
    static class Pork extends  Meat{}
    static class Beef extends  Meat{}

    static class RedApple extends Apple{}
    static class GreenApple extends Apple{}


    public static void main(String[] args) {
        Plate<? super Fruit> sp  = new Plate<>(new Fruit());
        sp.setItem(new Apple());
        sp.setItem(new Banana());
        sp.setItem(new RedApple());
        sp.setItem(new GreenApple());
        Object object = sp.getItem();
        Fruit fruit = (Fruit) sp.getItem();
        System.out.println(fruit);

        Plate<? extends Meat> ex  = new Plate<>(new Meat());
        //ex.setItem(new Food()); // error
        Meat item = ex.getItem();

        PlateMeat<Meat> exx = new PlateMeat<>(new Meat());
        exx.setItem(new Pork());
        exx.setItem(new Beef());

        Meat meat = exx.getItem();

    }
}
