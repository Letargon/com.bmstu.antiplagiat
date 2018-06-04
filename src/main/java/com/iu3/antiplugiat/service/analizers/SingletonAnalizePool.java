package com.iu3.antiplugiat.service.analizers;

import com.iu3.antiplugiat.constants.DataBaseConstants;
import com.iu3.antiplugiat.service.database.local.ConnectionPool;

/**
 *
 * @author Andalon
 */
public class SingletonAnalizePool {
    private static ConnectionPool conPool = null;
    public static int POOL_SIZE =  8;

    private SingletonAnalizePool() {}

    public static synchronized ConnectionPool getInstance() {
        if (conPool == null)
            conPool = new ConnectionPool(DataBaseConstants.URL,DataBaseConstants.DRIVER_NAME,POOL_SIZE);
        return conPool;
    }
}
