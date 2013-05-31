package nl.avans.threading;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal Slegtenhorst
 * Date: 30-5-13
 * Time: 20:49
 */
public class SettingsIOHandler {

    /**
     *  Read the settings from settings.xml document
     *   and store values in static Settings class
     */
    public static void loadSettings()
    {
        try {
            //Create DOM tree from file//
            File xmlFile = new File("settings.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();   //http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work

            //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            //Handle DB-Settings children//
            NodeList nodeList = doc.getElementsByTagName("DB-settings");
            //if (nodeList.getLength() == 0)
            //    throw new Exception("Settings file has no element: 'DB-settings'");

            Node node = nodeList.item(0);
            Element element;
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                element = (Element) node;
                Settings.dbUrl = element.getElementsByTagName("URL").item(0).getTextContent();
                Settings.dbName = element.getElementsByTagName("database").item(0).getTextContent();
                Settings.dbUsername = element.getElementsByTagName("username").item(0).getTextContent();
                Settings.dbPassword = element.getElementsByTagName("password").item(0).getTextContent();
            }

            //Handle Server-settings children//
            nodeList = doc.getElementsByTagName("server-settings");
            node = nodeList.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                element = (Element) node;
                Settings.webPort = Integer.parseInt(element.getElementsByTagName("web-port").item(0).getTextContent());
                Settings.controlPort = Integer.parseInt(element.getElementsByTagName("control-port").item(0).getTextContent());
                Settings.webRoot = element.getElementsByTagName("web-root").item(0).getTextContent();
                Settings.defaultPage = element.getElementsByTagName("default-page").item(0).getTextContent();
                Settings.logLocation = element.getElementsByTagName("log-location").item(0).getTextContent();
            }
            System.out.println("Settings loaded into Application");

        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfigurationException: " + e.getMessage());
        } catch (SAXException e) {
            System.out.println("SAXException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: document settings.xml is not well formed");
        }
    }

    /**
     *  Store values from @param in settings.xml file
     */
    public static void saveChanges(int webPort, int controlPort, String webRoot, String defaultPage)
    {
        //TODO prevent xml-injection by escaping characters like '<' and "'"
    }
}
