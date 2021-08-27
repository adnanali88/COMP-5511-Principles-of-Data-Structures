import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Assignment1 {
    public static void main(String[] args) {
        Scanner input = null;
        Stack<String> s = new Stack<>();

        // open input file
        try {
            input = new Scanner(new FileInputStream("ds20s-a1.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println("The file does not exist. The program will terminate");
            System.exit(0);
        }

        String line = null;
        String lowerCase = null;

        //read the input file and process the data
        while (input.hasNextLine()) {
            line = input.nextLine();
            lowerCase = line.toLowerCase();
            if (lowerCase.equals("pop")) {
                System.out.println("Popping the stack, the top element is: " + s.pop());
                System.out.println();
            }
            else if (lowerCase.equals("print")) {
                System.out.println("Printing the stack...");
                s.print();
                System.out.println();
            }
            else {
                System.out.println("Pushing this element to the stack: " + line);
                s.push(line);
            }
        }

        //close the input file
        input.close();

    }
}
