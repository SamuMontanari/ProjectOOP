package com.example.projectoop;

import java.util.List;
import java.util.Random;

public class Field {
    int rows;
    int cols;
    int[][] grid;

    int foodPlaced;

    Random rnd = new Random();

    public Field(int rows, int cols) {
        this.cols = cols;
        this.rows = rows;
        this.grid = new int[rows][cols];
        this.foodPlaced = 0;
    }

    public Field() {
        this.cols = 60;
        this.rows = 80;
        this.grid = new int[rows][cols];
        this.foodPlaced = 0;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void removeFood() {this.foodPlaced--;}

    public void setFood(int qnt) {

        for(int r = 3; r < rows - 3; r++) {
            for(int c = 3; c < cols - 3; c++) {
                    grid[r][c] = 0;
            }
        }

        foodPlaced = 0;

        if(qnt <= foodPlaced) {
            return;
        }

        while (foodPlaced <= qnt) {
            for(int r = 3; r < rows - 3; r++) {
                for(int c = 3; c < cols - 3; c++) {
                    int feed = rnd.nextInt(rows + cols);
                    if(feed == 0 && grid[r][c] != 1) {
                        grid[r][c] = 1;
                        foodPlaced++;
                        if(foodPlaced == qnt)
                            return;
                    }
                }
            }
        }
    }

    public void spawnHunters(int qnt, List<Hunter> hunters, int minS, int maxS, int minM, int maxM) {
        int counter = 0;



        while(counter < (qnt)/4) {

            int mov = rnd.nextInt(minM, maxM + 1);
            int sig = rnd.nextInt(minS, maxS + 1);

            int col = rnd.nextInt(1, getCols() - 1);
            if(grid[0][col] != 2) {
                grid[0][col] = 2;
                Hunter h = new Hunter(0, col, mov, sig);
                hunters.add(h);
                counter++;
            }
        }

        while (counter < (qnt)/2) {

            int mov = rnd.nextInt(minM, maxM + 1);
            int sig = rnd.nextInt(minS, maxS + 1);

            int col = rnd.nextInt(1, getCols() - 1);
            if(grid[getRows() - 1][col] != 2) {
                grid[getRows() - 1][col] = 2;
                Hunter h = new Hunter(getRows() - 1, col, mov, sig);
                hunters.add(h);
                counter++;
            }
        }

        while (counter < ((qnt)/4 + (qnt)/2)) {

            int mov = rnd.nextInt(minM, maxM + 1);
            int sig = rnd.nextInt(minS, maxS + 1);

            int row = rnd.nextInt(1, getRows() - 1);
            if (grid[row][0] != 2) {
                grid[row][0] = 2;
                Hunter h = new Hunter(row, 0, mov, sig);
                hunters.add(h);
                counter++;
            }
        }

        while (counter < qnt) {

            int mov = rnd.nextInt(minM, maxM + 1);
            int sig = rnd.nextInt(minS, maxS + 1);

            int row = rnd.nextInt(1, getRows() - 1);
            if(grid[row][getCols() - 1] != 2) {
                grid[row][getCols() - 1] = 2;
                Hunter h = new Hunter(row, getCols() - 1, mov, sig);
                hunters.add(h);
                counter++;
            }
        }
    }

}
