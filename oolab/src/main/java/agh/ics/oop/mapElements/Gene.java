package agh.ics.oop.mapElements;

import agh.ics.oop.enums.MoveDirection;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Gene {
    public static final int amount = 32;
    private int[] amountsOfGenomes = {0,0,0,0,0,0,0,0};

    public Gene(){
        for (int i=0; i<amount; i++){
            int value = getRandomNumber(0, 8);
            amountsOfGenomes[value] += 1;
        }
    }

    public Gene(int[] amountsOfGenomes){
        int sum = 0;
        for (int i=0; i< amountsOfGenomes.length; i++)
            sum += amountsOfGenomes[i];

        if (sum != amount)
            throw new IllegalArgumentException("Given gene is wrong");

        this.amountsOfGenomes = amountsOfGenomes;
    }

    public static int[] joinGenes(int[] gene1, int[] gene2){
        if (gene1.length != gene2.length)
            throw new IllegalArgumentException("Genes are different size");

        for (int i=0; i<gene2.length; i++){
            gene1[i] += gene2[i];
        }

        return gene1;
    }

    public int[] getGenes() {
        return amountsOfGenomes;
    }

    public int[] getLeftGenes(int amount){
        int index = 0;
        int[] genePart = {0,0,0,0,0,0,0,0};
        while (amount > 0 && index < 8){
            genePart[index] += Math.min(amount,amountsOfGenomes[index]);
            amount -= Math.min(amount,amountsOfGenomes[index]);
            index += 1;
        }
        return genePart;
    }

    public int[] getRightGenes(int amount){
        int index = 7;
        int[] genePart = {0,0,0,0,0,0,0,0};
        while (amount > 0 && index >= 0){
            genePart[index] += Math.min(amount,amountsOfGenomes[index]);
            amount -= Math.min(amount,amountsOfGenomes[index]);
            index -= 1;
        }
        return genePart;
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public MoveDirection getRandomMove(){
        int moveIndex = 0;
        int amount = getRandomNumber(1, Gene.amount+1);
        while (amount > 0 && moveIndex<8){
            amount -= Math.min(amount, amountsOfGenomes[moveIndex]);
            if (amount > 0)
                moveIndex += 1;
        }
        return switch (moveIndex){
            case 0 -> MoveDirection.FORWARD;
            case 1 -> MoveDirection.TURN45DEG;
            case 2 -> MoveDirection.TURN90DEG;
            case 3 -> MoveDirection.TURN135DEG;
            case 4 -> MoveDirection.BACKWARD;
            case 5 -> MoveDirection.TURN225DEG;
            case 6 -> MoveDirection.TURN270DEG;
            case 7 -> MoveDirection.TURN315DEG;
            default -> throw new IndexOutOfBoundsException("Couldn't calculate genomes properly");
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gene)) return false;
        Gene gene = (Gene) o;
        return Arrays.equals(amountsOfGenomes, gene.amountsOfGenomes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(amountsOfGenomes);
    }

    @Override
    public String toString() {
        String result = "";
        for (int i=0; i<amountsOfGenomes.length; i++){
            if (i == amountsOfGenomes.length-1)
                result += amountsOfGenomes[i];
            else
                result += amountsOfGenomes[i] + ":";
        }
        return result;
    }
}
