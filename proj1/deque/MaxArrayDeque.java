package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        } else {
            int maxIndex = 0;
            for (int i = 0; i < size(); i++) {
                if (0 < c.compare(get(i), (get(maxIndex)))) {
                    maxIndex = i;
                }
            }
            return (T) get(maxIndex);
        }
    }

    public T max() {
        return max(comparator);
    }
}
