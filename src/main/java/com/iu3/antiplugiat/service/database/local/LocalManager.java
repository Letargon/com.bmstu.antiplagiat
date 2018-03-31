/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service.database.local;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andalon
 */
public abstract class LocalManager {

    protected Connection connect;

    public LocalManager() {
        try {
            createConnection();
        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        initDataBase();
    }

    public void openConnection() {
        try {
            createConnection();
        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    protected void createConnection() throws SQLException {
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

    protected final void initDataBase() {
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
                    + "        UNIQ FLOAT,"
                    + "        FOREIGN KEY (PLUGID) REFERENCES DOCS(DOC_ID),"
                    + "        PRIMARY KEY (DOC_ID)"
                    + ");");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TERMS"
                    + "("
                    + "        ID SERIAL,"
                    + "        NAME VARCHAR(100)  NOT NULL ,"
                    + "        DOC_ID INT  NOT NULL ,"
                    + "        POSITION INT  NOT NULL ,"
                    + "        PRIMARY KEY (ID),"
                    + "        FOREIGN KEY (DOC_ID) REFERENCES DOCS(DOC_ID)"
                    + ");");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
