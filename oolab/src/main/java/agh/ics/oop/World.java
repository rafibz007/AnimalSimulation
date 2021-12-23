package agh.ics.oop;
import agh.ics.oop.engines.SimulationEngine;
import agh.ics.oop.gui.App;
import agh.ics.oop.mapElements.Vector2d;
import agh.ics.oop.maps.WallBorderMap;
import agh.ics.oop.maps.WrappedBorderMap;
import agh.ics.oop.statistics.Statistics;
import javafx.application.Application;

import static java.lang.System.out;

public class World {

    public static void main(String[] args){
       try{
//           Application.launch(App.class, args);
           WrappedBorderMap map = new WrappedBorderMap(10, 10, 5, 5, 10, 5, 0, 5);
           Statistics statistics = new Statistics();
           map.addObserverForAnimals(statistics);
           SimulationEngine engine = new SimulationEngine(map, 0, 1, statistics);

//            todo cos nie tak dodawaniem zwierzat mozliwe, polozylem 80, na debbugu pokazalo 16, ale to moze byc iles dni pozniej
//           todo DODAJE DOBRZE, STATYSTYKI NIE DZIALAJA!!!!
           map.addAmountOfAnimalsToMap(50);
           map.addAmountOfGrassToStep(5);
           map.addAmountOfGrassToJungle(5);
           out.println(map);
//           out.println(new Vector2d(1,2).hashCode());

           engine.run();
//           engine.run();
//           engine.run();
//           engine.run();
//           engine.run();
//           engine.run();
//           engine.run();
//           engine.run();
//           engine.run();
//           engine.run();
//           engine.run();

//           out.println(map);


       } catch( IllegalArgumentException exception ) {
           out.println(exception);
           exception.printStackTrace();
       }

    }
}
