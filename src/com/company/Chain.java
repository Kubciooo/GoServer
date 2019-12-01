package com.company;

import com.company.Stone;

import java.util.ArrayList;

/**
 * zbior sasiadujacych kamieni
 *
 */
public class Chain {

    public ArrayList<Stone> stones;
    public State state;

    public Chain(State state) {
        stones = new ArrayList<>();
    }

    public int getLiberties() {
        int total = 0;
        for (Stone stone : stones) {
            total += stone.liberties;
        }
        return total;
    }

    public void addStone(Stone stone) {
        stone.chain = this;
        stones.add(stone);
    }

    public void join(Chain chain) {
        for (int i = 0; i <chain.stones.size(); i++) {
            Stone stone = chain.stones.get(i);
            addStone(stone);
        }
    }

}