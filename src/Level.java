import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.ClassLoader.getSystemClassLoader;

public class Level
{
    private ConcurrentLinkedQueue<Enemy> enemies;
    private ConcurrentLinkedQueue<Spawner> spanwers;
    private ConcurrentLinkedQueue<Polygon> collisionPoints;
    private BufferedImage texture;
    private Audio backgroundMusic;

    public Level()
    {
        try
        {
            enemies = new ConcurrentLinkedQueue<Enemy>();
            spanwers = new ConcurrentLinkedQueue<Spawner>();
            collisionPoints = new ConcurrentLinkedQueue<Polygon>();
            texture = ImageIO.read(getSystemClassLoader().getResourceAsStream("levels/test.jpg"));
            backgroundMusic = new Audio("level-1.vgz");
            backgroundMusic.changeVolumne(0.5D);
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

    public int getEnemyCount()
    {
        return enemies.size();
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
            Rectangle p = new Rectangle(e.x, e.y, 64 ,64);
            if (p.contains(r))
            {
                hitEnemy = e;
            }
        }

        if (hitEnemy == null) return false;

        enemies.remove(hitEnemy);
        return true;
    }
}

