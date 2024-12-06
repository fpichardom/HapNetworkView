package org.big.chenhua;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;

public class XChoiceBox1<T> extends ChoiceBox<T> {

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        this.setStyle("-fx-background-color: #c0c0c0;-fx-font-size:14px;-fx-background-radius: 5;-fx-mark-color: rgb(0,175,239);");
        Node label =lookup(".label");
        label.setStyle("-fx-text-fill: rgb(0,0,0);-fx-font-family: 'Times New Roman';-fx-font-size: 14;");
    }

    private class Invalid{
        public void tesltjesltjeslre(){

        }
    }

}
