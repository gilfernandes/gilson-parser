package com.fernandes.json.parser.provider;

import java.io.*;

public class ResourceProvider {

    public static PushbackReader createNumber1Reader() throws IOException {
        return createPushbackReader("simple/number1.txt");
    }

    public static PushbackReader createDoubleReader() throws IOException {
        StringReader reader = new StringReader("12.34");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createNegativeReader() throws IOException {
        StringReader reader = new StringReader("-1234");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createPositiveReader() throws IOException {
        StringReader reader = new StringReader("+1234");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createPositiveLongReader() throws IOException {
        StringReader reader = new StringReader(Long.toString(Long.MAX_VALUE));
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createNegativeLongReader() throws IOException {
        StringReader reader = new StringReader(Long.toString(Long.MIN_VALUE));
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createStringReader() throws IOException {
        StringReader reader = new StringReader("\"aassadd\"");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createStringReaderWithLineBreaks() throws IOException {
        StringReader reader = new StringReader("\"hello, \\r\\n world\"");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createUnicodeString() throws IOException {
        return new PushbackReader(loadFromCp("json/unicodeStrings.json"), 1);
    }


    public static PushbackReader createKeyReader() throws IOException {
        StringReader reader = new StringReader("    \"key\"    :");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createBracket() {
        StringReader reader = new StringReader("{");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createTrue() {
        StringReader reader = new StringReader("true");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createFalse() {
        StringReader reader = new StringReader("false");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createNull() {
        StringReader reader = new StringReader("null");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createIncompleteNull() {
        StringReader reader = new StringReader("nul");
        return new PushbackReader(reader, 1);
    }

    public static PushbackReader createFalseIncomplete() {
        StringReader reader = new StringReader("fal");
        return new PushbackReader(reader, 1);
    }

    private static PushbackReader createPushbackReader(String file) {
        return new PushbackReader(
                loadFromCp(file));
    }

    public static Reader createSimpleObject() {
        return loadFromCp("json/simpleObject.json");
    }

    public static Reader createSimpleObjectWithBoolean() {
        return loadFromCp("json/simpleObject.json");
    }

    public static Reader createSimpleArray() {
        return loadFromCp("json/simpleArray.json");
    }

    public static Reader createNestedObject() {
        return loadFromCp("json/nestedObject.json");
    }

    public static Reader createNestedObjectWithBoolean() {
        return loadFromCp("json/nestedObjectBoolean.json");
    }

    public static Reader createNestedList() {
        return loadFromCp("json/nestedList.json");
    }

    public static Reader createCustomerOwnerKey() {
        return loadFromCp("json/customerOwnerKey.json");
    }

    public static Reader createComplexCustomer() {
        return loadFromCp("json/complexCustomer1.json");
    }

    public static Reader createArrayWithObject() {
        return loadFromCp("json/arrayWithObject.json");
    }

    public static Reader createObjectWithArray() {
        return loadFromCp("json/nestedObjectArray.json");
    }

    private static Reader loadFromCp(String fileStr) {
        return new InputStreamReader(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileStr));
    }
}
