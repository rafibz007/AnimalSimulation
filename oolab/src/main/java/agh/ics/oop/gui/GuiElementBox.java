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
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;


import java.io.FileNotFoundException;

public class GuiElementBox extends Node {
    private final VBox vbox;
    private boolean highlight;

    public GuiElementBox( IMapElement element, int boxSize, boolean highlight ) {

        vbox = new VBox(0);
        this.highlight = highlight;

        Node representation = element.guiRepresentation(boxSize);
        vbox.getChildren().add(representation);

        vbox.setAlignment(Pos.CENTER);

        if (highlight && representation instanceof Shape){
            ((Shape) representation).setStroke(new Color(1,0,0,1));
            ((Shape) representation).setStrokeWidth(3);
            ((Shape) representation).setStrokeType(StrokeType.CENTERED);
        }
    }

    public GuiElementBox( IMapElement element, int boxSize ){
        this(element, boxSize, false);
    }


    public VBox getVbox(){
        return this.vbox;
    }



}
