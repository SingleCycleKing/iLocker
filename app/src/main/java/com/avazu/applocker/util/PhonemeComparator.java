package com.avazu.applocker.util;

import com.avazu.applocker.database.model.SortModel;
import java.util.Comparator;

public class PhonemeComparator implements Comparator<SortModel>{


    @Override
    public int compare(SortModel former,SortModel latter) {
        if (latter.getSort().equals("#")) {
            return -1;
        } else if (former.getSort().equals("#")) {
            return 1;
        } else {
            return former.getSort().compareTo(latter.getSort());
        }
    }
}
