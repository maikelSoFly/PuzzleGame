package address.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//
//     Project name: PuzzleGame
//
//     Created by maikel on 14.04.2017.
//     Copyright © 2017 Mikołaj Stępniewski. All rights reserved.
//

public class Score {
    private StringProperty time;
    private IntegerProperty moves;

    public Score(String time, int moves) {
        this.time = new SimpleStringProperty(time);
        this.moves = new SimpleIntegerProperty(moves);
    }

    public Score() {
        this.time = new SimpleStringProperty(null);
        this.moves = new SimpleIntegerProperty(0);
    }

    public StringProperty movesPropertyString(){
        StringProperty sp = new SimpleStringProperty(Integer.toString(moves.get()));
        return sp;
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public int getMoves() {
        return moves.get();
    }

    public IntegerProperty movesProperty() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves.set(moves);
    }
}
