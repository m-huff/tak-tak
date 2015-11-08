/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tak.net;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import tak.util.OrderedPair;
import tak.window.TakTakMultiplayerWindow;

public class ClientHandler {
	public static boolean connected = false;
	public static Point postPoints = null;
	public static Point initPoints = null;
	private static String hostIP = null;
	private static int hostPort = -1;
	private static Socket server = null;
	private static PrintWriter serverOut = null;
	private static BufferedReader serverIn = null;

	//Time it allows to connect, in seconds
	private static final int TIMEOUT = 20;

	public static void connect(String ip, int port) throws UnknownHostException, IOException {
		hostIP = ip;
		hostPort = port;
		server = new Socket();
		server.connect(new InetSocketAddress(ip, port), TIMEOUT * 1000);
		serverOut = new PrintWriter(server.getOutputStream(), true);
		serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
		connected = true;
		recievePieceMove();
		recieveChat();
	}

	public static void disconnect() {
		try {
			if (server != null)
				server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		hostIP = null;
		hostPort = -1;
		server = null;
		serverOut = null;
		serverIn = null;
		connected = false;
	}

	public static void sendPieceMove(int initrow, int initcol, int movedrow, int movedcol) {
		if (connected) {
			serverOut.println(initrow + ":" + initcol + ":" + movedrow + ":" + movedcol);
			TakTakMultiplayerWindow.myTurn = !TakTakMultiplayerWindow.myTurn;
			TakTakMultiplayerWindow.movePieceToLocation(new OrderedPair(initrow, initcol),
					new OrderedPair(movedrow, movedcol));
		}
	}

	public static void sendChat(String chat) {
		if (connected) {
			serverOut.println("!" + chat);
			//For debug
			System.out.println("!" + chat);
		}
	}

	public static void sendDisconnect() {
		if (connected) {
			serverOut.println("esc");
		}
	}

	private static void recievePieceMove() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String inputLine;

				try {
					while ((inputLine = serverIn.readLine()) != null) {
						try {
							if (inputLine.equals("esc")) {
								disconnect();
								return;
							}

							if (!inputLine.startsWith("!")) {
								//add or modify.
								// row:col:initrow:initcol:myScore
								int initrowpost = Integer.parseInt(inputLine.split(":")[0]);
								int initcolpost = Integer.parseInt(inputLine.split(":")[1]);
								int movedrowpost = Integer.parseInt(inputLine.split(":")[2]);
								int movedcolpost = Integer.parseInt(inputLine.split(":")[3]);

								TakTakMultiplayerWindow.initRow = initrowpost;
								TakTakMultiplayerWindow.initCol = initcolpost;
								TakTakMultiplayerWindow.movedRow = movedrowpost;
								TakTakMultiplayerWindow.movedCol = movedcolpost;
								TakTakMultiplayerWindow.myTurn = !TakTakMultiplayerWindow.myTurn;
								TakTakMultiplayerWindow.movePieceToLocation(new OrderedPair(initrowpost, initcolpost),
										new OrderedPair(movedrowpost, movedcolpost));
							}
						} catch (NumberFormatException | NullPointerException e) {
							e.printStackTrace();
							if (e instanceof NullPointerException)
								disconnect();
						}
					}
				} catch (IOException e) {
					disconnect();
				}

			}
		}).start();
	}

	private static void recieveChat() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String inputLine;

				try {
					while ((inputLine = serverIn.readLine()) != null) {
						try {
							if (inputLine.equals("esc")) {
								disconnect();
								return;
							}

							if (inputLine.startsWith("!")) {
								String msg = inputLine.replace("!", "");
								TakTakMultiplayerWindow.chat.add(msg);
								System.out.println("\"" + msg + "\" was added to client chat");
							}
						} catch (NumberFormatException | NullPointerException e) {
							e.printStackTrace();
							if (e instanceof NullPointerException)
								disconnect();
						}
					}
				} catch (SocketException e) {
					disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	public static boolean isConnected() {
		return connected;
	}
}
