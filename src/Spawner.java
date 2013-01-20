import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Spawns an enemy of the specified type at the supplied rate.
 * Spawners can't move, so only have a single animation instead of a list of
 * directional animations (this may change in the future to add a "spawn" animation
 *
 * @author Geoff Wilson
 * @version 1.0
 *
 */
public class Spawner
{
    private int type; // Type of enemy to spawn
    private Level owner; // What level this spawner is creating enemies on

    // Location of the spawner
    public int x;
    public int y;

    // Health of the spawner
    public int health = 10;

    // Spawner animations
    private Animation animation;

    // Spawn timer
    private Timer timer;
    private int spawnRate; // Delay between enemy spawn rates (in milliseconds)

    public Spawner(Level owner, int type, int spawnRate)
    {
        this.owner = owner;
        this.type = type;
        this.spawnRate = spawnRate;
        this.animation = new Animation(new int[] {0});
        animation.loadAnimation("assets/spawner/spawner_", 1);
        animation.beginAnimation(25000);
    }

    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public BufferedImage getFrame()
    {
        return animation.getFrame();
    }

    public void activateSpanwer()
    {
        timer = new Timer();
        timer.schedule(new SpawnTask(), spawnRate, spawnRate);
    }

    public void disableSpanwer()
    {
        timer.cancel();
    }

    public void spawnEnemy()
    {
        Enemy e = new Enemy();
        e.changeAnimation("south");
        e.x = x;
        e.y = y;
        owner.addEnemy(e);
    }

    private class SpawnTask extends TimerTask
    {
        public void run()
        {
            spawnEnemy();
        }
    }
}
