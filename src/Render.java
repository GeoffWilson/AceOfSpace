import ui.MessageWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

import static java.lang.ClassLoader.getSystemClassLoader;

/**
 * Renders the main game to the screen
 */
public class Render implements Runnable
{
    // Sprite Cache
    public static SpriteCache cache = new SpriteCache();

    private Graphics2D graphics;
    private BufferStrategy buffer;
    private JoyPad gamePad;
    private Player player;
    private ConcurrentLinkedQueue<Shot> shots;
    private Level currentLevel;
    private boolean running = true;
    private boolean gameStarted = false;

    private BufferedImage titleScreen;

    private boolean UIGameLock = false;

    private class Shot
    {
        public int x;
        public int y;
        public int xInc;
        public int yInc;

        public void inc()
        {
            x += xInc;
            y += yInc;
        }
    }

    public Render(JoyPad gamePad)
    {
        this.gamePad = gamePad;
        this.setupGame();
        this.shots = new ConcurrentLinkedQueue<Shot>();
        this.currentLevel = new Level();

        try
        {
            InputStream in = getSystemClassLoader().getResourceAsStream("assets/ui/temp_title.png");
            titleScreen = ImageIO.read(in);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Frame baseFrame = new Frame("Ace Of Space v0.2");
        baseFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);    //To change body of overridden methods use File | Settings | File Templates.
                running = false;
            }
        });
        baseFrame.setIgnoreRepaint(true);
        baseFrame.setResizable(false);
        //baseFrame.setPreferredSize(dimension);
        //baseFrame.setBounds(0, 0, 640, 480);
        baseFrame.setLayout(new BorderLayout());
        baseFrame.setLocationByPlatform(true);

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 640, 480);
        canvas.setIgnoreRepaint(true);

        baseFrame.add(canvas, BorderLayout.CENTER);
        baseFrame.pack();

        canvas.createBufferStrategy(2);
        buffer = canvas.getBufferStrategy();

        graphics = (Graphics2D) buffer.getDrawGraphics();

        baseFrame.setVisible(true);

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();

        if (graphicsDevice.isDisplayChangeSupported())
        {
            int colorDepth = 32;
            graphicsDevice.setDisplayMode(new DisplayMode(640, 480, colorDepth, DisplayMode.REFRESH_RATE_UNKNOWN));
        }


    }

    private void startGame()
    {
        LockSupport.parkNanos(500000000L);
        currentLevel.beginLevel();
        gameStarted = true;
    }

    private void setupGame()
    {
        player = new Player();
    }

    public void run()
    {
        while (running)
        {
            this.updateInput();

            if (!UIGameLock) this.logic();

            this.render();
            LockSupport.parkNanos(16666666L);
        }

        System.exit(0);
    }

    private void logic()
    {
        for (Shot s : shots)
        {
            if (currentLevel.checkGeometryCollision(s.x, s.y, 8, 8))
            {
                shots.remove(s);
            }
            else if (currentLevel.checkEntityCollision(s.x, s.y))
            {
                shots.remove(s);
            }
        }

        currentLevel.moveEnemies(player.x, player.y);
    }

    private void render()
    {
        graphics = (Graphics2D) buffer.getDrawGraphics();

        if (gameStarted)
        {
            graphics.setColor(Color.BLACK);
            graphics.drawImage(currentLevel.getImage(), 0, 0, null);

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

            for (Spawner s : currentLevel.getSpawners())
            {
                graphics.drawImage(s.getFrame(), s.x, s.y, null);
            }

            for (Enemy e : currentLevel.getEnemies())
            {
                graphics.drawImage(e.getFrame(), e.x, e.y, null);
            }

            for (StaticEntity s : currentLevel.getEntities())
            {
                graphics.drawImage(s.getFrame(), s.x, s.y, null);
            }

            graphics.drawImage(player.getFrame(), player.x, player.y, null);

            if (UIGameLock)
            {
                MessageWindow msgWindow = new MessageWindow(graphics);
                msgWindow.setText("Hello World");
                msgWindow.render();
            }

            graphics.setColor(Color.WHITE);
            graphics.drawString(Integer.toString(currentLevel.getEnemyCount()), 10, 20);
        }
        else
        {
            graphics.drawImage(titleScreen, 0, 0, null);
        }

        buffer.show();
    }

    private void updateInput()
    {
        int x = gamePad.getXAxis();
        int y = gamePad.getYAxis();
        boolean shoot = gamePad.getButton(0);

        if (gameStarted)
        {
            UIGameLock = gamePad.getButton(1);

            // Work out player details
            if (!currentLevel.checkGeometryCollision(player.x + (x * player.moveX) + 16, player.y + (y * player.moveY) + 40, 32, 24))
            {
                player.x += x * player.moveX;
                player.y += y * player.moveY;
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

                shots.add(newShot);
            }
            else if (!shoot && player.shoot) player.shoot = false;
        }
        else
        {
            if (shoot) this.startGame();
        }
    }
}
