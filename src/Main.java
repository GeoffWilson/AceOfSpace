
public class Main
{
    public static void main(String[] args)
    {
        new Main();
    }

    public Main()
    {
        JoyPad gamePad = new JoyPad();

        Thread thread = new Thread(new Render(gamePad), "Core Rendering");
        thread.start();
    }
}
