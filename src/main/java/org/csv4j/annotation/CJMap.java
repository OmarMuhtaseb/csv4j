package org.csv4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field annotation that is specific to data types that implement
 * `java.util.Map` interface.
 *
 * This annotation instructs the CSVWriter
 * to treat `java.util.Map` as a source for dynamic columns. The map key
 * represents the column header whereas the value represents the row value
 *
 * The order of the keys will be preserved
 *
 * @author Omar Muhtaseb
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CJMap {

    /**
     * Possible keys that the map might contain.
     * The order of the keys will be preserved
     *
     * This can be used to define the order of the possible keys of the map.
     * Moreover, it is recommended to set the value for `keys()` whenever it is
     * possible to enhance the CSVWriter performance.
     *
     * */
    String[] keys() default {};

    /**
     * Include null columns when setting `keys()` with possible keys.
     *
     * When set to `false` the columns with no values will be ignored in CSVWRiter
     * */
    boolean includeNull() default true;
}
