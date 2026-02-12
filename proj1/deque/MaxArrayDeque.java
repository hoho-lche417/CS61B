package deque;

import java.util.Comparator;

/**
 * Extension of ArrayDeque that can return the maximum element
 * according to a Comparator.
 *
 * @param <T> element type
 */
public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<T> comparator;

    /**
     * Creates a MaxArrayDeque with the given Comparator.
     *
     * @param c comparator used for max()
     */
    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }

    /**
     * Returns the maximum element using the constructor Comparator.
     */
    public T max() {
        return max(this.comparator);
    }

    /**
     * Returns the maximum element using the provided Comparator.
     */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        T best = get(0);

        for (T item : this) {
            if (c.compare(item, best) > 0) {
                best = item;
            }
        }

        return best;
    }

    /**
     * equals() not required by spec — defer to ArrayDeque behavior
     * or keep default Object implementation.
     */
}

