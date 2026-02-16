package hashmap;

import java.util.Collection;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int size;
    private double maxLoadFactor;

    /* Maintain keys for keySet() + iterator() */
    private HashSet<K> keys;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        size = 0;
        maxLoadFactor = maxLoad;
        keys = new HashSet<>();
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
        Collection<Node>[] table = (Collection<Node>[]) new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    /** Clears map */
    @Override
    public void clear() {
        buckets = createTable(DEFAULT_INITIAL_SIZE);
        size = 0;
        keys.clear();
    }

    /** Number of mappings */
    @Override
    public int size() {
        return size;
    }

    /** Returns bucket index */
    private int getBucketIndex(K key) {
        int hash = key.hashCode();
        return Math.floorMod(hash, buckets.length);
    }

    /** Returns true if key exists */
    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /** Returns value for key */
    @Override
    public V get(K key) {
        int index = getBucketIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    /** Inserts or updates mapping */
    @Override
    public void put(K key, V value) {
        int index = getBucketIndex(key);

        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                node.value = value; // Replace existing
                return;
            }
        }

        Node newNode = createNode(key, value);
        buckets[index].add(newNode);
        size++;
        keys.add(key);

        if ((double) size / buckets.length > maxLoadFactor) {
            resize(buckets.length * 2);
        }
    }

    /** Resizes table */
    private void resize(int newCapacity) {
        Collection<Node>[] newBuckets = createTable(newCapacity);

        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                int newIndex = Math.floorMod(node.key.hashCode(), newCapacity);
                newBuckets[newIndex].add(node);
            }
        }

        buckets = newBuckets;
    }

    /** Returns set of keys */
    @Override
    public Set<K> keySet() {
        return keys;
    }

    /** Unsupported remove */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /** Iterator over keys */
    @Override
    public Iterator<K> iterator() {
        return keys.iterator();
    }
}
