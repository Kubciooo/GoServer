package com.company;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;
import static sun.net.www.protocol.http.AuthCacheValue.Type.Server;

// ClientHandler class
public class ClientHandler implements Runnable
{
    public static final int SIZE = 9;
    public static final int N_OF_TILES = SIZE - 1;
    public static final int TILE_SIZE = 40;
    public static final int BORDER_SIZE = TILE_SIZE;

    private int name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    public Grid grid;
    boolean isloggedin;
    State state;
    // constructor 
    public ClientHandler(Socket s, int name, DataInputStream dis, DataOutputStream dos, Grid grid) {
        this.grid = grid;
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
        try {
            dos.writeUTF(name+"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                // receive the string
                received = dis.readUTF();
                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                int clientID = parseInt(st.nextToken());
                if(clientID==1)state=State.BLACK;
                else state = State.WHITE;
                int row = parseInt(st.nextToken());
                int col = parseInt(st.nextToken());
                System.out.println(clientID + " " + row + " " + col + '\n');
                if (row >= SIZE || col >= SIZE || row < 0 || col < 0) {
                    for (ClientHandler mc : Main.ar)
                    {
                        int c = (clientID%2)+1;
                        mc.dos.writeUTF(c + "#" + -1 +"#" + -1);
                    }
                }

               else if (grid.isOccupied(row, col)) {
                    for (ClientHandler mc : Main.ar)
                    {
                        int c = (clientID%2)+1;
                        mc.dos.writeUTF(c  + "#" + -1 +"#" + -1);
                    }
                }
                else {
                    grid.addStone(row, col, state);
                    for (ClientHandler mc : Main.ar)
                    {
                        mc.dos.writeUTF(clientID  + "#" + row +"#" + col);
                    }
                }


            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }
} 