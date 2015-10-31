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
							if (validMoves.get(i).toString()
									.equals(new OrderedPair(currentRow, currentColumn).toString())) {
								movePieceToLocation(new OrderedPair(selectedRow, selectedColumn),
										new OrderedPair(currentRow, currentColumn));
								movedPiece = true;
								selectedRow = 999;
								selectedColumn = 999;
								validMoves.clear();
								initRow = selectedRow;
								initCol = selectedColumn;
								movedRow = currentRow;
								movedCol = currentColumn;
								if (isClient) {
									ClientHandler.sendPieceMove(frame, initRow, initCol, movedRow, movedCol);
								} else {
									ServerHandler.sendPieceMove(frame, initRow, initCol, movedRow, movedCol);
								}
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

		gOld.drawImage(image, 0, 0, null);
	}
	
	@Override
	public void reset() {
		super.reset();
		myTurn = isClient;
	}

	@Override
	public void movePieceToLocation(OrderedPair piece, OrderedPair location) {

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
		//TODO - add client stuff here
//		if (location.getX() >= 5 && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black ||
//			location.getX() < 2 && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.white) {
//			if (board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black) {
//				aiScore += board[location.getX()][location.getY()].getValue();
//			} else {
//				myScore += board[location.getX()][location.getY()].getValue();
//			}
//			board[location.getX()][location.getY()] = null;
//			
//			//TODO - display some text that says "+ #of points" that fades out after a couple seconds
//		}
	}

	public static void updateTurn() {
		myTurn = !myTurn;
		//Update the turn after both players have gone
		if (!isClient)
			turn++;
	}
}
