package agh.ics.oop.gui;

import agh.ics.oop.interfaces.IMapElement;
import agh.ics.oop.mapElements.Grass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;


import java.io.FileNotFoundException;

public class GuiElementBox extends Node {
    private VBox vbox;

    public GuiElementBox( IMapElement element, int boxSize ) throws FileNotFoundException {

        vbox = new VBox(0);
        vbox.setPrefHeight(boxSize);
        vbox.setPrefWidth(boxSize);

        Node guiRepresentation = element.guiRepresentation(boxSize);
//        guiRepresentation.
        vbox.getChildren().add(guiRepresentation);

        vbox.setAlignment(Pos.CENTER);
    }

    public VBox getVbox(){
        return this.vbox;
    }



}
