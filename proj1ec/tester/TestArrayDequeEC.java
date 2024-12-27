package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        ArrayDequeSolution<Integer> R = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> B = new StudentArrayDeque<>();
        int N = 5000;
        String message = "\n";
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                R.addLast(randVal);
                B.addLast(randVal);
                message += String.format("addFirst(%d)\n", randVal);
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                R.addLast(randVal);
                B.addLast(randVal);
                message += String.format("addLast(%d)\n", randVal);
            } else if (operationNumber == 2) {
                // removeLast
                if (R.size() == 0) {
                    continue;
                }
                int last1 = R.removeLast();
                int last2 = B.removeLast();
                message += "removeLast()\n";
                assertEquals(message, last1, last2);
            } else if (operationNumber == 3) {
                // removeFirst
                if (R.size() == 0) {
                    continue;
                }
                int last1 = R.removeFirst();
                int last2 = B.removeFirst();
                message += "removeFirst()\n";
                assertEquals(message, last1, last2);
            }
        }
    }
}
