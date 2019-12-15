package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import static com.company.Main.grid;
import static java.lang.Integer.parseInt;

public class ClientHandler implements Runnable
{

    private int name;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    public boolean isloggedin;
    private static int last_row;
    private  static int last_col;
    ClientHandler(int name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
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
        StringBuilder result = new StringBuilder();
        for(int i = 0; i<Main.SIZE; i++){
            for(int j = 0; j<Main.SIZE; j++){
                if(s[i][j] == null) result.append("#N");
                else if(s[i][j].state == State.BLACK) result.append("#B");
                else if(s[i][j].state == State.WHITE) result.append("#W");
            }
        }
        return result.toString();
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


        State state;
        if(pom.contains("bot")){
            Bot bot = new Bot(State.WHITE);
            for (ClientHandler mc : Main.ar) {
                try {
                    mc.dos.writeUTF("found");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (this.isloggedin) {
                try {
                    received = dis.readUTF();
                    System.out.println(received+"\n");
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
                            for (ClientHandler mc : Main.ar) {
                                int c = licz_biale > licz_czarne ? 2 : 1;
                                mc.dos.writeUTF(  "finish#" + c + "#"+licz_czarne+"#"+licz_biale);
                                mc.isloggedin = false;
                            }
                            Main.ar.removeAllElements();
                            grid = null;
                            Main.i = name;
                        } else Main.passes++;
                    } else if (parameter.contains("surr")) {

                        System.out.println("Wygrywa bot");
                        int licz_biale = grid.podlicz_punkty(State.WHITE) + grid.wynikwhite;
                        int licz_czarne = grid.podlicz_punkty(State.BLACK) + grid.wynikblack;

                        for (ClientHandler mc : Main.ar) {
                            mc.dos.writeUTF(  "finish#2#"+ licz_czarne+"#"+licz_biale);
                            mc.isloggedin = false;
                        }
                        Main.i = name;
                        Main.FOUND = false;
                        Main.ar.removeAllElements();
                        Main.grid  = null;
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

                                int x = bot.doMove();
                                if(x==1){
                                    last_col = bot.col;
                                    last_row = bot.row;
                                    for (ClientHandler mc : Main.ar) {
                                        mc.dos.writeUTF(2 + writeGrid() + "#" + bot.row + "#" + bot.col);
                                    }
                                }
                                else{
                                    System.out.println("koniec gry!");
                                for (ClientHandler mc : Main.ar) {
                                    mc.dos.writeUTF(2 + writeGrid() + "#" + last_row +"#"+ last_col);
                                }
                            }
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    Main.i = name;
                    Main.ar.removeAllElements();
                    Main.FOUND = false;
                    System.out.println("Klient " + name + " rozłączył się \n");
                    isloggedin = false;
                }
            }
        }
        else {
            while (Main.ar.size() != 2) {
                System.out.println("Czekam na przeciwnika...");
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
                    received = dis.readUTF();
                    System.out.println(received+"\n");
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
                            for (ClientHandler mc : Main.ar) {
                                int c = licz_biale > licz_czarne ? 2 : 1;
                                mc.dos.writeUTF(  "finish#" + c +"#"+ licz_czarne+"#"+licz_biale);
                                mc.isloggedin = false;
                            }
                            grid = null;
                            Main.FOUND = false;
                            Main.ar.removeAllElements();
                            Main.i = name;
                            System.out.println("Klient " + name + " rozłączył się \n");

                        } else Main.passes++;
                    } else if (parameter.contains("surr")) {

                        System.out.println("Wygrywa gracz " + (clientID % 2 + 1));
                        int c = (clientID % 2) + 1;
                        int licz_biale = grid.podlicz_punkty(State.WHITE) + grid.wynikwhite;
                        int licz_czarne = grid.podlicz_punkty(State.BLACK) + grid.wynikblack;
                        if(c==1)licz_biale = 0;
                        else licz_czarne = 0;
                        for (ClientHandler mc : Main.ar) {
                            mc.dos.writeUTF(  "finish#" + c + "#"+licz_czarne+"#"+licz_biale);
                            mc.isloggedin = false;

                        }
                        grid = null;
                        Main.ar.removeAllElements();
                        Main.i = name;
                        Main.FOUND = false;
                        System.out.println("Klient " + name + " rozłączył się \n");
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
                    System.out.println("Wygrywa gracz " + (name % 2 + 1));
                    int c = (name % 2) + 1;
                    int licz_biale = grid.podlicz_punkty(State.WHITE) + grid.wynikwhite;
                    int licz_czarne = grid.podlicz_punkty(State.BLACK) + grid.wynikblack;
                    for (ClientHandler mc : Main.ar) {
                        try {
                            mc.dos.writeUTF(  "finish#" + c + "#"+licz_czarne+"#"+licz_biale);
                            mc.isloggedin = false;
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    grid = null;
                    Main.ar.removeAllElements();
                    Main.i = name;
                    Main.FOUND = false;
                    System.out.println("Klient " + name + " rozłączył się \n");
                }
            }
        }
    }
} 