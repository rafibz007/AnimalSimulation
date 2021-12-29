package agh.ics.oop.statistics;

import agh.ics.oop.interfaces.IDetailObserver;
import agh.ics.oop.mapElements.Animal;
import agh.ics.oop.mapElements.Gene;

public class AnimalDetails implements IDetailObserver {
    private Animal animal;
    private int childrenAmount = 0;
    private int offspringAmount = 0;
    private Integer eraOfDeath;

    public AnimalDetails(Animal animal){
        this.animal = animal;
    }

    public AnimalDetails(){
        this(null);
    }

    @Override
    public void newOffspringBorn(Animal offspring) {
        offspring.addDetailObserver(this);
        offspringAmount += 1;

        if (offspring.getParents().contains(this.animal))
            childrenAmount += 1;
    }

    @Override
    public void animalDied() {

    }

    public String getChildrenAmount() {
        if (animal == null)
            return "-";

        return String.valueOf(childrenAmount);
    }

    public String getOffspringAmount() {
        if (animal == null)
            return "-";

        return String.valueOf(offspringAmount);
    }

    public String getAnimalGene(){
        if (animal == null)
            return "-";

        return animal.gene.toString();
    }

    public String getEraOfDeath(){
        if (animal == null)
            return "-";

        return eraOfDeath != null ? String.valueOf(eraOfDeath) : "-";
    }
}
