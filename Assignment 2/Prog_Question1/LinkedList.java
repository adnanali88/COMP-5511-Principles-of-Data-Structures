package com.example; 
public class LinkedList {
    class Node
    {
        int data;
        Node next;
        Node(int d)  { data = d;  next = null; }
    }

    Node head;

    public void push(int new_data)
    {
        Node new_node = new Node(new_data);

        new_node.next = head;

        head = new_node;
    }

    public int getCount()
    {
        Node temp = head;
        int count = 0;
        while (temp != null)
        {
            count++;
            temp = temp.next;
        }
        return count;
    }

    public int getCountRec(Node node)
    {
        // Base case
        if (node == null)
            return 0;

        return 1 + getCountRec(node.next);
    }

    public void printList() {
        Node it = head;
        while (it != null) {
            System.out.print(it.data + "   ");
            it = it.next;
        }
        System.out.println();
    }

    public static void main(String[] args)
    {
        //input a linked list
        LinkedList llist = new LinkedList();//create empty linked list
        //push elements into the linked list
        llist.push(1);
        llist.push(3);
        llist.push(1);
        llist.push(2);
        llist.push(1);

        //print the list
        System.out.println("Printing the list...");
        llist.printList();

        //count upon iterative method
        System.out.println("Count of nodes with iterative method is " +
                llist.getCount());

        //count upon recursive method
        System.out.println("Count of nodes with recursive method is " +
                llist.getCountRec(llist.head));
    }
}
