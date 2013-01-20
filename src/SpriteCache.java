import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches images so they do not need to be reloaded all the time
 *
 * @author Geoff Wilson
 * @version 1.0
 */
public class SpriteCache
{
    private ConcurrentHashMap<String, BufferedImage> sprites;

    public SpriteCache()
    {
        sprites = new ConcurrentHashMap<String, BufferedImage>();
    }

    public boolean hasSprite(String name)
    {
        return sprites.containsKey(name);
    }

    public BufferedImage getSprite(String name)
    {
        return sprites.get(name);
    }

    public void putSprite(BufferedImage sprite, String name)
    {
        sprites.put(name, sprite);
    }
}
