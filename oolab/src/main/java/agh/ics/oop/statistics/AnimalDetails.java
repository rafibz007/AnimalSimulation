package agh.ics.oop.statistics;

import agh.ics.oop.interfaces.IDetailObserver;
import agh.ics.oop.mapElements.Animal;
import agh.ics.oop.mapElements.Gene;

import java.util.HashSet;
import java.util.Set;

public class AnimalDetails implements IDetailObserver {
    private final Animal animal;
    private int childrenAmount = 0;
    private int offspringAmount = 0;
    private Integer eraOfDeath;
    private final Set<Animal> offspringAnimals = new HashSet<>();
    private final Set<Animal> childrenAnimals = new HashSet<>();

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
        offspringAnimals.add(offspring);

        if (offspring.getParents().contains(this.animal))
            childrenAnimals.add(offspring);

        childrenAmount = childrenAnimals.size();
        offspringAmount = offspringAnimals.size();
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
