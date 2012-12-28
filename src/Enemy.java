import java.util.concurrent.ConcurrentHashMap;

public class Enemy extends Player
{
    public Enemy()
    {
        super.animations = new ConcurrentHashMap<String, Animation>();
        super.sounds = new ConcurrentHashMap<String, Audio>();

        super.direction = Directions.SOUTH;
        super.moveX = 1;
        super.moveY = 1;
    }
}
