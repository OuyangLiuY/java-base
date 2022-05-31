package com.ouyangliuy.GenericityTest;

public class PlateMeat<T extends Food.Meat> {
    public T item;

    public PlateMeat(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
