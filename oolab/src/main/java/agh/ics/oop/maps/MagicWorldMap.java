package agh.ics.oop.maps;

import agh.ics.oop.mapElements.Animal;

public class MagicWorldMap extends WorldMap{

    int amountOfMagicTricksLEft = 3;

    public MagicWorldMap(int mapHeight, int mapWidth, int jungleHeight, int jungleWidth, int grassEnergy, int animalEnergy, int minEnergyToBreed, int maxAnimalEnergy, boolean isWrapped) {
        super(mapHeight, mapWidth, jungleHeight, jungleWidth, grassEnergy, animalEnergy, minEnergyToBreed, maxAnimalEnergy, isWrapped);
    }

    public boolean doTheMagic(){
        if (amountOfMagicTricksLEft < 0)
            return false;

        for (Animal animal : allAnimals())
            spawnAnimal(animal.clone());

        amountOfMagicTricksLEft -= 1;
        return true;
    }
}
