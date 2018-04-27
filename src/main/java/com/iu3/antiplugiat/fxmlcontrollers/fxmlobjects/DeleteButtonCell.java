/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.fxmlcontrollers.fxmlobjects;

import com.iu3.antiplugiat.model.Document;
import com.iu3.antiplugiat.service.database.local.DocManager;
import java.sql.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

/**
 *
 * @author Andalon
 */
public class DeleteButtonCell extends TableCell<Document, String> {

    final Button cellButton = new Button("Delete");
    DocManager docManager;

    public DeleteButtonCell(Connection connect) {

        docManager = new DocManager(connect);
        //Action when the button is pressed
        cellButton.setOnAction((ActionEvent t) -> {
            // get Selected Item
            Document curDoc = (Document) DeleteButtonCell.this.getTableView().getItems().get(DeleteButtonCell.this.getIndex());
            //remove selected item from the table list
            docManager.deleteDoc(curDoc.getDocID());
            ObservableList<Document> docList = FXCollections.observableArrayList(docManager.getDocumentList());
            this.getTableView().setItems(docList);
        });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setGraphic(cellButton);
        }
    }
}
