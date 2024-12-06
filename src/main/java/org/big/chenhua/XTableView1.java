package org.big.chenhua;

import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public class XTableView1<T> extends TableView<T> {

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        Pane header = (Pane) lookup("TableHeaderRow");
        header.setMinHeight(20);
        header.setPrefHeight(20);
        header.setMaxHeight(20);
        header.setVisible(true);
        this.setStyle("-fx-background-radius: 5;-fx-background-color: #eaf5fa;");
    }


    private class Invalid{
        public void testklesjtrlresjrfl(){

        }
    }

}
