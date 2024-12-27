package deque;

import java.util.Comparator;
import org.junit.Test;
import static org.junit.Assert.*;

public class MaxArrayDequeTest<T> {
    private class stringLenghComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            if (o1.length() > o2.length()) {
                return 1;
            }else if (o1.length() < o2.length()) {
                return -1;
            }
            return 0;
        }
    }

    private class lastIntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            int last1 = o1 % 10;
            int last2 = o2 % 10;
            if (last1 > last2) {
                return 1;
            } else if (last1 < last2) {
                return -1;
            }
            return 0;
        }
    }

    private class intcomparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (o1.intValue() > o2.intValue()) {
                return 1;
            } else if (o1.intValue() < o2.intValue()) {
                return -1;
            }
            return 0;
        }
    }

    @Test
    public void TestMaxArrayDeque() {
        MaxArrayDeque<Integer> mad;
        mad = new MaxArrayDeque<>(new lastIntComparator());
            mad.addLast(90);
            mad.addLast(19);
        assertEquals("Should have same value",(Integer) 19,mad.max());
        assertEquals("Shold have same value",(Integer) 90,mad.max(new intcomparator()));
    }

    @Test
    public void TestMinArrayDequeString() {
        MaxArrayDeque<String> mad;
        mad = new MaxArrayDeque<>(new stringLenghComparator());
        mad.addFirst( "I");
        mad.addLast("Love");
        mad.addLast("U");
        assertEquals("Should be same String","Love",mad.max());
    }

    @Test
    public void TestEqual() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new lastIntComparator());
        MaxArrayDeque<Integer> mad2 = new MaxArrayDeque<>(new intcomparator());
        for (int i = 0; i < 19; i++) {
            mad.addLast(i);
            mad2.addLast(i);
        }
        MaxArrayDeque<Integer> same = mad;
        assertEquals("Should be True",true,mad.equals(same));
        assertEquals("Should be false",false,mad.equals(mad2));
    }

}
