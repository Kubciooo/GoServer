package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import static com.company.Main.grid;
import static java.lang.Integer.parseInt;

public class ClientHandler implements Runnable
{

    public int name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;
    State state;
    private static int last_row;
    private  static int last_col;
    public ClientHandler(Socket s, int name, DataInputStream dis, DataOutputStream dos) {
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
        for(int i = 0; i<Main.SIZE; i++){
            for(int j = 0; j<Main.SIZE; j++){
                if(s[i][j] == null)result+="#N";
                else if(s[i][j].state == State.BLACK)result+="#B";
                else if(s[i][j].state == State.WHITE)result+="#W";
            }
        }
        return result;
    }
    @Override
    public void run() {

        String received = "";
        try {
            received = dis.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringTokenizer str = new StringTokenizer(received, "#");

        if(Main.ar.size()==1)Main.SIZE = Integer.parseInt(str.nextToken());
        if(grid == null){
            grid = new Grid(Main.SIZE);
        }
        String pom = str.nextToken();
        System.out.println(Main.SIZE);


        if(pom.contains("bot")){
            Bot bot = new Bot(State.WHITE);
            System.out.println(grid.SIZE+"XD\n");
            for (ClientHandler mc : Main.ar) {
                try {
                    mc.dos.writeUTF("found");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (this.isloggedin) {
                try {
                    // receive the string
                    received = dis.readUTF();
                    System.out.println(received+"\n");
                    // break the string into message and recipient part
                    StringTokenizer st = new StringTokenizer(received, "#");
                    int clientID = parseInt(st.nextToken());
                    if (clientID == 1) state = State.BLACK;
                    else state = State.WHITE;
                    String parameter = st.nextToken();
                    if (parameter.contains("pass")) {
                        for (ClientHandler mc : Main.ar) {
                            Main.passes = 1;
                            mc.dos.writeUTF(clientID + writeGrid() + "#" + last_row + "#" + last_col);
                        }
                        if (Main.passes == 1) {
                            int licz_biale = grid.podlicz_punkty(State.WHITE) + grid.wynikwhite;
                            int licz_czarne = grid.podlicz_punkty(State.BLACK) + grid.wynikblack;
                            System.out.println("GRA SKONCZONA. Podliczanie punktów...\nbiali: " + licz_biale + "\nczarni: " + licz_czarne);
                            isloggedin = false;
                        } else Main.passes++;
                    } else if (parameter.contains("surr")) {
                        System.out.println("Wygrywa bot");
                        isloggedin = false;
                    } else {
                        int row = parseInt(parameter);
                        int col = parseInt(st.nextToken());

                        if (row >= Main.SIZE || col >= Main.SIZE || row < 0 || col < 0) {
                            for (ClientHandler mc : Main.ar) {
                                int c = (clientID % 2) + 1;
                                mc.dos.writeUTF(c + writeGrid() + "#" + last_row + "#" + last_col);
                            }
                        } else if (!grid.isSafe(row, col, state)) {
                            for (ClientHandler mc : Main.ar) {
                                int c = (clientID % 2) + 1;
                                mc.dos.writeUTF(c + writeGrid() + "#" + last_row + "#" + last_col);
                            }
                        } else {
                            grid.addStone(row, col, state);
                            last_col = col;
                            last_row = row;
                            for (ClientHandler mc : Main.ar) {
                                mc.dos.writeUTF(clientID + writeGrid() + "#" + last_row + "#" + last_col);
                                String s = (clientID  + "#" + last_row + "#" + last_col);
                                StringTokenizer ss = new StringTokenizer(s,"#");
                                System.out.println(ss.countTokens());

                            }
                            TimeUnit.MILLISECONDS.sleep(300);
                            if(bot.canPlace()) {

                                bot.doMove();
                                last_col = bot.col;
                                last_row = bot.row;
                                for (ClientHandler mc : Main.ar) {
                                    mc.dos.writeUTF(2 + writeGrid() + "#" + bot.row + "#" + bot.col);
                                }
                            }
                            else{
                                for (ClientHandler mc : Main.ar) {
                                    mc.dos.writeUTF(2 + writeGrid() + "#" + last_row + last_col);
                                }
                            }
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    Main.i = name;
                    System.out.println("Client number " + name + " has disconnected \n");
                    isloggedin = false;
                }
            }
            System.out.println("rozłączyło kurwaaa");
        }
        else {
            while (Main.ar.size() != 2) {
                System.out.println("waiting for opponent...");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (this) {
                if (!Main.FOUND) {
                    Main.FOUND = true;
                    for (ClientHandler mc : Main.ar) {
                        try {
                            mc.dos.writeUTF("found");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            while (this.isloggedin) {
                try {
                    // receive the string
                    received = dis.readUTF();
                    System.out.println(received+"\n");
                    // break the string into message and recipient part
                    StringTokenizer st = new StringTokenizer(received, "#");
                    int clientID = parseInt(st.nextToken());
                    if (clientID == 1) state = State.BLACK;
                    else state = State.WHITE;
                    String parameter = st.nextToken();
                    if (parameter.contains("pass")) {
                        for (ClientHandler mc : Main.ar) {
                            mc.dos.writeUTF(clientID + writeGrid() + "#" + last_row + "#" + last_col);
                        }
                        if (Main.passes == 1) {
                            int licz_biale = grid.podlicz_punkty(State.WHITE) + grid.wynikwhite;
                            int licz_czarne = grid.podlicz_punkty(State.BLACK) + grid.wynikblack;
                            System.out.println("GRA SKONCZONA. Podliczanie punktów...\nbiali: " + licz_biale + "\nczarni: " + licz_czarne);
                            isloggedin = false;
                        } else Main.passes++;
                    } else if (parameter.contains("surr")) {
                        System.out.println("Wygrywa gracz " + (clientID % 2 + 1));
                        isloggedin = false;
                    } else {
                        Main.passes = 0;
                        int row = parseInt(parameter);
                        int col = parseInt(st.nextToken());

                        if (row >= Main.SIZE || col >= Main.SIZE || row < 0 || col < 0) {
                            for (ClientHandler mc : Main.ar) {
                                int c = (clientID % 2) + 1;
                                mc.dos.writeUTF(c + writeGrid() + "#" + last_row + "#" + last_col);
                            }
                        } else if (!grid.isSafe(row, col, state)) {
                            for (ClientHandler mc : Main.ar) {
                                int c = (clientID % 2) + 1;
                                mc.dos.writeUTF(c + writeGrid() + "#" + last_row + "#" + last_col);
                            }
                        } else {
                            grid.addStone(row, col, state);
                            last_col = col;
                            last_row = row;
                            for (ClientHandler mc : Main.ar) {
                                mc.dos.writeUTF(clientID + writeGrid() + "#" + last_row + "#" + last_col);
                            }
                        }
                    }
                } catch (IOException e) {
                    Main.i = name;
                    System.out.println("Client number " + name + " has disconnected \n");
                    isloggedin = false;
                }
            }
        }

    }
} 