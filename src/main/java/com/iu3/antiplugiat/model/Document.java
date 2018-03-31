/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.model;

import java.util.Objects;

/**
 *
 * @author Andalon
 */
public class Document {
    String docID;
    String path;
    String uniq;
    String plugID;

    public Document(String docID, String path) {
        this.docID = docID;
        this.path = path;
    }

    public Document(String docID, String path, String uniq, String plugID) {
        this.docID = docID;
        this.path = path;
        this.uniq = uniq;
        this.plugID = plugID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setUniq(String uniq) {
        this.uniq = uniq;
    }

    public void setPlugID(String plugID) {
        this.plugID = plugID;
    }

    public String getDocID() {
        return docID;
    }

    public String getPath() {
        return path;
    }

    public String getUniq() {
        return uniq;
    }

    public String getPlugID() {
        return plugID;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.docID);
        hash = 97 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Document other = (Document) obj;
        if (!Objects.equals(this.docID, other.docID)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return true;
    }  
}
