import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A HashMap implementation using open-chaining to resolve collisions
 */
public class HashMap<K, V> {
    private final int size;
    private final Object[] hashTable;

    HashMap(int size) {
        this.size = Math.max(1, size);
        this.hashTable = new Object[this.size];
    }

    /**
     * put the pair "key, value" into the map
     * @param key: the given key
     * @param value: the given value
     */
    public void put(K key, V value) {
        int index = getIndex(key);
        List<Entry<K, V>> list = (List<Entry<K, V>>) hashTable[index];
        if (list == null) {
            list = new LinkedList<>();
            hashTable[index] = list;
        } else {
            for (Entry<K, V> e : list) {
                if (e.key.equals(key)) {
                    e.value = value;
                    return;
                }
            }
        }
        list.add(new Entry<>(key, value));
    }

    /**
     * get the value associated with the given key
     * @param key: the given key
     * @return the value of the given key if any, return null otherwise
     */
    public V get(K key) {
        int index = getIndex(key);
        if (hashTable[index] != null) {
            List<Entry<K, V>> list = (List<Entry<K, V>>) hashTable[index];
            for (Entry<K, V> e : list) {
                if (e.key.equals(key)) {
                    return e.value;
                }
            }
        }
        return null;
    }

    /**
     * get the index of the key in the hash table
     * @param key: the given key
     * @return the index of the key in the hash table
     */
    public int getIndex(K key) {
        int index = key.hashCode() % size;
        if (index < 0) {
            index = index + size;
        }
        return index;
    }

    /**
     * the inner class Entry, containing a pair "key, value"
     */
    static class Entry<K, V> {
        final K key;
        V value; // value can be updated

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Method "getKeys", return keys of the map
     */
    List<K> getKeys() {
        List<K> keys = new ArrayList<>();
        for (int i = 0; i < hashTable.length; i++) {
            if (hashTable[i] != null) {
                List<Entry<K, V>> list = (List<Entry<K, V>>) hashTable[i];
                for (Entry<K, V> e : list) {
                    keys.add(e.key);
                }
            }
        }
        return keys;
    }
}
