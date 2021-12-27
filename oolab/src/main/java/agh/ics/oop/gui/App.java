package agh.ics.oop.gui;

import agh.ics.oop.engines.SimulationEngine;
import agh.ics.oop.interfaces.IMapObserver;
import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.mapElements.AbstractWorldElement;
import agh.ics.oop.mapElements.Vector2d;
import agh.ics.oop.maps.WorldMap;
import agh.ics.oop.statistics.Statistics;
import com.sun.scenario.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jdk.jfr.EventType;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.MouseEvent;

import java.io.FileNotFoundException;
import java.util.*;

public class App extends Application implements IMapObserver, IPositionChangeObserver {

    WorldMap map;
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

    int amountOfMaps = 0;


    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setFullScreen(true);
        window.show();
        showMapChoosingMenu();
//        showMapsOptions(2);
        window.setScene(simulation);
        new Thread(engine).start();
    }



    @Override
    public void init() throws FileNotFoundException {
//        SETTING UP ENGINE
        map = new WorldMap(30, 30, 10, 10, 40, 100,5, 80, true);
        Statistics statistics = new Statistics();
        engine = new SimulationEngine(map, 10, 1, statistics);
        map.addObserverForAnimals(this);
        engine.addObserver(this);


        gridPane = new GridPane();
        simulation = new Scene(gridPane, 400, 400);
        drawGrid();

        map.addAmountOfAnimalsToMap(800);
        map.addAmountOfGrassToJungle(50);
        map.addAmountOfGrassToStep(50);

//        new Thread(engine);


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

    public void showSimulation(int amountOfMaps, List<Map<String, Integer>> inputs, Set<String> inputNames){
        System.out.println(inputs);
    }

    public void showMapChoosingMenu(){
        GridPane menu = new GridPane();
        menu.setAlignment(Pos.CENTER);

        VBox vbox = new VBox();
        vbox.getChildren().add(new Label("How many maps do you want to simulate?"));

        RadioButton radioButton1 = new RadioButton("1");
        RadioButton radioButton2 = new RadioButton("2");


        ToggleGroup toggleGroup = new ToggleGroup();

        radioButton1.setToggleGroup(toggleGroup);
        radioButton2.setToggleGroup(toggleGroup);


        HBox hBox = new HBox();
        hBox.getChildren().addAll(radioButton1, radioButton2);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(30);

        vbox.getChildren().add(hBox);


        Button button = new Button("OK");
        button.setOnAction( (ActionEvent event) ->{
            if ((toggleGroup.getSelectedToggle()) == null)
                return;

            amountOfMaps = Integer.parseInt(((RadioButton) toggleGroup.getSelectedToggle()).getText());

            showMapsOptions(amountOfMaps);
        } );
        vbox.getChildren().add(button);
        vbox.setAlignment(Pos.CENTER);

        menu.getChildren().add(vbox);

        mapChoosingMenu = new Scene(menu, 600, 300);

        window.setTitle("Simulation (Settings)");
        window.setScene(mapChoosingMenu);

    }


    public void showMapsOptions(int amountOfMaps){
        int startingMenuWidth = 600*amountOfMaps;
        int startingMenuHeight = 800;

        int startingMenuColsAmount = 2*amountOfMaps;
        int startingMenuRowsAmount = 25;
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

        List<Map<String, Integer>> inputs = new ArrayList<>();
        Set<String> inputsNames = new HashSet<>();
        for (int j=0; j<amountOfMaps; j++){
            inputs.add(new HashMap<>());
            int offset = j*2;

//            HEADER
            int rowIndex = 0;
            addMapsOptionHeader(rowIndex, offset, menu, "Map Details");

//            MAP PROPERTIES
            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "Map Properties");

            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "(values must be integers greater than 0)");

            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "(map width/height must be greater than jungle width/height by at least 2)");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Map height");
            addMapsOptionTextField(rowIndex, offset, menu, 100, 1, "mapHeight", inputs.get(j));
            inputsNames.add("mapHeight");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Map width");
            addMapsOptionTextField(rowIndex, offset, menu, 100, 1, "mapWidth", inputs.get(j));
            inputsNames.add("mapWidth");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Jungle height");
            addMapsOptionTextField(rowIndex, offset, menu, 25, 1, "jungleHeight", inputs.get(j));
            inputsNames.add("jungleHeight");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Jungle width");
            addMapsOptionTextField(rowIndex, offset, menu, 25, 1, "jungleWidth", inputs.get(j));
            inputsNames.add("jungleWidth");

            rowIndex += 1;
            addTwoRadioButtonsWithOptions(rowIndex, offset, menu, "Darwin World", "Magic World", 0, 1, "worldType", inputs.get(j));
            inputsNames.add("worldType");

            rowIndex += 1;
            addTwoRadioButtonsWithOptions(rowIndex, offset, menu, "Wall Border", "Wrapped Border", 0, 1, "borderType", inputs.get(j));
            inputsNames.add("borderType");

//            ENERGY PROPERTIES
            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "Energy Properties");

            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "(values must be integers greater or equal to 0)");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Grass energy profit");
            addMapsOptionTextField(rowIndex, offset, menu, 10, 0, "grassEnergy", inputs.get(j));
            inputsNames.add("grassEnergy");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Minimum energy for copulation");
            addMapsOptionTextField(rowIndex, offset, menu, 10, 0, "minCopulationEnergy", inputs.get(j));
            inputsNames.add("minCopulationEnergy");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Animal's starting energy");
            addMapsOptionTextField(rowIndex, offset, menu, 50, 0, "startingEnergy", inputs.get(j));
            inputsNames.add("startingEnergy");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Animal's maximum energy");
            addMapsOptionTextField(rowIndex, offset, menu, 60, 0,"maxEnergy", inputs.get(j));
            inputsNames.add("maxEnergy");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Daily energy loss");
            addMapsOptionTextField(rowIndex, offset, menu, 5, 0, "dailyLoss", inputs.get(j));
            inputsNames.add("dailyLoss");

//            SPAWNING PROPERTIES
            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "Spawning Properties");

            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "(values must be integers greater or equal to 0)");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Animals at start");
            addMapsOptionTextField(rowIndex, offset, menu, 300, 0, "animalsAtStart", inputs.get(j));
            inputsNames.add("animalsAtStart");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Grass spawning each day at biomes");
            addMapsOptionTextField(rowIndex, offset, menu, 2, 0, "grassEachDay", inputs.get(j));
            inputsNames.add("grassEachDay");

//            OTHERS
            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "Others");

            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "(values must be integers greater than 0)");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Time between days (ms)");
            addMapsOptionTextField(rowIndex, offset, menu, 100, 1, "dayDelay", inputs.get(j));
            inputsNames.add("dayDelay");

        }


        Button submit = new Button("Submit");
        submit.setOnAction( (ActionEvent event) ->{
//            CHECK INPUTS
            if (!inputsNames.contains("mapWidth") ||
                    !inputsNames.contains("mapHeight") ||
                    !inputsNames.contains("jungleWidth") ||
                    !inputsNames.contains("jungleHeight"))
                throw new IllegalArgumentException("Expected inputs were not passed");

            for (Map<String, Integer> mapInputs : inputs){
                for (Integer value : mapInputs.values()){
                    if (value == null)
                        return;
                }

                if (mapInputs.get("mapWidth") - mapInputs.get("jungleWidth") < 2 ||
                        mapInputs.get("mapHeight") - mapInputs.get("jungleHeight") < 2)
                    return;
            }



//            START SIMULATIONS
            showSimulation(amountOfMaps, inputs, inputsNames);
        } );
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

    private void addMapsOptionTextField(int rowIndex, int offset, GridPane menu, int value, int minVal, String fieldName, Map<String, Integer> inputs){
        TextField field = new TextField(String.valueOf(value));
        menu.add(field, offset+1, rowIndex);
        GridPane.setHalignment(field, HPos.LEFT);
        GridPane.setMargin(field, new Insets(5));
        inputs.put(fieldName, value);

        field.textProperty().addListener(((observable, oldValue, newValue) -> {
            try {
                int fieldValue = Integer.parseInt(newValue);
                if (fieldValue >= minVal)
                    inputs.put(fieldName, fieldValue);
                else
                    inputs.put(fieldName, null);
            } catch (NumberFormatException e){
                inputs.put(fieldName, null);
            }

//            System.out.println(inputs.get(fieldName));
        }));
    }

    private void addTwoRadioButtonsWithOptions(int rowIndex, int offset, GridPane menu, String name1, String name2, int value1, int value2, String fieldName, Map<String, Integer> inputs){
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton radioButton1 = new RadioButton(name1);
        RadioButton radioButton2 = new RadioButton(name2);
        radioButton1.setToggleGroup(toggleGroup);
        radioButton2.setToggleGroup(toggleGroup);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(radioButton1, radioButton2);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(30);
        GridPane.setHalignment(hBox, HPos.CENTER);

        radioButton1.setSelected(true);
        inputs.put(fieldName, value1);

        toggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            String selectedName = (((RadioButton) toggleGroup.getSelectedToggle()).getText());

            if (selectedName.equals(name1))
                inputs.put(fieldName, value1);
            else
                inputs.put(fieldName, value2);

        }));

        menu.add(hBox, offset, rowIndex, 2, 1);
    }

    public synchronized void drawGrid() throws FileNotFoundException {
        int rowSize = 30;
        int columnSize = 30;

        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();


        upperRight = map.getUpperRight();
        lowerLeft = map.getLowerLeft();

        for (int i=0; i<upperRight.x- lowerLeft.x+1; i++)
            gridPane.getColumnConstraints().add(new ColumnConstraints(columnSize));

        for (int i=0; i< upperRight.y- lowerLeft.y+1; i++)
            gridPane.getRowConstraints().add(new RowConstraints(rowSize));

        String jungleColor = "#197036";
        String stepColor = "#72b35b";
        for (int i=0; i<upperRight.x- lowerLeft.x+1; i++){
            for (int j=0; j< upperRight.y- lowerLeft.y+1; j++){
                Vector2d position = new Vector2d(i,j);
                if (map.isInJungle(position)){
                    TilePane tilePane = new TilePane();
                    tilePane.setStyle("-fx-background-color: " + jungleColor + ";");
                    gridPane.add(tilePane, mapToGuiX(position), mapToGuiY(position));
                } else {
                    TilePane tilePane = new TilePane();
                    tilePane.setStyle("-fx-background-color: " + stepColor + ";");
                    gridPane.add(tilePane, mapToGuiX(position), mapToGuiY(position));
                }
            }
        }
//        DRAW OBJECTS
        for (Vector2d position : changedPositions){
            removeDrawnObject(position);
            drawObject(position);
        }
    }

    public synchronized void updateGrid() throws FileNotFoundException {
        for (Vector2d position : changedPositions){
            removeDrawnObject(position);
            drawObject(position);
        }
    }

//    TODO
    @Override
    public void updateMap() {
        Platform.runLater(()->{
            try {
                updateGrid();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
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
            GuiElementBox elementBox = new GuiElementBox(map.objectAt(position), 25);
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
