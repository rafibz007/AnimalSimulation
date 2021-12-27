package agh.ics.oop.engines;

import agh.ics.oop.interfaces.IEngine;
import agh.ics.oop.interfaces.IMapObserver;
import agh.ics.oop.mapElements.*;
import agh.ics.oop.maps.WorldMap;
import agh.ics.oop.statistics.Statistics;
import javafx.scene.layout.GridPane;

import java.util.*;

// TODO POZYCJE ZE ZWIERZETAMI I TRAWA MOZNA BRAC Z MAPY
public class SimulationEngine implements IEngine, Runnable{
    private final WorldMap map;
    final List<IMapObserver> Observers = new ArrayList<>();


    int dayDaley = 1000;
    int dailyEnergyLoss = 1;
    int dailyGrassGrowth = 1;


    public SimulationEngine(WorldMap map, int dayDelay, int dailyEnergyLoss, int dailyGrassGrowth){
        this.map = map;
        this.dayDaley = dayDelay;
        this.dailyEnergyLoss = dailyEnergyLoss;
        this.dailyGrassGrowth = dailyGrassGrowth;
    }



    @Override
    public synchronized void run() {


        while (map.anyAnimalAlive()){

//            System.out.println("pre" + statistics);

    //        MOVING ANIMALS
            ArrayList<Animal> allAnimals = map.allAnimals();
            for (Animal animal : allAnimals){
                animal.moveDirection(animal.getMove());
                animal.decreaseEnergy(dailyEnergyLoss);

            }




    //        EATING
            ArrayList<Vector2d> positionsWithAnimals = new ArrayList<Vector2d>(map.animalsPositionsSet());
            for (Vector2d position : new ArrayList<>(positionsWithAnimals)){
                if (!map.grassIsAt(position))
                    continue;

                Grass grass = map.grassAt(position);

                List<Animal> strongestAnimalsList = map.strongestAnimalsAtPosition(position);
                for (Animal animal : strongestAnimalsList)
                    animal.eatGrass(grass, strongestAnimalsList.size());

                grass.remove();
            }


    //        BREEDING
            for (Vector2d position : new ArrayList<>(positionsWithAnimals)){

                if (!map.animalIsAt(position))
                    continue;


                ArrayList<Animal> animalsAtPosition = map.allAnimalsAt(position);
                int length = animalsAtPosition.size();
                if (length<2)
                    continue;

                Animal a1 = animalsAtPosition.get(length-1);
                Animal a2 = animalsAtPosition.get(length-2);

                if (a1.energy < map.minAnimalEnergyToBreed || a2.energy < map.minAnimalEnergyToBreed)
                    continue;

                Gene gene = Animal.getGeneForNewBornAnimal( a1, a2 );
                map.spawnAnimal(a1.getPosition(), gene, (int) Math.ceil((double) (a1.energy/4)+ (double) Math.ceil(a2.energy/4)));
                a1.decreaseEnergy((int) Math.ceil((double) a1.energy/4));
                a2.decreaseEnergy((int) Math.ceil((double) a2.energy/4));

            }


//                Platform.runLater(()->{
                map.addAmountOfGrassToStep(dailyGrassGrowth);
                map.addAmountOfGrassToJungle(dailyGrassGrowth);
//                });



//            System.out.println(map);

            for (IMapObserver observer : Observers)
                observer.updateMap();

            if (dayDaley > 0){
                try {
                    Thread.sleep(dayDaley);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            System.out.println("post" + statistics);

        }


    }

    public void addObserver(IMapObserver observer){
        Observers.add(observer);
    }
    public void removeObserver(IMapObserver observer){
        Observers.remove(observer);
    }
}
