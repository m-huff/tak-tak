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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import tak.com.Piece;

public class TakTakWindow extends JFrame implements Runnable {

	static final int WINDOW_WIDTH = 590;
	static final int WINDOW_HEIGHT = 740;
	final int XBORDER = 15;
	final int YBORDER = 40;
	final int YTITLE = 25;
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
	static ImageIcon icon = new ImageIcon(TakTakWindow.class.getResource("/tak/assets/icon.png"));

	//Board of real game is 6x7
	static final int COLUMNS = 6;
	static final int ROWS = 7;
	Piece[][] board;

	private final TakTakWindow frame = this;
	
	public static int turn;
	public static boolean p1Turn;
	
	public static int selectedRow;
	public static int selectedColumn;

	public static int tipTime;
	public static String HINT_PREFIX = "Tip: ";
	public static String currentHint;
	public static String[] HINTS = {"Stack your pieces to move across the board faster!",
			"The king piece moves twice as fast, but won't stack anything on top of it!",
			"The king piece can also move backwards!",
			"Press the ESC key to quit the game and return to the main menu!",
			"Right-click a piece to see the stack size and total value!",
			"Press BACKSPACE after you've selected a piece to cancel the selection.",
			"Once you've selected a piece, all valid spaces to move turn green." };

	public TakTakWindow() {

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setTitle("Tak-Tak");
		setLocation(CENTER_X, CENTER_Y);
		setIconImage(icon.getImage());

		addMouseListener(new MouseAdapter() {
			@SuppressWarnings("static-access")
			public void mousePressed(MouseEvent e) {
				if (e.BUTTON1 == e.getButton()) {

				}
				repaint();
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@SuppressWarnings("static-access")
			public void mousePressed(MouseEvent e) {
				if (e.BUTTON1 == e.getButton()) {

					int xpos = e.getX() - getX(0);
					int ypos = e.getY() - getY(0);
					if (xpos < 0 || ypos < 0 || xpos > getWidth2() || ypos > getHeight2())
						return;
					
					//Calculate the width and height of each board square.
					int ydelta = getHeight2()/ROWS;
					int xdelta = getWidth2() / COLUMNS;
					int currentColumn = xpos / xdelta;
					int currentRow = ypos / ydelta;

					if (currentRow > ROWS - 1) {
						currentRow = ROWS - 1;
					}

					if (currentColumn > COLUMNS - 1) {
						currentColumn = COLUMNS - 1;
					}
					
					if (selectedRow == 999 && board[currentRow][currentColumn] != null) {
						selectedRow = currentRow;
						selectedColumn = currentColumn;
					}
					else if (board[currentRow][currentColumn] == null){
						//Tell the player the spot is empty
					}
					else if (selectedRow != 999) {
						//Try to move the piece to the selected location
					}
				}
				repaint();
			}
		});

		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if (e.VK_ESCAPE == e.getKeyCode()) {
					new MenuWindow();
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

		//Drawing the initial board

		g.setColor(Color.black);
		g.fillRect(0, 0, xsize, ysize);

		if (animateFirstTime) {
			gOld.drawImage(image, 0, 0, null);
			return;
		}

		int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0) };
		int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0) };
		g.setColor(Color.white);
		g.fillPolygon(x, y, 4);

		g.setColor(new Color(64, 150, 64, 100));
		g.setFont(new Font("Arial Bold", Font.BOLD, 72));
		g.drawString("SAFE ZONE", 90, 635);
		g.drawString("SAFE ZONE", 90, 180);

		//Light green rectangles as the 'safe zones'

		g.setColor(new Color(64, 128, 64, 150));
		g.fillRect(getX(0), getY(0), getWidth2(), 2 * (getHeight2() / ROWS) + 2);
		g.fillRect(getX(0), getY(0) + 5 * (getHeight2() / ROWS) + 5, getWidth2(), 2 * (getHeight2() / ROWS) + 2);

		//Draw a dark grey rectangle over the middle of the board

		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(getX(0), 2 * (getHeight2() / ROWS) + getY(0), getWidth2(), 3 * (getHeight2() / ROWS) + 5);

		g.setColor(Color.black);
		for (int i = 1; i < ROWS; i++) {
			g.drawLine(0, getY(0) + i * getHeight2() / ROWS, getX(getWidth2()), getY(0) + i * getHeight2() / ROWS);
		}

		for (int i = 1; i < COLUMNS; i++) {
			g.drawLine(getX(0) + i * getWidth2() / COLUMNS, getY(0), getX(0) + i * getWidth2() / COLUMNS,
					getY(getHeight2()));
		}

		for (int zRow = 0; zRow < ROWS; zRow++) {
			for (int zColumn = 0; zColumn < COLUMNS; zColumn++) {
				if (board[zRow][zColumn] != null) {
					board[zRow][zColumn].draw(g, getX(0) + zColumn * getWidth2() / COLUMNS,
							getY(0) + zRow * getHeight2() / ROWS);
				}
			}
		}
		
		if (selectedRow != 999) {
			displayAllValidMoves(g, selectedRow, selectedColumn);
		}

		g.setColor(Color.white);
		g.setFont(new Font("Arial Bold", Font.PLAIN, 14));
		g.drawString(currentHint, 15, 725);

		gOld.drawImage(image, 0, 0, null);
	}

	public void run() {
		while (true) {
			animate();
			repaint();
			double seconds = 0.04;//time that 1 frame takes.
			int miliseconds = (int) (1000.0 * seconds);
			try {
				Thread.sleep(miliseconds);
			} catch (InterruptedException e) {
			}
		}
	}

	public void reset() {
		board = new Piece[ROWS][COLUMNS];
		resetBoard();

		tipTime = 0;
		currentHint = HINT_PREFIX + HINTS[rand.nextInt(HINTS.length)];
		
		turn = 0;
		p1Turn = true;
		
		selectedRow = 999;
		selectedColumn = 999;
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

		if (tipTime < 150) {
			tipTime++;
		} else {
			tipTime = 0;
			currentHint = HINT_PREFIX + HINTS[rand.nextInt(HINTS.length)];
		}
	}

	public void resetBoard() {

		int value = 10;

		for (int blue = 0; blue < 4; blue++) {
			int row = rand.nextInt(2) + 5;
			int column = rand.nextInt(COLUMNS);

			while (board[row][column] != null) {
				row = rand.nextInt(2) + 5;
				column = rand.nextInt(COLUMNS);
			}

			board[row][column] = new Piece(value, Color.blue, Color.white);
			value += 10;
		}

		value = 10;

		for (int orange = 0; orange < 4; orange++) {
			int row = rand.nextInt(2) + 5;
			int column = rand.nextInt(COLUMNS);

			while (board[row][column] != null) {
				row = rand.nextInt(2) + 5;
				column = rand.nextInt(COLUMNS);
			}

			board[row][column] = new Piece(value, Color.orange, Color.white);
			value += 10;
		}

		value = 10;

		for (int green = 0; green < 4; green++) {
			int row = rand.nextInt(2) + 5;
			int column = rand.nextInt(COLUMNS);

			while (board[row][column] != null) {
				row = rand.nextInt(2) + 5;
				column = rand.nextInt(COLUMNS);
			}

			board[row][column] = new Piece(value, Color.green, Color.white);
			value += 10;
		}

		value = 10;

		for (int blue = 0; blue < 4; blue++) {
			int row = rand.nextInt(2);
			int column = rand.nextInt(COLUMNS);

			while (board[row][column] != null) {
				row = rand.nextInt(2);
				column = rand.nextInt(COLUMNS);
			}

			board[row][column] = new Piece(value, Color.blue, Color.black);
			value += 10;
		}

		value = 10;

		for (int orange = 0; orange < 4; orange++) {
			int row = rand.nextInt(2);
			int column = rand.nextInt(COLUMNS);

			while (board[row][column] != null) {
				row = rand.nextInt(2);
				column = rand.nextInt(COLUMNS);
			}

			board[row][column] = new Piece(value, Color.orange, Color.black);
			value += 10;
		}

		value = 10;

		for (int green = 0; green < 4; green++) {
			int row = rand.nextInt(2);
			int column = rand.nextInt(COLUMNS);

			while (board[row][column] != null) {
				row = rand.nextInt(2);
				column = rand.nextInt(COLUMNS);
			}

			board[row][column] = new Piece(value, Color.green, Color.black);
			value += 10;
		}
		
		//Puts the kings on the board
		
		int row = rand.nextInt(2);
		int column = rand.nextInt(COLUMNS);
		Piece blackKing = new Piece(value, Color.black, Color.black);
		blackKing.setKing(true);
		board[row][column] = blackKing;
		
		row = rand.nextInt(2) + 5;
		column = rand.nextInt(COLUMNS);
		Piece whiteKing = new Piece(value, Color.white, Color.white);
		whiteKing.setKing(true);
		board[row][column] = whiteKing;
	}
	
	public void displayAllValidMoves(Graphics2D g, int row, int column) {
		//Make the selected spot appear faded
		g.setColor(new Color(10, 10, 10, 150));
		g.fillRect(column * (getWidth2() / COLUMNS) + getX(0), row * (getHeight2() / ROWS) + getY(0), 94, 94);
		
		//Display the moves the piece can make (forward and diagonal, unless it's a king)
		//Pieces can't move into the enemy safe zone unless the spot is empty
		//Maybe instead of just highlighting the valid moves in green, draw checkmarks to emphasize
	}

	public void start() {
		if (relaxer == null) {
			relaxer = new Thread(this);
			relaxer.start();
		}
	}

	public void stop() {
		if (relaxer.isAlive()) {
			relaxer.stop();
		}
		relaxer = null;
	}

	public int getX(int x) {
		return (x + XBORDER);
	}

	public int getY(int y) {
		return (y + YBORDER + YTITLE);
	}

	public int getYNormal(int y) {
		return (-y + YBORDER + YTITLE + getHeight2());
	}

	public int getWidth2() {
		return (xsize - getX(0) - XBORDER);
	}

	public int getHeight2() {
		return (ysize - getY(0) - YBORDER);
	}
}
