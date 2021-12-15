package agh.ics.oop.maps;

import agh.ics.oop.mapElements.Vector2d;

public class WrappedBorderMap extends AbstractWorldMap{


    public WrappedBorderMap(int mapHeight, int mapWidth, int jungleHeight, int jungleWidth, int grassEnergy, int animalEnergy, int minEnergyToBreed, int maxAnimalEnergy) {
        super(mapHeight, mapWidth, jungleHeight, jungleWidth, grassEnergy, animalEnergy, minEnergyToBreed, maxAnimalEnergy);
    }

    @Override
    public Vector2d translatePosition(Vector2d position) {
        int mapWidth = mapUpperRight.x-mapLowerLeft.x+1;
        int mapHeight = mapUpperRight.y-mapLowerLeft.y+1;
        return new Vector2d(position.x%mapWidth, position.y%mapHeight);
    }
}
