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

        Animation south = new Animation(new int[] {0});
        south.loadAnimation("assets/baddie_", 1);
        south.beginAnimation(150);
        super.animations.put("south", south);
    }
}
