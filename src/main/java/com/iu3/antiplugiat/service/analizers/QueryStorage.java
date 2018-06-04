package com.iu3.antiplugiat.service.analizers;

import com.iu3.antiplugiat.model.TermInfo;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Andalon
 */
public class QueryStorage {

    private final SortedSet<TermInfo> storage;

    public QueryStorage(TreeSet<TermInfo> st) {
        storage = Collections.synchronizedSortedSet(st);
    }

    synchronized void addAll(TreeSet<TermInfo> ti) {
        storage.addAll(ti);
    }

    synchronized boolean add(TermInfo t) {
        return storage.add(t);
    }

    synchronized void addLimitedList(TreeMap<TermInfo, Boolean> inter, int limiter) {

        int num = 1;
        int prevDocId = -1;
        for (Map.Entry<TermInfo, Boolean> cur : inter.entrySet()) {
            if (cur.getKey().getDocID() == prevDocId) {
                num++;
            } else {
                num = 1;
            }
            if (cur.getValue()) {

                if (num <= limiter) {
                    if (!this.add(cur.getKey())) {
                        num--;
                    } 
                }
            } else {
                num--;
            }
            prevDocId = cur.getKey().getDocID();
        }
        
    }
    static int count = 0;

    synchronized public int getIntersectNum(int docID) {
        int i = 0;
        for (TermInfo t : storage) {
            if (t.getDocID() == docID) {
                i++;
            }
        }
        return i;
    }

    synchronized public Set<Integer> getDocIDs() {

        Set<Integer> docIds = new HashSet<>();
        for (TermInfo t : storage) {
            docIds.add(t.getDocID());
        }
        return docIds;
    }

    synchronized public void clear() {
        storage.clear();
    }

}
