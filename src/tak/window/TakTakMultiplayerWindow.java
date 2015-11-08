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
import tak.net.ClientHandler;
import tak.net.ServerHandler;
import tak.util.OrderedPair;
import tak.util.ScoreFader;
import tak.util.Sound;

public class TakTakMultiplayerWindow extends JFrame implements Runnable {

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
	
	//FULL_WIDTH allows room for the chat area
	static public final int FULL_WIDTH = 840;

	public static Random rand = new Random();
	private static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

	private static final int CENTER_X = (SCREEN_WIDTH / 2) - (FULL_WIDTH / 2);
	private static final int CENTER_Y = (SCREEN_HEIGHT / 2) - (WINDOW_HEIGHT / 2);

	private final TakTakMultiplayerWindow frame = this;
	private final NetworkWindow controller;

	//The client player will always go first and play as black.

	//Network variables
	public static boolean isClient;
	public static int initRow;
	public static int initCol;
	public static int movedRow;
	public static int movedCol;

	public static int myScore;
	public static Color myColor;
	public static int myWins;
	public static int opponentScore;
	public static int opponentWins;

	public static int gameDelayTimer;

	public static int turn;
	public static boolean myTurn;

	public static int selectedRow = 999;
	public static int selectedColumn = 999;
	public static int lilWindaRow = 999;
	public static int lilWindaColumn = 999;
	public static int mousex;
	public static int mousey;

	public static final int COLUMNS = 6;
	public static final int ROWS = 7;
	public static Piece[][] board;

	//This will hold every chat message sent
	public static ArrayList<String> chat = new ArrayList<String>();

	//The text the user is currently typing
	public static String currentChatText = "Chat with your opponent!";

	static ImageIcon icon = new ImageIcon(TakTakSingleplayerWindow.class.getResource("/tak/assets/icon.png"));
	static ImageIcon background = new ImageIcon(TakTakSingleplayerWindow.class.getResource("/tak/assets/wood.png"));

	private static ImageIcon hoverButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_hover.png"));
	private static ImageIcon button = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button.png"));

	public static ArrayList<OrderedPair> validMoves = new ArrayList<OrderedPair>();
	public static ArrayList<ScoreFader> faders = new ArrayList<ScoreFader>();

	static Sound tick;
	static boolean singleplayer = false;

	public static int numBlackPiecesOnBoard;
	public static int numWhitePiecesOnBoard;

	public static int tipTime;
	public static String HINT_PREFIX = "Tip: ";
	public static String currentHint = "";
	public static String[] HINTS = TakTakSingleplayerWindow.HINTS;

	static Sound move;
	static Sound cha_ching;

	private boolean mouseoverReturn;

	public static enum EnumWinner {
		PlayerOne, PlayerTwo, PlayerAI, Tie, None
	}

	public static EnumWinner winner;

	public static int fadeOut;

	public TakTakMultiplayerWindow(NetworkWindow _controller) {

		controller = _controller;

		setSize(FULL_WIDTH, WINDOW_HEIGHT);
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
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (fadeOut < 150)
					return;
				int xpos = e.getX();
				int ypos = e.getY() + 2;

				if (xpos >= 225 && xpos <= 365 && ypos >= 450 && ypos <= 490)
					mouseoverReturn = true;
				else
					mouseoverReturn = false;

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
						if (board[currentRow][currentColumn].getTopPiece().getBackgroundColor() == myColor) {
							selectedRow = currentRow;
							selectedColumn = currentColumn;
						}
					} else if (selectedRow != 999) {
						boolean movedPiece = false;
						for (int i = 0; i < validMoves.size() && !movedPiece; i++) {
							if (validMoves.get(i).toString()
									.equals(new OrderedPair(currentRow, currentColumn).toString())) {
								movedPiece = true;
								validMoves.clear();
								initRow = selectedRow;
								initCol = selectedColumn;
								movedRow = currentRow;
								movedCol = currentColumn;
								if (isClient) {
									ClientHandler.sendPieceMove(initRow, initCol, movedRow, movedCol);
								} else {
									ServerHandler.sendPieceMove(initRow, initCol, movedRow, movedCol);
								}
								selectedRow = 999;
								selectedColumn = 999;
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
					}
				}
				repaint();
			}
		});

		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if (KeyEvent.VK_BACK_SPACE == e.getKeyCode()) {
					selectedRow = 999;
					selectedColumn = 999;
					validMoves.clear();
				}

				repaint();
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (fadeOut < 150)
					return;
				if (MouseEvent.BUTTON1 == e.getButton() && mouseoverReturn) {
					if (isClient) {
						ClientHandler.sendDisconnect();
						ClientHandler.disconnect();
					} else {
						ServerHandler.sendDisconnect();
						ServerHandler.disconnect();
					}
					myWins = 0;
					opponentWins = 0;
					controller.dispose();
					frame.dispose();
				}
			}
		});

		//Writes your keystrokes to the chat thing
		//This handles all keyboard input, and filters out any button press we shouldn't add to chat
		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				//In this if statement, there should be every key we don't want to add text
				//to the current chat with

				if (e.getKeyCode() != KeyEvent.VK_ESCAPE && e.getKeyCode() != KeyEvent.VK_CAPS_LOCK
						&& e.getKeyCode() != KeyEvent.VK_SPACE && e.getKeyCode() != KeyEvent.VK_BACK_SPACE
						&& e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_ALT
						&& e.getKeyCode() != KeyEvent.VK_CONTROL && e.getKeyCode() != KeyEvent.VK_CONTEXT_MENU
						&& e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN
						&& e.getKeyCode() != KeyEvent.VK_RIGHT && e.getKeyCode() != KeyEvent.VK_LEFT
						&& e.getKeyCode() != KeyEvent.VK_SHIFT && e.getKeyCode() != KeyEvent.VK_TAB
						&& e.getKeyCode() != KeyEvent.VK_QUOTE && e.getKeyCode() != KeyEvent.VK_QUOTEDBL
						&& e.getKeyCode() != KeyEvent.VK_WINDOWS && e.getKeyCode() != KeyEvent.VK_F1
						&& e.getKeyCode() != KeyEvent.VK_F2 && e.getKeyCode() != KeyEvent.VK_F3
						&& e.getKeyCode() != KeyEvent.VK_F4 && e.getKeyCode() != KeyEvent.VK_F5
						&& e.getKeyCode() != KeyEvent.VK_F6 && e.getKeyCode() != KeyEvent.VK_F7
						&& e.getKeyCode() != KeyEvent.VK_F8 && e.getKeyCode() != KeyEvent.VK_F9
						&& e.getKeyCode() != KeyEvent.VK_F10 && e.getKeyCode() != KeyEvent.VK_F11
						&& e.getKeyCode() != KeyEvent.VK_F12 && e.getKeyCode() != KeyEvent.VK_COMMA
						&& e.getKeyCode() != KeyEvent.VK_PERIOD && e.getKeyCode() != KeyEvent.VK_SLASH
						&& e.getKeyCode() != KeyEvent.VK_BACK_SLASH) {
					if (currentChatText.equals("Chat with your opponent!"))
						currentChatText = "";
					currentChatText += KeyEvent.getKeyText(e.getKeyCode());
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					currentChatText += " ";
				} else if (e.getKeyCode() == KeyEvent.VK_COMMA) {
					currentChatText += ",";
				} else if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
					currentChatText += ".";
				} else if (e.getKeyCode() == KeyEvent.VK_SLASH) {
					currentChatText += "/";
				} else if (e.getKeyCode() == KeyEvent.VK_BACK_SLASH) {
					currentChatText += "\\";
				} else if (e.getKeyCode() == KeyEvent.VK_QUOTE) {
					currentChatText += "'";
				} else if (e.getKeyCode() == KeyEvent.VK_QUOTEDBL) {
					currentChatText += "\"";
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER && !currentChatText.equals("Chat with your opponent!")) {
					String finalString = (isClient ? "P1:" : "P2:")
							+ currentChatText.replaceAll("null", "").toLowerCase();

					if (isClient) {
						ClientHandler.sendChat(finalString);
					} else {
						ServerHandler.sendChat(finalString);
					}
					chat.add(finalString);
					currentChatText = "Chat with your opponent!";
				} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if (currentChatText.equals("Chat with your opponent!"))
						currentChatText = "";
					else if (currentChatText.length() >= 1)
						currentChatText = currentChatText.substring(0, currentChatText.length() - 1);
				}
			}
		});

		//Send the user back to the menu screen to make sure the entire system gets exited
		//Without this the sound will continue to run until it finishes	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (isClient) {
					ClientHandler.sendDisconnect();
					ClientHandler.disconnect();
				} else {
					ServerHandler.sendDisconnect();
					ServerHandler.disconnect();
				}
				myWins = 0;
				opponentWins = 0;
			}
		});

		init();
		start();
	}

	public void init() {
		requestFocus();
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

		g.drawString("Your Score: " + myScore, 40, 60);
		g.drawString("Your Wins: " + myWins, 40, 45);
		g.drawString("Opponent Wins: " + opponentWins, 430, 45);
		g.drawString("Opponent Score: " + opponentScore, 430, 60);
		g.setFont(new Font("Arial Bold", Font.BOLD, 18));
		g.drawString((myTurn ? "YOUR (" + (myColor == Color.black ? "BLACK" : "WHITE") + ")" : "OPPONENT'S") + " Turn",
				myTurn ? 205 : 210, 55);
		g.setFont(new Font("Arial Bold", Font.BOLD, 14));
		g.drawString("Turn #" + (turn + 1), 270, 40);

		//Chat area background
		g.setColor(new Color(20, 20, 20));
		g.fillRect(590, 65, 230, 595);
		g.fillRect(590, 665, 230, 38);
		g.setColor(new Color(0, 0, 0));
		g.fillRect(595, 70, 220, 585);
		g.fillRect(595, 670, 220, 28);

		//The current text is displayed
		int stringWrap = 25;
		g.setColor(Color.white);
		g.setFont(new Font("Arial Bold", Font.BOLD, 14));

		if (currentChatText != null) {
			String finalText = currentChatText.replaceAll("null", "").toLowerCase();
			g.drawString(
					finalText.length() > stringWrap ? finalText.substring(finalText.length() - stringWrap) : finalText,
					598, 690);
		}

		//The most recent chat is displayed
		int chatBoardLength = 22;
		g.setColor(Color.white);

		if (chat.size() <= chatBoardLength) {
			int chatY = 0;
			for (String msg : chat) {
				g.setColor((msg.startsWith("P1") ? Color.cyan : Color.green));
				g.drawString(msg.substring(0, 3), 600, 100 + chatY);
				g.setColor(Color.white);
				g.drawString("      " + msg.substring(3), 600, 100 + chatY);
				chatY += 25;
			}
		} else if (chat.size() > chatBoardLength) {
			int chatY = 0;
			ArrayList<String> chatToShow = new ArrayList<String>();

			//Start at the index where we begin showing the chat
			for (int i = chat.size() - 1 - chatBoardLength; i < chat.size(); i++) {
				chatToShow.add(chat.get(i));
			}

			for (String msg : chatToShow) {
				g.setColor((msg.startsWith("P1") ? Color.cyan : msg.startsWith("GAME") ? Color.orange : Color.green));
				g.drawString(msg.substring(0, 3), 600, 100 + chatY);
				g.setColor(Color.white);
				g.drawString("      " + msg.substring(3), 600, 100 + chatY);
				chatY += 25;
			}
		}

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
			g.drawString("New game begins in " + ((gameDelayTimer / 25) + 1) + " seconds", 160, 390);
			//g.drawString("Press ESC to disconnect to the main menu", 90, 410);

			if (fadeOut < 230)
				fadeOut += 5;

			if (mouseoverReturn)
				g.drawImage(hoverButton.getImage(), 225, 450, null);
			else
				g.drawImage(button.getImage(), 225, 450, null);

			g.setFont(new Font("Arial", Font.BOLD, 16));
			g.setColor(mouseoverReturn ? Color.red : Color.black);
			g.drawString("Return to Menu", 287, 425);

			if (gameDelayTimer > 0) {
				gameDelayTimer--;
				if (gameDelayTimer % 25 == 0) {
					tick = new Sound("tick.wav");
				}
			} else if (gameDelayTimer == 0)
				reset();
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

	public void reset() {
		board = new Piece[ROWS][COLUMNS];
		resetBoard();

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
		opponentScore = 0;

		winner = EnumWinner.None;
		myTurn = isClient;
		myColor = (isClient ? Color.black : Color.white);
	}

	public static void chooseWinner() {
		if (myScore > opponentScore) {
			winner = EnumWinner.PlayerOne;
			myWins++;
		} else if (opponentScore > myScore) {
			winner = EnumWinner.PlayerTwo;
			opponentWins++;
		} else if (opponentScore == myScore) {
			winner = EnumWinner.Tie;
		}

		//25 is one second
		gameDelayTimer = 150;
	}

	//Multiplayer boards just can't be random, the amount of data that would
	//have to be sent is way too much and the fact that we'd have to have another
	//board generated and stored away isn't good.
	public void resetBoard() {
		board[0][0] = new Piece(10, Color.orange, Color.black);
		board[0][4] = new Piece(20, Color.orange, Color.black);
		board[1][2] = new Piece(30, Color.orange, Color.black);
		board[1][5] = new Piece(40, Color.orange, Color.black);

		board[1][3] = new Piece(10, Color.blue, Color.black);
		board[1][0] = new Piece(20, Color.blue, Color.black);
		board[0][1] = new Piece(30, Color.blue, Color.black);
		board[0][5] = new Piece(40, Color.blue, Color.black);

		board[0][2] = new Piece(10, Color.green, Color.black);
		board[1][1] = new Piece(20, Color.green, Color.black);
		board[0][3] = new Piece(30, Color.green, Color.black);

		Piece p = new Piece(50, Color.green, Color.black);
		p.setKing(true);
		board[1][4] = p;

		board[5][0] = new Piece(10, Color.orange, Color.white);
		board[5][4] = new Piece(20, Color.orange, Color.white);
		board[6][2] = new Piece(30, Color.orange, Color.white);
		board[6][5] = new Piece(40, Color.orange, Color.white);

		board[6][3] = new Piece(10, Color.blue, Color.white);
		board[6][0] = new Piece(20, Color.blue, Color.white);
		board[5][1] = new Piece(30, Color.blue, Color.white);
		board[5][5] = new Piece(40, Color.blue, Color.white);

		board[5][2] = new Piece(10, Color.green, Color.white);
		board[6][1] = new Piece(20, Color.green, Color.white);
		board[5][3] = new Piece(30, Color.green, Color.white);

		Piece p2 = new Piece(50, Color.green, Color.white);
		p2.setKing(true);
		board[6][4] = p2;
	}

	//TODO - issues with scoring
	public static void movePieceToLocation(OrderedPair piece, OrderedPair location) {

		//This works just fine
		if (board[location.getX()][location.getY()] != null) {

			if (board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black)
				numBlackPiecesOnBoard--;
			else
				numWhitePiecesOnBoard--;

			board[location.getX()][location.getY()].addStackToStack(board[piece.getX()][piece.getY()].getWholeStack());
			board[piece.getX()][piece.getY()] = null;
		} else {
			board[location.getX()][location.getY()] = board[piece.getX()][piece.getY()];

			board[piece.getX()][piece.getY()] = null;
		}

		System.out.println("White: " + numWhitePiecesOnBoard + " | Black: " + numBlackPiecesOnBoard);

		//Pieces are in opponent's safe zone
		//"my" pieces are black if I'm a client
		if (location.getX() >= 5 || location.getX() < 2) {
			if (location.getX() >= 5
					&& board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black) {
				if (isClient)
					myScore += board[location.getX()][location.getY()].getValue();
				else
					opponentScore += board[location.getX()][location.getY()].getValue();

				numBlackPiecesOnBoard--;
			}
			//"my" pieces are white if I'm a server
			else if (location.getX() < 2
					&& board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.white) {
				if (!isClient)
					myScore += board[location.getX()][location.getY()].getValue();
				else
					opponentScore += board[location.getX()][location.getY()].getValue();

				numWhitePiecesOnBoard--;
			}

			Color c;
			if (location.getX() >= 5
					&& board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black
					&& isClient
					|| location.getX() < 2
							&& board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.white
							&& !isClient)
				c = new Color(64, 180, 64);
			else
				c = new Color(128, 64, 64);

			if (location.getX() >= 5
					&& board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black
					|| location.getX() < 2 && board[location.getX()][location.getY()].getTopPiece()
							.getBackgroundColor() == Color.white) {
				ScoreFader sf = new ScoreFader(board[location.getX()][location.getY()].getValue(),
						getX(0) + location.getY() * getWidth2() / COLUMNS,
						getY(0) + location.getX() * getHeight2() / ROWS, c);

				board[location.getX()][location.getY()] = null;
			}
			move = new Sound("chaching.wav");
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

		if (tipTime < 200) {
			tipTime++;
		} else {
			tipTime = 0;
			currentHint = HINT_PREFIX + HINTS[rand.nextInt(HINTS.length)];
		}

		//If at any point we disconnect, dispose
		if (isClient) {
			if (!ClientHandler.isConnected())
				dispose();
		} else {
			if (!ServerHandler.isConnected())
				dispose();
		}

		if ((numWhitePiecesOnBoard == 0 || numBlackPiecesOnBoard == 0) && winner == EnumWinner.None) {
			//If one side doesn't have any pieces left
			//Score all remaining pieces
			for (int zRow = 0; zRow < ROWS; zRow++) {
				for (int zColumn = 0; zColumn < COLUMNS; zColumn++) {
					if (board[zRow][zColumn] != null) {
						if (board[zRow][zColumn].getTopPiece().getBackgroundColor() != myColor) {
							opponentScore += board[zRow][zColumn].getValue();
							ScoreFader sf2 = new ScoreFader(board[zRow][zColumn].getValue(),
									getX(0) + zColumn * getWidth2() / COLUMNS, getY(0) + zRow * getHeight2() / ROWS,
									new Color(128, 64, 64));
						} else {
							myScore += board[zRow][zColumn].getValue();
							ScoreFader sf2 = new ScoreFader(board[zRow][zColumn].getValue(),
									getX(0) + zColumn * getWidth2() / COLUMNS, getY(0) + zRow * getHeight2() / ROWS,
									new Color(64, 180, 64));
						}
						board[zRow][zColumn] = null;
					}
				}
			}
			//End the game
			chooseWinner();
		}
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

	Thread relaxer;

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
		return (WINDOW_WIDTH - getX(0) - XBORDER);
	}

	public static int getHeight2() {
		return (WINDOW_HEIGHT - getY(0) - YBORDER);
	}

	public void start() {
		if (relaxer == null) {
			relaxer = new Thread(this);
			relaxer.start();
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

		if (pieceDirection == 1) {
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

		if (pieceDirection == 0) {
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
				if (board[row][column].getTopPiece().getValue() == _piece.getTopPiece().getValue() || board[row][column]
						.getTopPiece().getForegroundColor() == _piece.getTopPiece().getForegroundColor()) {
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
}
