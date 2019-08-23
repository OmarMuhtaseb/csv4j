package org.csv4j;

import org.csv4j.annotation.CJMap;
import org.csv4j.exception.CJException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * utils used in CSV4J
 *
 * @author Omar Muhtaseb
 */
class utils {

    /**
     * Append class fields to fields list
     *
     * @param fields: Fields list to append data to
     * @param clazz:  The clazz to get fields from
     * @return fields: The fields list with clazz fields append to it
     */
    static List<Field> appendClassFields(List<Field> fields, Class<?> clazz) {
        if (clazz.getSuperclass() != null) {
            appendClassFields(fields, clazz.getSuperclass());
        }
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    /**
     * Check the list for at least one non empty string list
     *
     * @param strings: The string list to check for null or
     *                 empty strings
     * @return boolean: True for at least one non-empty string,
     * otherwise false
     */
    static boolean nonEmptyStrings(List<String> strings) {
        if (strings != null && strings.size() > 0) {
            return strings.stream()
                    .filter(Objects::nonNull)
                    .anyMatch(str -> !str.isEmpty());
        }
        return false;
    }

    /**
     * Check the list for at least one non empty string list
     *
     * @param strings: The string list to check for null or
     *                 empty strings
     * @return boolean: True for at least one non-empty string,
     * otherwise false
     */
    static boolean nonEmptyStrings(String[] strings) {
        return nonEmptyStrings(Arrays.asList(strings));
    }


    /**
     * Remove null and empty strings from the list
     *
     * @param strings: The list of the strings to process
     * @return stringList: The list of strings after removing
     * null and empty values
     */
    static List<String> removeEmptyStrings(List<String> strings) {
        return strings.stream()
                .filter(Objects::nonNull)
                .filter(str -> !str.isEmpty()).collect(Collectors.toList());
    }

    /**
     * Check if field can be assigned by the Java Map
     */
    static Boolean isMap(Field field) {
        return Map.class.isAssignableFrom(field.getType());
    }

    /**
     * Check if the field is a CJMap
     * CJMap is a field that is assignable from `Java.util.Map`
     * and is annotated by `CJMap` annotation
     *
     * @param field: The model field
     * @return boolean: True if the field is CJMap
     */
    static Boolean isCJMap(Field field) {
        return utils.isMap(field) &&
                field.getAnnotation(CJMap.class) != null;
    }

    /**
     * The function assumes that the Field is an instance
     * of the `java.util.Map`. It Checks if the map is a
     * static map or not.
     * <p>
     * A map is a static when it uses the `CJMap`,
     * the `CJMap.keys()` are set, and finally
     * `CJMap.includeNull()` is true
     *
     * @param field: A field that is instance of Map
     * @return boolean: True if the map is static
     */
    static Boolean isCJStaticMap(Field field) {
        return isCJMap(field) &&
                (utils.nonEmptyStrings(
                        field.getAnnotation(CJMap.class).keys()) &&
                        field.getAnnotation(CJMap.class).includeNull());
    }

    /*
     * Throwing consumer wrapper for lambda
     */
    static <T> Consumer<T> cjThrowingConsumerWrapper(CJThrowingConsumer<T, Exception> throwingConsumer) {
        return t -> {
            try {
                throwingConsumer.accept(t);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CJException(ex);
            }
        };
    }
}
