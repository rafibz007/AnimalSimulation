package agh.ics.oop.interfaces;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

import javafx.scene.control.Label;

import java.awt.*;
import java.io.FileNotFoundException;

public interface IMapElement {
    public Node guiRepresentation(int boxSize);
}
