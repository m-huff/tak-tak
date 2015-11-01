package tak.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import tak.net.ClientHandler;
import tak.net.ServerHandler;
import tak.util.OrderedPair;
import tak.util.Sound;

public class TakTakMultiplayerWindow extends TakTakSingleplayerWindow {

	private static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

	private static final int CENTER_X = (SCREEN_WIDTH / 2) - (WINDOW_WIDTH / 2);
	private static final int CENTER_Y = (SCREEN_HEIGHT / 2) - (WINDOW_HEIGHT / 2);

	private final TakTakMultiplayerWindow frame = this;
	
	//The client player will always go first and play as black.

	//Network variables
	public static boolean isClient;
	public static int initRow;
	public static int initCol;
	public static int movedRow;
	public static int movedCol;
	
	public static int myScore;
	public static Color myColor;
	
	public static int opponentScore;
	public static int opponentWins;
	
	public static int gameDelayTimer;
	
	static Sound tick;

	public TakTakMultiplayerWindow() {

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
						if (board[currentRow][currentColumn].getBackgroundColor() == myColor) {
							selectedRow = currentRow;
							selectedColumn = currentColumn;
						}
					} else if (selectedRow != 999) {
						boolean movedPiece = false;
						for (int i = 0; i < validMoves.size() && !movedPiece; i++) {
							if (validMoves.get(i).toString().equals(new OrderedPair(currentRow, currentColumn).toString())) {
								movedPiece = true;
								validMoves.clear();
								initRow = selectedRow;
								initCol = selectedColumn;
								movedRow = currentRow;
								movedCol = currentColumn;
								initMovePiece();
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
					if (isClient)
                    {
                        ClientHandler.sendDisconnect();
                        ClientHandler.disconnect();
                    }
                    else
                    {
                        ServerHandler.sendDisconnect();
                        ServerHandler.disconnect();
                    }
					myWins = 0;
					opponentWins = 0;
					new MenuWindow();
					frame.dispose();
				}
				if (KeyEvent.VK_BACK_SPACE == e.getKeyCode()) {
					selectedRow = 999;
					selectedColumn = 999;
					validMoves.clear();
				}
				repaint();
			}
		});
		
		//Send the user back to the menu screen to make sure the entire system gets exited
		//Without this the sound will continue to run until it finishes	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (isClient)
                {
                    ClientHandler.sendDisconnect();
                    ClientHandler.disconnect();
                }
                else
                {
                    ServerHandler.sendDisconnect();
                    ServerHandler.disconnect();
                }
				myWins = 0;
				opponentWins = 0;
				new MenuWindow();
			}
		});
		
		init();
		start();
	}
	
	@Override
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
		if (lilWindaRow != 999) {
			board[lilWindaRow][lilWindaColumn].drawLilWinda(g, mousex, mousey);
		}
		g.setColor(Color.white);
		g.setFont(new Font("Arial Bold", Font.PLAIN, 14));
		g.drawString(currentHint, 15, 725);
		
		g.drawString("Your Score: " + myScore, 40, 50);
		g.drawString("Your Wins: " + myWins, 40, 45);
		g.drawString("Opponent Wins: " + opponentWins, 460, 45);
		g.drawString("Opponent Score: " + opponentScore, 430, 50);
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
				g.drawString("New game begins in " + (gameDelayTimer / 25) + 1 + " seconds", 160, 390);
				g.drawString("Press ESC to disconnect to the main menu", 90, 410);
				
				if (fadeOut < 230)
					fadeOut += 5;
				
				if (gameDelayTimer > 0) {
					gameDelayTimer--;
					if (gameDelayTimer == 74 || gameDelayTimer == 50 || gameDelayTimer == 25) {
						tick = new Sound("tick.wav");
					}
				}
				else
					reset();
		}
		
		if (winner == EnumWinner.None) {
			if (fadeOut > 0)
				fadeOut -= 5;
		}

		gOld.drawImage(image, 0, 0, null);
	}
	
	@Override
	public void reset() {
		super.reset();
		myTurn = isClient;
	}

	public static void chooseWinner() {
		if (myScore > opponentScore) {
			winner = EnumWinner.PlayerOne;
			myWins++;
		}
		else if (opponentScore > myScore) {
			winner = EnumWinner.PlayerTwo;
			opponentWins++;
		}
		else if (opponentScore == myScore) {
			winner = EnumWinner.Tie;
		}
		
		gameDelayTimer = 75;
	}
	
	public static void initMovePiece() {
		if (isClient) {
			ClientHandler.sendPieceMove(initRow, initCol, movedRow, movedCol, myScore);
		} else {
			ServerHandler.sendPieceMove(initRow, initCol, movedRow, movedCol, myScore);
		}
		movePieceToLocation(new OrderedPair(initRow, initCol), new OrderedPair(movedRow, movedCol));
		
		selectedRow = 999;
		selectedColumn = 999;
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
		//"my" pieces are black if I'm a client
		if (location.getX() >= 5 && isClient) {
			myScore += board[location.getX()][location.getY()].getValue();
			numBlackPiecesOnBoard--;
		}
		//"my" pieces are white if I'm a server
		if (location.getX() < 2 && !isClient) {
			myScore += board[location.getX()][location.getY()].getValue();
			numWhitePiecesOnBoard--;
		}
		numPiecesOnBoard--;
		board[location.getX()][location.getY()] = null;
	}

	public static void updateTurn() {
		myTurn = !myTurn;
		//Update the turn after both players have gone
		if (!isClient)
			turn++;
	}
}
