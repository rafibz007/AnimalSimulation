package agh.ics.oop.statistics;

import agh.ics.oop.interfaces.IDetailObserver;
import agh.ics.oop.mapElements.Animal;
import agh.ics.oop.mapElements.Gene;

// todo, offspring potrafi siegac wartosci po 100k i zaczyna niemilosiernie wtedy lagowac, trzeba bedzie znalezl blad
public class AnimalDetails implements IDetailObserver {
    private Animal animal;
    private int childrenAmount = 0;
    private int offspringAmount = 0;
    private Integer eraOfDeath;

    public AnimalDetails(Animal animal){
        this.animal = animal;
        if (animal != null) {
            animal.hasDetailsTracked = true;
            animal.addDetailObserver(this);
        }
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
    public void animalDied(Animal animal) {
        if (this.animal.equals(animal))
            eraOfDeath = animal.eraOfBirth+animal.lifeLength;
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
