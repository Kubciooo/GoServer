package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class ClientHandler implements Runnable
{
    public static final int SIZE = 9;

    public int name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    public Grid grid;
    boolean isloggedin;
    State state;
    private int last_row;
    private  int last_col;
    public ClientHandler(Socket s, int name, DataInputStream dis, DataOutputStream dos, Grid grid) {
        this.grid = grid;
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        last_col = -1;
        last_row = -1;
        this.isloggedin=true;
        try {
            dos.writeUTF(name+"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String writeGrid(){
        Stone[][]s = grid.getStones();
        String result = "";
        for(int i = 0; i<grid.SIZE; i++){
            for(int j = 0; j<grid.SIZE; j++){
                if(s[i][j] == null)result+="#N";
                else if(s[i][j].state == State.BLACK)result+="#B";
                else if(s[i][j].state == State.WHITE)result+="#W";
            }
        }
        return result;
    }
    @Override
    public void run() {

        String received;
        while (this.isloggedin)
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

                if (row >= SIZE || col >= SIZE || row < 0 || col < 0) {
                    for (ClientHandler mc : Main.ar)
                    {
                        int c = (clientID%2)+1;
                        mc.dos.writeUTF(c + writeGrid()+"#"+last_row+"#"+last_col);
                    }
                }

               else if (!grid.isSafe(row,col,state)) {
                    for (ClientHandler mc : Main.ar)
                    {
                        int c = (clientID%2)+1;
                        mc.dos.writeUTF(c + writeGrid()+"#"+last_row+"#"+last_col);
                    }
                }
                else {
                    grid.addStone(row, col, state);
                    last_col = col;
                    last_row = row;
                    for (ClientHandler mc : Main.ar)
                    {
                        mc.dos.writeUTF(clientID +writeGrid()+"#"+last_row+"#"+last_col);
                    }
                }
            } catch (IOException e) {
                Main.i = name;
                System.out.println("Client number "+ name + " has disconnected \n");
                isloggedin = false;
            }
        }

    }
} 