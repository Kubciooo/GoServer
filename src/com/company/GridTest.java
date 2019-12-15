package com.company;

import org.testng.annotations.Test;

import static com.company.State.BLACK;
import static com.company.State.WHITE;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class GridTest{

    @Test
    public void podlicz_punkty() {
        Grid grid = new Grid(9);
        grid.addStone(0,0,BLACK);
        assertEquals(grid.podlicz_punkty(BLACK),81);
    }
    @Test
    public void podlicz_punkty_2(){
        Grid grid = new Grid(9);
        grid.addStone(0,0,BLACK);
        grid.addStone(0,1,WHITE);
        assertEquals(grid.podlicz_punkty(BLACK),1);
    }
    @Test
    public void czy_zajety(){
        Grid grid = new Grid(9);
        grid.addStone(0,0,BLACK);
        assertFalse(grid.isSafe(0,0,WHITE));
    }
    @Test
    public void zbij(){
        Grid grid = new Grid(9);
        grid.addStone(0,0,WHITE);
        grid.addStone(1,0,BLACK);
        grid.addStone(0,1,BLACK);
        assertEquals(grid.wynikblack,1);
    }

    @Test
    public void check_ko(){
        Grid grid = new Grid(9);
        grid.addStone(0,3,BLACK);
        grid.addStone(1,2,BLACK);
        grid.addStone(2,3,BLACK);
        grid.addStone(0,4,WHITE);
        grid.addStone(1,5,WHITE);
        grid.addStone(2,4,WHITE);
        if(grid.isSafe(1,4,BLACK))grid.addStone(1,4,BLACK);
        if(grid.isSafe(1,3,WHITE))grid.addStone(1,3,WHITE);
        assertEquals(grid.wynikwhite,1);
        assertFalse(grid.isSafe(1,4,BLACK));
    }
    @Test
    public void wynik_box(){
        Grid grid = new Grid(13);
        for(int i = 0; i<5; i++){
            grid.addStone(4,i,BLACK);
            grid.addStone(i,4,BLACK);
        }
        grid.addStone(12,12,WHITE);
        assertEquals(grid.podlicz_punkty(BLACK),25);

    }
}
