/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service;

import com.iu3.antiplugiat.model.TermInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
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

    //заметка - подумать о реализации.
    //Предыдущий: с двойным проходом или Сейчас:конвертация TreeSet в ArrayList на каждой итерации;
    //Описать алгоритм востановления по последней выборке queueInterc. Запомнить все выборки до, как пары значений. 
    //По последней выборке востанавливать эти пары, пока таковые находятся в памяти, и записывать в список перекрытий
    //функция перекрытия возвращает все значения из диапазона near - проверить валидность
    private void findIntercections() {

        ArrayList<TermInfo> curT;
        TreeMap<TermInfo, Boolean> queueInterc = new TreeMap<>();

        for (String cur : queue.subList(0, queue.size() - 1)) {
            curT = termManager.getTermInfo(cur);

            if (queueInterc.isEmpty()) {
                curT.forEach(t -> queueInterc.put(t, false));
            } else {
                queueInterc.putAll(intercept(new ArrayList(queueInterc.keySet()), curT, false));
            }
        }
        curT = termManager.getTermInfo(queue.get(queue.size() - 1));
        queueInterc.putAll(intercept(new ArrayList(queueInterc.keySet()), curT, true));

        queueInterc.forEach((k, v) -> {
            if (v) {
                interTerms.add(k);
            }
        });

        termManager.closeConnection();
    }

    private int iterateAndProccess(int pos, ArrayList<TermInfo> mass, TermInfo oth, BiPredicate<TermInfo, TermInfo> cond,
            boolean prev, boolean add, TreeMap<TermInfo, Boolean> res) {

        if (boundCheck(mass.size()).test(pos)) {
            TermInfo nxt = mass.get(pos + 1);
            while (cond.test(nxt, oth)) {
                if (add) {
                    res.put(nxt, prev);
                    res.put(oth,true);
                }
                pos++;
                if (boundCheck(mass.size()).test(pos)) {
                    nxt = mass.get(pos + 1);
                } else {
                    break;
                }
            }
        }
        return pos;
    }

    public BiPredicate<TermInfo, TermInfo> nearCond(int x) {
        return (t1, t2) -> t1.getDocID() == t2.getDocID() && withinRange(t1, t2, x);
    }

    public Predicate<Integer> boundCheck(int size) {
        return t -> t + 1 < size;
    }

//заметка: подумать как свести к минимуму необходимости конвертаций treeset в arraylist
    private TreeMap<TermInfo, Boolean> intercept(ArrayList<TermInfo> mass1, ArrayList<TermInfo> mass2, boolean last) {

        TreeMap<TermInfo, Boolean> interc = new TreeMap<>();

        int i = 0;
        int j = 0;

        while (mass1.size() > i && mass2.size() > j) {

            TermInfo t1 = mass1.get(i);
            TermInfo t2 = mass2.get(j);
            if (nearCond(3).test(t1, t2)) {

                interc.put(t1, true);

                interc.put(t2, last);

                i = iterateAndProccess(i, mass1, t2, nearCond(3), true, true, interc);

                j = iterateAndProccess(j, mass2, mass1.get(i), nearCond(3), last, true, interc);

                i++;
                j++;
            } else {
                BiPredicate<TermInfo, TermInfo> compare = (a, b) -> a.compareTo(b) < 0;
                if (compare.test(t2, t1)) {//t2<t1
                    j = iterateAndProccess(j, mass2, t1, nearCond(3).negate().and(compare), false, false, interc);
                    j++;

                } else {
                    i = iterateAndProccess(i, mass1, t2, nearCond(3).negate().and(compare), false, false, interc);
                    i++;
                }
            }
        }

        return interc;
    }

    private boolean withinRange(TermInfo term1, TermInfo term2, int x) {

        return (Math.abs(term1.getPos() - term2.getPos()) < x);

    }

}
