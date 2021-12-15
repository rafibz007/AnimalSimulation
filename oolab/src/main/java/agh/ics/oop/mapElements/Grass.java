package agh.ics.oop.mapElements;

import agh.ics.oop.interfaces.IMapElement;
import agh.ics.oop.interfaces.IPositionChangeObserver;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
    public ImageView guiRepresentationImageView() throws FileNotFoundException {
        Image image = new Image( new FileInputStream("src/main/resources/grass.png"));
        return new ImageView(image);
    }

    @Override
    public Label guiRepresentationLabel() {
        return new Label("Trawa");
    }


}
