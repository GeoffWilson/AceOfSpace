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
    private ConcurrentLinkedQueue<ActivationVector> collisionVectors;
    private BufferedImage texture;
    private ConcurrentHashMap<String, Audio> sounds;
    public Polygon collisionPolygon;
    private Point startLocation;
    private int levelID;

    public Level()
    {
        sounds = new ConcurrentHashMap<String, Audio>();
        sounds.put("music", new Audio("level-1.vgz"));
        sounds.put("pop", new Audio("pop.vgz"));
        sounds.put("done", new Audio("done.vgz"));
        sounds.get("pop").changeVolumne(2.0D);
        changeLevel(1, false, new Point(10 * 32 , 7 * 32));
    }

    public Point getStartLocation()
    {
        return startLocation;
    }

    /**
     * Temporary function to allow level switching before a proper structure for levels is created
     * @param id The level number to load
     */
    public void changeLevel(int id, boolean clear, Point startLocation)
    {
        levelID = id;
        this.startLocation = startLocation;

        if (spawners != null)
        {
            for (Spawner s : spawners) s.disableSpanwer();
        }

        enemies = new ConcurrentLinkedQueue<Enemy>();
        spawners = new ConcurrentLinkedQueue<Spawner>();
        entities = new ConcurrentLinkedQueue<StaticEntity>();
        collisionVectors = new ConcurrentLinkedQueue<ActivationVector>();

        try
        {
            switch (id)
            {
                case 1:

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

                    if (!clear)
                    {
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
                    }
                    else
                    {
                        // Load test entities
                        StaticEntity downArrow = new StaticEntity(180, 436);
                        downArrow.loadAnimation("assets/ui/down_arrow_", 2, new int[]{0, 1});
                        downArrow.changeAnimation("south", 500);
                        this.addEntity(downArrow);

                        ActivationVector v = new ActivationVector(1, 2);
                        v.addPoint(4 * 32, 14 * 32);
                        v.addPoint(9 * 32, 14 * 32);
                        v.addPoint(9 * 32, 15 * 32);
                        v.addPoint(4 * 32, 15* 32);
                        collisionVectors.add(v);
                    }

                    texture = ImageIO.read(getSystemClassLoader().getResourceAsStream("levels/level_1-1.png"));
                    break;

                case 2:

                    collisionPolygon = new Polygon();
                    collisionPolygon.addPoint(0, 9 * 32);
                    collisionPolygon.addPoint(2 * 32, 9 * 32);
                    collisionPolygon.addPoint(2 * 32, 11 * 32);
                    collisionPolygon.addPoint(4 * 32, 11 * 32);
                    collisionPolygon.addPoint(4 * 32, 0);
                    collisionPolygon.addPoint(9 * 32, 0);
                    collisionPolygon.addPoint(9 * 32, 2 * 32);
                    collisionPolygon.addPoint(15 * 32, 2 * 32);
                    collisionPolygon.addPoint(15 * 32, 5 * 32);
                    collisionPolygon.addPoint(14 * 32, 5 * 32);
                    collisionPolygon.addPoint(14 * 32, 7 * 32);
                    collisionPolygon.addPoint(16 * 32, 7 *-32);
                    collisionPolygon.addPoint(16 * 32, 9 * 32);
                    collisionPolygon.addPoint(17 * 32, 9 * 32);
                    collisionPolygon.addPoint(17 * 32, 13 * 32);
                    collisionPolygon.addPoint(0, 13 * 32);
                    collisionPolygon.addPoint(0, 9 * 32);

                    if (!clear)
                    {
                        // Create three test spawners
                        Spawner spawnerOne = new Spawner(this, 0, 2500);
                        spawnerOne.setLocation(14 * 32, 128);
                        Spawner spawnerTwo = new Spawner(this, 5000, 500);
                        spawnerTwo.setLocation(16 * 32, 12 * 32);

                        spawners.add(spawnerOne);
                        spawners.add(spawnerTwo);
                    }

                    texture = ImageIO.read(getSystemClassLoader().getResourceAsStream("levels/level_1-2.png"));
                    sounds.put("music", new Audio("level-1.vgz"));
                    sounds.put("pop", new Audio("pop.vgz"));
                    sounds.put("done", new Audio("done.vgz"));
                    sounds.get("pop").changeVolumne(2.0D);
                    break;
            }
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

    public void addCollisionVector(ActivationVector polygon)
    {
        collisionVectors.add(polygon);
    }

    public void beginLevel(boolean startMusic)
    {
        if (startMusic) sounds.get("music").play(1, 10000);

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

    public ActivationVector checkActivationCollision(int x, int y, int w, int h)
    {
        for (ActivationVector v : collisionVectors)
        {
            Area area = new Area(v.getPolygon());
            if (area.contains(x + 16, y + 40)) return v;
        }

        return null;
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
                    if (levelID == 1)
                    {
                        StaticEntity downArrow = new StaticEntity(180, 436);
                        downArrow.loadAnimation("assets/ui/down_arrow_", 2, new int[]{0, 1});
                        downArrow.changeAnimation("south", 500);
                        this.addEntity(downArrow);

                        ActivationVector v = new ActivationVector(1, 2);
                        v.addPoint(4 * 32, 14 * 32);
                        v.addPoint(9 * 32, 14 * 32);
                        v.addPoint(9 * 32, 15 * 32);
                        v.addPoint(4 * 32, 15* 32);
                        collisionVectors.add(v);

                        sounds.get("done").play(1, 2);
                    }
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

