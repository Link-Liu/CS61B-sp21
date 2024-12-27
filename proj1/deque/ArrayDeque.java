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
        T[] a = (T[]) new Object[capacity];
        int itemIdx = 0;
        for (int i = 0; i < size; i++) {
            itemIdx = (headIdx + i) % array.length;
            a[i] = array[itemIdx];
        }
        array = a;
        headIdx = 0;
        tailIdx = size;
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
        if (!(o instanceof Deque)) {
            return false;
        }
        if (o == null) {
            return false;
        } else if (o == this) {
            return true;
        } else {
            Deque<T> other = (Deque<T>) o;
            if (o instanceof ArrayDeque) {
                other = (ArrayDeque<T>) o;
            } else {
                other = (LinkedListDeque<T>) o;
            }
            if (size != other.size()) {
                return false;
            } else {
                for (int i = 0; i < size; i++) {
                    if (!(get(i).equals(other.get(i)))) {
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
