package tak.com;

import tak.util.Sound;
import tak.window.MenuWindow;

public class TakTakMain {
	
	//Music - http://opsound.org/artist/macroform/ - public domain
	//SFX - http://www.wavsource.com/sfx/sfx.htm - public domain
	
	//TODO
	// - maybe add drag and drop for pieces?
	// - add more sounds?
	// - uhh... better music probably
        // - fix piece scoring in multiplayer
	
	static Sound music = new Sound("darude_sandstorm.wav");

    public static void main(String[] args) {
        final MenuWindow ttw = new MenuWindow();
        
        while (true) {
	        if(music.donePlaying == true)
	        	music = new Sound("darude_sandstorm.wav");
        }
    }
}
