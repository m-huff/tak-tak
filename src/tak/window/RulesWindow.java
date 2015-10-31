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
	static final int WINDOW_WIDTH = 575;
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

	private final RulesWindow frame = this;
	
	//Can't implement this until the game is fully working
	//GIFs probably
	public static ArrayList<Image> images = new ArrayList<Image>();
	public static String[] imageText = {"Tak-Tak pieces can move forward, straight and diagonally.",
										"You can move your own pieces on top of each other, stacking them.",
										"Right-click a piece to see how many pieces are in its stack.",
										"The more pieces in a stack, the higher the point value.",
										"Getting your pieces into the opponent's safe zone is how you score.",
										"You can't stack pieces on the opponent's if in their safe zone.",
										"Your king piece can move two spaces in any direction.",
										"It can even move backwards, unlike any other piece!",
										"Stacks can be worth an unlimited number of points, unless it",
										"You can stack on top of opponent's stacks to make them your own.",
										"The king can never be stacked on top of, but can stack on top of",
										"Your goal is to score the most points in your opponent's safe zone.",
										"Stacking your and your opponent's pieces allows you to move"};
	
	public static String[] imageText2 = {"If you accidentally select a piece, press BACKSPACE to cancel.",
										"You can only stack pieces if they have the same point value or color.",
										"This display will also tell you the total point value of the stack.",
										"",
										"",
										"When a piece reaches the opponent's safe zone, it disappears.",
										"",
										"",
										"contains the king. If the king is stacked, the entire value of the",
										"",
										"others. You can then control them for yourself, and score them.",
										"",
										" across the board faster, and steal points from the opponent."};

	
	public static ArrayList<Piece> pieces = new ArrayList<Piece>();
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
				
				if (e.VK_RIGHT == e.getKeyCode()) {
					if (currentSlide < images.size() - 1)
						currentSlide++;
					else
						currentSlide = 0;
				}
				
				if (e.VK_LEFT == e.getKeyCode()) {
					if (currentSlide > 0)
						currentSlide--;
					else
						currentSlide = images.size() - 1;
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

		g.setColor(Color.black);
		g.fillRect(85, 165, WINDOW_WIDTH - 170, WINDOW_HEIGHT - 165);
		g.setColor(new Color(0, 0, 0, 230));
		g.fillRect(0, 0, WINDOW_WIDTH, 100);

		g.setColor(new Color(240, 240, 240));
		g.setFont(new Font("Arial", Font.BOLD, 42));
		g.drawString("TAK-TAK Rules", 30, 80);

		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.drawString(imageText[currentSlide], 95, 500);
		g.drawString(imageText2[currentSlide], 95, 520);
		if (currentSlide == 8) {
			//The only one that needs a third line
			g.drawString("stack becomes 100.", 95, 540);
		}
		g.drawImage(images.get(currentSlide), 112, 190, null);
		
		g.setFont(new Font("Arial", Font.BOLD, 18));
		
		if (currentSlide == 0) {
			
			g.setFont(new Font("Arial", Font.BOLD, 14));
			g.drawString("Use the LEFT and RIGHT arrow keys to navigate.", 115, 590);
		}

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
		
		for (int i = 0; i < 50; i++) {
			Color[] colors = {Color.orange, Color.blue, Color.green };
			Piece p = new Piece((rand.nextInt(4) * 10) + 10, colors[rand.nextInt(colors.length)],
					rand.nextBoolean() ? Color.black : Color.white);
			if (rand.nextInt(10) == 0)
				p.setKing(true);
			pieces.add(p);
		}
		
		if (images.size() == 0) {
			for (int i = 0; i < 13; i++) {
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