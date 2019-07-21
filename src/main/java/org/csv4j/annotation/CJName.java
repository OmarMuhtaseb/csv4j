package org.csv4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field annotation to set the column name for the annotated field.
 *
 * The field name will be used as a the default value for the column name
 * when CJName is not set
 *
 * @author Omar Muhtase
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CJName {

    /**
     * The column name
     * */
    String value();
}
