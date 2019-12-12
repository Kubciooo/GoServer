package com.company;

import com.sun.net.httpserver.Filter;

public class Stone {

    public State state;
    public int liberties;
    // Row and col are need to remove (set to null) this Stone from Grid
    public int row;
    public int col;

    public Stone(int row, int col, State state) {
        this.state = state;
        liberties = 4;
        this.row = row;
        this.col = col;
    }
}