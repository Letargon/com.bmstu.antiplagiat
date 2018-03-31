/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service.database.local;

import com.iu3.antiplugiat.model.Document;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andalon
 */
public class DocManager extends LocalManager {

    public void addDoc(String docDir) {
        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict
            if (!connect.isValid(0)) {
                createConnection();
            }

            Statement stmt = connect.createStatement();

            stmt.executeUpdate("INSERT INTO DOCS (PATH) VALUES"//check sql syntex for insert
                    + "("
                    + "'" + docDir + "'"
                    + ");");

            stmt.close();

        } catch (SQLException ex) {
            //нужно подумать об ошибке уникальности, вывод сообщения
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getDocId(String docDir) {
        int docId = -1;
        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict
            if (!connect.isValid(0)) {
                createConnection();
            }

            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DOC_ID FROM DOCS WHERE PATH='" + docDir + "';");
            if (rs.next()) {
                docId = rs.getInt("DOC_ID");
            }
            rs.close();

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docId;
    }

    public String getDocDir(String docID) {
        String docDir = new String();
        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict
            if (!connect.isValid(0)) {
                createConnection();
            }
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT PATH FROM DOCS WHERE DOC_ID='" + docID + "';");
            if (rs.next()) {
                docDir = rs.getString("PATH");
            }
            rs.close();

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docDir;
    }

    public void writePlugInfo(String docID, String uniq, String plugID) {
        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict
            if (!connect.isValid(0)) {
                createConnection();
            }

            Statement stmt = connect.createStatement();

            stmt.executeUpdate("UPDATE DOCS SET"//check sql syntex for insert
                    + " UNIQ ='" + uniq + "'"
                    + " PLUGID ='" + plugID + "'"
                    + " WHERE DOC_ID ='" + docID + "';");

            stmt.close();

        } catch (SQLException ex) {
            //нужно подумать об ошибке уникальности, вывод сообщения
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Document> getDocumentList() {
        List<Document> docList = new ArrayList<>();

        String docDir = new String();
        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict
            if (!connect.isValid(0)) {
                createConnection();
            }
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM DOCS;");
            while (rs.next()) {
                String docID = rs.getString("DOC_ID");
                String path = rs.getString("PATH");
                String uniq = rs.getString("UNIQ");
                String plugID = rs.getString("PLUGID");

                docList.add(new Document(docID, path, uniq, plugID));
            }
            rs.close();

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docList;

    }

}
