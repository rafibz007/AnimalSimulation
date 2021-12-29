package agh.ics.oop.engines;

import agh.ics.oop.interfaces.IEngine;
import agh.ics.oop.interfaces.IEngineObserver;
import agh.ics.oop.interfaces.IMapObserver;
import agh.ics.oop.mapElements.*;
import agh.ics.oop.maps.MagicWorldMap;
import agh.ics.oop.maps.WorldMap;

import java.util.*;

public class SimulationEngine implements IEngine, Runnable{
    private final WorldMap map;
    final List<IMapObserver> MapObservers = new ArrayList<>();
    final List<IEngineObserver> EngineObservers = new ArrayList<>();


    int dayDaley;
    int dailyEnergyLoss;
    int dailyGrassGrowth;

    int era;

    public int getEra() {
        return era;
    }

    private volatile boolean running = false;


    public SimulationEngine(WorldMap map, int dayDelay, int dailyEnergyLoss, int dailyGrassGrowth){
        this.map = map;
        this.dayDaley = dayDelay;
        this.dailyEnergyLoss = dailyEnergyLoss;
        this.dailyGrassGrowth = dailyGrassGrowth;
        this.era = 1;
    }

    public void pause(){
        running = false;
    }

    public synchronized void resume(){
        this.notify();
        running = true;
    }

    @Override
    public synchronized void run() {


        while (map.anyAnimalAlive()){
            synchronized (this) {
                while (!running) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        // do nothing, just continue
                    }
                }
            }


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
                map.spawnAnimal(a1.getPosition(), gene, (int) Math.ceil((double) (a1.energy/4) + Math.ceil((double) a2.energy/4)));
                a1.decreaseEnergy((int) Math.ceil((double) a1.energy/4));
                a2.decreaseEnergy((int) Math.ceil((double) a2.energy/4));

                a1.incrementAmountOfChildren();
                a2.incrementAmountOfChildren();

            }


            map.addAmountOfGrassToStep(dailyGrassGrowth);
            map.addAmountOfGrassToJungle(dailyGrassGrowth);

            allAnimals = map.allAnimals();
            for (Animal animal : allAnimals){
                animal.incrementLifeLength();
            }

            if (allAnimals.size() == 5 && map instanceof MagicWorldMap)
                ((MagicWorldMap) map).doTheMagic();

            for (IEngineObserver observer : EngineObservers)
                observer.dayEnded();

            for (IMapObserver observer : MapObservers)
                observer.updateSimulation();

            if (dayDaley > 0){
                try {
                    Thread.sleep(dayDaley);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            era += 1;
        }


    }

    public void addMapObserver(IMapObserver observer){
        MapObservers.add(observer);
    }
    public void removeMapObserver(IMapObserver observer){
        MapObservers.remove(observer);
    }

    public void addEngineObserver(IEngineObserver observer){
        EngineObservers.add(observer);
    }
    public void removeEngineObserver(IEngineObserver observer){
        EngineObservers.remove(observer);
    }
}
