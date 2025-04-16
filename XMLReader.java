import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class XMLReader{
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify the fields to print. Example: java XMLFieldSelector name country address");
            return;
        }

        List<String> selectedFields = Arrays.asList(args);

        try {
            File xmlFile = new File("data.xml"); // Adjust path if needed
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList recordList = doc.getElementsByTagName("record");

            for (int i = 0; i < recordList.getLength(); i++) {
                Node recordNode = recordList.item(i);

                if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element record = (Element) recordNode;
                    System.out.println("Record #" + (i + 1));

                    for (String field : selectedFields) {
                        String value = getTagValue(field, record);
                        System.out.println(capitalize(field) + ": " + value);
                    }

                    System.out.println("-------------------------");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0 && nodeList.item(0).getFirstChild() != null) {
            return nodeList.item(0).getTextContent();
        }
        return "(not found)";
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
