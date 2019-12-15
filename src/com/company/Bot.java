package com.company;

import java.util.Random;

import static com.company.Main.grid;

@SuppressWarnings("ALL")
class Bot {
    private State state;
    private int points;
    int row;
    int col;


    Bot(State state){
        this.state = state;
        points = 0;
    }
    private void write(){
        StringBuilder s = new StringBuilder();
        for(int i =0; i<grid.SIZE; i++){
            for(int j = 0; j<grid.SIZE; j++){
                s.append(grid.stones[i][j] == null ? "null" : grid.stones[i][j].state).append(" ");
            }
            s.append('\n');
        }
        System.out.println(s);

    }
    private static int getRandomNumberInRange(int max) {
        Random r = new Random();
        return r.nextInt((max) + 1);
    }
    private boolean canPlace(){
        for(int i = 0; i<grid.SIZE; i++){
            for(int j = 0; j<grid.SIZE; j++){
                if(grid.isSafe(i,j,state))return true;
            }
        }
        return false;
    }
    int doMove(){
        if(!canPlace())return -1;
        points =  grid.wynikwhite;
        boolean flag = false;
        for(int i = 0; i<grid.SIZE; i++){
            for(int j = 0; j<grid.SIZE; j++){
                if(grid.isSafe(i,j,state)){
                    grid.addStone(i,j,state);
                        if(points < grid.wynikwhite){
                            row = i; col = j;
                            return 1;
                        }
                        else{
                            grid.stones[i][j] = null;
                        }
                }
            }
        }

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
                                row = i; col = j;
                                grid.addStone(i,j,state);
                                return 1;
                            }
                        }
                    }
                }
            }

         row = getRandomNumberInRange(grid.SIZE-1);
         col = getRandomNumberInRange(grid.SIZE-1);
         int licz = 0;
        while(!grid.isSafe(row,col,state) && licz<15){

            row = getRandomNumberInRange(grid.SIZE-1);
            col = getRandomNumberInRange(grid.SIZE-1);
            licz++;
            if(licz==15)return -1;
        }
        grid.addStone(row,col,state);
        return 1;
    }
}
