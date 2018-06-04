package com.iu3.antiplugiat.service.database.local;

import com.iu3.antiplugiat.model.TermInfo;
import java.sql.Connection;
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

    private String doc_id;

    public TermManager(Connection connect,int doc_id) {
        super(connect);
        this.doc_id = Integer.toString(doc_id);
    }

    public void setDocID(String doc_id) {
        this.doc_id = doc_id;
    }
   
    public boolean addTerm(String name, TermInfo term) {
        try {

            Statement stmt = connect.createStatement();
            stmt.executeUpdate("INSERT INTO TERMS (NAME, DOC_ID, POSITION) VALUES"
                    + "("
                    + "'" + name + "',"
                    + Integer.toString(term.getDocID()) + ","
                    + Integer.toString(term.getPos())
                    + ");");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.TermManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    void removeTerm(String name, TermInfo term) {

        try {

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
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TERMS WHERE NAME='" + term
                    + "' AND NOT DOC_ID = " + doc_id
                    + " ORDER BY DOC_ID, POSITION;");
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
