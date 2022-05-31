package com.ouyangliuy.GenericityTest;

public class Plate <T>{
    public T item;

    public Plate(T item) {
        this.item = item;
    }
    public void setItem(T t){
        this.item = t;
    }

    public T getItem() {
        return item;
    }
}
