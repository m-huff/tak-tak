package tak.com;

import tak.util.Sound;
import tak.window.MenuWindow;

public class TakTakMain {
	
	//Music - http://opsound.org/artist/macroform/ - public domain
	//SFX - http://www.wavsource.com/sfx/sfx.htm - public domain
        //Logo - http://www.flamingtext.com/net-fu/dynamic.cgi?script=inferno-logo - logo generator
	
	//TODO
        // - more sounds
        // - buttons (main menu, network window, after game single player and multi player)
        // - update rules for new king stuff

	static Sound music = new Sound("darude_sandstorm.wav");

    public static void main(String[] args) {
        final MenuWindow ttw = new MenuWindow();
        
        while (true) {
	        if(music.donePlaying == true)
	        	music = new Sound("darude_sandstorm.wav");
        }
    }
}
