package tak.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import tak.com.Piece;

@SuppressWarnings("serial")
public class RulesWindow extends JFrame implements Runnable {

	static final int XBORDER = 20;
	static final int YBORDER = 20;
	static final int YTITLE = 30;
	static final int WINDOW_BORDER = 8;
	static final int WINDOW_WIDTH = 590;
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

	public static ImageIcon icon = new ImageIcon(RulesWindow.class.getResource("/tak/assets/icon.png"));

	private final RulesWindow frame = this;
	
	//Can't implement this until the game is fully working
	public static ArrayList<Image> images = new ArrayList<Image>();
	public static String[] imageText = {""};
	
	public static int currentSlide;

	public RulesWindow() {

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setIconImage(icon.getImage());
		setTitle("Tak-Tak");
		setLocation(CENTER_X, CENTER_Y);

		addKeyListener(new KeyAdapter() {

			@SuppressWarnings("static-access")
			public void keyPressed(KeyEvent e) {
				if (e.VK_ESCAPE == e.getKeyCode()) {
					frame.dispose();
				}

				repaint();
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

		g.setColor(new Color(100, 100, 100, 80));
		g.fillRect(0, 0, WINDOW_WIDTH, 110);

		g.setColor(new Color(240, 240, 240));
		g.setFont(new Font("Arial", Font.BOLD, 42));
		g.drawString("TAK-TAK Rules", 30, 80);

		g.setFont(new Font("Arial", Font.BOLD, 18));
		g.drawString("Image Text", 30, 500);
		g.drawString("Left-click to go to the next slide", 30, 550);
		g.drawString("Right-click to go back a slide", 30, 570);

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
		currentSlide = 1;
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
}