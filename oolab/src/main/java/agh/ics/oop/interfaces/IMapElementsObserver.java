package agh.ics.oop.interfaces;


import agh.ics.oop.mapElements.AbstractWorldElement;
import agh.ics.oop.mapElements.Vector2d;

import java.io.FileNotFoundException;

public interface IMapElementsObserver {

    /*
    * Change position of object on the map from oldPosition
    * */
    public void elementMovedFromPosition(Vector2d oldPosition, AbstractWorldElement element);
    public void elementRemoved(AbstractWorldElement element);
    public void elementAdded(AbstractWorldElement element);
    public void elementHasNewChild(AbstractWorldElement parent);
    public void elementChangedEnergy(AbstractWorldElement element);
}
