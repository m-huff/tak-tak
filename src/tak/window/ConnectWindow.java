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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import tak.net.ClientHandler;
import tak.net.ServerHandler;

@SuppressWarnings("serial")
public class ConnectWindow extends JFrame implements Runnable {

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
	public static ImageIcon icon = new ImageIcon(ConnectWindow.class.getResource("/tak/assets/icon.png"));

	private final ConnectWindow frame = this;

	public static boolean isPotentialGameClient;
	public static String ipAddress = new String();
	public static boolean gameStarted = false;
	public static boolean isConnecting = false;

	public ConnectWindow(boolean isClient) {

		isPotentialGameClient = isClient;

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setIconImage(icon.getImage());
		setTitle("Tak-Tak");
		setLocation(CENTER_X, CENTER_Y);

		addKeyListener(new KeyAdapter() {

			@SuppressWarnings("static-access")
			public void keyPressed(KeyEvent e) {
				if (e.VK_ESCAPE == e.getKeyCode()) {
					new MenuWindow();
					frame.dispose();
				}
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
					if (ipAddress.length() >= 1)
						ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
				}

				if (gameStarted || isConnecting) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !isConnecting) {
						if (gameStarted)
							if (isPotentialGameClient) {
								ClientHandler.sendDisconnect();
								ClientHandler.disconnect();
							} else {
								ServerHandler.sendDisconnect();
								ServerHandler.disconnect();
							}
						gameStarted = false;
						reset();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_S) {
					if (isPotentialGameClient) {
						if (!isConnecting) {
							try {

								isConnecting = true;
								System.out.println("is connecting true");
								ServerHandler.recieveConnect(5657);
								System.out.println("after recieveConnect");
								if (ServerHandler.connected) {
									final TakTakWindow ttw = new TakTakWindow();
									ttw.isClient = isPotentialGameClient;
									ttw.myTurn = isPotentialGameClient;
									gameStarted = true;
									isConnecting = false;
								}
							} catch (IOException ex) {
								System.out.println("Cannot host server: " + ex.getMessage());
								isConnecting = false;
							}
						}
					} else {
						if (!isConnecting) {

							try {

								isConnecting = true;
								ClientHandler.connect(ipAddress, 5657);
								if (ClientHandler.connected) {
									final TakTakWindow ttw = new TakTakWindow();
									ttw.isClient = isPotentialGameClient;
									ttw.myTurn = isPotentialGameClient;
									gameStarted = true;
									isConnecting = false;
								}
							} catch (IOException ex) {
								System.out.println("Cannot join server: " + ex.getMessage());
								isConnecting = false;
							}
						}       
					}
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

		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(0, 0, WINDOW_WIDTH, 180);

		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString((isPotentialGameClient ? "Client" : "Server"), 480, 50);
		try {
			g.drawString("Your IP: " + InetAddress.getLocalHost().getHostAddress(), 30, 50);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		g.drawString("Enter an IP address to play against", 30, 90);
		g.drawString("Press S to attempt to start a game", 30, 280);
		g.drawString("Opponent IP: " + ipAddress, 30, 110);

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