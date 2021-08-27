public class BinaryTree {
    private String root;
    private String[] nodes;

    public BinaryTree(String[] nodes) {
        this.nodes = nodes;
        if (nodes.length > 0) {
            root = nodes[0];
        }
    }

    protected void printTree() {
        for (String node : this.nodes) {
            System.out.print(node + "   ");
        }
    }

    protected String[] getNodes() {
        return nodes;
    }
}
