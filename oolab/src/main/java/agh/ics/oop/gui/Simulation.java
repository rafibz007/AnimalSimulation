package agh.ics.oop.gui;

import agh.ics.oop.engines.SimulationEngine;
import agh.ics.oop.interfaces.IMapObserver;
import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.mapElements.AbstractWorldElement;
import agh.ics.oop.mapElements.Vector2d;
import agh.ics.oop.maps.WorldMap;
import agh.ics.oop.statistics.Statistics;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Simulation implements IMapObserver, IPositionChangeObserver {

    private final WorldMap map;
    private final SimulationEngine engine;
    private final int mapBoxSize;

    private final Vector2d lowerLeft;
    private final Vector2d upperRight;

    private final Set<Vector2d> changedPositions = new HashSet<>();
    private final Map<Vector2d, GuiElementBox> drawnElementsPositions = new HashMap<>();

    private final Map<String, Integer> parameters;
    private final Set<String> parameterNames;
    private final Statistics statistics;

    final GridPane simulationPane = new GridPane();
    private final GridPane mapPane = new GridPane();
    private final GridPane legendPane = new GridPane();
    private final GridPane amountPlotPane = new GridPane();
    private final GridPane averagePlotPane = new GridPane();
    private final GridPane statisticsPane = new GridPane();
    private final GridPane animalDetailsPane = new GridPane();
    private final GridPane animalDetailsButtonsPane = new GridPane();
    private final GridPane simulationButtonsPane = new GridPane();
    private final GridPane logsPane = new GridPane();

    Simulation(WorldMap map, SimulationEngine engine, Map<String, Integer> parameters, Set<String> parameterNames, Statistics statistics){
        this.map = map;
        this.engine = engine;
        this.parameters = parameters;
        this.parameterNames = parameterNames;
        this.statistics = statistics;

        mapBoxSize = calculateMapBoxSize();

        lowerLeft = map.getLowerLeft();
        upperRight = map.getUpperRight();

        this.engine.addObserver(this);
        map.addObserverForAnimals(this);
        map.addAmountOfAnimalsToMap(
                parameters.get("animalsAtStart")
        );

        simulationPane.setGridLinesVisible(true);
//        GUI PREPARATIONS

//        for (int i=0; i<3; i++){
//            ColumnConstraints col = new ColumnConstraints();
//            col.setPercentWidth((double) 1/3);
//            simulationPane.getColumnConstraints().add(col);
//        }
//
//        RowConstraints row1 = new RowConstraints();
//        row1.setPercentHeight((double) 2/9);
//
//        RowConstraints row2 = new RowConstraints();
//        row2.setPercentHeight((double) 2/9);
//
//        RowConstraints row3 = new RowConstraints();
//        row3.setPercentHeight((double) 2/9);
//
//        RowConstraints row4 = new RowConstraints();
//        row4.setPercentHeight((double) 1/9);
//
//        RowConstraints row5 = new RowConstraints();
//        row5.setPercentHeight((double) 2/9);

//        simulationPane.getRowConstraints().addAll(row1, row2, row3, row4, row5);


        simulationPane.getColumnConstraints().addAll(
                new ColumnConstraints(300),
                new ColumnConstraints(300),
                new ColumnConstraints(300)
        );
        simulationPane.getRowConstraints().addAll(
                new RowConstraints(200),
                new RowConstraints(200),
                new RowConstraints(200),
                new RowConstraints(100),
                new RowConstraints(200)
        );


        drawGrid();
        simulationPane.add(mapPane, 1, 0, 2, 3);
        mapPane.setAlignment(Pos.CENTER);

//        System.out.println(map);
        start();
    }

//    todo
    private int calculateMapBoxSize(){
        int mapHeight = parameters.get("mapHeight");
        int mapWidth = parameters.get("mapWidth");

        int longerSide = Math.max(mapHeight, mapWidth);

        return 600/longerSide;
    }

    public synchronized void start(){
        new Thread(this.engine).start();
    }


    public synchronized void drawGrid() {
        mapPane.getChildren().clear();
        mapPane.getColumnConstraints().clear();
        mapPane.getRowConstraints().clear();


        for (int i=0; i<upperRight.x- lowerLeft.x+1; i++)
            mapPane.getColumnConstraints().add(new ColumnConstraints(mapBoxSize));

        for (int i=0; i< upperRight.y- lowerLeft.y+1; i++)
            mapPane.getRowConstraints().add(new RowConstraints(mapBoxSize));

        String jungleColor = "#197036";
        String stepColor = "#72b35b";
        for (int i=0; i<upperRight.x- lowerLeft.x+1; i++){
            for (int j=0; j< upperRight.y- lowerLeft.y+1; j++){
                Vector2d position = new Vector2d(i,j);
                if (map.isInJungle(position)){
                    TilePane tilePane = new TilePane();
                    tilePane.setStyle("-fx-background-color: " + jungleColor + ";");
                    mapPane.add(tilePane, mapToGuiX(position), mapToGuiY(position));
                } else {
                    TilePane tilePane = new TilePane();
                    tilePane.setStyle("-fx-background-color: " + stepColor + ";");
                    mapPane.add(tilePane, mapToGuiX(position), mapToGuiY(position));
                }
            }
        }
//        DRAW OBJECTS
        for (Vector2d position : changedPositions){
            removeDrawnObject(position);
            drawObject(position);
        }
    }

    public synchronized void updateGrid(){
        for (Vector2d position : changedPositions){
            removeDrawnObject(position);
            drawObject(position);
        }
    }

    //    TODO
    @Override
    public void updateMap() {
        Platform.runLater(()->{
            updateGrid();
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
            mapPane.getChildren().remove(drawnElementsPositions.get(position).getVbox());
            drawnElementsPositions.remove(position);
        }
    }

    private synchronized void drawObject(Vector2d position) {
        if (!drawnElementsPositions.containsKey(position) && map.isOccupied(position)){
            GuiElementBox elementBox = new GuiElementBox(map.objectAt(position), mapBoxSize);
            mapPane.add(elementBox.getVbox(), mapToGuiX(position), mapToGuiY(position));
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
