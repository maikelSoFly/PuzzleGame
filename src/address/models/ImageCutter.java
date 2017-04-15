package address.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//
//     Project name: PuzzleGame
//
//     Created by maikel on 13.04.2017.
//     Copyright © 2017 Mikołaj Stępniewski. All rights reserved.
//

public class ImageCutter {
    private File file;
    private  int tilesAmount;
    private ObservableList<Tile> tilesList = FXCollections.observableArrayList();
    private int size;


    public ImageCutter(File file, int tilesAmount, int size) {
        this.file = file;
        this.tilesAmount = tilesAmount;
        this.size = size;
    }

    public ObservableList<Tile> cutImage() {
        BufferedImage image = setImage();

        if (image != null) {
            System.out.println("Image size: " + image.getWidth() + " " + image.getHeight());


            for(int i = 0; i < tilesAmount; i++) {
                for (int j = 0; j < tilesAmount; j++) {
                    BufferedImage part = image.getSubimage(
                            j * (size / tilesAmount),
                            i * (size / tilesAmount),
                            size / tilesAmount,
                            size / tilesAmount
                    );

                    Tile tile = new Tile(part.getWidth(), part.getHeight(), part, i * tilesAmount + j);
                    tile.setLayoutX(j * (tile.getWidth() ) + j*5);
                    tile.setLayoutY(i * (tile.getHeight() ) + i*5);

                    tile.setFill(new ImagePattern(SwingFXUtils.toFXImage(tile.getPart(), null)));
                    tilesList.add(tile);
                }
            }
        } else System.out.println("Image is null");

        return tilesList;
    }

    private BufferedImage setImage() {
        BufferedImage tmpImage = null;

        try {
            tmpImage = ImageIO.read(file);

        } catch (IOException e) {
            System.out.println("Cannot read image from file path");
            e.printStackTrace();
        }

        if(tmpImage != null) {
            if (tmpImage.getHeight() != size || tmpImage.getWidth() != size) {
                Image tmp = tmpImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = img.createGraphics();
                g2d.drawImage(tmp, 0, 0, null);
                g2d.dispose();
                return img;
            } else return tmpImage;
        } else return null;

    }
}
