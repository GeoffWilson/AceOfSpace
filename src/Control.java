import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the game.
 */
public class Control
{
    // Input Control
    private JoyPad joyPad;

    // Levels, this needs testing to see how many we can load into memory at once!
    private ConcurrentHashMap<Integer, Level> levels;

    public Control()
    {
        levels = new ConcurrentHashMap<Integer, Level>();
    }
}
