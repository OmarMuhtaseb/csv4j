package org.csv4j;

import org.csv4j.annotation.CJIgnore;
import org.csv4j.annotation.CJMap;
import org.csv4j.annotation.CJName;
import org.csv4j.exception.CJException;
import org.csv4j.exception.CJExceptionMessages;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CJStructure is responsible for building
 * the structure of the csv
 *
 * @author Omar Muhtaseb
 */
public class CJStructure<T> {

    private Collection<T> beans;
    private Class clazz;
    private List<CJColumn> cjColumns;

    /**
     * Args Constructor
     *
     * @param beans: The data collection
     * @param clazz: The model class of the data
     */
    CJStructure(Collection<T> beans, Class clazz) {
        this.beans = beans;
        this.clazz = clazz;
        build();
    }

    /**
     * Get the number of the csv columns
     */
    public Integer getColumnsLength() {
        return cjColumns.size();
    }

    /**
     * Get the structure of the csv
     */
    public List<CJColumn> getStructure() {
        return cjColumns;
    }

    /**
     * Check if the structure is empty, has no columns
     */
    public Boolean isEmpty() {
        return cjColumns.isEmpty();
    }

    /**
     * Build the structure of the CSV
     */
    private void build() {

        List<Field> fields = utils.appendClassFields(new ArrayList<>(), clazz);

        fields = filterIgnoredFields(fields);

        cjColumns = genCJColumns(fields);

        genCJMapsKeys();

        if (beansScanNeeded()) {
            cjNonStaticMapsKeys();
        }
    }

    /**
     * Ignore fields with CJIgnore annotation
     *
     * @param fields : The list of model's fields
     * @return fields: The list of fields to use in csv writing
     */
    private List<Field> filterIgnoredFields(List<Field> fields) {
        return fields.stream()
                .filter(field -> !field.isAnnotationPresent(CJIgnore.class))
                .collect(Collectors.toList());
    }

    /**
     * Generate a list of CJColumn of the fields
     *
     * @param fields: The List of model fields
     * @return list: A list of CJColumn
     */
    private List<CJColumn> genCJColumns(List<Field> fields) {
        return fields.stream()
                .map(field ->
                        new CJColumn(
                                field.getName(),
                                genColumnName(field),
                                utils.isCJMap(field),
                                utils.isCJStaticMap(field)
                        )
                )
                .collect(Collectors.toList());
    }

    /**
     * Get the column name of the csv
     *
     * @param field: The model field
     * @return string: The header name of the column
     */
    private String genColumnName(Field field) {
        return field.getDeclaredAnnotation(CJName.class) != null ?
                field.getDeclaredAnnotation(CJName.class).value() :
                field.getName();
    }

    /**
     * Get the keys for each CJ map
     * For each CJMap get the list of key
     * defined in the annotation
     */
    private void genCJMapsKeys() {
        cjColumns.stream()
                .filter(CJColumn::isCJMap)
                .forEach(utils.cjThrowingConsumerWrapper(cjColumn ->
                        cjColumn.setMapKeys(
                                new LinkedHashSet<>(
                                        Arrays.asList(
                                                clazz.getDeclaredField(cjColumn.getFieldName())
                                                        .getDeclaredAnnotation(CJMap.class).keys())))
                ));
    }

    /**
     * Check if beans scan is needed.
     * Beans scan is needed whenever there is a non-static map.
     *
     * @return boolean : True when beanScan is needed and False when not
     */
    private Boolean beansScanNeeded() {
        return cjColumns.stream()
                .filter(CJColumn::isCJMap)
                .anyMatch(cjColumn -> !cjColumn.isCJStaticMap());
    }

    /**
     * This function builds the structure of the dynamic fields
     * It does that by iterating over the beans and
     * get all the keys for each CJMap
     */
    private void cjNonStaticMapsKeys() {
        Map<String, Set<String>> mapsKeys = getBeansMapsKeys();

        // Set cjColumn mapKeys
        cjColumns.stream()
                .filter(CJColumn::isCJMap)
                .filter(cjColumn -> !cjColumn.isCJStaticMap())
                .forEach(utils.cjThrowingConsumerWrapper(cjColumn -> {
                    Field field = clazz.getDeclaredField(cjColumn.getFieldName());
                    CJMap cjMap = field.getAnnotation(CJMap.class);

                    // As this is a nonStatic map then there are two possibilities
                    // First the annotation keys have been set and includeNull = false
                    // Then we need to keep the keys that intersect with the beans' keys
                    // The second possibility is that it is a totally dynamic map then
                    // we only cares about the bean's keys
                    Collection keys = mapsKeys.get(cjColumn.getFieldName());
                    keys = (keys == null) ? new HashSet() : keys;
                    if (utils.nonEmptyStrings(cjMap.keys())) {
                        cjColumn.getMapKeys().retainAll(keys);
                    } else {
                        cjColumn.getMapKeys().addAll(keys);
                    }
                }));
    }

    /**
     * For all CJMaps get all the possible keys
     * from the beans
     */
    private Map<String, Set<String>> getBeansMapsKeys() {
        Map<String, Set<String>> mapsKeys = new HashMap<>();
        beans.forEach(bean -> cjColumns.stream()
                .filter(CJColumn::isCJMap)
                .filter(cjColumn -> !cjColumn.isCJStaticMap())
                .forEach(utils.cjThrowingConsumerWrapper(cjColumn -> {
                    Set<String> keys = mapsKeys.computeIfAbsent(cjColumn.getFieldName(),
                            k -> new LinkedHashSet<>());
                    keys.addAll(getMapKeys(cjColumn, bean));
                })));
        return mapsKeys;
    }

    /*
     * Get the all the keys of the bean's map
     *
     * @return set: A set of map's keys, otherwise EmptySet
     */
    private Set getMapKeys(CJColumn cjColumn, T bean) throws IllegalAccessException, NoSuchFieldException {
        Field field = bean.getClass().getDeclaredField(cjColumn.getFieldName());

        if (!utils.isMap(field)) {
            throw new CJException(CJExceptionMessages.OBJECT_NOT_MAP);
        }

        field.setAccessible(true);
        Object value = field.get(bean);
        field.setAccessible(false);
        return (value == null) ? Collections.emptySet() : ((Map) value).keySet();
    }
}
