import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * InvertedIndex is a HashMap from term to a list of document ids whose documents contain that term
 */
public class InvertedIndex {
    private final HashMap<String, List<String>> map = new HashMap<>(100003);

    public void addTerm(String id, String term) {
        List<String> docIds = map.get(term);
        if (docIds == null) {
            docIds = new ArrayList<>();
            map.put(term, docIds);
        }
        docIds.add(id);
    }

    public List<String> queryTerm(String term) {
        List<String> docIds = map.get(term);
        if (docIds == null) {
            return new ArrayList<>();
        }
        // If a field has duplicate terms, then documentId appears more than once
        // Using the map to deduplicate it
        HashMap<String, Boolean> map = new HashMap<>(docIds.size());
        for (String docId : docIds) {
            map.put(docId, Boolean.TRUE);
        }
        return map.getKeys();
    }

    public void printIndex(PrintStream out) {
        List<String> terms = map.getKeys();
        Collections.sort(terms);
        out.println("total terms: " + terms.size());
        out.println("list of terms are printed in the format: term -> list of document ids containing that term");
        for (String key : terms) {
            out.println(key + " -> " + queryTerm(key));
        }
    }
}
