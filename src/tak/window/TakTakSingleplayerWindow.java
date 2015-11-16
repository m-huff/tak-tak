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
import tak.ui.ScoreFader;
import tak.ui.TurnIndicator;
import tak.util.OrderedPair;
import tak.util.PlayerAI;
import tak.util.Sound;

public class TakTakSingleplayerWindow extends JFrame implements Runnable {

    static public final int WINDOW_WIDTH = 590;
    static public final int WINDOW_HEIGHT = 740;
    static public final int FULL_HEIGHT = 770;
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
    private static final int CENTER_Y = (SCREEN_HEIGHT / 2) - (FULL_HEIGHT / 2);
    public static Random rand = new Random();
    static ImageIcon icon = new ImageIcon(TakTakSingleplayerWindow.class.getResource("/tak/assets/icon.png"));
    static ImageIcon background = new ImageIcon(TakTakSingleplayerWindow.class.getResource("/tak/assets/wood.png"));
    private static ImageIcon arrow = new ImageIcon(MenuWindow.class.getResource("/tak/assets/greenarrow.png"));
    private static ImageIcon hoverButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_hover.png"));
    private static ImageIcon button = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button.png"));
    private static ImageIcon smallHoverButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_small_hover.png"));
    private static ImageIcon smallButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_small.png"));
    private static ImageIcon muted = new ImageIcon(MenuWindow.class.getResource("/tak/assets/muted.png"));
    private static ImageIcon notMuted = new ImageIcon(MenuWindow.class.getResource("/tak/assets/notmuted.png"));
    //Board of real game is 6x7
    public static final int COLUMNS = 6;
    public static final int ROWS = 7;
    public static Piece[][] board;
    public static int numPiecesOnBoard;
    public static int numBlackPiecesOnBoard;
    public static int numWhitePiecesOnBoard;
    private final TakTakSingleplayerWindow frame = this;
    public int turn;
    public boolean myTurn;
    public static int selectedRow = 999;
    public static int selectedColumn = 999;
    public static int lilWindaRow = 999;
    public static int lilWindaColumn = 999;
    public static int mousex;
    public static int mousey;
    public static int myScore;
    public static int myWins;
    public int arrowLoc;
    public int arrowAnim;
    public static int aiScore;
    public int aiMoveDelay;
    public static int aiWins;
    public int fadeOut;
    private boolean mouseoverPlayAgain;
    private boolean mouseoverReturn;
    private boolean mouseoverHelp;
    private boolean mouseoverQuit;
    private boolean mouseoverMute;
    private boolean tellMeWhenItsMyTurn = true;
    private boolean mouseoverConfig;
    static Sound move;
    static Sound cha_ching;
    public static ArrayList<OrderedPair> validMoves = new ArrayList<OrderedPair>();
    public static ArrayList<ScoreFader> faders = new ArrayList<ScoreFader>();
    public static TurnIndicator turnIndicator = null;
    public int tipTime;
    public static boolean isWindowOpen;
    public static String HINT_PREFIX = "Tip: ";
    public static String currentHint = "";
    public static String[] HINTS = {"Stack your pieces to move across the board faster!",
        "Right-click a piece to see the stack size and total value!",
        "Once you've selected a piece, the green arrows show all of the places you can move.",
        "Right-clicking a piece will reveal all of the information about it!",
        "You can stack your pieces in your own safe zone to get pieces across faster!",
        "Your king piece can't be stacked on top of!",
        "When you right-click a piece, you can see a 3D image of the entire stack!",
        "The king is valuable because it can't be captured!",
        "If you're having trouble, click the \"Help\" button to relearn the rules!"};

    public static enum EnumWinner {

        PlayerOne,
        PlayerTwo,
        PlayerAI,
        Tie,
        None
    }
    public static EnumWinner winner;

    public TakTakSingleplayerWindow() {
        isWindowOpen = true;

        setSize(WINDOW_WIDTH, FULL_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

                if (xpos >= 200 && xpos <= 340 && ypos >= 350 && ypos <= 390 && fadeOut >= 150) {
                    mouseoverPlayAgain = true;
                } else {
                    mouseoverPlayAgain = false;
                }

                if (xpos >= 200 && xpos <= 340 && ypos >= 400 && ypos <= 440 && fadeOut >= 150) {
                    mouseoverReturn = true;
                } else {
                    mouseoverReturn = false;
                }

                if (xpos >= 400 && xpos <= 540 && ypos >= 730 && ypos <= 765) {
                    mouseoverHelp = true;
                } else {
                    mouseoverHelp = false;
                }

                if (xpos >= 255 && xpos <= 395 && ypos >= 730 && ypos <= 765) {
                    mouseoverQuit = true;
                } else {
                    mouseoverQuit = false;
                }

                if (xpos >= 105 && xpos <= 245 && ypos >= 730 && ypos <= 765) {
                    mouseoverConfig = true;
                } else {
                    mouseoverConfig = false;
                }

                if (xpos >= 545 && xpos <= 580 && ypos >= 730 && ypos <= 765) {
                    mouseoverMute = true;
                } else {
                    mouseoverMute = false;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverPlayAgain && fadeOut >= 150) {
                    reset();
                } else if (MouseEvent.BUTTON1 == e.getButton() && mouseoverReturn && fadeOut >= 150) {
                    reset();
                    myWins = 0;
                    aiWins = 0;
                    new MenuWindow();
                    frame.dispose();
                } else if (MouseEvent.BUTTON1 == e.getButton() && mouseoverHelp) {
                    new RulesWindow();
                    //Maybe open up to different slides depending on what the state of the game is
                } else if (MouseEvent.BUTTON1 == e.getButton() && mouseoverQuit) {
                    reset();
                    myWins = 0;
                    aiWins = 0;
                    new MenuWindow();
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

                    if (selectedRow == 999 && board[currentRow][currentColumn] != null) {
                        if (board[currentRow][currentColumn].getTopPiece().getBackgroundColor() == Color.white) {
                            selectedRow = currentRow;
                            selectedColumn = currentColumn;
                            arrowLoc = 0;
                            arrowAnim = 0;
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
                                myTurn = !myTurn;
                            } else if (board[currentRow][currentColumn] != null
                                    && board[currentRow][currentColumn].getTopPiece().getBackgroundColor() == Color.white) {

                                boolean isGoodMove = false;
                                for (int j = 0; j < validMoves.size(); j++) {
                                    if (validMoves.get(j).toString()
                                            .equals(new OrderedPair(currentRow, currentColumn).toString())) {
                                        isGoodMove = true;
                                    }
                                }

                                if (!isGoodMove) {
                                    selectedRow = currentRow;
                                    selectedColumn = currentColumn;
                                    arrowLoc = 0;
                                    arrowAnim = 0;
                                    return;
                                }
                            } else if (board[currentRow][currentColumn] != null
                                    && board[currentRow][currentColumn].getTopPiece().getBackgroundColor() == Color.white) {

                                boolean isGoodMove = false;
                                for (int j = 0; j < validMoves.size(); j++) {
                                    if (validMoves.get(j).toString()
                                            .equals(new OrderedPair(currentRow, currentColumn).toString())) {
                                        isGoodMove = true;
                                    }
                                }

                                if (isGoodMove) {
                                    movePieceToLocation(new OrderedPair(selectedRow, selectedColumn),
                                            new OrderedPair(currentRow, currentColumn));
                                    selectedRow = 999;
                                    selectedColumn = 999;
                                    validMoves.clear();
                                    myTurn = !myTurn;
                                }
                            }
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

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.VK_ESCAPE == e.getKeyCode()) {
                    new MenuWindow();
                    reset();
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

        for (int x = 0; x < WINDOW_WIDTH; x += background.getIconWidth()) {
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
            displayAllValidMoves(g, selectedRow, selectedColumn);
        }
        if (lilWindaRow != 999 && board[lilWindaRow][lilWindaColumn] != null) {
            board[lilWindaRow][lilWindaColumn].drawLilWinda(g, mousex, mousey);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial Bold", Font.PLAIN, 14));
        g.drawString(currentHint, 15, 720);

        g.drawString("Player Score: " + myScore, 40, 60);
        g.drawString("Your Wins: " + myWins, 40, 45);
        g.drawString("AI Score: " + aiScore, 460, 60);
        g.drawString("AI Wins: " + aiWins, 460, 45);
        g.setFont(new Font("Arial Bold", Font.BOLD, 18));
        g.drawString((myTurn ? "YOUR (WHITE)" : "OPPONENT'S") + " Turn", myTurn ? 205 : 210, 55);
        g.setFont(new Font("Arial Bold", Font.BOLD, 14));
        g.drawString("Turn #" + (turn + 1), 270, 40);

        g.setColor(new Color(0, 0, 0, fadeOut));
        g.fillRect(0, 0, WINDOW_WIDTH, FULL_HEIGHT);

        g.setColor(new Color(255, 255, 255, fadeOut));
        g.setFont(new Font("Arial Bold", Font.PLAIN, 36));

        if (mouseoverHelp) {
            g.drawImage(hoverButton.getImage(), 400, 730, null);
        } else {
            g.drawImage(button.getImage(), 400, 730, null);
        }
        if (mouseoverQuit) {
            g.drawImage(hoverButton.getImage(), 255, 730, null);
        } else {
            g.drawImage(button.getImage(), 255, 730, null);
        }

        if (tellMeWhenItsMyTurn) {
            g.drawImage(hoverButton.getImage(), 105, 730, null);
        } else {
            g.drawImage(button.getImage(), 105, 730, null);
        }

        if (mouseoverMute) {
            g.drawImage(smallHoverButton.getImage(), 545, 730, null);
            g.drawImage((TakTakMain.muted ? muted.getImage() : notMuted.getImage()), 545, 730, null);
        } else {
            g.drawImage(smallButton.getImage(), 545, 730, null);
            g.drawImage((TakTakMain.muted ? muted.getImage() : notMuted.getImage()), 545, 730, null);
        }

        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.black);
        g.drawString("Turn Indicators: " + (tellMeWhenItsMyTurn ? "On" : "Off"), 120, 753);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(mouseoverHelp ? Color.red : Color.black);
        g.drawString("Help", 453, 753);
        g.setColor(mouseoverQuit ? Color.red : Color.black);
        g.drawString("Quit", 307, 753);

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

            if (mouseoverPlayAgain) {
                g.drawImage(hoverButton.getImage(), 225, 350, null);
            } else {
                g.drawImage(button.getImage(), 225, 350, null);
            }

            if (mouseoverReturn) {
                g.drawImage(hoverButton.getImage(), 225, 400, null);
            } else {
                g.drawImage(button.getImage(), 225, 400, null);
            }


            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(mouseoverPlayAgain ? Color.red : Color.black);
            g.drawString("Play Again", 254, 373);
            g.setColor(mouseoverReturn ? Color.red : Color.black);
            g.drawString("Return to Menu", 237, 425);

            g.setColor(Color.white);
            g.drawString("Your score: " + myScore, 235, 475);
            g.drawString("AI Score: " + aiScore, 235, 505);

            if (fadeOut < 150) {
                fadeOut += 5;
            }
        }

        if (winner == EnumWinner.None) {
            if (fadeOut > 0) {
                fadeOut -= 5;
            }
        }

        for (int i = 0; i < faders.size(); i++) {
            faders.get(i).draw(g);
        }

        if (turnIndicator != null && tellMeWhenItsMyTurn && winner == EnumWinner.None) {
            turnIndicator.draw(g);
        } else if (!tellMeWhenItsMyTurn || winner != EnumWinner.None) {
            turnIndicator = null;
        }

        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////

    public void drawArrow(Image image, int xpos, int ypos, double rot, double xscale,
            double yscale) {
        int width;
        int height;

        width = arrow.getImage().getWidth(this);
        height = arrow.getImage().getHeight(this);

        g.translate(xpos, ypos);
        g.rotate(rot * Math.PI / 180.0);
        g.scale(xscale, yscale);

        g.drawImage(image, -width / 2, -height / 2,
                width, height, this);

        g.scale(1.0 / xscale, 1.0 / yscale);
        g.rotate(-rot * Math.PI / 180.0);
        g.translate(-xpos, -ypos);
    }
    ////////////////////////////////////////////////////////////////////////////

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

        arrowLoc = 0;
        arrowAnim = 0;

        winner = EnumWinner.None;

        faders.clear();
        resetBoard();
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

        if (numBlackPiecesOnBoard > 0) {
            if (!myTurn && aiMoveDelay >= 50) {
                PlayerAI.makeMove();
                myTurn = !myTurn;
                turnIndicator = new TurnIndicator();
                turn++;
                aiMoveDelay = 0;
            } else if (!myTurn && aiMoveDelay < 50) {
                aiMoveDelay++;
            }
        } else {
            turn++;
        }

        if ((numWhitePiecesOnBoard == 0 || numBlackPiecesOnBoard == 0) && winner == EnumWinner.None) {
            //If one side doesn't have any more pieces
            //Score all remaining pieces
            for (int zRow = 0; zRow < ROWS; zRow++) {
                for (int zColumn = 0; zColumn < COLUMNS; zColumn++) {
                    if (board[zRow][zColumn] != null) {
                        if (board[zRow][zColumn].getTopPiece().getBackgroundColor() == Color.black) {
                            aiScore += board[zRow][zColumn].getValue();
                            ScoreFader sf2 = new ScoreFader(board[zRow][zColumn].getValue(), getX(0) + zColumn * getWidth2() / COLUMNS,
                                    getY(0) + zRow * getHeight2() / ROWS, new Color(128, 64, 64));
                        } else {
                            myScore += board[zRow][zColumn].getValue();
                            ScoreFader sf2 = new ScoreFader(board[zRow][zColumn].getValue(), getX(0) + zColumn * getWidth2() / COLUMNS,
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

    public static void movePieceToLocation(OrderedPair piece, OrderedPair location) {

        if (board[location.getX()][location.getY()] != null && board[piece.getX()][piece.getY()] != null) {
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

        //Pieces are in opponent's safe zone
        if (location.getX() >= 5 && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black
                || location.getX() < 2 && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.white) {
            if (board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black) {
                aiScore += board[location.getX()][location.getY()].getValue();
                numBlackPiecesOnBoard--;
            } else {
                myScore += board[location.getX()][location.getY()].getValue();
                numWhitePiecesOnBoard--;
            }

            Color c;
            if (location.getX() >= 5 && board[location.getX()][location.getY()].getTopPiece().getBackgroundColor() == Color.black) {
                c = new Color(128, 64, 64);
            } else {
                c = new Color(64, 180, 64);
            }

            ScoreFader sf = new ScoreFader(board[location.getX()][location.getY()].getValue(), getX(0) + location.getY() * getWidth2() / COLUMNS,
                    getY(0) + location.getX() * getHeight2() / ROWS, c);
            numPiecesOnBoard -= board[location.getX()][location.getY()].getWholeStack().size();
            board[location.getX()][location.getY()] = null;
            move = new Sound("sound/chaching.wav");
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
        Piece blackKing = new Piece(50, Color.black, Color.black);
        blackKing.setKing(true);
        board[row][column] = blackKing;

        row = rand.nextInt(2) + 5;
        column = rand.nextInt(COLUMNS);
        Piece whiteKing = new Piece(50, Color.white, Color.white);
        whiteKing.setKing(true);
        board[row][column] = whiteKing;
    }

    public static void chooseWinner() {
        if (myScore > aiScore) {
            winner = EnumWinner.PlayerOne;
            myWins++;
        } else if (aiScore > myScore) {
            winner = EnumWinner.PlayerAI;
            aiWins++;
        } else if (aiScore == myScore) {
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
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

        Piece p = board[row][column];
        g.setColor(new Color(64, 128, 64, 150));

        if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column)) {
            if (board[row - 1][column] == null) {
                p.draw(g2d, getX(0) + column * getWidth2() / COLUMNS,
                        getY(0) + (row - 1) * getHeight2() / ROWS);
            }
            drawArrow(arrow.getImage(), getX(0) + column * getWidth2() / COLUMNS + (getWidth2() / COLUMNS / 2) + 2,
                    getY(0) + (row + 1) * getHeight2() / ROWS - arrowLoc - 45, 270, 0.1, 0.2);
            p.draw(g, getX(0) + column * getWidth2() / COLUMNS,
                    getY(0) + (row) * getHeight2() / ROWS);
            validMoves.add(new OrderedPair(row - 1, column));
        }
        if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column + 1)) {
            if (board[row - 1][column + 1] == null) {
                p.draw(g2d, getX(0) + (column + 1) * getWidth2() / COLUMNS,
                        getY(0) + (row - 1) * getHeight2() / ROWS);
            }
            drawArrow(arrow.getImage(), getX(0) + column * getWidth2() / COLUMNS + (getWidth2() / COLUMNS / 2) + (arrowLoc / 2),
                    getY(0) + (row + 1) * getHeight2() / ROWS - (arrowLoc / 2) - 47, 315, 0.13, 0.2);
            p.draw(g, getX(0) + column * getWidth2() / COLUMNS,
                    getY(0) + (row) * getHeight2() / ROWS);
            validMoves.add(new OrderedPair(row - 1, column + 1));
        }
        if (canPieceMoveToLocation(p.getTopPiece(), row - 1, column - 1)) {
            if (board[row - 1][column - 1] == null) {
                p.draw(g2d, getX(0) + (column - 1) * getWidth2() / COLUMNS,
                        getY(0) + (row - 1) * getHeight2() / ROWS);
            }
            drawArrow(arrow.getImage(), getX(0) + column * getWidth2() / COLUMNS + (getWidth2() / COLUMNS / 2) - (arrowLoc / 2),
                    getY(0) + (row + 1) * getHeight2() / ROWS - (arrowLoc / 2) - 47, 235, 0.13, 0.2);
            p.draw(g, getX(0) + column * getWidth2() / COLUMNS,
                    getY(0) + (row) * getHeight2() / ROWS);
            validMoves.add(new OrderedPair(row - 1, column - 1));
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

    public void updateTurn() {
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
        return (WINDOW_WIDTH - getX(0) - XBORDER);
    }

    public static int getHeight2() {
        return (WINDOW_HEIGHT - getY(0) - YBORDER);
    }
}
