package address.views;

import address.Main;
import address.models.*;
import address.models.Slider;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;

//
//     Project name: PuzzleGame
//
//     Created by maikel on 14.04.2017.
//     Copyright © 2017 Mikołaj Stępniewski. All rights reserved.
//

public class Controller implements Initializable {
    @FXML
    private AnchorPane panel;
    @FXML
    private Label lblTime;
    @FXML
    private Label lblMoves;
    @FXML
    BorderPane borderPane;
    @FXML
    private Label lblHighScore;

    private TableView tableView;
    private Main mainApp;
    private ObservableList<Tile> tilesList = FXCollections.observableArrayList();
    private Tile first = null;
    private Tile second = null;
    private int moves = 0;
    private Time time;
    private Timeline timeline;
    private boolean isStarted = false;
    private boolean isAnimationFinished = true;
    private int highScore = 0;
    private boolean isScoreBoardOpened = false;

    //SETTINGS
    final private int tilesInArow = 4;
    final private int timeLimit = 2; //minutes
    final private String imageName = "ytho.jpg";


    public Controller() {

    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        mainApp.getScoreData().addListener((ListChangeListener<Score>) c -> {
            while (c.next()) {
                if(c.wasAdded() || c.wasRemoved()) {
                   mainApp.getScoreData().sort(Comparator.comparing(Score::getMoves));
                   highScore = mainApp.getScoreData().get(0).getMoves();
                   lblHighScore.setText("HighScore: " +Integer.toString(highScore));
                    tableView.setItems(mainApp.getScoreData());
                }

            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int w = (tilesInArow-1)*5;

        panel.setMaxWidth(600+w);
        panel.setMinWidth(600+w);
        panel.setPrefWidth(600+w);

        panel.setMaxHeight(600+w);
        panel.setMinHeight(600+w);
        panel.setPrefHeight(600+w);

        createTableView();


        File imgFile = new File("out/production/PuzzleGame/assets/"+imageName);

        ImageCutter cutter = new ImageCutter(imgFile,tilesInArow, 600);
        tilesList = cutter.cutImage();
        panel.getChildren().addAll(tilesList);


        for (Tile tile : tilesList) {
            tile.setOnMouseClicked(event -> {
                if(isStarted && isAnimationFinished) {
                    if (first == null) {
                        first = (Tile) event.getSource();
                        first.setStrokeWidth(5);
                        first.setStroke(Color.GOLD);
                    } else {
                        second = (Tile) event.getSource();
                        if (second == first) {
                            first.setStrokeWidth(0);
                            first = null;
                        } else if (second != null && isNeighbour(second)) {
                            first.setStrokeWidth(0);
                            playAnimation();
                        }
                    }
                }
            });

            tile.setOnMouseMoved(event -> {
                if(isStarted && tile != first && (first == null || first != null && isNeighbour(tile) )) {
                    tile.setStrokeWidth(4);
                    tile.setStroke(Color.ORANGERED);
                }
            });

            tile.setOnMouseExited(event -> {
                if(tile != first)
                    tile.setStrokeWidth(0);
            });
        }
        time = new Time();
        timeline = new Timeline(new KeyFrame(
                Duration.millis(100),
                event -> {
                    if(time.getMinutes() == timeLimit) {
                        timeline.stop();
                        Platform.runLater(() -> {
                            timeline.stop();
                            isStarted = false;
                            if(first != null) {
                                first.setStrokeWidth(0);
                                first = null;
                            }
                            second = null;
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("You lose!");
                            alert.setHeaderText("Time is up!");
                            alert.setContentText("Better luck next time.");
                            alert.showAndWait();
                        });
                    }
                    time.updateTime();
                    lblTime.setText("Time: " +time.getTimeString());
                }
        ));

        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private boolean isNeighbour (Tile tile) {
        int indexFirst = tilesList.indexOf(first);
        int indexSecond = tilesList.indexOf(tile);
        int tir = tilesInArow;

        return indexFirst + tir == indexSecond ||
                indexFirst - tir == indexSecond ||
                (indexFirst % tir != 0 && indexFirst - 1 == indexSecond) ||
                ((indexFirst + 1) % tir != 0 && indexFirst + 1 == indexSecond);

    }

    private void playAnimation() {
        isAnimationFinished = false;
        first.toFront();
        second.toFront();

        PathTransition ptr = getPathTransition(first, second);
        PathTransition ptr2 = getPathTransition(second, first);
        ParallelTransition pt = new ParallelTransition(ptr, ptr2);

        pt.setOnFinished(event -> {
            first.setTranslateX(0);
            first.setTranslateY(0);
            second.setTranslateX(0);
            second.setTranslateY(0);
            swapPuzzles();
            isAnimationFinished = true;
        });
        pt.play();
    }

    private void swapPuzzles() {
        int indexFirst = tilesList.indexOf(first);
        int indexSecond = tilesList.indexOf(second);

        double firstX = first.getLayoutX();
        double firstY = first.getLayoutY();

        first.setLayoutX(second.getLayoutX());
        first.setLayoutY(second.getLayoutY());

        second.setLayoutX(firstX);
        second.setLayoutY(firstY);

        Collections.swap(tilesList, indexFirst, indexSecond);

        first = null;
        second = null;
        moves++;
        lblMoves.setText("Moves: " +Integer.toString(moves));

        if(checkWin()) {

            Platform.runLater(() -> {
                timeline.stop();
                isStarted = false;

                Score score = new Score(time.getTimeString(), moves);
                mainApp.getScoreData().add(score);

                File scoreFile = mainApp.getScoreFilePath();
                if(!mainApp.isScoreFileFound()) {
                    saveAs();
                }
                else if (scoreFile != null) {
                    mainApp.saveProductDataToFile(scoreFile);
                } else saveAs();

                boolean wasOpened = true;
                if(!isScoreBoardOpened) {
                    handleScoreTable();
                    wasOpened = false;
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Winner");

                if(mainApp.isScoreFileFound() &&
                moves < mainApp.getScoreData().get(1).getMoves())
                    alert.setHeaderText("NEW HIGHSCORE!");

                else alert.setHeaderText("You win!");
                alert.setContentText("Your time is: " +time.getTimeString()+ "\nMoves: " +moves);
                alert.showAndWait();
                if(!wasOpened)
                    handleScoreTable();
            });
        }
    }

    private void saveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.saveProductDataToFile(file);
        }
    }

    private boolean checkWin() {
        for (Tile tile : tilesList) {
            if(tile.getNum() != tilesList.indexOf(tile))
                return false;
        }
        return true;
    }

    @FXML
    private void handleShuffle() {
        moves = 0;
        lblMoves.setText("Moves: 0");
        time.setZero();

        lblTime.setText("Time: 0");
        Collections.shuffle(tilesList);
        for(int i = 0; i < tilesInArow; i++)
        for (int j = 0; j < tilesInArow; j++) {
            Tile tile = tilesList.get(i*tilesInArow+j);
            tile.setLayoutX(j * tile.getWidth() + j*5);
            tile.setLayoutY(i * tile.getHeight() + i*5);
        }
        isStarted = true;
        timeline.play();
    }

    private PathTransition getPathTransition(Tile first, Tile second) {
        PathTransition ptr = new PathTransition();
        Path path = new Path();
        path.getElements().clear();
        path.getElements().add(new MoveToAbs(first));
        path.getElements().add(new LineToAbs(first, second.getLayoutX(), second.getLayoutY()));
        ptr.setPath(path);
        ptr.setNode(first);
        return ptr;
    }

    public static class MoveToAbs extends MoveTo {
        MoveToAbs(Node node) {
            super(node.getLayoutBounds().getWidth() / 2,
                  node.getLayoutBounds().getHeight() / 2);
        }
    }
    public static class LineToAbs extends LineTo {
        LineToAbs(Node node, double x, double y) {
            super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2,
                  y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
        }
    }

    @FXML
    private void handleScoreTable() {
        if(!isScoreBoardOpened ) {
            if(tableView == null) {
                Slider slider = new Slider(mainApp.getPrimaryStage(), !isScoreBoardOpened);
                Thread th = new Thread(slider);
                th.setDaemon(true);
                th.start();
            } else {
                Slider slider = new Slider(mainApp.getPrimaryStage(), !isScoreBoardOpened);
                Thread th = new Thread(slider);
                th.setDaemon(true);
                th.start();
                borderPane.getRight().setVisible(true);
            }
            isScoreBoardOpened = true;

        } else {
            Slider slider = new Slider(mainApp.getPrimaryStage(), !isScoreBoardOpened);
            Thread th = new Thread(slider);
            th.setDaemon(true);
            th.start();
            isScoreBoardOpened = false;
        }

        for(Tile tile : tilesList) {
            if(tile.getNum() == 0) {
                tile.setFill(new ImagePattern(SwingFXUtils.toFXImage(tile.getPart(), null)));
            }
        }
    }

    private void createTableView() {
        tableView = new TableView();
        TableColumn<Score, String> tblColTime = new TableColumn<>("Time");
        TableColumn<Score, String> tblColMoves = new TableColumn<>("Moves");

        tblColTime.setPrefWidth(98);
        tblColMoves.setPrefWidth(100);
        tblColTime.setStyle("-fx-alignment: CENTER;");
        tblColMoves.setStyle("-fx-alignment: CENTER;");

        tblColTime.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        tblColMoves.setCellValueFactory(cellData -> cellData.getValue().movesPropertyString());

        tableView.getColumns().addAll(tblColMoves, tblColTime);
        tableView.setPrefWidth(200);
        borderPane.setRight(tableView);


    }


    public int getTilesInArow() {
        return tilesInArow;
    }

    public String getImageName() {
        return imageName;
    }
}

