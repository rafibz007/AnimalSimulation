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


       } catch( IllegalArgumentException exception ) {
           out.println(exception);
           exception.printStackTrace();
       }

    }

}

