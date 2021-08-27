import java.util.*;

public class Assignment2 {

    /**
     * Returns the list of tokens from the input string
     */
    public static List<String> parseToken(String in) {
        List<String> tokens = new ArrayList<>();
        int startIndex = -1;
        char[] chars = in.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isSpaceChar(c)) {
                if (startIndex != -1){
                    tokens.add(in.substring(startIndex, i));
                    startIndex = -1;
                }
            } else if (Character.isDigit(c)) {
                if (startIndex == -1) {
                    startIndex = i;
                }
            } else if (c == '-') {
                if (startIndex != -1) {
                    tokens.add(in.substring(startIndex, i));
                    startIndex = -1;
                    tokens.add(in.substring(i, i + 1));
                } else {
                    startIndex = i;
                }
            } else if (c == '(' || c == ')' || c == '*' || c == '/' || c == '+') {
                if (startIndex != -1) {
                    tokens.add(in.substring(startIndex, i));
                    startIndex = -1;
                }
                tokens.add(in.substring(i, i + 1));
            } else {
                throw new IllegalArgumentException("unexpected character [" + c + "]");
            }
        }
        if (startIndex != -1) {
            tokens.add(in.substring(startIndex, chars.length));
        }
        return tokens;
    }

    public static boolean isOperator(String c) {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/");
    }

    public static int priority(String c) {
        if (c.equals("+") || c.equals("-")) return 0;
        if (c.equals("*") || c.equals("/")) return 1;
        return 2; // '(', ')', or number
    }

    /**
     * @param index is the index of the current opening parentheses
     * @return the index of the corresponding closing parentheses
     */
    public static int indexOfNextClosingParentheses(List<String> expressions, int index) {
        index++; // skip the char '('
        String c = expressions.get(index);
        while (!c.equals(")")) {
            if (c.equals("(")) {
                index = indexOfNextClosingParentheses(expressions, index) + 1;
            } else {
                index++;
                c = expressions.get(index);
            }
        }
        return index;
    }

    /**
     * Returns a list representing the prefix of expressions
     * @param expressions
     * @param startIndex inclusive
     * @param endIndex exclusive
     */
    public static List<String> prefix(List<String> expressions, int startIndex, int endIndex) {

        if (expressions.isEmpty()) return Collections.emptyList();

        List<String> output = new ArrayList<>();
        int lastOpIdx = -1;

        while (startIndex < endIndex) {
            String current = expressions.get(startIndex);

            if (current.equals("(")) {
                startIndex++;//skip the element "("
                int end = indexOfNextClosingParentheses(expressions, startIndex);//return index of ')'
                List<String> toAppend = prefix(expressions, startIndex, end);

                output.add("(");
                output.addAll(toAppend);
                output.add(")");
                startIndex = end + 1;

            } else if (isOperator(current)) {
                if (priority(current) <= priority(output.get(0))) {
                    output.add(0, current); //insert operator before the first element of output
                    lastOpIdx = 0;

                } else {//current operator has higher priority than the first element
                    if (lastOpIdx != -1 && priority(current) <= priority(output.get(lastOpIdx))) {
                        output.add(lastOpIdx, current);//insert operator before the last inserted operator

                    } else {
                        int idxToInsert = output.size() - 1;
                        output.add(idxToInsert, current);//insert operator before the last element of output
                        lastOpIdx = idxToInsert;
                    }
                }
                startIndex++;

            } else {//current element is a number
                output.add(current);
                startIndex++;
            }
        }
        //delete brackets from the list
        output.removeIf(e -> e.equals("(") || e.equals(")"));
        return output;
    }

    /**
     * Returns the binary tree
     * @param expression: the expression to be processed
     */
    public static BinaryTree expToTree(String expression) {
        List<String> tokens = parseToken(expression);
        List<String> prefixedTokens = prefix(tokens, 0, tokens.size());

        String[] nodes = new String[1];

        if (prefixedTokens.isEmpty()) {
            throw new IllegalArgumentException("empty expression");
        }

        Stack<Integer> nullRight = new Stack<Integer>();
        int indexInList = 0;
        int indexLastInsertedInArray = 0;
        while (indexInList < prefixedTokens.size()) {
            String element = prefixedTokens.get(indexInList);
            if (indexInList == 0) {
                nodes[0] = element;
                indexLastInsertedInArray = 0;
                nullRight.push(indexLastInsertedInArray);
                indexInList++;
            }
            else {
                if (isOperator(nodes[indexLastInsertedInArray])) {//last inserted is an operator
                    indexLastInsertedInArray = indexLastInsertedInArray * 2 + 1; //insert element to the left of the last inserted
                }
                else {//last inserted element is numbers
                    int indexParent = nullRight.pop();
                    indexLastInsertedInArray = indexParent * 2 + 2; //insert element to the right of the popped element fr nullright
                }
                if (nodes.length <= indexLastInsertedInArray) {
                    // resize to contain the index indexLastInsertedInArray
                    nodes = Arrays.copyOf(nodes, indexLastInsertedInArray + 1);
                }
                nodes[indexLastInsertedInArray] = element;
                if (isOperator(element)) {
                    nullRight.push(indexLastInsertedInArray);
                }
                indexInList++;
            }
        }
        return new BinaryTree(nodes);
    }

    /**
     * Push an element into a stack and check the top elements of stack, then pop the stack for calculation if needed
     */
    static void pushAndCheckStack(Stack<String> stack, String element) {
        if (stack.isEmpty()) {
            stack.push(element);
            return; // done
        }

        stack.push(element);
        String peek = stack.peek();
        String secondPeek = stack.secondPeek();

        if (!isOperator(peek) && !isOperator(secondPeek)) {
            stack.pop();
            stack.pop();
            String operator = stack.pop();
            String new_element = Double.toString(calculate(secondPeek, operator, peek));
            pushAndCheckStack(stack, new_element);
        }
    }

    static Stack<String> stack = new Stack<String>();
    /**
     * Evaluate the tree, process the stack (static) so that the last element in the stack is the value of the expression
     */
    public static void evaluateTree(BinaryTree tree, int index) {
        String[] nodes = tree.getNodes();

        stack.push(nodes[index]);//push the first element into stack

        //process left subtree
        int leftIndex = index*2 + 1;
        if (leftIndex < nodes.length) {
            String leftElement  = nodes[leftIndex];
            if (!isOperator(leftElement)) {//element is a number
                stack.push(leftElement);
            } else {//element is an operator
                evaluateTree(tree, leftIndex);
            }
        }

        //process right subtree
        int rightIndex = index*2 + 2;
        if (rightIndex < nodes.length) {
            String rightElement = nodes[rightIndex];
            if (!isOperator(rightElement)) {//element is a number
                pushAndCheckStack(stack, rightElement);
            } else {//element is an operator
                evaluateTree(tree, rightIndex);
            }
        }
    }

    /**
     * Calculate operand1 operator operand2
     */
    static double calculate(String operand1, String operator, String operand2) {
        double num1 = Double.parseDouble(operand1);
        double num2 = Double.parseDouble(operand2);
        if (operator.equals("+")) { return num1 + num2; }
        else if (operator.equals("-")) { return num1 - num2; }
        else if (operator.equals("*")) { return num1 * num2; }
        else if (operator.equals("/")) { return num1 / num2; }
        else {
            throw new IllegalStateException("Illegal operator");
        }
    }

    public static void main(String[] args) {

        System.out.println("Please enter an expression: ");
        Scanner input = new Scanner(System.in);
        String s = input.nextLine();

        //process expression, put it in a binary tree
        System.out.println("Expression: " + s);
        System.out.println("Putting expression in a binary tree ...");
        BinaryTree tree = expToTree(s);
        System.out.println("Printing the binary tree...");
        tree.printTree();
        System.out.println();

        //process binary tree, put nodes in stack, evaluate the expression
        System.out.println("Processing binary tree...");
        evaluateTree(tree, 0);
        System.out.println("Result after evaluating: " + stack.pop());
    }
}
