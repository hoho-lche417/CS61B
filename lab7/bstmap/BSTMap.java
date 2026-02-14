package bstmap;

import java.util.Iterator;
import java.util.Set;

/**
 * BST-based Map implementation.
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;

        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private BSTNode root;
    private int size;

    /** Creates an empty BSTMap. */
    public BSTMap() {
        root = null;
        size = 0;
    }

    /** Removes all mappings. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /** Returns number of key-value pairs. */
    @Override
    public int size() {
        return size;
    }

    /** Returns true if key exists. */
    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode node, K key) {
        if (node == null) return false;

        int cmp = key.compareTo(node.key);

        if (cmp < 0) return containsKey(node.left, key);
        if (cmp > 0) return containsKey(node.right, key);
        return true;
    }

    /** Returns value associated with key. */
    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);

        if (cmp < 0) return get(node.left, key);
        if (cmp > 0) return get(node.right, key);
        return node.value;
    }

    /** Associates value with key. */
    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            size++;
            return new BSTNode(key, value);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value; // Replace existing value
        }

        return node;
    }

    /** Prints keys in sorted order. */
    public void printInOrder() {
        printInOrder(root);
        System.out.println();
    }

    private void printInOrder(BSTNode node) {
        if (node == null) return;

        printInOrder(node.left);
        System.out.print(node.key + " ");
        printInOrder(node.right);
    }

    /** Unsupported operations (Lab 7 spec). */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}

