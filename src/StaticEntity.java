import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an entity (item) on a level.
 *
 * @author Geoff Wilson
 * @version 1.0
 */
public class StaticEntity extends Player
{
    public StaticEntity(int x, int y)
    {
        super.x = x;
        super.y = y;
        super.animations = new ConcurrentHashMap<String, Animation>();
        super.direction = Directions.SOUTH;
    }

    public StaticEntity()
    {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void loadAnimation(String name, int frameCount, int[] frameOrder)
    {
        Animation a = new Animation(frameOrder);
        a.loadAnimation(name, frameCount);
        a.beginAnimation(1000);
        super.animations.put("south", a);
    }
}
