import java.util.concurrent.ConcurrentHashMap;

public class Enemy extends Player
{
    public boolean alive = true;

    public Enemy()
    {
        super.animations = new ConcurrentHashMap<String, Animation>();
        super.sounds = new ConcurrentHashMap<String, Audio>();

        super.direction = Directions.SOUTH;
        super.moveX = 1;
        super.moveY = 1;

        Animation south = new Animation(new int[]{0});
        south.loadAnimation("assets/enemy_one/model_down_", 1);
        south.beginAnimation(1500);
        super.animations.put("south", south);

        Animation die = new Animation(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        die.loadAnimation("assets/effects/explode/die_", 11);
        super.animations.put("die", die);
    }
}
