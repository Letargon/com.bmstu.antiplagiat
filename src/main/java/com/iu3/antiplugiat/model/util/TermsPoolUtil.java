package com.iu3.antiplugiat.model.util;

import com.iu3.antiplugiat.model.TermInfo;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Andalon
 */
public class TermsPoolUtil {

    private final static int MAX_POOL_SIZE = 1000;

    private ConcurrentHashMap<String, ArrayList<TermInfo>> pool;

    private ConcurrentHashMap<String, Integer> poolFreq;

    public TermsPoolUtil() {
        pool = new ConcurrentHashMap<>();
        poolFreq = new ConcurrentHashMap<>();

    }
    public synchronized ArrayList<TermInfo> get(String term){
        return pool.get(term);
    }
    public synchronized int getSize(){
        return pool.size();
    }
    public synchronized void put(String term, ArrayList<TermInfo> info) {
        if (pool.size() < MAX_POOL_SIZE) {
            pool.put(term, info);
        } else {
            removeFromPool();
            pool.put(term, info);
        }
        poolFreq.put(term, 1);
    }

    private synchronized void removeFromPool() {

        Map.Entry<String, Integer> min = null;
        for (Map.Entry<String, Integer> entry : poolFreq.entrySet()) {
            if (min == null || min.getValue() > entry.getValue()) {
                min = entry;
            }
        }
        if (min != null) {
            poolFreq.remove(min.getKey());
            pool.remove(min.getKey());
        }
    }

    public synchronized void incFreq(String term) {
        int freq = poolFreq.get(term);
        poolFreq.put(term, freq + 1);
    }
    public synchronized void clear(){
        pool.clear();
        poolFreq.clear();
    }
}
