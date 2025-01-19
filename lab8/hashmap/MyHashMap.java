package hashmap;

import java.lang.reflect.Array;
import java.security.Key;
import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private double loadFactor;
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_INITAL_LOADFACTOR = 0.75;
    private int size;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_INITAL_LOADFACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_INITAL_LOADFACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.buckets = createTable(initialSize);
        this.loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    public void clear() {
        size = 0;
        buckets = createTable(DEFAULT_INITIAL_SIZE);
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V get(K key) {
        Node lookedNode = getNode(key);
        if (lookedNode == null) {
            return null;
        } else {
            return lookedNode.value;
        }
    }

    public int size() {
        return size;
    }

    public void put(K key, V value) {
        int bucketIndex = getKeyIndex(key);
        Node node = getNode(key, bucketIndex);
        if (node != null) {
            node.value = value;
            return;
        }
        node = createNode(key, value);
        buckets[bucketIndex].add(node);
        size++;
        if (needResize()) {
            resize(buckets.length * 2);
        }
    }

    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (K ket : this) {
            keySet.add(ket);
        }
        return keySet;
    }

    public V remove(K key) {
        int bucketIndex = getKeyIndex(key);
        Node node = getNode(key, bucketIndex);
        if (node == null) {
            return null;
        }
        V value = node.value;
        buckets[bucketIndex].remove(node);
        size--;
        return value;
    }

    public V remove(K key, V value) {
        int bucketIndex = getKeyIndex(key);
        Node node = getNode(key, bucketIndex);
        if (node == null || !node.value.equals(value)) {
            return null;
        }
        size -= 1;
        buckets[bucketIndex].remove(node);
        return node.value;
    }

    private boolean needResize() {
        double loadFactor = size() / (double) buckets.length;
        return loadFactor > DEFAULT_INITAL_LOADFACTOR;
    }

    private void resize(int newSize) {
        Collection<Node>[] newBuckets = createTable(newSize);
        Iterator<Node> nodeIterator = new MyHashMapNodeIterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            int bucketIndex = getKeyIndex(node.key, newBuckets);
            newBuckets[bucketIndex].add(node);
        }
        buckets = newBuckets;
    }


    private Node getNode(K key) {
        int bucketIndex = getKeyIndex(key);
        return getNode(key, bucketIndex);
    }

    private Node getNode(K key, int bucketIndex) {
        Node LookedNode = null;
        for(Node node : buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                LookedNode = node;
            }
        }
        return LookedNode;
    }

    private int getKeyIndex(K key) {
        return getKeyIndex(key, buckets);
    }

    private int getKeyIndex(K key, Collection<Node>[] table) {
        int keyHash = key.hashCode();
        return Math.floorMod(keyHash, table.length);
    }

    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    public class MyHashMapIterator implements Iterator<K> {
        private final Iterator<Node> nodeIterator= new MyHashMapNodeIterator();

        public boolean hasNext() {
            return nodeIterator.hasNext();
        }

        public K next() {
            return nodeIterator.next().key;
        }

    }

    private class MyHashMapNodeIterator implements Iterator<Node> {
        private final Iterator<Collection<Node>> bucketsIterator = Arrays.stream(buckets).iterator();
        private int sizeLift = size();
        private Iterator<Node> curIterator;

        public boolean hasNext() {
            return sizeLift > 0;
        }

        public Node next() {
            if (curIterator == null || !curIterator.hasNext()) {
                Collection<Node> curBucket = bucketsIterator.next();
                while (curBucket.isEmpty()) {
                    curBucket = bucketsIterator.next();
                }
                curIterator = curBucket.iterator();
            }
            sizeLift--;
            return curIterator.next();
        }
    }
}
