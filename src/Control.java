import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * Manages the game.
 */
public class Control implements Runnable
{
    // Assets
    public static SpriteCache cache = new SpriteCache();

    // Flow control
    private boolean started;
    private boolean dead;
    private boolean gameOver;
    private boolean running = true;

    // Input Control
    private JoyPad gamePad;

    // Levels, this needs testing to see how many we can load into memory at once!
    private ConcurrentHashMap<Integer, Level> levels;
    private Level currentLevel;

    // Player data
    private Player player;

    // Sounds
    public ConcurrentHashMap<String, Audio> sounds;

    // Rendering
    private BufferStrategy buffer;
    private ConcurrentLinkedQueue<Renderable> renderQueue;

    public Control(JoyPad gamePad)
    {
        // Ensure all objects are initialized
        this.gamePad = gamePad;
        levels = new ConcurrentHashMap<Integer, Level>();
        player = new Player();
        sounds = new ConcurrentHashMap<String, Audio>();
        renderQueue = new ConcurrentLinkedQueue<Renderable>();

        // Load the music
        sounds.put("music", new Audio("level-1.vgz"));
        sounds.get("music").play(1, 10000);

        // Create the frame
        configureFrame();
    }

    private void configureFrame()
    {
        Frame baseFrame = new Frame("Ace Of Space v0.3");
        baseFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                running = false;
            }
        });

        baseFrame.setIgnoreRepaint(true);
        baseFrame.setResizable(false);
        baseFrame.setLayout(new BorderLayout());
        baseFrame.setLocationByPlatform(true);

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 640, 480);
        canvas.setIgnoreRepaint(true);

        baseFrame.add(canvas, BorderLayout.CENTER);
        baseFrame.pack();

        canvas.createBufferStrategy(2);
        buffer = canvas.getBufferStrategy();

        baseFrame.setVisible(true);

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();

        if (graphicsDevice.isDisplayChangeSupported())
        {
            int colorDepth = 32;
            graphicsDevice.setDisplayMode(new DisplayMode(640, 480, colorDepth, DisplayMode.REFRESH_RATE_UNKNOWN));
        }

        // Add the title screen to the render queue
        TitleScreen titleScreen = new TitleScreen();
        renderQueue.add(titleScreen);

    }

    public Player getPlayer()
    {
        return this.player;
    }

    public void run()
    {
        while (running)
        {
            updateInput();
            if (started) logic();
            render();
            LockSupport.parkNanos(16666667);
        }

        System.exit(0);
    }


    private void changeLevel(String destination)
    {
        currentLevel = levels.get(destination);
    }

    private void render()
    {
        Graphics2D g = (Graphics2D) buffer.getDrawGraphics();
        for (Renderable r : renderQueue)
        {
            r.render(g);
        }
        buffer.show();
    }

    private void logic()
    {
        for (Shot s : currentLevel.getShots())
        {
            if (currentLevel.checkGeometryCollision(s.x, s.y, 8, 8))
            {
                currentLevel.removeShot(s);
            }
            else if (currentLevel.checkEntityCollision(s.x, s.y))
            {
                currentLevel.removeShot(s);
            }
        }

        currentLevel.moveEnemies(player.x, player.y);
    }

    private void updateInput()
    {
        int x = gamePad.getXAxis();
        int y = gamePad.getYAxis();
        boolean shoot = gamePad.getButton(0);

        if (started)
        {
            // Work out player details
            if (!currentLevel.checkGeometryCollision(player.x + (x * player.moveX) + 16, player.y + (y * player.moveY) + 40, 32, 24))
            {
                player.x += x * player.moveX;
                player.y += y * player.moveY;
            }

            ActivationVector v = currentLevel.checkActivationCollision(player.x, player.y, 32, 24);
            if (v != null)
            {
                switch (v.getAction())
                {
                    case 1: // Level switch
                        boolean clear = v.getFlag(0);
                        int levelID = v.getActionDataID();

                        changeLevel("50.50");

                        ActivationVector goBack = new ActivationVector(1, 1);
                        goBack.setFlag(0, true);
                        goBack.addPoint(4 * 32, 0);
                        goBack.addPoint(9 * 32, 0);
                        goBack.addPoint(9 * 32, 32);
                        goBack.addPoint(4 * 32, 32);
                        currentLevel.addCollisionVector(goBack);

                        if (!clear) currentLevel.beginLevel(false);

                        break;
                }
            }

            if (x == 0 && y == 1) player.updateDirection(Directions.SOUTH);
            else if (x == 1 && y == 1) player.updateDirection(Directions.SOUTH_EAST);
            else if (x == 1 && y == 0) player.updateDirection(Directions.EAST);
            else if (x == 1 && y == -1) player.updateDirection(Directions.NORTH_EAST);
            else if (x == 0 && y == -1) player.updateDirection(Directions.NORTH);
            else if (x == -1 && y == -1) player.updateDirection(Directions.NORTH_WEST);
            else if (x == -1 && y == 0) player.updateDirection(Directions.WEST);
            else if (x == -1 && y == 1) player.updateDirection(Directions.SOUTH_WEST);

            if (shoot && !player.shoot)
            {
                player.shoot = true;
                player.shoot();

                Shot newShot = new Shot();
                newShot.x = player.x + 26;
                newShot.y = player.y + 26;
                int shotIncX = 0;
                int shotIncY = 0;

                if (x == 0 && y == 0)
                {
                    switch (player.direction)
                    {
                        case NORTH:
                            shotIncY = -4;
                            break;
                        case SOUTH:
                            shotIncY = 4;
                            break;
                        case EAST:
                            shotIncX = 4;
                            break;
                        case WEST:
                            shotIncX = -4;
                            break;
                        case NORTH_EAST:
                            shotIncX = 4;
                            shotIncY = -4;
                            break;
                        case NORTH_WEST:
                            shotIncX = -4;
                            shotIncY = -4;
                            break;
                        case SOUTH_EAST:
                            shotIncX = 4;
                            shotIncY = 4;
                            break;
                        case SOUTH_WEST:
                            shotIncX = -4;
                            shotIncY = 4;
                            break;
                    }
                }
                else
                {
                    shotIncX = x == 0 ? 0 : x > 0 ? 4 : -4;
                    shotIncY = y == 0 ? 0 : y > 0 ? 4 : -4;
                }

                newShot.xInc = shotIncX;
                newShot.yInc = shotIncY;

                currentLevel.addShot(newShot);
            }
            else if (!shoot && player.shoot) player.shoot = false;
        }
        else
        {
            if (shoot)
            {
                renderQueue.remove();
                started = true;
            }
        }
    }
}
