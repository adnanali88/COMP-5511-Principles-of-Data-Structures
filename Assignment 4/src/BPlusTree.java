import java.util.ArrayList;
import java.util.List;

public class BPlusTree<K extends Comparable<K>, V> {
    private Node<K, V> root;
    private final int maxKeySize;

    public BPlusTree(int maxKeySize) {
        if (maxKeySize < 1) {
            throw new IllegalArgumentException("max key size must be at least 1; got " + maxKeySize);
        }
        this.root = new LeafNode<>();
        this.maxKeySize = maxKeySize;
    }

    /**
     * Adds a pair of key and value to the tree. The value is first written to disk, then we add the file-position
     * to the leaf node.
     *
     * @param k
     * @param v
     */
    public void add(K k, V v) {
        SplitResult<K, V> result = root.addKeyValue(k, v, maxKeySize);
        if (result != null) {
            InternalNode<K, V> newNode = new InternalNode<>(new ArrayList<K>(), newList(root));
            newNode.addNewNodeAndSplitIfNeeded(result.newKey, result.newNode, maxKeySize);
            root = newNode;
        }
    }

    /**
     * Find elements of the given key. This is used to query places AT a coordinate
     *
     * @param k
     * @return
     */
    public List<V> find(K k) {
        return root.find(k);
    }

    /**
     * Find elements in the given range. This is used to query places WITHIN two coordinates
     *
     * @param lower
     * @param upper
     * @return
     */
    public List<V> findRange(K lower, K upper) {
        return root.findRange(lower, upper);
    }

    private static <E> List<E> split(List<E> input, int retainingSize) {
        List<E> newList = new ArrayList<>(input.size() - retainingSize);
        while (input.size() > retainingSize) {
            E e = input.remove(retainingSize);
            newList.add(e);
        }
        return newList;
    }

    static class SplitResult<K extends Comparable<K>, V> {
        final Node<K, V> newNode;
        final K newKey;

        SplitResult(Node<K, V> newNode, K newKey) {
            this.newNode = newNode;
            this.newKey = newKey;
        }
    }

    static abstract class Node<K extends Comparable<K>, V> {
        protected final List<K> keys;

        Node(List<K> keys) {
            this.keys = keys;
        }

        abstract SplitResult<K, V> addKeyValue(K k, V v, int maxKeySize);

        abstract List<V> find(K k);

        abstract List<V> findRange(K lower, K upper);

        /**
         * With a given key "k", this method will return the branch which this key belongs to
         */
        int findBranch(K k) {
            int i;
            for (i = 0; i < keys.size(); i++) {
                if (k.compareTo(keys.get(i)) < 0) {
                    break;
                }
            }
            return i;
        }
    }

    static class InternalNode<K extends Comparable<K>, V> extends Node<K, V> {
        private final List<Node<K, V>> children;

        InternalNode(List<K> keys, List<Node<K, V>> children) {
            super(keys);
            this.children = children;
        }

        /**
         * Find the key "k" in the internal node
         */
        @Override
        List<V> find(K k) {
            Node<K, V> child = children.get(findBranch(k));//find the child which the key "k" belong to
            return child.find(k);//find key "k" on the child, the method "find" used depends on what class the child is of
        }

        /**
         * Find the list of keys in range of "lower - upper"
         */
        @Override
        List<V> findRange(K lower, K upper) {
            List<V> results = new ArrayList<>();
            //find the fromIndex in the internal node
            int fromIndex;
            for (fromIndex = 0; fromIndex < keys.size(); fromIndex++) {
                int cmp = lower.compareTo(keys.get(fromIndex));
                if (cmp < 0) {
                    break;
                } else if (cmp == 0) {
                    fromIndex++;
                    break;
                }
            }
            //consider the node's children from the fromIndex, if their keys are in range then add them
            int toIndex;
            for (toIndex = fromIndex; toIndex < keys.size(); toIndex++) {
                Node<K, V> childNode = children.get(toIndex);
                results.addAll(childNode.findRange(lower, upper));
                if (keys.get(toIndex).compareTo(upper) > 0) {
                    return results;
                }
            }
            Node<K, V> childNode = children.get(toIndex);
            results.addAll(childNode.findRange(lower, upper));
            return results;
        }
        /**
         * Insert a new key, new node to an internal node. If node is overflow, then split and returns a new node
         */
        SplitResult<K, V> addNewNodeAndSplitIfNeeded(K newKey, Node<K, V> newNode, int maxKeySize) {
            int branch = findBranch(newKey);//find the position "branch" to add new key
            keys.add(branch, newKey);//add the new key to the higher node at the position "branch"
            children.add(branch + 1, newNode);//add the new child to the higher node at the position "branch+1"
            //not overflow
            if (keys.size() <= maxKeySize) {
                return null;
            }
            // Overflow, split
            int pivotIndex = (keys.size() - 1) / 2;
            K pivotKey = keys.remove(pivotIndex);
            List<K> rightKeys = split(keys, pivotIndex);
            List<Node<K, V>> rightChildren = split(children, pivotIndex + 1);
            return new SplitResult<>(new InternalNode<>(rightKeys, rightChildren), pivotKey);
        }

        /**
         * Add a pair "key k, value v" into an internal node
         */
        @Override
        SplitResult<K, V> addKeyValue(K k, V v, int maxKeySize) {
            int branch = findBranch(k);
            SplitResult<K, V> result = children.get(branch).addKeyValue(k, v, maxKeySize);
            if (result == null) {
                return null;
            }
            return addNewNodeAndSplitIfNeeded(result.newKey, result.newNode, maxKeySize);
        }
    }

    static class LeafNode<K extends Comparable<K>, V> extends Node<K, V> {
        private final List<List<Integer>> filePointers;

        LeafNode() {
            this(new ArrayList<K>(), new ArrayList<List<Integer>>());
        }

        LeafNode(List<K> keys, List<List<Integer>> filePointers) {
            super(keys);
            this.filePointers = filePointers;
        }

        /**
         * Find the key "k" in the leaf node
         */
        @Override
        List<V> find(K k) {
            int left = 0;
            int right = keys.size() - 1;
            while (left <= right) {
                int mid = (left + right) / 2;
                int cmp = k.compareTo(keys.get(mid));
                if (cmp < 0) {
                    right = mid - 1;
                } else if (cmp > 0) {
                    left = mid + 1;
                } else {
                    return readValuesFromDisk(filePointers.get(mid));
                }
            }
            return new ArrayList<>();
        }

        /**
         * Give the range "lower - upper", this method finds all keys in the leaf node within this range
         */
        @Override
        List<V> findRange(K lower, K upper) {
            List<V> results = new ArrayList<>();
            int fromIndex;
            for (fromIndex = 0; fromIndex < keys.size(); fromIndex++) {
                if (lower.compareTo(keys.get(fromIndex)) <= 0) {
                    break;
                }
            }
            for (int i = fromIndex; i < keys.size(); i++) {
                if (keys.get(i).compareTo(upper) <= 0) {
                    results.addAll(readValuesFromDisk(filePointers.get(i)));
                } else {
                    break;
                }
            }
            return results;
        }

        /**
         * The method takes and returns all keys (from the "Disk") associated with the given filePositions
         */
        private List<V> readValuesFromDisk(List<Integer> filePositions) {
            List<V> result = new ArrayList<>(filePositions.size());
            for (Integer pos : filePositions) {
                result.add((V) Disk.readFromDisk(pos));
            }
            return result;
        }

        /**
         * Insert a new key,value to a leaf node. If the leaf node is overflow, then split and returns a new node
         */
        @Override
        SplitResult<K, V> addKeyValue(K k, V v, int maxKeySize) {
            final int filePosition = Disk.writeToDisk(v); // write the adding value to disk
            int branch = 0;
            for (; branch < keys.size(); branch++) {
                int cmp = k.compareTo(keys.get(branch));
                if (cmp == 0) {
                    filePointers.get(branch).add(filePosition);
                    return null;
                }
                if (cmp < 0) {
                    break;
                }
            }
            keys.add(branch, k);
            filePointers.add(branch, newList(filePosition));

            //not overflow
            if (keys.size() <= maxKeySize) {
                return null;
            }

            // Overflow, split
            int leftSize = keys.size() / 2;
            List<K> rightKeys = split(keys, leftSize);
            List<List<Integer>> rightFilePointers = split(filePointers, leftSize);
            final LeafNode<K, V> newLeaf = new LeafNode<>(rightKeys, rightFilePointers);
            return new SplitResult<>(newLeaf, rightKeys.get(0));
        }
    }

    private static <E> List<E> newList(E e) {
        List<E> list = new ArrayList<>();
        list.add(e);
        return list;
    }


    /**
     * Values of the leaf nodes of a B+ Tree are stored in the disk. This class simulates a disk
     */
    static class Disk {
        private final static List<Object> elements = new ArrayList<>(50000);

        /**
         * Write the value e to the disk and return the file pointer at the beginning of the object
         *
         * @param e the value to write
         * @return the file position of the written object
         */
        static int writeToDisk(Object e) {
            final int pos = elements.size();
            elements.add(e);
            return pos;
        }

        /**
         * Read the element on disk at the given file position
         *
         * @param filePosition the file position to read
         * @return the value read from disk at the given file position
         */
        static Object readFromDisk(int filePosition) {
            return elements.get(filePosition);
        }
    }
}
