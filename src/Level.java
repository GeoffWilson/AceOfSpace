import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages a level
 *
 * @author Geoff Wilson
 * @version 1.0
 */
public class Level implements Renderable
{
    // Basic Data
    private String name;
    private BufferedImage texture;
    private Audio music;

    // Location
    private int x;
    private int y;
    private int z;

    // Bounding polygon
    private Polygon collisionPolygon;

    // Entities
    private ConcurrentLinkedQueue<Enemy> enemies;
    private ConcurrentLinkedQueue<Spawner> spawners;
    private ConcurrentLinkedQueue<StaticEntity> entities;
    private ConcurrentLinkedQueue<ActivationVector> collisionVectors;
    private ConcurrentLinkedQueue<Shot> shots;

    // Control object
    private Control control;

    public Level(Control control)
    {
        this.control = control;
        this.enemies = new ConcurrentLinkedQueue<Enemy>();
        this.spawners = new ConcurrentLinkedQueue<Spawner>();
        this.entities = new ConcurrentLinkedQueue<StaticEntity>();
        this.collisionVectors = new ConcurrentLinkedQueue<ActivationVector>();
        this.shots = new ConcurrentLinkedQueue<Shot>();
    }

    /**
     * Interface implementation, each level is capable of rendering itself.
     *
     * @param graphics The graphics object to render to (provided by Control)
     */
    public void render(Graphics2D graphics)
    {
        graphics.setColor(Color.BLACK);
        graphics.drawImage(texture, 0, 0, null);

        for (Shot s : shots)
        {
            if (s.x > 640 || s.x < 0) shots.remove(s);
            if (s.y > 480 || s.y < 0) shots.remove(s);

            s.inc();
            graphics.setColor(Color.BLUE);
            graphics.fillOval(s.x, s.y, 7, 7);
            graphics.setColor(Color.WHITE);
            graphics.drawOval(s.x, s.y, 8, 8);
        }

        for (Spawner s : spawners)
        {
            graphics.drawImage(s.getFrame(), s.x, s.y, null);
        }

        for (Enemy e : enemies)
        {
            graphics.drawImage(e.getFrame(), e.x, e.y, null);
        }

        for (StaticEntity s : entities)
        {
            graphics.drawImage(s.getFrame(), s.x, s.y, null);
        }

        Player p = control.getPlayer();
        graphics.drawImage(p.getFrame(), p.x, p.y, null);

        // Draws the collision polygon (for debugging)
        graphics.setColor(Color.RED);
        graphics.draw(collisionPolygon);
    }

    public void setLocation(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setCollisionPolygon(Polygon polygon)
    {
        this.collisionPolygon = polygon;
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
        if (startMusic) control.sounds.get("music").play(1, 10000);
        for (Spawner s : spawners)
        {
            s.activateSpanwer();
        }
    }

    public void setTexture(BufferedImage texture)
    {
        this.texture = texture;
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

    public void addSpanwer(Spawner s)
    {
        this.spawners.add(s);
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
        Rectangle rectangle = new Rectangle(shotX, shotY, 8, 8);

        for (Enemy enemy : enemies)
        {
            if (!enemy.alive) continue;
            Area enemyArea = new Area(new Rectangle(enemy.x, enemy.y, 64, 64));
            if (enemyArea.intersects(rectangle)) hitEnemy = enemy;
        }

        if (hitEnemy != null)
        {
            hitEnemy.changeAnimation("die", 100);
            hitEnemy.moveX = 0;
            hitEnemy.moveY = 0;
            hitEnemy.alive = false;
            return true;
        }

        Spawner hitSpawner = null;
        for (Spawner spawner : spawners)
        {
            Area spawnerArea = new Area(new Rectangle(spawner.x, spawner.y, 32, 32));
            if (spawnerArea.intersects(rectangle)) hitSpawner = spawner;
        }

        if (hitSpawner != null)
        {
            if (--hitSpawner.health == 0)
            {
                hitSpawner.disableSpanwer();
                control.sounds.get("pop").play(1, 2);
                spawners.remove(hitSpawner);
            }

            return true;
        }

        return false;
    }

    public void addShot(Shot s)
    {
        shots.add(s);
    }

    public ConcurrentLinkedQueue<Shot> getShots()
    {
        return this.shots;
    }

    public void removeShot(Shot s)
    {
        shots.remove(s);
    }
}

