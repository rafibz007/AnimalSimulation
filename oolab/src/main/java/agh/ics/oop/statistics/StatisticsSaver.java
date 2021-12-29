package agh.ics.oop.statistics;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// idealnie uzylbym opencsv, ale nie wiem czy mi wolno, wiec nie zaryzykuje
public class StatisticsSaver {
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Integer> amountOfAnimalsHistory = new ArrayList<>();
    private final List<Integer> amountOfGrassHistory = new ArrayList<>();
    private final List<Double> averageLifeLengthHistory = new ArrayList<>();
    private final List<Double> averageAnimalEnergyHistory = new ArrayList<>();
    private final List<Double> averageAnimalChildrenAmountHistory = new ArrayList<>();

    String fileName;

    public StatisticsSaver(String fileName){
        this.fileName = fileName;
    }

    public void save(){
        lock.lock();
        try {
            try (PrintWriter writer = new PrintWriter(fileName)){
                writer.write(header().toString());
                for (int i=0; i<amountOfAnimalsHistory.size(); i++)
                    writer.write(recordAt(i).toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }

    public StringBuilder header(){
        StringBuilder head = new StringBuilder();
        head.append("animalsAmount");
        head.append(",");
        head.append("grassAmount");
        head.append(",");
        head.append("avgLifeLength");
        head.append(",");
        head.append("avgEnergy");
        head.append(",");
        head.append("avgChildrenAmount");
        head.append("\n");

        return head;
    }

    public StringBuilder recordAt(int index){
        StringBuilder record = new StringBuilder();
        record.append(amountOfAnimalsHistory.get(index));
        record.append(",");
        record.append(amountOfGrassHistory.get(index));
        record.append(",");
        record.append(averageLifeLengthHistory.get(index));
        record.append(",");
        record.append(averageAnimalEnergyHistory.get(index));
        record.append(",");
        record.append(averageAnimalChildrenAmountHistory.get(index));
        record.append("\n");
        return record;
    }

    public void addAmountOfGrassHistory(int amount){
        lock.lock();
        try {
            amountOfGrassHistory.add(amount);
        } finally {
            lock.unlock();
        }
    }
    public void addAmountOfAnimalsHistory(int amount){
        lock.lock();
        try {
            amountOfAnimalsHistory.add(amount);
        } finally {
            lock.unlock();
        }
    }
    public void addAverageLifeLengthHistory(double average){
        lock.lock();
        try {
            averageLifeLengthHistory.add(average);
        } finally {
            lock.unlock();
        }
    }
    public void addAverageAnimalEnergyHistory(double average){
        lock.lock();
        try {
            averageAnimalEnergyHistory.add(average);
        } finally {
            lock.unlock();
        }
    }
    public void addAverageAnimalChildrenAmountHistory(double average){
        lock.lock();
        try {
            averageAnimalChildrenAmountHistory.add(average);
        } finally {
            lock.unlock();
        }
    }
}
