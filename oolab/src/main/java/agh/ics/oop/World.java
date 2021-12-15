package agh.ics.oop;
import agh.ics.oop.engines.SimulationEngine;
import agh.ics.oop.gui.App;
import agh.ics.oop.maps.WallBorderMap;
import javafx.application.Application;

import static java.lang.System.out;

public class World {

    public static void main(String[] args){
       try{
//           Application.launch(App.class, args);
//           TODO zwierzeta nowonarodzone maja wiecej energii niz rodzice, albo trawa daje za duzo energii
           WallBorderMap map = new WallBorderMap(5, 5, 1, 1, 10, 10, 5, 20);
           SimulationEngine engine = new SimulationEngine(map, 0, 5);
           map.addObserverForAnimals(engine);


           map.addAmountOfAnimalsToMap(30);
           map.addAmountOfGrassToStep(5);
           map.addAmountOfGrassToJungle(5);
           out.println(map);

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
