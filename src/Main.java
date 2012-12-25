
public class Main
{
    public static void main(String[] args)
    {
        new Main();
    }

    public Main()
    {
        GamePad gamePad = new GamePad();

        Thread thread = new Thread(new Render(gamePad), "Core Rendering");
        thread.start();
    }
}
