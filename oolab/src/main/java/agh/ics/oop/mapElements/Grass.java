package agh.ics.oop.mapElements;

import agh.ics.oop.interfaces.IMapElement;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;


public class Grass extends AbstractWorldElement implements IMapElement {
    private final int energy;


    public Grass(Vector2d position, int energy){
        super(position);
        this.position = position;
        this.energy = energy;
    }

    public String toString(){return "*";}

    public int getEnergy() {
        return energy;
    }

    @Override
    public Node guiRepresentation(int boxSize) {
        Rectangle rectangle = new Rectangle(boxSize,boxSize);
        rectangle.setFill(new Color(0,1,0,1));
        return rectangle;
    }
}
