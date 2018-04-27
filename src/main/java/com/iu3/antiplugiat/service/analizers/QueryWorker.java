/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.antiplugiat.service.analizers;

import com.iu3.antiplugiat.model.TermInfo;
import com.iu3.antiplugiat.service.database.local.TermManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 *
 * @author Andalon
 */
public class QueryWorker implements Runnable {

    final private String name;

    final private List<String> query;//check for another collections

    final private QueryStorage qStor;

    final private TermManager termManager;

    final private TreeMap<TermInfo, Boolean> queueInterc;

    final private DocAnalizer parent;

    volatile boolean running;

    QueryWorker(List<String> query, QueryStorage qStor, DocAnalizer parent, TermManager termManager, String name) {
        this.query = query;
        this.qStor = qStor;
        this.termManager = termManager;
        this.running = false;
        this.queueInterc = new TreeMap<>();
        this.name = name;
        this.parent = parent;

    }

    public String getName() {
        return name;
    }

    public TermManager getTermManager() {
        return termManager;
    }
    

    void notifyEnd() {
        running = false;
        synchronized (parent) {
            parent.notifyAll();
        }
    }
    static int count=0;
    @Override
    public void run() {
       // System.out.println(Thread.currentThread().getName() +" started");
       
        running = true;
        queueInterc.clear();

        if (query.size() > 1) {
            findIntercections();
        }
        SingletonAnalizePool.getInstance().putback(termManager.getConnect());
        //необходимо задать максимальное количество включений предложения в документе,
        //по логике программы до одного на документ. 
        //проблема: необходимо добавить новый набор, если в списке уже присутствует старый
        qStor.addLimitedList(queueInterc, query.size());
        //System.out.println(name+" finished");
        notifyEnd();
        
    }

    //заметка - подумать о реализации.
    //Предыдущий: с двойным проходом или Сейчас:конвертация TreeSet в ArrayList на каждой итерации;
    //Описать алгоритм востановления по последней выборке queueInterc. Запомнить все выборки до, как пары значений. 
    //По последней выборке востанавливать эти пары, пока таковые находятся в памяти, и записывать в список перекрытий
    //функция перекрытия возвращает все значения из диапазона near - проверить валидность
    private void findIntercections() {

        ArrayList<TermInfo> curT;

        List<String> subList = query.subList(0, query.size() - 1);
        for (String cur : subList) {

            
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

    }
    //внутри документа можно было найти несколько включений одного запроса отстоящих на разном расстоянии
    //надо ограничить количество таких вклюений до 1 набора. При этом если набор уже есть, то добавляется другой набор.
    
    //для многопоточности надо залочить для единственного выполнения во всей среде
    //private void addLimitedList(TreeMap<TermInfo, Boolean> inter, int limiter)
    
    static int cnt = 0;

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
