package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> noResizingList = new AListNoResizing<>();
        BuggyAList<Integer> buggyList = new BuggyAList<>();
        noResizingList.addLast(4);
        noResizingList.addLast(5);
        noResizingList.addLast(6);
        buggyList.addLast(4);
        buggyList.addLast(5);
        buggyList.addLast(6);

        assertEquals(noResizingList.size(), buggyList.size());
        Assert.assertEquals(noResizingList.removeLast(), buggyList.removeLast());
        Assert.assertEquals(noResizingList.removeLast(), buggyList.removeLast());
        Assert.assertEquals(noResizingList.removeLast(), buggyList.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size1 = L.size();
                int size2 = B.size();
                System.out.println("size1: " + size1 + " size2: " + size2);
                Assert.assertEquals(size1, size2);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() == 0) {
                    continue;
                }
                int last1 = L.getLast();
                int last2 = B.getLast();
                System.out.println("added last1: " + last1 + " added last2: " + last2);
                Assert.assertEquals(last1, last2);
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() == 0) {
                    continue;
                }
                int last1 = L.removeLast();
                int last2 = B.removeLast();
                System.out.println("removed last1: " + last1 + " removed last2: " + last2);
                Assert.assertEquals(last1, last2);
            }
        }

    }
}
