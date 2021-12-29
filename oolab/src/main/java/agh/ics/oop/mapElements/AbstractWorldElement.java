package agh.ics.oop.mapElements;

import agh.ics.oop.interfaces.IMapElementsObserver;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class AbstractWorldElement {
    final List<IMapElementsObserver> Observers = new ArrayList<>();
    protected Vector2d position;

    AbstractWorldElement(Vector2d position){
        this.position = position;
    }

    public void add() {
        for (IMapElementsObserver observer : Observers){
            observer.elementAdded(this);
        }
    }

    public void remove() {
        for (IMapElementsObserver observer : Observers){
            observer.elementRemoved(this);
        }
    }



    public Vector2d getPosition() {
        return position;
    }
    public void addObserver(IMapElementsObserver observer){
        Observers.add(observer);
    }
    public void removeObserver(IMapElementsObserver observer){
        Observers.remove(observer);
    }

}
