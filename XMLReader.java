import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import java.io.File;
import java.util.*;

public class XMLReader{
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("❗ Please specify the fields to output in JSON. Example:");
            System.out.println("   java XMLToJsonValidator name country address");
            return;
        }

        List<String> selectedFields = Arrays.asList(args);

        try {
            File xmlFile = new File("data.xml");
            if (!xmlFile.exists() || !xmlFile.isFile()) {
                System.out.println("❗ Error: 'data.xml' file not found.");
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            try {
                doc = dBuilder.parse(xmlFile);
            } catch (Exception parseException) {
                System.out.println("❗ Error parsing XML file. Make sure it's well-formed.");
                return;
            }

            doc.getDocumentElement().normalize();
            NodeList recordList = doc.getElementsByTagName("record");

            if (recordList.getLength() == 0) {
                System.out.println("⚠️ No <record> elements found in XML.");
                return;
            }

            Set<String> foundFields = getAvailableFields((Element) recordList.item(0));
            List<String> validFields = new ArrayList<>();
            List<String> invalidFields = new ArrayList<>();

            for (String field : selectedFields) {
                if (foundFields.contains(field)) {
                    validFields.add(field);
                } else {
                    invalidFields.add(field);
                }
            }

            if (validFields.isEmpty()) {
                System.out.println("❗ None of the specified fields are valid. Available fields: " + foundFields);
                return;
            }

            if (!invalidFields.isEmpty()) {
                System.out.println("⚠️ Warning: The following fields do not exist and will be ignored: " + invalidFields);
            }

            System.out.println("["); // JSON array start

            for (int i = 0; i < recordList.getLength(); i++) {
                Element record = (Element) recordList.item(i);
                Map<String, String> jsonMap = new LinkedHashMap<>();

                for (String field : validFields) {
                    jsonMap.put(field, getTagValue(field, record));
                }

                System.out.print("  " + toJsonObject(jsonMap));
                if (i < recordList.getLength() - 1) {
                    System.out.print(",");
                }
                System.out.println();
            }

            System.out.println("]"); // JSON array end

        } catch (Exception e) {
            System.out.println("❗ Unexpected error: " + e.getMessage());
        }
    }

    private static Set<String> getAvailableFields(Element record) {
        NodeList children = record.getChildNodes();
        Set<String> fieldNames = new HashSet<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                fieldNames.add(node.getNodeName());
            }
        }
        return fieldNames;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0 && nodeList.item(0).getFirstChild() != null) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }

    private static String toJsonObject(Map<String, String> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        int count = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\": ");
            json.append("\"").append(escapeJson(entry.getValue())).append("\"");
            if (++count < map.size()) {
                json.append(", ");
            }
        }
        json.append("}");
        return json.toString();
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
