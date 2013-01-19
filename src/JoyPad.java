import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Manages input from a controller.
 *
 * @author Geoff Wilson
 * @author Ben Carvell
 * @version 1.0
 */
public class JoyPad
{
    private ArrayList<Controller> availableControllers;

    private Controller controller;
    private Component[] buttons;
    private Component xAxis;
    private Component yAxis;

    public JoyPad()
    {
        availableControllers = getValidControllers();
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

    public void setController(Controller c)
    {
        controller = c;

        xAxis = controller.getComponent(Component.Identifier.Axis.X);
        yAxis = controller.getComponent(Component.Identifier.Axis.Y);
        buttons = new Component[8];

        buttons[0] = controller.getComponent(Component.Identifier.Button._0);
        buttons[1] = controller.getComponent(Component.Identifier.Button._1);
        buttons[2] = controller.getComponent(Component.Identifier.Button._2);

        GamePadPoll poll = new GamePadPoll();
        Timer timer = new Timer();
        timer.schedule(poll, 0, 16);
    }

    public ArrayList<Controller> getAvailableControllers()
    {
        return availableControllers;
    }

    private ArrayList<Controller> getValidControllers()
    {
        ArrayList<Controller> controllers = new ArrayList<Controller>();
        ControllerEnvironment controllerEnvironment = ControllerEnvironment.getDefaultEnvironment();

        for (Controller c : controllerEnvironment.getControllers())
        {
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK)
            {
                controllers.add(c);
            }
        }

        return controllers;
    }
}
