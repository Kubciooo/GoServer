package com.company;

import java.util.ArrayList;

public class Grid {

    protected final int SIZE;
    /**
     * [row][column]
     */
    private Stone[][] stones;
    protected int wynikblack;
    protected  int wynikwhite;
    protected int deleted_col;
    protected int deleted_row;
    protected int how_many_did_i_delete;
    public Grid(int size) {
        SIZE = size;
        stones = new Stone[SIZE][SIZE];
        deleted_col = -1;
        deleted_col = -1;
        how_many_did_i_delete = 0;
    }

    /**
     * Adds Stone to Grid.
     *
     * @param row
     * @param col
     */
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

    /**
     * Check liberties of Stone
     *
     * @param stone
     */
    public void checkStone(Stone stone) {
            if(!checkDFS(stone,stone)){
                State stan = (stone.state == State.BLACK? State.WHITE : State.BLACK);
                System.out.println(stone.state + ": " + stan + '\n');
                deleteStones(stone,stone,stan);
            }
        System.out.println("wynik czarnych: " + wynikblack + "    wynik białych: "+wynikwhite + '\n');
        }
    private void dodajWynik(State state){
        if(state==State.BLACK)wynikblack++;
        else wynikwhite++;
    }
    /**
     * Returns true if given position is occupied by any stone
     *
     * @param row
     * @param col
     * @return true if given position is occupied
     */
    public boolean isOccupied(int row, int col) {
        return stones[row][col] != null;
    }
    public boolean isSafe(int row, int col, State state) {
        if(isOccupied(row,col))return false;
        if(row==deleted_row && col == deleted_col && how_many_did_i_delete == 1)return false;
        Stone helper = new Stone(row,col,state);
        stones[row][col] = helper;
        if(!checkDFS(helper,helper)){
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
                    if(checkDFS(neighbor,neighbor) == false)return true;
                }
            }
            stones[row][col] = null;
            return false;
        }
        return true;

    }
    private boolean checkDFS(Stone stone, Stone ojciec){
        Stone[] neighbors = new Stone[4];
        if (stone.row > 0) {
            neighbors[0] = stones[stone.row - 1][stone.col];
        }
        if (stone.row < SIZE - 1) {
            neighbors[1] = stones[stone.row + 1][stone.col];
        }
        if (stone.col > 1) {
            neighbors[2] = stones[stone.row][stone.col - 1];
        }
        if (stone.col < SIZE - 1) {
            neighbors[3] = stones[stone.row][stone.col + 1];
        }
        for(Stone s : neighbors){
            if(s == ojciec)continue;
            else if(s == null) return true;
            else if(s.state == stone.state){
                if(checkDFS(s, stone))return true;
            }
        }
        return false;
    }
    private void deleteStones(Stone stone, Stone ojciec, State state){
        Stone[] neighbors = new Stone[4];
        if (stone.row > 0) {
            neighbors[0] = stones[stone.row - 1][stone.col];
        }
        if (stone.row < SIZE - 1) {
            neighbors[1] = stones[stone.row + 1][stone.col];
        }
        if (stone.col > 1) {
            neighbors[2] = stones[stone.row][stone.col - 1];
        }
        if (stone.col < SIZE - 1) {
            neighbors[3] = stones[stone.row][stone.col + 1];
        }
        for(Stone s : neighbors){
            if(s == ojciec)continue;
            else if(s==null) continue;
            else if(s.state == stone.state){
                deleteStones(s,stone,state);
            }
        }
        stones[stone.row][stone.col] = null;
        dodajWynik(state);
        deleted_col = stone.col;
        deleted_row = stone.row;
        how_many_did_i_delete++;
        System.out.println("Usunięto kolumnę " + stone.col + " wiersz " + stone.row + '\n');
    }
        public Stone[][] getStones(){
            return stones;
    }
}