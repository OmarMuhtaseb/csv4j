package org.csv4j;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ToString is a util class to generate string
 * for collections
 *
 * @author Omar Muhtaseb
 */
class ToString {

    /**
     * Convert ar to string
     *
     * @param ar:        Array to be converted
     * @param separator: The items' separator
     * @return string: The string representation
     */
    static String arrayToString(Object[] ar, String separator, String nullValue) {
        if (ar == null) {
            return nullValue;
        }

        int iMax = ar.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(ar[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(separator);
        }
    }

    /**
     * Convert ar to string
     *
     * @param list:      List to be converted
     * @param separator: The items' separator
     * @return string: The string representation
     */
    static String collectionToString(Collection<Object> list, String separator, String nullValue) {
        if (list == null) {
            return nullValue;
        }

        if (list.size() == 0) {
            return "[]";
        }

        return list.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(separator, "[", "]"));
    }

    /**
     * Convert ar to string
     *
     * @param map:       Map to be converted
     * @param separator: The items' separator
     * @return string: The string representation
     */
    static String MapToString(Map<Object, Object> map, String separator, String nullValue) {
        if (map == null) {
            return nullValue;
        }

        if (map.size() == 0) {
            return "{}";
        }

        return map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(separator, "{", "}"));
    }

}
