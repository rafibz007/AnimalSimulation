package agh.ics.oop.interfaces;

import agh.ics.oop.mapElements.Animal;

public interface IDetailObserver {
    public void newOffspringBorn(Animal offspring);
    public void animalDied();
}
