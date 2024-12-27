package deque;


import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private Node prev;
        private Node next;
        private T data;

        Node(T x, Node p, Node n) {
            data = x;
            prev = p;
            next = n;
            if (n != null) {
                n.prev = this;
            }
            if (p != null) {
                p.next = this;
            }
        }

        @Override
        public String toString() {
            if (data == null) {
                return "null";
            } else {
                return data.toString();
            }
        }
    }

    /* The first item (if it exists) is at sentinel.next. */
    private Node sentFront;
    private int size;
    private Node sentBack;

    public LinkedListDeque() {
        sentFront = new Node(null, null, null);
        sentBack = new Node(null, sentFront, null);
        sentFront.next = sentBack;
        sentBack.prev = sentFront;
        size = 0;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void addFirst(T item) {
        sentFront.next = new Node(item, sentFront, sentFront.next);
        size++;
    }
    @Override
    public void addLast(T item) {
        sentBack.prev = new Node(item, sentBack.prev, sentBack);
        size++;
    }
    @Override
    public void printDeque() {
        String[] deque = new String[size];
        int i = 0;
        Node cur = sentFront.next;
        while (cur != sentBack) {
            deque[i] = cur.data.toString();
            cur = cur.next;
            i++;
        }
        System.out.println(String.join(" ", deque));
    }
    @Override
    public T removeFirst() {
        if (sentFront.next == sentBack) {
            return null;
        } else {
            T value = sentFront.next.data;
            sentFront.next = sentFront.next.next;
            sentFront.next.prev = sentFront;
            size--;
            return value;
        }
    }
    @Override
    public T removeLast() {
        if (sentBack.prev == sentFront) {
            return null;
        } else {
            T value = sentBack.prev.data;
            sentBack.prev = sentBack.prev.prev;
            sentBack.prev.next = sentBack;
            size--;
            return value;
        }
    }
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node cur = sentFront.next;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur.data;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentFront.next);
    }

    private T getRecursiveHelper(int index, Node node) {
        if (index == 0) {
            return node.data;
        }
        return getRecursiveHelper(index - 1, node.next);
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node p;

        LinkedListDequeIterator() {
            p = sentFront.next;
        }

        public boolean hasNext() {
            return !(p == sentBack);
        }

        public T next() {
            T data = p.data;
            p = p.next;
            return data;
        }
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
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
            if (size != other.size()) {
                return false;
            } else {
                Node cur = sentFront.next;
                for (int i = 0; i < size; i++) {
                    if (!cur.data.equals(other.get(i))) {
                        return false;
                    }
                    cur = cur.next;
                }
                return true;
            }
        }
    }
}
