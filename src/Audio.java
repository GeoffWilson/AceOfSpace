import uk.co.kernite.VGM.VGMPlayer;

public class Audio
{
    private VGMPlayer playback;

    public Audio(String fileName)
    {
        try
        {
            // We want the sound to be slightly crunched because of poor sample rate
            playback = new VGMPlayer(22050);

            // Default volume is 1.0
            playback.setVolume(1.0D);

            //TODO: This should be loaded as a resource from the JAR, this will crash outside of IDE
            playback.loadFile("src/audio/" + fileName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void changeVolumne(double volume)
    {
        playback.setVolume(volume);
    }

    public void play(int track, int duration)
    {
        Thread t = new Thread(new PlaySound(track, duration));
        t.start();
    }

    private class PlaySound implements Runnable
    {
        private int track;
        private int duration;

        public PlaySound(int track, int duration)
        {
            this.track = track;
            this.duration = duration;
        }

        public void run()
        {
            try
            {
                playback.startTrack(track, duration);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
