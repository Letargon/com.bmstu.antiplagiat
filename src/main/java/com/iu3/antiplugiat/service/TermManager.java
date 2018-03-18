/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service;

import com.iu3.antiplugiat.model.TermInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andalon
 */
public class TermManager {

    private Connection connect;

    public TermManager() {

        try {
            createConnection();
        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        initDataBase();
    }

    public void closeConnection() {

        try {
            if (connect != null) {
                connect.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        String url = "jdbc:postgresql://localhost:5432/termdict";
        String login = "Andalon";
        String password = "qwerty";
        connect = DriverManager.getConnection(url, login, password);
    }

    final void initDataBase() {
        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict
            if (!connect.isValid(0)) {
                createConnection();
            }

            Statement stmt = connect.createStatement();
            //максимальная длина пути обработка исключения?
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS DOCS"
                    + "("
                    + "        DOC_ID SERIAL,"
                    + "        PATH VARCHAR(255) NOT NULL UNIQUE,"
                    + "        PRIMARY KEY (DOC_ID)"
                    + ");");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TERMS"
                    + "("
                    + "        ID SERIAL,"
                    + "        NAME VARCHAR(50)  NOT NULL ,"
                    + "        DOC_ID INT  NOT NULL ,"
                    + "        POSITION INT  NOT NULL ,"//check for int type in sql
                    + "        PRIMARY KEY (ID),"
                    + "        FOREIGN KEY (DOC_ID) REFERENCES DOCS(DOC_ID)"
                    + ");");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void addTerm(String name, TermInfo term) {
        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict
            if (!connect.isValid(0)) {
                createConnection();
            }

            Statement stmt = connect.createStatement();
            stmt.executeUpdate("INSERT INTO TERMS (NAME, DOC_ID, POSITION) VALUES"//check sql syntex for insert
                    + "("
                    + "'" + name + "',"
                    + Integer.toString(term.getDocID()) + ","
                    + Integer.toString(term.getPos())
                    + ");");//нужно органичить, чтобы связка DocId - Pos была уникальной

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void removeTerm(String name, TermInfo term) {

        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict
            if (!connect.isValid(0)) {
                createConnection();
            }

            Statement stmt = connect.createStatement();
            stmt.executeUpdate("DELETE FROM TERMS WHERE "
                    + "NAME='" + name + "' AND"
                    + "DOC_ID=" + Integer.toString(term.getDocID()) + " AND"
                    + "POSITION=" + Integer.toString(term.getPos()) + " ;");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ArrayList<TermInfo> getTermInfo(String term) {
        ArrayList<TermInfo> info = new ArrayList<>();
        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict
            if (!connect.isValid(0)) {
                createConnection();
            }
            //добавить условие для docid
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TERMS WHERE NAME='" + term
                    + "' ORDER BY DOC_ID, POSITION;");
            while (rs.next()) {
                TermInfo termInfo = new TermInfo(rs.getInt("DOC_ID"), rs.getInt("POSITION"));
                info.add(termInfo);
            }
            rs.close();

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }

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
                    + ");");//нужно подумать об ошибке уникальности

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docId;
    }
    public String getDocDir(String docID) {
        String docDir=new String();
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
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docDir;
    }

}
