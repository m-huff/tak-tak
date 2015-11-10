package tak.com;

import tak.util.Sound;
import tak.window.MenuWindow;

public class TakTakMain {

	//SFX - http://www.wavsource.com/sfx/sfx.htm - public domain
	
	//TODO
        // - buttons in more places

	static Sound music = new Sound("darude_sandstorm.wav");

    public static void main(String[] args) {
        final MenuWindow ttw = new MenuWindow();
        
        while (true) {
	        if(music.donePlaying == true)
	        	music = new Sound("darude_sandstorm.wav");
        }
    }
}
