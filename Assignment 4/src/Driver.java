import java.io.*;
import java.util.*;

public class Driver {

    /**
     * This method read commands from the script file, put them in a list and return the list
     */
    private static List<String> readCommandsFromScript(String fileName) throws IOException {
        List<String> commands = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(fileName)) {
            Scanner scanner = new Scanner(in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                //eliminate the content after "//" from the line to be added to the list
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex);
                }
                commands.add(line);
            }
        }
        return commands;
    }

    /**
     * This method read data from the record file, put them under the format of Document objects, put documents in the
     * list, sort it and return it
     */
    private static List<Document> readDocumentsFromRecordFile(String fileName) throws IOException {
        List<Document> documents = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(fileName)) {
            Scanner scanner = new Scanner(in);
            scanner.nextLine(); // skip the header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    Document doc = Document.parseCVSLine(line);
                    documents.add(doc);
                } catch (Exception e) {
                    throw new IllegalArgumentException(line + " is not a valid record", e);
                }
            }
        }
        // Sort the documents by id before building the tree so we can have the minimum-height BST,
        // according to the instruction from professor Desai
        Collections.sort(documents, new Comparator<Document>() {
            @Override
            public int compare(Document d1, Document d2) {
                return d1.id.compareTo(d2.id);
            }
        });
        return documents;
    }

    /**
     * This method run the search based on the "searchIndex", list of commands "commands", log the results to
     * the "logStream"
     * @param searchIndex an object of class SearchIndex
     * @param commands the list of commands
     * @param logStream the output file
     */
    private static void runCommands(SearchIndex searchIndex, List<String> commands, PrintStream logStream) {
        for (String command : commands) {
            try {
                if (command.isEmpty()) {
                    continue;
                }
                if (command.startsWith("PRINT ")) {
                    logStream.println("==> " + command);
                    String indexName = command.substring("PRINT ".length());
                    searchIndex.printIndex(logStream, indexName);
                } else if (command.startsWith("QUERY ")) {
                    logStream.println("==> " + command);
                    String query = command.substring("QUERY ".length());
                    List<Document> result = searchIndex.searchQuery(new Query(query));
                    if (result.isEmpty()) {
                        logStream.println("No match\n");
                    } else {
                        logStream.println(result.size() + " matches");
                        for (Document doc : result) {
                            logStream.println("Geographic ID: " + doc.id);
                            logStream.println("Geographic Name: " + doc.name);
                            logStream.println("Geographic Region: " + doc.region);
                            logStream.println("Geographic Type: " + doc.type);
                            logStream.println("Geographic Latitude: " + doc.latitude);
                            logStream.println("Geographic Longitude: " + doc.longitude);
                            logStream.println();
                        }
                    }
                    logStream.println();
                } else {
                    logStream.println("command must start with QUERY or PRINT; got " + command);
                }
            } catch (Exception e) {
                logStream.println("Failed to execute command [" + command + "] reason [" + e.getMessage() + "]");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Program needs three files in format: Driver <record-file> <script-file> <log-file>");
            System.out.println("Program will be terminated!");
            System.exit(0);
        }
        String recordFile = args[0];
        String scriptFile = args[1];
        String logFile = args[2];
        SearchIndex searchIndex = new SearchIndex();

        //read documents from the record file
        List<Document> documents = null;
        try {
            documents = readDocumentsFromRecordFile(recordFile);
        } catch (FileNotFoundException e) {
            System.out.println("Record file [" + recordFile + "] does not exist. Program will be terminated!");
            System.exit(0);
        }

        //put all documents into the searchIndex
        for (Document doc : documents) {
            searchIndex.addDocument(doc);
        }

        //read commands from the script file
        List<String> commands = null;
        try {
            commands = readCommandsFromScript(scriptFile);
        } catch (FileNotFoundException e) {
            System.out.println("Script file [" + scriptFile + "] does not exist. Program will be terminated!");
            System.exit(0);
        }

        //do the search on the searchIndex for each command
        try (PrintStream logStream = new PrintStream(new FileOutputStream(logFile))) {
            runCommands(searchIndex, commands, logStream);
        }
    }
}
