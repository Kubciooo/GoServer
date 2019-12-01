package com.company;

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
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor 
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
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
                int row = parseInt(st.nextToken());
                int col = parseInt(st.nextToken());
                System.out.println(row + " " + col + '\n');
                for (ClientHandler mc : Main.ar)
                {
                    System.out.println(1);
                    mc.dos.writeUTF("Klient: "+ clientID + " : " + row +" " + col + "\n");
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }
} 