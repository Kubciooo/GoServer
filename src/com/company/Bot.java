package com.company;

import java.util.Random;

public class Bot {
    private State state;
    private Grid grid;
    private int points;



    public Bot(State state, Grid grid){
        this.state = state;
        this.grid = grid;
        points = 0;
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    public void doMove(){
        Grid testingGrid = grid;
        points = (state == State.BLACK? grid.wynikblack : grid.wynikwhite);
        boolean flag = false;
        for(int i = 0; i<grid.SIZE; i++){
            for(int j = 0; j<grid.SIZE; j++){
                if(!flag && testingGrid.isSafe(i,j,state)){
                    testingGrid.addStone(i,j,state);
                    if(state == State.BLACK){
                        if(points < testingGrid.wynikblack){
                            grid.addStone(i,j,state);
                            flag = true;
                            return;
                        }
                        else testingGrid = grid;
                    }
                    else if(state == State.WHITE){
                        if(points < testingGrid.wynikwhite){
                            grid.addStone(i,j,state);
                            flag = true;
                            return;
                        }
                        else testingGrid = grid;
                    }
                }
            }
        }
        if(!flag){
            for(int i = 0; i < grid.SIZE; i++){
                for(int j = 0; j < grid.SIZE; j++){
                    if(!flag && testingGrid.isSafe(i,j,state)){
                        Stone[] neighbors = new Stone[4];
                        if (i > 0) {
                            neighbors[0] =  grid.stones[i - 1][j];
                        }
                        if (i < grid.SIZE - 1) {
                            neighbors[1] =  grid.stones[i + 1][j];
                        }
                        if (j > 1) {
                            neighbors[2] =  grid.stones[i][j - 1];
                        }
                        if (j <  grid.SIZE - 1) {
                            neighbors[3] =  grid.stones[i][j + 1];
                        }
                        for(Stone s : neighbors){
                            if(s.state == state){
                                grid.addStone(i,j,state);
                                return;
                            }
                        }
                    }
                }
            }
        }
        int row = getRandomNumberInRange(0,grid.SIZE-1);
        int col = getRandomNumberInRange(0,grid.SIZE-1);
        while(!grid.isSafe(row,col,state)){
            row = getRandomNumberInRange(0,grid.SIZE-1);
            col = getRandomNumberInRange(0,grid.SIZE-1);
        }
        grid.addStone(row,col,state);
    }


}
