package agh.ics.oop.maps;

import agh.ics.oop.mapElements.*;
import agh.ics.oop.interfaces.IMapElement;
import agh.ics.oop.interfaces.IMapElementsObserver;

import java.util.*;

public class WorldMap implements IMapElementsObserver {
    protected final Map <Vector2d, Set<Animal>> animalsSetsOnPositions;
    protected final Map <Vector2d, Grass> grassTiles;
    protected final Vector2d mapLowerLeft;
    protected final Vector2d mapUpperRight;
    protected final Vector2d jungleLowerLeft;
    protected final Vector2d jungleUpperRight;
    public final int grassEnergy;
    public final int animalEnergy;
    public final int maxAnimalEnergy;
    public final int minAnimalEnergyToBreed;
    protected final Set<IMapElementsObserver> observersForMapElements;
    protected boolean isWrapped;
    protected int amountOfAnimals;
    protected int amountOfGrass;



    public WorldMap(int mapHeight, int mapWidth, int jungleHeight, int jungleWidth, int grassEnergy, int animalEnergy, int minEnergyToBreed, int maxAnimalEnergy, boolean isWrapped){

        mapLowerLeft = new Vector2d(0,0);
        mapUpperRight = new Vector2d(mapWidth-1, mapHeight-1);

        jungleLowerLeft = new Vector2d((mapWidth-jungleWidth)/2, (mapHeight-jungleHeight)/2);
        jungleUpperRight = new Vector2d((mapWidth-jungleWidth)/2+jungleWidth-1, (mapHeight-jungleHeight)/2+jungleHeight-1);

        this.grassEnergy = grassEnergy;
        this.animalEnergy = animalEnergy;
        this.minAnimalEnergyToBreed = minEnergyToBreed;
        this.maxAnimalEnergy = maxAnimalEnergy;

        this.animalsSetsOnPositions = new HashMap<>();
        this.grassTiles = new HashMap<>();
        this.observersForMapElements = new HashSet<>();
        addObserverForAnimals(this);

        this.isWrapped = isWrapped;

        amountOfAnimals = 0;
        amountOfGrass = 0;
    }


//    MANAGING ELEMENTS
    @Override
    public void elementMovedFromPosition(Vector2d oldPosition, AbstractWorldElement element) {
        Vector2d newPosition = element.getPosition();

        if (newPosition == oldPosition)
            return;

        if (element instanceof Animal){
            Animal animal = (Animal) element;

            animalsSetsOnPositions.get(oldPosition).remove(animal);
            if (animalsSetsOnPositions.get(oldPosition).isEmpty())
                animalsSetsOnPositions.remove(oldPosition);

            placeAnimalOnMap(animal);


        } else {
            Grass grass = grassTiles.remove(oldPosition);
            grassTiles.put(newPosition, grass);
        }
    }

    public ArrayList<Animal> strongestAnimalsAtPosition(Vector2d position){
        ArrayList<Animal> allAnimalsAtPosition = allAnimalsAt(position);
        int bestEnergy = strongestAnimalAt(position).energy;

        ArrayList<Animal> result = new ArrayList<>();

        for (Animal animal : allAnimalsAtPosition){
            if (animal.energy == bestEnergy)
                result.add(animal);
        }

        return result;
    }

    public boolean animalIsAt(Vector2d position){
        return animalsSetsOnPositions.containsKey(position);
    }

    public boolean grassIsAt(Vector2d position){
        return grassAt(position) != null;
    }

//    adding
    @Override
    public void elementAdded(AbstractWorldElement element) {
        if (element instanceof Animal) {
            placeAnimalOnMap((Animal) element);
            amountOfAnimals += 1;
        }


        if (element instanceof Grass) {
            placeGrassOnMap((Grass) element);
            amountOfGrass += 1;
        }

    }

    public Animal spawnAnimal(Vector2d position, int era) {
        Animal animal = new Animal(this, position, animalEnergy, era);
        addObserversToMapElement(animal);
        animal.add();

        return animal;
    }

    public Animal spawnAnimal(Vector2d position, int era, Gene gene) {
        Animal animal = new Animal(this, position, animalEnergy, era, gene);
        addObserversToMapElement(animal);
        animal.add();
        return animal;
    }

    public Animal spawnAnimal(Vector2d position, int era, Gene gene, int energy) {
        Animal animal = new Animal(this, position, energy, era, gene);
        addObserversToMapElement(animal);
        animal.add();
        return animal;
    }

    public Animal spawnAnimal(Animal animal) {
        addObserversToMapElement(animal);
        animal.add();
        return animal;
    }

    public Grass spawnGrass(Vector2d position) {
        Grass grass = new Grass(position, grassEnergy);
        addObserversToMapElement(grass);
        grass.add();
        return grass;
    }


    protected void placeAnimalOnMap(Animal animal) {
        if (!animalsSetsOnPositions.containsKey(animal.getPosition()))
            animalsSetsOnPositions.put(animal.getPosition(), new HashSet<>());
        animalsSetsOnPositions.get(animal.getPosition()).add(animal);
    }


    protected void placeGrassOnMap(Grass grass) {
        if (grassAt(grass.getPosition()) == null){
            grassTiles.put(grass.getPosition(), grass);
        }
    }

    public void addAmountOfAnimalsToMapAtStart(int amount){
        for (int i=0; i<amount; i++){
            int x = getRandomNumber(mapLowerLeft.x, mapUpperRight.x);
            int y = getRandomNumber(mapLowerLeft.y, mapUpperRight.y);
            spawnAnimal(new Vector2d(x,y), 0);
        }
    }

    public void addAmountOfGrassToJungle(int amount){

        for (int j=0; j<amount; j++){
            boolean foundFreeTile = false;
            Set<Vector2d> checkedTiles = new HashSet<>();

            int midX = (jungleLowerLeft.x+ jungleUpperRight.x)/2;
            int rangeX = (jungleUpperRight.x - jungleLowerLeft.x)/2;
            int startX = midX + getRandomNumber(-rangeX, rangeX);

            int midY = (jungleLowerLeft.y+ jungleUpperRight.y)/2;
            int rangeY = (jungleUpperRight.y - jungleLowerLeft.y)/2;
            int startY = midY + getRandomNumber(-rangeY, rangeY);

            Vector2d startPosition = new Vector2d(startX, startY);

            Queue<Vector2d> queue = new LinkedList<>();
            queue.add(startPosition);

            while (!queue.isEmpty()){
                Vector2d position = queue.poll();
                if (!isOccupied(position)) {
                    spawnGrass(position);
                    foundFreeTile = true;
                    break;
                }

                assert position != null;
                List<Vector2d> checkingPositions = new ArrayList<>(List.of(
                        new Vector2d(position.x, position.y + 1),
                        new Vector2d(position.x + 1, position.y),
                        new Vector2d(position.x, position.y - 1),
                        new Vector2d(position.x - 1, position.y)
                ));

                for (Vector2d checkPosition : checkingPositions){
                    if (isInJungle(checkPosition) && !checkedTiles.contains(checkPosition)){
                        checkedTiles.add(checkPosition);
                        queue.add(checkPosition);

                    }
                }

            }
            if (!foundFreeTile)
                break;
        }

    }

    public void addAmountOfGrassToStep(int amount){

        for (int j=0; j<amount; j++){
            boolean foundFreeTile = false;
            Set<Vector2d> checkedTiles = new HashSet<>();


            int startX = getRandomNumber(mapLowerLeft.x, mapUpperRight.x);
            int startY = 0;
            if (isInJungle(new Vector2d(startX, jungleLowerLeft.y))){
                int direction = 0;
                while (direction==0)
                    direction = (int)Math.signum(getRandomNumber(-1000, 1000));
                int jungleRangeY = (jungleUpperRight.y - jungleLowerLeft.y)/2;
                int rangeY = (mapUpperRight.y-mapLowerLeft.y)/2;
                startY = (jungleLowerLeft.y+ jungleUpperRight.y)/2 + direction*getRandomNumber(jungleRangeY+1,rangeY);
            } else {
                startY = getRandomNumber(mapLowerLeft.y, mapUpperRight.y);
            }

            Vector2d startPosition = new Vector2d(startX, startY);

            Queue<Vector2d> queue = new LinkedList<>();
            queue.add(startPosition);

            while (!queue.isEmpty()){
                Vector2d position = queue.poll();
                if (!isOccupied(position)) {
                    spawnGrass(position);
                    foundFreeTile = true;
                    break;
                }

                assert position != null;
                List<Vector2d> checkingPositions = new ArrayList<>(List.of(
                        new Vector2d(position.x, position.y + 1),
                        new Vector2d(position.x + 1, position.y),
                        new Vector2d(position.x, position.y - 1),
                        new Vector2d(position.x - 1, position.y)
                ));

                for (Vector2d checkPosition : checkingPositions){
                    if (isInStep(checkPosition) && !checkedTiles.contains(checkPosition)){
                        checkedTiles.add(checkPosition);
                        queue.add(checkPosition);

                    }
                }
            }

            if (!foundFreeTile)
                break;

        }
    }


//    removing
    @Override
    public void elementRemoved(AbstractWorldElement element) {
        if (element instanceof Animal) {
            removeAnimalFromMap((Animal) element);
            amountOfAnimals += 1;
        }

        if (element instanceof Grass) {
            removeGrassFromMap((Grass) element);
            amountOfGrass += 1;
        }
    }

    protected boolean removeAnimalFromMap(Animal animal) {
        if (!animalsSetsOnPositions.containsKey(animal.getPosition()))
            return false;

        animalsSetsOnPositions.get(animal.getPosition()).remove(animal);

        if (animalsSetsOnPositions.get(animal.getPosition()).isEmpty())
            animalsSetsOnPositions.remove(animal.getPosition());

        return true;
    }

    protected boolean removeGrassFromMap(Grass grass) {
        if (grassTiles.containsKey(grass.getPosition())){
            grassTiles.remove(grass.getPosition());
            return true;
        }
        return false;
    }



//    CHECKING ANIMAL MOVEMENT

    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    public boolean canMoveTo(Vector2d position) {
        return position.follows(mapLowerLeft) && position.precedes(mapUpperRight);
    }

    public Grass grassAt(Vector2d position){
        return grassTiles.get(position);
    }

    public Animal strongestAnimalAt(Vector2d position){
        ArrayList<Animal> allAnimalsOnPosition = allAnimalsAt(position);
        if (allAnimalsOnPosition.isEmpty())
            return null;
        allAnimalsOnPosition.sort(Comparator.comparingInt((Animal o) -> o.energy));
        return allAnimalsOnPosition.get( allAnimalsOnPosition.size()-1 );
    }

    public ArrayList<Animal> allAnimalsAt(Vector2d position){
        if (animalsSetsOnPositions.get(position) == null)
            return new ArrayList<Animal>();
        return new ArrayList<Animal>(animalsSetsOnPositions.get(position));
    }

    public IMapElement objectAt(Vector2d position) {
        Animal animal = strongestAnimalAt(position);
        if (animal != null)
            return animal;
        Grass grass = grassAt(position);
        if (grass != null)
            return grass;

        return null;
    }

    public Vector2d translatePosition(Vector2d position){
        if (!isWrapped)
            return position;
        int mapWidth = mapUpperRight.x-mapLowerLeft.x+1;
        int mapHeight = mapUpperRight.y-mapLowerLeft.y+1;
        return new Vector2d((position.x+mapWidth)%mapWidth, (position.y+mapHeight)%mapHeight);
    }


//    OTHER
    public Set<Vector2d> animalsPositionsSet(){
        return animalsSetsOnPositions.keySet();
    }

    public ArrayList<Animal> allAnimals(){
        ArrayList<Vector2d> positionsWithAnimals = new ArrayList<>(animalsPositionsSet());
        ArrayList<Animal> allAnimals = new ArrayList<>();

        for (Vector2d position : positionsWithAnimals)
            allAnimals.addAll(new ArrayList<>(allAnimalsAt(position)));

        return allAnimals;
    }

    public Set<Vector2d> grassPositionsSet(){
        return new HashSet<>(grassTiles.keySet());
    }

    public Set<Vector2d> objectsPositionsSet() {
        Set<Vector2d> set1 = animalsPositionsSet();
        Set<Vector2d> set2 = grassPositionsSet();
        Set<Vector2d> set = new HashSet<>();
        set.addAll(set1);
        set.addAll(set2);
        return set;
    }

    public Vector2d getLowerLeft(){
        return mapLowerLeft;
    };

    public Vector2d getUpperRight(){
        return mapUpperRight;
    };

    public void addObserversToMapElement(AbstractWorldElement element){
        for (IMapElementsObserver observer : observersForMapElements)
            element.addObserver(observer);
    }

    protected static int getRandomNumber(int min, int max) {
        max+=1;
        return (int) ((Math.random() * (max - min)) + min);
    }

    public boolean isInJungle(Vector2d position){
        return position.follows(jungleLowerLeft) && position.precedes(jungleUpperRight);
    }

    public boolean isInStep(Vector2d position){
        return position.follows(mapLowerLeft) && position.precedes(mapUpperRight) && !isInJungle(position);
    }

    public void addObserverForAnimals(IMapElementsObserver observer){
        observersForMapElements.add(observer);
    }

    public void removeObserverForAnimals(IMapElementsObserver observer){
        observersForMapElements.remove(observer);
    }

    public boolean anyAnimalAlive(){
        return animalsSetsOnPositions.size() > 0;
    }

    public int amountOfAnimalsAt(Vector2d position){
        return animalsSetsOnPositions.get(position) != null ? animalsSetsOnPositions.get(position).size() : 0;
    }


    @Override
    public void elementChangedEnergy(AbstractWorldElement element) {
//        nothing
    }

    @Override
    public void elementHasNewChild(AbstractWorldElement parent, AbstractWorldElement child) {
//        nothing
    }

}

