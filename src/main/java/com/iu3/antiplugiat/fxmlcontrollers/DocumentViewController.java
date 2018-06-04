package com.iu3.antiplugiat.fxmlcontrollers;

import com.iu3.antiplugiat.fxmlcontrollers.fxmlobjects.DeleteButtonCell;
import com.iu3.antiplugiat.model.Document;
import com.iu3.antiplugiat.service.database.local.ConnectionPool;
import com.iu3.antiplugiat.service.database.local.DocManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @author Andalon
 */
public class DocumentViewController implements Initializable {

    private DocManager docManager;
    
    @FXML
    TableView<Document> tableView;
    @FXML
    TableColumn<Document, String> docIdColumn;
    @FXML
    TableColumn<Document, String> pathColumn;
    @FXML
    TableColumn<Document, String> uniqColumn;
    @FXML
    TableColumn<Document, String> plugiatidColumn;
    @FXML
    TableColumn<Document, String> tsizeColumn;
    @FXML
    TableColumn<Document, String> delColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        docManager = new DocManager(ConnectionPool.createDefaultConnection());
        //установка cellFactory для каждой колонны - определение источника данных ячеек в колоне.
        docIdColumn.setCellValueFactory(cellValue->cellValue.getValue().docIDProperty());
        pathColumn.setCellValueFactory(cellValue->cellValue.getValue().pathProperty());
        uniqColumn.setCellValueFactory(cellValue->cellValue.getValue().uniqProperty());
        plugiatidColumn.setCellValueFactory(cellValue->cellValue.getValue().plugIDProperty());
        tsizeColumn.setCellValueFactory(cellValue->cellValue.getValue().tsizeProperty());
        delColumn.setCellValueFactory(new PropertyValueFactory<>("DELETE"));
        //Adding the Button to the cell
        delColumn.setCellFactory((TableColumn<Document, String> p) -> new DeleteButtonCell(ConnectionPool.createDefaultConnection()));
        
        ObservableList<Document> docList = FXCollections.observableArrayList(docManager.getDocumentList());
        ConnectionPool.closeConnection(docManager.getConnect());
        tableView.setItems(docList);   
    }
}
