package com.sample.applocker.util;

import com.sample.applocker.database.model.AppModel;

import java.util.Comparator;

public class PhonemeComparator implements Comparator<AppModel>{


    @Override
    public int compare(AppModel former,AppModel latter) {
        if (latter.getSort().equals("#")) {
            return -1;
        } else if (former.getSort().equals("#")) {
            return 1;
        } else {
            return former.getSort().compareTo(latter.getSort());
        }
    }
}
