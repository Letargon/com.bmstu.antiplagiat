/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Andalon
 */
public class Document {
    
    private final StringProperty docID = new SimpleStringProperty();
    private final StringProperty path = new SimpleStringProperty();
    private final StringProperty uniq = new SimpleStringProperty();
    private final StringProperty plugID = new SimpleStringProperty();
    private final StringProperty tsize = new SimpleStringProperty();

    public String getTsize() {
        return tsize.get();
    }

    public void setTsize(String value) {
        tsize.set(value);
    }

    public StringProperty tsizeProperty() {
        return tsize;
    }

    
    
    public Document(String docID, String path, String tsize, String uniq, String plugID) {
        this.docID.set(docID);
        this.path.set(path);
        this.uniq.set(uniq);
        this.plugID.set(plugID);
        this.tsize.set(tsize);
        
    }

    
    public String getPlugID() {
        return plugID.get();
    }

    public void setPlugID(String value) {
        plugID.set(value);
    }

    public StringProperty plugIDProperty() {
        return plugID;
    }

    public String getUniq() {
        return uniq.get();
    }

    public void setUniq(String value) {
        uniq.set(value);
    }

    public StringProperty uniqProperty() {
        return uniq;
    }

    public String getPath() {
        return path.get();
    }

    public void setPath(String value) {
        path.set(value);
    }

    public StringProperty pathProperty() {
        return path;
    }

    public String getDocID() {
        return docID.get();
    }

    public void setDocID(String value) {
        docID.set(value);
    }

    public StringProperty docIDProperty() {
        return docID;
    }
    
}
