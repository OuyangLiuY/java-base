package utils;




import com.ouyangliuy.jvm.agent.ObjectSizeAgent;

import java.util.*;

/**
 * 执行该方法需要添加Agent参数
 * 指定当前ObjectSizeAgent.jar的路径
 * -javaagent:E:\workspace\java-base\common\lib\ObjectSizeAgent.jar
 */
public class ObjectSize {

    public static void main(String[] args) {
//        System.out.println(getObjectSize(new Object()));
//        System.out.println(getObjectSize(new int[]{}));
        System.out.println(getObjectSize(new HashMap<>()));
//        System.out.println(getObjectSize(new P()));
        System.out.println(getObjectSize(new MyHashMap()));
        System.out.println(getObjectSize(new HashSet<>()));
    }

    public static long getObjectSize(Object o){
        return ObjectSizeAgent.sizeOf(o);
    }

    static class P{
                        //8 _markword
                        //4 _oop指针
        int id;         //4
        String name;    //4
        int age;        //4

        byte b1;        //1
        byte b2;        //1

        Object o;       //4
        byte b3;        //1
    }

    static class MyHashMap{


        static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

        static final int MAXIMUM_CAPACITY = 1 << 30;

        static final float DEFAULT_LOAD_FACTOR = 0.75f;

        static final int TREEIFY_THRESHOLD = 8;
        static final int UNTREEIFY_THRESHOLD = 6;

        static final int MIN_TREEIFY_CAPACITY = 64;

        private static final long serialVersionUID = 362498820763181265L;

        transient int size;

        transient int modCount;

        int threshold;
        float loadFactor;
        transient MyHashMap.Node[] table;


//        transient Set entrySet;

        static class Node<K,V> implements Map.Entry<K,V> {
            final int hash;
            final K key;
            V value;
            Node<K,V> next;

            Node(int hash, K key, V value, Node<K,V> next) {
                this.hash = hash;
                this.key = key;
                this.value = value;
                this.next = next;
            }

            public final K getKey()        { return key; }
            public final V getValue()      { return value; }
            public final String toString() { return key + "=" + value; }

            public final int hashCode() {
                return Objects.hashCode(key) ^ Objects.hashCode(value);
            }

            public final V setValue(V newValue) {
                V oldValue = value;
                value = newValue;
                return oldValue;
            }

            public final boolean equals(Object o) {
                if (o == this)
                    return true;
                if (o instanceof Map.Entry) {
                    Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                    if (Objects.equals(key, e.getKey()) &&
                            Objects.equals(value, e.getValue()))
                        return true;
                }
                return false;
            }
        }


    }


}
