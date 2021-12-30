package agh.ics.oop.mapElements;

import agh.ics.oop.enums.MoveDirection;
import agh.ics.oop.enums.MapDirection;
import agh.ics.oop.interfaces.IDetailObserver;
import agh.ics.oop.interfaces.IMapElement;
import agh.ics.oop.interfaces.IMapElementsObserver;
import agh.ics.oop.maps.WorldMap;
import javafx.scene.Node;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Animal extends AbstractWorldElement implements IMapElement {
    private MapDirection mapDirection = MapDirection.NORTH;
    private final WorldMap map;
    public int energy;
    public Gene gene;
    public int eraOfBirth;
    public int lifeLength = 0;
    private final Set<Animal> parents = new HashSet<>();
    private final List<IDetailObserver> detailsObservers = new ArrayList<>();

    public boolean hasDetailsTracked = false;

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

    public static void breed(Animal a1, Animal a2, double energyPercentageForChild){
        if (!a1.map.equals(a2.map))
            return;

        Gene gene = Animal.getGeneForNewBornAnimal( a1, a2 );
        Animal newBorn = a1.map.spawnAnimal(a1.getPosition(), a1.eraOfBirth + a1.lifeLength,gene, (int) (Math.ceil(a1.energy*energyPercentageForChild) + Math.ceil(a2.energy*energyPercentageForChild)));
        a1.decreaseEnergy((int) Math.ceil( a1.energy*energyPercentageForChild));
        a2.decreaseEnergy((int) Math.ceil( a2.energy*energyPercentageForChild));

        newBorn.addParent(a1);
        newBorn.addParent(a2);

        a1.incrementAmountOfChildren();
        a2.incrementAmountOfChildren();

        a1.notifyOffspring(newBorn);
        a2.notifyOffspring(newBorn);
    }

    public Animal(WorldMap map, Vector2d initialPosition, int energy, int birthEra){
        super(initialPosition);
        this.map = map;
        this.energy = energy;
        this.position = initialPosition;
        this.eraOfBirth = birthEra;
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

    public Animal(WorldMap map, Vector2d initialPosition, int energy, int birthEra, Gene gene){
        this(map, initialPosition, energy, birthEra);
        this.gene = gene;
    }

    public void decreaseEnergy(int amount){
        this.energy -= amount;
        if (energy<=0)
            this.remove();
        else
            this.notifyEnergy();
    }

    public MoveDirection getMove(){
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

        if (hasDetailsTracked){
            InnerShadow innerShadow = new InnerShadow();
            innerShadow.setColor(new Color(1,0,1,1));
            circle.setEffect(innerShadow);
        }

        return circle;
    }

    private static int getRandomNumber(int min, int max) {
        max += 1;
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public void remove() {
        super.remove();
        for (IDetailObserver observer : detailsObservers)
            observer.animalDied(this);
    }

    public void incrementLifeLength(){
        this.lifeLength += 1;
    }

    public void incrementAmountOfChildren(){
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

    public void notifyOffspring(Animal offspring){
        for (IDetailObserver observer : detailsObservers)
            observer.newOffspringBorn(offspring);
    }

    public Animal clone(){
        Vector2d newPosition = new Vector2d(
                getRandomNumber(map.getLowerLeft().x, map.getUpperRight().x),
                getRandomNumber(map.getLowerLeft().y, map.getUpperRight().y)
        );
        Animal newAnimal = new Animal(map, newPosition, this.energy, this.eraOfBirth, this.gene);
        newAnimal.lifeLength = this.lifeLength;
        return newAnimal;
    }

    public void addParent(Animal parent){
        parents.add(parent);
    }

    public Set<Animal> getParents(){
        return new HashSet<>(parents);
    }

    public void addDetailObserver(IDetailObserver observer){
        detailsObservers.add(observer);
    }

    public void removeDetailObserver(IDetailObserver observer){
        detailsObservers.remove(observer);
    }

    public void clearDetailObservers(){
        detailsObservers.clear();
    }

    public Set<IDetailObserver> getDetailObservers(){
        return new HashSet<>(detailsObservers);
    }
}
