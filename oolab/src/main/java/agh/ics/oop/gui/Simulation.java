package agh.ics.oop.gui;

import agh.ics.oop.engines.SimulationEngine;
import agh.ics.oop.interfaces.IEngineObserver;
import agh.ics.oop.interfaces.IMapElementsObserver;
import agh.ics.oop.mapElements.*;
import agh.ics.oop.maps.WorldMap;
import agh.ics.oop.statistics.AnimalDetails;
import agh.ics.oop.statistics.Statistics;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

// todo w pewnym momencie wszystkie zwierzeta przejmuja jeden gen, naprawic :((
public class Simulation implements IEngineObserver, IMapElementsObserver {

    private final ReentrantLock changedPositionsLock = new ReentrantLock();

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
    private final GridPane simulationButtonsPane = new GridPane();

    XYChart.Series<Number, Number> animalAmountSeries;
    XYChart.Series<Number, Number> grassAmountSeries;
    XYChart.Series<Number, Number> averageLifeLengthSeries;
    XYChart.Series<Number, Number> averageEnergySeries;
    XYChart.Series<Number, Number> averageChildrenAmountSeries;

    Label dominantGene;
    Label animalsAmount;
    Label grassAmount;
    Label era;

    VBox logs;

    Label detailGene;
    Label detailChildrenAmount;
    Label detailOffspringAmount;
    Label detailEraOfDeath;

    Button startStopButton;
    Button saveStatistics;
    Button showAnimalsWithDominantGenotypeButton;

    boolean mapRunning = false;

    private Gene dominantGenotype;

    boolean animalsHighlighted = false;

    private AnimalDetails animalDetails = new AnimalDetails(null);

    private final int plotAmountOfEras = 300;

    Simulation(WorldMap map, SimulationEngine engine, Map<String, Integer> parameters, Set<String> parameterNames, Statistics statistics){
        this.map = map;
        this.engine = engine;
        this.parameters = parameters;
        this.parameterNames = parameterNames;
        this.statistics = statistics;

        mapBoxSize = calculateMapBoxSize();

        lowerLeft = map.getLowerLeft();
        upperRight = map.getUpperRight();

        this.engine.addEngineObserver(this);
        this.engine.addEngineObserver(statistics);
        map.addObserverForAnimals(this);

        map.addAmountOfAnimalsToMapAtStart(
                parameters.get("animalsAtStart")
        );

//        GUI PREPARATIONS
//        simulationPane.setGridLinesVisible(true);

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

//        MAP
        drawGrid();
        simulationPane.add(mapPane, 1, 0, 2, 3);
        mapPane.setAlignment(Pos.CENTER);




//        LEGEND
        String jungleColor = "#197036";
        String stepColor = "#72b35b";
        Rectangle tilePane;

        VBox legend = new VBox();
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10));
        legend.setSpacing(10);

        HBox animal = new HBox();
        animal.setAlignment(Pos.CENTER_LEFT);
        animal.setSpacing(10);
        animal.getChildren().addAll(
                new Animal(map, new Vector2d(0,0), map.maxAnimalEnergy/2, 0).guiRepresentation(30),
                new Label("Animal")
        );

        HBox grass = new HBox();
        grass.setAlignment(Pos.CENTER_LEFT);
        grass.setSpacing(10);
        grass.getChildren().addAll(
                new Grass(new Vector2d(0,0), 0).guiRepresentation(30),
                new Label("Grass")
        );

        HBox jungle = new HBox();
        jungle.setAlignment(Pos.CENTER_LEFT);
        jungle.setSpacing(10);
        tilePane = new Rectangle(30, 30);
        tilePane.setFill(Color.web(jungleColor));
        jungle.getChildren().addAll(
                tilePane,
                new Label("Jungle")
        );

        HBox step = new HBox();
        step.setAlignment(Pos.CENTER_LEFT);
        step.setSpacing(10);
        tilePane = new Rectangle(30, 30);
        tilePane.setFill(Color.web(stepColor));
        tilePane.setStyle("-fx-background-color: " + stepColor + ";");
        step.getChildren().addAll(
                tilePane,
                new Label("Step")
        );

        legend.getChildren().addAll(
                step,
                jungle,
                grass,
                animal,
                new Label("(the darker color, the more energy)")
        );

        simulationPane.add(legend, 0, 0);



//        DOMINANT GENOTYPE BUTTONS
        showAnimalsWithDominantGenotypeButton = new Button("Highlight dominant genotype");

        HBox dominantButtonHBox = new HBox();
        dominantButtonHBox.getChildren().add(showAnimalsWithDominantGenotypeButton);
        dominantButtonHBox.setAlignment(Pos.CENTER);

        showAnimalsWithDominantGenotypeButton.setOnAction( (ActionEvent event) -> {
            if (mapRunning)
                return;

            if (animalsHighlighted) {
                showAnimalsWithDominantGenotypeButton.setText("Highlight dominant genotype");
                unhighlightAnimals();
            }
            else{
                showAnimalsWithDominantGenotypeButton.setText("Unhighlight dominant genotype");
                if (dominantGenotype == null)
                    return;
                highlightAnimalsWithGenotype(dominantGenotype);
            }

            animalsHighlighted = !animalsHighlighted;
        } );


        simulationPane.add(dominantButtonHBox, 1, 3);


        //        SIMULATION BUTTONS
        simulationPane.add(simulationButtonsPane, 2, 3, 1, 1);
        simulationButtonsPane.setAlignment(Pos.CENTER);

        simulationButtonsPane.getRowConstraints().add(new RowConstraints(100));
        simulationButtonsPane.getColumnConstraints().add(new ColumnConstraints(200));

        startStopButton = new Button("Start");
        saveStatistics = new Button("Save");

        startStopButton.setAlignment(Pos.CENTER);
        saveStatistics.setAlignment(Pos.CENTER);

        simulationButtonsPane.add(startStopButton, 0, 0);
        simulationButtonsPane.add(saveStatistics, 1, 0);

        startStopButton.setOnAction( (ActionEvent event) ->{
            if (mapRunning){
                engine.pause();
                startStopButton.setText("Resume");
            } else {
                animalsHighlighted = false;
                unhighlightAnimals();
                showAnimalsWithDominantGenotypeButton.setText("Highlight dominant genotype");
                engine.resume();
                startStopButton.setText("Pause");
            }
            mapRunning = !mapRunning;

        } );

        saveStatistics.setOnAction( (ActionEvent event) -> {
            if (mapRunning)
                return;

            Platform.runLater(statistics.statisticsSaver::save);
        } );


//        LOGS
        logs = new VBox();
        logs.setAlignment(Pos.TOP_CENTER);
        simulationPane.add(logs, 2, 4);

//        AMOUNT PLOT
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> amountLineChart = new LineChart<>(xAxis, yAxis);
        animalAmountSeries = new XYChart.Series<>();
        grassAmountSeries = new XYChart.Series<>();
        animalAmountSeries.setName("Animal");
        grassAmountSeries.setName("Grass");
        amountLineChart.getData().addAll(animalAmountSeries, grassAmountSeries);
        amountLineChart.setStyle("-fx-font-size: " + 10 + "px;");
        amountLineChart.setCreateSymbols(false);
        amountLineChart.setAnimated(false);
        updateAmountPlot(0);

//        test
        xAxis.setTickLabelsVisible(false);

        simulationPane.add(amountLineChart, 0, 1);


//        AVERAGE PLOT
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        LineChart<Number, Number> averageLineChart = new LineChart<>(xAxis, yAxis);
        averageLifeLengthSeries = new XYChart.Series<>();
        averageEnergySeries = new XYChart.Series<>();
        averageChildrenAmountSeries = new XYChart.Series<>();
        averageLifeLengthSeries.setName("Avg Life");
        averageEnergySeries.setName("Avg Energy");
        averageChildrenAmountSeries.setName("Avg Children");
        averageLineChart.getData().addAll(averageLifeLengthSeries, averageEnergySeries, averageChildrenAmountSeries);
        averageLineChart.setStyle("-fx-font-size: " + 10 + "px;");
        averageLineChart.setCreateSymbols(false);
        averageLineChart.setAnimated(false);
        updateAveragePlot(0);

//        test
        xAxis.setTickLabelsVisible(false);

        simulationPane.add(averageLineChart, 0, 2);


//        DOMINANT GENE
        VBox dominantGeneVBox = new VBox();
        dominantGeneVBox.setAlignment(Pos.CENTER);
        dominantGene = new Label("none");
        grassAmount = new Label("0");
        animalsAmount = new Label("0");
        era = new Label("0");
        dominantGeneVBox.getChildren().addAll(
                new Label("Dominant genotype:"),
                new Label("(0 amount : ... : 7 amount <-> amount)"),
                dominantGene,
                new Label(""),
                new Label("Animal amount:"),
                animalsAmount,
                new Label(""),
                new Label("Grass amount:"),
                grassAmount,
                new Label(""),
                new Label("Era:"),
                era
        );
        updateStatistics();

        simulationPane.add(dominantGeneVBox, 0, 3, 1, 2);


//        ANIMAL DETAILS
        VBox animalDetails = new VBox();
        animalDetails.setAlignment(Pos.TOP_CENTER);

        detailGene = new Label("-");
        detailChildrenAmount = new Label("-");
        detailOffspringAmount = new Label("-");
        detailEraOfDeath = new Label("-");
        animalDetails.getChildren().addAll(
                new Label("Animal's genotype:"),
                detailGene,
                new Label(""),
                new Label("Animal's children amount:"),
                detailChildrenAmount,
                new Label(""),
                new Label("Animal's offspring amount: "),
                detailOffspringAmount,
                new Label(""),
                new Label("Animal's era of death:"),
                detailEraOfDeath
        );

        simulationPane.add(animalDetails, 1, 4);


        start();
    }


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
        changedPositionsLock.lock();
        try {
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
        } finally {
            changedPositionsLock.unlock();
        }
    }

    public synchronized void updateGrid(){
        changedPositionsLock.lock();
        try {
            for (Vector2d position : changedPositions){
                removeDrawnObject(position);
                drawObject(position);
            }
            changedPositions.clear();
        } finally {
            changedPositionsLock.unlock();
        }
    }

    public synchronized void updateGridWithHighlighting(Gene gene){
        changedPositionsLock.lock();
        try {
            for (Vector2d position : changedPositions){
                removeDrawnObject(position);
                drawObject(position, gene);
            }
            changedPositions.clear();
        } finally {
            changedPositionsLock.unlock();
        }
    }

    public synchronized void updateAmountPlot(int era){
//        test
        if (animalAmountSeries.getData().size() > plotAmountOfEras) animalAmountSeries.getData().remove(0);
        if (grassAmountSeries.getData().size() > plotAmountOfEras) grassAmountSeries.getData().remove(0);

        animalAmountSeries.getData().add(new XYChart.Data<>(era%plotAmountOfEras, statistics.getAmountOfAnimals()));
        grassAmountSeries.getData().add(new XYChart.Data<>(era%plotAmountOfEras, statistics.getAmountOfGrass()));

//        working
//        animalAmountSeries.getData().add(new XYChart.Data<>(era, statistics.getAmountOfAnimals()));
//        grassAmountSeries.getData().add(new XYChart.Data<>(era, statistics.getAmountOfGrass()));
    }

    public synchronized void updateAveragePlot(int era){
//        test
        if (averageLifeLengthSeries.getData().size() > plotAmountOfEras) averageLifeLengthSeries.getData().remove(0);
        if (averageEnergySeries.getData().size() > plotAmountOfEras) averageEnergySeries.getData().remove(0);
        if (averageChildrenAmountSeries.getData().size() > plotAmountOfEras) averageChildrenAmountSeries.getData().remove(0);

        averageLifeLengthSeries.getData().add(new XYChart.Data<>(era%plotAmountOfEras, statistics.getAverageLifeLength()));
        averageEnergySeries.getData().add(new XYChart.Data<>(era%plotAmountOfEras, statistics.getAverageEnergy()));
        averageChildrenAmountSeries.getData().add(new XYChart.Data<>(era%plotAmountOfEras, statistics.getAverageChildrenAmount()));

//        working
//        averageLifeLengthSeries.getData().add(new XYChart.Data<>(era, statistics.getAverageLifeLength()));
//        averageEnergySeries.getData().add(new XYChart.Data<>(era, statistics.getAverageEnergy()));
//        averageChildrenAmountSeries.getData().add(new XYChart.Data<>(era, statistics.getAverageChildrenAmount()));
    }

    public synchronized void updateStatistics(){
        animalsAmount.setText(String.valueOf(statistics.getAmountOfAnimals()));
        grassAmount.setText(String.valueOf(statistics.getAmountOfGrass()));
        era.setText(String.valueOf(engine.getEra()));

        Set<Gene> genes = statistics.getDominantGenes();
        if (genes.size() > 0){
            Gene gene = new ArrayList<>(genes).get(0);
            dominantGene.setText( gene.toString() + " <-> " + statistics.amountOfGene(gene));
            this.dominantGenotype = gene;
        } else
            dominantGene.setText("none");
    }

    public synchronized void updateDetails(){
        detailChildrenAmount.setText(animalDetails.getChildrenAmount());
        detailGene.setText(animalDetails.getAnimalGene());
        detailEraOfDeath.setText(animalDetails.getEraOfDeath());
        detailOffspringAmount.setText(animalDetails.getOffspringAmount());
    }

    public void updateSimulation() {
        Platform.runLater(this::updateGrid);
        Platform.runLater(() -> updateAmountPlot(engine.getEra()));
        Platform.runLater(() -> updateAveragePlot(engine.getEra()));
        Platform.runLater(this::updateStatistics);
        Platform.runLater(this::updateDetails);
    }

    @Override
    public synchronized void dayEnded() {
        updateSimulation();
    }

    @Override
    public synchronized void magicHappened() {
        Platform.runLater(()->{
            String message = "Magic happened. Era: " + engine.getEra();
            logs.getChildren().add(
                    new Label(message)
            );
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

            if (map.objectAt(position) instanceof Animal animal)
                addFunctionalityToGuiBox(elementBox, animal);

            mapPane.add(elementBox.getVbox(), mapToGuiX(position), mapToGuiY(position));
            GridPane.setHalignment(elementBox.getVbox(), HPos.CENTER);

            drawnElementsPositions.put(position, elementBox);
        }
    }

    private synchronized void drawObject(Vector2d position, Gene highlightGene) {
        if (!drawnElementsPositions.containsKey(position) && map.isOccupied(position)){
            GuiElementBox elementBox = null;
            if (map.animalIsAt(position)){
                boolean found = false;
                for (Animal animal : map.allAnimalsAt(position)){
                    if (animal.gene.equals(highlightGene)){
                        elementBox = new GuiElementBox(animal, mapBoxSize, true);
                        found = true;
                        break;
                    }
                }

                if (!found)
                    elementBox = new GuiElementBox(map.objectAt(position), mapBoxSize);
            } else {
                elementBox = new GuiElementBox(map.objectAt(position), mapBoxSize);
            }

            if (map.objectAt(position) instanceof Animal animal)
                addFunctionalityToGuiBox(elementBox, animal);

            mapPane.add(elementBox.getVbox(), mapToGuiX(position), mapToGuiY(position));
            GridPane.setHalignment(elementBox.getVbox(), HPos.CENTER);

            drawnElementsPositions.put(position, elementBox);
        }
    }

    private void clearDetailObservers(){
        for (Animal animal : map.allAnimals()) {
            animal.clearDetailObservers();
            animal.hasDetailsTracked = false;
        }
    }

    private void addFunctionalityToGuiBox(GuiElementBox box, Animal animal){
        box.getVbox().setOnMouseClicked( (event) -> {
            eventFunction(box, animal);
        } );
    }

    private synchronized void eventFunction(GuiElementBox box, Animal animal){
        if (mapRunning)
            return;

        changedPositionsLock.lock();
        try {
            animalsHighlighted = false;
            unhighlightAnimals();
            showAnimalsWithDominantGenotypeButton.setText("Highlight dominant genotype");

            clearDetailObservers();

            animalDetails = new AnimalDetails(animal);
            Platform.runLater(this::updateDetails);

            changedPositions.addAll(map.animalsPositionsSet());
            Platform.runLater(this::updateGrid);
        } finally {
            changedPositionsLock.unlock();
        }
    }

    @Override
    public synchronized void elementMovedFromPosition(Vector2d oldPosition, AbstractWorldElement element) {
        changedPositionsLock.lock();
        try {
            changedPositions.add(oldPosition);
            changedPositions.add(element.getPosition());
        } finally {
            changedPositionsLock.unlock();
        }
    }

    @Override
    public synchronized void elementAdded(AbstractWorldElement element) {
        changedPositionsLock.lock();
        try {
            changedPositions.add(element.getPosition());
        } finally {
            changedPositionsLock.unlock();
        }
    }

    @Override
    public synchronized void elementRemoved(AbstractWorldElement element) {
        changedPositionsLock.lock();
        try {
            changedPositions.add(element.getPosition());
        } finally {
            changedPositionsLock.unlock();
        }
    }

    @Override
    public void elementHasNewChild(AbstractWorldElement parent, AbstractWorldElement child) {
        changedPositionsLock.lock();
        try {
            changedPositions.add(parent.getPosition());
            changedPositions.add(child.getPosition());
        } finally {
            changedPositionsLock.unlock();
        }
    }

    @Override
    public void elementChangedEnergy(AbstractWorldElement element) {
        changedPositionsLock.lock();
        try {
            changedPositions.add(element.getPosition());
        } finally {
            changedPositionsLock.unlock();
        }
    }




    private void highlightAnimalsWithGenotype(Gene gene){
        changedPositionsLock.lock();
        try {
            changedPositions.addAll(map.animalsPositionsSet());
            updateGridWithHighlighting(gene);
        } finally {
            changedPositionsLock.unlock();
        }
    }

    private void unhighlightAnimals(){
        changedPositionsLock.lock();
        try {
            changedPositions.addAll(map.animalsPositionsSet());
            updateGrid();
        } finally {
            changedPositionsLock.unlock();
        }
    }
}
