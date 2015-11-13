package tak.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import tak.window.TakTakMultiplayerWindow;
import tak.window.TakTakSingleplayerWindow;

public class ScoreFader {

    private int x;
    private int y;
    private int points;
    private int fade;
    public boolean isActive;
    public Color color;

    public ScoreFader(int _points, int _x, int _y, Color _color) {
        points = _points;
        x = _x;
        y = _y;
        color = _color;

        fade = 255;
        isActive = true;

        //This works because they can't be running at the same time
        TakTakSingleplayerWindow.faders.add(this);
        TakTakMultiplayerWindow.faders.add(this);
    }

    public int getX() {
        return x;
    }

    public void setX(int _x) {
        x = _x;
    }

    public int getY() {
        return y;
    }

    public void setY(int _y) {
        y = _y;
    }

    public int getPointValue() {
        return points;
    }

    public void setPointValue(int _points) {
        points = _points;
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fade));
        g.setFont(new Font("Arial Bold", Font.PLAIN, 30));

        String s = "+" + points;
        g.drawString(s, x + (s.length() <= 3 ? 25 : 15), y + 55);

        if (fade >= 5) {
            fade -= 5;
        } else {
            isActive = false;
            //This works because they can't be running at the same time
            TakTakSingleplayerWindow.faders.remove(this);
            TakTakMultiplayerWindow.faders.remove(this);
        }
    }
}
