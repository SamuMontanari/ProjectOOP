package com.example.projectoop;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SurvivalController {
    public static final int ROWS = 25;
    public static final int COLS = 25;

    public static final int DEFfoodQNT = 30;
    public static final int DEFhuntersQNT = 10;
    public static final int DEFmaxSight = 6;
    public static final int DEFmaxMov = 50;
    public static final int DEFminSight = 1;
    public static final int DEFminMov = 7;
    public static final int DEFreproduceNeed = 2;

    public int foodQNT = DEFfoodQNT;
    public int huntersQNT = DEFhuntersQNT;
    public int maxSight = DEFmaxSight;
    public int maxMov = DEFmaxMov;
    public int minSight = DEFminSight;
    public int minMov = DEFminMov;
    public int reproduceNeed = DEFreproduceNeed;

    @FXML private TilePane tilePane;
    @FXML private Button step;
    @FXML private Button day;
    @FXML private Label movLab;
    @FXML private Label sigLab;
    @FXML private Label popLab;
    @FXML private CheckBox check;


    @FXML private TextField food;
    @FXML private TextField hunt;
    @FXML private TextField minS;
    @FXML private TextField minM;
    @FXML private TextField maxS;
    @FXML private TextField maxM;
    @FXML private TextField repr;

    Field field;
    List<Hunter> hunters;

    boolean allDone = false;
    float avgMov = 0;
    float avgSig = 0;

    public void initialize() {
        allDone = false;
        field = new Field(ROWS, COLS);
        hunters = new ArrayList<>();
        step.setDisable(false);
        day.setDisable(true);
        day.setText("Next day");
        initPanel();
        setPanel();
    }

    void initPanel() {
        tilePane.getChildren().clear();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Rectangle rect = new Rectangle(24, 24, Color.RED);

                rect.setArcHeight(30);
                rect.setArcWidth(30);
                tilePane.getChildren().add(rect);
            }
        }
    }

    void setupGrid() {
        avgMov = 0;
        avgSig = 0;
        int[][] grid = field.getGrid();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Rectangle tile = (Rectangle) tilePane.getChildren().get(i * COLS + j);
                Color color = Color.WHITE;

                if(grid[i][j] == 0)
                    color = Color.WHITE;

                if(grid[i][j] == 1)
                    color = Color.GREEN;

                if(grid[i][j] == 2)
                    color = Color.RED;

                tile.setFill(color);
            }
        }

        int counter = 0;
        for(Hunter h: hunters) {
            avgMov += h.getMovement();
            avgSig += h.getSight();
            counter++;
        }

        avgMov = avgMov/counter;
        avgSig = avgSig/counter;

        String movText = String.format("%.02f", avgMov);
        String sigText = String.format("%.02f", avgSig);
        String popText = String.format(String.valueOf(counter));

        movLab.setText("AVG MOV: " + movText);
        sigLab.setText("AVG SIG: " + sigText);
        popLab.setText("POPULATION: " + popText);
    }

    void setPanel() {
        field.setFood(foodQNT);
        field.spawnHunters(huntersQNT, hunters, minSight, maxSight, minMov, maxMov);

        day.setDisable(true);

        setupGrid();
    }

    @FXML
    void whatToDo() {
        if(check.isSelected()) {
            auto();
        } else {
            stepHunters();
        }
    }

    public void auto() {

        while (!allDone) {
            stepHunters();
        }
        killAllOutside();

        if(hunters.isEmpty()) {
            System.out.println("Tutti i cacciatori sono morti, non sono riusciti ad evolversi correttamente");
            day.setText("Close app");
            movLab.setText("AVG MOV: --- ");
            sigLab.setText("AVG SIG: --- ");
        }

    }

    public void stepHunters() {
        allDone = true;

        for (int i = hunters.size() - 1; i >= 0; i--) {
            Hunter h = hunters.get(i);
            if (h.actMovement > 0) {
                allDone = false;
                h.act(field);
            } else {
                survive(h);
            }
        }

        if(allDone) {
            day.setDisable(false);
            step.setDisable(true);
            killAllOutside();
        }

        setupGrid();
    }

    public void survive(Hunter h) {

        System.out.println("Il cacciatore in posizione r:" + h.getrPos() + " c:" + h.getcPos() + " ha collezionato " + h.getFoodCollected() + " su " + h.getFoodNeeded());

        if (!(h.getcPos() == 0 || h.getrPos() == 0 || h.getrPos() == field.getRows() - 1 || h.getcPos() == field.getCols() - 1)) {
            die(h);
            return;
        }

        if (h.getFoodCollected() < h.getFoodNeeded()) {
            System.out.println("Di conseguenza è morto");
            die(h);
        } else if (h.getcPos() == 0 || h.getrPos() == 0 || h.getrPos() == field.getRows() - 1 || h.getcPos() == field.getCols() - 1) {
            System.out.println("Quindi può vivere un altro giorno");
        } else {
            System.out.println("Ma non è tornato a casa in tempo ed è morto");
            die(h);

        }
    }

    public void die(Hunter h){
        field.grid[h.getrPos()][h.getcPos()] = 0;
        hunters.remove(h);
        setupGrid();
    }

    public void nextDay() {

        if(hunters.isEmpty()) {
            System.out.println("Tutti i cacciatori sono morti, non sono riusciti ad evolversi correttamente");
            step.setDisable(true);
            day.setDisable(true);
            Platform.exit();
            return;
        }

        System.out.println("Cibo presente sulla griglia: " + field.foodPlaced);
        field.setFood(foodQNT);
        System.out.println("Cibo presente sulla griglia: " + field.foodPlaced);


        List<Hunter> newHunters = new ArrayList<>();

        for (Hunter h : hunters) {
            Hunter hunter = null;

            if(h.getFoodCollected() > (h.getFoodNeeded()) + reproduceNeed)
                hunter = h.reproduce(field);

            if(hunter != null)
                newHunters.add(hunter);
            h.resetHunter();
        }

        hunters.addAll(newHunters);

        int counter = 0;
        for (Hunter h: hunters) {
            System.out.println("Cacciatore numero " + counter + " in posizione r:" + h.getrPos() + " c:" + h.getcPos());
            counter++;
        }

        step.setDisable(false);
        day.setDisable(true);
        allDone = false;
        setupGrid();
    }

    void killAllOutside () {
        for (int i = hunters.size() - 1; i >= 0; i--) {
            Hunter h = hunters.get(i);
            if(h.getcPos() != field.getRows() - 1 && h.getcPos() != 0 && h.getrPos() != 0 && h.getrPos() != field.getCols() - 1) {
                die(h);
            }
        }
    }

    void exterminate() {
        System.out.println("Ci sono ben" + hunters.size() + "cacciatori");
        for (int i = hunters.size() - 1; i >= 0; i--) {
            Hunter h = hunters.get(i);
            die(h);
        }

        System.out.println("Ci sono ben" + hunters.size() + "cacciatori");
    }

    @FXML
    void helpUnderstanding() throws IOException {
        Stage helpStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("help-view.fxml"));
        Scene scene = new Scene(root);
        helpStage.setTitle("Help");
        helpStage.setScene(scene);
        helpStage.setResizable(false);
        helpStage.show();
    }

    @FXML
    void advancedHelp() throws IOException {
        Stage helpStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("advancedHelp-view.fxml"));
        Scene scene = new Scene(root);
        helpStage.setTitle("Help");
        helpStage.setScene(scene);
        helpStage.setResizable(false);
        helpStage.show();
    }

    @FXML
    void restartSimulation() {

        if(!food.getText().equals(""))
            foodQNT = Integer.parseInt(food.getText());

        if(!hunt.getText().equals(""))
            huntersQNT = Integer.parseInt(hunt.getText());

        if(!maxS.getText().equals(""))
            maxSight = Integer.parseInt(maxS.getText());

        if(!maxM.getText().equals(""))
            maxMov = Integer.parseInt(maxM.getText());

        if(!minS.getText().equals(""))
            minSight = Integer.parseInt(minS.getText());

        if(!minM.getText().equals(""))
            minMov = Integer.parseInt(minM.getText());

        if(!repr.getText().equals(""))
            reproduceNeed = Integer.parseInt(repr.getText());

        if(foodQNT > 361)
            foodQNT = 361;

        if(foodQNT < 10)
            foodQNT = 10;

        if(huntersQNT < 1)
            huntersQNT = 1;

        if(huntersQNT > 92)
            huntersQNT = 92;

        if(minSight < 1)
            minSight = 1;

        if(maxSight < minSight)
            maxSight = minSight + 1;

        if(minMov < 7)
            minMov = 7;

        if(maxMov < minMov)
            maxMov = minMov + 1;

        if(reproduceNeed < 0)
            reproduceNeed = 0;

        exterminate();
        initialize();

        food.setText("");
        hunt.setText("");
        minS.setText("");
        maxS.setText("");
        minM.setText("");
        maxM.setText("");
        repr.setText("");

        food.setPromptText(String.valueOf(foodQNT));
        hunt.setPromptText(String.valueOf(huntersQNT));
        minS.setPromptText(String.valueOf(minSight));
        maxS.setPromptText(String.valueOf(maxSight));
        minM.setPromptText(String.valueOf(minMov));
        maxM.setPromptText(String.valueOf(maxMov));
        repr.setPromptText(String.valueOf(reproduceNeed));
    }

    @FXML
    void resetFields() {
        food.setText("");
        hunt.setText("");
        minS.setText("");
        minM.setText("");
        maxM.setText("");
        maxS.setText("");
        repr.setText("");
    }

    @FXML
    void fullResetFields() {

        food.setPromptText(String.valueOf(DEFfoodQNT));
        hunt.setPromptText(String.valueOf(DEFhuntersQNT));
        minS.setPromptText(String.valueOf(DEFminSight));
        maxS.setPromptText(String.valueOf(DEFmaxSight));
        minM.setPromptText(String.valueOf(DEFminMov));
        repr.setPromptText(String.valueOf(DEFreproduceNeed));

        food.setText("");
        hunt.setText("");
        minS.setText("");
        minM.setText("");
        maxM.setText("");
        maxS.setText("");
        repr.setText("");

        foodQNT = DEFfoodQNT;
        huntersQNT = DEFhuntersQNT;
        maxSight = DEFmaxSight;
        maxMov = DEFmaxMov;
        minSight = DEFminSight;
        minMov = DEFminMov;
        reproduceNeed = DEFreproduceNeed;

        exterminate();
        initialize();
    }
/*
    @FXML
    void printAll() {
        int counter = 0;
        for(Hunter h : hunters) {
            System.out.println("Cacciatore " + counter + " mov: " + h.getMovement() + " sig: " + h.getSight());
            counter++;
        }
    }*/
}