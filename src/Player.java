import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Player
{
    private ConcurrentHashMap<String, Animation> animations;

    public int x = 100;
    public int y = 100;

    public int moveX = 2;
    public int moveY = 2;

    public int realX;
    public int realY;

    public boolean shoot;

    private String currentAnimation;
    private int animationDelay = 150;
    public Directions direction;

    public Player()
    {
        animations = new ConcurrentHashMap<String, Animation>();

        int frameOrder[] = {0, 1, 0, 2};

        Animation south = new Animation(frameOrder);
        south.loadAnimation("assets/char_model/model_down_", 3);
        animations.put("south", south);

        Animation north = new Animation(frameOrder);
        north.loadAnimation("assets/char_model/model_up_", 3);
        animations.put("north", north);

        currentAnimation = Directions.SOUTH.name().toLowerCase();
        animations.get(currentAnimation).beginAnimation(150);
    }

    public void changeAnimation(String name)
    {
        animations.get(currentAnimation).stopAnimation();
        currentAnimation = name;
        animations.get(name).beginAnimation(animationDelay);
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
}
