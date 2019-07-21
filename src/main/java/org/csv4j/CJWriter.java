package org.csv4j;

import org.csv4j.exception.CJException;
import org.csv4j.exception.CJExceptionMessages;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * A CSV generator/writer class
 *
 * A simple CSV generator class with the capability of
 * writing the generated data to `java.io.writer`
 *
 * @author Omar Muhtaseb
 */
public class CJWriter<T> {

    private Class clazz;
    private Collection<T> beans;
    private Writer writer;
    private String delimiter = ",";
    private String lineSeparator = "\n";
    private String nullValue = "null";
    private String multiValuesSeparator = ";";
    private Boolean includeHeader = true;

    /**
     * Args Constructor
     *
     * @param clazz: The class model
     */
    public CJWriter(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * The columns' delimiter -> default ","
     */
    public CJWriter<T> delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     * Set Java writer
     */
    public CJWriter<T> writer(Writer writer) {
        this.writer = writer;
        return this;
    }

    /**
     * The lines' separator -> default "\n"
     */
    public CJWriter<T> lineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return this;
    }

    /**
     * Supersede null values with -> default "null"
     */
    public CJWriter<T> nullValue(String nullValue) {
        this.nullValue = nullValue;
        return this;
    }

    /**
     * The separator of the items in collections, arrays, and maps -> default ";"
     */
    public CJWriter<T> multiValuesSeparator(String multiValuesSeparator) {
        this.multiValuesSeparator = multiValuesSeparator;
        return this;
    }

    /**
     * Include the CSV header
     */
    public CJWriter<T> includeHeader(Boolean includeHeader) {
        this.includeHeader = includeHeader;
        return this;
    }

    /**
     * A collection of the data
     */
    public CJWriter<T> beans(Collection<T> beans) {
        this.beans = beans;
        return this;
    }

    /**
     * Get the CJWriterIterator
     *
     * @param beans: A collection of data
     */
    public CJWriteIterator<T> cjWriteIterator(Collection<T> beans) {

        if (beans == null) {
            throw new CJException(CJExceptionMessages.DATA_NOT_SET);
        }

        CJStructure<T> cjStructure = new CJStructure<>(beans, clazz);
        return new CJWriteIterator<T>(
                cjStructure, beans, delimiter, lineSeparator, nullValue, multiValuesSeparator);
    }

    /**
     * Get the CJWriterIterator
     *
     * @param writer: A java writer
     * @param beans:  A collection of data
     */
    public CJWriteIterator<T> cjWriteIterator(Writer writer, Collection<T> beans) {

        if (beans == null) {
            throw new CJException(CJExceptionMessages.DATA_NOT_SET);
        }

        CJStructure<T> cjStructure = new CJStructure<>(beans, clazz);
        return new CJWriteIterator<T>(
                cjStructure, beans, delimiter, lineSeparator, nullValue, multiValuesSeparator, writer);
    }

    /**
     * Generate CSV given the defined beans
     *
     * @return string: The generated csv as string
     */
    public String csv() {
        return csv(beans);
    }

    /**
     * Generate CSV for this collection
     *
     * @param beans: The collection of data
     * @return string: The generated csv as string
     */
    public String csv(Collection<T> beans) {
        return csv(beans, includeHeader);
    }

    /**
     * Generate CSV for the beans
     *
     * @param beans:         The collection of data
     * @param includeHeader: Whether to include the header or not
     * @return string: The generated csv as string
     */
    public String csv(Collection<T> beans, Boolean includeHeader) {

        if (beans == null) {
            throw new CJException(CJExceptionMessages.DATA_NOT_SET);
        }

        CJStructure<T> cjStructure = new CJStructure<>(beans, clazz);
        CJWriteIterator cjWriteIterator = new CJWriteIterator<T>(
                cjStructure, beans, delimiter, lineSeparator, nullValue, multiValuesSeparator);

        String csv = "";
        if (includeHeader) {
            csv = cjWriteIterator.header();
        }

        csv += cjWriteIterator.remaining();
        return csv;
    }

    /**
     * Generate CSV given the defined beans and writer
     *
     */
    public void writeCSV() throws IOException {
        writeCSV(beans);
    }

    /**
     * Generate CSV for this collection
     *
     * @param beans: The collection of data
     */
    public void writeCSV(Collection<T> beans) throws IOException {
        writeCSV(writer, beans, includeHeader);
    }

    /**
     * Generate CSV for this collection
     *
     * @param writer: The writer
     */
    public void writeCSV(Writer writer) throws IOException {
        writeCSV(writer, beans, includeHeader);
    }

    /**
     * Generate CSV for this collection
     *
     * @param writer: The writer
     * @param beans:  The collection of data
     */
    public void writeCSV(Writer writer, Collection<T> beans) throws IOException {
        writeCSV(writer, beans, includeHeader);
    }

    /**
     * Generate CSV for the beans
     *
     * @param beans:         The collection of data
     * @param includeHeader: Whether to include the header or not
     */
    public void writeCSV(Writer writer, Collection<T> beans, Boolean includeHeader) throws IOException {

        if (writer == null) {
            throw new CJException(CJExceptionMessages.WRITER_NOT_SET);
        }

        if (beans == null) {
            throw new CJException(CJExceptionMessages.DATA_NOT_SET);
        }

        CJStructure<T> cjStructure = new CJStructure<>(beans, clazz);
        CJWriteIterator cjWriteIterator = new CJWriteIterator<T>(
                cjStructure, beans, delimiter, lineSeparator, nullValue, multiValuesSeparator, writer);

        if (includeHeader) {
            cjWriteIterator.writeHeader();
        }

        cjWriteIterator.writeRemaining();
    }

}
