import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import java.io.File;
import java.util.*;

public class XMLReader{
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify the fields to output in JSON. Example:");
            System.out.println("java XMLToJsonSelector name country address");
            return;
        }

        List<String> selectedFields = Arrays.asList(args);

        try {
            File xmlFile = new File("data.xml"); // Adjust if needed
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList recordList = doc.getElementsByTagName("record");

            System.out.println("["); // Begin JSON array

            for (int i = 0; i < recordList.getLength(); i++) {
                Node recordNode = recordList.item(i);

                if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element record = (Element) recordNode;

                    Map<String, String> jsonMap = new LinkedHashMap<>();
                    for (String field : selectedFields) {
                        String value = getTagValue(field, record);
                        jsonMap.put(field, value);
                    }

                    System.out.print("  " + toJsonObject(jsonMap));
                    if (i < recordList.getLength() - 1) {
                        System.out.print(",");
                    }
                    System.out.println();
                }
            }

            System.out.println("]"); // End JSON array

        } catch (Exception e) {
            e.printStackTrace();
        }
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
