package tak.com;

import tak.util.Sound;
import tak.window.MenuWindow;

public class TakTakMain {
	
	//From http://opsound.org/artist/macroform/ - music is public domain
	static Sound music = new Sound("swoosh.wav");

    public static void main(String[] args) {
        final MenuWindow ttw = new MenuWindow();
        
        while (true) {
	        if(music.donePlaying == true)
	        	music = new Sound("swoosh.wav");
        }
    }
}
