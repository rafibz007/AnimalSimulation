package agh.ics.oop.mapElements;

import agh.ics.oop.enums.MoveDirection;
import agh.ics.oop.enums.MapDirection;
import agh.ics.oop.interfaces.IMapElement;
import agh.ics.oop.interfaces.IMapElementsObserver;
import agh.ics.oop.maps.WorldMap;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Animal extends AbstractWorldElement implements IMapElement {
    private MapDirection mapDirection = MapDirection.NORTH;
    private final WorldMap map;
    public int energy;
    public Gene gene;
    public int lifeLength = 0;
    public int amountOfChildren = 0;

    public static Gene getGeneForNewBornAnimal(Animal a1, Animal a2){
        int a1GenesAmount;
        int a2GenesAmount;
        Gene gene;
        if (a1.energy < a2.energy){
            a1GenesAmount = Gene.amount*(a1.energy/ (a1.energy + a2.energy));
            a2GenesAmount = Gene.amount - a1GenesAmount;

            if (getRandomNumber(-1000,1000) > 0){
//            FOR STRONGER ANIMAL TAKE FROM LEFT
                int[] newGenesLeft = a2.gene.getLeftGenes(a1GenesAmount);
                int[] newGenesRight = a1.gene.getRightGenes(a2GenesAmount);
                gene = new Gene( Gene.joinGenes(newGenesLeft, newGenesRight) );
            } else {
//            FOR STRONGER ANIMAL TAKE FROM RIGHT
                int[] newGenesLeft = a2.gene.getRightGenes(a1GenesAmount);
                int[] newGenesRight = a1.gene.getLeftGenes(a2GenesAmount);
                gene = new Gene( Gene.joinGenes(newGenesLeft, newGenesRight) );
            }

        } else {
            a2GenesAmount = Gene.amount*(a2.energy/ (a1.energy + a2.energy));
            a1GenesAmount = Gene.amount - a2GenesAmount;

            if (getRandomNumber(-1000,1000) > 0){
//            FOR STRONGER ANIMAL TAKE FROM LEFT
                int[] newGenesLeft = a1.gene.getLeftGenes(a1GenesAmount);
                int[] newGenesRight = a2.gene.getRightGenes(a2GenesAmount);
                gene = new Gene( Gene.joinGenes(newGenesLeft, newGenesRight) );
            } else {
//            FOR STRONGER ANIMAL TAKE FROM RIGHT
                int[] newGenesLeft = a1.gene.getRightGenes(a1GenesAmount);
                int[] newGenesRight = a2.gene.getLeftGenes(a2GenesAmount);
                gene = new Gene( Gene.joinGenes(newGenesLeft, newGenesRight) );
            }
        }

        return gene;
    }

    public Animal(WorldMap map, Vector2d initialPosition, int energy){
        super(initialPosition);
        this.map = map;
        this.energy = energy;
        this.position = initialPosition;
        this.mapDirection = switch (getRandomNumber(0,7)){
            case 0 -> MapDirection.NORTH;
            case 1 -> MapDirection.NORTHEAST;
            case 2 -> MapDirection.EAST;
            case 3 -> MapDirection.SOUTHEAST;
            case 4 -> MapDirection.SOUTH;
            case 5 -> MapDirection.SOUTHWEST;
            case 6 -> MapDirection.WEST;
            default -> MapDirection.NORTHWEST;
        };
        this.gene = new Gene();
    }

    public Animal(WorldMap map, Vector2d initialPosition, int energy, Gene gene){
        this(map, initialPosition, energy);
        this.gene = gene;
    }

    public void decreaseEnergy(int amount){
        this.energy -= amount;
        if (energy<=0)
            this.remove();
        else
            this.notifyEnergy();
    }

//    TODO
    public MoveDirection getMove(){
//        return MoveDirection.FORWARD;
        return gene.getRandomMove();
    }

    public void eatGrass(Grass grass){
        this.energy += grass.getEnergy();
        this.energy = Math.min(this.energy, map.maxAnimalEnergy);
        this.notifyEnergy();
    }

    public void eatGrass(Grass grass, int amountOfEaters){
        this.energy += Math.ceil((double) grass.getEnergy()/amountOfEaters);
        this.energy = Math.min(this.energy, map.maxAnimalEnergy);
        this.notifyEnergy();
    }

    public MapDirection getMapDirection() {
        return mapDirection;
    }

    public void forTestingSetPositionAndDirection(int x, int y, MapDirection direction){
        this.position = new Vector2d(x,y);
        this.mapDirection = direction;
    }

    public String toString(){
        return switch (mapDirection){
            case NORTH -> "\u2191";
            case NORTHEAST -> "\u2197";
            case EAST -> "\u2192";
            case SOUTHEAST -> "\u2198";
            case SOUTH -> "\u2193";
            case SOUTHWEST -> "\u2199";
            case WEST -> "\u2190";
            case NORTHWEST -> "\u2196";
        };
    }

    public void moveDirection(MoveDirection direction){
        Vector2d oldPosition = position;
        switch (direction){
            case FORWARD -> {
                Vector2d newPosition = map.translatePosition(position.add(mapDirection.toUnitVector()));
                if (map.canMoveTo(newPosition)){
                    position = newPosition;
                }
            }
            case BACKWARD -> {
                Vector2d newPosition = map.translatePosition(position.add(mapDirection.toUnitVector().opposite()));
                if (map.canMoveTo(newPosition)) {
                    position = newPosition;
                }
            }
            default -> this.mapDirection = this.mapDirection.rotate(direction);
        }
        positionChanged(oldPosition, this);
    }


    private void positionChanged(Vector2d oldPosition, AbstractWorldElement element){
        for (IMapElementsObserver observer : Observers){
            observer.elementMovedFromPosition(oldPosition, element);
        }
    }


    @Override
    public Node guiRepresentation(int boxSize) {
        float percentageEnergy = Math.min((float) this.energy / map.maxAnimalEnergy, 1);
        Circle circle = new Circle((double) boxSize/2);
        circle.setFill(new Color(1-percentageEnergy,1-percentageEnergy,1-(percentageEnergy),1));
        return circle;
    }

    private static int getRandomNumber(int min, int max) {
        max += 1;
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void incrementLifeLength(){
        this.lifeLength += 1;
    }

    public void incrementAmountOfChildren(){
        this.amountOfChildren += 1;
        this.notifyNewChild();
    }

    public void notifyEnergy() {
        for (IMapElementsObserver observer : Observers){
            observer.elementChangedEnergy(this);
        }
    }

    public void notifyNewChild(){
        for (IMapElementsObserver observer : Observers){
            observer.elementHasNewChild(this);
        }
    }

    public Animal clone(){
        Vector2d newPosition = new Vector2d(
                getRandomNumber(map.getLowerLeft().x, map.getUpperRight().x),
                getRandomNumber(map.getLowerLeft().y, map.getUpperRight().y)
        );
        Animal newAnimal = new Animal(map, newPosition, this.energy, this.gene);
        newAnimal.amountOfChildren = this.amountOfChildren;
        newAnimal.lifeLength = this.lifeLength;
        return newAnimal;
    }
}
