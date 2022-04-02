package com.ouyangliuy.GenericityTest;

import java.util.ArrayList;
import java.util.List;

public class GenericTest {


    public static void main(String[] args) {


        List<? super Dog> dogs = new ArrayList<>();
        dogs.add(new Husky("xx", "xx", 20));
        dogs.add(new Dog("xx", "xx"));
        Husky o1 = (Husky) dogs.get(0);
        System.out.println(o1);
        System.out.println(dogs);


        List<? extends Dog> eList;
        eList = new ArrayList<>();
        Husky dog = (Husky) eList.get(0);

        System.out.println(dog);
    }

    public static class Animal {

        private String type;

        public Animal(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Animal{" +
                    "type='" + type + '\'' +
                    '}';
        }
    }

    // 狗类
    public static class Dog extends Animal {

        private String name;

        public Dog(String type, String name) {
            super(type);
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Dog{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    // 哈士奇类
    public static class Husky extends Dog {

        private Integer age;

        public Husky(String type, String name, Integer age) {
            super(type, name);
            this.age = age;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Husky{" +
                    "age=" + age +
                    '}';
        }
    }

}
