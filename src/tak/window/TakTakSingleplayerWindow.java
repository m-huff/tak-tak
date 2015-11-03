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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import tak.com.Piece;
import tak.util.OrderedPair;
import tak.util.PlayerAI;
import tak.util.ScoreFader;
import tak.util.Sound;

public class TakTakSingleplayerWindow extends JFrame implements Runnable {

	static public final int WINDOW_WIDTH = 590;
	static public final int WINDOW_HEIGHT = 740;
	static final int XBORDER = 15;
	static final int YBORDER = 40;
	static final int YTITLE = 25;
	static boolean animateFirstTime = true;
	static int xsize = -1;
	static int ysize = -1;
	Image image;
	static Graphics2D g;

	private static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

	private static final int CENTER_X = (SCREEN_WIDTH / 2) - (WINDOW_WIDTH / 2);
	private static final int CENTER_Y = (SCREEN_HEIGHT / 2) - (WINDOW_HEIGHT / 2);

	public static Random rand = new Random();
	static ImageIcon icon = new ImageIcon(TakTakSingleplayerWindow.class.getResource("/tak/assets/icon.png"));
	static ImageIcon background = new ImageIcon(TakTakSingleplayerWindow.class.getResource("/tak/assets/wood.png"));

	//Board of real game is 6x7
	public static final int COLUMNS = 6;
	public static final int ROWS = 7;
	public static Piece[][] board;
	
	public static int numPiecesOnBoard;
	public static int numBlackPiecesOnBoard;
	public static int numWhitePiecesOnBoard;

	private final TakTakSingleplayerWindow frame = this;

	public static int turn;
	public static boolean myTurn;

	public static int selectedRow = 999;
	public static int selectedColumn = 999;
	public static int lilWindaRow = 999;
	public static int lilWindaColumn = 999;
	public static int mousex;
	public static int mousey;
	
	public static int myScore;
	public static int myWins;
	
	public static int aiScore;
	public static int aiMoveDelay;
	public static int aiWins;
	
	public static int fadeOut;
	
	static Sound move;
	static Sound cha_ching;

	public static ArrayList<OrderedPair> validMoves = new ArrayList<OrderedPair>();
        public static ArrayList<ScoreFader> faders = new ArrayList<ScoreFader>();

	public static int tipTime;
	public static String HINT_PREFIX = "Tip: ";
	public static String currentHint = "";
	public static String[] HINTS = {"Stack your pieces to move across the board faster!",
			"The king piece moves twice as fast, but won't stack anything on top of it!",
			"The king piece can also move backwards!",
			"Press the ESC key to quit the game and return to the main menu!",
			"Right-click a piece to see the stack size and total value!",
			"Press BACKSPACE after you've selected a piece to cancel the selection.",
			"Once you've selected a piece, all valid spaces to move turn green.",
			"Press the ESC key from any screen to return to the main menu.",
			"Right-clicking a piece will reveal all of the information about it!",
			"You can stack your pieces in your own safe zone to get pieces across faster!",
			"Your king piece can't be stacked on top of!",
			"The king can't be stacked on top of, so it will block a stack from growing.",
			"Even if a stack has a value of over 100 points, the king will limit it to 100.",
			"When you right-click a piece, you can see a cool 3D image of the entire stack!" };

	public static enum EnumWinner {
		PlayerOne,
		PlayerTwo,
		PlayerAI,
		Tie,
		None
	}
	public static EnumWinner winner;
	
	public TakTakSingleplayerWindow() {

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setTitle("Tak-Tak");
		setLocation(CENTER_X, CENTER_Y);
		setIconImage(icon.getImage());

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				lilWindaRow = 999;
				lilWindaColumn = 999;
				repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (MouseEvent.BUTTON1 == e.getButton() && myTurn) {

					int xpos = e.getX() - getX(0);
					int ypos = e.getY() - getY(0);
					if (xpos < 0 || ypos < 0 || xpos > getWidth2() || ypos > getHeight2())
						return;

					//Calculate the width and height of each board square.
					int ydelta = getHeight2() / ROWS;
					int xdelta = getWidth2() / COLUMNS;
					int currentColumn = xpos / xdelta;
					int currentRow = ypos / ydelta;

					if (currentRow > ROWS - 1) {
						currentRow = ROWS - 1;
					}

					if (currentColumn > COLUMNS - 1) {
						currentColumn = COLUMNS - 1;
					}

					if (currentRow < 0) {
						currentRow = 0;
					}

					if (currentColumn < 0) {
						currentColumn = 0;
					}

					if (selectedRow == 999 && board[currentRow][currentColumn] != null) {
						if (board[currentRow][currentColumn].getTopPiece().getBackgroundColor() == Color.white) {
							selectedRow = currentRow;
							selectedColumn = currentColumn;
						}
					} else if (selectedRow != 999) {
						boolean movedPiece = false;
						for (int i = 0; i < validMoves.size() && !movedPiece; i++) {
							if (validMoves.get(i).toString()
									.equals(new OrderedPair(currentRow, currentColumn).toString())) {
								movePieceToLocation(new OrderedPair(selectedRow, selectedColumn),
										new OrderedPair(currentRow, currentColumn));
								movedPiece = true;
								selectedRow = 999;
								selectedColumn = 999;
								validMoves.clear();
								//turn++;
								//Have turn only update after both players have gone
								//AI taking a turn adds one to turn counter
								myTurn = !myTurn;
							}
						}
					}
				}
				if (MouseEvent.BUTTON3 == e.getButton()) {

					int xpos = e.getX() - getX(0);
					int ypos = e.getY() - getY(0);
					if (xpos < 0 || ypos < 0 || xpos > getWidth2() || ypos > getHeight2())
						return;

					//Calculate the width and height of each board square.
					int ydelta = getHeight2() / ROWS;
					int xdelta = getWidth2() / COLUMNS;
					int currentColumn = xpos / xdelta;
					int currentRow = ypos / ydelta;

					if (currentRow > ROWS - 1) {
						currentRow = ROWS - 1;
					}

					if (currentColumn > COLUMNS - 1) {
						currentColumn = COLUMNS - 1;
					}

					if (currentRow < 0) {
						currentRow = 0;
					}

					if (currentColumn < 0) {
						currentColumn = 0;
					}

					if (lilWindaRow == 999 && board[currentRow][currentColumn] != null) {
						lilWindaRow = currentRow;
						lilWindaColumn = currentColumn;
						mousex = e.getX();
						mousey = e.getY();
					} else if (board[currentRow][currentColumn] == null) {
						//Tell the player the spot is empty
					} else if (lilWindaRow != 999) {

					}
				}
				repaint();
			}
		});

		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
					reset();
					myWins = 0;
					aiWins = 0;
					new MenuWindow();
					frame.dispose();
				}
				if (KeyEvent.VK_BACK_SPACE == e.getKeyCode()) {
					selectedRow = 999;
					selectedColumn = 999;
					validMoves.clear();
				}
				if (KeyEvent.VK_ENTER == e.getKeyCode() && winner != EnumWinner.None) {
					reset();
				}
				repaint();
			}
		});
		
		//Send the user back to the menu screen to make sure the entire system gets exited
		//Without this the sound will continue to run until it finishes	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				new MenuWindow();
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

		int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0) };
		int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0) };
		g.setColor(Color.white);
		g.fillPolygon(x, y, 4);

		g.setColor(new Color(64, 150, 64, 100));
		g.setFont(new Font("Arial Bold", Font.BOLD, 72));
		g.drawString("SAFE ZONE", 90, 635);
		g.drawString("SAFE ZONE", 90, 180);

		g.setColor(new Color(64, 128, 64, 150));
		g.fillRect(getX(0), getY(0), getWidth2(), 2 * (getHeight2() / ROWS) + 2);
		g.fillRect(getX(0), getY(0) + 5 * (getHeight2() / ROWS) + 5, getWidth2(), 2 * (getHeight2() / ROWS) + 2);

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
		if (lilWindaRow != 999 && board[lilWindaRow][lilWindaColumn] != null) {
			board[lilWindaRow][lilWindaColumn].drawLilWinda(g, mousex, mousey);
		}
		g.setColor(Color.white);
		g.setFont(new Font("Arial Bold", Font.PLAIN, 14));
		g.drawString(currentHint, 15, 725);
		
		g.drawString("Player Score: " + myScore, 40, 60);
		g.drawString("Your Wins: " + myWins, 40, 45);
		g.drawString("AI Score: " + aiScore, 460, 60);
		g.drawString("AI Wins: " + aiWins, 460, 45);
		g.setFont(new Font("Arial Bold", Font.BOLD, 18));
		g.drawString((myTurn ? "YOUR" : "OPPONENT'S") + " Turn", myTurn ? 245 : 210, 55);
		g.setFont(new Font("Arial Bold", Font.BOLD, 14));
		g.drawString("Turn #" + (turn + 1), 270, 40);

		g.setColor(new Color(0, 0, 0, fadeOut));
		g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		g.setColor(new Color(255, 255, 255, fadeOut));
		g.setFont(new Font("Arial Bold", Font.PLAIN, 36));
		if (winner != EnumWinner.None) {
				if (winner == EnumWinner.PlayerOne)
					g.drawString("You win!", 220, 300);
				if (winner == EnumWinner.PlayerAI)
					g.drawString("The AI won...", 190, 300);
				if (winner == EnumWinner.PlayerTwo)
					g.drawString("The opponent won...", 130, 300);
				if (winner == EnumWinner.Tie)
					g.drawString("You tied!", 220, 300);
				
				g.setFont(new Font("Arial Bold", Font.PLAIN, 22));
				g.drawString("Press ENTER to play again", 160, 390);
				g.drawString("Press ESC to return to the main menu", 100, 410);
				
				if (fadeOut < 230)
					fadeOut += 5;
		}
		
		if (winner == EnumWinner.None) {
			if (fadeOut > 0)
				fadeOut -= 5;
		}
                
                for (int i = 0; i < faders.size(); i++) {
                    faders.get(i).draw(g);
                }

		gOld.drawImage(image, 0, 0, null);
	}

	public void run() {
		while (true) {
			animate();
			repaint();
			double seconds = 0.04;
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
		
		//4 rows of 6
		numPiecesOnBoard = 24;
		
		numWhitePiecesOnBoard = 12;
		numBlackPiecesOnBoard = 12;

		tipTime = 0;
		currentHint = HINT_PREFIX + HINTS[rand.nextInt(HINTS.length)];

		turn = 0;
		myTurn = true;

		selectedRow = 999;
		selectedColumn = 999;

		validMoves.clear();
		
		myScore = 0;
		aiScore = 0;
		
		winner = EnumWinner.None;
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

		if (tipTime < 200) {
			tipTime++;
		} else {
			tipTime = 0;
			currentHint = HINT_PREFIX + HINTS[rand.nextInt(HINTS.length)];
		}
		
		if (numBlackPiecesOnBoard > 0) {
			if (!myTurn && aiMoveDelay >= 50) {
				//TODO - well this is buggy
				PlayerAI.makeMove();
				myTurn = !myTurn;
				turn++;
				aiMoveDelay = 0;
			} else if (!myTurn && aiMoveDelay < 50) {
				aiMoveDelay++;
			}
		} else {
			turn++;
		}
		
		if (numWhitePiecesOnBoard == 0 && myTurn) {
			myTurn = !myTurn;
		}
	}

	public static void movePieceToLocation(OrderedPair piece, OrderedPair location) {

		if (board[location.getX()][location.getY()] != null) {
			// Stacking isn't working right, doesn't add stacks correctly because
			// the contructor in Piece adds itself to the stack...
			board[location.getX()][location.getY()].addStackToStack(board[piece.getX()][piece.getY()].getWholeStack());
			board[piece.getX()][piece.getY()] = null;
		} else {
			//This works just fine
			board[location.getX()][location.getY()] = board[piece.getX()][piece.getY()];
			board[piece.getX()][piece.getY()] = null;
		}
                
		//Pieces are in opponent's safe zone
		if (location.getX() >= 5 && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black ||
			location.getX() < 2 && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.white) {
			if (board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black) {
				aiScore += board[location.getX()][location.getY()].getValue();
			} else {
				myScore += board[location.getX()][location.getY()].getValue();
			}
			
			int whitePieces = 0, blackPieces = 0;
			for (int i = 0; i < board[location.getX()][location.getY()].getWholeStack().size(); i++) {
				if (board[location.getX()][location.getY()].getWholeStack().get(i).getBackgroundColor() == Color.WHITE)
					whitePieces++;
				else
					blackPieces++;
			}
                        
            Color c;
            if (location.getX() >= 5 && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black)
                c = new Color(128, 64, 64);
            else
                c = new Color(64, 180, 64);
            
            //Works
            ScoreFader sf = new ScoreFader(board[location.getX()][location.getY()].getValue(),getX(0) + location.getY() * getWidth2() / COLUMNS,
				getY(0) + location.getX() * getHeight2() / ROWS, c);

			numWhitePiecesOnBoard -= whitePieces;
			numBlackPiecesOnBoard -= blackPieces;
			numPiecesOnBoard -= board[location.getX()][location.getY()].getWholeStack().size();
			board[location.getX()][location.getY()] = null;
			move = new Sound("chaching.wav");
			if (numWhitePiecesOnBoard == 0 || numBlackPiecesOnBoard == 0) {
				//If one side doesn't have any more pieces
				//Score all remaining pieces
				for (int zRow = 0; zRow < ROWS; zRow++) {
					for (int zColumn = 0; zColumn < COLUMNS; zColumn++) {
						if (board[zRow][zColumn] != null) {
							if (board[zRow][zColumn].getTopPiece().getBackgroundColor() == Color.black) {
								aiScore += board[zRow][zColumn].getValue();
								ScoreFader sf2 = new ScoreFader(board[zRow][zColumn].getValue(),getX(0) + zColumn * getWidth2() / COLUMNS,
										getY(0) + zRow * getHeight2() / ROWS, new Color(128, 64, 64));
							} else {
								myScore += board[zRow][zColumn].getValue();
								ScoreFader sf2 = new ScoreFader(board[zRow][zColumn].getValue(),getX(0) + zColumn * getWidth2() / COLUMNS,
										getY(0) + zRow * getHeight2() / ROWS, new Color(64, 180, 64));
							}
							board[zRow][zColumn] = null;
						}
					}
				}
				//End the game
				chooseWinner();
			}
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
		Piece blackKing = new Piece(0, Color.black, Color.black);
		blackKing.setKing(true);
		board[row][column] = blackKing;

		row = rand.nextInt(2) + 5;
		column = rand.nextInt(COLUMNS);
		Piece whiteKing = new Piece(0, Color.white, Color.white);
		whiteKing.setKing(true);
		board[row][column] = whiteKing;
	}
	
	public static void chooseWinner() {
		if (myScore > aiScore) {
			winner = EnumWinner.PlayerOne;
			myWins++;
		}
		else if (aiScore > myScore) {
			winner = EnumWinner.PlayerAI;
			aiWins++;
		}
		else if (aiScore == myScore) {
			winner = EnumWinner.Tie;
		}
	}

	public void displayAllValidMoves(Graphics2D g, int row, int column) {

		// Show all spaces that the piece can move to, represented by green rectangles
		// The piece can move to a space if it is one space above/below it, or diagonal,
		// depending on the color of the piece. The move is allowed if there is another
		// piece at the location to move to, IF the piece at the desired location has the
		// same color or value as the piece you're moving. Kings can move onto any piece,
		// no matter what the value or color. If a piece/stack is a king, then a move to
		// that space is not possible.

		g.setColor(new Color(10, 10, 10, 150));
		g.fillRect(column * (getWidth2() / COLUMNS) + getX(0), row * (getHeight2() / ROWS) + getY(0), 94, 94);

		Piece p = board[row][column];
		int pieceDirection = (p.getTopPiece().getBackgroundColor() == Color.black ? 0 : 1);

		g.setColor(new Color(64, 128, 64, 150));

		if (p.getTopPiece().isKing()) {
			if (canPieceMoveToLocation(p.getTopPiece(), row + 1, column)) {
				g.fillRect(column * (getWidth2() / COLUMNS) + getX(0), (row + 1) * (getHeight2() / ROWS) + getY(0), 94,
						94);
				validMoves.add(new OrderedPair(row + 1, column));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row + 2, column)) {
				g.fillRect(column * (getWidth2() / COLUMNS) + getX(0), (row + 2) * (getHeight2() / ROWS) + getY(0), 94,
						94);
				validMoves.add(new OrderedPair(row + 2, column));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row + 1, column + 1)) {
				g.fillRect((column + 1) * (getWidth2() / COLUMNS) + getX(0),
						(row + 1) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row + 1, column + 1));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row + 2, column + 2)) {
				g.fillRect((column + 2) * (getWidth2() / COLUMNS) + getX(0),
						(row + 2) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row + 2, column + 2));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row + 1, column - 1)) {
				g.fillRect((column - 1) * (getWidth2() / COLUMNS) + getX(0),
						(row + 1) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row + 1, column - 1));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row + 2, column - 2)) {
				g.fillRect((column - 2) * (getWidth2() / COLUMNS) + getX(0),
						(row + 2) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row + 2, column - 2));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column)) {
				g.fillRect(column * (getWidth2() / COLUMNS) + getX(0), (row - 1) * (getHeight2() / ROWS) + getY(0), 94,
						94);
				validMoves.add(new OrderedPair(row - 1, column));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row - 2, column)) {
				g.fillRect(column * (getWidth2() / COLUMNS) + getX(0), (row - 2) * (getHeight2() / ROWS) + getY(0), 94,
						94);
				validMoves.add(new OrderedPair(row - 2, column));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column + 1)) {
				g.fillRect((column + 1) * (getWidth2() / COLUMNS) + getX(0),
						(row - 1) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row - 1, column + 1));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row - 2, column + 2)) {
				g.fillRect((column + 2) * (getWidth2() / COLUMNS) + getX(0),
						(row - 2) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row - 2, column + 2));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column - 1)) {
				g.fillRect((column - 1) * (getWidth2() / COLUMNS) + getX(0),
						(row - 1) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row - 1, column - 1));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row - 2, column - 2)) {
				g.fillRect((column - 2) * (getWidth2() / COLUMNS) + getX(0),
						(row - 2) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row - 2, column - 2));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row, column - 1)) {
				g.fillRect((column - 1) * (getWidth2() / COLUMNS) + getX(0),
						(row) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row, column - 1));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row, column - 2)) {
				g.fillRect((column - 2) * (getWidth2() / COLUMNS) + getX(0),
						(row) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row, column - 2));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row, column + 1)) {
				g.fillRect((column + 1) * (getWidth2() / COLUMNS) + getX(0),
						(row) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row, column + 1));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row, column + 2)) {
				g.fillRect((column + 2) * (getWidth2() / COLUMNS) + getX(0),
						(row) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row, column + 2));
			}
		}

		if (!p.getTopPiece().isKing() && pieceDirection == 1) {
			if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column)) {
				g.fillRect(column * (getWidth2() / COLUMNS) + getX(0), (row - 1) * (getHeight2() / ROWS) + getY(0), 94,
						94);
				validMoves.add(new OrderedPair(row - 1, column));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column + 1)) {
				g.fillRect((column + 1) * (getWidth2() / COLUMNS) + getX(0),
						(row - 1) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row - 1, column + 1));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column - 1)) {
				g.fillRect((column - 1) * (getWidth2() / COLUMNS) + getX(0),
						(row - 1) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row - 1, column - 1));
			}
		}

		if (!p.getTopPiece().isKing() && pieceDirection == 0) {
			if (canPieceMoveToLocation(p.getTopPiece(), row + 1, column)) {
				g.fillRect(column * (getWidth2() / COLUMNS) + getX(0), (row + 1) * (getHeight2() / ROWS) + getY(0), 94,
						94);
				validMoves.add(new OrderedPair(row + 1, column));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row + 1, column + 1)) {
				g.fillRect((column + 1) * (getWidth2() / COLUMNS) + getX(0),
						(row + 1) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row + 1, column + 1));
			}
			if (canPieceMoveToLocation(p.getTopPiece(), row + 1, column - 1)) {
				g.fillRect((column - 1) * (getWidth2() / COLUMNS) + getX(0),
						(row + 1) * (getHeight2() / ROWS) + getY(0), 94, 94);
				validMoves.add(new OrderedPair(row + 1, column - 1));
			}
		}
	}
	
	
	public void start() {
		if (relaxer == null) {
			relaxer = new Thread(this);
			relaxer.start();
		}
	}

	public static boolean canPieceMoveToLocation(Piece _piece, int row, int column) {
		//Check if the desired place is within the bounds of the board, and
		//if the piece is allowed to move there.
		
		//Rules: if there is no piece where you're trying to move, there will
		//		 never be any reason why you can't move there. If there IS a
		//       piece there, you can stack on top of that piece/stack if your
		//       piece has the same number/color as the top piece of the stack,
		//       unless the top piece of the stack is a king, in which case no
		//       piece can stack on top of it. Further, you can't stack your
		//       piece on top of another piece if it is still in it's safe zone.
		
		if (row >= 0 && row < ROWS && column >= 0 && column < COLUMNS) {
			//If there's no piece there
			if (board[row][column] == null) {
				return true;
			}
			//If there IS a piece there
			else if (board[row][column] != null && !_piece.getTopPiece().isKing()) {
				//If the piece is a king
				if (board[row][column].getTopPiece().isKing()) {
					return false;
				}
				//Can't go into opponent safe zone if a piece is there
				if (row >= 5 && _piece.getTopPiece().getBackgroundColor() == Color.BLACK) {
					return false;
				}
				if (row < 2 && _piece.getTopPiece().getBackgroundColor() == Color.WHITE) {
					return false;
				}
				//If the piece has a good color or value
				if (board[row][column].getTopPiece().getValue() == _piece.getTopPiece().getValue()
						|| board[row][column].getTopPiece().getForegroundColor() == _piece.getTopPiece().getForegroundColor()) {
					return true;
				}
			} else if (board[row][column] != null && _piece.getTopPiece().isKing()) {
				if (board[row][column].getTopPiece().isKing()) {
					return false;
				}
				if (row >= 5 && _piece.getTopPiece().getBackgroundColor() == Color.BLACK) {
					return false;
				}
				if (row < 2 && _piece.getTopPiece().getBackgroundColor() == Color.WHITE) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	public static void updateTurn() {
		myTurn = !myTurn;
	}

	public void stop() {
		if (relaxer.isAlive()) {
			relaxer.stop();
		}
		relaxer = null;
	}

	public static int getX(int x) {
		return (x + XBORDER);
	}

	public static int getY(int y) {
		return (y + YBORDER + YTITLE);
	}

	public static int getYNormal(int y) {
		return (-y + YBORDER + YTITLE + getHeight2());
	}

	public static int getWidth2() {
		return (xsize - getX(0) - XBORDER);
	}

	public static int getHeight2() {
		return (ysize - getY(0) - YBORDER);
	}
}
