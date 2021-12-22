package agh.ics.oop.statistics;

import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.mapElements.Gene;

import java.util.HashMap;
import java.util.Map;

public class Statistics //implements IPositionChangeObserver
         {
    private int AmountOfGrass;
    private int AmountOfAnimals;
    private Gene dominantGene;
    private final Map<Gene, Integer> GeneAmount = new HashMap<Gene, Integer>();



}
