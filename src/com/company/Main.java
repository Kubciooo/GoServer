package com.company;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

@SuppressWarnings("ALL")
public class Main
{
    static Vector<ClientHandler> ar = new Vector<>();
    static int passes = 0;
    static int SIZE = 13;
    static Grid grid;
    static boolean FOUND = false;
    static int i = 1;
    public static void main(String[] args) throws IOException
    {


        ServerSocket ss = new ServerSocket(8080);
        System.out.println(InetAddress.getLocalHost() + "\n");
        Socket s;

        while (true)
        {
            if(ar.size() < 2)
            {
                s = ss.accept();
                System.out.println("Otrzymano nowego requesta : " + s);
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Tworzenie wątku dla nowego klienta...");
                ClientHandler mtch = new ClientHandler(i, dis, dos);
                Thread t = new Thread(mtch);

                System.out.println("Dodawanie tego klienta do bazy aktywnych klientów");
                ar.add(mtch);
                t.start();
                i++;
            }


        }
    }
} 