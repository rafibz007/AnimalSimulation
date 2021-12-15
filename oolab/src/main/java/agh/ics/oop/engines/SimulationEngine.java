package agh.ics.oop.engines;

import agh.ics.oop.interfaces.IEngine;
import agh.ics.oop.interfaces.IMapObserver;
import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.mapElements.*;
import agh.ics.oop.maps.AbstractWorldMap;

import java.util.*;

// TODO ENGINE SHOULD TELL GUI TO UPDATE, MAKE SPECIAL IOBSERVER FOR IT
// TODO ENGINE USUWA TRAWE I NAKAZUJE KTO MA JA ZJESC ORAZ MOWI KTO MA SIE ROZMNAZAC,
// TODO ALE SWOJEGO POTOMKA SPAWNUJA JUZ RODZICE
public class SimulationEngine implements IEngine, Runnable, IPositionChangeObserver{
    private final AbstractWorldMap map;
    private final Set<Vector2d> positionsWithGrass = new HashSet<>();
    private final Set<Vector2d> positionsWithAnimals = new HashSet<>();
    final List<IMapObserver> Observers = new ArrayList<>();
//    TODO, MOZE ZAMIAST LISTY OBIEKTOW, SET POL NA KTORYCH COS JEST

    int dayDaley = 1000;
    int dailyEnergyLoss = 1;


    public SimulationEngine(AbstractWorldMap map, int dayDelay, int dailyEnergyLoss){
        this.map = map;
        this.dayDaley = dayDelay;
        this.dailyEnergyLoss = dailyEnergyLoss;
    }

    @Override // todo ewentualnie tu sie psuje
    public void elementMovedFromPosition(Vector2d oldPosition, AbstractWorldElement element) {

        if (element instanceof Animal){
            elementRemoved( new Animal(map, oldPosition, 0) );
            elementAdded(element);
        }

    }

    @Override
    public void elementAdded(AbstractWorldElement element) {

        if (element instanceof Grass) {
            if (map.grassIsAt(element.getPosition()))
                positionsWithGrass.add(element.getPosition());
        }

        if (element instanceof Animal) {
            if (map.animalIsAt(element.getPosition()))
                positionsWithAnimals.add(element.getPosition());
        }

    }

    @Override
    public void elementRemoved(AbstractWorldElement element) {
        if (element instanceof Grass)
            positionsWithGrass.remove(element.getPosition());

        if (element instanceof Animal){
            if (!map.animalIsAt(element.getPosition())) {
                positionsWithAnimals.remove(element.getPosition());
//                positionsWithAnimals.add(element.getPosition());
            }
//                positionsWithAnimals.remove(element.getPosition());
        }
    }

    @Override
    public synchronized void run() {


        while (map.anyAnimalAlive()){
            map.animalCheck();
    //        MOVING ANIMALS
            ArrayList<Animal> allAnimals = new ArrayList<>();

            for (Vector2d position : positionsWithAnimals)
                allAnimals.addAll(new ArrayList<>(map.allAnimalsAt(position)));

//        todo po ruchu sie psuje...
            String energies = "";
            for (Animal animal : allAnimals){
                animal.moveDirection(animal.getMove());
                animal.decreaseEnergy(dailyEnergyLoss);
                if (animal.energy > 0)
                    energies +=  animal.energy + ":" + animal.getPosition() + " ";
            }

//            todo podczas bledu wypisalo to
//            todo 10:(4, 2)
//            todo [(1, 4), (4, 2)]
//            todo co ciekawe na mapie pokazaly sie zwierzaki na obu pozycjach (zwrocone w ta sama strone - moze to wazne) - moze juz nie wazne
            System.out.println(energies);
            System.out.println(positionsWithAnimals);

            map.animalCheck();
    //        EATING
            for (Vector2d position : new ArrayList<>(positionsWithAnimals)){
                if (!map.grassIsAt(position))
                    continue;

                Grass grass = map.grassAt(position);

                List<Animal> strongestAnimalsList = map.strongestAnimalsAtPosition(position);
                for (Animal animal : strongestAnimalsList)
                    animal.eatGrass(grass, strongestAnimalsList.size());

                grass.remove();
            }

            map.animalCheck();
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
                map.spawnAnimal(a1.getPosition(), gene, (int) Math.ceil((double) (a1.energy/4)+(double)(a2.energy/4)));
                a1.decreaseEnergy((int) Math.ceil((double) a1.energy/4));
                a2.decreaseEnergy((int) Math.ceil((double) a2.energy/4));
                System.out.println("breeded");
            }


    //            Platform.runLater(()->{
                map.addAmountOfGrassToStep(1);
                map.addAmountOfGrassToJungle(1);
    //            });



            System.out.println(map);

            for (IMapObserver observer : Observers)
                observer.updateMap();

//            try {
//                Thread.sleep(dayDaley);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            map.animalCheck();
        }


    }

    public void addObserver(IMapObserver observer){
        Observers.add(observer);
    }
    public void removeObserver(IMapObserver observer){
        Observers.remove(observer);
    }
}