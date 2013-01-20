import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

public class Player
{
    public int x = 320;
    public int y = 256;
    public int moveX = 2;
    public int moveY = 2;
    public boolean shoot;
    public Directions direction;
    protected ConcurrentHashMap<String, Animation> animations;
    protected String currentAnimation;
    protected ConcurrentHashMap<String, Audio> sounds;

    public Player()
    {
        animations = new ConcurrentHashMap<String, Animation>();
        sounds = new ConcurrentHashMap<String, Audio>();

        Audio shootSound = new Audio("shot.vgz");
        sounds.put("shoot", shootSound);

        direction = Directions.SOUTH;

        int frameOrder[] = {0, 1, 0, 2};

        Animation south = new Animation(frameOrder);
        south.loadAnimation("assets/char_model/model_down_", 3);
        animations.put("south", south);

        Animation north = new Animation(frameOrder);
        north.loadAnimation("assets/char_model/model_up_", 3);
        animations.put("north", north);

        Animation east = new Animation(frameOrder);
        east.loadAnimation("assets/char_model/model_right_", 3);
        animations.put("east", east);

        Animation west = new Animation(frameOrder);
        west.loadAnimation("assets/char_model/model_left_", 3);
        animations.put("west", west);

        currentAnimation = Directions.SOUTH.name().toLowerCase();
        animations.get(currentAnimation).beginAnimation(150);
    }

    public void changeAnimation(String name)
    {
        if (name.contains("north")) name = "north";
        else if (!animations.containsKey(name)) name = "south";

        animations.get(currentAnimation).stopAnimation();
        currentAnimation = name;
        animations.get(name).beginAnimation(150);
    }

    public BufferedImage getFrame()
    {
        return animations.get(currentAnimation).getFrame();
    }

    public void updateDirection(Directions direction)
    {
        if (this.direction != direction)
        {
            this.direction = direction;
            changeAnimation(direction.name().toLowerCase());
        }
    }

    public void shoot()
    {
        this.shoot = true;
        sounds.get("shoot").play(1,2);
    }
}
