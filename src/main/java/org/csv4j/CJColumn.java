package org.csv4j;

import java.util.LinkedHashSet;

/**
 * CJColumn the core class of the csv4j
 *
 * @author Omar Muhtaseb
 */
class CJColumn {
    private String fieldName;
    private String cjName;
    private Boolean isCJMap;
    private Boolean isCJStaticMap;
    private LinkedHashSet<Object> mapKeys;

    CJColumn(String fieldName, String cjName, Boolean isCJMap, Boolean isCJStaticMap) {
        this.fieldName = fieldName;
        this.cjName = cjName;
        this.isCJMap = isCJMap;
        this.isCJStaticMap = isCJStaticMap;
    }

    String getFieldName() {
        return fieldName;
    }

    String getCjName() {
        return cjName;
    }

    Boolean isCJMap() {
        return isCJMap;
    }

    Boolean isCJStaticMap() {
        return isCJStaticMap;
    }

    LinkedHashSet<Object> getMapKeys() {
        return mapKeys;
    }

    void setMapKeys(LinkedHashSet<Object> mapKeys) {
        this.mapKeys = mapKeys;
    }
}
