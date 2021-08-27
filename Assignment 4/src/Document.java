
import java.util.ArrayList;
import java.util.List;

public class Document {
    final String id;
    final String name;
    final String type;
    final String latitude;
    final String longitude;
    final String region;

    /**
     * This method processes a line from the record file, put it in the document format then return
     */
    public static Document parseCVSLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean escaping = false;
        char[] chars = line.toCharArray();
        String token = "";
        for (char c : chars) {
            if (c == '\"') {
                escaping = !escaping;
            } else {
                if (!escaping && c == ',') {
                    tokens.add(token);
                    token = "";
                } else {
                    token += c;
                }
            }
        }
        tokens.add(token);
        return new Document(tokens.get(0), tokens.get(1), tokens.get(4), tokens.get(8), tokens.get(9), tokens.get(10));
    }

    public Document(String id, String name, String type, String latitude, String longitude, String region) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.region = region;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id: " + id + " name: " + name + " type: " + type +
                " latitude: " + latitude + " longitude: '" + longitude +
                " region: " + region + "}";
    }
}
