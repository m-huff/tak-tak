package tak.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import tak.com.Piece;
import tak.com.TakTakMain;
import tak.config.ConfigLoader;
import tak.util.Sound;

@SuppressWarnings("serial")
public class ConfigWindow extends JFrame implements Runnable {

    static final int XBORDER = 20;
    static final int YBORDER = 20;
    static final int YTITLE = 30;
    static final int WINDOW_BORDER = 8;
    static final int WINDOW_WIDTH = 572;
    static final int WINDOW_HEIGHT = 640;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;
    private static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private static final int CENTER_X = (SCREEN_WIDTH / 2) - (WINDOW_WIDTH / 2);
    private static final int CENTER_Y = (SCREEN_HEIGHT / 2) - (WINDOW_HEIGHT / 2);
    public static Random rand = new Random();
    public static ImageIcon icon = new ImageIcon(ConfigWindow.class.getResource("/tak/assets/icon.png"));
    static ImageIcon background = new ImageIcon(ConfigWindow.class.getResource("/tak/assets/wood.png"));
    private static ImageIcon hoverButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_hover.png"));
    private static ImageIcon button = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button.png"));
    private static ImageIcon smallHoverButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_small_hover.png"));
    private static ImageIcon smallButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_small.png"));
    private static ImageIcon arrow = new ImageIcon(MenuWindow.class.getResource("/tak/assets/greenarrow.png"));
    private static ImageIcon bigButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/big_button.png"));
    private static ImageIcon cenaButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/cenaButton.png"));
    private static ImageIcon shrekButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/shrekButton.png"));
    private final ConfigWindow frame = this;
    public static ArrayList<Piece> pieces = new ArrayList<Piece>();
    public static boolean hasChangedSlides;
    public boolean mouseoverPrev;
    public boolean mouseoverNext;
    public boolean mouseoverReturn;
    public static boolean isWindowOpen;
    
    //Screen 0
    public boolean mouseover1;
    public boolean mouseover2;
    public boolean mouseover3;
    public boolean mouseover4;
    public boolean mouseover5;
    public boolean mouseover6;
    public boolean mouseover7;
    public boolean mouseover8;
    
    //Screen 1
    public boolean mouseoverStopMusic;
    public boolean mouseoverStopSFX;
    
    //Screen 2
    public boolean mouseoverJohnCena;
    public boolean mouseoverShrek;
    public boolean mouseoverDefault;
    
    //Screen 3
    public boolean mouseoverAnim;
    public boolean mouseoverReset;

    //Screens 0-3, one for each configuration section
    public static int currentScreen;
    
    public Piece p = new Piece((rand.nextInt(4) * 10) + 10, Color.green,
            rand.nextBoolean() ? Color.black : Color.white);

    public ConfigWindow() {
        isWindowOpen = true;

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setIconImage(icon.getImage());
        setTitle("Tak-Tak");
        setLocation(CENTER_X, CENTER_Y);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int xpos = e.getX();
                int ypos = e.getY() + 2;

                if (ypos >= 585 && ypos <= 620 && xpos >= 70 && xpos <= 105) {
                    mouseoverPrev = true;
                } else {
                    mouseoverPrev = false;
                }

                if (ypos >= 585 && ypos <= 620 && xpos >= 470 && xpos <= 505) {
                    mouseoverNext = true;
                } else {
                    mouseoverNext = false;
                }

                if (ypos >= 540 && ypos <= 575 && xpos >= 217 && xpos <= 357) {
                    mouseoverReturn = true;
                } else {
                    mouseoverReturn = false;
                }
                
                switch (currentScreen) {
                	case 0:
                		
	                	if (e.getX() > 135 && e.getX() < 235 && e.getY() > 175 && e.getY() < 275) {
	                		mouseover1 = true;
	                	} else {
	                		mouseover1 = false;
	                	}
	                	if (e.getX() > 235 && e.getX() < 335 && e.getY() > 175 && e.getY() < 275) {
	                		mouseover2 = true;
	                	} else {
	                		mouseover2 = false;
	                	}
	                	if (e.getX() > 335 && e.getX() < 435 && e.getY() > 175 && e.getY() < 275) {
	                		mouseover3 = true;
	                	} else {
	                		mouseover3 = false;
	                	}
	                	if (e.getX() > 135 && e.getX() < 235 && e.getY() > 275 && e.getY() < 375) {
	                		mouseover4 = true;
	                	} else {
	                		mouseover4 = false;
	                	}
	                	if (e.getX() > 335 && e.getX() < 435 && e.getY() > 275 && e.getY() < 375) {
	                		mouseover5 = true;
	                	} else {
	                		mouseover5 = false;
	                	}
	                	if (e.getX() > 135 && e.getX() < 235 && e.getY() > 375 && e.getY() < 475) {
	                		mouseover6 = true;
	                	} else {
	                		mouseover6 = false;
	                	}
	                	if (e.getX() > 235 && e.getX() < 335 && e.getY() > 375 && e.getY() < 475) {
	                		mouseover7 = true;
	                	} else {
	                		mouseover7 = false;
	                	}
	                	if (e.getX() > 335 && e.getX() < 435 && e.getY() > 375 && e.getY() < 475) {
	                		mouseover8 = true;
	                	} else {
	                		mouseover8 = false;
	                	}
	                	break;
	                	
                	case 1:
                	
	                	if (e.getX() > 73 && e.getX() < 213 && e.getY() > 185 && e.getY() < 215) {
	                		mouseoverStopMusic = true;
	                	} else {
	                		mouseoverStopMusic = false;
	                	}
	                	
	                	if (e.getX() > 73 && e.getX() < 213 && e.getY() > 265 && e.getY() < 300) {
	                		mouseoverStopSFX = true;
	                	} else {
	                		mouseoverStopSFX = false;
	                	}
	                	break;
                	
                	case 2:
                	
	                	if (e.getX() > 113 && e.getX() < 463 && e.getY() > 135 && e.getY() < 260) {
	                		mouseoverJohnCena = true;
	                	} else {
	                		mouseoverJohnCena = false;
	                	}
	                	
	                	if (e.getX() > 113 && e.getX() < 463 && e.getY() > 270 && e.getY() < 395) {
	                		mouseoverShrek = true;
	                	} else {
	                		mouseoverShrek = false;
	                	}
	                	
	                	if (e.getX() > 113 && e.getX() < 463 && e.getY() > 405 && e.getY() < 530) {
	                		mouseoverDefault = true;
	                	} else {
	                		mouseoverDefault = false;
	                	}
	                	break;
                	
                	case 3:
                	
	                	if (e.getX() > 73 && e.getX() < 213 && e.getY() > 185 && e.getY() < 215) {
	                		mouseoverAnim = true;
	                	} else {
	                		mouseoverAnim = false;
	                	}
	                	
	                	if (e.getX() > 73 && e.getX() < 213 && e.getY() > 265 && e.getY() < 300) {
	                		mouseoverReset = true;
	                	} else {
	                		mouseoverReset = false;
	                	}
	                	break;
                }

                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverReturn) {
                    reset();
                    new MenuWindow();
                    frame.dispose();
                }
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverPrev) {
                    if (currentScreen > 0) {
                        currentScreen--;
                    } else {
                        currentScreen = 3;
                    }
                    hasChangedSlides = true;
                }
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverNext) {
                    if (currentScreen < 3) {
                        currentScreen++;
                    } else {
                        currentScreen = 0;
                    }
                    hasChangedSlides = true;
                }
                
                //TODO - change states and button mouseovers depending on the current screen
                
                switch (currentScreen) {
                
                	case 0:
                	
	                	if (mouseover1) {
	                		ConfigLoader.moveDiagonalLeftForward = !ConfigLoader.moveDiagonalLeftForward;
	                		ConfigLoader.saveConfig();
	                	}
	                	if (mouseover2) {
	                		ConfigLoader.moveForward = !ConfigLoader.moveForward;
	                		ConfigLoader.saveConfig();
	                	}
	                	if (mouseover3) {
	                		ConfigLoader.moveDiagonalRightForward = !ConfigLoader.moveDiagonalRightForward;
	                		ConfigLoader.saveConfig();
	                	}
	                	if (mouseover4) {
	                		ConfigLoader.moveLeft = !ConfigLoader.moveLeft;
	                		ConfigLoader.saveConfig();
	                	}
	                	if (mouseover5) {
	                		ConfigLoader.moveRight = !ConfigLoader.moveRight;
	                		ConfigLoader.saveConfig();
	                	}
	                	if (mouseover6) {
	                		ConfigLoader.moveDiagonalLeftBack = !ConfigLoader.moveDiagonalLeftBack;
	                		ConfigLoader.saveConfig();
	                	}
	                	if (mouseover7) {
	                		ConfigLoader.moveBackward = !ConfigLoader.moveBackward;
	                		ConfigLoader.saveConfig();
	                	}
	                	if (mouseover8) {
	                		ConfigLoader.moveDiagonalRightBack = !ConfigLoader.moveDiagonalRightBack;
	                		ConfigLoader.saveConfig();
	                	}
	                	break;
                	
                	case 1:
                	
	                	if (mouseoverStopMusic) {
	                		ConfigLoader.music = !ConfigLoader.music;
	                		if (!ConfigLoader.music)
	                			TakTakMain.music.stop();
	                		else if (ConfigLoader.music)
	                			TakTakMain.music = new Sound(ConfigLoader.johnCena ? "sound/time_is_now.wav" : ConfigLoader.shrek ? "sound/all_star.wav" : "sound/darude_sandstorm.wav");
	                		ConfigLoader.saveConfig();
	                	}
	                	
	                	if (mouseoverStopSFX) {
	                		ConfigLoader.sfx = !ConfigLoader.sfx;
	                		ConfigLoader.saveConfig();
	                	}
	                	break;
                	
                	case 2:
                	
                		if (mouseoverJohnCena) {
	                		//Have to set all other themes to false here too
	                		ConfigLoader.johnCena = true;
	                		TakTakMain.music.stop();
	                		TakTakMain.music = new Sound("sound/time_is_now.wav");
	                		ConfigLoader.shrek = false;
	                		ConfigLoader.saveConfig();
	                	}
	                	
	                	if (mouseoverShrek) {
	                		//Have to set all other themes to false here too
	                		ConfigLoader.shrek = true;
	                		TakTakMain.music.stop();
	                		TakTakMain.music = new Sound("sound/all_star.wav");
	                		ConfigLoader.johnCena = false;
	                		ConfigLoader.saveConfig();
	                	}
	                	
	                	if (mouseoverDefault) {
	                		//Have to set all other themes to false here too
	                		ConfigLoader.shrek = false;
	                		ConfigLoader.johnCena = false;
	                		TakTakMain.music.stop();
	                		TakTakMain.music = new Sound("sound/darude_sandstorm.wav");
	                		ConfigLoader.saveConfig();
	                	}
	                	break;
                	
                	case 3:
                	
	                	if (mouseoverAnim) {
	                		ConfigLoader.animations = !ConfigLoader.animations;
	                		ConfigLoader.saveConfig();
	                	}
	                	
	                	if (mouseoverReset) {
	                		ConfigLoader.setToDefaults();
	                		ConfigLoader.saveConfig();
	                	}
	                	break;
                	
                }
            }
        });

        init();
        start();
    }
    Thread relaxer;

    public void init() {
        requestFocus();
    }

    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.setColor(Color.black);
        g.fillRect(0, 0, xsize, ysize);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        int index = 0;
        for (int x = 0; x < WINDOW_WIDTH; x += 80) {
            for (int y = 0; y < WINDOW_HEIGHT; y += 80) {
                    pieces.get(index).update();
                    pieces.get(index).draw(g, x, y);
                    if (index < pieces.size() - 2) {
                        index++;
                    } else {
                        index = 0;
                    }
            }
        }

        g.setColor(new Color(0, 0, 0, 230));
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (mouseoverPrev) {
            g.drawImage(smallHoverButton.getImage(), 70, 585, null);
        } else {
            g.drawImage(smallButton.getImage(), 70, 585, null);
        }

        if (mouseoverNext) {
            g.drawImage(smallHoverButton.getImage(), 470, 585, null);
        } else {
            g.drawImage(smallButton.getImage(), 470, 585, null);
        }

        if (mouseoverReturn) {
            g.drawImage(hoverButton.getImage(), 217, 540, null);
        } else {
            g.drawImage(button.getImage(), 217, 540, null);
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(mouseoverPrev ? Color.red : Color.black);
        g.drawString("<", 82, 609);
        g.setColor(mouseoverNext ? Color.red : Color.black);
        g.drawString(">", 483, 609);
        g.setColor(mouseoverReturn ? Color.red : Color.black);
        g.drawString("Return", 261, 563);

        g.setColor(new Color(240, 240, 240));
        g.setFont(new Font("Arial", Font.BOLD, 38));
        g.drawString("TAK-TAK Configuration", 73, 80);
        
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(currentScreen == 0 ? Color.orange : Color.white);
        g.drawString("Movement", 130, 609);
        g.setColor(currentScreen == 1 ? Color.orange : Color.white);
        g.drawString("Music", 233, 609);
        g.setColor(currentScreen == 2 ? Color.orange : Color.white);
        g.drawString("Themes", 299, 609);
        g.setColor(currentScreen == 3 ? Color.orange : Color.white);
        g.drawString("General", 380, 609);
        g.setColor(Color.white);
        g.drawString(" | ", 215, 609);
        g.drawString(" | ", 282, 609);
        g.drawString(" | ", 363, 609);
        
        //Movement things
        switch (currentScreen) {
        
        	case 0:
        		
	        	g.setColor(new Color(50, 50, 50));
	        	g.fillRect(130, 170, 310, 310);
	        	g.setColor(new Color(150, 150, 150));
	        	g.fillRect(135, 175, 300, 300);
	        	g.setColor(new Color(0, 0, 0));
	        	g.drawLine(235, 175, 235, 475);
	        	g.drawLine(335, 175, 335, 475);
	        	g.drawLine(135, 275, 435, 275);
	        	g.drawLine(135, 375, 435, 375);
	
	            g.setColor(Color.white);
	            g.drawString("Click on the directions you want the pieces to be able to move!", 51, 105);
	            g.setColor(Color.orange);
	            g.drawString("This will ONLY apply to singleplayer games!", 120, 125);
	        	
	        	g.setColor(new Color(0, 0, 0, 65));
	        	if (mouseover1) {
	        		g.fillRect(135, 175, 100, 100);
	        	} else if (mouseover2) {
	        		g.fillRect(235, 175, 100, 100);
	        	} else if (mouseover3) {
	        		g.fillRect(335, 175, 100, 100);
	        	} else if (mouseover4) {
	        		g.fillRect(135, 275, 100, 100);
	        	} else if (mouseover5) {
	        		g.fillRect(335, 275, 100, 100);
	        	} else if (mouseover6) {
	        		g.fillRect(135, 375, 100, 100);
	        	} else if (mouseover7) {
	        		g.fillRect(235, 375, 100, 100);
	        	} else if (mouseover8) {
	        		g.fillRect(335, 375, 100, 100);
	        	}
	
	        	if (ConfigLoader.moveDiagonalLeftForward) {
	        		drawArrow(arrow.getImage(), 235, 275, 225, 0.15, 0.2);
	        	}
	        	if (ConfigLoader.moveDiagonalRightForward) {
	        		drawArrow(arrow.getImage(), 335, 275, 315, 0.15, 0.2);
	        	}
	        	if (ConfigLoader.moveForward) {
	        		drawArrow(arrow.getImage(), 287, 275, -90, 0.15, 0.2);
	        	}
	        	if (ConfigLoader.moveLeft) {
	        		drawArrow(arrow.getImage(), 230, 325, 180, 0.15, 0.2);
	        	}
	        	if (ConfigLoader.moveRight) {
	        		drawArrow(arrow.getImage(), 340, 325, 0, 0.15, 0.2);
	        	}
	        	if (ConfigLoader.moveDiagonalLeftBack) {
	        		drawArrow(arrow.getImage(), 235, 375, 135, 0.15, 0.2);
	        	}
	        	if (ConfigLoader.moveDiagonalRightBack) {
	        		drawArrow(arrow.getImage(), 335, 375, 45, 0.15, 0.2);
	        	}
	        	if (ConfigLoader.moveBackward) {
	        		drawArrow(arrow.getImage(), 287, 375, 90, 0.15, 0.2);
	        	}
	
	        	p.draw(g, 238, 280);
	        	break;

        //Music things	
        	case 1:
        	
	        	g.setColor(Color.white);
	            g.drawString("Change the configuration of the game's sounds!", 104, 105);
	            
	            g.drawString("Current Track: " + (TakTakMain.music.getSound().contains("sandstorm") ?
	            "Darude - Sandstorm" : "John Cena - Time Is Now"), 73, 155);
	            g.drawString("Music " + (ConfigLoader.music ? "is" : "is not") + " playing", 73, 175);
	            
	            if (mouseoverStopMusic) {
	                g.drawImage(hoverButton.getImage(), 73, 185, null);
	            } else {
	                g.drawImage(button.getImage(), 73, 185, null);
	            }
	
	            g.setFont(new Font("Arial", Font.BOLD, 16));
	            g.setColor(mouseoverStopMusic ? Color.red : Color.black);
	            g.drawString("Stop Music", 100, 208);
	            
	            g.setColor(Color.white);
	            g.drawString("SFX are currently " + (ConfigLoader.sfx ? "on" : "off"), 73, 265);
	            
	            if (mouseoverStopSFX) {
	                g.drawImage(hoverButton.getImage(), 73, 275, null);
	            } else {
	                g.drawImage(button.getImage(), 73, 275, null);
	            }
	
	            g.setFont(new Font("Arial", Font.BOLD, 16));
	            g.setColor(mouseoverStopSFX ? Color.red : Color.black);
	            g.drawString((ConfigLoader.sfx ? "Stop" : "Start") + " SFX", 107, 298);
	            break;
        
        //Themes, like john Cena	
        	case 2:
        	
	        	g.setColor(Color.white);
	            g.drawString("Change the theme of the entire game!", 151, 105);
	
	            if (ConfigLoader.johnCena) {
	                g.drawImage(cenaButton.getImage(), 113, 135, null);
	            } else {
	                g.drawImage(bigButton.getImage(), 113, 135, null);
	            }
	
	            g.setFont(new Font("Arial", Font.BOLD, 30));
	            g.setColor(ConfigLoader.johnCena ? Color.red : Color.black);
	            g.drawString("John Cena", 215, 208);
	
	            if (ConfigLoader.shrek) {
	                g.drawImage(shrekButton.getImage(), 113, 270, null);
	            } else {
	                g.drawImage(bigButton.getImage(), 113, 270, null);
	            }
	            
	            g.setColor(ConfigLoader.shrek ? Color.red : Color.black);
	            g.drawString("Shrek", 245, 343);
	
	            g.drawImage(bigButton.getImage(), 113, 405, null);
	
	            g.setColor(!ConfigLoader.shrek && !ConfigLoader.johnCena ? Color.red : Color.black);
	            g.drawString("Default", 235, 478);
	            break;
        	
        //General settings	
        	case 3:
        	
	        	g.setColor(Color.white);
	            g.drawString("Configure general things about the game!", 133, 105);
	            
	            g.drawString("Game Animations", 73, 175);
	            
	            if (mouseoverAnim) {
	                g.drawImage(hoverButton.getImage(), 73, 185, null);
	            } else {
	                g.drawImage(button.getImage(), 73, 185, null);
	            }
	
	            g.setFont(new Font("Arial", Font.BOLD, 16));
	            g.setColor(mouseoverAnim ? Color.red : Color.black);
	            g.drawString((ConfigLoader.animations ? "Disable" : "Enable"), 116, 208);
	            
	            g.setColor(Color.white);
	            g.drawString("Reset All Configuration to Defaults", 73, 265);
	            
	            if (mouseoverReset) {
	                g.drawImage(hoverButton.getImage(), 73, 275, null);
	            } else {
	                g.drawImage(button.getImage(), 73, 275, null);
	            }
	
	            g.setFont(new Font("Arial", Font.BOLD, 16));
	            g.setColor(mouseoverReset ? Color.red : Color.black);
	            g.drawString("Reset", 121, 298);
	            break;
            
        }

        g.setFont(new Font("Arial", Font.BOLD, 12));

        gOld.drawImage(image, 0, 0, null);
    }

    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.03;
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }

    public void reset() {
        currentScreen = 0;
        hasChangedSlides = false;

        for (int i = 0; i < 80; i++) {
            Color[] colors = {Color.orange, Color.blue, Color.green};
            Piece p = new Piece((rand.nextInt(4) * 10) + 10, colors[rand.nextInt(colors.length)],
                    rand.nextBoolean() ? Color.black : Color.white);
            if (rand.nextInt(10) == 0) {
                p.setKing(true);
            }
            pieces.add(p);
        }
        
        currentScreen = 0;
    }

    public void animate() {

        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            reset();
        }
    }

    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }

    public int getX(int x) {
        return (x + XBORDER + WINDOW_BORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }

    public int getWidth2() {
        return (xsize - 2 * (XBORDER + WINDOW_BORDER));
    }

    public int getHeight2() {
        return (ysize - 2 * YBORDER - WINDOW_BORDER - YTITLE);
    }
    
    public void drawArrow(Image image, int xpos, int ypos, double rot, double xscale,
            double yscale) {
        int width;
        int height;

        width = arrow.getImage().getWidth(this);
        height = arrow.getImage().getHeight(this);

        g.translate(xpos, ypos);
        g.rotate(rot * Math.PI / 180.0);
        g.scale(xscale, yscale);

        g.drawImage(image, -width / 2, -height / 2,
                width, height, this);

        g.scale(1.0 / xscale, 1.0 / yscale);
        g.rotate(-rot * Math.PI / 180.0);
        g.translate(-xpos, -ypos);
    }
}