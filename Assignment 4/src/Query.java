import java.util.ArrayList;
import java.util.List;

public class Query {
    private final String str;
    private final List<Clause> clauses = new ArrayList<>();

    public Query(String str) {
        this.str = str;
        String[] clauseStrings = str.split(" AND ");
        for (String clauseString : clauseStrings) {
            String[] parts = clauseString.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException(str + " is not a valid query");
            }
            String field = parts[0].trim();
            String value = parts[1].trim();
            if (field.isEmpty() || value.isEmpty()) {
                throw new IllegalArgumentException(str + " is not a valid query");
            }
            clauses.add(new Clause(field, value));
        }
        if (clauses.isEmpty()) {
            throw new IllegalArgumentException(str + " is not a valid query: no clause");
        }
    }
    
    public List<Clause> getClauses() {
        return clauses;
    }

    @Override
    public String toString() {
        return str;
    }

    static class Clause {
        private final String field;
        private final String value;

        Clause(String field, String value) {
            this.field = field;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return field + ":" + value;
        }
    }
}
