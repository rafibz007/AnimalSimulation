package agh.ics.oop.interfaces;


import agh.ics.oop.mapElements.AbstractWorldElement;
import agh.ics.oop.mapElements.Vector2d;

import java.io.FileNotFoundException;

public interface IPositionChangeObserver {

    /*
    * Change position of object on the map from oldPosition to newPosition
    * */
    public void elementMovedFromPosition(Vector2d oldPosition, AbstractWorldElement element);
    public void elementRemoved(AbstractWorldElement element) throws FileNotFoundException;
    public void elementAdded(AbstractWorldElement element) throws FileNotFoundException;
}
