public class SinglyLinkedList<E> {

    //Node class
    private static class Node<E> {
        private E element;
        private Node<E> next;
        public Node(E e, Node<E> n) {
            element = e;
            next = n;
        }
        public E getElement(){ return element; }
        public Node<E> getNext() { return next; }
        public void setNext(Node<E> n) { next = n; }
    }

    //instance variable
    private Node<E> head = null;
    private Node<E> tail = null;
    private int size = 0;

    //constructor
    public SinglyLinkedList(){}
    //access methods
    public int getSize() { return size; }
    public boolean isEmpty() { return size == 0; }
    public E getHead() {
        if (isEmpty()) return null;
        return head.getElement();
    }
    public E getTail() {
        if (isEmpty()) return null;
        return tail.getElement();
    }

    //update method
    public void addFirst(E e) {
        head = new Node<>(e, head);
        if (size == 0)
            tail = head;
        size++;
    }
    public void addLast(E e) {
        Node<E> n = new Node<>(e, null);
        if (isEmpty())
            head = n;
        else
            tail.setNext(n);
        tail = tail.next;
        size++;
    }
    public E removeFirst() {
        if (isEmpty()) return null;
        E toRemove = head.getElement();
        head = head.next;
        size--;
        if (size == 0)
            tail = null;
        return toRemove;
    }

    //print method
    public void printList() {
        Node<E> i = head;
        while (i != null) {
            System.out.println(i.getElement());
            i = i.getNext();
        }
    }
}
