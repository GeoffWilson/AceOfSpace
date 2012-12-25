import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.util.Timer;
import java.util.TimerTask;

public class GamePad
{
    private Controller controller;
    private Component[] buttons;
    private Component xAxis;
    private Component yAxis;

    public GamePad()
    {
        if (this.getController())
        {
            xAxis = controller.getComponent(Component.Identifier.Axis.X);
            yAxis = controller.getComponent(Component.Identifier.Axis.Y);
            buttons = new Component[8];

            buttons[0] = controller.getComponent(Component.Identifier.Button._0);
            buttons[1] = controller.getComponent(Component.Identifier.Button._1);
            buttons[2] = controller.getComponent(Component.Identifier.Button._2);

            GamePadPoll poll = new GamePadPoll();
            Timer timer = new Timer();
            timer.schedule(poll, 0, 10);
        }
    }

    public boolean getButton(int button)
    {
        return buttons[button].getPollData() > 0.9F;
    }

    public int getXAxis()
    {
        return Math.round(xAxis.getPollData());
    }

    public int getYAxis()
    {
        return Math.round(yAxis.getPollData());
    }

    private class GamePadPoll extends TimerTask
    {
        public void run()
        {
            controller.poll();
        }
    }

    private boolean getController()
    {
        ControllerEnvironment controllerEnvironment = ControllerEnvironment.getDefaultEnvironment();

        boolean controllerFound = false;

        for (Controller c : controllerEnvironment.getControllers())
        {
            if (c.getType() == Controller.Type.GAMEPAD)
            {
                System.out.println(c.getName());
                controller = c;
                controllerFound = true;
            }
        }

        return controllerFound;
    }
}
