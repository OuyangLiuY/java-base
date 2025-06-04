package com.ouyangliuy.disruptor;

public class LongEvent<T> {
    private T value;

    public void set(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LongEvent{" +
                "value=" + value +
                "}";
    }
}