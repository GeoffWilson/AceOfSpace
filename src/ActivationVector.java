import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Geoff
 * Date: 20/01/13
 * Time: 13:22
 * To change this template use File | Settings | File Templates.
 */
public class ActivationVector
{
    private int action;
    private int actionDataID;
    private boolean[] actionDataFlags;

    private Polygon polygon;

    public ActivationVector(int action, int actionDataID)
    {
        this.actionDataFlags = new boolean[10];

        // Set all flags to false
        for (int i = 0; i < 10; i++) actionDataFlags[i] = false;

        this.actionDataID = actionDataID;
        this.action = action;
        polygon = new Polygon();
    }

    public int getAction()
    {
        return action;
    }

    public void setFlag(int id, boolean value)
    {
        actionDataFlags[id] = value;
    }

    public boolean getFlag(int id)
    {
        return actionDataFlags[id];
    }

    public int getActionDataID()
    {
        return actionDataID;
    }

    public void addPoint(int x, int y)
    {
        polygon.addPoint(x, y);
    }

    public Polygon getPolygon()
    {
        return polygon;
    }
}
