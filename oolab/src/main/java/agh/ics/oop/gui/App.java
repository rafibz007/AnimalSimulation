package agh.ics.oop.gui;

import agh.ics.oop.engines.SimulationEngine;
import agh.ics.oop.interfaces.IMapObserver;
import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.mapElements.AbstractWorldElement;
import agh.ics.oop.mapElements.Vector2d;
import agh.ics.oop.maps.AbstractWorldMap;
import agh.ics.oop.maps.WrappedBorderMap;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.*;

public class App extends Application implements IMapObserver, IPositionChangeObserver {

    AbstractWorldMap map;
    SimulationEngine engine;

    Vector2d lowerLeft;
    Vector2d upperRight;

    Set<Vector2d> changedPositions = new HashSet<>();
    Map<Vector2d, GuiElementBox> drawnElementsPositions = new HashMap<>();

    Stage window;
    GridPane gridPane;
    Scene simulation;
    Scene startingMenu;
    Scene mapChoosingMenu;


    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
//        showMapChoosingMenu();
//        showMapsOptions(2);
//        window.setFullScreen(true);
        window.show();
        window.setScene(simulation);
        new Thread(engine).start();
    }



    @Override
    public void init() throws FileNotFoundException {
//        SETTING UP ENGINE
        map = new WrappedBorderMap(20, 20, 10, 10, 1, 1,1, 1);
        engine = new SimulationEngine(map, 0, 500);
        map.addObserverForAnimals(this);
        engine.addObserver(this);


        gridPane = new GridPane();
        simulation = new Scene(gridPane, 400, 400);
        drawGrid();

        map.addAmountOfAnimalsToMap(1);
//        map.addGrassToJungle(300);
//        map.addGrassToStep(2000);


//        SETTING UP STARTING MENU GUI







//        SETTING UP STATISTICS

//        SETTING UP MAP GUI

//        BorderPane borderPane = new BorderPane();


//        gridPane.setAlignment(Pos.CENTER);

//        VBox vBox = new VBox();
//        TextField textField = new TextField("f r b l");
//        vBox.getChildren().addAll(textField, gridPane);
//        vBox.setAlignment(Pos.CENTER);
//        vBox.setSpacing(10);
//        BorderPane.setMargin(vBox, new Insets(10));

//        Button button = new Button("RUN");
//        button.setOnAction( (ActionEvent event) -> {
//            String[] args = textField.getText().split(" ");
////            engine.setMoves(OptionParser.parse(args));
////            Thread thread = new Thread(engine);
////            thread.start();
//        } );
//        vBox.getChildren().add(1,button);


//        borderPane.setTop(vBox);

    }


    public void showMapChoosingMenu(){
        GridPane menu = new GridPane();
        menu.setAlignment(Pos.CENTER);
        mapChoosingMenu = new Scene(menu, 600, 300);

        window.setTitle("Simulation (Settings)");
        window.setScene(mapChoosingMenu);

    }


    public void showMapsOptions(int amountOfMaps){
        int startingMenuWidth = 600*amountOfMaps;
        int startingMenuHeight = 800;

        int startingMenuColsAmount = 2*amountOfMaps;
        int startingMenuRowsAmount = 21;
        int startingMenuRowSize = startingMenuHeight/startingMenuRowsAmount;
        int startingMenuColSize = startingMenuWidth/startingMenuColsAmount;

        GridPane menu = new GridPane();
        menu.setAlignment(Pos.CENTER);
//        menu.setGridLinesVisible(true);
        startingMenu = new Scene(menu, startingMenuWidth, startingMenuHeight);
        for (int i=0; i<startingMenuRowsAmount; i++)
            menu.getRowConstraints().add(new RowConstraints(startingMenuRowSize));
        for (int i=0; i<startingMenuColsAmount; i++)
            menu.getColumnConstraints().add(new ColumnConstraints(startingMenuColSize));

        List<TextField> inputFields = new ArrayList<>();
        for (int j=0; j<amountOfMaps; j++){
            int offset = j*2;

//            HEADER
            int rowIndex = 0;
            addMapsOptionHeader(rowIndex, offset, menu, "Map Details");

//            MAP PROPERTIES
            rowIndex = 1;
            addMapsOptionHeader(rowIndex, offset, menu, "Map Properties");

            rowIndex = 2;
            addMapsOptionLabel(rowIndex, offset, menu, "Map Height");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex = 3;
            addMapsOptionLabel(rowIndex, offset, menu, "Map Width");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex = 4;
            addMapsOptionLabel(rowIndex, offset, menu, "Jungle Height");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex = 5;
            addMapsOptionLabel(rowIndex, offset, menu, "Jungle Width");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex=6;
//            TODO
            rowIndex=7;
//            TODO

//            ENERGY PROPERTIES
            rowIndex = 8;
            addMapsOptionHeader(rowIndex, offset, menu, "Energy Properties");

            rowIndex = 9;
            addMapsOptionLabel(rowIndex, offset, menu, "Grass Energy Profit");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex = 10;
            addMapsOptionLabel(rowIndex, offset, menu, "Minimum energy to copulation");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex = 11;
            addMapsOptionLabel(rowIndex, offset, menu, "Animal Starting Energy");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex = 12;
            addMapsOptionLabel(rowIndex, offset, menu, "Daily Energy Loss");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

//            SPAWNING PROPERTIES
            rowIndex = 13;
            addMapsOptionHeader(rowIndex, offset, menu, "Spawning Properties");

            rowIndex = 14;
            addMapsOptionLabel(rowIndex, offset, menu, "Animals At Start");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex = 15;
            addMapsOptionLabel(rowIndex, offset, menu, "Grass spawning Each Day");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex = 16;
            addMapsOptionLabel(rowIndex, offset, menu, "Animals Amount For Cloning");
            addMapsOptionTextField(rowIndex, offset, menu, 5, inputFields);

//            OTHERS
            rowIndex = 17;
            addMapsOptionHeader(rowIndex, offset, menu, "Others");

            rowIndex = 18;
            addMapsOptionLabel(rowIndex, offset, menu, "Time Between Days (ms)");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);

            rowIndex = 19;
            addMapsOptionLabel(rowIndex, offset, menu, "Time Between Animals Moves (ms)");
            addMapsOptionTextField(rowIndex, offset, menu, 100, inputFields);
        }


        Button submit = new Button("Submit");
        menu.add(submit, 0, startingMenuRowsAmount-1, startingMenuColsAmount, 1);
        GridPane.setHalignment(submit, HPos.CENTER);

        window.setTitle("Simulation (Settings)");
        window.setScene(startingMenu);
    }

    private void addMapsOptionHeader(int rowIndex, int offset, GridPane menu, String text){
        Label header = new Label(text);
        menu.add(header, offset, rowIndex, 2, 1);
        GridPane.setHalignment(header, HPos.CENTER);
    }

    private void addMapsOptionLabel(int rowIndex, int offset, GridPane menu, String text){
        Label label = new Label(text);
        menu.add(label, offset, rowIndex);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setMargin(label, new Insets(5));
    }

    private void addMapsOptionTextField(int rowIndex, int offset, GridPane menu, int value, List<TextField> inputFields){
        TextField field = new TextField(String.valueOf(value));
        menu.add(field, offset+1, rowIndex);
        GridPane.setHalignment(field, HPos.LEFT);
        GridPane.setMargin(field, new Insets(5));
        inputFields.add(field);
    }



    public synchronized void drawGrid() throws FileNotFoundException {
        int rowSize = 30;
        int columnSize = 30;

        gridPane.setGridLinesVisible(false);
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.setGridLinesVisible(true);


        upperRight = map.getUpperRight();
        lowerLeft = map.getLowerLeft();

//        DRAW OBJECTS
        for (Vector2d position : changedPositions){
            removeDrawnObject(position);
            drawObject(position);
        }
    }

//    TODO
    @Override
    public void updateMap() {
        try {
            drawGrid();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    private int mapToGuiX(Vector2d position){
        return -lowerLeft.x+position.x;
    }
    private int mapToGuiY(Vector2d position){
        return upperRight.y-position.y;
    }


    private synchronized void removeDrawnObject(Vector2d position){
        if (drawnElementsPositions.containsKey(position)) {
            gridPane.getChildren().remove(drawnElementsPositions.get(position).getVbox());
            drawnElementsPositions.remove(position);
        }
    }

    private synchronized void drawObject(Vector2d position) throws FileNotFoundException {
        if (!drawnElementsPositions.containsKey(position) && map.isOccupied(position)){
            GuiElementBox elementBox = new GuiElementBox(map.objectAt(position));
            gridPane.add(elementBox.getVbox(), mapToGuiX(position), mapToGuiY(position));
            GridPane.setHalignment(elementBox.getVbox(), HPos.CENTER);

            drawnElementsPositions.put(position, elementBox);
        }
    }

    @Override
    public synchronized void elementMovedFromPosition(Vector2d oldPosition, AbstractWorldElement element) {
        changedPositions.add(oldPosition);
        changedPositions.add(element.getPosition());
    }

    @Override
    public synchronized void elementAdded(AbstractWorldElement element) {
        changedPositions.add(element.getPosition());
    }

    @Override
    public synchronized void elementRemoved(AbstractWorldElement element) {
        changedPositions.add(element.getPosition());
    }
}
