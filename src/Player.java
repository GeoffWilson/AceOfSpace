import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

public class Player
{
    public int x = 100;
    public int y = 100;
    public int moveX = 2;
    public int moveY = 2;
    public int realX;
    public int realY;
    public boolean shoot;
    public Directions direction;
    private ConcurrentHashMap<String, Animation> animations;
    private String currentAnimation;

    public Player()
    {
        animations = new ConcurrentHashMap<String, Animation>();
        direction = Directions.SOUTH;

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
        if (!animations.containsKey(name)) name = "south";

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
}
