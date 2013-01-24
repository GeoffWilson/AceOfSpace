import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.jar.Attributes;

import static java.lang.ClassLoader.getSystemClassLoader;

public class LevelParser extends DefaultHandler
{
    private Level level;
    private String nodeText;

    private boolean inBasic;
    private boolean inPolygon;
    private boolean inEntities;
    private boolean inActions;

    public LevelParser(Level level)
    {
        this.level = level;
    }

    public boolean readLevelData(String name)
    {
        try
        {
            InputStream in = getSystemClassLoader().getResourceAsStream("/assets/levels/" + name + ".level");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(in);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            // Get the level name
            XPathExpression expr = xpath.compile("//level/basic/name/text()");
            String levelName = (String) expr.evaluate(doc, XPathConstants.STRING);
            level.setName(levelName);

            // Get the music
            expr = xpath.compile("//level/basic/music/text()");
            String music = (String) expr.evaluate(doc, XPathConstants.STRING);

            // Get the location
            expr = xpath.compile("//level/basic/location");
            Element locationNode = (Element) expr.evaluate(doc, XPathConstants.NODE);
            int x = Integer.parseInt(locationNode.getAttribute("x"));
            int y = Integer.parseInt(locationNode.getAttribute("y"));
            int z = Integer.parseInt(locationNode.getAttribute("z"));
            level.setLocation(x, y, z);

            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}
