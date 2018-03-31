/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service.analizers;

import com.iu3.antiplugiat.service.database.local.DocManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Andalon
 */
public class DocAnalizer {

    DocManager docManager;

    QueryHandler qMaster;
    List<String> tokenizedText;

    Integer plugID;
    Double uniqueAttr;

    public DocAnalizer(List<String> tokenizedText) {
        this.tokenizedText = tokenizedText;
        
        plugID = -1;
        uniqueAttr = 1.;
        docManager = new DocManager();
        qMaster = new QueryHandler();
    }

    public void checkUniqueness(){
        long start = System.nanoTime();
        analizeDocument(5);
        System.out.println("Time of analizing" + (System.nanoTime() - start));
    }
    public double getuniqueAttr() {
        return uniqueAttr;
    }

    public String getPlugiatDir(){
        return docManager.getDocDir(plugID.toString());
    }
    private void analizeDocument(int querySize) {
        HashMap<Integer, Integer> interNums = new HashMap<>();
        List<String> query = new ArrayList<>();

        //begin sql session
        qMaster.startSession();
        for (int i = 0; i < tokenizedText.size(); i++) {
            query.add(tokenizedText.get(i));
            if (query.size() == querySize) {
                qMaster.setQuery(query);
                query.clear();
            } else {
                if (i == tokenizedText.size() - 1) {
                    qMaster.setQuery(query);
                }
            }
        }
        qMaster.stopSession();

        Set<Integer> docIds = qMaster.getDocIDs();
        int max = 0;
        for (Integer id : docIds) {
            int cur = qMaster.getIntersectNum(id);
            if (cur > max) {
                max = cur;
                plugID = id;
            }
        }
        uniqueAttr = 1 - (double) max / tokenizedText.size();
        qMaster.flush();
    }
}
