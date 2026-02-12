package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

/**
 * Basic tests for MaxArrayDeque.
 */
public class MaxArrayDequeTest {

    /** Natural order comparator */
    private static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a.compareTo(b);
        }
    }

    /** Reverse order comparator */
    private static class ReverseIntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return b.compareTo(a);
        }
    }

    /** Comparator based on absolute value */
    private static class AbsComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return Integer.compare(Math.abs(a), Math.abs(b));
        }
    }

    @Test
    public void testMaxNaturalOrder() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new IntComparator());

        assertNull(deque.max());

        deque.addLast(3);
        deque.addLast(1);
        deque.addLast(5);
        deque.addLast(2);

        assertEquals(Integer.valueOf(5), deque.max());
    }

    @Test
    public void testMaxReverseOrder() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new ReverseIntComparator());

        deque.addLast(3);
        deque.addLast(1);
        deque.addLast(5);
        deque.addLast(2);

        // Reverse comparator → smallest value is "max"
        assertEquals(Integer.valueOf(1), deque.max());
    }

    @Test
    public void testMaxWithDifferentComparator() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new IntComparator());

        deque.addLast(-10);
        deque.addLast(3);
        deque.addLast(-7);
        deque.addLast(5);

        // Natural order
        assertEquals(Integer.valueOf(5), deque.max());

        // Absolute value comparator
        assertEquals(Integer.valueOf(-10), deque.max(new AbsComparator()));
    }

    @Test
    public void testMaxSingleElement() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new IntComparator());

        deque.addFirst(42);
        assertEquals(Integer.valueOf(42), deque.max());
    }

    @Test
    public void testMaxAfterRemovals() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new IntComparator());

        deque.addLast(1);
        deque.addLast(9);
        deque.addLast(3);

        assertEquals(Integer.valueOf(9), deque.max());

        deque.removeLast(); // remove 3
        deque.removeLast(); // remove 9

        assertEquals(Integer.valueOf(1), deque.max());
    }
}
