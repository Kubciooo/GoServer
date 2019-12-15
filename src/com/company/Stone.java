package com.company;

class Stone {

    State state;
    // Row and col are need to remove (set to null) this Stone from Grid
    int row;
    int col;

    Stone(int row, int col, State state) {
        this.state = state;
        this.row = row;
        this.col = col;
    }
}