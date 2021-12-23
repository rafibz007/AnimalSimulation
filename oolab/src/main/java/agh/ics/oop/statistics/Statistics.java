package agh.ics.oop.statistics;

import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.mapElements.*;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.util.*;

public class Statistics implements IPositionChangeObserver {
    //    todo nie zlicza wszystkich zwierzat i traw, czesto brakuje
    private int amountOfGrass;
    private int amountOfAnimals;

//    Map : Gene -> AmountOfIt
    private final Map<Gene, Integer> geneAmount = new HashMap<Gene, Integer>();

//    Map: AmountOfGenes -> SetOfGenesWithAmount
    private final SortedMap< Integer, Set<Gene> > dominantGene = new TreeMap<>();


     @Override
     public void elementMovedFromPosition(Vector2d oldPosition, AbstractWorldElement element) {
//         todo tu chyba nic nie trzeba
     }

    @Override
    public void elementAdded(AbstractWorldElement element) throws FileNotFoundException {
        if (element instanceof Grass)
            amountOfGrass += 1;

        if (element instanceof Animal){
            amountOfAnimals += 1;
            Gene gene = ((Animal) element).gene;

            addGene(gene);

        }

    }

    @Override
    public void elementRemoved(AbstractWorldElement element) throws FileNotFoundException {
        if (element instanceof Grass)
            amountOfGrass -= 1;

        if (element instanceof Animal){
            amountOfAnimals -= 1;
            Gene gene = ((Animal) element).gene;

            removeGene(gene);
        }
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "amountOfGrass=" + amountOfGrass +
                ", amountOfAnimals=" + amountOfAnimals +
                ", dominantGene=" + getDominantGenes() +
                ", geneAmount=" + geneAmount.keySet() +
                '}';
    }


    public Set<Gene> getDominantGenes(){
         if (dominantGene.size() == 0)
             return new HashSet<Gene>();
        Set<Gene> potentiallyDominant = dominantGene.get(dominantGene.firstKey());
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
}
