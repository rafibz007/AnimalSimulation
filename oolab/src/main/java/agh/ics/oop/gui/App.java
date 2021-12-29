package agh.ics.oop.gui;

import agh.ics.oop.engines.SimulationEngine;
import agh.ics.oop.maps.MagicWorldMap;
import agh.ics.oop.maps.WorldMap;
import agh.ics.oop.statistics.Statistics;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.List;

public class App extends Application  {

    Stage window;
    Scene simulation;
    Scene startingMenu;
    Scene mapChoosingMenu;

    int amountOfMaps = 0;


    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.show();
        showMapChoosingMenu();

    }

    public void showSimulation(int amountOfMaps, List<Map<String, Integer>> inputsList, Set<String> inputNames) {
        int startingWidth = 900*amountOfMaps;
        int startingHeight = 900;

        GridPane gridPane = new GridPane();

        for (int j=0; j<amountOfMaps; j++){
            gridPane.getColumnConstraints().addAll(new ColumnConstraints((float) startingWidth/amountOfMaps));
        }


        RowConstraints row = new RowConstraints();
        row.setPercentHeight(100);
        gridPane.getRowConstraints().addAll(row);

        gridPane.setGridLinesVisible(true);



        for (int j=0; j<amountOfMaps; j++){
            Statistics statistics = new Statistics("mapStatistics" + (j+1) + ".csv");

            Map<String, Integer> inputs = inputsList.get(j);

            boolean isMagic = inputs.get("worldType") == 1;
            WorldMap map;
            if (isMagic)
                map = new MagicWorldMap(
                        inputs.get("mapHeight"),
                        inputs.get("mapWidth"),
                        inputs.get("jungleHeight"),
                        inputs.get("jungleWidth"),
                        inputs.get("grassEnergy"),
                        inputs.get("startingEnergy"),
                        inputs.get("minCopulationEnergy"),
                        inputs.get("maxEnergy"),
                        inputs.get("borderType") == 1
                );
            else
                map = new WorldMap(
                        inputs.get("mapHeight"),
                        inputs.get("mapWidth"),
                        inputs.get("jungleHeight"),
                        inputs.get("jungleWidth"),
                        inputs.get("grassEnergy"),
                        inputs.get("startingEnergy"),
                        inputs.get("minCopulationEnergy"),
                        inputs.get("maxEnergy"),
                        inputs.get("borderType") == 1
                );
            map.addObserverForAnimals(statistics);

            SimulationEngine engine = new SimulationEngine(
                    map,
                    inputs.get("dayDelay"),
                    inputs.get("dailyLoss"),
                    inputs.get("grassEachDay")
            );

            Simulation simulation = new Simulation(map, engine, inputs, inputNames, statistics);


            gridPane.add(simulation.simulationPane, j, 0);
        }



        simulation = new Scene(gridPane, startingWidth, startingHeight);
        window.setTitle("Simulation");
        window.setScene(simulation);
        window.show();
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

        radioButton1.setSelected(true);
        amountOfMaps = Integer.parseInt(((RadioButton) toggleGroup.getSelectedToggle()).getText());

        HBox hBox = new HBox();
        hBox.getChildren().addAll(radioButton1, radioButton2);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(30);

        vbox.getChildren().add(hBox);


        Button button = new Button("OK");
        button.setOnAction( (ActionEvent event) ->{

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
            addMapsOptionTextField(rowIndex, offset, menu, 30, 1, "mapHeight", inputs.get(j));
            inputsNames.add("mapHeight");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Map width");
            addMapsOptionTextField(rowIndex, offset, menu, 30, 1, "mapWidth", inputs.get(j));
            inputsNames.add("mapWidth");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Jungle height");
            addMapsOptionTextField(rowIndex, offset, menu, 10, 1, "jungleHeight", inputs.get(j));
            inputsNames.add("jungleHeight");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Jungle width");
            addMapsOptionTextField(rowIndex, offset, menu, 10, 1, "jungleWidth", inputs.get(j));
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
            addMapsOptionTextField(rowIndex, offset, menu, 40, 0, "grassEnergy", inputs.get(j));
            inputsNames.add("grassEnergy");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Minimum energy for copulation");
            addMapsOptionTextField(rowIndex, offset, menu, 5, 0, "minCopulationEnergy", inputs.get(j));
            inputsNames.add("minCopulationEnergy");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Animal's starting energy");
            addMapsOptionTextField(rowIndex, offset, menu, 100, 0, "startingEnergy", inputs.get(j));
            inputsNames.add("startingEnergy");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Animal's maximum energy");
            addMapsOptionTextField(rowIndex, offset, menu, 80, 0,"maxEnergy", inputs.get(j));
            inputsNames.add("maxEnergy");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Daily energy loss");
            addMapsOptionTextField(rowIndex, offset, menu, 1, 0, "dailyLoss", inputs.get(j));
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
            addMapsOptionLabel(rowIndex, offset, menu, "Grass spawning each day at each biome");
            addMapsOptionTextField(rowIndex, offset, menu, 1, 0, "grassEachDay", inputs.get(j));
            inputsNames.add("grassEachDay");

//            OTHERS
            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "Others");

            rowIndex += 1;
            addMapsOptionHeader(rowIndex, offset, menu, "(values must be integers greater than 0)");

            rowIndex += 1;
            addMapsOptionLabel(rowIndex, offset, menu, "Time between days (ms)");
            addMapsOptionTextField(rowIndex, offset, menu, 30, 1, "dayDelay", inputs.get(j));
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


}
