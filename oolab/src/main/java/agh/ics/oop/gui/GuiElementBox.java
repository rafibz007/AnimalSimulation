package agh.ics.oop.gui;

import agh.ics.oop.interfaces.IMapElement;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.io.FileNotFoundException;

public class GuiElementBox extends Node {
    private ImageView imageView;
    private Label label;
    private VBox vbox;
    private final int width = 20;
    private final int height = 20;


    public GuiElementBox( IMapElement element ) throws FileNotFoundException {

        imageView = element.guiRepresentationImageView();
        label = element.guiRepresentationLabel();



        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        label.setFont(new Font(8));

        vbox = new VBox(-5);
        vbox.setPrefHeight(height);
        vbox.setPrefWidth(width);

        vbox.getChildren().add(0, imageView);
        vbox.getChildren().add(1, label);

        vbox.setAlignment(Pos.CENTER);
    }

    public VBox getVbox(){
        return this.vbox;
    }



}
