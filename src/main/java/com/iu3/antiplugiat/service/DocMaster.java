/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service;

import com.iu3.antiplugiat.model.TermInfo;
import com.iu3.antiplugiat.service.analizers.DocAnalizer;
import com.iu3.antiplugiat.service.database.local.ConnectionPool;
import com.iu3.antiplugiat.service.database.local.DocManager;
import com.iu3.antiplugiat.service.database.local.TermManager;
import com.iu3.antiplugiat.service.extractors.MSWordExtractor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andalon
 */
public class DocMaster {

    private String docDir;
    private Integer docID;

    private DocAnalizer analizer;

    private List<String> text;
    
    final private Connection connect;

    public DocMaster(String docDir) {
        this.connect = ConnectionPool.createDefaultConnection();
        this.docDir = docDir;
        
        DocManager docManager = new DocManager(connect);
        
        docID = docManager.getDocId(docDir);
        text = new ArrayList<>();
       
        String[] splited = docDir.split("\\.");
        String format = splited[splited.length - 1];

        if (format.equals("doc") || format.equals("docx")) {
            MSWordExtractor extractor = new MSWordExtractor(docDir);
            text = extractor.getTokens();
        }
 
        analizer = new DocAnalizer(docID, text);
    }
    public void analizeDoc() {
        DocManager docManager = new DocManager(connect);
        docID = docManager.getDocId(docDir);
        
        analizer.setdocID(docID);
        analizer.checkUniqueness();
    }

    public Double getUniqAttr() {
        return analizer.getuniqueAttr();
    }

    public String getPlugiatDir() {
        return analizer.getPlugiatDir();
    }

    public Integer getPlugiatID() {
        return analizer.getPlugID();
    }

    public void loadToDatabase() {

        long start = System.nanoTime();
        TermManager termManager = new TermManager(connect, docID);
        DocManager docManager = new DocManager(termManager.getConnect());
        boolean success = docManager.addDoc(docDir, Integer.toString(text.size()));

        if (getUniqAttr() != 1. && getPlugiatID() != -1) {
            docManager.writePlugInfo(docID.toString(), getUniqAttr().toString(), getPlugiatID().toString());
        }

        if (success) {
            docID = docManager.getDocId(docDir);
            for (int i = 0; i < text.size(); i++) {
                termManager.addTerm(text.get(i), new TermInfo(docID, i));
            }
        }
        termManager.setDocID(docID.toString());
        analizer.setdocID(docID);
        System.out.println("Time of load:" + (System.nanoTime() - start));

    }

}
