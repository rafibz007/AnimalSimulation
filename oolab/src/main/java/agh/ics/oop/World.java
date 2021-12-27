package agh.ics.oop;
import agh.ics.oop.engines.SimulationEngine;
import agh.ics.oop.gui.App;
import agh.ics.oop.maps.WorldMap;
import agh.ics.oop.statistics.Statistics;
import javafx.application.Application;

import java.lang.reflect.Method;

import static java.lang.System.out;

public class World {

    public static void main(String[] args){
       try{

           Application.launch(App.class, args);

//           WorldMap map = new WorldMap(10, 10, 5, 5, 10, 50, 10, 50, false);
//           Statistics statistics = new Statistics();
//           map.addObserverForAnimals(statistics);
//           SimulationEngine engine = new SimulationEngine(map, 0, 5, statistics);


//           map.addAmountOfAnimalsToMap(80);
//           map.addAmountOfGrassToStep(5);
//           map.addAmountOfGrassToJungle(5);
//           out.println(map);

//           engine.run();


       } catch( IllegalArgumentException exception ) {
           out.println(exception);
           exception.printStackTrace();
       }

    }

}

