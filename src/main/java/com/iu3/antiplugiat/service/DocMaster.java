/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service;

import com.iu3.antiplugiat.model.TermInfo;
import com.iu3.antiplugiat.service.analizers.DocAnalizer;
import com.iu3.antiplugiat.service.database.local.DocManager;
import com.iu3.antiplugiat.service.database.local.TermManager;
import com.iu3.antiplugiat.service.extractors.MSWordExtractor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andalon
 */
public class DocMaster {

    String docDir;

    DocAnalizer analizer;
    DocManager docManager;
    TermManager termManager;

    List<String> text;

    public DocMaster(String docDir) {
        this.docDir = docDir;
        text = new ArrayList<>();

        String[] splited = docDir.split("\\.");
        String format = splited[splited.length - 1];

        if (format.equals("doc") || format.equals("docx")) {
            MSWordExtractor extractor = new MSWordExtractor(docDir);
            text = extractor.getTokens();
        }
        docManager = new DocManager();
        termManager = new TermManager();
        analizer = new DocAnalizer(text);
    }

    public void analizeDoc() {
        analizer.checkUniqueness();
    }

    public Double getUniqAttr() {
        return analizer.getuniqueAttr();
    }

    public String getPlugiatDir() {
        return analizer.getPlugiatDir();
    }

    public void loadToDatabase() {

        long start = System.nanoTime();

        int docId = -1;
        
        docManager.addDoc(docDir);
        docId = docManager.getDocId(docDir);

        for (int i = 0; i < text.size(); i++) {
            termManager.addTerm(text.get(i), new TermInfo(docId, i));
        }
        
        System.out.println("Time of load:" + (System.nanoTime() - start));

    }

}
