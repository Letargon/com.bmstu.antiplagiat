package com.iu3.antiplugiat;

import com.iu3.antiplugiat.service.DocManager;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXMLController implements Initializable {
    
    private DocManager docManager;

    @FXML
    public static Stage STAGE;

    @FXML
    private Label uniqAttrLabel;

    @FXML
    private void handleButtonChooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setTitle("Open Document");//Заголовок диалога
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Doc", "*.doc"),
                new FileChooser.ExtensionFilter("Docx", "*.docx")
        );
        File file = fileChooser.showOpenDialog(STAGE);//Указываем текущую сцену CodeNote.mainStage
        if (file != null) {
            docManager=new DocManager(file.getPath());
        }
    }
    @FXML
    private void handleButtonLoad(ActionEvent event){
        if (docManager!=null)
            docManager.loadToDatabase();
    }
    @FXML
    private void handleButtonAnalize(ActionEvent event){
        if (docManager!=null)
            uniqAttrLabel.setText(Double.toString(docManager.getuniqueAttr()));
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
