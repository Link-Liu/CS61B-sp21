package bstmap;

import java.security.Key;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K, V> {
    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;

        private BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
        /*return 2 if both R and L exist,return 0 if both R and L don't exist,-1 if only L exist,1 if only R exist*/
        private int getChildNumber() {
            if (left != null && right != null) {return 2;}
            else if (right != null && left == null) {return 1;}
            else if (left == null && right == null) {return 0;}
            return -1;
        }

    }
    private BSTNode root;
    private int size;
    /** Removes all of the mappings from this map. */
    public void clear() {
        root = null;
        size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {return containsKeyHelper(root, key);}

    private boolean containsKeyHelper(BSTNode bstNode, K key) {
        if (bstNode == null) {return false;}
        if (bstNode.key.compareTo(key) == 0) {return true;}
        if (bstNode.key.compareTo(key) > 0) {return containsKeyHelper(bstNode.left, key);}
        return containsKeyHelper(bstNode.right, key);
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {return getHelper(root, key);}

    private V getHelper(BSTNode bstNode, K key) {
        if (bstNode == null) {return null;}
        if (bstNode.key.compareTo(key) == 0) {return bstNode.value;}
        if (bstNode.key.compareTo(key) < 0) {return getHelper(bstNode.right, key);}
        return getHelper(bstNode.left, key);
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {return size;}

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {root = putHelper(root, key, value);size++;}

    private BSTNode putHelper(BSTNode bstNode, K key, V value) {
        if (bstNode == null) {return new BSTNode(key, value);}
        int cmp = bstNode.key.compareTo(key);
        if (cmp < 0) {bstNode.right = putHelper(bstNode.right, key, value);}
        else if (cmp > 0) {bstNode.left = putHelper(bstNode.left, key, value);}
        else {bstNode.value = value;}
        return bstNode;
    }

    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        addKeys(root, set);
        return set;
    }

    private void addKeys(BSTNode node, Set<K> set) {
        if (node == null) {
            return;
        }
        set.add(node.key);
        addKeys(node.left, set);
        addKeys(node.right, set);
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        if (containsKey(key)) {
            V value = get(key);
            root = removeHelper(root, key);
            size--;
            return value;
        }
        return null;
    }

    private BSTNode removeHelper(BSTNode bstNode, K key) {
        if (bstNode == null) {return null;}
        if (bstNode.key.compareTo(key) < 0) {bstNode.right =  removeHelper(bstNode.right, key);}
        else if (bstNode.key.compareTo(key) > 0) {bstNode.left =  removeHelper(bstNode.left, key);}
        else {
            if (bstNode.getChildNumber() == 0) {bstNode = null;}
            else if (bstNode.getChildNumber() == 1) {return bstNode.right;}
            else if (bstNode.getChildNumber() == -1) {return bstNode.left;}
            else {
                BSTNode originalNode = bstNode;
                bstNode = getLeftBiggestChild(originalNode.left);
                bstNode.right = originalNode.right;
                bstNode.left = removeHelper(originalNode.left, bstNode.key);
            }
        }
        return bstNode;
    }

    private BSTNode getLeftBiggestChild(BSTNode bstNode) {
        if (bstNode.right == null) {return bstNode;}
        return getLeftBiggestChild(bstNode.right);
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        if (containsKey(key)) {
            V contianedValue = get(key);
            if (contianedValue != null && contianedValue.equals(value)) {
                root = removeHelper(root, key);
                size--;
                return value;
            }
        }
        return null;
    }

    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.key.toString() + " -> " + node.value.toString());
        printInOrder(node.right);
    }

}
