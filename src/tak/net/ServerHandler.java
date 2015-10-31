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
import java.net.*;
import tak.window.TakTakMultiplayerWindow;

public class ServerHandler
{
    public static boolean connected = false;
    public static Point postPoints = null;
    public static Point initPoints = null;
    private static Socket client = null;
    private static PrintWriter serverOut = null;
    private static BufferedReader serverIn = null;

    public static void recieveConnect(int port) throws UnknownHostException, IOException, SocketTimeoutException
    {
        ServerSocket server = new ServerSocket(port);
        server.setSoTimeout(8000);
        client = server.accept();
        serverOut = new PrintWriter(client.getOutputStream(), true);
        serverIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        connected = true;
        recievePieceMove();
    }

    public static void disconnect()
    {
        try
        {
            if (client != null)
                client.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        client = null;
        serverOut = null;
        serverIn = null;
        connected = false;
       // tak.window.TakTakWindow.closeGame();
    }
    public static void sendPieceMove(int initrow, int initcol, int movedrow, int movedcol, int myScore)
    {
		if (connected)
		{
//add or modify.                    
			serverOut.println(initrow + ":" + initcol + ":" + movedrow + ":" + movedcol + ":" + myScore);
		}        
    }


    public static void sendDisconnect()
    {
        if (connected)
        {
            serverOut.println("esc");
        }
    }

    private static void recievePieceMove()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String inputLine;

                try
                {
                    while ((inputLine = serverIn.readLine()) != null)
                    {
                        try
                        {
                            if (inputLine.equals("esc"))
                            {
                                disconnect();
                                return;
                            }
                            // row:col:initrow:initcol
//add or modify.                            
                            int initrowpost = Integer.parseInt(inputLine.split(":")[0]);
                            int initcolpost = Integer.parseInt(inputLine.split(":")[1]);
                            int movedrowpost = Integer.parseInt(inputLine.split(":")[2]);
                            int movedcolpost = Integer.parseInt(inputLine.split(":")[3]);
                            int myScore = Integer.parseInt(inputLine.split(":")[4]);

                            TakTakMultiplayerWindow.initRow = initrowpost;
                            TakTakMultiplayerWindow.initCol = initcolpost;
                            TakTakMultiplayerWindow.movedRow = movedrowpost;
                            TakTakMultiplayerWindow.movedCol = movedcolpost;
                            TakTakMultiplayerWindow.opponentScore = myScore;
                            TakTakMultiplayerWindow.updateTurn();
                            TakTakMultiplayerWindow.initMovePiece();
                        }
                        catch (NumberFormatException e)
                        {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e)
                        {
                            disconnect();
                        }
                    }
                }
                catch (SocketException e)
                {
                    disconnect();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static boolean isConnected()
    {
        return connected;
    }
}
