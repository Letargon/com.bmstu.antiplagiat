/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service.analizers;

import com.iu3.antiplugiat.model.TermInfo;
import com.iu3.antiplugiat.service.database.local.TermManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 *
 * @author Andalon
 */
class QueryHandler {

    private List<String> query;//check for another collections
    private TermManager termManager;
    private TreeSet<TermInfo> interTerms;

    //for debug purposes
    public QueryHandler() {
        termManager = new TermManager();
        interTerms = new TreeSet<>();
    }

    public QueryHandler(List<String> query) {
        this();
        this.query = query;
        if (!query.isEmpty()) {
            findIntercections();
        }

    }

    public List<String> getQuery() {
        return query;
    }

    public void startSession() {
        termManager.openConnection();
    }
    public void stopSession(){
        termManager.closeConnection();
    }

    public void setQuery(List<String> query) {
        this.query = query;
        if (query.size() > 1) {
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

    public Set<Integer> getDocIDs() {

        Set<Integer> docIds = new HashSet<>();
        for (TermInfo t : interTerms) {
            docIds.add(t.getDocID());
        }
        return docIds;
    }

    public void flush() {
        interTerms.clear();
    }

    //заметка - подумать о реализации.
    //Предыдущий: с двойным проходом или Сейчас:конвертация TreeSet в ArrayList на каждой итерации;
    //Описать алгоритм востановления по последней выборке queueInterc. Запомнить все выборки до, как пары значений. 
    //По последней выборке востанавливать эти пары, пока таковые находятся в памяти, и записывать в список перекрытий
    //функция перекрытия возвращает все значения из диапазона near - проверить валидность
    private void findIntercections() {

        ArrayList<TermInfo> curT;
        final TreeMap<TermInfo, Boolean> queueInterc = new TreeMap<>();

        long s = System.nanoTime();

        for (String cur : query.subList(0, query.size() - 1)) {

            curT = termManager.getTermInfo(cur);

            if (curT.isEmpty()) {
                queueInterc.clear();
                break;
            }
            if (queueInterc.isEmpty()) {
                curT.forEach(t -> queueInterc.put(t, false));
            } else {
                //если на какой то итерации перекрытия не было, значит предложение не встречается в целом виде
                TreeMap<TermInfo, Boolean> inter = intercept(new ArrayList(queueInterc.keySet()), curT, false);
                if (!inter.isEmpty()) {
                    queueInterc.putAll(inter);
                } else {
                    queueInterc.clear();
                    break;
                }
            }

        }

        curT = termManager.getTermInfo(query.get(query.size() - 1));
        TreeMap<TermInfo, Boolean> inter = intercept(new ArrayList(queueInterc.keySet()), curT, true);

        //проблема запросов с одинаковыми словами: интерсепт заполнится вновь, но может дать те же значения,
        //т.е. размер не изменится
        if (!curT.isEmpty() && !inter.isEmpty()) {
            //check if inter is full sublist of queueInterc
            queueInterc.putAll(inter);
            if (queueInterc.size() == inter.size()) {
                queueInterc.clear();
            }

        } else {
            queueInterc.clear();
        }

        //необходимо задать максимальное количество включений предложения в документе,
        //по логике программы до одного на документ. 
        //проблема: необходимо добавить новый набор, если в списке уже присутствует старый
        addLimitedList(queueInterc, query.size(), interTerms);

    }

    private void addLimitedList(TreeMap<TermInfo, Boolean> inter, int limiter, TreeSet<TermInfo> res) {

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
                    if (!res.add(cur.getKey())) {
                        num--;
                    }
                }
            } else {
                num--;
            }
            prevDocId = cur.getKey().getDocID();
        }
    }

    private int iterateAndProccess(int pos, ArrayList<TermInfo> mass, TermInfo oth, BiPredicate<TermInfo, TermInfo> cond,
            boolean prev, boolean add, TreeMap<TermInfo, Boolean> res) {

        if (boundCheck(mass.size()).test(pos)) {
            TermInfo nxt = mass.get(pos + 1);
            while (cond.test(nxt, oth)) {
                if (add) {
                    res.put(nxt, prev);
                    res.put(oth, true);
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
