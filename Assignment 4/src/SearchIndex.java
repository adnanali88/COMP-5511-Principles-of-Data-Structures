import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A SearchIndex is an in-memory that includes:
 * - Inverted index for name
 * - Inverted index for region
 * - Inverted index for type
 * - AVLTree for id
 * - B+Tree for locations(latitude, longitude)
 */
public class SearchIndex {
    static String[] STOP_WORDS = {"a", "aux", "des", "du", "of", "la", "sur", "de", "the", "à"};

    final BalancedBST<String, Document> byId = new BalancedBST<>();
    final InvertedIndex wordedNameIndex = new InvertedIndex();
    final InvertedIndex fullNameIndex = new InvertedIndex();
    final InvertedIndex wordedRegionIndex = new InvertedIndex();
    final InvertedIndex fullRegionIndex = new InvertedIndex();
    final InvertedIndex wordedTypeIndex = new InvertedIndex();
    final InvertedIndex fullTypeIndex = new InvertedIndex();
    final BPlusTree<Double, String> byLatitude = new BPlusTree<>(16);
    final BPlusTree<Double, String> byLongitude = new BPlusTree<>(16);
    final BPlusTree<Coordinate, String> byCoordinate = new BPlusTree<>(16);

    /**
     * This method adds a given document to 1 BST (used for the search by id), 6 inverted indices(2 used for the
     * search by name, 2 used for the search by region, 2 used for the search by type), 3 B+ Trees (used for the
     * search by latitude, search by longitude, search by coordinates)
     */
    public void addDocument(Document doc) {
        byId.add(doc.id, doc);
        addTerms(fullNameIndex, wordedNameIndex, doc.id, doc.name);
        addTerms(fullRegionIndex, wordedRegionIndex, doc.id, doc.region);
        addTerms(fullTypeIndex, wordedTypeIndex, doc.id, doc.type);
        // coordinate
        Coordinate coordinate = new Coordinate(parseLatitude(doc.latitude), parseLongitude(doc.longitude));
        byLatitude.add(coordinate.latitude, doc.id);
        byLongitude.add(coordinate.longitude, doc.id);
        byCoordinate.add(coordinate, doc.id);
    }

    /**
     * This method does the search for a given query, return a list of relevant documents
     */
    public List<Document> searchQuery(Query query) {
        List<Query.Clause> clauses = query.getClauses();
        //find intersection of all IDs matched with given clauses in a query
        List<String> ids = searchOneClause(clauses.get(0));
        for (int i = 1; i < clauses.size(); i++) {
            List<String> other = searchOneClause(clauses.get(i));
            ids = intersect(ids, other);
        }
        //find documents associated with the found IDs
        List<Document> docs = new ArrayList<>();
        for (String id : ids) {
            Document doc = byId.get(id);
            if (doc != null) {
                docs.add(doc);
            }
        }
        return docs;
    }

    /**
     * This method prints the indices
     */
    public void printIndex(PrintStream out, String index) {
        if ("name".equals(index)) {
            out.println("PRINTING inverted index of the names of places");
            wordedNameIndex.printIndex(out);
        } else if ("region".equals(index)) {
            out.println("PRINTING inverted index of the region names of places");
            wordedRegionIndex.printIndex(out);
        } else if ("type".equals(index)) {
            out.println("PRINTING inverted index of the type of places");
            fullTypeIndex.printIndex(out);
        } else if ("id".equals(index)) {
            out.println("PRINTING Balanced binary search tree of ids");
            byId.printTree(out);
        } else {
            throw new IllegalArgumentException("PRINT must be used with id or name or region or type");
        }
    }

    /**
     * This method decides if a string s is a stop word
     */
    private static boolean isStopWord(String s) {
        for (String w : STOP_WORDS) {
            if (w.equals(s)) {
                return true;
            }
        }
        return false;
    }


    /**
     * This method decides if a char c is a delimiter
     */
    private static boolean isDelimiter(char c) {
        return c == ' ' || c == ';' || c == ',' || c == '(' || c == ')' || c == '|';
    }

    /**
     * This method tokenizes a string s
     */
    private static List<String> tokenize(String s) {
        List<String> words = new ArrayList<>();
        String word = "";
        char[] chars = s.toLowerCase().toCharArray();
        for (char c : chars) {
            if (isDelimiter(c)) {
                words.add(word);
                word = "";
            } else {
                word += c;
            }
        }
        words.add(word);
        List<String> stopWords = new ArrayList<>();
        List<String> tokens = new ArrayList<>();
        for (String w : words) {
            if (!w.isEmpty()) {
                if (isStopWord(w)) {
                    stopWords.add(w);
                } else {
                    tokens.add(w);
                }
            }
        }
        // Some name contains only stop words id: EFJXB name: Aux type: Township 47.3333333,-76.2666667
        if (tokens.isEmpty()) {
            return stopWords;
        } else {
            return tokens;
        }
    }

    /**
     * This method joins the "terms" in a list
     */
    private static String joinStrings(List<String> terms) {
        String s = "";
        for (int i = 0; i < terms.size(); i++) {
            if (i > 0) {
                s += " ";
            }
            s += terms.get(i);
        }
        return s;
    }

    /**
     * Intersects two lists of document ids and returns a result consisting of ids that exist in both of them
     * list1: (1, 2, 3, 11, 20), list2: (3, 5, 7, 20) => result: (3, 20)
     */
    private static List<String> intersect(List<String> first, List<String> second) {
        // Use a map to avoid O(N^2)
        HashMap<String, Integer> map = new HashMap<>(first.size());//the map used here is our own implementation
        for (String id : first) {
            map.put(id, 1);
        }
        List<String> result = new ArrayList<>();
        for (String id : second) {
            if (map.get(id) != null) {
                result.add(id);
            }
        }
        return result;
    }

    /**
     * Unions two lists of document ids and returns a result consisting of ids that exist in either of them
     * list1: (1, 2, 3, 11, 20), list2: (3, 5, 7, 20) => result: (1, 2, 3, 11, 20, 5, 7)
     */
    private static List<String> union(List<String> first, List<String> second) {
        // Use a map to avoid O(N^2)
        HashMap<String, Integer> map = new HashMap<>(first.size() + second.size()); //the map used here is our own implementation
        for (String id : first) {
            map.put(id, 1);
        }
        for (String id : second) {
            map.put(id, 1);
        }
        return map.getKeys();
    }

    /**
     * This method does the search on a single clause of a query, returns the list of relevant ids
     */
    private List<String> searchOneClause(Query.Clause clause) {
        String field = clause.getField();
        String value = clause.getValue();
        switch (field) {
            case "id":
                // an id clause just returns itself so we can use AVLTree/BST to search by id later
                ArrayList<String> ids = new ArrayList<>();
                ids.add(value);
                return ids;
            case "name":
                return searchByString(fullNameIndex, wordedNameIndex, value);
            case "type":
                return searchByString(fullTypeIndex, wordedTypeIndex, value);
            case "region":
                return searchByString(fullRegionIndex, wordedRegionIndex, value);
            case "location":
                return searchByLocation(value);
        }
        throw new IllegalArgumentException("invalid query: field: " + field + " value: " + value);
    }

    /**
     * This method eliminates the operator from a query
     */
    private static String trimQueryOperator(String query) {
        int lastIndex = query.lastIndexOf(")");
        if (lastIndex == -1) {
            throw new IllegalArgumentException(query + " is not a valid query");
        }
        int firstIndex = query.indexOf("(");
        if (firstIndex == -1 || firstIndex > lastIndex) {
            throw new IllegalArgumentException(query + " is not a valid query");
        }
        return query.substring(firstIndex + 1, lastIndex).trim();
    }

    /**
     * This method does a search on a given string "q", will be used in searching by name, region, type (full/part)
     * returns a list of relevant ids
     * @param fullIndex the inverted index for exact strings to be searched by full string of name, region, type
     * @param wordedIndex the inverted index for the substrings, to be searched by part of name, region, type
     */
    private static List<String> searchByString(InvertedIndex fullIndex, InvertedIndex wordedIndex, String q) {
        q = q.trim();
        if (q.startsWith("EXACT_MATCH")) {
            q = trimQueryOperator(q);
            q = joinStrings(tokenize(q));
            return fullIndex.queryTerm(q);
        } else if (q.startsWith("MATCH_ANY")) {
            q = trimQueryOperator(q);
            List<String> result = new ArrayList<>();
            for (String clause : q.split("\\|")) {
                final List<String> other = searchByTerms(wordedIndex, tokenize(clause));
                result = union(result, other);
            }
            return result;
        } else {
            if (q.startsWith("MATCH_ALL")) {
                q = trimQueryOperator(q);
            }
            return searchByTerms(wordedIndex, tokenize(q));
        }
    }

    private static List<String> searchByTerms(InvertedIndex wordedIndex, List<String> terms) {
        if (terms.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> result = wordedIndex.queryTerm(terms.get(0));
        for (int i = 1; i < terms.size(); i++) {
            List<String> other = wordedIndex.queryTerm(terms.get(i));
            result = intersect(result, other);
        }
        return result;
    }

    /**
     * This method adds a term (with an id "docId" and a string "str") to 2 inverted indices fullIndex and wordedIndex
     */
    private static void addTerms(InvertedIndex fullIndex, InvertedIndex wordedIndex, String docId, String str) {
        List<String> tokens = tokenize(str);
        if (!tokens.isEmpty()) {
            for (String term : tokens) {
                wordedIndex.addTerm(docId, term);
            }
            fullIndex.addTerm(docId, joinStrings(tokens));
        }
    }

    private List<String> searchByLocation(String s) {
        //if the query requests the location AT 1 pair of (latitude, longitude)
        if (s.startsWith("AT")) {
            s = trimQueryOperator(s);
            return byCoordinate.find(parseCoordinate(s));
        } else if (s.startsWith("WITHIN")) {//if the query requests the location WITHIN 2 pairs of (latitude, longitude)
            s = trimQueryOperator(s);
            String[] coords = s.split("\\|");
            if (coords.length != 2) {
                throw new IllegalArgumentException("invalid within location query [" + s + "]");
            }
            Coordinate first = parseCoordinate(coords[0]);//first pair of (latitude, longitude)
            Coordinate second = parseCoordinate(coords[1]);//second pair of (latitude, longitude)

            double minLatitude = Math.min(first.latitude, second.latitude);
            double maxLatitude = Math.max(first.latitude, second.latitude);
            //search for documents with latitude in the range of (minLatitude, maxLatitude) on the inverted index byLatitude
            List<String> withinLatitude = byLatitude.findRange(minLatitude, maxLatitude);

            double minLongitude = Math.min(first.longitude, second.longitude);
            double maxLongitude = Math.max(first.longitude, second.longitude);
            //search for documents with longitude in the range of (minLongitude, maxLongitude) on the inverted index byLongitude
            List<String> withinLongitude = byLongitude.findRange(minLongitude, maxLongitude);
            //return the intersection of documents in range of latitude and longitude
            return intersect(withinLatitude, withinLongitude);
        }
        throw new IllegalArgumentException("location must be queried with AT or WITHIN; got " + s);
    }

    /**
     * static inner class Coordinate with latitude and longitude
     */
    static class Coordinate implements Comparable<Coordinate> {
        final double latitude;
        final double longitude;

        Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public int compareTo(Coordinate that) {
            int cmp = Double.compare(this.latitude, that.latitude);
            if (cmp < 0) {
                return -1;
            } else if (cmp > 0) {
                return 1;
            }
            return Double.compare(this.longitude, that.longitude);
        }
    }

    /**
     * This method take a string "s" of latitude and longitude, return an object of class Coordinate
     */
    private static Coordinate parseCoordinate(String s) {
        String[] parts = s.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("invalid coordinate [" + s + "]");
        }
        double latitude = parseLatitude(parts[0]);
        double longitude = parseLongitude(parts[1]);
        return new Coordinate(latitude, longitude);
    }

    /**
     * This method takes a string "s" of longitude with human-readable format, transforms it to the format of the
     * input file and returns
     */
    private static double parseLongitude(String s) {
        s = s.toLowerCase().trim();
        double longitude;
        if (s.endsWith("w")) {
            s = s.replace("w", "");
            longitude = -Double.parseDouble(s);
        } else if (s.endsWith("e")) {
            s = s.replace("e", "");
            longitude = Double.parseDouble(s);
        } else {
            longitude = Double.parseDouble(s);
        }
        if (longitude > 180.0 || longitude < -180.0) {
            throw new IllegalArgumentException("longitude must be between -180.0 to 180.0; got " + longitude);
        }
        return longitude;
    }

    /**
     * This method takes a string "s" of latitude with human-readable format, transforms it to the format of the
     * input file and returns
     */
    private static double parseLatitude(String s) {
        s = s.toLowerCase().trim();
        double latitude;
        if (s.endsWith("n")) {
            s = s.replace("n", "");
            latitude = Double.parseDouble(s);
        } else if (s.endsWith("s")) {
            s = s.replace("s", "");
            latitude = -Double.parseDouble(s);
        } else {
            latitude = Double.parseDouble(s);
        }
        if (latitude > 90.0 || latitude < -90.0) {
            throw new IllegalArgumentException("latitude must be between -90.0 to 90.0; got " + latitude);
        }
        return latitude;
    }
}
