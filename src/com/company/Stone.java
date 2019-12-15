package com.company;

class Stone {

    State state;
    int row;
    int col;

    Stone(int row, int col, State state) {
        this.state = state;
        this.row = row;
        this.col = col;
    }
}