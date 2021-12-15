package agh.ics.oop.maps;

import agh.ics.oop.mapElements.*;
import agh.ics.oop.engines.MapVisualizer;
import agh.ics.oop.interfaces.IMapElement;
import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.interfaces.IWorldMap;

import java.util.*;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    protected final Map <Vector2d, TreeSet<Animal>> animalsList;
    protected final Map <Vector2d, Grass> grassTiles;
    protected final Vector2d mapLowerLeft;
    protected final Vector2d mapUpperRight;
    protected final Vector2d jungleLowerLeft;
    protected final Vector2d jungleUpperRight;
    public final int grassEnergy;
    public final int animalEnergy;
    public final int maxAnimalEnergy;
    public final int minAnimalEnergyToBreed;
    protected final Set<IPositionChangeObserver> observersForMapElements;



    public AbstractWorldMap(int mapHeight, int mapWidth, int jungleHeight, int jungleWidth, int grassEnergy, int animalEnergy, int minEnergyToBreed, int maxAnimalEnergy){

        mapLowerLeft = new Vector2d(0,0);
        mapUpperRight = new Vector2d(mapWidth-1, mapHeight-1);

        jungleLowerLeft = new Vector2d((mapWidth-jungleWidth)/2, (mapHeight-jungleHeight)/2);
        jungleUpperRight = new Vector2d((mapWidth-jungleWidth)/2+jungleWidth-1, (mapHeight-jungleHeight)/2+jungleHeight-1);

        this.grassEnergy = grassEnergy;
        this.animalEnergy = animalEnergy;
        this.minAnimalEnergyToBreed = minEnergyToBreed;
        this.maxAnimalEnergy = maxAnimalEnergy;

        this.animalsList = new HashMap<>();
        this.grassTiles = new HashMap<>();
        this.observersForMapElements = new HashSet<>();
        addObserverForAnimals(this);

        System.out.println(jungleLowerLeft);
        System.out.println(jungleUpperRight);
    }


    //    MANAGING ELEMENTS
    @Override //todo niektore zwierzeta sa neiusuwane z jakiegos powodu, zostaja na mapie i nie daja sie juz wiecej skasowac, moze przy tej funkcji cos sie psuje, ale moze to byc probelm w simulation engine
//    todo animal na pozycji 3,3 dalej jest blednie trzymany na 2,2
//    todo no chyba ta funkcja nie dziala

//    todo nie usuwa czasem elementow z mapy, nawet gdy powinno je wykryc
    public void elementMovedFromPosition(Vector2d oldPosition, AbstractWorldElement element) {
        Vector2d newPosition = element.getPosition();
        oldPosition = translatePosition(oldPosition);
        newPosition = translatePosition(newPosition);

        if (newPosition == oldPosition)
            return;

        //todo tu sie psuje cos, moze przerobic calosc na SET
        if (element instanceof Animal){
            Animal animal = (Animal) element;
//            if (animalsList.get(oldPosition) == null)
//                return;
//                animalsList.get(oldPosition).contains(animal); //todo tu wykrylo element, a na dole go nie usunelo, mimo ze byl
//            if (animalsList.get(oldPosition) != null) {
                animalsList.get(oldPosition).remove(animal); //todo nullpointrexcepiton
                if (animalsList.get(oldPosition).isEmpty())
                    animalsList.remove(oldPosition);
//            }
            placeAnimalOnMap(animal);

        } else {
            Grass grass = grassTiles.remove(oldPosition);
            grassTiles.put(newPosition, grass);
        }
}

    public ArrayList<Animal> strongestAnimalsAtPosition(Vector2d position){
//        TODO
        Animal strongestAnimal = strongestAnimalAt(position);
        if (strongestAnimal == null)
            return new ArrayList<>();

        int energy = strongestAnimal.energy;
        ArrayList<Animal> list = new ArrayList<>();
        for (Animal animal : animalsList.get(position)){
            if (animal.energy == energy)
                list.add(animal);
        }

        return list;
    }

    public boolean animalIsAt(Vector2d position){
        return strongestAnimalAt(position) != null;
    }

    public boolean grassIsAt(Vector2d position){
        return grassAt(position) != null;
    }

//    adding
    @Override
    public void elementAdded(AbstractWorldElement element) {
        if (element instanceof Animal)
            placeAnimalOnMap((Animal) element);

        if (element instanceof Grass)
            placeGrassOnMap((Grass) element);
    }

    @Override
    public boolean spawnAnimal(Vector2d position) {
        Animal animal = new Animal(this, position, animalEnergy);
        addObserversToMapElement(animal);
        animal.add();

        return true;
    }

    public boolean spawnAnimal(Vector2d position, Gene gene) {
        Animal animal = new Animal(this, position, animalEnergy, gene);
        addObserversToMapElement(animal);
        animal.add();
        return true;
    }

    public boolean spawnAnimal(Vector2d position, Gene gene, int energy) {
        Animal animal = new Animal(this, position, energy, gene);
        addObserversToMapElement(animal);
        animal.add();
        return true;
    }

    @Override
    public boolean spawnGrass(Vector2d position) {
        Grass grass = new Grass(position, grassEnergy);
        addObserversToMapElement(grass);
        grass.add();
        return true;
    }

    protected boolean placeAnimalOnMap(Animal animal) {
        if (canMoveTo(animal.getPosition())){
            if (!animalsList.containsKey(animal.getPosition())){
                animalsList.put(animal.getPosition(), generateTreeSet());
            }
            animalsList.get(animal.getPosition()).add(animal);
//            System.out.println("added");
            return true;
        }
        return false;
    }

    protected TreeSet<Animal> generateTreeSet(){
        return new TreeSet<Animal>(new Comparator<Animal>(){
            @Override
            public int compare(Animal o1, Animal o2) {
                if (o1.equals(o2))
                    return 0;
                if (o1.energy >= o2.energy)
                    return -1;

                return 1;
            }
        });
    }

    protected boolean placeGrassOnMap(Grass grass) {
        if (grassAt(grass.getPosition()) == null){
            grassTiles.put(grass.getPosition(), grass);
            return true;
        }
        return false;
    }

    public void addAmountOfAnimalsToMap(int amount){
        for (int i=0; i<amount; i++){
            int x = getRandomNumber(mapLowerLeft.x, mapUpperRight.x);
            int y = getRandomNumber(mapLowerLeft.y, mapUpperRight.y);
            spawnAnimal(new Vector2d(x,y));
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

            int direction=0;

            while (direction==0)
                direction = (int)Math.signum(getRandomNumber(-1000, 1000));

            int jungleRangeX = (jungleUpperRight.x - jungleLowerLeft.x)/2;
            int rangeX = (mapUpperRight.x - mapLowerLeft.x)/2;
            int startX = (jungleLowerLeft.x+ jungleUpperRight.x)/2 +  direction*getRandomNumber(jungleRangeX+1, rangeX);

            direction = 0;
            while (direction==0)
                direction = (int)Math.signum(getRandomNumber(-1000, 1000));
            int jungleRangeY = (jungleUpperRight.y - jungleLowerLeft.y)/2;
            int rangeY = (mapUpperRight.y-mapLowerLeft.y)/2;
            int startY = (jungleLowerLeft.y+ jungleUpperRight.y)/2 + direction*getRandomNumber(jungleRangeY+1,rangeY);

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
        if (element instanceof Animal)
            removeAnimalFromMap((Animal) element);

        if (element instanceof Grass)
            removeGrassFromMAp((Grass) element);
    }

    protected boolean removeAnimalFromMap(Animal animal) {
        if (animalsList.containsKey(animal.getPosition())){

            Vector2d tmp = animal.getPosition();
            if (!animalsList.get(tmp).contains(animal)) {
                animalsList.get(tmp).contains(animal);
            }
//            System.out.println(animalsList.get(animal.getPosition()));

            animalsList.get(animal.getPosition()).remove(animal);


            if (animalsList.get(animal.getPosition()).isEmpty())
                animalsList.remove(animal.getPosition());

            return true;
        }
        return false;
    }

    protected boolean removeGrassFromMAp(Grass grass) {
        if (grassTiles.containsKey(grass.getPosition())){
            grassTiles.remove(grass.getPosition());
            return true;
        }
        return false;
    }



//    CHECKING ANIMAL MOVEMENT

    @Override
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

        if (animalsList.containsKey(position) && !animalsList.get(position).isEmpty()) {
            return animalsList.get(position).last();
        }

//        System.out.println(animalsList.get(position));
//        System.out.println(animalsList.get(position));
        return null;
    }

    public ArrayList<Animal> allAnimalsAt(Vector2d position){
//        if (!animalIsAt(position))
//            return null;
        return new ArrayList<>(animalsList.get(position));
    }

    @Override
    public IMapElement objectAt(Vector2d position) {
        Animal animal = strongestAnimalAt(position);
        if (animal != null)
            return animal;
        Grass grass = grassAt(position);
        if (grass != null)
            return grass;

        return null;
    }

    public abstract Vector2d translatePosition(Vector2d position);



//    OTHER
    public Set<Vector2d> animalsPositionsSet(){
        return animalsList.keySet();
    }

    public Set<Vector2d> grassPositionsSet(){
        return grassTiles.keySet();
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

    public String toString(){
        Vector2d lowerLeft = getLowerLeft();
        Vector2d upperRight = getUpperRight();
        MapVisualizer visualizer = new MapVisualizer(this);
        return visualizer.draw(lowerLeft, upperRight);
    }

    public void addObserversToMapElement(AbstractWorldElement element){
        for (IPositionChangeObserver observer : observersForMapElements)
            element.addObserver(observer);
    }

    protected static int getRandomNumber(int min, int max) {
        max+=1;
        return (int) ((Math.random() * (max - min)) + min);
    }

    protected boolean isInJungle(Vector2d position){
        return position.follows(jungleLowerLeft) && position.precedes(jungleUpperRight);
    }

    protected boolean isInStep(Vector2d position){
        return position.follows(mapLowerLeft) && position.precedes(mapUpperRight) && !isInJungle(position);
    }

    public void addObserverForAnimals(IPositionChangeObserver observer){
        observersForMapElements.add(observer);
    }

    public void removeObserverForAnimals(IPositionChangeObserver observer){
        observersForMapElements.remove(observer);
    }

    public boolean anyAnimalAlive(){
        return animalsList.size() > 0;
    }

    public int amountOfAnimalsAt(Vector2d position){
        return animalsList.get(position) != null ? animalsList.get(position).size() : 0;
    }






    public void animalCheck(){
        for ( Vector2d position : animalsList.keySet() ){
            for (Animal animal : animalsList.get(position)){
                if (!animal.getPosition().equals(position)) {
                    System.out.println("Juz sie zepsulo");
                    return;
                }
            }
        }
        System.out.println("jest ok");
    }
}

