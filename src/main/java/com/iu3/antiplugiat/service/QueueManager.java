/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service;

import com.iu3.antiplugiat.model.TermInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 *
 * @author Andalon
 */
public class QueueManager {

    private List<String> queue;//check for another collections
    private TermManager termManager;
    private TreeSet<TermInfo> interTerms;

    public QueueManager(List<String> queue) {
        termManager = new TermManager();
        this.queue = queue;
        interTerms = new TreeSet<>();
        if (!queue.isEmpty()) {
            findIntercections();

        }
    }

    public QueueManager(List<String> queue, TermManager termManager) {
        this.termManager = termManager;
        this.queue = queue;
        interTerms = new TreeSet<>();
        if (!queue.isEmpty()) {
            findIntercections();

        }
    }

    public QueueManager(TermManager termManager) {
        this.termManager = termManager;
        this.queue = new ArrayList<>();
        interTerms = new TreeSet<>();

    }

    public List<String> getQueue() {
        return queue;
    }

    public void setQueue(List<String> queue) {
        this.queue = queue;
        if (queue.size() > 1) {
            findIntercections();
        }
    }
//количество включений в документе

    public int getIntersectNum(int docID) {
        int i = 0;
        for (TermInfo t : interTerms) {
            if (t.getDocID() == docID) {
                i++;
            }
        }
        return i;
    }
//

    public Set<Integer> getDocIDs() {
        Set<Integer> docIds = new HashSet<>();
        for (TermInfo t : interTerms) {
            docIds.add(t.getDocID());
        }
        return docIds;
    }

    public void clear() {
        interTerms.clear();
    }

    private void findIntercections() {

        String first = queue.get(0);
        String second = queue.get(1);
        String last = queue.get(queue.size() - 1);

        ArrayList<TermInfo> firstT = termManager.getTermInfo(first);
        ArrayList<TermInfo> secondT = termManager.getTermInfo(second);
        ArrayList<TermInfo> lastT = termManager.getTermInfo(last);

        if (interTerms.size() ==18) {
            System.out.println("debug");
        }
        ArrayList<TermInfo> firstInterc = intercept(firstT, secondT, nearCond());

        String prev = second;
        ArrayList<TermInfo> prevT = secondT;
        ArrayList<TermInfo> curT;

        ArrayList<TermInfo> prevInterc = firstInterc;
        ArrayList<TermInfo> curInterc;

        if (queue.size() > 3) {
            for (String cur : queue.subList(2, queue.size())) {
                curT = termManager.getTermInfo(cur);
                curInterc = intercept(prevT, curT, nearCond());
                interTerms.addAll(intercept(curInterc, prevInterc, andCond()));

                prevT = curT;
                prevInterc = curInterc;
            }
            curInterc = intercept(firstT, lastT, nearCond());
            interTerms.addAll(intercept(curInterc, prevInterc, andCond()));
            interTerms.addAll(intercept(curInterc, firstInterc, andCond()));
        } else {
            interTerms.addAll(intercept(firstT, lastT, nearCond()));
        }
        termManager.closeConnection();
    }
//заметка: переделать все включения termInfo на TreeSet?
    //функция находит все одинаковые включения двух выборок по документу и позиции
    //эти включения определяют набор позиций среднего слова, удовлетворяющих обеим выборкам

    public BiPredicate<TermInfo, TermInfo> andCond() {
        return (t1, t2) -> t1.equals(t2);
    }

    public BiPredicate<TermInfo, TermInfo> nearCond() {
        return (t1, t2) -> t1.getDocID() == t2.getDocID() && withinRange(t1, t2, 3);
    }
//переделать TreeSet на ArrayList, прикинуть, что делать со скипами если cond=near. 

    private ArrayList<TermInfo> intercept(ArrayList<TermInfo> mass1, ArrayList<TermInfo> mass2,
            BiPredicate<TermInfo, TermInfo> cond) {

        TreeSet<TermInfo> interc = new TreeSet<>();

        int i = 0;
        int j = 0;

        while (mass1.size() > i && mass2.size() > j) {

            TermInfo t1 = mass1.get(i);
            TermInfo t2 = mass2.get(j);
            if (cond.test(t1, t2)) {

                interc.add(t1);
                interc.add(t2);

                if ((i + 1) < mass1.size()) {
                    TermInfo nxt1 = mass1.get(i + 1);
                    while (cond.test(nxt1, t2)) {
                        interc.add(nxt1);
                        i++;
                        nxt1 = mass1.get(i + 1);
                        
                    }
                }
                if ((j + 1) < mass2.size()) {
                    TermInfo nxt2 = mass2.get(j + 1);
                    while (cond.test(nxt2, t1)) {
                        interc.add(nxt2);
                        j++;
                        nxt2 = mass2.get(j + 1);
                       
                    }
                }
                i++;
                j++;
            } else {
//
                if (t2.compareTo(t1) < 0) {
                    Predicate<Integer> rangeCheck=t->(t+1)<mass2.size();
                    if (rangeCheck.test(j)) {
                        TermInfo next = mass2.get(j + 1);
                        while (next.compareTo(t1) < 0 && !cond.test(next, t1)) {
                            j++;
                            if (rangeCheck.test(j))
                                next = mass2.get(j + 1);
                            else
                                break;
                        }
                    }
                    j++;

                } else {
                    Predicate<Integer> rangeCheck=t->(t+1)<mass1.size();
                    if (rangeCheck.test(i)) {
                        TermInfo next = mass1.get(i + 1);
                        while (next.compareTo(t2) < 0 && !cond.test(next, t2)) {
                            i++;
                            if (rangeCheck.test(i))
                                next = mass1.get(i + 1);
                            else
                                break;
                        }
                    }
                    i++;
                }
            }
        }

        return new ArrayList<>(interc);
    }

    private boolean withinRange(TermInfo term1, TermInfo term2, int x) {

        return (Math.abs(term1.getPos() - term2.getPos()) < x);

    }

    public HashMap<TermInfo, Integer> getMinQDistance() {
        //TODO
        return null;
    }

}