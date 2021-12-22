package agh.ics.oop.mapElements;

import agh.ics.oop.enums.MoveDirection;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Gene {
    private final ArrayList<MoveDirection> genes;
    public static final int amount = 32;
    private final int[] amountsOfGenomes = {0,0,0,0,0,0,0,0};

    public Gene(){
        genes = new ArrayList<>();
        for (int i=0; i<amount; i++){
            int value = getRandomNumber(0, 8);
            switch (value) {
                case 0 -> genes.add(MoveDirection.FORWARD);
                case 1 -> genes.add(MoveDirection.TURN45DEG);
                case 2 -> genes.add(MoveDirection.TURN90DEG);
                case 3 -> genes.add(MoveDirection.TURN135DEG);
                case 4 -> genes.add(MoveDirection.BACKWARD);
                case 5 -> genes.add(MoveDirection.TURN225DEG);
                case 6 -> genes.add(MoveDirection.TURN270DEG);
                case 7 -> genes.add(MoveDirection.TURN315DEG);
            }
            amountsOfGenomes[value] += 1;
        }
        genes.sort(new Comparator<MoveDirection>() {
            @Override
            public int compare(MoveDirection o1, MoveDirection o2) {
                return o1.value - o2.value;
            }
        });
//        System.out.println(genes);
    }

    public Gene(ArrayList<MoveDirection> genes){
//        if (genes.size() != 32) throw IllegalArgumentException
        this.genes = genes;
        this.genes.sort(new Comparator<MoveDirection>() {
            @Override
            public int compare(MoveDirection o1, MoveDirection o2) {
                return o1.value - o2.value;
            }
        });
    }

    public ArrayList<MoveDirection> getGenes() {
        return genes;
    }

    public ArrayList<MoveDirection> getLeftGenes(int amount){
//        amount TODO
        int index = Math.min(amount, Gene.amount);
        return new ArrayList<>(genes.subList(0, index));
    }

    public ArrayList<MoveDirection> getRightGenes(int amount){
//        amount TODO sometimes somehow index get higher than Gene.amount
        int index = Math.max(0, Gene.amount-amount);
        return new ArrayList<>(genes.subList(index, Gene.amount));
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public MoveDirection getRandomMove(){
        return genes.get(getRandomNumber(0,amount-1));
    }

    
}
