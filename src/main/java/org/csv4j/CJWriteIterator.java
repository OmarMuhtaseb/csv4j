package org.csv4j;

import org.csv4j.exception.CJException;
import org.csv4j.exception.CJExceptionMessages;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * CJWriterIterator is responsible for writing
 * the data as string or to writer
 *
 * @author Omar Muhtaseb
 */
public class CJWriteIterator<T> {

    private CJStructure<T> cjStructure;
    private Iterator<T> beans;
    private String delimiter;
    private String lineSeparator;
    private String nullValue;
    private String multiValuesSeparator;
    private Writer writer;
    private Integer totalRows;

    /**
     * Args constructor
     *
     * @param cjStructure:          The structure of the csv
     * @param beans:                The collection of data
     * @param delimiter:            The delimiter between cols
     * @param lineSeparator:        The line separator between rows
     * @param nullValue:            Supersede the null value with this
     * @param multiValuesSeparator: The separator of items in collections and arrays
     */
    public CJWriteIterator(CJStructure<T> cjStructure, Collection<T> beans, String delimiter,
                           String lineSeparator, String nullValue, String multiValuesSeparator) {
        this(cjStructure, beans, delimiter, lineSeparator, nullValue, multiValuesSeparator, null);
    }

    /**
     * Args constructor
     *
     * @param cjStructure:          The structure of the csv
     * @param beans:                The collection of data
     * @param delimiter:            The delimiter between cols
     * @param lineSeparator:        The line separator between rows
     * @param nullValue:            Supersede the null value with this
     * @param multiValuesSeparator: The separator of items in collections and arrays
     * @param writer:               The java writer
     */
    public CJWriteIterator(CJStructure<T> cjStructure, Collection<T> beans, String delimiter,
                           String lineSeparator, String nullValue, String multiValuesSeparator, Writer writer) {
        this.cjStructure = cjStructure;
        this.beans = beans.iterator();
        this.delimiter = delimiter;
        this.lineSeparator = lineSeparator;
        this.nullValue = nullValue;
        this.multiValuesSeparator = multiValuesSeparator;
        this.writer = writer;
        this.totalRows = beans.size();
    }

    /**
     * Return the total number of rows for the csv
     */
    public Integer totalRows() {
        return totalRows;
    }

    /**
     * Check if the csv has next rows
     *
     * @return boolean: True if there are still rows
     */
    public Boolean hasNext() {
        return beans.hasNext();
    }

    /**
     * Write the header of the csv as String
     */
    public void writeHeader() throws IOException {
        writer.write(header());
    }

    /**
     * Generate the header of the csv as String
     *
     * @return header: String of the generated header
     */
    public String header() {
        return cjStructure.getStructure().stream()
                .flatMap(cjColumn -> {
                    if (!cjColumn.isCJMap()) {
                        return Stream.of(cjColumn.getCjName());
                    }
                    return cjColumn.getMapKeys().stream().map(String::valueOf);
                })
                .collect(Collectors.joining(delimiter, "", lineSeparator));
    }

    /**
     * Write the csv for the remaining data in
     * the iterator
     */
    public void writeRemaining() throws IOException {
        writer.write(remaining());
    }

    /**
     * Generate the csv for the remaining data in
     * the iterator
     *
     * @return data: The csv for the remaining data
     */
    public String remaining() {
        List<String> rows = new ArrayList<>(totalRows);
        beans.forEachRemaining(
                bean ->
                        rows.add(
                                cjStructure.getStructure().stream()
                                        .flatMap(cjColumn -> {
                                            try {
                                                return genCJColumnValues(bean, cjColumn);
                                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                                throw new CJException(e);
                                            }
                                        })
                                        .collect(Collectors.joining(delimiter))
                        )
        );
        return rows.stream().collect(Collectors.joining(lineSeparator));
    }

    /**
     * Write the csv for the remaining data in
     * the iterator
     */
    public void writeNext() throws IOException {
        writer.write(next());
    }

    /**
     * Generate the csv for the remaining data in
     * the iterator
     *
     * @return data: The csv for the remaining data
     */
    public String next() {
        if (!beans.hasNext()) {
            throw new CJException(CJExceptionMessages.NO_SUCH_ELT_EXISTS);
        }
        T bean = beans.next();
        String row = cjStructure.getStructure().stream()
                .flatMap(cjColumn -> {
                    try {
                        return genCJColumnValues(bean, cjColumn);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new CJException(e);
                    }
                })
                .collect(Collectors.joining(delimiter));
        return row + lineSeparator;
    }


    /**
     * Generate the data for a CJColumn
     *
     * @param bean:     The object to get the data from
     * @param cjColumn: The csv column
     * @return stream: A stream of the result values
     * @throws NoSuchFieldException:   In case the field doesn't exist in the object
     * @throws IllegalAccessException: In case the field was private
     */
    private Stream<String> genCJColumnValues(T bean, CJColumn cjColumn)
            throws NoSuchFieldException, IllegalAccessException {

        Field field = bean.getClass().getDeclaredField(cjColumn.getFieldName());
        field.setAccessible(true);
        Object value = field.get(bean);
        field.setAccessible(false);

        if (!cjColumn.isCJMap()) {
            return Stream.of(objectToString(value));
        }

        if (value == null) {
            return IntStream.range(0, cjColumn.getMapKeys().size()).mapToObj(obj -> nullValue);
        }
        return cjColumn.getMapKeys()
                .stream()
                .map(key ->
                        String.valueOf(((Map) value).get(key)));
    }

    /**
     * Convert the object to String
     *
     * @param obj: The object to get its representation
     * @return string: The string representation of the object
     */
    private String objectToString(Object obj) {
        if (obj == null) {
            return nullValue;
        }
        if (obj.getClass().isArray()) {
            return ToString.arrayToString((Object[]) obj, multiValuesSeparator, nullValue);
        }

        if (obj instanceof Collection) {
            return ToString.collectionToString((Collection) obj, multiValuesSeparator, nullValue);
        }

        if (obj instanceof Map) {
            return ToString.MapToString((Map) obj, multiValuesSeparator, nullValue);
        }

        return String.valueOf(obj);
    }


}
