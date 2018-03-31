package com.iu3.antiplugiat;

import com.iu3.antiplugiat.fxmlcontrollers.CentralSceneController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane mainScene;

    public void initMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/MainScene.fxml"));
            mainScene = (BorderPane) loader.load();

            Scene scene = new Scene(mainScene);
            primaryStage.setTitle("Антиплагиат ИУ3");
            primaryStage.setScene(scene);

        } catch (IOException e) {
            Logger.getLogger(com.iu3.antiplugiat.MainApp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void initCentralScene() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CentralScene.fxml"));
            AnchorPane centralScene = (AnchorPane) loader.load();

            mainScene.setCenter(centralScene);
        } catch (IOException e) {
            Logger.getLogger(com.iu3.antiplugiat.MainApp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void initAnalizeBar() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AnalizeBar.fxml"));
            AnchorPane analizeBar = (AnchorPane) loader.load();
            CentralSceneController.setAnalizeController(loader.getController());
            mainScene.setBottom(analizeBar);
        } catch (IOException e) {
            Logger.getLogger(com.iu3.antiplugiat.MainApp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage = stage;

        initMainScene();
        initCentralScene();
        initAnalizeBar();
        
        primaryStage.show();

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
