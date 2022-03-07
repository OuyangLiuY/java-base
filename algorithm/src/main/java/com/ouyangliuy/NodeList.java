package com.ouyangliuy;

public class NodeList {


    static class Node {
        public int value;
        public Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    static class DoubleNode {
        public int value;
        public DoubleNode next;
        public DoubleNode pre;

        public DoubleNode(int value) {
            this.value = value;
        }
    }

    //       a->b->c->null
    // null<-a<-b<-c
    public static Node reverseList(Node head) {
        Node pre = null;
        Node next = null;
        while (head != null) {
            next = head.next; // next用于遍历
            head.next = pre; // 断链，并将pre赋予head的next
            pre = head;
            head = next;
        }

        return pre;
    }

    //       a->b->c->null
    // null<-a<-b<-c
    public static DoubleNode reverseDouList(DoubleNode head) {
        DoubleNode pre = null;
        DoubleNode next = null;
        while (head != null) {
            next = head.next; // next用于遍历
            head.next = pre; // 断链，并将pre赋予head的next
            head.pre = head;
            pre = head;
            head = next;
        }

        return pre;
    }

    private static void println(Node head) {

        while (head != null) {
            System.out.print(head.value);
            head = head.next;
            if (head != null)
                System.out.print("->");
        }
        System.out.println();
    }

    private static void println(DoubleNode head) {

        while (head != null) {
            System.out.print(head.value);
            head = head.next;
            if (head != null)
                System.out.print("<->");
        }
        System.out.println();
    }

    private  static Node removeNode(Node head,int num){
        // 找到第一个不需要删除头部
        while (head != null){
            if(head.value != num)
                break;
            head = head.next;
        }
        Node pre =head;
        Node cur =head;
        while (cur != null){
            if(cur.value == num)
                pre.next = cur.next;
            else
                pre = cur; // 说明我pre走到哪一个位置了
            cur = cur.next;
        }
        return head;
    }

    public static void main(String[] args) {
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node4 = new Node(4);
        Node node5 = new Node(5);
        Node node6 = new Node(6);
        Node node7 = new Node(7);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        node5.next = node6;
        node6.next = node7;
        println(node1);
//        Node node = reverseList(node1);
//        println(node);
        Node node = removeNode(node1, 3);
        println(node);

        DoubleNode doubleNode1 = new DoubleNode(1);
        DoubleNode doubleNode2 = new DoubleNode(2);
        DoubleNode doubleNode3 = new DoubleNode(3);
        DoubleNode doubleNode4 = new DoubleNode(4);
        doubleNode1.next = doubleNode2;
        doubleNode1.pre = null;
        doubleNode2.next = doubleNode3;
        doubleNode2.pre = doubleNode1;

        doubleNode3.next = doubleNode4;
        doubleNode3.pre = doubleNode2;

        doubleNode4.next = null;
        doubleNode4.pre = doubleNode3;

        println(doubleNode1);
        DoubleNode reverseDouList = reverseDouList(doubleNode1);
        println(reverseDouList);

    }

}
