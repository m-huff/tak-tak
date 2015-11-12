package tak.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import tak.com.Piece;

@SuppressWarnings("serial")
public class MenuWindow extends JFrame implements Runnable {

	static final int XBORDER = 20;
	static final int YBORDER = 20;
	static final int YTITLE = 30;
	static final int WINDOW_BORDER = 8;
	static final int WINDOW_WIDTH = 2 * (WINDOW_BORDER + XBORDER) + 575;
	static final int WINDOW_HEIGHT = YTITLE + WINDOW_BORDER + 2 * YBORDER + 170;
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
	public static ImageIcon icon = new ImageIcon(MenuWindow.class.getResource("/tak/assets/icon.png"));
	static ImageIcon background = new ImageIcon(MenuWindow.class.getResource("/tak/assets/wood.png"));

	private static ImageIcon hoverButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_hover.png"));
	private static ImageIcon button = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button.png"));
	
	private static ImageIcon logo = new ImageIcon(MenuWindow.class.getResource("/tak/assets/logo.png"));

	private boolean mouseoverAI;
	private boolean mouseoverClient;
	private boolean mouseoverServer;
	private boolean mouseoverRules;

	private final MenuWindow frame = this;

	public int textPosition = WINDOW_WIDTH;

	public static ArrayList<Piece> pieces = new ArrayList<Piece>();

	public MenuWindow() {

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setIconImage(icon.getImage());
		setTitle("Tak-Tak");
		setLocation(CENTER_X, CENTER_Y);

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (textPosition < 35)
					return;
				int xpos = e.getX();
				int ypos = e.getY() + 2;

				if (xpos >= 475 && xpos <= 620 && ypos >= textPosition + 10 && ypos <= textPosition + 45)
					mouseoverRules = true;
				else
					mouseoverRules = false;

				//AI button
				if (ypos >= textPosition + 10 && ypos <= textPosition + 45 && xpos >= 30 && xpos <= 170) {
					mouseoverAI = true;
				} else {
					mouseoverAI = false;
				}

				//Client button
				if (ypos >= textPosition + 10 && ypos <= textPosition + 45 && xpos >= 175 && xpos <= 315) {
					mouseoverClient = true;
				} else {
					mouseoverClient = false;
				}

				//Client button
				if (ypos >= textPosition + 10 && ypos <= textPosition + 45 && xpos >= 320 && xpos <= 460) {
					mouseoverServer = true;
				} else {
					mouseoverServer = false;
				}

				repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (textPosition < 35)
					return;
				if (MouseEvent.BUTTON1 == e.getButton() && mouseoverAI) {
					new TakTakSingleplayerWindow();
					frame.dispose();
				} else if (MouseEvent.BUTTON1 == e.getButton() && mouseoverClient) {
					final NetworkWindow rw = new NetworkWindow(true);
					frame.dispose();
				} else if (MouseEvent.BUTTON1 == e.getButton() && mouseoverServer) {
					final NetworkWindow rw = new NetworkWindow(false);
					frame.dispose();
				} else if (MouseEvent.BUTTON1 == e.getButton() && mouseoverRules) {
					final RulesWindow rw = new RulesWindow();
					rw.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
			}
		});
                
                addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
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

		for (int x = 0; x < WINDOW_WIDTH; x += background.getIconWidth()) {
			for (int y = 0; y < WINDOW_HEIGHT; y += background.getIconHeight()) {
				g.drawImage(background.getImage(), x, y, null);
			}
		}

		int index = 0;
		for (int x = 0; x < WINDOW_WIDTH; x += 80) {
			for (int y = 0; y < WINDOW_HEIGHT; y += 80) {

				if (index <= pieces.size() - 2 && pieces.get(index) != null) {
					pieces.get(index).draw(g, x, y);
					if (index < pieces.size() - 2) {
						index++;
					} else {
						index = 0;
					}
				}
			}
		}

		g.setColor(new Color(0, 0, 0, 190));
		g.fillRect(0, 0, WINDOW_WIDTH, textPosition + 60);
		
		g.drawImage(logo.getImage(), textPosition - 28, 35, null);

		if (mouseoverRules)
			g.drawImage(hoverButton.getImage(), 465, textPosition + 10, null);
		else
			g.drawImage(button.getImage(), 465, textPosition + 10, null);
		
		if (mouseoverServer)
			g.drawImage(hoverButton.getImage(), 320, textPosition + 10, null);
		else
			g.drawImage(button.getImage(), 320, textPosition + 10, null);

		if (mouseoverClient)
			g.drawImage(hoverButton.getImage(), 175, textPosition + 10, null);
		else
			g.drawImage(button.getImage(), 175, textPosition + 10, null);

		if (mouseoverAI)
			g.drawImage(hoverButton.getImage(), 30, textPosition + 10, null);
		else
			g.drawImage(button.getImage(), 30, textPosition + 10, null);

		g.setFont(new Font("Arial", Font.BOLD, 16));

		g.setColor(mouseoverRules ? Color.red : Color.black);
		g.drawString("Learn the Rules", 476, textPosition + 34);
		g.setColor(mouseoverAI ? Color.red : Color.black);
		g.drawString("Play vs. AI", 59, textPosition + 34);
		g.setColor(mouseoverClient ? Color.red : Color.black);
		g.drawString("Play as Client", 194, textPosition + 34);
		g.setColor(mouseoverServer ? Color.red : Color.black);
		g.drawString("Play as Server", 335, textPosition + 34);

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
		for (int i = 0; i < 20; i++) {
			Color[] colors = {Color.orange, Color.blue, Color.green };
			Piece p = new Piece((rand.nextInt(4) * 10) + 10, colors[rand.nextInt(colors.length)],
					rand.nextBoolean() ? Color.black : Color.white);
			if (rand.nextInt(10) == 0)
				p.setKing(true);
			pieces.add(p);
		}
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

		if (textPosition >= 35) {
			if (textPosition >= 400)
				textPosition -= 20;
			else if (textPosition < 400 && textPosition >= 250)
				textPosition -= 16;
			else if (textPosition < 250 && textPosition >= 135)
				textPosition -= 10;
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
}