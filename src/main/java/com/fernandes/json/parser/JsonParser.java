package com.fernandes.json.parser;

import com.fernandes.json.parser.matcher.*;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.function.Consumer;

public class JsonParser {

    private final SingleMatcher openCurlySingleMatcher;

    private final SingleMatcher closeCurlySingleMatcher;

    private final SingleMatcher squareStartSingleMatcher;

    private final SingleMatcher squareStopSingleMatcher;

    private final SingleMatcher commaSingleMatcher;

    private final SingleMatcher colonSingleMatcher;

    private final StringMatcher stringMatcher;

    private final Consumer<Token> callback;

    private final NumberMatcher numberMatcher;

    private final BooleanMatcher booleanMatcher;

    private final NullMatcher nullMatcher;

    private final PushbackReader pushbackReader;

    private final Space space;

    private final KeyMatcher keyMatcher;

    public JsonParser(Reader reader, Consumer<Token> callback) {
        this.pushbackReader = new PushbackReader(reader, 5); // Using 5 because of "false"
        this.callback = callback;
        numberMatcher = new NumberMatcher(callback);
        stringMatcher = new StringMatcher(callback);
        booleanMatcher = new BooleanMatcher(callback);
        nullMatcher = new NullMatcher(callback);
        space = new Space(pushbackReader);
        openCurlySingleMatcher = new SingleMatcher('{', TokenType.OBJECT_START, callback);
        closeCurlySingleMatcher = new SingleMatcher('}', TokenType.OBJECT_END, callback);
        squareStartSingleMatcher = new SingleMatcher('[', TokenType.ARRAY_START, callback);
        squareStopSingleMatcher = new SingleMatcher(']', TokenType.ARRAY_END, callback);
        commaSingleMatcher = new SingleMatcher(',', TokenType.SEPARATOR, callback);
        colonSingleMatcher = new SingleMatcher(':', TokenType.COLON, callback);
        keyMatcher = new KeyMatcher(callback);
    }

    public boolean parse() throws IOException {
        return readMain();
    }

    private boolean readMain() throws IOException {
        if (finished()) {
            return true;
        }
        return readObject() || readArray();
    }

    private boolean readArray() throws IOException {
        space.space();
        if (!squareStartSingleMatcher.match(pushbackReader)) {
            return false;
        }
        if (!readArrayValues()) {
            return false;
        }
        space.space();
        return squareStopSingleMatcher.match(pushbackReader);
    }

    private boolean readArrayValues() throws IOException {
        space.space();
        if (!readValue()) {
            return false;
        }
        if(commaSingleMatcher.match(pushbackReader)) {
            space.space();
            readArrayValues();
        }
        return true;
    }

    private boolean readObject() throws IOException {
        space.space();
        if (!openCurlySingleMatcher.match(pushbackReader)) {
            return false;
        }
        return readObjectElement();
    }

    private boolean readObjectElement() throws IOException {
        if (!key()) {
            return false;
        }
        if (!readValue()) {
            return false;
        }
        return closeCurlySingleMatcher.match(pushbackReader) || (commaSingleMatcher.match(pushbackReader) && readObjectElement());
    }

    private boolean readValue() throws IOException {
        space.space();
        // TODO: add null matcher
        boolean ok = stringMatcher.match(pushbackReader) || numberMatcher.match(pushbackReader)
                || booleanMatcher.match(pushbackReader) || nullMatcher.match(pushbackReader)
                || readMain();
        if (ok) {
            space.space();
        }
        return ok;
    }

    private boolean key() throws IOException { // TODO: create matcher for this
        return keyMatcher.match(pushbackReader);
    }

    private boolean finished() throws IOException {
        int read = pushbackReader.read();
        boolean finished = read == -1;
        if (!finished) {
            pushbackReader.unread(read);
        }
        return finished;
    }


}
