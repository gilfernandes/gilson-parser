package com.fernandes.json.parser.consumer;

import com.fernandes.json.parser.Token;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Builder used to create a structure of maps and lists.
 */
public class TreeBuilder implements Consumer<Token> {

    private ObjectWithParent current;

    private Object root;

    private String key;

    @Override
    public void accept(Token token) {
        final Serializable object = token.getObject();
        switch (token.getTokenType()) {
            case KEY:
                key = object.toString();
                break;
            case STRING:
            case BOOLEAN:
            case DOUBLE:
            case INTEGER:
            case LONG:
            case BIG_INTEGER:
            case NULL:
                addPrimitive(object);
                break;
            case OBJECT_START:
                processObjectStart();
                break;
            case OBJECT_END:
                moveToParent();
                break;
            case ARRAY_START:
                processArrayStart();
                break;
            case ARRAY_END:
                moveToParent();
                break;
        }
    }

    private void moveToParent() {
        current = current.parent;
    }

    private void processArrayStart() {
        addObjectToParent(new ArrayList<>());
    }

    private void processObjectStart() {
        addObjectToParent(new LinkedHashMap<String, Object>());
    }

    @SuppressWarnings({"unchecked"})
    private void addObjectToParent(Object newKeys) {
        if (currentIsList()) {
            ((List) current.object).add(newKeys);
            current = new ObjectWithParent(newKeys, current);
        } else if (currentIsMap()) {
            ((Map<String, Object>) current.object).put(key, newKeys);
            current = new ObjectWithParent(newKeys, current);
        } else { // Should be null
            current = new ObjectWithParent(newKeys, null);
            root = current.object;
        }
    }

    @SuppressWarnings({"unchecked"})
    private void addPrimitive(Serializable object) {
        if (currentIsList()) {
            ((List) current.object).add(object);
        } else if (currentIsMap()) {
            ((Map<String, Object>) current.object).put(key, object);
        } else {
            throw new IllegalStateException("Could not add primitive to any object.");
        }
    }

    private boolean currentIsList() {
        return current != null && current.object instanceof List;
    }

    private boolean currentIsMap() {
        return current != null && current.object instanceof Map;
    }

    @SuppressWarnings({"unchecked"})
    public Map<String, Object> getRootMap() {
        return rootIsMap() ? (Map<String, Object>) root : null;
    }

    public List<Object> getRootList() {
        return !rootIsMap() ? List.class.cast(root) : null;
    }

    private boolean rootIsMap() {
        return Map.class.isAssignableFrom(root.getClass());
    }

    private class ObjectWithParent {

        private Object object;

        private ObjectWithParent parent;

        ObjectWithParent(Object object, ObjectWithParent parent) {
            this.object = object;
            this.parent = parent;
        }
    }
}


