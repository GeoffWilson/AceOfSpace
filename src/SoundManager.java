import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds and plays all of the audio in the game
 */
public class SoundManager
{
    private ConcurrentHashMap<String, Audio> sounds;

    public SoundManager()
    {
        sounds = new ConcurrentHashMap<String, Audio>();

        // Create
        loadAudio("pop", "/assets/audio/effects/pop.vgz", 1.0D);
        loadAudio("clear", "/assets/audio/effects/clear.vgz", 2.0D);
    }

    public void loadAudio(String key, String fileName, double volume)
    {
        if (!sounds.containsKey(key))
        {
            Audio audio = new Audio(fileName);
            audio.changeVolumne(volume);
            sounds.put(key, audio);
        }
    }

    public void playAudio(String key, int track, int duration)
    {
        sounds.get(key).play(track, duration);
    }
}
