package com.iu3.antiplugiat.model;

import com.iu3.antiplugiat.model.util.TermsPoolUtil;
import com.iu3.antiplugiat.service.database.local.TermManager;
import java.util.ArrayList;

/**
 *
 * @author Andalon
 */
public class TermsPool {

    private static TermsPoolUtil TERMS_POOL = new TermsPoolUtil();
    private final TermManager tm;

    public TermsPool(TermManager tm) {
        this.tm = tm;
    }

    public ArrayList<TermInfo> getTermInfo(String term) {
        ArrayList<TermInfo> result = TERMS_POOL.get(term);

        if (result == null) {
            result = tm.getTermInfo(term);
            TERMS_POOL.put(term, result);

        } else {
            TERMS_POOL.incFreq(term);
        }
        return result;
    }

    public void clear() {
        TERMS_POOL.clear();
    }
}
