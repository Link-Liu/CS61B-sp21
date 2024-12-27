package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] array;
    private int size;
    private int headIdx;
    private int tailIdx;

    public ArrayDeque() {
        array = (T[]) new Object[8];
        headIdx = 0;
        tailIdx = 0;
    }

    private void resize(int capacity) {
        int p = headIdx;
        int n = size;
        int r;
        T[] a = (T[]) new Object[capacity];
        if (headIdx >= tailIdx) {
            r = n - p;
            System.arraycopy(array, p, a, 0, r);
            System.arraycopy(array, 0, a, r, p);
        } else {
            r = tailIdx - headIdx;
            System.arraycopy(array, p, a, 0, r);
        }
        array = a;
        headIdx = 0;
        tailIdx = n;
    }
    @Override
    public void addFirst(T item) {
        headIdx--;
        headIdx = (array.length + headIdx) % array.length;
        array[headIdx] = item;
        size++;
        if (headIdx == tailIdx) {
            resize(2 * array.length);
        }
    }
    @Override
    public void addLast(T item) {

        array[tailIdx] = item;
        size++;
        tailIdx++;
        tailIdx = (array.length + tailIdx) % array.length;
        if (tailIdx == headIdx) {
            resize(2 * array.length);
        }
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T value = array[headIdx];
        array[headIdx] = null;
        size--;
        headIdx = (headIdx + 1 + array.length) % array.length;
        checkUserate();
        return value;
    }
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        tailIdx = (tailIdx - 1 + array.length) % array.length;
        T value = array[tailIdx];
        array[tailIdx] = null;
        size--;
        checkUserate();
        return value;
    }

    private static final int CHECK_SIZE = 16;
    private void checkUserate() {
        if (array.length >= CHECK_SIZE) {
            double userate = (double) size / array.length;
            if (userate < 0.25) {
                resize(array.length / 2);
            }
        }
    }
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        } else {
            int itemIdx = (headIdx + index) % array.length;
            return array[itemIdx];
        }
    }
    @Override
    public void printDeque() {
        String[] deque = new String[size];
        for (int i = 0; i < size; i++) {
            deque[i] = array[(i + headIdx) % array.length].toString();
        }
        System.out.println(String.join(" ", deque));
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o == this) {
            return true;
        } else {
            LinkedListDeque<T> other = (LinkedListDeque<T>) o;
            if (size != other.size()) {
                return false;
            } else {
                for (int i = 0; i < size; i++) {
                    if (other.get(i) != get(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;

        ArrayDequeIterator() {
            index = 0;
        }

        public boolean hasNext() {
            return index < size;
        }

        public T next() {
            T item = get(index);
            index += 1;
            return item;
        }
    }
}
