package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class ArrsyDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> lld1 = new ArrayDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String>  lld1 = new ArrayDeque<String>();
        ArrayDeque<Double>  lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when empty", null, lld1.removeFirst());
        assertEquals("Should return null when empty", null, lld1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }

    }

    @Test
    public void getTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            lld1.addLast(i);
            assertEquals("Should have the same value", i, (double) lld1.get(i), 0.0);
        }
    }

    @Test
    public void getStringTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ad1.addFirst("I");
        ad1.addLast("love");
        ad1.addLast("U");
        assertEquals("Should have the same value", "I", ad1.get(0));
        assertEquals("Should have the same value", "love", ad1.get(1));
        assertEquals("Should have the same value", "U", ad1.get(2));
    }
    @Test
    public void equalTest1() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        ArrayDeque<Integer> lld2 = new ArrayDeque<>();
        for (int i = 0; i < 10; i++) {
            lld1.addLast(i);
            lld2.addLast(i);
        }
        assertTrue(lld1.equals(lld2));
    }

    @Test
    public void equalTest2() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        ArrayDeque<Integer> lld2 = new ArrayDeque<>();
        for (int i = 0; i < 10; i++) {
            lld1.addLast(i);
            lld2.addLast(i);
        }
        assertTrue(lld1.equals(lld2));
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> R = new ArrayDeque<>();
        int N = 500000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                R.addLast(randVal);
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                R.addLast(randVal);
            } else if (operationNumber == 2) {
                // removeLast
                if (R.size() == 0) {
                    continue;
                }
                R.removeLast();
            } else if (operationNumber == 3) {
                // removeFirst
                if (R.size() == 0) {
                    continue;
                }
                R.removeFirst();
            }
        }
    }
}
