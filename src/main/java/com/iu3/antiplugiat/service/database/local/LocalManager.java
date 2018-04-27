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

    final protected Connection connect;

    public LocalManager(Connection connect) {
        this.connect = connect;
        initDataBase();
    }
    public Connection getConnect(){
        return connect;
    }

    protected final void initDataBase() {
        try {
            //createdb -D pg_default -E UTF8 -O Andalon --locale=Russian_Russia.1251 termdict

            Statement stmt = connect.createStatement();
            //максимальная длина пути обработка исключения?
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS DOCS"
                    + "("
                    + "        DOC_ID SERIAL,"
                    + "        PATH VARCHAR(255) NOT NULL UNIQUE,"
                    + "        TSIZE INT NOT NULL,"
                    + "        UNIQ FLOAT,"
                    + "        PLUGID INT,"
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
                    + "        FOREIGN KEY (DOC_ID) REFERENCES DOCS(DOC_ID) ON DELETE CASCADE"
                    + ");");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
