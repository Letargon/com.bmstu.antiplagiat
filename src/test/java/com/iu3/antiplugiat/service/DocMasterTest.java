/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service;

import org.junit.Test;

/**
 *
 * @author Andalon
 */
public class DocMasterTest {

    public DocMasterTest() {
    }

    /**
     * Test of analizeDoc method, of class DocMaster.
     */
    @Test
    public void testAnalizeDoc() {
        DocMaster inst = new DocMaster("C:\\БД. Волков Н.А..docx");
        inst.analizeDoc();
        System.out.println("answer: "+inst.getUniqAttr());
    }

    /**
     * Test of getUniqAttr method, of class DocMaster.
     */
    @Test
    public void testGetUniqAttr() {
    }

    /**
     * Test of getPlugiatDir method, of class DocMaster.
     */
    @Test
    public void testGetPlugiatDir() {
    }

    /**
     * Test of loadToDatabase method, of class DocMaster.
     */
    @Test
    public void testLoadToDatabase() {
    }

}
