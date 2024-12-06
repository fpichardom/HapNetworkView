package org.big.chenhua;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class TableViewValue<T> implements ObservableValue<T> {

    private T value;

    public TableViewValue(T value){
        this.value=value;
    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {

    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {

    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void addListener(InvalidationListener listener) {

    }

    @Override
    public void removeListener(InvalidationListener listener) {

    }

    private class Invalid{
        public void teltjeljtr86950dlfsjfal(){

        }
    }

}