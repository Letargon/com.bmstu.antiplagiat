package com.iu3.antiplugiat.service.database.local;

/**
 *
 * @author Andalon
 */
import com.iu3.antiplugiat.constants.DataBaseConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionPool {

    private List<Connection> availableConns = Collections.synchronizedList(new ArrayList<>());
    private List<Connection> usedConns = Collections.synchronizedList(new ArrayList<>());
    final private String url;
    
    public ConnectionPool(String url, String driver, int initConnCnt) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.url = url;
        for (int i = 0; i < initConnCnt; i++) {
            availableConns.add(getConnection());
        }
    }

    public static Connection createDefaultConnection() {
        try {
            Class.forName(DataBaseConstants.DRIVER_NAME);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TermManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection conn  = null;
        try {
            String url = DataBaseConstants.URL;
            conn = DriverManager.getConnection(url, DataBaseConstants.LOGIN, DataBaseConstants.PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;

    }

    public static void closeConnection(Connection connect) {
        try {
            connect.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Connection getConnection() {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, DataBaseConstants.LOGIN, DataBaseConstants.PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
        }

        return conn;
    }

    public synchronized Connection retrieve() {
        Connection newConn = null;
        if (availableConns.isEmpty()) {
            newConn = getConnection();
        } else {
            newConn = (Connection) availableConns.get(availableConns.size()-1);
            availableConns.remove(newConn);
        }
        usedConns.add(newConn);
        return newConn;
    }

    public synchronized void putback(Connection c) {
        if (c != null) {
            if (usedConns.remove(c)) {
                availableConns.add(c);
            } else {
                throw new NullPointerException("Connection not in the usedConns array");
            }
        }
    }

    public int getAvailableConnsCnt() {
        return availableConns.size();
    }
}
