package com.iu3.antiplugiat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.iu3.antiplugiat.service.DocManager;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Andalon
 */
public class CentralSceneController implements Initializable {

    DocManager docManager;

    static AnalizeBarController AN_BAR;
    
    String plugiat;

    @FXML
    VBox dragTarget;

    @FXML
    Button loadButton;

    @FXML
    Label fileDirLabel;

    @FXML
    Label dropped;

    @FXML
    public static Stage STAGE;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    static void setStage(Stage stage) {
        STAGE = stage;
    }

    static void setAnalizeController(AnalizeBarController anBar) {
        AN_BAR = anBar;
    }

    @FXML
    private void handleButtonChooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setTitle("Открыть документ");//Заголовок диалога
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Doc", "*.doc"),
                new FileChooser.ExtensionFilter("Docx", "*.docx")
        );
        File file = fileChooser.showOpenDialog(STAGE);//Указываем текущую сцену CodeNote.mainStage
        if (file != null) {
            docManager = new DocManager(file.getPath());
            fileDirLabel.setText(file.getPath());
            AN_BAR.dropUnique();
        }
        

    }

    @FXML
    private void handleButtonLoad(ActionEvent event) {
        if (docManager != null) {
            docManager.loadToDatabase();
        }
    }

    @FXML
    private void handleButtonAnalize(ActionEvent event) {
        if (docManager != null) {
            Double uniq = docManager.getuniqueAttr();
            String plagiat=docManager.getPlugiatDoc();
            AN_BAR.setUniqueNum(uniq);
            AN_BAR.setplugDoc(plagiat);
        }

    }

    @FXML
    private void handleVBoxDragOver(DragEvent event) {
        if (event.getGestureSource() != dragTarget
                && event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }

    @FXML
    private void handleVBoxDragDropped(DragEvent event) {

        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String[] dirSplitted = db.getString().split("/");
            dropped.setText(dirSplitted[dirSplitted.length - 1]);
            success = true;
        }
        /* let the source know whether the string was successfully 
                 * transferred and used */
        event.setDropCompleted(success);

        event.consume();
    }

    @FXML
    private void handleVBoxDragDone(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasString()) {
            docManager = new DocManager(db.getString());
            fileDirLabel.setText(db.getString());
        }
    }

}
