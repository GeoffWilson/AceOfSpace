import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.ClassLoader.getSystemClassLoader;

/**
 * Manages a level
 *
 * @author Geoff Wilson
 * @version 1.0
 */
public class Level
{
    private ConcurrentLinkedQueue<Enemy> enemies;
    private ConcurrentLinkedQueue<Spawner> spawners;
    private ConcurrentLinkedQueue<StaticEntity> entities;
    private BufferedImage texture;
    private ConcurrentHashMap<String, Audio> sounds;
    public Polygon collisionPolygon;

    public Level()
    {
        try
        {
            collisionPolygon = new Polygon();
            collisionPolygon.addPoint(64, 64);
            collisionPolygon.addPoint(512, 64);
            collisionPolygon.addPoint(512, 96);
            collisionPolygon.addPoint(576, 96);
            collisionPolygon.addPoint(576, 160);
            collisionPolygon.addPoint(608, 160);
            collisionPolygon.addPoint(608, 224);
            collisionPolygon.addPoint(576, 224);
            collisionPolygon.addPoint(576, 288);
            collisionPolygon.addPoint(544, 288);
            collisionPolygon.addPoint(544, 416);
            collisionPolygon.addPoint(288, 416);
            collisionPolygon.addPoint(288, 480);
            collisionPolygon.addPoint(128, 480);
            collisionPolygon.addPoint(128, 384);
            collisionPolygon.addPoint(64, 384);
            collisionPolygon.addPoint(64, 64);

            enemies = new ConcurrentLinkedQueue<Enemy>();
            spawners = new ConcurrentLinkedQueue<Spawner>();
            sounds = new ConcurrentHashMap<String, Audio>();
            entities = new ConcurrentLinkedQueue<StaticEntity>();

            // Create three test spawners
            Spawner spawnerOne = new Spawner(this, 0, 2500);
            spawnerOne.setLocation(128, 128);
            Spawner spawnerTwo = new Spawner(this, 0, 2500);
            spawnerTwo.setLocation(512, 192);
            Spawner spawnerThree = new Spawner(this, 0, 2500);
            spawnerThree.setLocation(320, 384);

            spawners.add(spawnerOne);
            spawners.add(spawnerTwo);
            spawners.add(spawnerThree);

            texture = ImageIO.read(getSystemClassLoader().getResourceAsStream("levels/level_1-1.png"));
            sounds.put("music", new Audio("level-1.vgz"));
            sounds.put("pop", new Audio("pop.vgz"));
            sounds.put("done", new Audio("done.vgz"));
            sounds.get("pop").changeVolumne(2.0D);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addEntity(StaticEntity entity)
    {
        entities.add(entity);
    }

    public void beginLevel()
    {
        // Do level stuff here
        sounds.get("music").play(1, 10000);

        for (Spawner s : spawners)
        {
            s.activateSpanwer();
        }
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

    public ConcurrentLinkedQueue<Spawner> getSpawners()
    {
        return spawners;
    }

    public ConcurrentLinkedQueue<Enemy> getEnemies()
    {
        return enemies;
    }

    public ConcurrentLinkedQueue<StaticEntity> getEntities()
    {
        return entities;
    }

    public boolean checkGeometryCollision(int x, int y, int w, int h)
    {
        Area area = new Area(collisionPolygon);
        return !area.contains(new Rectangle(x, y, w, h));
    }

    public boolean checkEntityCollision(int shotX, int shotY)
    {
        Enemy hitEnemy = null;
        Rectangle r = new Rectangle(shotX, shotY, 8, 8);

        for (Enemy e : enemies)
        {
            if (!e.alive) continue;

            Rectangle p = new Rectangle(e.x, e.y, 64, 64);
            if (p.contains(r))
            {
                hitEnemy = e;
            }
        }

        if (hitEnemy != null)
        {
            Timer timer = new Timer();
            timer.schedule(new RemoveEnemyTask(hitEnemy), 1000);
            hitEnemy.changeAnimation("die", 100);
            hitEnemy.moveX = 0;
            hitEnemy.moveY = 0;
            hitEnemy.alive = false;
            //enemies.remove(hitEnemy);
            return true;
        }

        Spawner hitSpawner = null;

        for (Spawner s : spawners)
        {
            Rectangle p = new Rectangle(s.x, s.y, 32, 32);
            if (p.contains(r))
            {
                hitSpawner = s;
            }
        }

        if (hitSpawner != null)
        {
            if (--hitSpawner.health == 0)
            {
                hitSpawner.disableSpanwer();
                sounds.get("pop").play(1, 2);
                spawners.remove(hitSpawner);

                if (spawners.size() == 0)
                {
                    // Load test entities
                    StaticEntity downArrow = new StaticEntity(180, 436);
                    downArrow.loadAnimation("assets/ui/down_arrow_", 2, new int[]{0, 1});
                    downArrow.changeAnimation("south", 500);
                    this.addEntity(downArrow);
                    sounds.get("done").play(1, 2);
                }
            }
            return true;
        }

        return false;
    }

    private class RemoveEnemyTask extends TimerTask
    {
        private Enemy enemy;

        public RemoveEnemyTask(Enemy enemy)
        {
            this.enemy = enemy;
        }

        public void run()
        {
            enemies.remove(enemy);
        }

    }
}

