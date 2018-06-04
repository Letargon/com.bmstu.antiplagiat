package com.iu3.antiplugiat.service.extractors;

import com.iu3.antiplugiat.service.database.local.DocManager;
import com.iu3.antiplugiat.service.txtproccessing.Tokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.ICell;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFSDTCell;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 *
 * @author Andalon
 */
public class MSWordExtractor {

    private Tokenizer tokenizer;
    private String docDir;
    private String format;

    public MSWordExtractor(String docDir) {
        String[] splited = docDir.split("\\.");
        String format = splited[splited.length - 1];
        try {
            if (!(format.equals("docx") || format.equals("doc"))) {
                throw new Exception("Illegal format");
            }
            this.docDir = docDir;
            this.format = format;
            tokenizer = new Tokenizer();

        } catch (Exception ex) {
            System.err.println("Error: Illegal format");
        }
    }

    public List<String> getTokens() {

        List<String> tokens = new ArrayList<>();
        if (format.equals("doc")) {
            tokens = getTokensFromDoc();
        }
        if (format.equals("docx")) {
            tokens = getTokensFromDocx();
        }
        return tokens;
    }

    public String getDocDir() {
        return docDir;
    }

    public String getFormat() {
        return format;
    }

    private List<String> getTokensFromDoc() {
        List<String> text = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(new File(docDir));
            HWPFDocument doc = new HWPFDocument(fis);

            WordExtractor extractor = new WordExtractor(doc);
            text = Arrays.asList(extractor.getParagraphText());
            tokenizer.addText(text);

        } catch (IOException ex) {
            Logger.getLogger(DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tokenizer.getTokens();
    }

    private List<String> getTokensFromDocx() {

        try {
            FileInputStream fis = new FileInputStream(new File(docDir));
            XWPFDocument doc = new XWPFDocument(fis);
            //add raw text
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            for (XWPFParagraph par : paragraphs) {
                tokenizer.addText(par.getText());
            }

            //add text from tables
            List<XWPFTable> tables = doc.getTables();
            for (XWPFTable table : tables) {
                List<String> rawText = new ArrayList<>();
                addTableText(table, rawText);
                tokenizer.addText(rawText);
            }

        } catch (IOException ex) {
            Logger.getLogger(DocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tokenizer.getTokens();
    }

    private void addTableText(XWPFTable table, List<String> text) {
        //this works recursively to pull embedded tables from tables
        for (XWPFTableRow row : table.getRows()) {
            List<ICell> cells = row.getTableICells();
            for (int i = 0; i < cells.size(); i++) {
                ICell cell = cells.get(i);
                if (cell instanceof XWPFTableCell) {
                    text.add(((XWPFTableCell) cell).getTextRecursively());
                } else if (cell instanceof XWPFSDTCell) {
                    text.add(((XWPFSDTCell) cell).getContent().getText());
                }
                if (i < cells.size() - 1) {
                    text.add(" ");
                }
            }
            text.add(" ");
        }
    }
}
