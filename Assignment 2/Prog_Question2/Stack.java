import java.util.Objects;

public class Stack<E> {
    private SinglyLinkedList<E> list = new SinglyLinkedList<>();

    public Stack() {
    }

    public void push(E e) {
        list.addFirst(e);
    }

    public E pop() {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Stack is empty");
        }
        return list.removeFirst();
    }

    public void print() {
        if (list.isEmpty()) {
            System.out.println("The stack is empty.");
            return;
        }
        list.printList();
        System.out.println();
        System.out.println("Finished printing.");
        System.out.println();
    }

    public E peek() {
        return list.getHead();
    }

    public E secondPeek() {
        E top = this.pop();
        E second = this.peek();
        push(top);
        return second;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
