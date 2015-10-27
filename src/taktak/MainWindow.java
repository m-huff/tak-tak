package taktak;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.util.Random;
import javax.swing.*;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements Runnable {

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
	public static ImageIcon icon = new ImageIcon(MainWindow.class.getResource("/taktak/assets/icon.png"));
        public static ImageIcon back = new ImageIcon(MainWindow.class.getResource("/taktak/assets/background.jpg"));

	private final MainWindow frame = this;

	public MainWindow() {

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
					new TakTakWindow();
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
                
                g.drawImage(back.getImage(), 0, 0, back.getIconWidth(), back.getIconHeight(), this);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 42));
		g.drawString("Tak-Tak", 30, 80);

		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString("Press X to play", 30, 105);

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