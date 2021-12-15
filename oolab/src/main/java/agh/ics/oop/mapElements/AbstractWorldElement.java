package agh.ics.oop.mapElements;

import agh.ics.oop.interfaces.IPositionChangeObserver;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class AbstractWorldElement {
    final List<IPositionChangeObserver> Observers = new ArrayList<>();
    protected Vector2d position;

    AbstractWorldElement(Vector2d position){
        this.position = position;
    }

    public void add() {
        for (IPositionChangeObserver observer : Observers){
            try {
                observer.elementAdded(this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void remove() {
        for (IPositionChangeObserver observer : Observers){
            try {
                observer.elementRemoved(this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Vector2d getPosition() {
        return position;
    }
    public void addObserver(IPositionChangeObserver observer){
        Observers.add(observer);
    }
    public void removeObserver(IPositionChangeObserver observer){
        Observers.remove(observer);
    }

}
