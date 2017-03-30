package com.fernandes.json.parser;

import com.fernandes.json.parser.consumer.TreeBuilder;
import com.fernandes.json.parser.provider.ResourceProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class JsonParserTest {

    private JsonParser parser;

    @Test
    void whenParseSimple_ShouldFind2Objects() throws IOException {

        List<String> keyList = new ArrayList<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        parser = new JsonParser(ResourceProvider.createSimpleObject(), token -> {
            switch (token.getTokenType()) {
                case KEY:
                    keyList.add(token.getObject().toString());
                    break;
                case STRING:
                    atomicInteger.incrementAndGet();
            }
        });
        parser.parse();
        assertThat(keyList.size()).isEqualTo(2);
        assertThat(atomicInteger.get()).isEqualTo(2);
    }

    @Test
    void whenParseSimpleArray_ShouldFind3Strings() throws IOException {

        AtomicInteger counter = new AtomicInteger(0);
        List<Serializable> list = new ArrayList<>();
        parser = new JsonParser(ResourceProvider.createSimpleArray(), token -> {
            switch (token.getTokenType()) {
                case STRING:
                case DOUBLE:
                    counter.incrementAndGet();
                    list.add(token.getObject());
            }
        });
        parser.parse();
        assertThat(counter.get()).isEqualTo(4);
        System.out.println(list);
    }

    @Test
    void whenParseNested_ShouldFind3Keys() throws IOException {
        List<String> keys = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        parser = new JsonParser(ResourceProvider.createNestedObject(), token -> {
            switch (token.getTokenType()) {
                case KEY:
                    keys.add(token.getObject().toString());
                    break;
                case STRING:
                    strings.add(token.getObject().toString());
                    break;
            }
        });
        parser.parse();
        assertThat(keys.size()).isEqualTo(3);
        assertThat(keys).isEqualTo(Arrays.asList("customer", "firstName", "lastName"));
        assertThat(strings.size()).isEqualTo(2);
    }

    @Test
    void whenParseNested_ShouldFind5Keys() throws IOException {

        final NestedConsumer callback = new NestedConsumer();
        parser = new JsonParser(ResourceProvider.createNestedObjectWithBoolean(), callback);
        assertThat(parser.parse()).isTrue();
        Map<String, Object> keys = callback.getRoot();
        assertThat(keys.size()).isEqualTo(1);
        assertThat(keys.containsKey("customer")).isTrue();
    }

    @Test
    void whenParseNestedList_ShouldFindArray() throws IOException {

        class ListCaptureConsumer implements Consumer<Token> {

            private List<String> contentList;

            /**
             * Performs this operation on the given argument.
             *
             * @param token the input argument
             */
            @Override
            public void accept(Token token) {
                switch (token.getTokenType()) {
                    case ARRAY_START:
                        contentList = new ArrayList<>();
                        break;
                    case STRING:
                        if(contentList != null) {
                            contentList.add((String) token.getObject());
                        }
                }
            }

            public List<String> getContentList() {
                return contentList;
            }
        }

        final ListCaptureConsumer callback = new ListCaptureConsumer();
        parser = new JsonParser(ResourceProvider.createNestedList(), callback);
        assertThat(parser.parse()).isTrue();
        assertThat(callback.getContentList().size()).isEqualTo(4);

    }

    @Test
    void whenParseArrayWithObject_ShouldExtractObject() throws IOException {
        TreeBuilder treeBuilder = new TreeBuilder();
        parser = new JsonParser(ResourceProvider.createArrayWithObject(), treeBuilder);
        assertThat(parser.parse()).isTrue();
        List<Object> l = treeBuilder.getRootList();
        assertThat(l).isNotNull();
        assertThat(l.size()).isEqualTo(6);
    }

    @Test
    void whenParseObjectWithArray_ShouldExtractObject() throws IOException {
        TreeBuilder treeBuilder = new TreeBuilder();
        parser = new JsonParser(ResourceProvider.createObjectWithArray(), treeBuilder);
        assertThat(parser.parse()).isTrue();
        Map<String, Object> map = treeBuilder.getRootMap();
        assertThat(map).isNotNull();
        assertThat(map.size()).isEqualTo(1);
        Map<String, Object> customerMap = (Map<String, Object>) map.get("customer");
        assertThat(customerMap).isNotNull();
    }

    @Test
    void whenParseCustomerOwnerKey_ShouldExtractObject() throws IOException {
        TreeBuilder treeBuilder = new TreeBuilder();
        parser = new JsonParser(ResourceProvider.createCustomerOwnerKey(), treeBuilder);
        assertThat(parser.parse()).isTrue();
        Map<String, Object> map = treeBuilder.getRootMap();
        assertThat(map.get("_id")).isEqualTo("58d26f6ce4b000fd45f8b144");
        final Object customer = map.get("customer");
        assertThat(customer).isNotNull();
        assertThat(customer instanceof Map).isTrue();
        Map<String, Object> customerMap = (Map<String, Object>) customer;
        Object ownerObject = customerMap.get("owner");
        assertThat(ownerObject).isNotNull();
        assertThat(ownerObject instanceof Map).isTrue();
        Map<String, Object> ownerMap = (Map<String, Object>) ownerObject;
        final Object keyObject = ownerMap.get("key");
        assertThat(keyObject).isNotNull();
        assertThat(keyObject instanceof Map).isTrue();
        Map<String, Object> keyMap = (Map<String, Object>) keyObject;
        assertThat(keyMap.get("systemOwner")).isEqualTo("CP");
        assertThat(keyMap.get("system")).isEqualTo("TU GROUP");
        assertThat(keyMap.get("id")).isEqualTo("3109880");
    }

    @Test
    void whenParseComplexCustomer_ShouldExtractObject() throws IOException {
        TreeBuilder treeBuilder = new TreeBuilder();
        parser = new JsonParser(ResourceProvider.createComplexCustomer(), treeBuilder);
        assertThat(parser.parse()).isTrue();
        Map<String, Object> rootMap = treeBuilder.getRootMap();
        assertThat(rootMap.get("_id")).isEqualTo("58d26f6ce4b000fd45f8b144");
        Object customerObj = rootMap.get("customer");
        assertThat(customerObj).isNotNull();
        if(Map.class.<String, Object>isAssignableFrom(customerObj.getClass())) {
            Map<String, Object> customer = Map.class.<String, Object>cast(customerObj);
            assertThat(customer.size()).isEqualTo(12);
            Object ownerObj = customer.get("owner");
            assertThat(ownerObj).isNotNull();
            if(Map.class.<String,Object>isAssignableFrom(ownerObj.getClass())) {
                Map<String, Object> ownerMap = Map.class.<String, Object>cast(ownerObj);
                assertThat(ownerMap).isNotNull();
                assertThat(ownerMap.size()).isEqualTo(1);
                assertThat(ownerMap.containsKey("key"));
            }
            assertThat(customer.get("firstName")).isEqualTo("Klara");
            assertThat(customer.get("lastName")).isEqualTo("Besenstiel");
            assertThat(customer.get("gender")).isEqualTo("Female");
        }
    }

    @Test
    void whenParseComplexEvent_ShouldExtractObject() throws IOException {
        TreeBuilder treeBuilder = new TreeBuilder();
        parser = new JsonParser(ResourceProvider.createComplexEvent(), treeBuilder);
        assertThat(parser.parse()).isTrue();
        Map<String, Object> map = treeBuilder.getRootMap();
        assertThat(map).isNotNull();
        Map<String, Object> responseMap = (Map<String, Object>) map.get("response");
        assertThat(responseMap).isNotNull();
        System.out.println(responseMap);
        Object totalCountObject = responseMap.get("totalCount");
        assertThat(totalCountObject instanceof Integer).isTrue();
        assertThat(totalCountObject).isEqualTo(21);
        Object statusObject = responseMap.get("status");
        assertThat(statusObject).isNotNull();
        assertThat(statusObject).isEqualTo(0);
    }
}

class NestedConsumer implements Consumer<Token> {

    private Map<String, Object> keys = new HashMap<>();

    private Map<String, Object> root = keys;

    private String key;

    @Override
    public void accept(Token token) {
        switch (token.getTokenType()) {
            case KEY:
                key = token.getObject().toString();
                break;
            case STRING:
            case BOOLEAN:
                keys.put(key, token.getObject());
                break;
            case OBJECT_START:
                if(key != null) {
                    Map<String, Object> newKeys = new HashMap<>();
                    keys.put(key, newKeys);
                    keys = newKeys;
                }
        }
    }

    public Map<String, Object> getRoot() {
        return root;
    }
}