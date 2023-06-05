package com.example.projectoop;

import java.util.Random;
import java.util.random.*;

import static java.lang.Math.*;
import static java.lang.Thread.sleep;

public class Hunter {

    int foodCollected;
    int rPos;
    int cPos;

    int maxMovement;
    int actMovement;
    int sight;
    float foodNeeded;

    Random rnd = new Random();

    public Hunter(int rPos, int cPos, int movement, int sight) {
        this.rPos = rPos;
        this.cPos = cPos;
        this.maxMovement = movement;
        this.actMovement = maxMovement;
        this.sight = sight;
        this.foodNeeded = (((float)(movement/7) + (float)(sight/3)) / 2) + 1;
    }

    public int getFoodCollected() {
        return foodCollected;
    }

    public int getrPos() {
        return rPos;
    }

    public int getcPos() {
        return cPos;
    }

    public void stepTaken() {actMovement--;}

    public int getMovement() {
        return maxMovement;
    }

    public int getSight() {
        return sight;
    }

    public float getFoodNeeded() {
        return foodNeeded;
    }



    public void moveUp(Field field) {
        field.grid[getrPos()][getcPos()] = 0;
        this.rPos--;
        eat(field);
        field.grid[getrPos()][getcPos()] = 2;
        stepTaken();
    }

    public void moveDown(Field field) {
        field.grid[getrPos()][getcPos()] = 0;
        this.rPos++;
        eat(field);
        field.grid[getrPos()][getcPos()] = 2;
        stepTaken();
    }

    public void moveLeft(Field field) {
        field.grid[getrPos()][getcPos()] = 0;
        this.cPos--;
        eat(field);
        field.grid[getrPos()][getcPos()] = 2;
        stepTaken();
    }

    public void moveRight(Field field) {
        field.grid[getrPos()][getcPos()] = 0;
        this.cPos++;
        eat(field);
        field.grid[getrPos()][getcPos()] = 2;
        stepTaken();
    }

    public void collectFood() {
        this.foodCollected++;
    }

    public int[] seekFood(Field field) {
        int [][] grid = field.getGrid();

        int topSearch = getrPos() - sight;
        int botSearch = getrPos() + sight;

        int leftSearch = getcPos() - sight;
        int rightSearch = getcPos() + sight;

        if(topSearch < 0) {
            topSearch = 0;
        }
        if(leftSearch < 0) {
            leftSearch = 0;
        }

        if(botSearch >= field.getRows() ) {
            botSearch = field.getRows() - 1;
        }
        if(rightSearch >= field.getCols() ) {
            rightSearch = field.getCols() - 1;
        }

        int[] location = new int[2];
        location[0] = 0;
        location[1] = 0;

        for(int k = 0; k <= sight; k++) {
            for(int i = topSearch; i < botSearch; i++) {
                for(int j = leftSearch; j < rightSearch; j++) {

                    if((abs(i - getrPos()) + abs(j - getcPos())) <= k) {
                        //System.out.println("Cerco con raggio " + k);


                        if(grid[i][j] == 1) {
                            location[0] = i;
                            location[1] = j;

                            //System.out.println("Trovato cibo in punto r: " + location[0] + " c: " + location[1] + "\n");
                            return location;
                        }
                    }
                }
            }
        }
        return location;
    }

    public void eat(Field field) {
        int [][] grid = field.getGrid();
        if(grid[getrPos()][getcPos()] == 1) {
            collectFood();
            field.removeFood();
        }

    }

    public void act(Field field) {
        int topDist = getrPos();
        int botDist = field.getRows() - getrPos();

        int leftDist = getcPos();
        int rightDist = field.getCols() - getcPos();

        int minDist = min(min(topDist, botDist), min(leftDist, rightDist));

        if(actMovement > minDist + 1 || foodCollected < foodNeeded) {
            move(field);
        } else {
            flee(field);
        }
    }

    public void flee(Field field) {
        int topDist = getrPos();
        int botDist = field.getRows() - getrPos();

        int leftDist = getcPos();
        int rightDist = field.getCols() - getcPos();

        int minDist = min(min(topDist, botDist), min(leftDist, rightDist));

        if (minDist == topDist) {
            moveUp(field);
        } else if (minDist == botDist) {
            moveDown(field);
        } else if (minDist == leftDist) {
            moveLeft(field);
        } else {
            moveRight(field);
        }

        if(getcPos() == 0 || getrPos() == 0 || getrPos() == field.getRows() - 1  || getcPos() == field.getCols() - 1) {
            actMovement = 0;
        }
    }

    public void move(Field field) {
        int[] located = seekFood(field);
/*
        System.out.println("row: " + getrPos() + " cols: " + getcPos());
        System.out.println("located row: " + located[0] + " located cols: " + located[1]);
*/

        /*
        100 - (getcpos * 4) = % di possibilità che il cacciatore si muova verso destra
        100 - (getrpos * 4) = % di possibilità che il cacciatore si muova verso il basso
         */

        if(located[0] != 0) {
            if(getrPos() > located[0]) {
                moveUp(field);
                return;
            }

            if(getrPos() < located[0]) {
                moveDown(field);
                return;
            }

            if(getcPos() < located[1]) {
                moveRight(field);
                return;
            }

            if(getcPos() > located[1]) {
                moveLeft(field);
                return;
            }
        }

        if(getcPos() >= (field.getCols() - 3) ) {
            moveLeft(field);
            return;
        }

        if(getcPos() <= 2) {
            moveRight(field);
            return;
        }

        if(getrPos() >= (field.getRows() - 3) ) {
            moveUp(field);
            return;
        }

        if(getrPos() <= (2) ) {
            moveDown(field);
            return;
        }

        int pickDirection = (int)Math.floor(Math.random() * 2);

        if(pickDirection == 0) {
            int randomMovement = (int)Math.floor(Math.random() * 25);

            if(randomMovement > 25 - getcPos())
                moveLeft(field);
            else
                moveRight(field);
        } else {
            int randomMovement = (int)Math.floor(Math.random() * 25);

            if(randomMovement > 25 - getrPos())
                moveUp(field);
            else
                moveDown(field);
        }
    }

    public void resetHunter() {
        this.foodCollected = 0;
        this.actMovement = this.maxMovement;
    }

    public Hunter reproduce(Field field) {
        int mov = this.maxMovement + (rnd.nextInt(-2, 2));
        if(mov < 6) {
            mov = 6;
        }

        int sig = this.sight + (rnd.nextInt(-1, 1));
        if(sig < 2) {
            sig = 2;
        }

        int grid[][] = field.getGrid();

        if(getcPos() == 0 || getcPos() == field.getCols() - 1) {
            if(grid[getrPos() - 1][getcPos()] != 2) {
                field.grid[getrPos() - 1][getcPos()] = 2;
                System.out.println("Riproduzione completata ");
                return new Hunter(getrPos() - 1, getcPos(), mov, sig);
            } else if (grid[getrPos() + 1][getcPos()] != 2) {
                field.grid[getrPos() + 1][getcPos()] = 2;
                System.out.println("Riproduzione completata ");
                return new Hunter(getrPos() + 1, getcPos(), mov, sig);
            }
            System.out.println("Caselle adiacenti bloccate, impossibile riprodursi");
        } else {
            if(grid[getrPos()][getcPos() - 1] != 2) {
                field.grid[getrPos()][getcPos() - 1] = 2;
                System.out.println("Riproduzione completata ");
                return new Hunter(getrPos(), getcPos() - 1, mov, sig);
            } else if (grid[getrPos()][getcPos() + 1] != 2) {
                field.grid[getrPos()][getcPos() + 1] = 2;
                System.out.println("Riproduzione completata ");
                return new Hunter(getrPos(), getcPos() + 1, mov, sig);
            }
        }
        return null;
    }
}
