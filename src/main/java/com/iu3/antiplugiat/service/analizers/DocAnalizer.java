/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service.analizers;

import com.iu3.antiplugiat.service.database.local.ConnectionPool;
import com.iu3.antiplugiat.service.database.local.DocManager;
import com.iu3.antiplugiat.service.database.local.TermManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andalon
 */
public class DocAnalizer {

    private static final ConnectionPool CON_POOL = SingletonAnalizePool.getInstance();

    Integer docID;
    QueryStorage qStor;

    List<String> tokenizedText;
    List<QueryWorker> workersList;

    Integer plugID;
    Double uniqueAttr;

    public DocAnalizer(Integer docID, List<String> tokenizedText) {
        this.tokenizedText = tokenizedText;
        this.docID = docID;
        this.qStor = new QueryStorage(new TreeSet<>());
        this.workersList = new ArrayList<>();
        this.plugID = -1;
        this.uniqueAttr = 1.;
    }

    public void setdocID(Integer docID) {
        this.docID = docID;
        TermManager tm = new TermManager(CON_POOL.retrieve(), -1);
        tm.setDocID(docID.toString());
        CON_POOL.putback(tm.getConnect());
    }

    public void checkUniqueness() {
        long start = System.nanoTime();
        analizeDocument(5);
        System.out.println("Time of analizing" + (System.nanoTime() - start));
    }

    public double getuniqueAttr() {
        return uniqueAttr;
    }

    public String getPlugiatDir() {

        DocManager dm = new DocManager(CON_POOL.retrieve());
        String dir = dm.getDocDir(plugID.toString());
        CON_POOL.putback(dm.getConnect());
        return dir;
    }

    public Integer getPlugID() {
        return plugID;
    }
    int i = 0;

    private void hireWorker(List<String> query) {
        TermManager tm = new TermManager(CON_POOL.retrieve(), docID);
        QueryWorker worker = new QueryWorker(new ArrayList<>(query), qStor, this, tm, "Thread" + i++);

        if (workersList.size() > SingletonAnalizePool.POOL_SIZE) {
            waitForWorkers();
        }

        workersList.add(worker);

        new Thread(worker).start();
        worker.running = true;

    }

    synchronized private void waitForWorkers() {
        workersList.forEach(w -> {
            while (w.running) {
                try {
                    wait();

                } catch (InterruptedException ex) {
                    Logger.getLogger(DocAnalizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        workersList.clear();
    }

    private void analizeDocument(int querySize) {

        List<String> query = new ArrayList<>();

        for (int i = 0; i < tokenizedText.size(); i++) {
            query.add(tokenizedText.get(i));
            if (query.size() == querySize) {

                hireWorker(query);
                query.clear();

            } else {
                if (i == tokenizedText.size() - 1) {
                    hireWorker(query);
                }
            }
        }
        //если в этот момент поток не успел запуститься тогда у нас проблема
        waitForWorkers();
        Set<Integer> docIds = qStor.getDocIDs();
        int max = 0;
        for (Integer id : docIds) {
            int cur = qStor.getIntersectNum(id);
            if (cur > max) {
                max = cur;
                plugID = id;
            }
        }
        uniqueAttr = 1 - (double) max / tokenizedText.size();

        qStor.clear();
    }
}
