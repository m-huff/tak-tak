package tak.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigLoader {

	//Config, writes them to a plain text file in the appdata/roaming folder
	//Not too good for temporary use like cs2, but it'll at least save between launches so there's that
	
	public static boolean moveDiagonalLeftForward;
	public static boolean moveForward;
	public static boolean moveDiagonalRightForward;
	public static boolean moveLeft;
	public static boolean moveRight;
	public static boolean moveDiagonalLeftBack;
	public static boolean moveBackward;
	public 	static boolean moveDiagonalRightBack;
	
	public static boolean music;
	public static boolean sfx;

	public static boolean johnCena;
	public static boolean shrek;
	
	public static boolean animations;
	
	static Properties config = new Properties();
	static InputStream input = null;
	static OutputStream output = null;
	
	public static String configPath = System.getProperty("user.home") + "/AppData/Roaming/taktak/";
	
	static File prop = new File(configPath);
	static File cfg = new File(configPath + "config.properties");
	
	public static void loadConfig() {
		try {
			input = new FileInputStream(cfg);
			config.load(input);

			//Movement
			moveDiagonalLeftForward = Boolean.parseBoolean(config.getProperty("diagonal_left_forward"));
			moveForward = Boolean.parseBoolean(config.getProperty("forward"));
			moveDiagonalRightForward = Boolean.parseBoolean(config.getProperty("diagonal_right_forward"));
			moveLeft = Boolean.parseBoolean(config.getProperty("left"));
			moveRight = Boolean.parseBoolean(config.getProperty("right"));
			moveDiagonalLeftBack = Boolean.parseBoolean(config.getProperty("diagonal_left_back"));
			moveDiagonalRightBack = Boolean.parseBoolean(config.getProperty("diagonal_right_back"));
			moveBackward = Boolean.parseBoolean(config.getProperty("backward"));
			
			//Music
			music = Boolean.parseBoolean(config.getProperty("music"));
			sfx = Boolean.parseBoolean(config.getProperty("sfx"));
			
			//Themes
			johnCena = Boolean.parseBoolean(config.getProperty("john_cena"));
			shrek = Boolean.parseBoolean(config.getProperty("shrek"));
			
			//General
			animations = Boolean.parseBoolean(config.getProperty("animations"));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveConfig() {
		try {
			prop.mkdirs();
			if(!prop.exists()) {
				try {
					prop.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
			
			if(!cfg.exists()) {
				try {
					cfg.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			output = new FileOutputStream(cfg);

			config.setProperty("diagonal_left_forward", String.valueOf(moveDiagonalLeftForward));
			config.setProperty("diagonal_right_forward", String.valueOf(moveDiagonalRightForward));
			config.setProperty("forward", String.valueOf(moveForward));
			config.setProperty("left", String.valueOf(moveLeft));
			config.setProperty("right", String.valueOf(moveRight));
			config.setProperty("diagonal_left_back", String.valueOf(moveDiagonalLeftBack));
			config.setProperty("diagonal_right_back", String.valueOf(moveDiagonalRightBack));
			config.setProperty("backward", String.valueOf(moveBackward));
			
			config.setProperty("music", String.valueOf(music));
			config.setProperty("sfx", String.valueOf(sfx));
			
			config.setProperty("john_cena", String.valueOf(johnCena));
			config.setProperty("shrek", String.valueOf(shrek));
			
			config.setProperty("animations", String.valueOf(animations));
			
			config.store(output, null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isInt(String in) {
	    try {
	        Integer.parseInt(in);
	        return true;
	    }
	    catch(NumberFormatException e) {
	        return false;
	    }
	}
	
	public static boolean isBoolean(String in) {
	    try {
	        Boolean.parseBoolean(in);
	        return true;
	    }
	    catch(NumberFormatException e) {
	        return false;
	    }
	}
	
	public static boolean checkConfigExists() {
		return cfg.exists();
	}
	
	public static void setToDefaults() {	
		moveDiagonalLeftForward = true;
		moveForward = true;
		moveDiagonalRightForward = true;
		moveLeft = false;
		moveRight = false;
		moveDiagonalLeftBack = false;
		moveBackward = false;
		moveDiagonalRightBack = false;
		
		music = true;
		sfx = true;

		johnCena = false;
		shrek = false;
		
		animations = true;
	}

}
