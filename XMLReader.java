import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import java.io.File;

public class XMLReader{
    public static void main(String[] args) {
        try {
            File xmlFile = new File("data.xml"); // Make sure this is the correct path to your XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();
            NodeList recordList = doc.getElementsByTagName("record");

            for (int i = 0; i < recordList.getLength(); i++) {
                Node recordNode = recordList.item(i);

                if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element record = (Element) recordNode;

                    String name = getTagValue("name", record);
                    String postalZip = getTagValue("postalZip", record);
                    String region = getTagValue("region", record);
                    String country = getTagValue("country", record);
                    String address = getTagValue("address", record);
                    String list = getTagValue("list", record);

                    System.out.println("Record #" + (i + 1));
                    System.out.println("Name: " + name);
                    System.out.println("Postal Zip: " + postalZip);
                    System.out.println("Region: " + region);
                    System.out.println("Country: " + country);
                    System.out.println("Address: " + address);
                    System.out.println("List: " + list);
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
        return "";
    }
}
