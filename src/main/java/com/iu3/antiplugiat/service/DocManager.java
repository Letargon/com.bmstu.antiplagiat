/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service;

import com.iu3.antiplugiat.model.TermInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 *
 * @author Andalon
 */
public class DocManager {

    String docDir;
    int docId;
    double uniqueAttr;
    List<String> termsQueue;

    TermManager termManager;
    QueueManager queueManager;

    public DocManager(String docDir) {

        long start = System.nanoTime();

        termManager = new TermManager();
        queueManager = new QueueManager(termManager);
        termsQueue = new ArrayList<>();

        this.docDir = docDir;
        termManager.addDoc(docDir);
        docId = termManager.getDocId(docDir);
        termManager.closeConnection();

        getTerms();
        System.out.println("Time of text processing:" + (System.nanoTime() - start));
    }

    private void getTerms() {

        String[] splited = docDir.split("\\.");
        String fileFormat = splited[splited.length - 1];
        if (fileFormat.equals("docx")) {
            termsQueue = getTockensFromDocx();
        }
        if (fileFormat.equals("doc")) {
            termsQueue = getTockensFromDoc();
        }
        termsQueue.forEach(t -> System.out.println(t));

    }

    private String normalizeText(String text) {

        StringBuilder nText = new StringBuilder();
        char prev = ' ';

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDefined(c) && !Character.isISOControl(c)) {
                switch (c) {
                    case '.':
                    case ',':
                    case '!':
                    case '?':
                    case ':':
                    case ';':
                    case '\r':
                    case '\n':
                    case '(':
                    case ')':
                    case '»':
                    case '«':
                    case '\"':
                    case '\'':
                    case '_':
                    case '\t':
                        if (prev != ' ') {
                            prev = ' ';
                            nText.append(" ");
                        }

                        break;
                    default:
                        if (prev != ' ' || c != ' ') {
                            nText.append(c);
                            prev = c;
                        }
                }
            }
        }
        while (nText.length() != 0 && nText.charAt(nText.length() - 1) == ' ') {
            nText.deleteCharAt(nText.length() - 1);
        }
        return nText.toString();
    }

    private List<String> getTockensFromDoc() {
        List<String> text = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(new File(docDir));
            HWPFDocument doc = new HWPFDocument(fis);
            WordExtractor extractor = new WordExtractor(doc);
            String[] rawText = extractor.getParagraphText();

            for (String rawTextPtn : rawText) {
                String[] tokenPattern = normalizeText(rawTextPtn).split(" ");

                for (String token : tokenPattern) {
                    if (!token.equals("")) {
                        text.add(Porter.stem(token));
                    }
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return text;
    }

    private List<String> getTockensFromDocx() {

        List<String> text = new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream(new File(docDir));
            XWPFDocument doc = new XWPFDocument(fis);

            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            for (XWPFParagraph par : paragraphs) {
                String[] tokenPattern = normalizeText(par.getText()).split(" ");

                for (String token : tokenPattern) {
                    if (!token.equals("")) {
                        text.add(Porter.stem(token));
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return text;
    }

    public void loadToDatabase() {

        long start = System.nanoTime();
        for (int i = 0; i < termsQueue.size(); i++) {
            termManager.addTerm(termsQueue.get(i), new TermInfo(docId, i));
        }
        termManager.closeConnection();

        System.out.println("Time of load:" + (System.nanoTime() - start));

    }

    private void analizeDocument() {
        HashMap<Integer, Integer> interNums = new HashMap<>();
        for (int i = 0; i < termsQueue.size(); i += 4) {
            if ((termsQueue.size() - i - 4) > 0) {
                queueManager.setQueue(termsQueue.subList(i, i + 4));
            } else {
                queueManager.setQueue(termsQueue.subList(i, termsQueue.size()));
            }

        }
        Set<Integer> docIds = queueManager.getDocIDs();
        int max = 0;
        for (Integer id : docIds) {
            int cur = queueManager.getIntersectNum(id);
            if (cur > max) {
                max = cur;
            }
            uniqueAttr = (double) max / termsQueue.size();

        }
        queueManager.clear();

    }

    public double getuniqueAttr() {
        analizeDocument();
        return uniqueAttr;
    }

}
