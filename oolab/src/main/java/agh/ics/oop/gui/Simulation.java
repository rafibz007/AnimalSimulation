package agh.ics.oop.gui;

import agh.ics.oop.engines.SimulationEngine;
import agh.ics.oop.interfaces.IMapObserver;
import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.mapElements.AbstractWorldElement;
import agh.ics.oop.mapElements.Vector2d;
import agh.ics.oop.maps.WorldMap;
import javafx.application.Platform;
import javafx.geometry.HPos;
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

    private final Vector2d lowerLeft;
    private final Vector2d upperRight;

    private final Set<Vector2d> changedPositions = new HashSet<>();
    private final Map<Vector2d, GuiElementBox> drawnElementsPositions = new HashMap<>();

    private final Map<String, Integer> parameters;
    private final Set<String> parameterNames;

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

    Simulation(WorldMap map, SimulationEngine engine, Map<String, Integer> parameters, Set<String> parameterNames){
        this.map = map;
        this.engine = engine;
        this.parameters = parameters;
        this.parameterNames = parameterNames;

        lowerLeft = map.getLowerLeft();
        upperRight = map.getUpperRight();

        this.engine.addObserver(this);
        map.addObserverForAnimals(this);
        map.addAmountOfAnimalsToMap(
                parameters.get("animalsAtStart")
        );

//        GUI PREPARATIONS
        drawGrid();
        simulationPane.add(mapPane, 0, 0);

//        System.out.println(map);
        start();
    }


    public synchronized void start(){
        new Thread(this.engine).start();
    }


    public synchronized void drawGrid() {
        int rowSize = 30;
        int columnSize = 30;

        mapPane.getChildren().clear();
        mapPane.getColumnConstraints().clear();
        mapPane.getRowConstraints().clear();


        for (int i=0; i<upperRight.x- lowerLeft.x+1; i++)
            mapPane.getColumnConstraints().add(new ColumnConstraints(columnSize));

        for (int i=0; i< upperRight.y- lowerLeft.y+1; i++)
            mapPane.getRowConstraints().add(new RowConstraints(rowSize));

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
            GuiElementBox elementBox = new GuiElementBox(map.objectAt(position), 25);
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
