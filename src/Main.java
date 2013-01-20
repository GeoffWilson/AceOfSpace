import net.java.games.input.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main
{
    private JoyPad gamePad;
    private JFrame configFrame;
    private JComboBox<Controller> controllerJComboBox;

    /**
     * Game entry point
     *
     * @param args Console arguments (not used)
     */
    public static void main(String[] args)
    {
        new Main();
    }

    /**
     * Constructor for the Main class. Creates the JoyPad instance and the controller selection JFrame
     */
    public Main()
    {
        try
        {
            // Use the system look and feel for dialog windows
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            gamePad = new JoyPad();
            this.buildFrame();
            configFrame.setLocationByPlatform(true);
            configFrame.setVisible(true);
        }
        catch (Exception e)
        {
            System.out.println("There was an error " + e.getLocalizedMessage());
            System.exit(0);
        }
    }

    /**
     * Configure the JFrame that will allow the user to select a controller input
     */
    private void buildFrame()
    {
        configFrame = new JFrame("AceOfSpace v1.0.0");
        Dimension dimension = new Dimension(320, 240);
        configFrame.setLayout(new BorderLayout());
        configFrame.setPreferredSize(dimension);
        configFrame.setBounds(0, 0, 320, 240);
        configFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Start game button
        JButton startButton = new JButton("START GAME");
        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                gamePad.setController((Controller) controllerJComboBox.getSelectedItem());
                configFrame.dispose();
                startGame();
            }
        });
        configFrame.add(startButton, BorderLayout.SOUTH);

        // Controller selection
        controllerJComboBox = new JComboBox<Controller>();
        if (gamePad.getAvailableControllers().size() == 0)
        {
            // If there is no valid controllers then prevent the game from starting (keyboard support to come)
            JLabel label = new JLabel("No controllers detected, unable to start");
            configFrame.add(label, BorderLayout.NORTH);
            startButton.setEnabled(false);
        }
        else
        {
            for (Controller c : gamePad.getAvailableControllers())
            {
                controllerJComboBox.addItem(c);
            }
            configFrame.add(controllerJComboBox, BorderLayout.NORTH);
        }
    }

    /**
     * Loads the main game after a controller has been selected
     */
    public void startGame()
    {
        Thread thread = new Thread(new Render(gamePad), "Core Rendering");
        thread.start();
    }
}
