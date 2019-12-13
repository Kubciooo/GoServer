package com.company;

import java.util.ArrayList;

public class Grid {

    protected int SIZE;
    /**
     * [row][column]
     */
    public Stone[][] stones;
    private boolean odw[][];
    protected int wynikblack;
    protected  int wynikwhite;
    protected int deleted_col;
    protected int deleted_row;
    protected int how_many_did_i_delete;
    protected boolean count_ter[][];
    public Grid(int size) {
        SIZE = size;
        stones = new Stone[SIZE][SIZE];
        deleted_col = -1;
        deleted_col = -1;
        how_many_did_i_delete = 0;
    }


    public void addStone(int row, int col, State state) {
        how_many_did_i_delete = 0;
        deleted_col = -1;
        deleted_row = -1;
        Stone newStone = new Stone(row, col, state);
        stones[row][col] = newStone;
        // Check neighbors
        Stone[] neighbors = new Stone[4];
        // Don't check outside the board
        if (row > 0) {
            neighbors[0] = stones[row - 1][col];
        }
        if (row < SIZE - 1) {
            neighbors[1] = stones[row + 1][col];
        }
        if (col > 1) {
            neighbors[2] = stones[row][col - 1];
        }
        if (col < SIZE - 1) {
            neighbors[3] = stones[row][col + 1];
        }

        for (Stone neighbor : neighbors) {
            if (neighbor == null) {
                continue;
            }
            if (neighbor.state != newStone.state) {
                checkStone(neighbor);
            }
        }

        //System.out.println("liberties: " + newStone.liberties+'\n');
    }

    public void checkStone(Stone stone) {
        odw = new boolean[SIZE][SIZE];
            if(!checkDFS(stone)){
                odw = new boolean[SIZE][SIZE];
                State stan = (stone.state == State.BLACK? State.WHITE : State.BLACK);
                //System.out.println(stone.state + ": " + stan + '\n');
                deleteStones(stone,stan);
            }
        //System.out.println("wynik czarnych: " + wynikblack + "    wynik białych: "+wynikwhite + '\n');
        }
    private void dodajWynik(State state, int i){
        if(state==State.BLACK)wynikblack+=i;
        else wynikwhite++;
    }

    public boolean isOccupied(int row, int col) {
        return stones[row][col] != null;
    }
    public boolean isSafe(int row, int col, State state) {
        odw = new boolean[SIZE][SIZE];
        if(isOccupied(row,col))return false;
        if(row==deleted_row && col == deleted_col && how_many_did_i_delete == 1)return false;
        Stone helper = new Stone(row,col,state);
        stones[row][col] = helper;
        if(!checkDFS(helper)){
            Stone[] neighbors = new Stone[4];
            // Don't check outside the board
            if (row > 0) {
                neighbors[0] = stones[row - 1][col];
            }
            if (row < SIZE - 1) {
                neighbors[1] = stones[row + 1][col];
            }
            if (col > 1) {
                neighbors[2] = stones[row][col - 1];
            }
            if (col < SIZE - 1) {
                neighbors[3] = stones[row][col + 1];
            }

            for (Stone neighbor : neighbors) {
                if (neighbor == null) {
                    continue;
                }
                else if(neighbor.state != helper.state){
                    if(!checkDFS(neighbor))return true;
                }
            }
            stones[row][col] = null;
            return false;
        }
        stones[row][col]=null;
        return true;

    }
    private boolean checkDFS(Stone stone){
        odw[stone.row][stone.col] = true;
        Stone[] neighbors = new Stone[4];
        int corners = 0;
        if(stone.row == 0)corners++;
         if(stone.col == 0)corners++;
        if(stone.row == SIZE-1)corners++;
         if(stone.col == SIZE-1)corners++;
        if (stone.row > 0) {
            neighbors[0] = stones[stone.row - 1][stone.col];
        }
        if (stone.row < SIZE - 1) {
            neighbors[1] = stones[stone.row + 1][stone.col];
        }
        if (stone.col > 0) {
            neighbors[2] = stones[stone.row][stone.col - 1];
        }
        if (stone.col < SIZE - 1) {
            neighbors[3] = stones[stone.row][stone.col + 1];
        }
        for(Stone s : neighbors){
            if(s == null) {
                corners--;
                if(corners==-1)return true;
            }
            else if(odw[s.row][s.col])continue;
            else if(s.state == stone.state){
                if(checkDFS(s))return true;
            }
        }
        return false;
    }
    private void deleteStones(Stone stone, State state){
        odw[stone.row][stone.col] = true;
        Stone[] neighbors = new Stone[4];
        if (stone.row > 0) {
            neighbors[0] = stones[stone.row - 1][stone.col];
        }
        if (stone.row < SIZE - 1) {
            neighbors[1] = stones[stone.row + 1][stone.col];
        }
        if (stone.col > 0) {
            neighbors[2] = stones[stone.row][stone.col - 1];
        }
        if (stone.col < SIZE - 1) {
            neighbors[3] = stones[stone.row][stone.col + 1];
        }
        for(Stone s : neighbors){
            if(s == null || odw[s.row][s.col])continue;
            else if(s.state == stone.state){
                deleteStones(s,state);
            }
        }
        stones[stone.row][stone.col] = null;
        dodajWynik(state,1);
        deleted_col = stone.col;
        deleted_row = stone.row;
        how_many_did_i_delete++;
        //System.out.println("Usunięto kolumnę " + stone.col + " wiersz " + stone.row + '\n');
    }
        public Stone[][] getStones(){
            return stones;
    }

    public int podlicz_punkty(State state){
        count_ter = new boolean[SIZE][SIZE];
        int suma = 0;
       for(int i = 0; i<SIZE; i++){
           for(int j = 0; j<SIZE; j++){
               if(!count_ter[i][j] && stones[i][j]==null){
                    int licz = count_territories(i,j,state);
                    if(licz != -1)suma+=licz;
               }
               else if(stones[i][j]!= null && stones[i][j].state==state)suma++;
           }
       }
       return suma;

    }
    public int count_territories(int row, int col, State state){
        count_ter[row][col]=true;
         Stone[] neighbors = new Stone[4];
        if (row > 0) {
            neighbors[0] = stones[row - 1][col];
        }
        if (row < SIZE - 1) {
            neighbors[1] = stones[row + 1][col];
        }
        if (col > 0) {
            neighbors[2] = stones[row][col - 1];
        }
        if (col < SIZE - 1) {
            neighbors[3] = stones[row][col + 1];
        }
        for(Stone s : neighbors){
            if(s != null && s.state != state)return -1;
        }
        int suma_czesciowa[] = new int[4];
        if(neighbors[0] == null && row > 0 && !count_ter[row-1][col]){
             suma_czesciowa[0] = count_territories(row-1, col, state);
        }
        if(neighbors[1] == null && row < SIZE - 1 && !count_ter[row+1][col]){
             suma_czesciowa[1] = count_territories(row+1, col, state);
        }
        if(neighbors[2] == null && col > 0 && !count_ter[row][col-1]){
             suma_czesciowa[2] = count_territories(row,col-1,state);
        }
        if(neighbors[3] == null && col < SIZE -1 && !count_ter[row][col+1]){
             suma_czesciowa[3] = count_territories(row,col+1,state);
        }
        for(int i = 0; i<4; i++){
            if(suma_czesciowa[i] == -1)return -1;
        }
        return suma_czesciowa[0]+suma_czesciowa[1]+suma_czesciowa[2]+suma_czesciowa[3]+1;

    }

}