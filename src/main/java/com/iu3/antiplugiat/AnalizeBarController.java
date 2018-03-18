package com.iu3.antiplugiat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Andalon
 */
public class AnalizeBarController implements Initializable {

    @FXML
    Label uniqueLabel;

    @FXML
    AnchorPane uniquePane;

    @FXML
    Label plugLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    public void dropUnique(){
        uniqueLabel.setText("? %");
    }

    public void setUniqueNum(double uniq) {
        uniqueLabel.setText(Double.toString(uniq*100).substring(0,4)+" %");
        
        String red = Integer.toString((int)Math.round(255 * (1. - uniq)));
        String green = Integer.toString((int)Math.round(255 * uniq));
        String blue = "0";
        String color = "rgb(" + red + "," + green + "," + blue + ")";
        uniquePane.setStyle("-fx-background-color:" + color);

    }
    public void setplugDoc(String dir){
        plugLabel.setText(dir);
    }

}
