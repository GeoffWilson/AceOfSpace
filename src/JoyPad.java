import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.util.ArrayList;

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

    // For Game Pads
    private Component[] buttons;
    private Component xAxis;
    private Component yAxis;

    // For Keyboards
    private Component[] actionKeys;
    private Component upKey;
    private Component downKey;
    private Component leftKey;
    private Component rightKey;

    private boolean keyboard;

    public JoyPad()
    {
        keyboard = false;
        availableControllers = getValidControllers();
    }

    public boolean getButton(int button)
    {
        controller.poll();
        if (keyboard) return actionKeys[button].getPollData() > 0.9F;
        return buttons[button].getPollData() > 0.9F;
    }

    public int getXAxis()
    {
        controller.poll();
        if (!keyboard) return Math.round(xAxis.getPollData());
        return -Math.round(leftKey.getPollData()) + Math.round(rightKey.getPollData());
    }

    public int getYAxis()
    {
        controller.poll();
        if (!keyboard) return Math.round(yAxis.getPollData());
        return -Math.round(upKey.getPollData()) + Math.round(downKey.getPollData());
    }

    public void setController(Controller c)
    {
        controller = c;

        if (c.getType() == Controller.Type.KEYBOARD)
        {
            keyboard = true;

            upKey = controller.getComponent(Component.Identifier.Key.W);
            downKey = controller.getComponent(Component.Identifier.Key.S);
            leftKey = controller.getComponent(Component.Identifier.Key.A);
            rightKey = controller.getComponent(Component.Identifier.Key.D);

            actionKeys = new Component[2];
            actionKeys[0] = controller.getComponent(Component.Identifier.Key.SPACE);
            actionKeys[1] = controller.getComponent(Component.Identifier.Key.RETURN);
        }
        else
        {
            xAxis = controller.getComponent(Component.Identifier.Axis.X);
            yAxis = controller.getComponent(Component.Identifier.Axis.Y);

            buttons = new Component[8];
            buttons[0] = controller.getComponent(Component.Identifier.Button._0);
            buttons[1] = controller.getComponent(Component.Identifier.Button._1);
            buttons[2] = controller.getComponent(Component.Identifier.Button._2);
        }
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
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK || c.getType() == Controller.Type.KEYBOARD)
            {
                controllers.add(c);
            }
        }

        return controllers;
    }
}
