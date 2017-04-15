package address.models;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

//
//     Project name: PuzzleGame
//
//     Created by maikel on 15.04.2017.
//     Copyright © 2017 Mikołaj Stępniewski. All rights reserved.
//

public class Slider {
    private Stage primaryStage;
    private Timeline timeline;
    private double width;
    private boolean direction;
    private double pw;


    public Slider(Stage primaryStage, boolean direction) {
        this.primaryStage = primaryStage;
        this.direction = direction;
        this.pw = 625;
        if(direction) {
            this.width = 0;
        }
        else {
            this.width = 200;
        }

        timeline = new Timeline(new KeyFrame(
                Duration.millis(20),
                event-> {
                    if(direction)
                        slideOut();
                    else slideIn();
                }
        ));
    }

    public Object play()  {


        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        return null;
    }

    private void slideOut() {
        if(width < 200) {
            width += 10;
            primaryStage.setWidth(pw + width);
        }
        else timeline.stop();
    }

    private void slideIn() {
        if(width > 0) {
            width -= 10;
            primaryStage.setWidth(pw + width);
        }
        else timeline.stop();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public double getWidth() {
        return width;
    }

    public boolean isDirection() {
        return direction;
    }

    public double getPw() {
        return pw;
    }
}
