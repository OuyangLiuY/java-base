package com.ouyangliuy.GenericityTest;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GenericErased {


    static Pair<String> pair = new Pair<>("1", "2");
    static Pair<? extends List> arrayListPair = new Pair<>(new ArrayList(), new ArrayList());
    static Pair<? extends List> linkedListPair = new Pair<>(new LinkedList(), new LinkedList());

//    public static void main(String[] args) {
//        String first = pair.getFirst();
//        System.out.println(first.getClass());
//        System.out.println(arrayListPair.getFirst().getClass());
//        System.out.println(linkedListPair.getFirst().getClass());
//    }

    public static class Pair<T> {
        private T first;
        private T second;

        public Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public T getSecond() {
            return second;
        }

        public void setFirst(T first) {
            this.first = first;
        }

        public void setSecond(T second) {
            this.second = second;
        }
    }
}

