import java.io.PrintStream;

/**
 * A height-balanced binary search tree with AVL implementation
 */
public class BalancedBST<K extends Comparable<K>, V> {

    /**
     * Inner class Node
     */
    static class Node<K extends Comparable<K>, V> {
        final K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;
        int height;

        public Node(K k, V v, Node<K, V> l, Node<K, V> r) {
            key = k;
            value = v;
            left = l;
            right = r;
        }
    }

    private Node<K, V> root = null;

    /**
     * method addToNode, to be used in method "add"
     *
     * @param key:   the key of the node
     * @param value: the value of the node
     * @param node:  the node to add
     * @return the new node
     */
    private Node<K, V> addToNode(K key, V value, Node<K, V> node) {
        if (node == null) {
            return new Node<>(key, value, null, null);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = addToNode(key, value, node.left);
        } else if (cmp > 0) {
            node.right = addToNode(key, value, node.right);
        } else {
            node.value = value;
            return node;
        }
        updateHeight(node);
        return balance(node);
    }

    /**
     * method "add", add "key" and "value" to the "root" node
     *
     * @param key:   the given key
     * @param value: the given value
     */
    public void add(K key, V value) {
        root = addToNode(key, value, root);
    }

    /**
     * update the height of the node "node"
     *
     * @param node: the given node
     */
    private void updateHeight(Node<K, V> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    /**
     * calculate the height of the node "node"
     *
     * @param node: the given node
     * @return
     */
    int height(Node<K, V> node) {
        if (node == null) {
            return -1;
        } else {
            return node.height;
        }
    }

    /**
     * balance the tree at the node "node"
     *
     * @param node: the given node
     * @return the node being balanced
     */
    private Node<K, V> balance(Node<K, V> node) {
        if (balanceFactor(node) < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            node = rotateLeft(node);
        } else if (balanceFactor(node) > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            node = rotateRight(node);
        }
        return node;
    }

    /**
     * rotate the tree to the left at the node "node", use it when the tree is right-heavy
     *
     * @param node the node to be rotated
     * @return the node being rotated
     */
    private Node<K, V> rotateLeft(Node<K, V> node) {
        Node<K, V> r = node.right;
        node.right = r.left;
        r.left = node;
        updateHeight(node);
        updateHeight(r);
        return r;
    }

    /**
     * rotate the tree to the right at the node "node", use it when the tree is left-heavy
     *
     * @param node the node to be rotated
     * @return the node being rotated
     */
    private Node<K, V> rotateRight(Node<K, V> node) {
        Node<K, V> l = node.left;
        node.left = l.right;
        l.right = node;
        updateHeight(node);
        updateHeight(l);
        return l;
    }

    /**
     * calculate the balance factor at the node "node"
     *
     * @param node: the given node
     * @return the balance factor
     */
    private int balanceFactor(Node<K, V> node) {
        return height(node.left) - height(node.right);
    }

    /**
     * get value of the given node with key "key", to be used in method "get"
     *
     * @param key:  key of the node
     * @param node: the given node
     * @return the value associated with the key "key"
     */
    private V getFromNode(K key, Node<K, V> node) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node.value;
        } else if (cmp < 0) {
            return getFromNode(key, node.left);
        } else {
            return getFromNode(key, node.right);
        }
    }

    /**
     * get value associated with key "key"
     *
     * @param key: the given key
     * @return the value associated with the key "key"
     */
    public V get(K key) {
        return getFromNode(key, root);
    }

    public void printTree(PrintStream writer) {
        if (root == null) {
            writer.println("Tree is empty");
        }
        writer.println("root node: key=" + root.key + " value=" + root.value);
        printNode(root, writer);
    }

    // traverse pre-order
    private void printNode(Node<K, V> node, PrintStream writer) {
        if (node == null) {
            return;
        }
        final Node<K, V> left = node.left;
        if (left != null) {
            writer.println("left of node (" + node.key +"): key=" + left.key + " value=" + left.value);
        }
        final Node<K, V> right = node.right;
        if (right != null) {
            writer.println("right of node (" + node.key +"): key=" + right.key + " value=" + right.value);
        }
        if (left != null) {
            printNode(left, writer);
        }
        if (right != null) {
            printNode(right, writer);
        }
    }
}
