package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.Stopwatch;
import java.util.Random;

/**
 * Basic tests for ArrayDeque.
 */
public class ArrayDequeTest {

    @Test
    public void testAddIsEmptySize() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());

        deque.addFirst(10);
        assertFalse(deque.isEmpty());
        assertEquals(1, deque.size());

        deque.addLast(20);
        assertEquals(2, deque.size());

        assertEquals(Integer.valueOf(10), deque.get(0));
        assertEquals(Integer.valueOf(20), deque.get(1));
    }

    @Test
    public void testRemove() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        assertNull(deque.removeFirst());
        assertNull(deque.removeLast());

        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        assertEquals(Integer.valueOf(1), deque.removeFirst());
        assertEquals(Integer.valueOf(3), deque.removeLast());
        assertEquals(1, deque.size());
        assertEquals(Integer.valueOf(2), deque.get(0));
    }

    @Test
    public void testWrapAround() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < 20; i++) {
            deque.addLast(i);
        }

        for (int i = 0; i < 15; i++) {
            assertEquals(Integer.valueOf(i), deque.removeFirst());
        }

        for (int i = 100; i < 110; i++) {
            deque.addLast(i);
        }

        assertEquals(Integer.valueOf(15), deque.get(0));
    }

    @Test
    public void testResizeDown() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }

        for (int i = 0; i < 95; i++) {
            deque.removeFirst();
        }

        assertEquals(5, deque.size()); // should be 16
        assertEquals(Integer.valueOf(95), deque.get(0));
    }

    @Test
    public void testCrossImplementationEquals() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        LinkedListDeque<Integer> linkedDeque = new LinkedListDeque<>();

        for (int i = 0; i < 10; i++) {
            arrayDeque.addLast(i);
            linkedDeque.addLast(i);
        }

        assertTrue(arrayDeque.equals(linkedDeque));
        assertTrue(linkedDeque.equals(arrayDeque));
    }

    @Test
    public void testGetEdgeCases() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        assertNull(deque.get(0));
        deque.addLast(42);

        assertNull(deque.get(-1));
        assertNull(deque.get(1));
        assertEquals(Integer.valueOf(42), deque.get(0));
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> studentDeque = new ArrayDeque<>();
        java.util.ArrayDeque<Integer> referenceDeque = new java.util.ArrayDeque<>();
        Random rand = new Random();

        for (int i = 0; i < 10000; i++) {
            int operation = rand.nextInt(4);

            switch (operation) {
                case 0 -> { // addFirst
                    int value = rand.nextInt(1000);
                    studentDeque.addFirst(value);
                    referenceDeque.addFirst(value);
                }
                case 1 -> { // addLast
                    int value = rand.nextInt(1000);
                    studentDeque.addLast(value);
                    referenceDeque.addLast(value);
                }
                case 2 -> { // removeFirst
                    Integer expected = referenceDeque.pollFirst();
                    Integer actual = studentDeque.removeFirst();
                    assertEquals(expected, actual);
                }
                case 3 -> { // removeLast
                    Integer expected = referenceDeque.pollLast();
                    Integer actual = studentDeque.removeLast();
                    assertEquals(expected, actual);
                }
            }

            assertEquals(referenceDeque.size(), studentDeque.size());
        }
    }

    @Test
    public void timingTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        Stopwatch sw = new Stopwatch();

        int N = 1_000_000;

        for (int i = 0; i < N; i++) {
            deque.addLast(i);
        }

        for (int i = 0; i < N; i++) {
            deque.removeLast();
        }

        double elapsed = sw.elapsedTime();
        System.out.println("Timing test (add/remove " + N + "): " + elapsed + " seconds");
    }
}

