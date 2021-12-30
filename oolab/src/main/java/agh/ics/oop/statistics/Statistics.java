package agh.ics.oop.statistics;

import agh.ics.oop.interfaces.IEngineObserver;
import agh.ics.oop.interfaces.IMapElementsObserver;
import agh.ics.oop.mapElements.*;

import java.util.*;

// średniej liczby dzieci dla żyjących zwierząt - rozumiem jako srednia ilosc ogolnie dzieci splodzonych, a nie splodzonych i zyjacych
// todo ta srednia cos nie dziala, liczy duzo za duzo
public class Statistics implements IMapElementsObserver, IEngineObserver {
    private int amountOfGrass = 0;
    private int amountOfAnimals = 0;

    private double averageLifeLength = 0;
    private int amountOfDeadAnimals = 0;

    private final Map<Animal, Integer> animalEnergy = new HashMap<>();
    private final Map<Animal, Set<Animal>> animalChildren = new HashMap<>();

    String fileName;
    public final StatisticsSaver statisticsSaver;




//    Map : Gene -> AmountOfIt
    private final Map<Gene, Integer> geneAmount = new HashMap<>();

//    Map: AmountOfGenes -> SetOfGenesWithAmount
    private final SortedMap< Integer, Set<Gene> > dominantGene = new TreeMap<>();

    public Statistics(String fileName){
        this.fileName = fileName;
        statisticsSaver = new StatisticsSaver(fileName);
    }

     @Override
     public void elementMovedFromPosition(Vector2d oldPosition, AbstractWorldElement element) {
//         nothing
     }

    @Override
    public void elementAdded(AbstractWorldElement element) {
        if (element instanceof Grass)
            amountOfGrass += 1;

        if (element instanceof Animal){

            Gene gene = ((Animal) element).gene;

            addGene(gene);


            animalEnergy.put((Animal) element, ((Animal) element).energy);
            amountOfAnimals += 1;
        }

    }

    @Override
    public void elementRemoved(AbstractWorldElement element) {
        if (element instanceof Grass)
            amountOfGrass -= 1;

        if (element instanceof Animal){


//            CALCULATE NEW AVERAGE LIFE LENGTH
            averageLifeLength = averageLifeLength*amountOfDeadAnimals + ((Animal) element).lifeLength;
            amountOfDeadAnimals += 1;
            averageLifeLength /= amountOfDeadAnimals;

//            DELETE GENE
            Gene gene = ((Animal) element).gene;

            removeGene(gene);


            animalEnergy.remove((Animal) element);
            amountOfAnimals -= 1;
        }
    }

    public Set<Gene> getDominantGenes(){
         if (dominantGene.size() == 0)
             return new HashSet<Gene>();
//        Set<Gene> potentiallyDominant = dominantGene.get(dominantGene.firstKey());
        return dominantGene.get(dominantGene.firstKey());
    }

    private void addGene(Gene gene){
//         REMOVE OLD FROM DOMINANT
        if (geneAmount.containsKey(gene)) {
            dominantGene.get(geneAmount.get(gene)).remove(gene);
            if (dominantGene.get(geneAmount.get(gene)).size() == 0){
                dominantGene.remove(geneAmount.get(gene));
            }
        }

//        ADD NEW TO DOMINANT
        if (!dominantGene.containsKey(geneAmount.getOrDefault(gene, 0)+1))
            dominantGene.put(geneAmount.getOrDefault(gene, 0)+1, new HashSet<Gene>());
        dominantGene.get(geneAmount.getOrDefault(gene, 0)+1).add(gene);

//        UPDATE AMOUNT
        if (!geneAmount.containsKey(gene))
            geneAmount.put(gene, 0);
        geneAmount.put( gene, geneAmount.get(gene)+1 );

    }

    private void removeGene(Gene gene){
//         REMOVE OLD FROM DOMINANT
        dominantGene.get(geneAmount.get(gene)).remove(gene);
        if (dominantGene.get(geneAmount.get(gene)).size() == 0)
            dominantGene.remove(geneAmount.get(gene));

//        ADD NEW TO DOMINANT
        if (geneAmount.get(gene)-1 > 0){
            if (!dominantGene.containsKey(geneAmount.get(gene)-1)) {
                dominantGene.put(geneAmount.get(gene)-1, new HashSet<>());
            }
            dominantGene.get(geneAmount.get(gene)-1).add(gene);
        }

//        UPDATE AMOUNT
        if (geneAmount.get(gene)-1 > 0)
            geneAmount.put(gene, geneAmount.get(gene)-1);
        else geneAmount.remove(gene);
    }

    @Override
    public void elementChangedEnergy(AbstractWorldElement element) {
         if (element instanceof Animal)
            animalEnergy.put((Animal) element, ((Animal) element).energy);
    }

    public synchronized double getAverageEnergy(){
         double sum = 0;
         Collection<Integer> energies = new ArrayList<>(animalEnergy.values());
         for (Integer integer : energies)
             sum += integer;
         return sum / amountOfAnimals;
    }

    @Override
    public void elementHasNewChild(AbstractWorldElement parent) {
        if (parent instanceof Animal animal) {
            animalChildren.get(animal).add();
        }
    }

    public synchronized double getAverageChildrenAmount(){
         double sum = 0;
         for (Set<Animal> set : animalChildren.values()){
            int amount = set.size();
            sum += amount;
         }
         return sum / amountOfAnimals;
    }

    @Override
    public void dayEnded() {
         statisticsSaver.addAmountOfGrassHistory(amountOfGrass);
         statisticsSaver.addAmountOfAnimalsHistory(amountOfAnimals);
         statisticsSaver.addAverageLifeLengthHistory(averageLifeLength);
         statisticsSaver.addAverageAnimalEnergyHistory(getAverageEnergy());
         statisticsSaver.addAverageAnimalChildrenAmountHistory(getAverageChildrenAmount());
    }

    @Override
    public void magicHappened() {
//         nothing
    }

    public synchronized int getAmountOfAnimals() {
        return amountOfAnimals;
    }

    public synchronized int getAmountOfGrass() {
        return amountOfGrass;
    }

    public synchronized double getAverageLifeLength() {
        return averageLifeLength;
    }

    public Integer amountOfGene(Gene gene){
         return geneAmount.getOrDefault(gene, 0);
    }
}
