package address.models;

import javafx.scene.shape.*;
import java.awt.image.BufferedImage;

//
//     Project name: PuzzleGame
//
//     Created by maikel on 12.04.2017.
//     Copyright © 2017 Mikołaj Stępniewski. All rights reserved.
//

public class Tile extends Rectangle {
    private BufferedImage part;
    private int num;


    Tile(double width, double height, BufferedImage part, int num) {
        super(width, height);
        this.part = part;
        this.num = num;
    }

    //GETTERS & SETERS
    public BufferedImage getPart() {
        return part;
    }

    public void setPart(BufferedImage part) {
        this.part = part;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
