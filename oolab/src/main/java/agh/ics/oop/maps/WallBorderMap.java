package agh.ics.oop.maps;

import agh.ics.oop.engines.MapVisualizer;
import agh.ics.oop.mapElements.Vector2d;

public class WallBorderMap extends AbstractWorldMap{

    public WallBorderMap(int mapHeight, int mapWidth, int jungleHeight, int jungleWidth, int grassEnergy, int animalEnergy, int minEnergyToBreed, int maxAnimalEnergy) {
        super(mapHeight, mapWidth, jungleHeight, jungleWidth, grassEnergy, animalEnergy, minEnergyToBreed, maxAnimalEnergy);
    }

//    public WallBorderMap(int mapHeight, int mapWidth, int jungleHeight, int jungleWidth, int grassEnergy, int animalEnergy) {
//        super(mapHeight, mapWidth, jungleHeight, jungleWidth, grassEnergy, animalEnergy);
//    }


    @Override
    public Vector2d translatePosition(Vector2d position) {
        return position;
    }

}
