package com.fricht.mygame.rendering;


public class Board {

    public static final int P1 = 1;
    public static final int P2 = -1;

    public final int size;
    private int[][][][] board;

    public Board(int size) {
        this.size = size;
        board = new int[size][size][3][2];
        /*
        layers :
            x and y for the position
            the 'stack'
            the data : [amount, user]
        the 'stack' :
        [
         0  current data
         1  other data to take account, 'hidden' and not registered for the animations
         2  new data to take account
        ]
        */
    }

    public int[][] get_at(int x, int y) {
        return board[y][x];
    }

    public void set_at(int x, int y, int[][] value) {
        board[y][x] = value;
    }

    public boolean is_out_of_bound(int x, int y) {
        return x < 0 || y < 0 || x >= size || y >= size;
    }

    public void clear_set_at(int x, int y, int value, int player) {
        int[][] cell = {{value, player}, {0, 0}, {0, 0}};  // reset 'stack'
        board[y][x] = cell;
    }

    public void add(int x, int y, int amount, int player) {
        if (is_out_of_bound(x, y)) {  // skip if out of bound
            return;
        }
        if (board[y][x][2][1] != 0) {
            board[y][x][1][0] += board[y][x][2][0];
            board[y][x][1][1] = board[y][x][2][1];
        }
        board[y][x][2][0] = amount;
        board[y][x][2][1] = player;
    }

    public int[][] get_merged_at(int x, int y) {
        int[][] cell = get_at(x, y);
        int[][] new_cell = {{cell[0][0] + cell[1][0] + cell[2][0], cell[2][1]}, {0, 0}, {0, 0}};
        return new_cell;
    }

    public int[][] get_split_at(int x, int y) {
        // WARNING : does not add to neighbouring cells !!!
        int[][] cell = get_at(x, y);
        cell[0][0] -= 4;
        if (cell[0][0] < 1) {
            cell[0][1] = 0;
        }
        return cell;
        //return new int[][]{{cell[0][0], cell[0][1]}, {cell[1][0], cell[1][1]}, {cell[2][0], cell[2][1]}};
    }

    public boolean can_play_at(int x, int y, int player) {
        if (is_out_of_bound(x, y)) {  // skip if out of bound
            return false;
        }
        return get_at(x, y)[0][1] == player;
    }

}
