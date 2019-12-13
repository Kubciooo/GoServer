package com.company;

import java.util.Random;

import static com.company.Main.grid;

public class Bot {
    private State state;
    private int points;
    public int row;
    public int col;


    public Bot(State state){
        this.state = state;
        points = 0;
    }
    private void write(){
        String s = "";
        for(int i =0; i<grid.SIZE; i++){
            for(int j = 0; j<grid.SIZE; j++){
                s+=(grid.stones[i][j]==null? "null" : grid.stones[i][j].state) + " ";
            }
            s+='\n';
        }
        System.out.println(s);

    }
    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    public boolean canPlace(){
        for(int i = 0; i<grid.SIZE; i++){
            for(int j = 0; j<grid.SIZE; j++){
                if(grid.isSafe(i,j,state))return true;
            }
        }
        return false;
    }
    public void doMove(){
        points =  grid.wynikwhite;
        boolean flag = false;
        for(int i = 0; i<grid.SIZE; i++){
            for(int j = 0; j<grid.SIZE; j++){
                if(grid.isSafe(i,j,state)){
                    grid.addStone(i,j,state);
                        if(points < grid.wynikwhite){
                            grid.addStone(i,j,state);
                            row = i; col = j;
                            flag = true;
                            return;
                        }
                        else{
                            grid.stones[i][j] = null;
                        }
                }
            }
        }


        if(!flag){
            for(int i = 0; i < grid.SIZE; i++){
                for(int j = 0; j < grid.SIZE; j++){
                  if(grid.isSafe(i,j,state)){
                        Stone[] neighbors = new Stone[4];
                        if (i > 0) {
                            neighbors[0] =  grid.stones[i - 1][j];
                        }
                        if (i < grid.SIZE - 1) {
                            neighbors[1] =  grid.stones[i + 1][j];
                        }
                        if (j > 0) {
                            neighbors[2] =  grid.stones[i][j - 1];
                        }
                        if (j <  grid.SIZE - 1) {
                            neighbors[3] =  grid.stones[i][j + 1];
                        }
                        for(Stone s : neighbors){

                            if(s!=null && s.state == state){
                                flag = true;
                                row = i; col = j;
                                grid.addStone(i,j,state);
                                return;
                            }
                        }
                    }
                }
            }
        }
         row = getRandomNumberInRange(0,grid.SIZE-1);
         col = getRandomNumberInRange(0,grid.SIZE-1);
        while(!grid.isSafe(row,col,state)){

            row = getRandomNumberInRange(0,grid.SIZE-1);
            col = getRandomNumberInRange(0,grid.SIZE-1);
        }
        grid.addStone(row,col,state);
    }





}
