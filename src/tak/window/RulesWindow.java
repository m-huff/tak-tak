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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	public static ImageIcon icon = new ImageIcon(RulesWindow.class.getResource("/tak/assets/icon.png"));
	static ImageIcon background = new ImageIcon(RulesWindow.class.getResource("/tak/assets/wood.png"));

	private final RulesWindow frame = this;

	public static ArrayList<Image> images = new ArrayList<Image>();
	public static String[] imageText = {"Tak-Tak pieces can move forward, straight and diagonally.",
										"You can move your own pieces on top of each other, stacking them.",
										"Right-click a piece to see how many pieces are in its stack.",
										"Stacking pieces lets you make more efficient use of your turns,",
										"Getting your pieces into the opponent's safe zone is how you score.",
										"In your safe zone, your pieces are safe from being stacked on.",
										"Stacking pieces allows you to score more points with a single move;",
										"Stacking on top of another piece/stack will add its points to",
										"You can stack on top of opponent's stacks to make them your own.",
										"The king can never be stacked on top of, and can also stack",
										"Your goal is to score the most points in your opponent's safe zone."
										};
	
	public static String[] imageText2 = {"If you accidentally select a piece, press BACKSPACE to cancel.",
										"You can only stack pieces if they have the same point value or color.",
										"This display will also tell you the total point value of the stack.",
										"allowing you to move more of your pieces across the board in a turn.",
										"When a piece reaches the opponent's safe zone, it disappears.",
										"Likewise, you can't stack on your opponent's pieces in their safe zone.",
                                        "there is no limit to how many points you can score from a single stack!",
                                        "the stack that stacked on top of it.",
										"You will be able to control any stack whose top piece is your color.",
										"on top of any other piece, regardless of color/number.",
										"",
										};

	
	public static ArrayList<Piece> pieces = new ArrayList<Piece>();
	public static int currentSlide;
	
	public static boolean hasChangedSlides;

	public RulesWindow() {

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setIconImage(icon.getImage());
		setTitle("Tak-Tak");
		setLocation(CENTER_X, CENTER_Y);

		addKeyListener(new KeyAdapter() {

			@SuppressWarnings("static-access")
			public void keyPressed(KeyEvent e) {
				if (e.VK_RIGHT == e.getKeyCode()) {
					if (currentSlide < images.size() - 1)
						currentSlide++;
					else
						currentSlide = 0;
					hasChangedSlides = true;
				}
				
				if (e.VK_LEFT == e.getKeyCode()) {
					if (currentSlide > 0)
						currentSlide--;
					else
						currentSlide = images.size() - 1;
					hasChangedSlides = true;
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
					if (y < 160 || y >= 160 && x < 80 || y >= 160 && x > 400)
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
		g.fillRect(0, 0, WINDOW_WIDTH, 100);

		g.setColor(new Color(240, 240, 240));
		g.setFont(new Font("Arial", Font.BOLD, 42));
		g.drawString("TAK-TAK Rules", 30, 80);

		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.drawString(imageText[currentSlide], 95, 500);
		g.drawString(imageText2[currentSlide], 95, 520);
		if (!images.isEmpty())
			g.drawImage(images.get(currentSlide), 112, 190, null);
		
		g.setFont(new Font("Arial", Font.BOLD, 18));
		
		if (currentSlide == 0 && !hasChangedSlides) {
			g.setFont(new Font("Arial", Font.BOLD, 12));
			g.setColor(Color.darkGray);
			g.drawString("USE THE LEFT AND RIGHT ARROW KEYS TO NAVIGATE THE RULES", 102, 590);
		}

		g.setColor(new Color(240, 240, 240));
		g.setFont(new Font("Arial", Font.BOLD, 18));
		g.drawString((currentSlide + 1) + " of " + images.size(), 260, 610);

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
		currentSlide = 0;
		hasChangedSlides = false;
		
		for (int i = 0; i < 50; i++) {
			Color[] colors = {Color.orange, Color.blue, Color.green };
			Piece p = new Piece((rand.nextInt(4) * 10) + 10, colors[rand.nextInt(colors.length)],
					rand.nextBoolean() ? Color.black : Color.white);
			if (rand.nextInt(10) == 0)
				p.setKing(true);
			pieces.add(p);
		}
		
		if (images.isEmpty()) {
			for (int i = 0; i < 11; i++) {
				images.add(new ImageIcon(RulesWindow.class.getResource("/tak/assets/gifs/" + (i + 1) + ".gif")).getImage());
			}
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