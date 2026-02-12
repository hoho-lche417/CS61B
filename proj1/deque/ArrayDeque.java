package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Array-based deque implementation using a circular buffer.
 *
 * @param <T> element type
 */
public class ArrayDeque<T> implements Iterable<T> {

    private static final int INITIAL_CAPACITY = 8;

    private T[] items;
    private int size;

    /** Index before the first element */
    private int nextFirst;

    /** Index after the last element */
    private int nextLast;

    /** Creates an empty array deque. */
    public ArrayDeque() {
        items = (T[]) new Object[INITIAL_CAPACITY];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    /** Adds an item to the front. */
    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size++;
    }

    /** Adds an item to the back. */
    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
        nextLast = plusOne(nextLast);
        size++;
    }

    /** Returns true if empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns number of elements. */
    public int size() {
        return size;
    }

    /** Prints deque from first to last. */
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    /** Removes and returns first item, or null if empty. */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        int firstIndex = plusOne(nextFirst);
        T item = items[firstIndex];
        items[firstIndex] = null; // Avoid loitering
        nextFirst = firstIndex;
        size--;
        checkShrink();
        return item;
    }

    /** Removes and returns last item, or null if empty. */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int lastIndex = minusOne(nextLast);
        T item = items[lastIndex];
        items[lastIndex] = null; // Avoid loitering
        nextLast = lastIndex;
        size--;
        checkShrink();
        return item;
    }

    /** Gets item at index, or null if invalid. */
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int start = plusOne(nextFirst);
        int actualIndex = (start + index) % items.length;
        return items[actualIndex];
    }

    /** Circular increment. */
    private int plusOne(int index) {
        return (index + 1) % items.length;
    }

    /** Circular decrement. */
    private int minusOne(int index) {
        return (index - 1 + items.length) % items.length;
    }

    /** Resizes underlying array. */
    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int start = plusOne(nextFirst);

        for (int i = 0; i < size; i++) {
            newItems[i] = items[(start + i) % items.length];
        }

        items = newItems;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    /** Shrinks array if usage factor < 25% and capacity >= 16. */
    private void checkShrink() {
        int capacity = items.length;
        if (capacity >= 16 && size < capacity / 4) {
            resize(capacity / 2);
        }
    }

    /** Returns iterator. */
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos = 0;

        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T item = get(pos);
            pos++;
            return item;
        }
    }

    /** Equality based on contents. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayDeque<?>)) return false;

        ArrayDeque<T> other = (ArrayDeque<T>) o;
        if (this.size != other.size) return false;

        for (int i = 0; i < size; i++) {
            T a = this.get(i);
            Object b = other.get(i);
            if (!a.equals(b)) return false;
        }
        return true;
    }
}

