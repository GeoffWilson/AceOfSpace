import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.ClassLoader.getSystemClassLoader;

public class TitleScreen implements Renderable
{
    private BufferedImage texture;

    public TitleScreen()
    {
        try
        {
            InputStream in = getSystemClassLoader().getResourceAsStream("assets/ui/temp_title.png");
            texture = ImageIO.read(in);
        }
        catch (IOException e)
        {
            // Failed to load title screen
            e.printStackTrace();
        }

    }

    @Override
    public void render(Graphics2D g)
    {
        g.drawImage(texture, 0, 0, null);
    }
}
