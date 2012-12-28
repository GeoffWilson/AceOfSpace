import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.ClassLoader.getSystemClassLoader;

public class Level
{
    private ConcurrentLinkedQueue<Enemy> enemies;
    private ConcurrentLinkedQueue<Polygon> collisionPoints;
    private BufferedImage texture;
    private Audio backgroundMusic;

    public Level()
    {
        try
        {
            enemies = new ConcurrentLinkedQueue<Enemy>();
            collisionPoints = new ConcurrentLinkedQueue<Polygon>();
            texture = ImageIO.read(getSystemClassLoader().getResourceAsStream("levels/test.jpg"));
            backgroundMusic = new Audio("level-1.vgz");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void beginLevel()
    {
        // Do level stuff here
        backgroundMusic.play(1, 10000);
    }

    public BufferedImage getImage()
    {
        return texture;
    }

    public void addEnemy(Enemy enemy)
    {
        enemies.offer(enemy);
    }

    public void moveEnemies(int playerX, int playerY)
    {
        for (Enemy e : enemies)
        {
            if (e.x > playerX) e.x -= e.moveX;
            if (e.x < playerX) e.x += e.moveX;
            if (e.y > playerY) e.y -= e.moveY;
            if (e.y < playerY) e.y += e.moveY;
        }
    }

    public ConcurrentLinkedQueue<Enemy> getEnemies()
    {
        return enemies;
    }

    public boolean checkCollision(int shotX, int shotY)
    {
        Enemy hitEnemy = null;

        for (Enemy e : enemies)
        {
            Rectangle r = new Rectangle(shotX, shotY, 5, 5);
            Point p = new Point(e.x, e.y);
            if (r.contains(p))
            {
                hitEnemy = e;
            }
        }

        if (hitEnemy == null) return false;

        enemies.remove(hitEnemy);
        return true;
    }
}

