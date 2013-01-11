import ui.MessageWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * Renders the main game to the screen
 */
public class Render implements Runnable
{
    private Graphics2D graphics;
    private BufferStrategy buffer;
    private JoyPad gamePad;
    private Player player;
    private ConcurrentLinkedQueue<Shot> shots;
    private Level currentLevel;
    private boolean running = true;
    private BufferedImage tmpEnemyImage;

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
            tmpEnemyImage = ImageIO.read(getClass().getResourceAsStream("assets/baddie.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Dimension dimension = new Dimension(640, 480);
        Frame baseFrame = new Frame("Ace Of Space v0.1");
        baseFrame.setPreferredSize(dimension);
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
        baseFrame.setBounds(0, 0, 640, 480);
        baseFrame.setLayout(new BorderLayout());

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

        if (false)
        {
            baseFrame.setUndecorated(true);
            if (graphicsDevice.isFullScreenSupported())
            {
                graphicsDevice.setFullScreenWindow(baseFrame);
            }
            else
            {
                System.out.println("Full screen is not supported on your system :(");
            }
        }
        if (graphicsDevice.isDisplayChangeSupported())
        {
            int colorDepth = 32;
            graphicsDevice.setDisplayMode(new DisplayMode(640, 480, colorDepth, DisplayMode.REFRESH_RATE_UNKNOWN));
        }

        currentLevel.beginLevel();
    }

    private void setupGame()
    {
        player = new Player();
        player.x = 100;
        player.y = 100;
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
            if (currentLevel.checkCollision(s.x, s.y))
            {
                shots.remove(s);
            }
        }

        int delta = 95;
        int random = (int) (Math.random() * 100);

        if (random > delta)
        {
            Enemy newEnemy = new Enemy();
            newEnemy.x = 150;
            newEnemy.y = 300;
            currentLevel.addEnemy(newEnemy);
        }

        currentLevel.moveEnemies(player.x, player.y);
    }

    private void render()
    {
        graphics = (Graphics2D)buffer.getDrawGraphics();

        graphics.setColor(Color.BLACK);
        graphics.drawImage(currentLevel.getImage(), 0, 0, null);

        for (Shot s : shots)
        {
            if (s.x > 640 || s.x < 0) shots.remove(s);
            if (s.y > 480 || s.y < 0) shots.remove(s);

            s.inc();
            graphics.setColor(Color.YELLOW);
            graphics.fillOval(s.x, s.y, 5,5);
        }

        for (Enemy e : currentLevel.getEnemies())
        {
            graphics.drawImage(tmpEnemyImage, e.x, e.y, null);
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

        buffer.show();
    }

    private void updateInput()
    {
        int x = gamePad.getXAxis();
        int y = gamePad.getYAxis();
        boolean shoot = gamePad.getButton(0);

        UIGameLock = gamePad.getButton(1);

        // Work out player details
        player.x += x * player.moveX;
        player.y += y * player.moveY;

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
            newShot.x = player.x + 14;
            newShot.y = player.y + 14;
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
}
