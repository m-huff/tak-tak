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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import tak.com.Piece;
import tak.net.ClientHandler;
import tak.net.ServerHandler;

@SuppressWarnings("serial")
public class NetworkWindow extends JFrame implements Runnable {

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
    public static ImageIcon icon = new ImageIcon(NetworkWindow.class.getResource("/tak/assets/icon.png"));
    static ImageIcon background = new ImageIcon(NetworkWindow.class.getResource("/tak/assets/wood.png"));
    private static ImageIcon hoverButton = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button_hover.png"));
    private static ImageIcon button = new ImageIcon(MenuWindow.class.getResource("/tak/assets/button.png"));
    private final NetworkWindow frame = this;
    public static boolean isPotentialGameClient;
    public static String ipAddress = new String();
    public static boolean gameStarted = false;
    public static boolean isConnecting = false;
    private boolean mouseoverPlay;
    private boolean mouseoverReturn;
    TakTakMultiplayerWindow theGame;
    public static ArrayList<Piece> pieces = new ArrayList<Piece>();

    public NetworkWindow(boolean isClient) {

        isPotentialGameClient = isClient;

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setIconImage(icon.getImage());
        setTitle("Tak-Tak");
        setLocation(CENTER_X, CENTER_Y);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.VK_ESCAPE == e.getKeyCode()) {
                    new MenuWindow();
                    frame.dispose();
                } else {
                    if (!gameStarted) {
                        if (e.getKeyCode() == KeyEvent.VK_0) {
                            ipAddress += "0";
                        } else if (e.getKeyCode() == KeyEvent.VK_1) {
                            ipAddress += "1";
                        } else if (e.getKeyCode() == KeyEvent.VK_2) {
                            ipAddress += "2";
                        } else if (e.getKeyCode() == KeyEvent.VK_3) {
                            ipAddress += "3";
                        } else if (e.getKeyCode() == KeyEvent.VK_4) {
                            ipAddress += "4";
                        } else if (e.getKeyCode() == KeyEvent.VK_5) {
                            ipAddress += "5";
                        } else if (e.getKeyCode() == KeyEvent.VK_6) {
                            ipAddress += "6";
                        } else if (e.getKeyCode() == KeyEvent.VK_7) {
                            ipAddress += "7";
                        } else if (e.getKeyCode() == KeyEvent.VK_8) {
                            ipAddress += "8";
                        } else if (e.getKeyCode() == KeyEvent.VK_9) {
                            ipAddress += "9";
                        } else if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
                            ipAddress += ".";
                        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            if (ipAddress.length() >= 1) {
                                ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
                            }
                        }
                    }
                }




                if (gameStarted || isConnecting) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !isConnecting) {
                        if (gameStarted) {
                            if (isPotentialGameClient) {
                                ClientHandler.sendDisconnect();
                                ClientHandler.disconnect();
                            } else {
                                ServerHandler.sendDisconnect();
                                ServerHandler.disconnect();
                            }
                        }
                        gameStarted = false;
                        theGame.dispose();
                        frame.dispose();
                    }
                }

                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (MouseEvent.BUTTON1 == e.getButton() && mouseoverPlay) {
                    if (isPotentialGameClient) {
                        if (!isConnecting) {

                            try {

                                isConnecting = true;
                                ClientHandler.connect(ipAddress, 5657);
                                if (ClientHandler.connected) {
                                    TakTakMultiplayerWindow ttw = new TakTakMultiplayerWindow(frame);
                                    ttw.isClient = true;
                                    ttw.myTurn = true;

                                    theGame = ttw;

                                    gameStarted = true;
                                    isConnecting = false;
                                }
                            } catch (IOException ex) {
                                isConnecting = false;
                            }
                        }
                    } else {
                        if (!isConnecting) {
                            try {
                                isConnecting = true;
                                ServerHandler.recieveConnect(5657);
                                if (ServerHandler.connected) {
                                    TakTakMultiplayerWindow ttw = new TakTakMultiplayerWindow(frame);
                                    ttw.isClient = false;
                                    ttw.myTurn = false;

                                    theGame = ttw;

                                    gameStarted = true;
                                    isConnecting = false;
                                }
                            } catch (IOException ex) {
                                isConnecting = false;
                            }
                        }
                    }
                } else if (MouseEvent.BUTTON1 == e.getButton() && mouseoverReturn) {
                    if (isPotentialGameClient) {
                        ClientHandler.sendDisconnect();
                        ClientHandler.disconnect();
                    } else {
                        ServerHandler.sendDisconnect();
                        ServerHandler.disconnect();
                    }
                    reset();
                    new MenuWindow();
                    frame.dispose();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int xpos = e.getX();
                int ypos = e.getY() + 2;

                if (ypos >= 260 && ypos <= 295 && xpos >= 390 && xpos <= 530) {
                    mouseoverPlay = true;
                } else {
                    mouseoverPlay = false;
                }

                if (ypos >= 260 && ypos <= 295 && xpos >= 245 && xpos <= 385) {
                    mouseoverReturn = true;
                } else {
                    mouseoverReturn = false;
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
            for (int y = 0; y < WINDOW_HEIGHT; y += background.getIconHeight()) {
                g.drawImage(background.getImage(), x, y, null);
            }
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

        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, WINDOW_WIDTH, 130);
        g.fillRect(0, 255, WINDOW_WIDTH, 60);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString((isPotentialGameClient ? "Client" : "Server"), 480, 50);
        g.drawString((gameStarted ? "Connected" : "Not Connected"), (gameStarted ? 450 : 420), 70);
        try {
            g.drawString("Your IP: " + InetAddress.getLocalHost().getHostAddress(), 30, 50);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        g.drawString("Enter an IP address to play against", 30, 90);
        g.drawString("You will play as " + (isPotentialGameClient ? "BLACK" : "WHITE"), 30, 280);
        g.drawString("Opponent IP: " + ipAddress, 30, 110);

        if (mouseoverPlay) {
            g.drawImage(hoverButton.getImage(), 390, 260, null);
        } else {
            g.drawImage(button.getImage(), 390, 260, null);
        }

        if (mouseoverReturn) {
            g.drawImage(hoverButton.getImage(), 245, 260, null);
        } else {
            g.drawImage(button.getImage(), 245, 260, null);
        }

        g.setColor(mouseoverPlay ? Color.red : Color.black);
        g.drawString("Start Game", 422, 283);
        g.setColor(mouseoverReturn ? Color.red : Color.black);
        g.drawString("Main Menu", 275, 283);

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
        ipAddress = "";

        for (int i = 0; i < 20; i++) {
            Color[] colors = {Color.orange, Color.blue, Color.green};
            Piece p = new Piece((rand.nextInt(4) * 10) + 10, colors[rand.nextInt(colors.length)],
                    rand.nextBoolean() ? Color.black : Color.white);
            if (rand.nextInt(10) == 0) {
                p.setKing(true);
            }
            pieces.add(p);
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