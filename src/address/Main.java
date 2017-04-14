package address;

import address.models.Score;
import address.models.ScoreWraper;
import address.views.Controller;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

//
//     Project name: PuzzleGame
//
//     Created by maikel on 14.04.2017.
//     Copyright © 2017 Mikołaj Stępniewski. All rights reserved.
//

public class Main extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private ObservableList<Score> scoreData = FXCollections.observableArrayList();
    private boolean scoreFileFound = true;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Puzzle");
        primaryStage.getIcons().add(new Image("http://orig06.deviantart.net/3717/f/2015/152/1/e/profile_picture_by_pepefrogplz-d8vnh1o.jpg"));

        showMainLayout();
    }

    private void showMainLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("./views/mainView.fxml"));
            rootLayout = (AnchorPane) loader.load();
            Controller controller = loader.getController();
            int w = 600 +(controller.getTilesInArow()+1)*5;
            rootLayout.setMaxWidth(w);
            rootLayout.setMinWidth(w);
            rootLayout.setPrefWidth(w);

            rootLayout.setMaxHeight(40+w);
            rootLayout.setMinHeight(40+w);
            rootLayout.setPrefHeight(40+w);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();


            controller.setMainApp( this );
            if(controller.getImageName().equals("ytho.jpg")) primaryStage.setTitle("Puzzle tho");
            else if(controller.getImageName().equals("frog.jpg")) primaryStage.setTitle("Puzzle Frog");

        }catch(IOException e) {
            e.printStackTrace();
        }

        // Try to load last opened product file.
        File file = getScoreFilePath();
        if (file != null) {
            loadScoreDataFromFile(file);
        }
    }

    public File getScoreFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public void setProductFilePath( File file ) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());
        } else {
            prefs.remove("filePath");
        }
    }

    public void loadScoreDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(ScoreWraper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            ScoreWraper wrapper = (ScoreWraper) um.unmarshal(file);

            scoreData.clear();
            scoreData.addAll(wrapper.getScores());

            // Save the file path to the registry.
            setProductFilePath(file);

        } catch (Exception e) { // catches ANY exception
            scoreFileFound = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    public void saveProductDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(ScoreWraper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our product data.
            ScoreWraper wrapper = new ScoreWraper();
            wrapper.setScores(scoreData);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            setProductFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<Score> getScoreData() {
        return scoreData;
    }

    public boolean isScoreFileFound() {
        return scoreFileFound;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
