package com.example.healthymind.util;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface ValueConstants {

    @IntDef({NavigationItemType.Header, NavigationItemType.Item})
    @Retention(RetentionPolicy.SOURCE)
    @interface NavigationItemType {
        int Header = 0, Item = 1;
    }
}
