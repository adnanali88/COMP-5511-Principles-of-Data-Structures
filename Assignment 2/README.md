
# Assignment 2

## **Theoritical questions 1, 2, 3:**   
Please see the separate file named “Question123.pdf”

## **Programming - question 1:**  
The programming question 1 is put in the folder Prog_Question1, the file name is  
**LinkedList.java**  

**Instruction to run the program:**   
The input should be created in the main function. You create an  
empty linked list, then push elements in the list afterward. The program will print the list,  
print the count of element upon the iterative method, and print the count of element upon  
the recursive method.  

If you want to test with another input, please go to the main function and change the input.  
For example, you would like to input this list “1 2 1 3 1”, you will go to the main  
function, and make input as follows:  

LinkedList llist = new LinkedList(); *//create empty linked list*  
*//push elements into the linked list*   
llist.push(1);  
llist.push(3);  
llist.push(1);  
llist.push(2);  
llist.push(1); 

**Output**  
Printing the list...  
1 2 1 3 1  
Count of nodes with iterative method is 5  
Count of nodes with recursive method is 5  

## **Programming - Question 2:**  
The programming question 2 is put in the folder **Prog_Question2**    
* Source codes file included: The folder includes 4 .java files:  
* BinaryTree.java: BinaryTree class
* Stack.java: Stack class
* SinglyLinkedList.java: SinglyLinkedList class to support Stack class
* Assignment2.java contains main method and supporting methods to evaluate the input  expression. 

**Important supporting methods include:**
* parseToken: take “expression” as input, output the list of tokens of the expression
* prefix: input a list of tokens, output another list of tokens in the prefix order,
(example of prefix order: expression: 1+2 ==> prefix order: + 1 2). This is the middle
step before putting expression in a binary tree.
* expToTree: output a binary tree from an expression
* evaluateTree: input a binary tree, process the tree and update a stack, after complete
running this method, the stack contain 1 element only, it’s the result of the evaluation.  

**Instruction to run the program:**   
* Run the main method in the file Assignment2.java  
* Input the expression via the console, example of an expression: 2*(5-1)+3*2 

**Output from the above example**  
****************************************
Expression: 2*(5-1)+3*2  
Putting expression in a binary tree ...  
Printing the binary tree...  
" + *   *  2 - 3 2 null null 5 1 "      
Processing binary tree...  
Result after evaluating: 14.0  
****************************************  

Please note that the binary tree is printed under the order of the inside array with principle:
* root at index 0
* index_of_left_child = index_of_parent * 2 + 1
* index_of_right_child = index_of_parent * 2 + 2
