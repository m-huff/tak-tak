package tak.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

import tak.window.TakTakMultiplayerWindow;
import tak.window.TakTakSingleplayerWindow;

public class TurnIndicator {

    public boolean isActive;

    private int y;
    
    private boolean up;
    public static Random rand = new Random();
    
    public TurnIndicator() {

        isActive = true;
        up = rand.nextBoolean();
        y = (up ? 800 : -150);

        
        //This works because they can't be running at the same time
        TakTakSingleplayerWindow.turnIndicator = this;
        TakTakMultiplayerWindow.turnIndicator = this;
    }
    
    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, y, 1000, 80);
        
        g.setColor(new Color(255, 255, 255, 190));
        g.setFont(new Font("Arial Bold", Font.PLAIN, 48));
        g.drawString("It's your turn!", 150, y + 55);
        
        if (!up) {
	        if (y < 150 || y > 500)
	            y += 20;
	        else if (y < 225 || y > 425)
	            y += 15;
	        else if (y <= 425)
	            y += 10;
	        else if (y > 1000) {
	            isActive = false;
	            //This works because they can't be running at the same time
	            TakTakSingleplayerWindow.turnIndicator = null;
	            TakTakMultiplayerWindow.turnIndicator = null;
	        }
        } else {
        	if (y < 150 || y > 500)
                y -= 20;
            else if (y < 225 || y > 425)
                y -= 15;
            else if (y <= 425)
                y -= 10;
            else if (y < -150) {
                isActive = false;
                //This works because they can't be running at the same time
                TakTakSingleplayerWindow.turnIndicator = null;
                TakTakMultiplayerWindow.turnIndicator = null;
            }
        }
    }
    
}
