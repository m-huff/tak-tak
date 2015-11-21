package tak.com;

import tak.config.ConfigLoader;
import tak.util.Sound;
import tak.window.MenuWindow;
import tak.window.NetworkWindow;
import tak.window.RulesWindow;
import tak.window.TakTakMultiplayerWindow;
import tak.window.TakTakSingleplayerWindow;

public class TakTakMain {

    public static boolean muted;
    public static Sound music = new Sound(MenuWindow.johnCena ? "sound/time_is_now.wav" : "sound/darude_sandstorm.wav");

    public static void main(String[] args) {
    	
    	if (!ConfigLoader.checkConfigExists()) {
			ConfigLoader.setToDefaults();
			ConfigLoader.saveConfig();
		}
		ConfigLoader.loadConfig();
    	
        final MenuWindow ttw = new MenuWindow();

        while (MenuWindow.isWindowOpen || NetworkWindow.isWindowOpen || RulesWindow.isWindowOpen
                || TakTakMultiplayerWindow.isWindowOpen || TakTakSingleplayerWindow.isWindowOpen) {


            if (music.donePlaying == true) {
                music = new Sound(MenuWindow.johnCena ? "sound/time_is_now.wav" : "sound/darude_sandstorm.wav");
            }
        }
    }
}
