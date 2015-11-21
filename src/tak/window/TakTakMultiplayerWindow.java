package tak.window;

import java.awt.AlphaComposite;
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
import tak.com.TakTakMain;
import tak.config.ConfigLoader;
import tak.net.ClientHandler;
import tak.net.ServerHandler;
import tak.ui.ScoreFader;
import tak.ui.TurnIndicator;
import tak.util.OrderedPair;
import tak.util.Sound;
import tak.window.TakTakSingleplayerWindow.EnumWinner;

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
    static public final int FULL_HEIGHT = 765;
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
    public int arrowLoc;
    public int arrowAnim;
    public static int myScore;
    public static Color myColor;
    public static int myWins;
    public static int opponentScore;
    public static int opponentWins;
    public int gameDelayTimer;
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
    private static ImageIcon arrow = new ImageIcon(MenuWindow.class.getResource("/tak/assets/greenarrow.png"));
    private static ImageIcon hoverButton = new ImageIcon(
            TakTakSingleplayerWindow.class.getResource("/tak/assets/button_hover.png"));
    private static ImageIcon button = new ImageIcon(
            TakTakSingleplayerWindow.class.getResource("/tak/assets/button.png"));
    private static ImageIcon smallHoverButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_small_hover.png"));
    private static ImageIcon smallButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_small.png"));
    private static ImageIcon muted = new ImageIcon(MenuWindow.class.getResource("/tak/assets/muted.png"));
    private static ImageIcon notMuted = new ImageIcon(MenuWindow.class.getResource("/tak/assets/notmuted.png"));
    public static ArrayList<OrderedPair> validMoves = new ArrayList<OrderedPair>();
    public static ArrayList<ScoreFader> faders = new ArrayList<ScoreFader>();
    public static TurnIndicator turnIndicator = null;
    static Sound tick;
    static boolean singleplayer = false;
    public static int numBlackPiecesOnBoard;
    public static int numWhitePiecesOnBoard;
    public int tipTime;
    public static String HINT_PREFIX = "Tip: ";
    public static String currentHint = "";
    public static String[] HINTS = TakTakSingleplayerWindow.HINTS;
    static Sound move;
    static Sound cha_ching;
    private boolean mouseoverReturn;
    private boolean mouseoverHelp;
    private boolean mouseoverQuit;
    private boolean tellMeWhenItsMyTurn = true;
    private boolean mouseoverConfig;
    private boolean mouseoverMute;
    public static boolean isWindowOpen;

    public static enum EnumWinner {

        PlayerOne, PlayerTwo, PlayerAI, Tie, None
    }
    public static EnumWinner winner;
    public int fadeOut;

    public TakTakMultiplayerWindow(NetworkWindow _controller) {
        isWindowOpen = true;

        controller = _controller;

        setSize(FULL_WIDTH, FULL_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setTitle("Tak-Tak");
        setLocation(CENTER_X, CENTER_Y);
        setIconImage(icon.getImage());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isWindowOpen = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                lilWindaRow = 999;
                lilWindaColumn = 999;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int xpos = e.getX();
                int ypos = e.getY() + 2;

                if (xpos >= 225 && xpos <= 365 && ypos >= 450 && ypos <= 490 && winner != EnumWinner.None) {
                    mouseoverReturn = true;
                } else {
                    mouseoverReturn = false;
                }

                if (xpos >= 650 && xpos <= 790 && ypos >= 725 && ypos <= 760) {
                    mouseoverHelp = true;
                } else {
                    mouseoverHelp = false;
                }
                if (xpos >= 500 && xpos <= 640 && ypos >= 725 && ypos <= 760) {
                    mouseoverQuit = true;
                } else {
                    mouseoverQuit = false;
                }
                if (xpos >= 350 && xpos <= 490 && ypos >= 725 && ypos <= 760) {
                    mouseoverConfig = true;
                } else {
                    mouseoverConfig = false;
                }
                if (xpos >= 795 && xpos <= 830 && ypos >= 725 && ypos <= 760) {
                    mouseoverMute = true;
                } else {
                    mouseoverMute = false;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (MouseEvent.BUTTON1 == e.getButton() && myTurn) {

                    int xpos = e.getX() - getX(0);
                    int ypos = e.getY() - getY(0);
                    if (xpos < 0 || ypos < 0 || xpos > getWidth2() || ypos > getHeight2()) {
                        return;
                    }

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
                    
                    if (validMoves.isEmpty()) {
                		selectedRow = 999;
                		selectedColumn = 999;
                	}

                    //If the place we're clicking on isn't empty
                    //and we have no piece selected
                    if (board[currentRow][currentColumn] != null && selectedRow == 999) {
                        if (board[currentRow][currentColumn].getTopPiece().getBackgroundColor() == myColor) {
                            selectedRow = currentRow;
                            selectedColumn = currentColumn;
                            arrowLoc = 0;
                            arrowAnim = 0;
                        }
                    //If we already have a piece selected
                    } else if (selectedRow != 999) {

                    	int moveIndex = 0;
                    	
                    	//Let's loop through the 'available' moves and see if where
                    	//we clicked is one of them
                    	while (moveIndex < validMoves.size()) {
                    		if (isMoveInArray(new OrderedPair(currentRow, currentColumn))) {
                    			
                    			//Set all network values
                    			initRow = selectedRow;
                    			initCol = selectedColumn;
                    			movedRow = currentRow;
                    			movedCol = currentColumn;
                    			
                    			//Send the data
                    			if (isClient) {
                    				ClientHandler.sendPieceMove(initRow, initCol, movedRow, movedCol);
                    			} else {
                    				ServerHandler.sendPieceMove(initRow, initCol, movedRow, movedCol);
                    			}
                    			
                    			//Move the piece
                    			movePieceToLocation(new OrderedPair(selectedRow, selectedColumn),
                                new OrderedPair(currentRow, currentColumn));
                    			
                    			//Reset the state after the move is made
                    			selectedRow = 999;
                    			selectedColumn = 999;
                    			validMoves.clear();
                                myTurn = !myTurn;
                                break;
                    			
                    		//If the piece CANT move here and the place is empty or not my color
                    		} else if (board[currentRow][currentColumn] == null || 
                    				   board[currentRow][currentColumn] != null &&
                    				   board[currentRow][currentColumn].getTopPiece().getBackgroundColor()
                    				   != myColor) {
                    			selectedRow = 999;
                    			selectedColumn = 999;
                    			validMoves.clear();
                    			break;
                    		//If the piece CANT move here and it's another one of my pieces
                    		} else if (board[currentRow][currentColumn] != null && board[currentRow][currentColumn]
                                       .getTopPiece().getBackgroundColor() == myColor) {
                    			selectedRow = currentRow;
                                selectedColumn = currentColumn;
                                arrowLoc = 0;
                                arrowAnim = 0;
                                validMoves.clear();
                                break;
                    		}
                    		moveIndex++;
                    	}
                    }
                }
                if (MouseEvent.BUTTON3 == e.getButton()) {

                    int xpos = e.getX() - getX(0);
                    int ypos = e.getY() - getY(0);
                    if (xpos < 0 || ypos < 0 || xpos > getWidth2() || ypos > getHeight2()) {
                        return;
                    }

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

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverReturn && fadeOut >= 150) {
                    if (isClient) {
                        ClientHandler.sendDisconnect();
                        ClientHandler.disconnect();
                    } else {
                        ServerHandler.sendDisconnect();
                        ServerHandler.disconnect();
                    }
                    myWins = 0;
                    opponentWins = 0;
                    reset();
                    frame.dispose();
                }
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverHelp) {
                    final RulesWindow w = new RulesWindow();
                }
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverQuit) {
                    if (isClient) {
                        ClientHandler.sendDisconnect();
                        ClientHandler.disconnect();
                    } else {
                        ServerHandler.sendDisconnect();
                        ServerHandler.disconnect();
                    }
                    myWins = 0;
                    opponentWins = 0;
                    reset();
                    frame.dispose();
                }
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverConfig) {
                    tellMeWhenItsMyTurn = !tellMeWhenItsMyTurn;
                }
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverMute) {
                    TakTakMain.muted = !TakTakMain.muted;
                }
            }
        });

        //Writes your keystrokes to the chat thing
        //This handles all keyboard input we want to add to the chat
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                //In this if statement, there should be every key we don't want to add text
                //to the current chat with

                //It has to be done this way
                if (e.getKeyCode() == KeyEvent.VK_0) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += ")";
                    } else {
                        currentChatText += "0";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_1) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "!";
                    } else {
                        currentChatText += "1";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_2) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "@";
                    } else {
                        currentChatText += "2";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_3) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "$";
                    } else {
                        currentChatText += "3";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_4) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "5";
                    } else {
                        currentChatText += "4";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_5) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "%";
                    } else {
                        currentChatText += "5";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_6) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "^";
                    } else {
                        currentChatText += "6";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_7) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "&";
                    } else {
                        currentChatText += "7";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_8) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "*";
                    } else {
                        currentChatText += "8";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_9) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "(";
                    } else {
                        currentChatText += "9";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "A";
                    } else {
                        currentChatText += "a";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_B) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "B";
                    } else {
                        currentChatText += "b";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_C) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "C";
                    } else {
                        currentChatText += "c";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "D";
                    } else {
                        currentChatText += "d";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_E) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "E";
                    } else {
                        currentChatText += "e";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_F) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "F";
                    } else {
                        currentChatText += "f";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_G) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "G";
                    } else {
                        currentChatText += "g";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_H) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "H";
                    } else {
                        currentChatText += "h";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_I) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "I";
                    } else {
                        currentChatText += "i";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_J) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "J";
                    } else {
                        currentChatText += "j";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_K) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "K";
                    } else {
                        currentChatText += "k";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_L) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "L";
                    } else {
                        currentChatText += "l";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_M) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "M";
                    } else {
                        currentChatText += "m";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_N) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "N";
                    } else {
                        currentChatText += "n";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_O) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "O";
                    } else {
                        currentChatText += "o";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "P";
                    } else {
                        currentChatText += "p";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_Q) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "Q";
                    } else {
                        currentChatText += "q";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "R";
                    } else {
                        currentChatText += "r";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "S";
                    } else {
                        currentChatText += "s";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_T) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "T";
                    } else {
                        currentChatText += "t";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_U) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "U";
                    } else {
                        currentChatText += "u";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_V) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "V";
                    } else {
                        currentChatText += "v";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "W";
                    } else {
                        currentChatText += "w";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_X) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "X";
                    } else {
                        currentChatText += "x";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_Y) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "Y";
                    } else {
                        currentChatText += "y";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_Z) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "Z";
                    } else {
                        currentChatText += "z";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_COMMA) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "<";
                    } else {
                        currentChatText += ",";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += ">";
                    } else {
                        currentChatText += ".";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_SEMICOLON) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += ":";
                    } else {
                        currentChatText += ";";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_QUOTE) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "\"";
                    } else {
                        currentChatText += "'";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_SLASH) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "?";
                    } else {
                        currentChatText += "/";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_BRACELEFT) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "{";
                    } else {
                        currentChatText += "[";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_BRACERIGHT) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "}";
                    } else {
                        currentChatText += "]";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SLASH) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "|";
                    } else {
                        currentChatText += "\\";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UNDERSCORE) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "_";
                    } else {
                        currentChatText += "-";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    if (e.isShiftDown()) {
                        currentChatText += "+";
                    } else {
                        currentChatText += "=";
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    }
                    currentChatText += " ";
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (currentChatText.equals("Chat with your opponent!")) {
                        currentChatText = "";
                    } else if (currentChatText.length() >= 1) {
                        currentChatText = currentChatText.substring(0, currentChatText.length() - 1);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER && !currentChatText.equals("Chat with your opponent!")) {
                    String finalString = (isClient ? "P1:" : "P2:") + currentChatText.replaceAll("null", "");

                    //Break it up into lines here

                    if (isClient) {
                        ClientHandler.sendChat(finalString);
                    } else {
                        ServerHandler.sendChat(finalString);
                    }
                    chat.add(finalString);
                    currentChatText = "Chat with your opponent!";
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
                    controller.gameStarted = false;
                    controller.theGame = null;
                } else {
                    ServerHandler.sendDisconnect();
                    ServerHandler.disconnect();
                    controller.gameStarted = false;
                    controller.theGame = null;
                }
                myWins = 0;
                opponentWins = 0;
                reset();
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

        for (int x = 0; x < FULL_WIDTH; x += background.getIconWidth()) {
            for (int y = 0; y < FULL_HEIGHT; y += background.getIconHeight()) {
                g.drawImage(background.getImage(), x, y, null);
            }
        }

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
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
        
        //Checkerboard look
        for (int zRow = 0; zRow < ROWS; zRow++) {
            for (int zColumn = 0; zColumn < COLUMNS; zColumn++) {
            	if (zRow % 2 == 0) {
            		if (zColumn % 2 == 1) {
            		g.setColor(new Color(0, 0, 0, 35));
            		g.fillRect(getX(0) + zColumn * getWidth2() / COLUMNS,
                            getY(0) + zRow * getHeight2() / ROWS, getWidth2() / COLUMNS,
                            getHeight2() / ROWS);
            		}
            	}
            	if (zRow % 2 == 1) {
            		if (zColumn % 2 == 0) {
            		g.setColor(new Color(0, 0, 0, 35));
            		g.fillRect(getX(0) + zColumn * getWidth2() / COLUMNS,
                            getY(0) + zRow * getHeight2() / ROWS, getWidth2() / COLUMNS,
                            getHeight2() / ROWS);
            		}
            	}
            }
        }

        for (int zRow = 0; zRow < ROWS; zRow++) {
            for (int zColumn = 0; zColumn < COLUMNS; zColumn++) {
                if (board[zRow][zColumn] != null) {
                    board[zRow][zColumn].update();
                    board[zRow][zColumn].draw(g, getX(0) + zColumn * getWidth2() / COLUMNS,
                            getY(0) + zRow * getHeight2() / ROWS);
                }
            }
        }

        if (selectedRow != 999) {
            displayAllValidMoves(g, selectedRow, selectedColumn, validMoves.isEmpty());
        }
        if (lilWindaRow != 999 && board[lilWindaRow][lilWindaColumn] != null) {
            board[lilWindaRow][lilWindaColumn].drawLilWinda(g, mousex, mousey);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial Bold", Font.PLAIN, 14));
        g.drawString(currentHint, 15, 719);

        g.drawString("Your Score: " + myScore, 40, 60);
        g.drawString("Your Wins: " + myWins, 40, 45);
        g.drawString("Opponent Wins: " + opponentWins, 430, 45);
        g.drawString("Opponent Score: " + opponentScore, 430, 60);
        g.setFont(new Font("Arial Bold", Font.BOLD, 18));
        g.drawString((myTurn ? "YOUR (" + (myColor == Color.black ? "BLACK" : "WHITE") + ")" : "OPPONENT'S") + " Turn",
                myTurn ? 205 : 210, 55);
        g.setFont(new Font("Arial Bold", Font.BOLD, 24));
        g.drawString("CHAT ROOM", 600, 55);
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
        int stringWrap = 33;
        g.setColor(Color.white);
        g.setFont(new Font("Arial Bold", Font.BOLD, 14));

        if (!currentChatText.isEmpty() && currentChatText != "") {
            String finalText = currentChatText.replaceAll("null", "");

            if (!finalText.equals("Chat with your opponent!")) {
                g.drawString(finalText.length() > stringWrap ? finalText.substring(finalText.length() - stringWrap)
                        : finalText, 598, 690);
            }
        } else {
            g.drawString("Chat with your opponent!", 598, 690);
        }

        //The most recent chat is displayed
        int chatBoardLength = 22;
        g.setColor(Color.white);

        if (chat.size() <= chatBoardLength) {
            int chatY = 0;
            for (String msg : chat) {
                boolean flag = msg.length() > 20;

                if (msg.startsWith("P1") || msg.startsWith("P2")) {
                    g.setColor((msg.startsWith("P1") ? Color.cyan : Color.green));
                    g.drawString(msg.substring(0, 3), 600, 100 + chatY);
                    g.setColor(Color.white);
                    g.drawString("      " + (flag ? msg.substring(3, 20) : msg.substring(3)), 600, 100 + chatY);
                } else if (msg.startsWith("GAME")) {
                    g.setColor(Color.ORANGE);
                    g.drawString(msg.substring(0, 5), 600, 100 + chatY);
                    g.setColor(Color.white);
                    g.drawString("           " + (flag ? msg.substring(5, 20) : msg.substring(5)), 600, 100 + chatY);
                }
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
                boolean flag = msg.length() > 20;

                if (msg.startsWith("P1") || msg.startsWith("P2")) {
                    g.setColor((msg.startsWith("P1") ? Color.cyan : Color.green));
                    g.drawString(msg.substring(0, 3), 600, 100 + chatY);
                    g.setColor(Color.white);
                    g.drawString("      " + (flag ? msg.substring(3, 20) : msg.substring(3)), 600, 100 + chatY);
                } else if (msg.startsWith("GAME")) {
                    g.setColor(Color.ORANGE);
                    g.drawString(msg.substring(0, 5), 600, 100 + chatY);
                    g.setColor(Color.white);
                    g.drawString("           " + (flag ? msg.substring(5, 20) : msg.substring(5)), 600, 100 + chatY);
                }
                ;
                chatY += 25;
            }
        }

        g.setColor(new Color(0, 0, 0, fadeOut));
        g.fillRect(0, 0, FULL_WIDTH, FULL_HEIGHT);

        g.setColor(new Color(255, 255, 255, fadeOut));
        g.setFont(new Font("Arial Bold", Font.PLAIN, 36));

        if (mouseoverHelp) {
            g.drawImage(hoverButton.getImage(), 650, 725, null);
        } else {
            g.drawImage(button.getImage(), 650, 725, null);
        }
        if (mouseoverQuit) {
            g.drawImage(hoverButton.getImage(), 500, 725, null);
        } else {
            g.drawImage(button.getImage(), 500, 725, null);
        }

        if (mouseoverMute) {
            g.drawImage(smallHoverButton.getImage(), 795, 725, null);
            g.drawImage((TakTakMain.muted ? muted.getImage() : notMuted.getImage()), 795, 725, null);
        } else {
            g.drawImage(smallButton.getImage(), 795, 725, null);
            g.drawImage((TakTakMain.muted ? muted.getImage() : notMuted.getImage()), 795, 725, null);
        }

        if (tellMeWhenItsMyTurn) {
            g.drawImage(hoverButton.getImage(), 350, 725, null);
        } else {
            g.drawImage(button.getImage(), 350, 725, null);
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(mouseoverHelp ? Color.red : Color.black);
        g.drawString("Help", 704, 749);
        g.setColor(mouseoverQuit ? Color.red : Color.black);
        g.drawString("Quit", 555, 749);

        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(mouseoverConfig ? Color.red : Color.black);
        g.drawString("Turn Indicators: " + (tellMeWhenItsMyTurn ? "On" : "Off"), 364, 747);

        if (winner != EnumWinner.None) {
            g.setColor(new Color(255, 255, 255, fadeOut));
            g.setFont(new Font("Arial Bold", Font.PLAIN, 36));
            if (winner == EnumWinner.PlayerOne) {
                g.drawString("You win!", 220, 300);
            }
            if (winner == EnumWinner.PlayerAI) {
                g.drawString("The AI won...", 190, 300);
            }
            if (winner == EnumWinner.PlayerTwo) {
                g.drawString("The opponent won...", 130, 300);
            }
            if (winner == EnumWinner.Tie) {
                g.drawString("You tied!", 220, 300);
            }

            g.setFont(new Font("Arial Bold", Font.PLAIN, 22));
            g.drawString("New game begins in " + ((gameDelayTimer / 25) + 1) + " seconds", 145, 390);

            if (fadeOut < 230) {
                fadeOut += 5;
            }

            if (mouseoverReturn) {
                g.drawImage(hoverButton.getImage(), 225, 450, null);
            } else {
                g.drawImage(button.getImage(), 225, 450, null);
            }

            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(mouseoverReturn ? Color.red : Color.black);
            g.drawString("Return to Menu", 240, 473);

            g.setColor(Color.white);
            g.drawString("Your score: " + myScore, 235, 540);
            g.drawString("Opponent score: " + opponentScore, 235, 570);

            if (gameDelayTimer > 0) {
                gameDelayTimer--;
                if (gameDelayTimer % 25 == 0) {
                	if (ConfigLoader.sfx)
                		tick = new Sound("sound/tick.wav");
                }
            } else if (gameDelayTimer == 0) {
                reset();
            }
        }

        if (winner == EnumWinner.None) {
            if (fadeOut > 0) {
                fadeOut -= 5;
            }
        }

        if (ConfigLoader.animations) {
	        for (int i = 0; i < faders.size(); i++) {
	            faders.get(i).draw(g);
	        }
	
	        if (turnIndicator != null && tellMeWhenItsMyTurn && winner == EnumWinner.None) {
	            turnIndicator.draw(g);
	        } else if (!tellMeWhenItsMyTurn || winner != EnumWinner.None) {
	            turnIndicator = null;
	        }
        }

        gOld.drawImage(image, 0, 0, null);
    }
    ////////////////////////////////////////////////////////////////////////////

    public void drawArrow(Image image, int xpos, int ypos, double rot, double xscale, double yscale) {
        int width;
        int height;

        width = arrow.getImage().getWidth(this);
        height = arrow.getImage().getHeight(this);

        g.translate(xpos, ypos);
        g.rotate(rot * Math.PI / 180.0);
        g.scale(xscale, yscale);

        g.drawImage(image, -width / 2, -height / 2, width, height, this);

        g.scale(1.0 / xscale, 1.0 / yscale);
        g.rotate(-rot * Math.PI / 180.0);
        g.translate(-xpos, -ypos);
    }
    ////////////////////////////////////////////////////////////////////////////

    public void reset() {
        board = new Piece[ROWS][COLUMNS];
        resetBoard();

        numWhitePiecesOnBoard = 12;
        numBlackPiecesOnBoard = 12;

        tipTime = 0;
        currentHint = HINT_PREFIX + HINTS[rand.nextInt(HINTS.length)];

        currentChatText = "Chat with your opponent!";

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

        arrowLoc = 0;
        arrowAnim = 0;

        chat.add("GAME: Game started!");
    }

    public void chooseWinner() {
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
        chat.add("GAME: Game ended!");
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

    public static void movePieceToLocation(OrderedPair piece, OrderedPair location) {

        if (board[location.getX()][location.getY()] != null) {

            if (board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black) {
                numBlackPiecesOnBoard--;
            } else {
                numWhitePiecesOnBoard--;
            }

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
                if (isClient) {
                	if (ConfigLoader.sfx)
                		move = new Sound("sound/chaching.wav");
                    myScore += board[location.getX()][location.getY()].getValue();
                } else {
                    opponentScore += board[location.getX()][location.getY()].getValue();
                }

                numBlackPiecesOnBoard--;
                chat.add("GAME: Black +" + board[location.getX()][location.getY()].getValue() + "!");
            } //"my" pieces are white if I'm a server
            else if (location.getX() < 2
                    && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.white) {
                if (!isClient) {
                    myScore += board[location.getX()][location.getY()].getValue();
                    if (ConfigLoader.sfx)
                    move = new Sound("sound/chaching.wav");
                } else {
                    opponentScore += board[location.getX()][location.getY()].getValue();
                }

                numWhitePiecesOnBoard--;
                chat.add("GAME: White +" + board[location.getX()][location.getY()].getValue() + "!");
            }

            Color c;
            if (location.getX() >= 5
                    && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black
                    && isClient
                    || location.getX() < 2
                    && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.white
                    && !isClient) {
                c = new Color(64, 180, 64);
            } else {
                c = new Color(128, 64, 64);
            }

            if (location.getX() >= 5
                    && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black
                    || location.getX() < 2 && board[location.getX()][location.getY()].getTopPiece()
                    .getBackgroundColor() == Color.white) {
                ScoreFader sf = new ScoreFader(board[location.getX()][location.getY()].getValue(),
                        getX(0) + location.getY() * getWidth2() / COLUMNS,
                        getY(0) + location.getX() * getHeight2() / ROWS, c);

                board[location.getX()][location.getY()] = null;
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
        arrowAnim += 4;
        if (arrowAnim < 70) {
            arrowLoc += 4;
        }
        if (arrowAnim > 100) {
            arrowLoc = 0;
            arrowAnim = 0;
        }

        if (tipTime < 200) {
            tipTime++;
        } else {
            tipTime = 0;
            currentHint = HINT_PREFIX + HINTS[rand.nextInt(HINTS.length)];
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

        if (currentChatText == null) {
            currentChatText = "Chat with your opponent!";
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

    public void displayAllValidMoves(Graphics2D g, int row, int column, boolean add) {

        // Show all spaces that the piece can move to, represented by green rectangles
        // The piece can move to a space if it is one space above/below it, or diagonal,
        // depending on the color of the piece. The move is allowed if there is another
        // piece at the location to move to, IF the piece at the desired location has the
        // same color or value as the piece you're moving. Kings can move onto any piece,
        // no matter what the value or color. If a piece/stack is a king, then a move to
        // that space is not possible.
    	
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

        g.setColor(new Color(10, 10, 10, 150));

        Piece p = board[row][column];
        int pieceDirection = (p.getTopPiece().getBackgroundColor() == Color.black ? 0 : 1);

        g.setColor(new Color(64, 128, 64, 150));

        if (pieceDirection == 1) {
            if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column)) {
                if (board[row - 1][column] == null) {
                    p.draw(g2d, getX(0) + column * getWidth2() / COLUMNS, getY(0) + (row - 1) * getHeight2() / ROWS);
                }
                drawArrow(arrow.getImage(), getX(0) + column * getWidth2() / COLUMNS + (getWidth2() / COLUMNS / 2) + 2,
                        getY(0) + (row + 1) * getHeight2() / ROWS - arrowLoc - 45, 270, 0.1, 0.2);
                p.draw(g, getX(0) + column * getWidth2() / COLUMNS, getY(0) + (row) * getHeight2() / ROWS);
                if (add)
                	validMoves.add(new OrderedPair(row - 1, column));
            }
            if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column + 1)) {
                if (board[row - 1][column + 1] == null) {
                    p.draw(g2d, getX(0) + (column + 1) * getWidth2() / COLUMNS,
                            getY(0) + (row - 1) * getHeight2() / ROWS);
                }
                drawArrow(arrow.getImage(),
                        getX(0) + column * getWidth2() / COLUMNS + (getWidth2() / COLUMNS / 2) + (arrowLoc / 2),
                        getY(0) + (row + 1) * getHeight2() / ROWS - (arrowLoc / 2) - 47, 315, 0.13, 0.2);
                p.draw(g, getX(0) + column * getWidth2() / COLUMNS, getY(0) + (row) * getHeight2() / ROWS);
                if (add)
                	validMoves.add(new OrderedPair(row - 1, column + 1));
            }
            if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column - 1)) {
                if (board[row - 1][column - 1] == null) {
                    p.draw(g2d, getX(0) + (column - 1) * getWidth2() / COLUMNS,
                            getY(0) + (row - 1) * getHeight2() / ROWS);
                }
                drawArrow(arrow.getImage(),
                        getX(0) + column * getWidth2() / COLUMNS + (getWidth2() / COLUMNS / 2) - (arrowLoc / 2),
                        getY(0) + (row + 1) * getHeight2() / ROWS - (arrowLoc / 2) - 47, 235, 0.13, 0.2);
                p.draw(g, getX(0) + column * getWidth2() / COLUMNS, getY(0) + (row) * getHeight2() / ROWS);
                if (add)
                	validMoves.add(new OrderedPair(row - 1, column - 1));
            }
            p.drawFade(g, getX(0) + column * getWidth2() / COLUMNS, getY(0) + row * getHeight2() / ROWS);
        }

        if (pieceDirection == 0) {
            if (canPieceMoveToLocation(p.getTopPiece(), row + 1, column)) {
                if (board[row + 1][column] == null) {
                    p.draw(g2d, getX(0) + column * getWidth2() / COLUMNS, getY(0) + (row + 1) * getHeight2() / ROWS);
                }
                drawArrow(arrow.getImage(), getX(0) + column * getWidth2() / COLUMNS + (getWidth2() / COLUMNS / 2) + 2,
                        getY(0) + (row + 1) * getHeight2() / ROWS + arrowLoc - 45, 90, 0.1, 0.2);
                p.draw(g, getX(0) + column * getWidth2() / COLUMNS, getY(0) + (row) * getHeight2() / ROWS);
                if (add)
                	validMoves.add(new OrderedPair(row + 1, column));
            }
            if (canPieceMoveToLocation(p.getTopPiece(), row + 1, column + 1)) {
                if (board[row + 1][column + 1] == null) {
                    p.draw(g2d, getX(0) + (column + 1) * getWidth2() / COLUMNS,
                            getY(0) + (row + 1) * getHeight2() / ROWS);
                }
                drawArrow(arrow.getImage(),
                        getX(0) + column * getWidth2() / COLUMNS + (getWidth2() / COLUMNS / 2) + (arrowLoc / 2) + 3,
                        getY(0) + (row + 1) * getHeight2() / ROWS + (arrowLoc / 2) - 43, 45, 0.13, 0.2);
                p.draw(g, getX(0) + column * getWidth2() / COLUMNS, getY(0) + (row) * getHeight2() / ROWS);
                if (add)
                	validMoves.add(new OrderedPair(row + 1, column + 1));
            }
            if (canPieceMoveToLocation(p.getTopPiece(), row + 1, column - 1)) {
                if (board[row + 1][column - 1] == null) {
                    p.draw(g2d, getX(0) + (column - 1) * getWidth2() / COLUMNS,
                            getY(0) + (row + 1) * getHeight2() / ROWS);
                }
                drawArrow(arrow.getImage(),
                        getX(0) + column * getWidth2() / COLUMNS + (getWidth2() / COLUMNS / 2) - (arrowLoc / 2) - 3,
                        getY(0) + (row + 1) * getHeight2() / ROWS + (arrowLoc / 2) - 43, 135, 0.13, 0.2);
                p.draw(g, getX(0) + column * getWidth2() / COLUMNS, getY(0) + (row) * getHeight2() / ROWS);
                if (add)
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
            } //If there IS a piece there
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
    
    public static boolean doMovesMatch(OrderedPair start, OrderedPair end) {
    	if (start.getX() == end.getX() && start.getY() == end.getY())
    		return true;
    	return false;
    }
    
    public static boolean isMoveInArray(OrderedPair move) {
    	for (int i = 0; i < validMoves.size(); i++) {
    		if (doMovesMatch(move, validMoves.get(i)))
    			return true;
    	}
    	return false;
    }
}
