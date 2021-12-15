package agh.ics.oop.interfaces;

import javafx.scene.image.ImageView;

import javafx.scene.control.Label;

import java.io.FileNotFoundException;

public interface IMapElement {
    public Label guiRepresentationLabel();
    public ImageView guiRepresentationImageView() throws FileNotFoundException;
}
