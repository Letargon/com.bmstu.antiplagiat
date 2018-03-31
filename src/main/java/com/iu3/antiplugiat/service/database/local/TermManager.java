/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service.database.local;

import com.iu3.antiplugiat.model.TermInfo;
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
public class TermManager extends LocalManager {

    public void addTerm(String name, TermInfo term) {
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
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.TermManager.class.getName()).log(Level.SEVERE, null, ex);
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
                    + "NAME='" + name + "' AND "
                    + "DOC_ID=" + Integer.toString(term.getDocID()) + " AND "
                    + "POSITION=" + Integer.toString(term.getPos()) + " ;");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.TermManager.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
}
