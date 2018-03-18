package com.iu3.antiplugiat;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//не поддерживает rtf
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
            e.printStackTrace();
        }
    }

    public void showCentralScene() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CentralScene.fxml"));
            AnchorPane centralScene = (AnchorPane) loader.load();

            mainScene.setCenter(centralScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAnalizeBar() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AnalizeBar.fxml"));
            AnchorPane analizeBar = (AnchorPane) loader.load();
            CentralSceneController.setAnalizeController(loader.getController());
            mainScene.setBottom(analizeBar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage = stage;

        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));

        //Scene scene = new Scene(root);
        //scene.getStylesheets().add("/styles/Styles.css");

        //stage.setTitle("JavaFX and Maven");
        //stage.setScene(scene);
        //CentralSceneController.setStage(stage);
        //FXMLController.STAGE = stage;
        initMainScene();
        showCentralScene();
        showAnalizeBar();
        primaryStage.show();
        //stage.show();

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
