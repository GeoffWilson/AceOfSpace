package ui;

import java.awt.*;

public class MessageWindow implements UIElement
{
    private Graphics2D graphics;
    private String text;
    private Font font;

    public MessageWindow(Graphics2D graphics)
    {
        this.graphics = graphics;
        this.font = new Font("Consolas", Font.BOLD, 24);
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void render()
    {
        graphics.setColor(new Color(0, 0, 125));
        graphics.setFont(this.font);
        graphics.fillRect(60, 10, 520, 200);
        graphics.setColor(Color.WHITE);
        graphics.drawString(text, 75, 65);
    }
}
