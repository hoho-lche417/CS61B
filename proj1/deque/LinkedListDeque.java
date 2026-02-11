package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A doubly-linked list implementation of a deque using a circular sentinel node.
 *
 * @param <T> element type
 */
public class LinkedListDeque<T> implements Iterable<T> {

    /** Sentinel node of the circular doubly linked list. */
    private final Node sentinel;

    /** Number of elements in the deque. */
    private int size;

    /** Doubly-linked list node. */
    private class Node {
        T item;
        Node prev;
        Node next;

        Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    /** Creates an empty linked list deque. */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    /** Adds an item to the front of the deque. */
    public void addFirst(T item) {
        Node first = sentinel.next;
        Node newNode = new Node(item, sentinel, first);
        sentinel.next = newNode;
        first.prev = newNode;
        size++;
    }

    /** Adds an item to the back of the deque. */
    public void addLast(T item) {
        Node last = sentinel.prev;
        Node newNode = new Node(item, last, sentinel);
        last.next = newNode;
        sentinel.prev = newNode;
        size++;
    }

    /** Returns true if the deque is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the number of items in the deque. */
    public int size() {
        return size;
    }

    /** Prints the deque from first to last. */
    public void printDeque() {
        Node current = sentinel.next;
        while (current != sentinel) {
            System.out.print(current.item + " ");
            current = current.next;
        }
        System.out.println();
    }

    /** Removes and returns the first item, or null if empty. */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node first = sentinel.next;
        T item = first.item;

        sentinel.next = first.next;
        first.next.prev = sentinel;

        first.next = null; // Avoid loitering
        first.prev = null;
        first.item = null;

        size--;
        return item;
    }

    /** Removes and returns the last item, or null if empty. */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node last = sentinel.prev;
        T item = last.item;

        sentinel.prev = last.prev;
        last.prev.next = sentinel;

        last.next = null; // Avoid loitering
        last.prev = null;
        last.item = null;

        size--;
        return item;
    }

    /** Gets the item at the given index, or null if invalid. */
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.item;
    }

    /** Gets the item at the given index using recursion. */
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }

    /**
     * Recursive helper for getRecursive.
     *
     * @param node current node
     * @param index remaining index
     * @return item at index
     */
    private T getRecursiveHelper(Node node, int index) {
        if (index == 0) {
            return node.item;
        }
        return getRecursiveHelper(node.next, index - 1);
    }

    /** Returns an iterator over the deque. */
    @Override
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    /** Iterator implementation. */
    private class DequeIterator implements Iterator<T> {
        private Node current = sentinel.next;

        @Override
        public boolean hasNext() {
            return current != sentinel;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T item = current.item;
            current = current.next;
            return item;
        }
    }

    /** Compares this deque with another object. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LinkedListDeque<?> other)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }

        Node p1 = this.sentinel.next;
        Node p2 = other.sentinel.next;

        while (p1 != sentinel) {
            if (!p1.item.equals(p2.item)) {
                return false;
            }
            p1 = p1.next;
            p2 = p2.next;
        }
        return true;
    }
}
