import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.*;

public class XMLReader{

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("❗ Please specify which fields to print. Example:");
            System.out.println("   java SAXJsonSelector name address country");
            return;
        }

        List<String> selectedFields = Arrays.asList(args);
        File xmlFile = new File("data.xml");

        if (!xmlFile.exists()) {
            System.out.println("❗ Error: 'data.xml' file not found.");
            return;
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            RecordHandler handler = new RecordHandler(selectedFields);

            System.out.println("["); // Start of JSON array
            saxParser.parse(xmlFile, handler);
            System.out.println("]"); // End of JSON array

        } catch (Exception e) {
            System.out.println("❗ Error: " + e.getMessage());
        }
    }

    static class RecordHandler extends DefaultHandler {
        private final List<String> selectedFields;
        private final Map<String, String> currentRecord = new LinkedHashMap<>();
        private StringBuilder characters = new StringBuilder();
        private boolean insideRecord = false;
        private boolean first = true;
        private String currentElement = null;

        public RecordHandler(List<String> selectedFields) {
            this.selectedFields = selectedFields;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            characters.setLength(0); // Reset character buffer
            if (qName.equals("record")) {
                insideRecord = true;
                currentRecord.clear();
            } else if (insideRecord && selectedFields.contains(qName)) {
                currentElement = qName;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (currentElement != null) {
                characters.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals("record")) {
                if (!first) System.out.print(",\n");
                System.out.print("  " + toJson(currentRecord));
                first = false;
                insideRecord = false;
            } else if (qName.equals(currentElement)) {
                currentRecord.put(currentElement, characters.toString().trim());
                currentElement = null;
            }
        }

        private String toJson(Map<String, String> map) {
            StringBuilder json = new StringBuilder();
            json.append("{");
            int count = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                json.append("\"").append(entry.getKey()).append("\": ");
                json.append("\"").append(escape(entry.getValue())).append("\"");
                if (++count < map.size()) {
                    json.append(", ");
                }
            }
            json.append("}");
            return json.toString();
        }

        private String escape(String value) {
            return value.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}
