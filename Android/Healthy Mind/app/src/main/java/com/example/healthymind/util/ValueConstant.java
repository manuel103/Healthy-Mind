package com.example.healthymind.util;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public interface ValueConstant {
    @IntDef({ItemType.Header, ItemType.Item})
    @Retention(RetentionPolicy.SOURCE)
    @interface ItemType {
        int Header = 0, Item = 1;
    }
}
