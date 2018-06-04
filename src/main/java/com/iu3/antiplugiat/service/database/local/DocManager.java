package com.iu3.antiplugiat.service.database.local;

import com.iu3.antiplugiat.model.Document;
import java.sql.Connection;
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

    public DocManager(Connection connect){
        super(connect);
    }
    public boolean addDoc(String docDir, String size) {
        try {

            Statement stmt = connect.createStatement();

            stmt.executeUpdate("INSERT INTO DOCS (PATH,TSIZE) VALUES"//check sql syntex for insert
                    + "("
                    + "'" + docDir + "' ,"
                    + size
                    + ");");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.DocManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public int getDocId(String docDir) {
        int docId = -1;
        try {

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

            Statement stmt = connect.createStatement();

            stmt.executeUpdate("UPDATE DOCS SET"
                    + " UNIQ ='" + uniq + "',"
                    + " PLUGID ='" + plugID + "'"
                    + " WHERE DOC_ID ='" + docID + "';");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Document> getDocumentList() {
        List<Document> docList = new ArrayList<>();

        String docDir = new String();
        try {
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM DOCS;");
            while (rs.next()) {
                String docID = rs.getString("DOC_ID");
                String path = rs.getString("PATH");
                String tsize = rs.getString("TSIZE");

                String uniq = rs.getString("UNIQ");
                String plugID = rs.getString("PLUGID");

                docList.add(new Document(docID, path, tsize, uniq, plugID));
            }
            rs.close();

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docList;
    }

    public void deleteDoc(String docID) {
        try {

            Statement stmt = connect.createStatement();

            stmt.executeUpdate("DELETE FROM DOCS"//check sql syntex for insert
                    + " WHERE DOC_ID =" + docID + ";");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(com.iu3.antiplugiat.service.database.local.DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
