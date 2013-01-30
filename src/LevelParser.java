import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.io.InputStream;

import static java.lang.ClassLoader.getSystemClassLoader;

public class LevelParser extends DefaultHandler
{
    public Level readLevelData(String name, Control control)
    {
        try
        {
            Level level = new Level(control);

            InputStream textureStream = getSystemClassLoader().getResourceAsStream("assets/levels/" + name + ".png");
            level.setTexture(ImageIO.read(textureStream));

            InputStream in = getSystemClassLoader().getResourceAsStream("assets/levels/" + name + ".level");
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

            // Collision Polygon
            expr = xpath.compile("//level/polygon/point");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            Polygon polygon = new Polygon();
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Element element = (Element) nodes.item(i);
                x = Integer.parseInt(element.getAttribute("x"));
                y = Integer.parseInt(element.getAttribute("y"));
                polygon.addPoint(x * 32, y * 32);
            }
            level.setCollisionPolygon(polygon);

            // Load the entities
            expr = xpath.compile("//level/entities/entity");
            nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Element entityElement = (Element) nodes.item(i);
                int type = Integer.parseInt(entityElement.getAttribute("type"));

                expr = xpath.compile("//level/entities/entity[" + (i + 1) + "]/location");
                locationNode = (Element) expr.evaluate(doc, XPathConstants.NODE);
                x = Integer.parseInt(locationNode.getAttribute("x"));
                y = Integer.parseInt(locationNode.getAttribute("y"));

                expr = xpath.compile("//level/entities/entity[" + (i + 1) + "]/data");
                locationNode = (Element) expr.evaluate(doc, XPathConstants.NODE);
                int delay = Integer.parseInt(locationNode.getAttribute("delay"));
                int rate = Integer.parseInt(locationNode.getAttribute("rate"));

                Spawner s = new Spawner(level, 0, rate);
                s.setLocation(x, y);

                level.addSpanwer(s);
            }
            return level;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
