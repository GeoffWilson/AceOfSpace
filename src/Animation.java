import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.ClassLoader.getSystemClassLoader;

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
        return frames.get(frameOrder[currentFrame]);
    }

    public boolean loadAnimation(String frameName, int frameCount)
    {
        boolean result = true;

        try
        {
            for (int i = 0; i < frameCount; i++)
            {
                if (Control.cache.hasSprite(frameName + i))
                {
                    frames.add(Control.cache.getSprite(frameName + i));
                }
                else
                {
                    InputStream in = getSystemClassLoader().getResourceAsStream(frameName + i + ".png");
                    BufferedImage frame = ImageIO.read(in);
                    frames.add(frame);
                    Control.cache.putSprite(frame, frameName + i);
                }
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
        public void run()
        {
            if ((currentFrame + 1) == frameOrder.length) currentFrame = 0;
            else ++currentFrame;
        }
    }
}
