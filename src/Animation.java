import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.ClassLoader.*;

public class Animation
{
    private ArrayList<BufferedImage> frames;
    private int[] frameOrder;
    private int currentFrame;
    private Timer timer;

    public Animation(int[] frameOrder)
    {
        frames = new ArrayList<BufferedImage>();
        this.frameOrder = frameOrder;
    }

    public void beginAnimation(int delay)
    {
        currentFrame = 0;
        timer = new Timer();
        timer.schedule(new Animate(), 0, delay);
    }

    public void stopAnimation()
    {
        timer.cancel();
    }

    public BufferedImage getFrame()
    {
        System.out.println(frameOrder[currentFrame]);
        return frames.get(frameOrder[currentFrame]);
    }

    public boolean loadAnimation(String frameName, int frameCount)
    {
        boolean result = true;

        try
        {
            for (int i = 0; i < frameCount; i++)
            {
                InputStream in = getSystemClassLoader().getResourceAsStream(frameName + i + ".png");
                BufferedImage frame = ImageIO.read(in);
                frames.add(frame);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return result;
    }

    private class Animate extends TimerTask
    {
        public Animate()
        {

        }

        public void run()
        {
            if (++currentFrame == frameOrder.length) currentFrame = 0;
        }
    }
}
