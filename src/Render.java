import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * Renders the main game to the screen
 */
public class Render implements Runnable
{
    private Graphics2D graphics;
    private BufferStrategy buffer;
    private GamePad gamePad;
    private Player player;
    private ConcurrentLinkedQueue<Shot> shots;
    private Audio shot;

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

    public Render(GamePad gamePad)
    {
        this.gamePad = gamePad;
        this.setupGame();
        this.shots = new ConcurrentLinkedQueue<Shot>();
        //Audio backgroundMusic = new Audio("music.vgz");
        //backgroundMusic.play(1, 1000);
        this.shot = new Audio("shot.vgz");

        Dimension dimension = new Dimension(640, 480);
        Frame baseFrame = new Frame();
        baseFrame.setPreferredSize(dimension);
        baseFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);    //To change body of overridden methods use File | Settings | File Templates.
                System.exit(0);
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
    }

    private void setupGame()
    {
        player = new Player();
        player.x = 100;
        player.y = 100;
    }

    public void run()
    {
        while (true)
        {
            this.updateInput();

            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, 640, 480);

            for (Shot s : shots)
            {
                if (s.x > 640 || s.x < 0) shots.remove(s);
                if (s.y > 480 || s.y < 0) shots.remove(s);

                s.inc();
                graphics.setColor(Color.YELLOW);
                graphics.fillOval(s.x, s.y, 5,5);
            }

            graphics.drawImage(player.getFrame(), player.x, player.y, null);
            buffer.show();

            LockSupport.parkNanos(16666666L);
        }
    }

    private void updateInput()
    {
        int x = gamePad.getXAxis();
        int y = gamePad.getYAxis();
        boolean shoot = gamePad.getButton(0);

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

        if (y != 0 || x != 0) System.out.println("x " + x + " y " + y);

        if (shoot && !player.shoot)
        {
            player.shoot = true;
            this.shot.play(1, 2);

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
