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
public class MenuWindow extends JFrame implements Runnable {

	static final int XBORDER = 20;
	static final int YBORDER = 20;
	static final int YTITLE = 30;
	static final int WINDOW_BORDER = 8;
	static final int WINDOW_WIDTH = 2 * (WINDOW_BORDER + XBORDER) + 495;
	static final int WINDOW_HEIGHT = YTITLE + WINDOW_BORDER + 2 * YBORDER + 225;
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

	private final MenuWindow frame = this;

	//Does not reset itself when the user escapes to the main menu after playing
	public static int textPosition = WINDOW_WIDTH;

	public static ArrayList<Piece> pieces = new ArrayList<Piece>();

	public MenuWindow() {

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setIconImage(icon.getImage());
		setTitle("Tak-Tak");
		setLocation(CENTER_X, CENTER_Y);

		addKeyListener(new KeyAdapter() {

			@SuppressWarnings("static-access")
			public void keyPressed(KeyEvent e) {
				if (e.VK_X == e.getKeyCode()) {
					new TakTakSingleplayerWindow();
					frame.dispose();
				}
				if (e.VK_Z == e.getKeyCode()) {
					final RulesWindow rw = new RulesWindow();
					rw.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
				if (e.VK_C == e.getKeyCode()) {
					final NetworkWindow rw = new NetworkWindow(true);
					frame.dispose();
				}
				if (e.VK_V == e.getKeyCode()) {
					final NetworkWindow rw = new NetworkWindow(false);
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

		g.setColor(new Color(0, 0, 0, 230));
		g.fillRect(0, 0, WINDOW_WIDTH, 210);

		g.setColor(new Color(240, 240, 240));
		g.setFont(new Font("Arial", Font.BOLD, 42));
		g.drawString("TAK-TAK", textPosition, 80);

		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString("Press X to play against an AI", textPosition, 155);
		g.drawString("Press Z to learn the rules", textPosition, 135);
		g.drawString("Press C to start a multiplayer game as the client", textPosition, 175);
		g.drawString("Press V to start a multiplayer game as the server", textPosition, 195);

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
			else if (textPosition < 250 && textPosition >= 100)
				textPosition -= 10;
			else if (textPosition < 100)
				textPosition -= 6;
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